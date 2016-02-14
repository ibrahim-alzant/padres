// =============================================================================
// This file is part of The PADRES Project.
//
// For more information, see http://www.msrg.utoronto.ca
//
// Copyright (c) 2003 Middleware Systems Research Group, University of Toronto
// =============================================================================
// $Id$
// =============================================================================
/*
 * Created on 21-Jul-2003
 */
package ca.utoronto.msrg.padres.broker.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.utoronto.msrg.padres.broker.brokercore.OutputQueueHandler;
import ca.utoronto.msrg.padres.common.message.MessageDestination;
import ca.utoronto.msrg.padres.common.message.MessageDestination.DestinationType;

/**
 * The Overlay Routing Table (ORT).
 * 
 * Uses Singleton pattern.
 * 
 * @author eli
 */
public class OverlayRoutingTable {

	private Map<MessageDestination, LinkInfo> statisticTable;

	private Map<MessageDestination, OutputQueueHandler> brokerQueues;

	private Map<MessageDestination, OutputQueueHandler> clientQueues;

	public OverlayRoutingTable() {
		statisticTable = Collections.synchronizedMap(new HashMap<MessageDestination, LinkInfo>());
		brokerQueues = Collections.synchronizedMap(new HashMap<MessageDestination, OutputQueueHandler>());
		clientQueues = Collections.synchronizedMap(new HashMap<MessageDestination, OutputQueueHandler>());
	}

	public synchronized Map<MessageDestination, OutputQueueHandler> getBrokerQueues() {
		return brokerQueues;
	}
	
	public synchronized Map<MessageDestination, OutputQueueHandler> getClientQueues() {
		return clientQueues;
	}
	
	public int getNoOfNeighborBrokers() {
		return brokerQueues.size();
	}

	public int getNoOfClients() {
		return clientQueues.size();
	}

	public synchronized Map<MessageDestination, LinkInfo> getStatisticTable() {
		return statisticTable;
	}

	public synchronized void addBroker(OutputQueueHandler neighbourQueue) {
		statisticTable.put(neighbourQueue.getDestination(), new LinkInfo());
		brokerQueues.put(neighbourQueue.getDestination(), neighbourQueue);
	}

	public synchronized void removeBroker(MessageDestination neighbour) {
		OutputQueueHandler queue = brokerQueues.remove(neighbour);
		if(queue != null)
			queue.shutdown();
		statisticTable.remove(neighbour);
	}

	public synchronized OutputQueueHandler getOutputQueue(MessageDestination remoteDest) {
		return brokerQueues.get(remoteDest);
	}

	public synchronized boolean isNeighbor(MessageDestination neighbour) {
		return brokerQueues.containsKey(neighbour);
	}

	public synchronized boolean isNeighbor(String neighborID) {
		return brokerQueues.containsKey(new MessageDestination(neighborID, DestinationType.BROKER));
	}

	public synchronized boolean isClient(MessageDestination clientDest) {
		return clientQueues.containsKey(clientDest);
	}

	public synchronized boolean isClient(String clientID) {
		return clientQueues.containsKey(new MessageDestination(clientID, DestinationType.CLIENT));
	}

	public synchronized Set<MessageDestination> getClients() {
		return clientQueues.keySet();
	}

	public synchronized int getClientCount() {
		return clientQueues.size();
	}

	public synchronized OutputQueueHandler addClient(OutputQueueHandler newClientQueue) {
		clientQueues.put(newClientQueue.getDestination(), newClientQueue);
		return newClientQueue;
	}

	public synchronized void removeClient(MessageDestination clientDest) {
		OutputQueueHandler queue = clientQueues.remove(clientDest);
		
		if(queue != null)
			queue.shutdown();
	}

}
