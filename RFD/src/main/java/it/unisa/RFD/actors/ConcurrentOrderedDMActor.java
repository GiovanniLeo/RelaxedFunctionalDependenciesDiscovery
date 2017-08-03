package it.unisa.RFD.actors;

import java.util.ArrayList;

import akka.actor.AbstractActor;
import akka.actor.Props;
import it.unisa.RFD.DistanceMatrix;
import it.unisa.RFD.actors.MainActor.ReceiveOrderedDM;
import it.unisa.RFD.utility.SerializedDataFrame;
import joinery.DataFrame;
/**
 * Attore per la creazione di DM ordinate
 * @author luigidurso
 *
 */
public class ConcurrentOrderedDMActor extends AbstractActor 
{
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
	/**
	 * Messaggio per la creazione di DM ordinata
	 * @author luigidurso
	 *
	 */
	public static class CreateOrderedDM
	{
		private ArrayList<ArrayList<Object>> dm;
		private int indiceRHS;
		
		public CreateOrderedDM(ArrayList<ArrayList<Object>> distanceMatrix,int rhs)
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
					
					this.getSender().tell(new ReceiveOrderedDM(DistanceMatrix.createOrderedDM(c.indiceRHS,SerializedDataFrame.deserializeDataFrame(c.dm))), this.getSelf());
					
				}).build();
	}

}
