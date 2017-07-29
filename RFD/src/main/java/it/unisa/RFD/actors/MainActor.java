package it.unisa.RFD.actors;

import java.util.logging.Logger;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import it.unisa.RFD.actors.ConcurrentDMActor.CreateConcurrentDM;
import joinery.DataFrame;
/**
 * Attore principale che gestisce la parallelizzazione 
 * @author luigidurso
 *
 */
public class MainActor extends AbstractActor
{
	
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private int countPart=0;
	private DataFrame<Object> df;
	private DataFrame<Object> completeDM;
	private int threadNr=2;
	private long timerInizio,timerFine;
	
	
	/**
	 * Costruttore Main actor che riceve un dataframe completo ed il numero di thread con cui si desidera eseguire il programma.
	 * In fase di costruzione assegnamo le variabili di istanza e creiamo la nostra DM vuota.
	 * 
	 * @param dataFrame dataframe completo letto da file
	 * @param threadNumber numero thread con cui si fa girare il programma...default:2
	 */
	public MainActor(DataFrame<Object> dataFrame,int threadNumber)
	{
		if(Runtime.getRuntime().availableProcessors()>=threadNumber)
		{
			this.threadNr=threadNumber;
		}
		else
		{
			log.info("Numero thread inserito troppo grande...default:2");
		}
		
		this.df=dataFrame;
		
		this.completeDM=df.dropna();
		this.completeDM=completeDM.slice(0,0);
		this.completeDM=completeDM.add("Id");
		
	}
	/**
	 * Metodo statico per istanziare MainActor
	 * @param dataFrame
	 * @param threadNumber
	 * @return reference a MainActor
	 */
	static public Props props(DataFrame<Object> dataFrame,int threadNumber)
	{
		return Props.create(MainActor.class,()->new MainActor(dataFrame,threadNumber));
	}
	
	@Override
	public void preStart() throws Exception 
	{
		log.info("Sono vivo");
		super.postStop();
	}

	@Override
	public void postStop() throws Exception 
	{
		log.info("Sono morto");
		super.postStop();
	}
	/**
	 * Metodo per l'attesa del completamento della Distance Matrix.
	 */
	private void isDMComplete()
	{
		this.countPart++;
		if(countPart==this.threadNr)
		{
			this.timerFine=System.currentTimeMillis();
			this.completeDM.show();
			System.out.println("Concluso in tempo: "+(this.timerFine-this.timerInizio));
		}
	}
	/**
	 * Messaggio per l'inizio della parallelizzazione della DM.
	 * @author luigidurso
	 *
	 */
	static public class ConcurrenceDistanceMatrix
	{
		public ConcurrenceDistanceMatrix()
		{
		}
	}
	/**
	 * Messaggio per la ricezione di parti della DM 
	 * @author luigidurso
	 *
	 */
	static public class ReceivePartDM
	{
		private DataFrame<Object> partialDM;
		
		public ReceivePartDM(DataFrame<Object> partialDM)
		{
			this.partialDM=partialDM;
		}
	}
	/**
	 * Messaggio di test
	 * @author luigidurso
	 *
	 */
	static public class TestMessage
	{
		
		public TestMessage()
		{
		}
	}
	/**
	 * Builder per la ricezione dei messaggi
	 */
	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(ConcurrenceDistanceMatrix.class, c->  //Creo il numero di thread necessari per la creazione di DM
				{
					int dimension=df.length()/this.threadNr;
					int lastStep= df.length()%this.threadNr;
					int inizioCorrente=0;
					
					for(int i=0; i<this.threadNr ;i++)
					{
						if(i<this.threadNr-1)
						{
							ActorRef actor=this.getContext().actorOf(ConcurrentDMActor.props());
							actor.tell(new CreateConcurrentDM(inizioCorrente,dimension,this.df), this.getSelf());

							
							inizioCorrente+=dimension;
						}
						else
						{
							ActorRef actor=this.getContext().actorOf(ConcurrentDMActor.props());
							
							actor.tell(new CreateConcurrentDM(inizioCorrente,dimension+lastStep,this.df), this.getSelf());
							
						}
					}
					
					this.timerInizio=System.currentTimeMillis();
					
				})
				.match(ReceivePartDM.class, r->  //Messggio con cui riceve parte della DM elaborata da ogni thread 
				{
					for(int i=0;i<r.partialDM.length();i++)
					{
						this.completeDM.append(r.partialDM.row(i));
					}
					this.isDMComplete();
					
				})
				.match(TestMessage.class, t->  //messaggio di test
				{
					log.info("ciao");
					
				}).build();
	}

}
