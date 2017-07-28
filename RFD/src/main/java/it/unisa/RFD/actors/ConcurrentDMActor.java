package it.unisa.RFD.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
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
	
	public ConcurrentDMActor() 
	{
	}
	
	static public Props props()
	{
		return Props.create(ConcurrentDMActor.class);
	}
	
	static public class CreateConcurrentDM
	{
		private DataFrame<Object> df;
		
		public CreateConcurrentDM(DataFrame<Object> dataFrame)
		{
			this.df=dataFrame;
		}
	}

	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(CreateConcurrentDM.class, c->
				{
					this.getSender().tell(new ReceivePartDM(DistanceMatrix.createMatrix(c.df)), this.getSelf());
					
				}).build();
	}

}
