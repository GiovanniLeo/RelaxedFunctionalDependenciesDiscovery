package it.unisa.RFD;


import java.util.Iterator;
import java.util.List;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unisa.RFD.utility.Tuple;
import joinery.DataFrame;
/**
 * Classe di utility per il feasibility test
 * @author 
 *
 */
public class FeasibilityOrderedDM 
{
/**
 * Metodo che permette di calcolare l'insieme c dell'orderedDM dato come parametro
 * @param orderedDM
 * @return hashMap contenente l'insieme c
 */
	public static Object2ObjectOpenHashMap<String,ObjectArrayList<Tuple>> feasibilityTest(OrderedDM orderedDM)
	{
		if(orderedDM.getOrderedDM().isEmpty())
		{
			return null;
		}
		
		Object2ObjectOpenHashMap<String,ObjectArrayList<Integer>> cProvvisori = new Object2ObjectOpenHashMap<>();
		ObjectArrayList<Integer> valueCluster;
		
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
			valueCluster = new ObjectArrayList<>();
			valueCluster.add(lastRow);
			cProvvisori.put(keyCluster, valueCluster);
		}
		
		for(int i = lastRow-1;i>=0;i--)
		{
			
			if(currentCluster!=(int)dataframe.get(i, orderedDM.getRhs()))
			{
				valueCluster = (ObjectArrayList<Integer>) cProvvisori.get(keyCluster).clone();
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
/**
 * Metodo che permette di verificare se tupla1 domina tupla2, in questo caso ritorniamo ture
 * @param tupla1
 * @param tupla2
 * @param dm
 * @return boolean
 */
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
	/**
	 * Metodo che permette di convertire un hashMap dell'insieme c che identifica le righe attraverso gli indici, in hashMap dell'insieme c che identifica le righe con l'id. 
	 * @param cProvvisori
	 * @param dataframe
	 * @return hashMap dell'insieme c con tuple come identificativo delle righe
	 */
	private static Object2ObjectOpenHashMap<String,ObjectArrayList<Tuple>> convertHashMapToTuple(Object2ObjectOpenHashMap<String,ObjectArrayList<Integer>> cProvvisori, DataFrame<Object> dataframe)
	{
		Object2ObjectOpenHashMap<String,ObjectArrayList<Tuple>> cDefinitivi = new Object2ObjectOpenHashMap<>();
		
		ObjectArrayList<String> chiavi=new ObjectArrayList<>();
		chiavi.addAll(cProvvisori.keySet());
		
		for(int count=0;count<chiavi.size();count++)
		{
			ObjectArrayList<Integer> interiC=cProvvisori.get(chiavi.get(count));
			
			ObjectArrayList<Tuple> insiemeC=new ObjectArrayList<>();
			
			for(int countJ=0; countJ<interiC.size();countJ++)
			{
				insiemeC.add((Tuple) dataframe.get(interiC.get(countJ), dataframe.size()-1));
			}
			cDefinitivi.put(chiavi.get(count), insiemeC);
		}
		return cDefinitivi;
	}
}
