package ca.utoronto.msrg.padres.common.comm.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.utoronto.msrg.padres.common.comm.HostType;
import ca.utoronto.msrg.padres.common.comm.socket.message.ConnectMessage;
import ca.utoronto.msrg.padres.common.comm.socket.message.PubSubMessage;
import ca.utoronto.msrg.padres.common.comm.socket.message.PubSubReplyMessage;
import ca.utoronto.msrg.padres.common.comm.socket.message.SocketMessage;
import ca.utoronto.msrg.padres.common.comm.socket.message.SocketMessage.SocketMessageType;
import ca.utoronto.msrg.padres.common.message.Message;
import ca.utoronto.msrg.padres.common.message.MessageDestination;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	private ChannelGroup channels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	private HashMap<Channel,MessageDestination> channelDestinations = new HashMap<>();
	private List<Channel> connectedClients = new ArrayList<>();
	private List<Channel> connectedBrokers = new ArrayList<>();
	
	private final NioListener parent;
	
	public ServerHandler(NioListener parent){
		this.parent = parent;
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        Channel incomming = ctx.channel();       
        channels.add(incomming);
        String address = incomming.localAddress().toString();
        this.parent.getAddressChannels().put(address, incomming);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {        
        Channel connection = ctx.channel();   
        channels.remove(connection);     
        parent.getParentServer().disconnect(channelDestinations.get(connection));
    }
    
    
    
    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
    	SocketMessage socketMessage = (SocketMessage)msg;
    	
    	if(socketMessage.getMessageType().equals(SocketMessageType.CONNECT)){
    		if	(((ConnectMessage)socketMessage).getSourceType() == HostType.CLIENT){
    			//if the connection is from client, then notify about it
        		parent.getParentServer().notifyConnectionListeners(((ConnectMessage)socketMessage).getSourceDestination());
        		this.channelDestinations.put(ctx.channel(), ((ConnectMessage)socketMessage).getSourceDestination()); 
        		connectedClients.add(ctx.channel());        		
    		}else {
    			connectedBrokers.add(ctx.channel());
    		}
    	}  else if (socketMessage.getMessageType().equals(SocketMessageType.PUB_SUB)){
			PubSubMessage pubSubSocketMsg = (PubSubMessage) socketMessage;
			Message pubSubMsg = pubSubSocketMsg.getMessage();
			parent.getParentServer().notifyMessageListeners(pubSubMsg, pubSubSocketMsg.getHostType());
			if(connectedClients.contains(ctx.channel())){
				ctx.channel().writeAndFlush(new PubSubReplyMessage(pubSubMsg.getMessageID()));
			}			
			
		}						
	}
        
        

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {             
            ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		// Close the connection when an exception is raised.
		Channel connection = ctx.channel();   
        channels.remove(connection);     
        parent.getParentServer().disconnect(channelDestinations.get(connection));
        ctx.close();
	}
}

