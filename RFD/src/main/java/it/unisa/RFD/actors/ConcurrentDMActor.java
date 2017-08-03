package it.unisa.RFD.actors;

import java.io.Serializable;
import java.util.ArrayList;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import it.unisa.RFD.DistanceMatrix;
import it.unisa.RFD.actors.MainActor.ConcurrenceDistanceMatrix;
import it.unisa.RFD.actors.MainActor.ReceivePartDM;
import it.unisa.RFD.utility.SerializedDataFrame;
import joinery.DataFrame;
/**
 * Attore che elabora una piccola parte di DataFrame per ottenere una DM parziale
 * @author luigidurso
 *
 */
public class ConcurrentDMActor extends AbstractActor 
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
		private ArrayList<ArrayList<Object>> completeDF;
		private int inizio,dimensione;
		
		public CreateConcurrentDM(int inizio,int dimensione,ArrayList<ArrayList<Object>> dataFrameCompleto)
		{
			this.completeDF=dataFrameCompleto;
			this.inizio=inizio;
			this.dimensione=dimensione;
		}
	}
	
	/**
	 * Messaggio di test
	 * @author luigidurso
	 *
	 */
	static public class TestMessage implements Serializable
	{
		private String msg;
		
		public TestMessage(String messaggio)
		{
			this.msg=messaggio;
		}
	}
	
	@Override
	public void preStart() throws Exception 
	{
		log.info("Sono vivo concurrentDMActor");
		super.postStop();
	}

	@Override
	public void postStop() throws Exception 
	{
		log.info("Sono morto concurrentDMActor");
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
					
					this.getSender().tell(new ReceivePartDM(DistanceMatrix.concurrentCreateMatrix(c.inizio,c.dimensione,SerializedDataFrame.deserializeDataFrame(c.completeDF))), this.getSelf());
					
				})
				.match(TestMessage.class, m->  //Stampa messaggio di test
				{
					
					System.out.println(m.msg);
					
				})
				.build();
	}

}
