package it.unisa.RFD.utility;

import java.io.Serializable;

public class Tuple <X,Y> implements Serializable
{

	private final X x;
	private final Y y;
	/**
	 * @param x
	 * @param y
	 * Coppia di elementi immutabili.
	 */
	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "<" + x + ", " + y +">";
	}
	
	/**
	 * Restituisce il primo elemento della tupla.
	 * @return primo elemento della tupla
	 */
	public X getX() {
		return x;
	}
	
	/**
	 * Restituisce il secondo elemento della tupla.
	 * @return primo elemento della tupla
	 */
	public Y getY() {
		return y;
	}



}
