package it.unisa.RFD;

import java.io.IOException;
import java.util.List;

import joinery.DataFrame;
/**
 * Classe che provvede alla creazione della matrice delle distanze partendo da un dataframe caricato da file csv
 * @author luigidurso
 *
 */
public class DistanceMatrix 
{
	private List<Class<?>> typesColumn;
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
		
		return DataFrame.readCsv(nameCSV, separator, naString, hasHeader);
		
	}
	
	/**
	 * Metodo che cerca tipo di elemento colonna e restituisce istanza dell'interfaccia per la sottrazione
	 * @param indiceColonna
	 * @return Subtraction istanza dell'interfaccia per la sottrazione
	 */
	private void checkTypes(int indiceColonna)
	{
		
	}
	

}
