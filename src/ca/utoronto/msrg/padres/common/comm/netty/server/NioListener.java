package ca.utoronto.msrg.padres.common.comm.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

public class NioListener extends Thread {
	
	private final int port;
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final NioServer parent;
    private Channel channel;
    private ServerBootstrap bootstrap;
    private HashMap<String,Channel> addressChannels = new HashMap<>();
    
	public NioListener(int port, NioServer parent){
		this.port = port;
		this.parent = parent;
	}
	
	@Override
	public void run(){		
        try{
            bootstrap = new ServerBootstrap()
                    .group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                    .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
                    .childHandler(new ServerInitializer(this));
            // Bind and start to accept incoming connections.
            ChannelFuture f = bootstrap.bind(port).sync(); // (7)
            this.channel = f.channel();
        } catch (InterruptedException ex) {
            //TODO LOGGER
        	ex.printStackTrace();
        }finally{
        	this.shutdown();
        }
	}
	
	public void shutdown(){
		try {
			this.channel.closeFuture().sync();
			this.workerGroup.shutdownGracefully();
	        this.bossGroup.shutdownGracefully();
		} catch (InterruptedException e) {
			//TODO LOGGER
			e.printStackTrace();
		}
	}
	
	public NioServer getParentServer(){
		return parent;
	}	
	
	public Map<String,Channel> getAddressChannels (){
		return this.addressChannels;
	}
	
}
