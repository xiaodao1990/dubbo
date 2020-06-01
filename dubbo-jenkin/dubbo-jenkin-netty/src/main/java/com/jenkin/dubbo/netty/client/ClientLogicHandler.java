package com.jenkin.dubbo.netty.client;

import org.jboss.netty.channel.*;

public class ClientLogicHandler extends SimpleChannelHandler {

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("### Client channelConnected");
        Channel channel = e.getChannel();
        String request = "Hi, Server! by jenkin";
        channel.write(request);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        System.out.println("### Client messageReceived");
        String message = (String) e.getMessage();
        System.out.println("The message gotten from server is : "+message + " from remote "+e.getRemoteAddress());

        ChannelFuture channelFuture = e.getChannel().close();
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        Channel ch = e.getChannel();
        ch.close();
    }
}
