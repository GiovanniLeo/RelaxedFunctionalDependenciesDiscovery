package it.unisa.RFD.actors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

import com.typesafe.config.Config;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.remote.routing.RemoteRouterConfig;
import akka.routing.ConsistentHashingPool;
import akka.routing.RoundRobinPool;
import it.unisa.RFD.OrderedDM;
import it.unisa.RFD.actors.ConcurrentDMActor.CreateConcurrentDM;
import it.unisa.RFD.utility.SerializedDataFrame;
import joinery.DataFrame;
import scala.Option;
/**
 * Attore principale che gestisce la parallelizzazione 
 * @author luigidurso
 *
 */
public class MainActor extends AbstractActor
{
	
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private int countPart=0,countOrderedDM=0;
	private DataFrame<Object> df;
	private DataFrame<Object> completeDM;
	private int threadNr=4;
	private long timerInizio,timerFine;
	private ArrayList<OrderedDM> listaDMOrdinati;
	Cluster cluster = Cluster.get(getContext().getSystem());
	private ArrayList<Address> addresses;
	
	
	/**
	 * Costruttore Main actor che riceve un dataframe completo ed il numero di thread con cui si desidera eseguire il programma.
	 * In fase di costruzione assegnamo le variabili di istanza e creiamo la nostra DM vuota.
	 * 
	 * @param dataFrame dataframe completo letto da file
	 */
	public MainActor(DataFrame<Object> dataFrame)
	{
		this.df=dataFrame;
		
		this.completeDM=df.dropna();
		this.completeDM=completeDM.slice(0,0);
		this.completeDM=completeDM.add("Id");
		
		this.listaDMOrdinati=new ArrayList<>();
		this.addresses=new ArrayList<>();
		this.addresses.add(this.getSelf().path().address());
		
	}
	/**
	 * Metodo statico per istanziare MainActor
	 * @param dataFrame
	 * @return reference a MainActor
	 */
	static public Props props(DataFrame<Object> dataFrame)
	{
		return Props.create(MainActor.class,()->new MainActor(dataFrame));
	}
	
	@Override
	public void preStart() throws Exception 
	{
		log.info("Sono vivo");
		cluster.subscribe(getSelf(),  MemberUp.class, MemberRemoved.class);
		super.postStop();
	}

	@Override
	public void postStop() throws Exception 
	{
		cluster.unsubscribe(getSelf());
		log.info("Sono morto");
		super.postStop();
	}
	/**
	 * Metodo per l'attesa del completamento della Distance Matrix.
	 */
	private void isDMComplete()
	{
		this.countPart++;
		if(countPart==this.threadNr)
		{
			this.timerFine=System.currentTimeMillis();
			System.out.println("Concluso in tempo: "+(this.timerFine-this.timerInizio));
			
			this.completeDM.show();
			
			ActorRef routerRemote = getContext().actorOf(new RemoteRouterConfig(new RoundRobinPool(this.completeDM.size()-1), addresses).props(ConcurrentOrderedDMActor.props()));
			for(int i=0; i<this.completeDM.size()-1; i++)
			{
				routerRemote.tell(new ConcurrentOrderedDMActor.CreateOrderedDM(SerializedDataFrame.serializeDF(completeDM),i), this.getSelf());
			}
		}
	}
	/**
	 * Metodo per l'attesa del completamento di tutte le DM ordinate
	 */
	private void isOrderedDMComplete()
	{
		countOrderedDM++;
		if(countOrderedDM==this.completeDM.size()-1)
		{
			this.timerFine=System.currentTimeMillis();
			System.out.println("Concluso in tempo Cluster: "+(this.timerFine-this.timerInizio));
			this.listaDMOrdinati.get(1).getOrderedDM().show();
		}
	}
	/**
	 * Messaggio per l'inizio della parallelizzazione della DM.
	 * @author luigidurso
	 *
	 */
	static public class ConcurrenceDistanceMatrix
	{
		public ConcurrenceDistanceMatrix()
		{
		}
	}
	/**
	 * Messaggio per la ricezione di parti della DM 
	 * @author luigidurso
	 *
	 */
	static public class ReceivePartDM implements Serializable
	{
		private ArrayList<ArrayList<Object>> partialDM;
		
		public ReceivePartDM(ArrayList<ArrayList<Object>> partialDM)
		{
			this.partialDM=partialDM;
		}
	}
	/**
	 * Messaggio ricezione DM ordinata
	 * @author luigidurso
	 *
	 */
	static public class ReceiveOrderedDM implements Serializable
	{
		private OrderedDM orderedDM;
		
		public ReceiveOrderedDM(OrderedDM orderedDM)
		{
			this.orderedDM=orderedDM;
		}
	}
	/**
	 * Messaggio di test
	 * @author luigidurso
	 *
	 */
	static public class TestMessage
	{
		
		public TestMessage()
		{
		}
	}
	/**
	 * Builder per la ricezione dei messaggi
	 */
	@Override
	public Receive createReceive() 
	{
		return receiveBuilder()
				.match(ConcurrenceDistanceMatrix.class, c->  //Creo il numero di thread necessari per la creazione di DM
				{
					this.threadNr=4*this.addresses.size();
					ActorRef routerRemote = getContext().actorOf(new RemoteRouterConfig(new RoundRobinPool(this.threadNr), addresses).props(ConcurrentDMActor.props()));
					
					int dimension=df.length()/this.threadNr;
					int lastStep= df.length()%this.threadNr;
					int inizioCorrente=0;
										
					for(int i=0; i<this.threadNr ;i++)
					{
						if(i<this.threadNr-1)
						{
							
							routerRemote.tell(new ConcurrentDMActor.CreateConcurrentDM(inizioCorrente,dimension,SerializedDataFrame.serializeDF(this.df)), this.getSelf());
							inizioCorrente+=dimension;
						}
						else
						{
							routerRemote.tell(new ConcurrentDMActor.CreateConcurrentDM(inizioCorrente,dimension+lastStep,SerializedDataFrame.serializeDF(this.df)), this.getSelf());
						}
					}			
					this.timerInizio=System.currentTimeMillis();
					
				})
				.match(ReceivePartDM.class, r->  //Messaggio con cui riceve parte della DM elaborata da ogni thread 
				{
					DataFrame<Object> dataFrameParziale=SerializedDataFrame.deserializeDataFrame(r.partialDM);
					for(int i=0;i<dataFrameParziale.length();i++)
					{
						this.completeDM.append(dataFrameParziale.row(i));
					}
					
					this.isDMComplete();
					
				})
				.match(TestMessage.class, t->  //messaggio di test
				{
					
				})
				.match(MemberUp.class, mUp -> 
				{
			        log.info("Member is Up: {}", mUp.member());
			        this.addresses.add(mUp.member().address());
			        System.out.println(addresses.toString());
			    })
				.match(MemberRemoved.class, mp -> 
				{
			        log.info("Member is Removed: {}", mp.member());
			        this.addresses.remove(mp.member().address());
			        System.out.println(addresses.toString());
			    })
				.match(ReceiveOrderedDM.class, rc-> //messaggio che riceve le DM ordinate
				{
					log.info("Ricevuta DM ordinata da: {}", this.getSender());
					this.listaDMOrdinati.add(rc.orderedDM);
					this.isOrderedDMComplete();
					
				}).build();
	}

}
