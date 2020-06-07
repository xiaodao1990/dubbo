#### Provider对消息的接收与发送原理解析
```text
NettyHandler.messageReceived(ChannelHandlerContext ctx, MessageEvent e)    
    -->handler.received(channel, e.getMessage());// [id: 0x4ed07a9f, /192.168.2.103:65307 => /192.168.2.103:20880]
        -->AbstractPeer.received(Channel ch, Object msg);
            -->MultiMessageHandler.received(Channel channel, Object message)
                -->HeartbeatHandler.received(Channel channel, Object message)
                    -->AllChannelHandler.received(Channel channel, Object message)
                        -->cexecutor.execute(new ChannelEventRunnable(channel, handler, ChannelState.RECEIVED, message));// 线程池 执行线程
                            -->DecodeHandler.received(Channel channel, Object message)
                                -->HeaderExchangeHandler.received(Channel channel, Object message)
                                    -->Response response = handleRequest(exchangeChannel, request);// 网络通信接收处理
                                        -->DubboProtocol.reply(ExchangeChannel channel, Object message)
                                            -->getInvoker(channel, inv);// DubboProtocol
                                                // 从远程服务暴露的exporterMap提取。serviceKey=com.alibaba.dubbo.demo.DemoService:20880
                                                -->DubboExporter<?> exporter = (DubboExporter<?>) exporterMap.get(serviceKey);
                                                -->DubboExporter.getInvoker()//最终得到一个invoker
                                            -->ProtocolFilterWrapper.invoke(next, invocation);
                                                -->EchoFilter.invoke(Invoker<?> invoker, Invocation inv);
                                                    -->ClassLoaderFilter.invoke(next, invocation);
                                                        -->GenericFilter.invoke(next, invocation);
                                                            -->ContextFilter.invoke(next, invocation);
                                                                -->TraceFilter.invoke(next, invocation);
                                                                    -->TimeoutFilter.invoke(next, invocation);
                                                                        -->MonitorFilter.invoke(next, invocation);
                                                                            -->ExceptionFilter.invoke(next, invocation);
                                                                                -->InvokerWrapper.invoke(Invocation invocation)
                                                                                    -->AbstractProxyInvoker.invoke(Invocation invocation)
                                                                                        -->JavassistProxyFactory.AbstractProxyInvoker.doInvoke
                                                                                            // proxy=DemoServiceImpl代理类，methodName=sayHello，parameterTypes=String，arguments=world2097
                                                                                            -->wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
                                                                                                // 进入真正执行的实现类
                                                                                                -->DemoServiceImpl.sayHello(String name)
                                    -->channel.send(response);// 把接收处理的结果，发送回去
                                        -->AbstractPeer.send(Object message)
                                            -->ChannelFuture future = channel.write(message);// NioAcceptedSocketChannel
                                        
```