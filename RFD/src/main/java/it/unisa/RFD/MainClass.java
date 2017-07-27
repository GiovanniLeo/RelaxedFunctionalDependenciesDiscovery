package it.unisa.RFD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import joinery.DataFrame;

/**
 * 
 *
 */
public class MainClass 
{
    public static void main( String[] args ) throws IOException
    {
    
    		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    		String nameCSV,separatorCSV,headingCSV,nullCharacterCSV;
    		DataFrame<Object> df;
    
    	
  
    	
    	
    	
        System.out.println("Name of CSV file");
        nameCSV=console.readLine();
        System.out.println("The separator character");                                       
        separatorCSV=console.readLine();
        System.out.println("Y or N if there is the heading");
        headingCSV=console.readLine();
        System.out.println("The null character");
        nullCharacterCSV=console.readLine();
        
        if(headingCSV.equalsIgnoreCase("y"))
            df = DistanceMatrix.loadDF(nameCSV,separatorCSV,nullCharacterCSV,true);
        else
        		df = DistanceMatrix.loadDF(nameCSV,separatorCSV,nullCharacterCSV,false);
     	    
        
        //DistanceMatrix.createMatrix(df);
     
       
    }
}
