package ca.utoronto.msrg.padres.broker.brokercore;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ca.utoronto.msrg.padres.broker.brokercore.BrokerConfig;
import ca.utoronto.msrg.padres.broker.brokercore.BrokerCore;
import ca.utoronto.msrg.padres.broker.brokercore.BrokerCoreException;
import ca.utoronto.msrg.padres.broker.router.AdvertisementFilter.AdvCoveringType;
import ca.utoronto.msrg.padres.common.util.LogSetup;

public class TestBrokers {
	
	public static void main (String ...args) throws BrokerCoreException{
		BasicConfigurator.configure();
		
		TestBrokers test = new TestBrokers();
		test.startBrokerA();
		test.startBrokerB();
	}
	
	public void startBrokerA() throws BrokerCoreException{
		BrokerCore brokerA = new BrokerCore(this.getBrokerAConfiguration());
		brokerA.setDebugMode(true);
		brokerA.initialize();		
	}
	public void startBrokerB() throws BrokerCoreException{
		BrokerCore brokerB = new BrokerCore(this.getBrokerBConfiguration());
		brokerB.setDebugMode(true);
		brokerB.initialize();
	}
	private BrokerConfig getBrokerAConfiguration() throws BrokerCoreException{
		BrokerConfig config = new BrokerConfig();
		config.setHeartBeat(true);
		config.setConnectionRetryLimit(1);
		config.setConnectionRetryPause(1);
		config.setAdvCovering(AdvCoveringType.OFF);
        config.setBrokerURI("socket://localhost:1100/BrokerA");
        config.setNeighborURIs(new String[]{"socket://localhost:1101/BrokerB"});
		
		return config;
	}
	
	
	private BrokerConfig getBrokerBConfiguration() throws BrokerCoreException{
		BrokerConfig config = new BrokerConfig();
		config.setHeartBeat(true);
		config.setConnectionRetryLimit(1);
		config.setConnectionRetryPause(1);
		config.setAdvCovering(AdvCoveringType.OFF);
        config.setBrokerURI("socket://localhost:1101/BrokerB");
        config.setNeighborURIs(new String[]{"socket://localhost:1100/BrokerA"});
		return config;
	}
	

}
