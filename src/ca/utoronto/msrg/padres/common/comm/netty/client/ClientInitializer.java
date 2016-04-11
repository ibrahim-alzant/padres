package ca.utoronto.msrg.padres.common.comm.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel>{

	private NioMessageSender parent;
	
	public ClientInitializer(NioMessageSender parent) {
		this.parent = parent;
	}
	
	public NioMessageSender getParent(){
		return this.parent;
	}

	@Override
	protected void initChannel(SocketChannel c) throws Exception {
		ChannelPipeline pipeline = c.pipeline();
		pipeline.addLast("decoder",	new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast("encoder", new ObjectEncoder());
		pipeline.addLast(new ClientHandler(parent));
	}
}
