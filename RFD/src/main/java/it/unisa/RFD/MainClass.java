package it.unisa.RFD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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

		df = DistanceMatrix.loadDF("datasetto.csv",",","?",true); 
//		indiciData.add(1);
//		df = DistanceMatrix.alternativeLoadDF("first_dataset2.csv",',',"?",true,"dd/MM/yyyy",indiciData); 
		df.show();

		DataFrame<Object> dm = DistanceMatrix.createMatrix(df);
		dm.show();
//
//		
		OrderedDM oDM=DistanceMatrix.createOrderedDM(2, dm);
		oDM.getOrderedDM().show();
		System.out.println(FeasibilityOrderedDM.feasibilityTest(oDM).toString());
		
//		ActorSystem system = ActorSystem.create("SistemaAttoriRDF");
//		try 
//		{
//			ActorRef act=system.actorOf(MainActor.props(df,4),"AttorePrincipale");
//			act.tell(new MainActor.ConcurrenceDistanceMatrix(), ActorRef.noSender());
//			System.out.println(">>> Press ENTER to exit <<<");
//		    System.in.read();
//		} 
//		catch (Exception e) 
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally
//		{
//			system.terminate();
//			console.close();
//		}
		

	}
}
