package it.unisa.RFD.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import it.unisa.RFD.DistanceMatrix;
import it.unisa.RFD.actors.MainActor.ReceiveOrderedDM;
import joinery.DataFrame;
/**
 * Attore per la creazione di DM ordinate
 * @author luigidurso
 *
 */
public class ConcurrentOrderedDMActor extends AbstractActor 
{
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	/**
	 * Costruttore vuoto
	 */
	public ConcurrentOrderedDMActor()
	{
		
	}
	/**
	 * Props per la restituzione di referenza a ConcurrentOrderedDMActor
	 * @return ConcurrentOrderedDMActor reference
	 */
	public static Props props()
	{
		return Props.create(ConcurrentOrderedDMActor.class);
	}
	@Override
	public void preStart() throws Exception 
	{
		log.info("Sono vivo concurrentOrderedDMActor");
		super.postStop();
	}

	@Override
	public void postStop() throws Exception 
	{
		log.info("Sono morto concurrentOrderedDMActor");
		super.postStop();
	}
	/**
	 * Messaggio per la creazione di DM ordinata
	 * @author luigidurso
	 *
	 */
	public static class CreateOrderedDM
	{
		private DataFrame<Object> dm;
		private int indiceRHS;
		
		public CreateOrderedDM(DataFrame<Object> distanceMatrix,int rhs)
		{
			this.dm=distanceMatrix;
			this.indiceRHS=rhs;
		}
	}

	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(CreateOrderedDM.class, c-> //crea DM ordinata e la spedisce al mittente
				{
					
					this.getSender().tell(new ReceiveOrderedDM(DistanceMatrix.createOrderedDM(c.indiceRHS, c.dm)), this.getSelf());
					
				}).build();
	}

}
