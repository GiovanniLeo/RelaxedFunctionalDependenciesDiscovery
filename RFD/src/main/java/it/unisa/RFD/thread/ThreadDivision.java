package it.unisa.RFD.thread;


import java.util.Vector;

import akka.actor.ActorRef;
import it.unisa.RFD.actors.ConcurrentDMActor;
import it.unisa.RFD.actors.ConcurrentDMActor.CreateConcurrentDM;
import joinery.DataFrame;

public class ThreadDivision {

	private DataFrame<Object> df;
	private int numThr;
	private int inizioCorrente = 0;
	private int dimension;
	private DataFrame<Object> completeDM;


	public ThreadDivision(DataFrame<Object> df, int numThr) {
		this.df = df;
		this.numThr = numThr;
		completeDM = new DataFrame<Object>();
	}

	public void parallizationStart()
	
	{
		Vector<MatrixCreationThread> thrs = new Vector<>();

		int dimension=df.length()/this.numThr;
		int lastStep= df.length()%this.numThr;
		int inizioCorrente=0;

		long timerInizio=System.currentTimeMillis();
		long timerFine;
		
		for(int i=0; i<this.numThr ;i++)
		{
			if(i<this.numThr-1)
			{
				MatrixCreationThread mct = new MatrixCreationThread(this.df, inizioCorrente, dimension);
				mct.start();
				thrs.add(mct);
				inizioCorrente+=dimension;
			}
			else
			{
				MatrixCreationThread mct = new MatrixCreationThread(this.df, inizioCorrente, 
						dimension+lastStep);
				mct.start();
				thrs.add(mct);

			}

		}

		for (int i = 0; i < this.numThr; i++) {
			try{
				thrs.get(i).join();
			} 
			catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		
		for (int i = 0; i <thrs.size(); i++) {
			DataFrame<Object> partialDF = thrs.get(i).getdF();
			//partialDF.show();
			for (int j = 0; j < partialDF.length(); j++) {
			
				//System.out.println(partialDF.row(j));
				completeDM.append(partialDF.row(j));
				
			}
			
		}
		timerFine=System.currentTimeMillis();
		System.out.println("Tempo totale: "+(timerFine-timerInizio));
		System.out.println(completeDM.toString());
		completeDM.show();
		

		
	}
}



