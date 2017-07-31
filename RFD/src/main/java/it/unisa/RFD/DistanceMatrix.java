package it.unisa.RFD;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

import it.unisa.RFD.utility.DateSubtraction;
import it.unisa.RFD.utility.IntAbsoluteSubtraction;
import it.unisa.RFD.utility.StringSubtraction;
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
	public static Logger log=Logger.getLogger("log");
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
	 * Metodo che riceve in input nome del file csv e lo carica in un DataFrame
	 * @param nameCSV
	 * @param separator
	 * @param naString
	 * @param hasHeader
	 * @param dateFormat
	 * @param colDate
	 * @return dataFRame
	 * @throws IOException
	 * @throws ParseException
	 */
	public static DataFrame<Object> alternativeLoadDF(String nameCSV,char separator,String naString,boolean hasHeader,String dateFormat,int colDate) throws IOException, ParseException
	{
		CSVReader reader = new CSVReader(new FileReader(nameCSV), separator);
		DataFrame<Object> df=new DataFrame<>(Arrays.asList(reader.readNext()));
		String [] nextLine;
		while ((nextLine = reader.readNext()) != null) 
		{
			List<String> riga=Arrays.asList(nextLine);
			Collections.replaceAll(riga, naString, null);
			if(dateFormat!=null)
			{
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				String value=riga.get(colDate).toString();
				Date d = sdf.parse(value);
				
				sdf.applyPattern("yyyy/MM/dd");
				String newDateString = sdf.format(d);
				
				riga.set(colDate, newDateString);
			}
			
			df.append(riga);
	    }
		df=df.convert();
		typesColumn=df.dropna().types();
		reader.close();
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
			sottrazione=new StringSubtraction();
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
			
			sottrazione=new DateSubtraction();
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
		long timerInizio=System.currentTimeMillis();
		long timerFine;
		
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
						list.add(Integer.MAX_VALUE);
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
		
		
		timerFine=System.currentTimeMillis();
		System.out.println("Tempo impiegato: "+(timerFine-timerInizio));
		return distanceMatrix;
	} 
	
	/**
	 * @param inizio Indice di riga iniziale da confrontare
	 * @param dimensione Indice di numero righe da confrontare
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
	 * Versione per la parallelizzazione.
	 */
	public static DataFrame<Object> concurrentCreateMatrix(int inizio,int dimensione,DataFrame<Object> completeDF)
	{
		long timerInizio=System.currentTimeMillis();
		long timerFine;
		
		int colNumber = completeDF.size();
		int rowNumberComplete = completeDF.length();

		DataFrame<Object> distanceMatrix = completeDF.dropna();
		distanceMatrix = distanceMatrix.slice(0,0);
		distanceMatrix = distanceMatrix.add("Id");
		
		for (int i = inizio; i < inizio+dimensione; i++)
		{
			for (int j=i+1; j < rowNumberComplete; j++) 
			{
				
				ArrayList<Object> list = new ArrayList<>();
			
				for (int x = 0; x < colNumber; x++)
				{
					Subtraction sub = DistanceMatrix.checkTypes(x);
					
					int subReturn=sub.subtracion(completeDF.get(i, x), completeDF.get(j, x));
					
					if(subReturn==-1)
					{
						
						list.add(Integer.MAX_VALUE);
					}
					else
					{
						list.add(subReturn);
					}
					
				}
				list.add(new Tuple<Object,Object>(i,j));
				distanceMatrix.append(list);
			}
			
		}
		
		
		timerFine=System.currentTimeMillis();
		System.out.println("Tempo impiegato: "+(timerFine-timerInizio));
		return distanceMatrix;
	} 
	/**
	 * Metodo statico per la creazione di una DM ordinata in base a RHS dato come parametro
	 * @param indiceRHS colonna RHS
	 * @param dm distance matrix
	 * @return orderedDM DM ordinata
	 */
	public static OrderedDM createOrderedDM(int indiceRHS,DataFrame<Object> dm)
	{
		ArrayList<Object> indiciColonne=new ArrayList<>();
		indiciColonne.addAll(dm.columns());
		indiciColonne.remove(dm.size()-1);
		indiciColonne.remove(indiceRHS);
		
		return new OrderedDM(dm.sortBy(indiceRHS).groupBy(indiceRHS), indiciColonne, indiceRHS);
	}

}
