package com.jenkin.dubbo.netty.server;

import org.jboss.netty.channel.*;

public class ServerLogicHandler extends SimpleChannelHandler {

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("### Server channelConnected");
        Server.channelGroup.add(e.getChannel());
        System.out.println(e.getChannel().toString());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        // 1、从客户端收到信息
        System.out.println("### Server messageReceived");
        String message = (String) e.getMessage();
        System.out.println("The message sent by client is : " + message + " from remote: "+e.getRemoteAddress());
        // 2、回写客户端信息
        Channel channel = e.getChannel();
        String response = "Hi, Client";
        /**
         * 由于IO操作是异步的，当方法返回时并不能保证IO操作一定完成了
         * 因此返回一个ChannelFuture对象实例,该实例中保存了IO操作的状态信息
         */
        ChannelFuture cf = channel.write(response);
        // 为ChannelFuture对象实例添加监听，如果数据发送完毕则关闭连接
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel ch = channelFuture.getChannel();
                ch.close();
                System.out.println("The message has sent to client. operationComplete");
            }
        });
        System.out.println("The message has sent to client. messageReceived end");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        Channel ch = e.getChannel();
        ch.close();
    }
}
