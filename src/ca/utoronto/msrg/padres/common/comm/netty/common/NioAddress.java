package ca.utoronto.msrg.padres.common.comm.netty.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;

import ca.utoronto.msrg.padres.common.comm.CommunicationException;
import ca.utoronto.msrg.padres.common.comm.NodeAddress;
import ca.utoronto.msrg.padres.common.comm.CommSystem.CommSystemType;

public class NioAddress extends NodeAddress {

	public static final String NIO_REG_EXP = "nio://([^:/]+)(:(\\d+))?/(.+)";
	
	public NioAddress(String nodeURI) throws CommunicationException {
		super(nodeURI);
		type = CommSystemType.NIO;
	}

	@Override
	protected void parseURI(String nodeURI) throws CommunicationException {
		// set the default values
		host = "localhost";
		port = 1099;
		remoteID = null;
		// get the actual values from the input string
		Matcher socketMatcher = getMatch(nodeURI);
		if (socketMatcher.find()) {
			host = socketMatcher.group(1);
			if (socketMatcher.group(3) != null) {
				port = Integer.parseInt(socketMatcher.group(3));
			}
			remoteID = socketMatcher.group(4);
		} else {
			throw new CommunicationException("Malformed remote broker socket URI: " + nodeURI);
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public String getNodeID() {
		return remoteID;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NioAddress))
			return false;
		NioAddress tempAddr = (NioAddress) o;
		if (tempAddr.host.equals(host) && tempAddr.port == port)
			return true;
		return false;
	}

	@Override
	public boolean isEqual(String checkURI) throws CommunicationException {
		try {
			NioAddress checkAddr = new NioAddress(checkURI);
			InetAddress checkHost = InetAddress.getByName(checkAddr.host);
			checkURI = String.format("nio://%s:%d/%s", checkHost.getHostAddress(),
					checkAddr.port, checkAddr.remoteID);
			InetAddress thisHost = InetAddress.getByName(checkAddr.host);
			String thisURI = String.format("nio://%s:%d/%s", thisHost.getHostAddress(), port,
					remoteID);
			return thisURI.equalsIgnoreCase(checkURI);
		} catch (UnknownHostException e) {
			throw new CommunicationException(e);
		}
	}

	public String toString() {
		return String.format("nio://%s:%d/%s", host, port, remoteID);
	}
}