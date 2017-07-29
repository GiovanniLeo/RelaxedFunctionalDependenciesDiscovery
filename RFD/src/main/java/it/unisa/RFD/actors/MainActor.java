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
 * 
 * @author luigidurso
 *
 */
public class MainActor extends AbstractActor
{
	
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private int countPart=0;
	private DataFrame<Object> df;
	private DataFrame<Object> completeDM;
	static public int threadNr=4;
	private long timerInizio,timerFine;
	
	
	
	public MainActor(DataFrame<Object> dataFrame,int threadNumber)
	{
		if(Runtime.getRuntime().availableProcessors()>=threadNumber)
		{
			this.threadNr=threadNumber;
		}
		else
		{
			log.info("Numero thread inserito troppo grande...default:4");
		}
		
		this.df=dataFrame;
		
		this.completeDM=df.dropna();
		this.completeDM=completeDM.slice(0,0);
		this.completeDM=completeDM.add("Id");
		
	}
	
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
	
	private void isDMComplete()
	{
		this.countPart++;
		if(countPart==MainActor.threadNr)
		{
			this.timerFine=System.currentTimeMillis();
			this.completeDM.show();
			//System.out.println(this.completeDM.toString());
			System.out.println("Concluso in tempo: "+(this.timerFine-this.timerInizio));
		}
	}
	
	static public class ConcurrenceDistanceMatrix
	{
		public ConcurrenceDistanceMatrix()
		{
		}
	}
	
	static public class ReceivePartDM
	{
		private DataFrame<Object> partialDM;
		
		public ReceivePartDM(DataFrame<Object> partialDM)
		{
			this.partialDM=partialDM;
		}
	}
	
	static public class TestMessage
	{
		
		public TestMessage()
		{
		}
	}
	
	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(ConcurrenceDistanceMatrix.class, c->
				{
					int dimension=df.length()/MainActor.threadNr;
					int lastStep= df.length()%MainActor.threadNr;
					int inizioCorrente=0;
					
					for(int i=0; i<MainActor.threadNr ;i++)
					{
						if(i<MainActor.threadNr-1)
						{
							ActorRef actor=this.getContext().actorOf(ConcurrentDMActor.props());
							actor.tell(new CreateConcurrentDM(this.df.slice(inizioCorrente, inizioCorrente+dimension),this.df), this.getSelf());
							
							inizioCorrente+=dimension;
						}
						else
						{
							ActorRef actor=this.getContext().actorOf(ConcurrentDMActor.props());
							actor.tell(new CreateConcurrentDM(this.df.slice(inizioCorrente, inizioCorrente+dimension+lastStep),this.df), 
									this.getSelf());
							
						}
					}
					
					this.timerInizio=System.currentTimeMillis();
					
				})
				.match(ReceivePartDM.class, r->
				{
					for(int i=0;i<r.partialDM.length();i++)
					{
						this.completeDM.append(r.partialDM.row(i));
					}
					this.isDMComplete();
					
				})
				.match(TestMessage.class, t->
				{
					log.info("ciao");
					
				}).build();
	}

}
