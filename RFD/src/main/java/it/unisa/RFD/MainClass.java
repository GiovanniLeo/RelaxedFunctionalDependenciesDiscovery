package it.unisa.RFD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import joinery.DataFrame;
import it.unisa.RFD.actors.*;

/**
 * 
 *
 */
public class MainClass 
{
	public static void main( String[] args ) throws IOException, ParseException
	{

		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String nameCSV,separatorCSV,headingCSV,nullCharacterCSV,dateFormat;
		ArrayList<Integer> indiciData=new ArrayList<>();
		DataFrame<Object> df;
		

//		System.out.println("Name of CSV file");
//		nameCSV=console.readLine();
//		System.out.println("The separator character");                                       
//		separatorCSV=console.readLine();
//      System.out.println("Y or N if there is the heading");
//      headingCSV=console.readLine();
//      System.out.println("The null character");
//      nullCharacterCSV=console.readLine();
//		System.out.println("Date format(null if not exist)");
//      dateFormat=console.readLine();
//		System.out.println("Index of date column(-1 if not exist)");
//		ArrayList<String> indiciDataString;
//		indiciDataString.addAll(Arrays.asList(console.readLine().split("\\s+")));
//		
//		for(String indice: indiciDataString)
//		{
//			indiciData.add(Integer.parseInt(indice));
//		}
//		
//		if(headingCSV.equalsIgnoreCase("y"))
//			df = DistanceMatrix.loadDF(nameCSV,separatorCSV,nullCharacterCSV,true);
//		else
//			df = DistanceMatrix.loadDF(nameCSV,separatorCSV,nullCharacterCSV,false);

		df = DistanceMatrix.loadDF("cora.csv",";","?",true); 
//		indiciData.add(1);
//		df = DistanceMatrix.alternativeLoadDF("first_dataset2.csv",',',"?",true,"dd/MM/yyyy",indiciData); 
		df.show();
		
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + 2551).withFallback(
						ConfigFactory.load());
		
		ActorSystem system = ActorSystem.create("SistemaAttoriRDFCluster",config);
		try 
		{
			final ClusterSingletonManagerSettings settings =ClusterSingletonManagerSettings.create(system);
			system.actorOf(ClusterSingletonManager.props(MainActor.props(df),PoisonPill.getInstance(), settings), "AttorePrincipale");
			
			ClusterSingletonProxySettings proxySettings =ClusterSingletonProxySettings.create(system);
			ActorRef proxyPrincipal=system.actorOf(ClusterSingletonProxy.props("/user/AttorePrincipale", proxySettings), "AttorePrincipaleProxy");
			
			System.out.println(">>> Press ENTER to continue <<<");
		    console.readLine();
		    
//		    proxyPrincipal.tell(new MainActor.TestMessage(), ActorRef.noSender());
		    
		    proxyPrincipal.tell(new MainActor.ConcurrenceDistanceMatrix(), ActorRef.noSender());
		    
			System.out.println(">>> Press ENTER to exit <<<");
			console.readLine();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			system.terminate();
			console.close();
		}
		

	}
}
