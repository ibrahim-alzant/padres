package ca.utoronto.msrg.padres.broker.router;

import ca.utoronto.msrg.padres.common.message.SubscriptionMessage;

public class SenderTagger {

	public static void processMessage(SubscriptionMessage msg) {
		if(!msg.getLastHopID().isBroker()){ // This is an edge broker
			msg.setSender(msg.getLastHopID());
		}
	}

}
