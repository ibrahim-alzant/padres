package ca.utoronto.msrg.padres.common.comm.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel>{

	private final NioListener parent;
	
	public ServerInitializer(NioListener parent) {
		this.parent = parent;
	}
	
	
	@Override
	protected void initChannel(SocketChannel pipeline) throws Exception {
		pipeline.pipeline().addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        pipeline.pipeline().addLast("encoder", new ObjectEncoder());
        pipeline.pipeline().addLast(new ServerHandler(parent));
		
	}

}
