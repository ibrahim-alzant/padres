package ca.utoronto.msrg.padres.common.comm.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import ca.utoronto.msrg.padres.common.comm.CommunicationException;
import ca.utoronto.msrg.padres.common.comm.socket.message.PubSubMessage;
import ca.utoronto.msrg.padres.common.comm.socket.message.SocketMessage;
import ca.utoronto.msrg.padres.common.comm.socket.message.SocketMessage.SocketMessageType;
import ca.utoronto.msrg.padres.common.message.Message;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	private static ChannelGroup channels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	private final NioMessageSender parent;
	
	public ClientHandler(NioMessageSender parent){
		this.parent = parent;
	}

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {        
       Channel connection = ctx.channel();   
       channels.remove(connection);     
       ctx.close();
    }
    
    
    
    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws CommunicationException { // (2)
    	SocketMessage socketMessage = (SocketMessage)msg;
    	
    	if ((socketMessage.getMessageType().equals(SocketMessageType.PUB_SUB))){			
			PubSubMessage pubSubSocketMsg = (PubSubMessage) socketMessage;
			Message pubSubMsg = pubSubSocketMsg.getMessage();
			parent.notifyMessageListeners(pubSubMsg, pubSubSocketMsg.getHostType());	
		} else {
			//throw new CommunicationException("Unsupported message type");
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
        ctx.close();
	}
}

