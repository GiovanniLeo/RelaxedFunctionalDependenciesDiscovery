package it.unisa.RFD;

import java.util.ArrayList;

import it.unisa.RFD.utility.Tuple;
import joinery.DataFrame;
/**
 * Classe rappresentante la distance matrix ordinata per RHS e divisa in cluster
 * @author luigidurso
 *
 */
public class OrderedDM 
{
	private DataFrame<Object> orderedDM;
	private ArrayList<Object> lhs;
	private int rhs;
	private ArrayList<Tuple> insiemeC;
	
	/**
	 * Costruttore che prende DM ordinata e indici per RHS e LHS
	 * @param orderedDistanceM
	 * @param indiciLHS
	 * @param indiceRHS
	 */
	public OrderedDM(DataFrame<Object> orderedDistanceM,ArrayList<Object> indiciLHS,int indiceRHS)
	{
		this.orderedDM=orderedDistanceM;
		this.lhs=indiciLHS;
		this.rhs=indiceRHS;
		this.insiemeC=null;
	}
	/**
	 * Getter DM
	 * @return orderedDM DM ordinata
	 */
	public DataFrame<Object> getOrderedDM()
	{
		return orderedDM;
	}
	/**
	 * Set DM ordinata
	 * @param orderedDM
	 */
	public void setOrderedDM(DataFrame<Object> orderedDM) 
	{
		this.orderedDM = orderedDM;
	}
	/**
	 * Getter indici LHS
	 * @return lhs 
	 */
	public ArrayList<Object> getLhs() 
	{
		return lhs;
	}
	/**
	 * Setter indici LHS
	 * @param lhs
	 */
	public void setLhs(ArrayList<Object> lhs) 
	{
		this.lhs = lhs;
	}
	/**
	 * Getter indice RHS
	 * @return rhs
	 */
	public int getRhs() 
	{
		return rhs;
	}
	/**
	 * Setter indice RHS
	 * @param rhs
	 */
	public void setRhs(int rhs)
	{
		this.rhs = rhs;
	}
	/**
	 * Getter insiemeC
	 * @return insiemeC
	 */
	public ArrayList<Tuple> getInsiemeC() 
	{
		return insiemeC;
	}
	/**
	 * Setter insiemeC
	 * @param insiemeC
	 */
	public void setInsiemeC(ArrayList<Tuple> insiemeC) 
	{
		this.insiemeC = insiemeC;
	}

}
