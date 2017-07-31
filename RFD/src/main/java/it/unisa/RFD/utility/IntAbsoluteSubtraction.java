package it.unisa.RFD.utility;

public class IntAbsoluteSubtraction implements Subtraction{
	/*
	 * La classe astratta Number e' la superclasse di  BigDecimal, BigInteger, Byte, Double, 
	 * Float, Integer,Long,e Short.
	 * La suddetta classe ha dei metodi per convertire i valori numerici rappresentati in 
	 * byte, double, float, int, long, and short.
	 */
	
	/**
	 * @param firstElement primo elemento della sottrazione
	 * @param secondElement secondo elemento della  sottrazione
	 * 
	 * Il metodo calcola la differenza assoluta fra due numeri gli elementi essendo 
	 * Object vengono prima controllati e se non sono un elemento numerico viene
	 * lanciata una eccezione.
	 * Se gli elementi superano il controllo vengono
	 * castati a Numeric e in seguito viene estratto il valore intero.
	 * @return La differenza assoluta fra i due numeri
	 */
	public int subtracion(Object firstElement, Object secondElement) 
	{
		
		
		boolean fisrtElementControl = firstElement instanceof Integer 
										|| firstElement instanceof Long 
										|| firstElement instanceof Double
										|| firstElement instanceof Float;
		boolean secondElementControl = secondElement instanceof Integer
										|| secondElement instanceof Long
										|| secondElement instanceof Double
										|| secondElement instanceof Float;
	
		if(firstElement==null || secondElement==null)
		{
			return -1;	
		}
		
		if(fisrtElementControl && secondElementControl)
				{
			Number first = (Number) firstElement;
			Number second = (Number) secondElement;
					
			return (int)Math.abs(Math.abs(first.intValue()) - Math.abs(second.intValue()));
		}
		else{
			throw new IllegalArgumentException("Expected a numeric element/s");
		}

	}
	
	
//	public static void main(String[] args) {
//		IntAbsoluteSubtraction abs = new IntAbsoluteSubtraction();
//		String i = "200";
//		long j = 200;
//		 System.out.println(abs.subtracion(i,j));
//		
//}

}
