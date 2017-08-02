package it.unisa.RFD.actors;

import java.util.ArrayList;
import java.util.logging.Logger;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.remote.routing.RemoteRouterConfig;
import akka.routing.RoundRobinPool;
import it.unisa.RFD.OrderedDM;
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
	private int countPart=0,countOrderedDM=0;
	private DataFrame<Object> df;
	private DataFrame<Object> completeDM;
	private int threadNr=2;
	private long timerInizio,timerFine;
	private ArrayList<OrderedDM> listaDMOrdinati;
	Cluster cluster = Cluster.get(getContext().getSystem());
	private ArrayList<Address> addresses;
	
	
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
		
		this.listaDMOrdinati=new ArrayList<>();
		this.addresses=new ArrayList<>();
		
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
		cluster.subscribe(getSelf(),  MemberUp.class);
		super.postStop();
	}

	@Override
	public void postStop() throws Exception 
	{
		cluster.unsubscribe(getSelf());
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
			System.out.println("Concluso in tempo: "+(this.timerFine-this.timerInizio));
			
			this.completeDM.show();
			
			for(int i=0; i<this.completeDM.size()-1; i++)
			{
				ActorRef act=this.getContext().actorOf(ConcurrentOrderedDMActor.props());
				act.tell(new ConcurrentOrderedDMActor.CreateOrderedDM(completeDM,i), this.getSelf());
			}
		}
	}
	/**
	 * Metodo per l'attesa del completamento di tutte le DM ordinate
	 */
	private void isOrderedDMComplete()
	{
		countOrderedDM++;
		if(countOrderedDM==this.completeDM.size()-1)
		{
			this.timerFine=System.currentTimeMillis();
			System.out.println("Concluso in tempo Cluster: "+(this.timerFine-this.timerInizio));
			this.listaDMOrdinati.get(1).getOrderedDM().show();
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
	 * Messaggio ricezione DM ordinata
	 * @author luigidurso
	 *
	 */
	static public class ReceiveOrderedDM
	{
		private OrderedDM orderedDM;
		
		public ReceiveOrderedDM(OrderedDM orderedDM)
		{
			this.orderedDM=orderedDM;
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
					
					ActorRef routerRemote = getContext().actorOf(new RemoteRouterConfig(new RoundRobinPool(this.threadNr), addresses).props(ConcurrentDMActor.props()));
					
					for(int i=0; i<this.threadNr ;i++)
					{
						if(i<this.threadNr-1)
						{
							
							routerRemote.tell(new CreateConcurrentDM(inizioCorrente,dimension,this.df), this.getSelf());
//							ActorRef actor=this.getContext().actorOf(ConcurrentDMActor.props());
//							actor.tell(new CreateConcurrentDM(inizioCorrente,dimension,this.df), this.getSelf());

							
							inizioCorrente+=dimension;
						}
						else
						{
//							ActorRef actor=this.getContext().actorOf(ConcurrentDMActor.props());
//							
//							actor.tell(new CreateConcurrentDM(inizioCorrente,dimension+lastStep,this.df), this.getSelf());
							routerRemote.tell(new CreateConcurrentDM(inizioCorrente,dimension+lastStep,this.df), this.getSelf());
//							
						}
					}
//					
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
					ActorRef routerRemote = getContext().actorOf(new RemoteRouterConfig(new RoundRobinPool(this.threadNr), addresses).props(ConcurrentDMActor.props()));
					
					for(int i=0; i<this.threadNr ;i++)
					{
						routerRemote.tell(new ConcurrentDMActor.TestMessage("ciao stocazzo: "+i), this.getSelf());
					}
				})
				.match(MemberUp.class, mUp -> 
				{
			        log.info("Member is Up: {}", mUp.member());
			        this.addresses.add(mUp.member().address());
			        System.out.println(addresses.toString());
			    })
				.match(ReceiveOrderedDM.class, rc-> //messaggio che riceve le DM ordinate
				{
					this.listaDMOrdinati.add(rc.orderedDM);
					this.isOrderedDMComplete();
					
				}).build();
	}

}
