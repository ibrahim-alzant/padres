package ca.utoronto.msrg.padres.common.comm.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import ca.utoronto.msrg.padres.common.comm.CommSystem;
import ca.utoronto.msrg.padres.common.comm.CommunicationException;
import ca.utoronto.msrg.padres.common.comm.HostType;
import ca.utoronto.msrg.padres.common.comm.MessageListenerInterface;
import ca.utoronto.msrg.padres.common.comm.MessageSender;
import ca.utoronto.msrg.padres.common.comm.NodeAddress;
import ca.utoronto.msrg.padres.common.comm.socket.message.ConnectMessage;
import ca.utoronto.msrg.padres.common.comm.socket.message.PubSubMessage;
import ca.utoronto.msrg.padres.common.message.Message;
import ca.utoronto.msrg.padres.common.message.MessageDestination;


public class NioMessageSender extends MessageSender {


	private Channel channel;
	private static EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap;
	private final HostType hostType;
	private final CommSystem parent;
	private MessageListenerInterface clientListener;
	
	public NioMessageSender(CommSystem parent,NodeAddress remoteAddress,HostType hostType) {
		super(remoteAddress);
		this.hostType = hostType;
		this.parent = parent;
        this.bootstrap = new Bootstrap()
        	.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ClientInitializer(this));         
	}
	
	public NioMessageSender(CommSystem parent,NodeAddress remoteAddress, Channel channel) {
		super(remoteAddress);
		this.parent = parent;
		this.hostType = HostType.SERVER;
		this.channel = channel;
		this.connected = true;
	}

	@Override
	public void connect() throws CommunicationException {
			try {
				if(!connected){
					this.channel = this.bootstrap.connect(remoteServerAddress.getHost(), remoteServerAddress.getPort()).sync().channel();
					super.connected = true;
				}
				channel.writeAndFlush(new ConnectMessage(this.hostType, null));
			} catch (InterruptedException e) {
				// TODO logger
			}	
	}


	@Override
	protected String sendTo(Message msg, HostType sendingHostType)
			throws CommunicationException {
		channel.writeAndFlush(new PubSubMessage(msg, sendingHostType));		
		return msg.getMessageID();
	}

	@Override
	public void disconnect(MessageDestination sourceDest)
			throws CommunicationException {
		channel.disconnect();
		this.connected = false;
	}

	@Override
	public String getID() throws CommunicationException {
		return this.remoteServerAddress.getNodeURI();
	}
	
	public CommSystem getParentServer(){
		return this.parent;
	}

	@Override
	public void connect(MessageDestination sourceDest,
			MessageListenerInterface msgListener) throws CommunicationException {
		try {
			this.channel = this.bootstrap.connect(remoteServerAddress.getHost(), remoteServerAddress.getPort()).sync().channel();
			super.connected = true;
			this.clientListener = msgListener;
			this.channel.writeAndFlush(new ConnectMessage(HostType.CLIENT, sourceDest));
		} catch (InterruptedException e) {
			//TODO logger
		}
	}
	
	public void notifyMessageListeners(Message msg, HostType hostType){
		if(this.hostType.equals(HostType.CLIENT)){
			this.clientListener.notifyMessage(msg, hostType);
		} else {
			for (MessageListenerInterface listener : parent.getServer().getMessageListeners())
				listener.notifyMessage(msg, hostType);
		}
	}
	
}
