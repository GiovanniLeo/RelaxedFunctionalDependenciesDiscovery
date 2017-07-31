package it.unisa.RFD.thread;

import it.unisa.RFD.DistanceMatrix;
import joinery.DataFrame;

public class MatrixCreationThread extends Thread {

	private DataFrame<Object> dF;
	private int inizio;
	private int dimensione;

	  
	
	public MatrixCreationThread(DataFrame<Object> dF, int inizio, int dimensione) {
		this.dF = dF;
		this.inizio = inizio;
		this.dimensione = dimensione;
	}



	@Override
	public void run() {
		dF = DistanceMatrix.concurrentCreateMatrix(inizio, dimensione, dF);
		super.run();
	}



	public DataFrame<Object> getdF() {
		return dF;
	}
	
	
	
	
	
	

}
