package it.unisa.RFD.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import it.unisa.RFD.actors.ConcurrentDMActor.CreateConcurrentDM;
import joinery.DataFrame;
/**
 * 
 * @author luigidurso
 *
 */
public class MainActor extends AbstractActor
{
	private DataFrame<Object> df;
	private DataFrame<Object> completeDM;
	
	public MainActor(DataFrame<Object> dataFrame)
	{
		this.df=dataFrame;
		
		this.completeDM=df.dropna();
		this.completeDM=completeDM.slice(0,0);
		this.completeDM=completeDM.add("Id");
	}
	
	static public Props props(DataFrame<Object> dataFrame)
	{
		return Props.create(MainActor.class,()->new MainActor(dataFrame));
	}
	
	static public class ConcurrenceDistanceMatrix
	{
		private int threadNr=4;
		private ActorSystem actSystem;
		
		public ConcurrenceDistanceMatrix(ActorSystem sysAct)
		{
			this.actSystem=sysAct;
		}
		
		public ConcurrenceDistanceMatrix(ActorSystem sysAct,int threadNumber)
		{
			this.actSystem=sysAct;
			this.threadNr=threadNumber;
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
	
	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(ConcurrenceDistanceMatrix.class, c->
				{
					int dimension=df.length()/c.threadNr;
					int lastStep= df.length()%c.threadNr;
					int inizioCorrente=0;
					
					for(int i=0; i<c.threadNr ;i++)
					{
						if(i<c.threadNr-1)
						{
							ActorRef actor=c.actSystem.actorOf(ConcurrentDMActor.props());
							actor.tell(new CreateConcurrentDM(this.df.slice(inizioCorrente, inizioCorrente+dimension)), this.getSender());
							
							inizioCorrente+=dimension;
						}
						else
						{
							ActorRef actor=c.actSystem.actorOf(ConcurrentDMActor.props());
							actor.tell(new CreateConcurrentDM(this.df.slice(inizioCorrente, inizioCorrente+dimension+lastStep)), 
									this.getSender());
						}
					}
					
				})
				.match(ReceivePartDM.class, r->
				{
					System.out.println(r.partialDM.toString());
					
				}).build();
	}

}
