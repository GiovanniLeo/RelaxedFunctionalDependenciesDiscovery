package it.unisa.RFD.utility;

import java.io.Serializable;

public class Tuple implements Serializable
{

	private final int x;
	private final int y;
	/**
	 * @param x
	 * @param y
	 * Coppia di elementi immutabili.
	 */
	public Tuple(int x, int y) {
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
	public int getX() {
		return x;
	}
	
	/**
	 * Restituisce il secondo elemento della tupla.
	 * @return primo elemento della tupla
	 */
	public int getY() {
		return y;
	}



}
