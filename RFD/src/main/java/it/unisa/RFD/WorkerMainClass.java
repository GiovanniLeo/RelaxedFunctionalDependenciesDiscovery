package it.unisa.RFD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


import akka.actor.ActorSystem;

public class WorkerMainClass
{

	public static void main(String[] args) throws IOException 
	{
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		
				Config config = ConfigFactory.parseString(
						"akka.remote.netty.tcp.port=" + 2551).withFallback(
								ConfigFactory.load());
				
				ActorSystem system = ActorSystem.create("SistemaAttoriRDFCluster",config);
				
				System.out.println(">>> Press ENTER to continue <<<");
			    try 
			    {
					console.readLine();
					
				} catch (IOException e)
			    {
					e.printStackTrace();
				}
			    finally
			    {
			    	system.terminate();
					console.close();
			    }

	}

}
