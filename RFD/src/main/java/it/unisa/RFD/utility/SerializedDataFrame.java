package it.unisa.RFD.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import joinery.DataFrame;
/**
 * Classe per serializzazione di DataFrame joinery
 * @author Giovanni Leo
 *
 */
public class SerializedDataFrame {

/**
 * Metodo statico che serializza un DF
 * @param df DataFrame
 * @return DF serializzato
 */
	public static  ArrayList<ArrayList<Object>>  serializeDF(DataFrame<Object> df)
	{
		ArrayList<ArrayList<Object>> dataframeRow = new ArrayList<>();
		ArrayList<Object> dataFrameColumns = new ArrayList<>();

		int colNumber = df.size();
		int rowNumber = df.length();

		for(Object col : df.columns())
		{
			dataFrameColumns.add(col);
		}
		
		dataframeRow.add(dataFrameColumns);

		for(int i = 0; i < rowNumber;i++)
		{
			List<Object> listRow = df.row(i);
			ArrayList<Object> row = new ArrayList<>();
			for(int j = 0;  j < listRow.size() ; j++)
			{
				row.add(listRow.get(j));
			}
			dataframeRow.add(row);
		}
		
		return dataframeRow;



	}
/**
 * Metodo per la deserializzazione di DataFrame joinery
 * @param dataframeRow
 * @return DF deserializzato
 */
	public static DataFrame<Object> deserializeDataFrame(ArrayList<ArrayList<Object>> dataframeRow)
	{
		DataFrame<Object> df = new DataFrame<Object>();
		for (int i = 0; i < dataframeRow.size(); i++) {
			if(i == 0)
			{
				ArrayList<Object> columnList = dataframeRow.get(0);
				for (int j = 0; j < columnList.size(); j++) {
					df.add(columnList.get(j));
				}
			}
			else
			{
				df.append(dataframeRow.get(i));
			}
		}
		return df;


	}




}
