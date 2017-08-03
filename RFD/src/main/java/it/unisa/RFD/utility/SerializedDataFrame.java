package it.unisa.RFD.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import joinery.DataFrame;

public class SerializedDataFrame implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<ArrayList<Object>> dataframeRow; 

	public  void  serializeDF(DataFrame<Object> df)
	{
		dataframeRow = new ArrayList<>();
		ArrayList<Object> dataFrameColumns = new ArrayList<>();

		int colNumber = df.size();
		int rowNumber = df.length();

		for(Object col : df.columns())
		{
			dataFrameColumns.add(col);
		}

		System.out.println(dataFrameColumns.toString());
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



	}

	public DataFrame<Object> deserializeDataFrame()
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

	public ArrayList<ArrayList<Object>> getDataframeRow() {
		return dataframeRow;
	}




}
