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
		test.startBrokerC();
		test.startBrokerD();
		test.startBrokerE();
		test.startBrokerF();
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
	public void startBrokerC() throws BrokerCoreException{
		BrokerCore brokerB = new BrokerCore(this.getBrokerCConfiguration());
		brokerB.setDebugMode(true);
		brokerB.initialize();
	}
	public void startBrokerD() throws BrokerCoreException{
		BrokerCore brokerB = new BrokerCore(this.getBrokerDConfiguration());
		brokerB.setDebugMode(true);
		brokerB.initialize();
	}
	public void startBrokerE() throws BrokerCoreException{
		BrokerCore brokerB = new BrokerCore(this.getBrokerEConfiguration());
		brokerB.setDebugMode(true);
		brokerB.initialize();
	}
	public void startBrokerF() throws BrokerCoreException{
		BrokerCore brokerB = new BrokerCore(this.getBrokerFConfiguration());
		brokerB.setDebugMode(true);
		brokerB.initialize();
	}
	
	private BrokerConfig getBrokerAConfiguration() throws BrokerCoreException{
		BrokerConfig config = new BrokerConfig();
		config.setHeartBeat(true);
		config.setConnectionRetryLimit(1);
		config.setConnectionRetryPause(1);
		config.setAdvCovering(AdvCoveringType.OFF);
        config.setBrokerURI("nio://localhost:1100/BrokerA");
        config.setNeighborURIs(new String[]{"nio://localhost:1101/BrokerB"});
		
		return config;
	}
	
	
	private BrokerConfig getBrokerBConfiguration() throws BrokerCoreException{
		BrokerConfig config = new BrokerConfig();
		config.setHeartBeat(true);
		config.setConnectionRetryLimit(1);
		config.setConnectionRetryPause(1);
		config.setAdvCovering(AdvCoveringType.OFF);
        config.setBrokerURI("nio://localhost:1101/BrokerB");
        config.setNeighborURIs(new String[]{"nio://localhost:1102/BrokerC"}); 
		return config;
	}
	
	private BrokerConfig getBrokerCConfiguration() throws BrokerCoreException{
		BrokerConfig config = new BrokerConfig();
		config.setHeartBeat(true);
		config.setConnectionRetryLimit(1);
		config.setConnectionRetryPause(1);
		config.setAdvCovering(AdvCoveringType.OFF);
        config.setBrokerURI("nio://localhost:1102/BrokerC");
        config.setNeighborURIs(new String[]{"nio://localhost:1103/BrokerD"});
		return config;
	}
	
	private BrokerConfig getBrokerDConfiguration() throws BrokerCoreException{
		BrokerConfig config = new BrokerConfig();
		config.setHeartBeat(true);
		config.setConnectionRetryLimit(1);
		config.setConnectionRetryPause(1);
		config.setAdvCovering(AdvCoveringType.OFF);
        config.setBrokerURI("nio://localhost:1103/BrokerD");
        config.setNeighborURIs(new String[]{"nio://localhost:1104/BrokerE"});
		return config;
	}
	
	private BrokerConfig getBrokerEConfiguration() throws BrokerCoreException{
		BrokerConfig config = new BrokerConfig();
		config.setHeartBeat(true);
		config.setConnectionRetryLimit(1);
		config.setConnectionRetryPause(1);
		config.setAdvCovering(AdvCoveringType.OFF);
        config.setBrokerURI("nio://localhost:1104/BrokerE");
        config.setNeighborURIs(new String[]{"nio://localhost:1105/BrokerF"});
		return config;
	}
	
	private BrokerConfig getBrokerFConfiguration() throws BrokerCoreException{
		BrokerConfig config = new BrokerConfig();
		config.setHeartBeat(true);
		config.setConnectionRetryLimit(1);
		config.setConnectionRetryPause(1);
		config.setAdvCovering(AdvCoveringType.OFF);
        config.setBrokerURI("nio://localhost:1105/BrokerF");        
		return config;
	}
	

}