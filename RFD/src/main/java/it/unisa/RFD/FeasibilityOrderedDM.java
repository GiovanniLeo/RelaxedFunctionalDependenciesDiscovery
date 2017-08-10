package it.unisa.RFD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import it.unisa.RFD.utility.Tuple;
import joinery.DataFrame;

public class FeasibilityOrderedDM {

	public static HashMap<String,ArrayList<Integer>> feasibilityTest(OrderedDM orderedDM)
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
			}
			if(currentCluster==0)
			{
				return cProvvisori;
			}


			cProvvisori.put(keyCluster, valueCluster);
			
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

		return cProvvisori;
	} 

	public static boolean dominance(int tupla1, int tupla2, DataFrame<Object> dm)
	{
	
		
		List<Object> firstRow = dm.row(tupla1);
		List<Object> secondRow = dm.row(tupla2);
		boolean domina = true;

		for(int i=0;i<dm.size();i++)
		{
			int firstElement = (int)firstRow.get(i);
			int secondElement = (int)secondRow.get(i);

			if((firstElement-secondElement)<0)
			{
			
				domina = false;
			
			}

		}
		return domina;
	}
}
