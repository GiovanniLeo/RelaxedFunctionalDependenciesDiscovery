package it.unisa.RFD.actors;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.HashMap;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
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
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
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
	@Override
	public void preStart() throws Exception 
	{
		log.info("Sono vivo concurrentFeasibilityActor");
		super.postStop();
	}

	@Override
	public void postStop() throws Exception 
	{
		log.info("Sono morto concurrentFeasibilityActor");
		super.postStop();
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
					Object2ObjectOpenHashMap<String,ObjectArrayList<Tuple>> hMap = FeasibilityOrderedDM.feasibilityTest(dm);
					dm.setInsiemeC(hMap);
					this.getSender().tell(new MainActor.ReciveFeasibility(dm),this.getSelf());
				})
				.build();
	}
}
