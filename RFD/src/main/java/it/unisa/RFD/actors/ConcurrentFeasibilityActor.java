package it.unisa.RFD.actors;

import java.util.ArrayList;
import java.util.HashMap;

import akka.actor.AbstractActor;
import akka.actor.Props;
import it.unisa.RFD.FeasibilityOrderedDM;
import it.unisa.RFD.OrderedDM;
import it.unisa.RFD.utility.Tuple;

public class ConcurrentFeasibilityActor extends AbstractActor {

	static public class CreateFeasibiity {
		private OrderedDM orderedDM;
		public CreateFeasibiity(OrderedDM orderedDM) {
			this.orderedDM = orderedDM;
		}

	}
	
	public ConcurrentFeasibilityActor()
	{
		
		
	}
	public static Props props() {
		// TODO Auto-generated method stub
		return Props.create(ConcurrentFeasibilityActor.class);
	}

	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
				.match(CreateFeasibiity.class, cf->{
					OrderedDM dm = cf.orderedDM;
					HashMap<String,ArrayList<Tuple>> hMap = FeasibilityOrderedDM.feasibilityTest(dm);
					dm.setInsiemeC(hMap);
					this.getSender().tell(new MainActor.ReciveFeasibility(dm),this.getSelf());
				})
				.build();
	}

	

}
