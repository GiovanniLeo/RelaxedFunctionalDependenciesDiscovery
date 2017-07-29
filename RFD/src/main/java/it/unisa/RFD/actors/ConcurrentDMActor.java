package it.unisa.RFD.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import it.unisa.RFD.DistanceMatrix;
import it.unisa.RFD.actors.MainActor.ConcurrenceDistanceMatrix;
import it.unisa.RFD.actors.MainActor.ReceivePartDM;
import joinery.DataFrame;
/**
 * 
 * @author luigidurso
 *
 */
public class ConcurrentDMActor extends AbstractActor 
{
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	
	public ConcurrentDMActor() 
	{
	}
	
	static public Props props()
	{
		return Props.create(ConcurrentDMActor.class);
	}
	
	static public class CreateConcurrentDM
	{
		private DataFrame<Object> completeDF;
		private int inizio,dimensione;
		
		public CreateConcurrentDM(int inizio,int dimensione,DataFrame<Object> dataFrameCompleto)
		{
			this.completeDF=dataFrameCompleto;
			this.inizio=inizio;
			this.dimensione=dimensione;
		}
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

	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(CreateConcurrentDM.class, c->
				{
					
					this.getSender().tell(new ReceivePartDM(DistanceMatrix.concurrentCreateMatrix(c.inizio,c.dimensione,c.completeDF)), this.getSelf());
					
				}).build();
	}

}
