package it.unisa.RFD.actors;

import java.io.Serializable;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import it.unisa.RFD.DistanceMatrix;
import it.unisa.RFD.actors.MainActor.ConcurrenceDistanceMatrix;
import it.unisa.RFD.actors.MainActor.ReceivePartDM;
import joinery.DataFrame;
/**
 * Attore che elabora una piccola parte di DataFrame per ottenere una DM parziale
 * @author luigidurso
 *
 */
public class ConcurrentDMActor extends AbstractActor  implements Serializable
{
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	/**
	 * Costruttore vuoto
	 */
	public ConcurrentDMActor() 
	{
	}
	/**
	 * Props per la creazione di referenza per ConcurrentDMActor
	 * @return Reference a ConcurrentDMActor
	 */
	static public Props props()
	{
		return Props.create(ConcurrentDMActor.class);
	}
	/**
	 * Messaggio per la creazione della DM parziale
	 * @author luigidurso
	 *
	 */
	static public class CreateConcurrentDM implements Serializable
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
	/**
	 * Builder per la ricezione dei messaggi
	 */
	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(CreateConcurrentDM.class, c->  //Chiama metodo per la creazione della DM parziale e la invia al MainActor
				{
					
					this.getSender().tell(new ReceivePartDM(DistanceMatrix.concurrentCreateMatrix(c.inizio,c.dimensione,c.completeDF)), this.getSelf());
					
				}).build();
	}

}
