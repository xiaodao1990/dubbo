package com.jenkin.dubbo.netty.server;


import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {

    public static ChannelGroup channelGroup = new DefaultChannelGroup();
    private ChannelFactory factory;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        /**
         * NioServerSocketChannelFactory用于创建基于NIO的服务端,ServerSocketChannel。本身包含2种线程，boss线程和worker线程。
         * 每个ServerSocketChannel会都会拥有自己的boss线程,当一个连接被服务端接受（accepted），boss线程就会将接收到的Channel
         * 传递给一个worker线程处理，而worker线程以非阻塞的方式为一个或多个Channel提供非阻塞的读写
         */
        factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), // boss线程池
                Executors.newCachedThreadPool(), // worker线程池
                8);// worker线程数

        // ServerBootstrap用于帮助服务器启动
        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        // 没有child.前缀，则该选项是为ServerSocketChannel设置
        bootstrap.setOption("reuseAddress", true);
        /**
         * 对每一个连接（channel），server都会调用ChannelPipelineFactory为该连接创建一个ChannelPipeline
         */
        ServerChannelPiplineFactory channelPiplineFactory = new ServerChannelPiplineFactory();
        bootstrap.setPipelineFactory(channelPiplineFactory);

        // 这里绑定服务端监听的IP和端口 bind：服务端方法
        Channel channel = bootstrap.bind(new InetSocketAddress("127.0.0.1", 8091));
        Server.channelGroup.add(channel);

        System.out.println("Server is started");
    }

    public void stop() {
        ChannelGroupFuture channelGroupFuture = Server.channelGroup.close();
        channelGroupFuture.awaitUninterruptibly();
        factory.releaseExternalResources();
        System.out.println("Server is stoped");
    }

}
