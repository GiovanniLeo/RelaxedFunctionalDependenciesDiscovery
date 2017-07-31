package it.unisa.RFD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import joinery.DataFrame;
import it.unisa.RFD.actors.*;

/**
 * 
 *
 */
public class MainClass 
{
	public static void main( String[] args ) throws IOException
	{

		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String nameCSV,separatorCSV,headingCSV,nullCharacterCSV;
		DataFrame<Object> df;


//		        System.out.println("Name of CSV file");
//		        nameCSV=console.readLine();
//		        System.out.println("The separator character");                                       
//		        separatorCSV=console.readLine();
//		        System.out.println("Y or N if there is the heading");
//		        headingCSV=console.readLine();
//		        System.out.println("The null character");
//		        nullCharacterCSV=console.readLine();
//		        
//		        if(headingCSV.equalsIgnoreCase("y"))
//		            df = DistanceMatrix.loadDF(nameCSV,separatorCSV,nullCharacterCSV,true);
//		        else
//		        		df = DistanceMatrix.loadDF(nameCSV,separatorCSV,nullCharacterCSV,false);

		df = DistanceMatrix.loadDF("hepatitis.csv",",","?",true); 

//		DataFrame<Object> dm = DistanceMatrix.createMatrix(df);
//		dm.show();
//
//		
//		OrderedDM oDM=DistanceMatrix.createOrderedDM(1, dm);
//		oDM.getOrderedDM().show();
		
		ActorSystem system = ActorSystem.create("SistemaAttoriRDF");
		try 
		{
			ActorRef act=system.actorOf(MainActor.props(df,4),"AttorePrincipale");
			act.tell(new MainActor.ConcurrenceDistanceMatrix(), ActorRef.noSender());
			System.out.println(">>> Press ENTER to exit <<<");
		    System.in.read();
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			system.terminate();
		}
		

	}
}
