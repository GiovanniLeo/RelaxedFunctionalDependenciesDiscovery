package it.unisa.RFD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import it.unisa.RFD.utility.Tuple;
import joinery.DataFrame;

public class FeasibilityOrderedDM {

	public static HashMap<String,ArrayList<Tuple>> feasibilityTest(OrderedDM orderedDM)
	{
		
		HashMap<String,ArrayList<Integer>> cProvvisori = new HashMap<>();
		ArrayList<Integer> valueCluster;
		
		DataFrame<Object> dataframe = orderedDM.getOrderedDM();
		DataFrame<Object> dm = dataframe.retain(orderedDM.getLhs().toArray(new Object[orderedDM.getLhs().size()]));
		
		int lastRow = dm.length()-1;
		int currentCluster = (int) dataframe.get(lastRow, orderedDM.getRhs());
		String keyCluster = "C"+currentCluster;

		if(currentCluster==0)
		{
			return null;
		}
		else
		{
			keyCluster = "C"+currentCluster;
			valueCluster = new ArrayList<>();
			valueCluster.add(lastRow);
			cProvvisori.put(keyCluster, valueCluster);
		}
		
		for(int i = lastRow-1;i>=0;i--)
		{
			
			if(currentCluster!=(int)dataframe.get(i, orderedDM.getRhs()))
			{
				valueCluster = (ArrayList<Integer>) cProvvisori.get(keyCluster).clone();
				currentCluster = (int)dataframe.get(i, orderedDM.getRhs());
			    keyCluster = "C"+currentCluster;
			    
			    if(currentCluster==0)
				{
					return FeasibilityOrderedDM.convertHashMapToTuple(cProvvisori, dataframe);
				}
			    
			    cProvvisori.put(keyCluster, valueCluster);
			}
			
			boolean verificata = false;
			
			Iterator<Integer> iterator = cProvvisori.get(keyCluster).iterator();
			while (iterator.hasNext()) 
			{
		
				int secondaTupla = iterator.next();

				if(FeasibilityOrderedDM.dominance(i, secondaTupla, dm)==true)
				{
					verificata = true;
				}
				if(FeasibilityOrderedDM.dominance(secondaTupla, i, dm)==true)
				{
					iterator.remove();
				}


			}
			if(!verificata)
			{
				cProvvisori.get(keyCluster).add(i);
				
			}
		}
		return FeasibilityOrderedDM.convertHashMapToTuple(cProvvisori, dataframe);
	} 

	private static boolean dominance(int tupla1, int tupla2, DataFrame<Object> dm)
	{
	
		
		List<Object> firstRow = dm.row(tupla1);
		List<Object> secondRow = dm.row(tupla2);

		for(int i=0;i<dm.size();i++)
		{
			int firstElement = (int)firstRow.get(i);
			int secondElement = (int)secondRow.get(i);

			if((firstElement-secondElement)<0)
			{
			
				return false;
			
			}

		}
		return true;
	}
	
	private static HashMap<String,ArrayList<Tuple>> convertHashMapToTuple(HashMap<String,ArrayList<Integer>> cProvvisori, DataFrame<Object> dataframe)
	{
		HashMap<String,ArrayList<Tuple>> cDefinitivi = new HashMap<>();
		
		ArrayList<String> chiavi=new ArrayList<>();
		chiavi.addAll(cProvvisori.keySet());
		
		for(int count=0;count<chiavi.size();count++)
		{
			ArrayList<Integer> interiC=cProvvisori.get(chiavi.get(count));
			
			ArrayList<Tuple> insiemeC=new ArrayList<>();
			
			for(int countJ=0; countJ<interiC.size();countJ++)
			{
				insiemeC.add((Tuple) dataframe.get(interiC.get(countJ), dataframe.size()-1));
			}
			cDefinitivi.put(chiavi.get(count), insiemeC);
		}
		return cDefinitivi;
	}
}
