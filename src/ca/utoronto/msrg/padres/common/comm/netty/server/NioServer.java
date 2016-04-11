package ca.utoronto.msrg.padres.common.comm.netty.server;

import io.netty.channel.Channel;
import ca.utoronto.msrg.padres.common.comm.CommServer;
import ca.utoronto.msrg.padres.common.comm.CommSystem;
import ca.utoronto.msrg.padres.common.comm.CommunicationException;
import ca.utoronto.msrg.padres.common.comm.ConnectionListenerInterface;
import ca.utoronto.msrg.padres.common.comm.HostType;
import ca.utoronto.msrg.padres.common.comm.MessageListenerInterface;
import ca.utoronto.msrg.padres.common.comm.MessageSender;
import ca.utoronto.msrg.padres.common.comm.NodeAddress;
import ca.utoronto.msrg.padres.common.comm.netty.client.NioMessageSender;
import ca.utoronto.msrg.padres.common.comm.netty.common.NioAddress;
import ca.utoronto.msrg.padres.common.message.Message;
import ca.utoronto.msrg.padres.common.message.MessageDestination;

public class NioServer extends CommServer {

	protected NioListener connectionListener;	
	private CommSystem parent;
	
	public NioServer(NioAddress serverAddress, CommSystem parent) throws CommunicationException {
		super(serverAddress);
		this.parent = parent;
		// Start listening for clients
		connectionListener = new NioListener(serverAddress.getPort(), this);
		connectionListener.start();
	}

	@Override
	public void shutDown() throws CommunicationException {
		connectionListener.shutdown();
	}
	
	/**
	 * notify all registered message listeners
	 */
	public void notifyMessageListeners(Message msg, HostType hostType) {		
			for (MessageListenerInterface listener : super.getMessageListeners())
				listener.notifyMessage(msg, hostType);
	} 
	

	/**
	 * Alerts all connection listeners that a connection has been received.
	 * 
	 * @param connectedDest
	 *            The destination that we just received a connection from.
	 * 
	 * @see: Precondition
	 * 
	 * @precondition: this method is only called if we are on a server and we receive a connection
	 *                from a client. Equivalently, if (serverConnection && !connectedToServer)
	 */
	public void notifyConnectionListeners(MessageDestination connectedDest) {
		for (ConnectionListenerInterface cl : super.getConnectionListeners()) {
			// Alert the connection listener
			String destId = connectedDest.getDestinationID();
			String address = "";
			try {
				address = "/" + NodeAddress.getAddress(destId).getHost() + ":" + NodeAddress.getAddress(destId).getPort();
			} catch (CommunicationException e) {
				
			}
			Channel channel = connectionListener.getAddressChannels().get(address);
			MessageSender msgSender = new NioMessageSender(this.parent,super.serverAddress,channel);
			cl.connectionMade(connectedDest, msgSender);
		}
	}
	
	/**
	 * Inform the connection listener of disconnection.
	 */
	//TODO make it one method that notifies for connection made and connection broke
	public void disconnect(MessageDestination connectedDest) {
		for (ConnectionListenerInterface cl : super.getConnectionListeners())
			cl.connectionBroke(connectedDest);
	}
	
	

	

}
