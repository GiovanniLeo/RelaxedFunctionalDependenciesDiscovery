package it.unisa.RFD.actors;

import java.util.ArrayList;
import java.util.HashMap;

import akka.actor.AbstractActor;
import akka.actor.Props;
import it.unisa.RFD.FeasibilityOrderedDM;
import it.unisa.RFD.OrderedDM;
import it.unisa.RFD.utility.Tuple;
/**
 * Attore per la gestione della fase feasibility
 * @author 
 *
 */
public class ConcurrentFeasibilityActor extends AbstractActor 
{
	/**
	 * Costruttore vuoto
	 */
	public ConcurrentFeasibilityActor()
	{
		
	}
	/**
	 * Props per la creazione di istanza dell'attore
	 * @return istanza ConcurrentFeasibilityActor
	 */
	public static Props props() 
	{
		return Props.create(ConcurrentFeasibilityActor.class);
	}
/**
 * 
 * Messaggio per la gestione del feasibility test
 * @author 
 * 
 */
	static public class CreateFeasibiity 
	{
		private OrderedDM orderedDM;
		
		public CreateFeasibiity(OrderedDM orderedDM) 
		{
			this.orderedDM = orderedDM;
		}
	}
	/**
	 * 
	 * Builder per la gestione dei messaggi ricevuti
	 * 
	 */
	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(CreateFeasibiity.class, cf-> //Gestione feasibility test e risposta al mittente
				{
					OrderedDM dm = cf.orderedDM;
					HashMap<String,ArrayList<Tuple>> hMap = FeasibilityOrderedDM.feasibilityTest(dm);
					dm.setInsiemeC(hMap);
					this.getSender().tell(new MainActor.ReciveFeasibility(dm),this.getSelf());
				})
				.build();
	}
}
