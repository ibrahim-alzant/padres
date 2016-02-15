package ca.utoronto.msrg.padres.broker.brokercore;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class test {
	
	public static void main(String args[]){
		//BasicConfigurator.configure();
		System.out.println(Logger.getRootLogger().getLevel());
		Logger.getRootLogger().info("bob");
	}

}
