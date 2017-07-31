package it.unisa.RFD.utility;

import java.util.Date;
import java.util.GregorianCalendar;

public class DateSubtraction implements Subtraction{

	/**
	 * @param firstElement primo elemento della sottrazione
	 * @param secondElement secondo elemento della sottrazione
	 * 
	 * Il metodo calcola la differenza tra due date e la ritorna in giorni.
	 * Si controlla inizialmente se una delle due date e' null e in caso positivo ritorna -1.
	 * Se i due parametri passati sono effettivamente istanze di GregorianCalendar controlla la differenza e la ritorna in giorni.
	 * 
	 * @return La differenza tra le due date in giorni.
	 */
	public int subtracion(Object firstElement, Object secondElement) {
		
		if(firstElement == null || secondElement == null)
		{
			return -1;
		}
		
		if(firstElement instanceof Date && secondElement instanceof Date)
		{
			Date date1 = (Date) firstElement;
			Date date2 = (Date) secondElement;
			
			GregorianCalendar grgDate1=new GregorianCalendar();
			GregorianCalendar grgDate2=new GregorianCalendar();
			
			grgDate1.setTime(date1);
			grgDate2.setTime(date2);
			
			long millisecondsDate1 = grgDate1.getTimeInMillis();
			long millisecondsDate2 = grgDate2.getTimeInMillis();
			
			return (int) Math.abs(((millisecondsDate1-millisecondsDate2)/(24*60*60*1000))) ;
			
		}
		
		else
		{
			throw new IllegalArgumentException("Expected a date element/s");
		}
	
	}

}
