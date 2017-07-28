package it.unisa.RFD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import it.unisa.RFD.utility.IntAbsoluteSubtraction;
import it.unisa.RFD.utility.Subtraction;
import it.unisa.RFD.utility.Tuple;
import joinery.DataFrame;
/**
 * Classe che provvede alla creazione della matrice delle distanze partendo da un dataframe caricato da file csv
 * @author luigidurso
 *
 */
public class DistanceMatrix 
{
	static private List<Class<?>> typesColumn;
	/**
	 * Metodo che riceve in input nome del file csv e lo carica in un DataFrame
	 * @param nameCSV nome file CSV
	 * @param separator separatore di colonne utilizzato nel file
	 * @param naString stringa nulla
	 * @param hasHeader presenza di header nel file
	 * @return Dataframe  DataFrame caricato da file
	 * @throws IOException 
	 */
	public static DataFrame<Object> loadDF(String nameCSV,String separator,String naString,boolean hasHeader) throws IOException
	{
		DataFrame<Object> df=DataFrame.readCsv(nameCSV, separator, naString, hasHeader);
		typesColumn=df.dropna().types();
		return  df;
		
	}
	
	/**
	 * Metodo che cerca tipo di elemento colonna e restituisce istanza dell'interfaccia per la sottrazione
	 * @param indiceColonna
	 * @return Subtraction istanza dell'interfaccia per la sottrazione
	 */
	private static Subtraction checkTypes(int indiceColonna)
	{
		
		Class<?> classType=typesColumn.get(indiceColonna);
		
		Subtraction sottrazione=null;
		
		switch (classType.getSimpleName()) 
		{
		case "String":
			
			break;
			
		case "Long":
			
			sottrazione=new IntAbsoluteSubtraction();
			break;
			
		case "Double":
			
			sottrazione=new IntAbsoluteSubtraction();
			break;
			
		case "Object":
			
			sottrazione=new IntAbsoluteSubtraction();
			break;
			
		case "Date":
			
			
			break;

		default:
			break;
		}
		
		return sottrazione;
		
	}
	/**
	 * @param df dataFrame in input
	 * @return Matrice Delle Distanze
	 * Otteniamo il numero di righe e di colonne.
	 * Copiamo il dataframe preso in input in una variabile distanceMatrix, attraverso slice otteniamo
	 * l'header e aggiungiamo la colonna id.
	 * Il primo for fissa il primo elemento della Tupla, il secondo for
	 * fissa il secondo elemento della tupla che  verra' incrementato ad ogni iterazione finch� non
	 * ci sono altri elementi.
	 * Il terzo for seleziona la colonna, ottenendo un confronto tra gli elementi della riga i e della
	 * riga j nella medesima colonna.
	 * Il risultato viene inserito in una lista che, insieme agli indici delle righe confrontate,
	 * viene inserito nel nuovo dataframe. 
	 */
	public static DataFrame<Object> createMatrix(DataFrame<Object> df)
	{
		Object[] indiciValidi=df.index().toArray();
		
		int colNumber = df.size();
		int rowNumber = df.length();

		DataFrame<Object> distanceMatrix = df.dropna();
		distanceMatrix = distanceMatrix.slice(0,0);
		distanceMatrix = distanceMatrix.add("Id");
		
		for (int i = 0; i < rowNumber; i++)
		{
			for (int j=i+1; j < rowNumber; j++) 
			{
				
				ArrayList<Object> list = new ArrayList<>();
			
				for (int x = 0; x < colNumber; x++)
				{
					Subtraction sub = DistanceMatrix.checkTypes(x);
					
					int subReturn=sub.subtracion(df.get(i, x), df.get(j, x));
					
					if(subReturn==-1)
					{
						list.add(null);
					}
					else
					{
						list.add(subReturn);
					}
					
				}
				list.add(new Tuple<Object,Object>(indiciValidi[i],indiciValidi[j]));
				distanceMatrix.append(list);
			}
			
		}

		return distanceMatrix;
	} 
	

}
