package it.unisa.RFD.utility;

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
		
		if(firstElement instanceof GregorianCalendar && secondElement instanceof GregorianCalendar)
		{
			GregorianCalendar date1 = (GregorianCalendar) firstElement;
			GregorianCalendar date2 = (GregorianCalendar) secondElement;
			
			long millisecondsDate1 = date1.getTimeInMillis();
			long millisecondsDate2 = date2.getTimeInMillis();
			
			return (int) ((millisecondsDate1-millisecondsDate2)/(24*60*60*1000));
			
		}
		
		else
		{
			throw new IllegalArgumentException("Expected a date element/s");
		}
	
	}

}
