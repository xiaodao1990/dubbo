#### 1、Dubbo服务请求(Java动态代理参见:https://www.liaoxuefeng.com/wiki/1252599548343744/1264804593397984)
动态代理实际上是JDK在运行期动态创建class字节码并加载的过程
```text
demoService.sayHello("world" + i);// demoService为DemoService的动态代理对象，DemoService.sayHello-->InvokerInvocationHandler.invoke()
    -->InvokerInvocationHandler.invoke(Object proxy, Method method, Object[] args)
        // 所有请求参数都会转换为RpcInvocation
        -->new RpcInvocation(method, args);// method[sayHello], args[java.lang.String] this.methodName=sayHello, this.parameterTypes=String
        -->invoker.invoke(new RpcInvocation(method, args));
-------------------------------------------------------1.进入集群-------------------------------------------------------
            -->MockClusterInvoker.invoke
                -->this.invoker.invoke(invocation);
                    -->AbstractClusterInvoker.invoke(final Invocation invocation);
                        -->list(invocation);
-------------------------------------------------------2.进入目录查找---------------------------------------------------
                            -->directory.list(invocation);// directory=RegistryDirectory
                                -->doList(invocation);// 从this.methodInvokerMap里面查找一个Invoker
                                    -->localMethodInvokerMap.get("sayHello");
-------------------------------------------------------3.进入路由-------------------------------------------------------
                                -->router.route(invokers, getConsumerUrl(), invocation);// 进入路由选择 router=MockInvokersSelector
                                    -->MockInvokersSelector.route(final List<Invoker<T>> invokers, URL url, final Invocation invocation)
                                        -->getNormalInvokers(invokers);
                    -->ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("roundrobin");
                    -->doInvoke(invocation, invokers, loadbalance);// loadbalance=RoundRobinLoadBalance
                        -->FailoverClusterInvoker.doInvoke(Invocation invocation, final List<Invoker<T>> invokers, LoadBalance loadbalance)
-------------------------------------------------------4.进入负载均衡---------------------------------------------------
                            -->select(loadbalance, invocation, copyinvokers, invoked);// 进入负载均衡,路由选择
                                -->doselect(loadbalance, invocation, invokers, selected);
                                    -->loadbalance.select(invokers, getUrl(), invocation);
                                        -->AbstractLoadBalance.select(List<Invoker<T>> invokers, URL url, Invocation invocation)
                                            -->doSelect(invokers, url, invocation);
                                                -->RoundRobinLoadBalance.doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation);
                                                    -->invokers.get(currentSequence % length);// 取模轮循
                            -->Result result = invoker.invoke(invocation);
-------------------------------------------------------网络通信-consumer发送原理----------------------------------------
                                -->InvokerWrapper.invoke(Invocation invocation);
                                    -->ProtocolFilterWrapper.invoke(invocation);// invoker=ProtocolFilterWrapper
                                        -->ConsumerContextFilter.invoke(next, invocation);// filter=ConsumerContextFilter,next=ProtocolFilterWrapper
                                            -->FutureFilter.invoke(next, invocation);// next=ProtocolFilterWrapper
                                                -->MonitorFilter.invoke(next, invocation);// next=ListenerInvokerWrapper
                                                    -->ListenerInvokerWrapper.invoke(invocation); 
                                                        -->AbstractInvoker.invoke(invocation);
                                                            -->doInvoke(invocation);
                                                                -->DubboInvoker.doInvoke(final Invocation invocation)
                                                                    -->ReferenceCountExchangeClient.request(inv, timeout);
                                                                        -->HeaderExchangeClient.request(request, timeout);
                                                                            -->HeaderExchangeChannel.request(request, timeout);
                                                                                -->NettyClient.send(req);
                                                                                    -->AbstractPeer.send(Object message);
                                                                                        -->send(message, url.getParameter(Constants.SENT_KEY, false));
                                                                                            -->NettyChannel.send(message, sent);
                                                                                                -->ChannelFuture future = NioClientSocketChannel.write(message);
                                                                    -->DefaultFuture.get();// 最终的目的：通过netty的channel发送网络数据
```

#### 客户端怎样连接到服务端的？
```text
// 见服务引用-原理探索 订阅zk的节点，和服务端发布一样（省略代码）。对节点/dubbo/com.alibaba.dubbo.demo.DemoService/providers，/dubbo/com.alibaba.dubbo.demo.DemoService/configurators，/dubbo/com.alibaba.dubbo.demo.DemoService/routers作监听(订阅)
directory.subscribe(subscribeUrl.addParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY + "," + Constants.CONFIGURATORS_CATEGORY + "," + Constants.ROUTERS_CATEGORY));// 参数=consumer://192.168.43.156/com.alibaba.dubbo.demo.DemoService?application=demo-consumer&category=providers,configurators,routers&check=false&dubbo=2.0.0&interface=com.alibaba.dubbo.demo.DemoService&methods=sayHello&pid=16464&side=consumer&timestamp=1591115228461
    // 此处省略一些步骤...
    -->refreshInvoker(invokerUrls);
        -->toInvokers(invokerUrls);// 将URL列表转成Invoker列表
            -->RegistryDirectory.toInvokers(List<URL> urls);
                -->invoker = new InvokerDelegete<T>(protocol.refer(serviceType, url), url, providerUrl);
                    -->Protocol$Adpative.refer(java.lang.Class arg0, com.alibaba.dubbo.common.URL arg1)
                        -->extension.refer(arg0, arg1);
                            -->ProtocolFilterWrapper.refer(Class<T> type, URL url)
                                -->protocol.refer(type, url)
                                    -->ProtocolListenerWrapper.refer(Class<T> type, URL url)
                                        -->protocol.refer(type, url)
                                            -->DubboProtocol.refer(Class<T> serviceType, URL url)
                                                -->getClients(url);
                                                    -->getSharedClient(url);
                                                        // dubbo://192.168.2.103:20880/com.alibaba.dubbo.demo.DemoService?anyhost=true&application=demo-consumer&check=false&dubbo=2.0.0&generic=false&interface=com.alibaba.dubbo.demo.DemoService&loadbalance=roundrobin&methods=sayHello&monitor=dubbo://192.168.2.103:2181/com.alibaba.dubbo.registry.RegistryService?application=demo-consumer&dubbo=2.0.0&pid=12420&protocol=registry&refer=dubbo=2.0.0&interface=com.alibaba.dubbo.monitor.MonitorService&pid=12420&timestamp=1591530067929&registry=zookeeper&timestamp=1591530067910&owner=william&pid=12420&side=consumer&timestamp=1591530067564
                                                        -->initClient(url);
                                                            -->client = Exchangers.connect(url, requestHandler);
                                                                -->getExchanger(url).connect(url, handler);// getExchanger(url)=HeaderExchanger
                                                                    -->HeaderExchanger.connect(URL url, ExchangeHandler handler)
                                                                        -->new HeaderExchangeClient(Transporters.connect(url, new DecodeHandler(new HeaderExchangeHandler(handler))));
                                                                            -->Transporters.connect(URL url, ChannelHandler... handlers);
                                                                                -->getTransporter().connect(url, handler);// getTransporter()=Transporter$Adpative
                                                                                    -->extension.connect(arg0, arg1);// extension=NettyTransporter
                                                                                        -->new NettyClient(url, listener);
                                                                                            --AbstractPeer(URL url, ChannelHandler handler)// this.url = url; this.handler = handler;
                                                                                            -->AbstractEndpoint(URL url, ChannelHandler handler)// this.codec; this.timeout=1000(请求超时时间); this.connectTimeout=3000(连接超时时间);
                                                                                                -->AbstractClient(URL url, ChannelHandler handler)
                                                                                                    -->doOpen();// 创建连接服务端的NioClientSocketChannelFactory
                                                                                                    protected void doOpen() throws Throwable {
                                                                                                        NettyHelper.setNettyLoggerFactory();
                                                                                                        bootstrap = new ClientBootstrap(channelFactory);
                                                                                                        // config
                                                                                                        // @see org.jboss.netty.channel.socket.SocketChannelConfig
                                                                                                        bootstrap.setOption("keepAlive", true);
                                                                                                        bootstrap.setOption("tcpNoDelay", true);
                                                                                                        bootstrap.setOption("connectTimeoutMillis", getTimeout());
                                                                                                        final NettyHandler nettyHandler = new NettyHandler(getUrl(), this);
                                                                                                        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
                                                                                                            public ChannelPipeline getPipeline() {
                                                                                                                NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NettyClient.this);
                                                                                                                ChannelPipeline pipeline = Channels.pipeline();
                                                                                                                pipeline.addLast("decoder", adapter.getDecoder());
                                                                                                                pipeline.addLast("encoder", adapter.getEncoder());
                                                                                                                pipeline.addLast("handler", nettyHandler);
                                                                                                                return pipeline;
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                    -->connect(); 
                                                                                                        -->doConnect();// NettyClient.doConnect()
                                                                                                            // 目的：连接到服务端 getConnectAddress()=/192.168.2.103:20880
                                                                                                            -->ChannelFuture future = bootstrap.connect(getConnectAddress());
```

#### 灰度发布例子：
```text
 provider  192.168.2.103    192.168.2.105
 1.发布192.168.2.103，切断192.168.2.105访问流量，然后进行服务的发布。
 2.192.168.2.105发布成功后，恢复192.168.2.105的流量，
3.切断192.168.2.103，继续发布192.168.2.103
```

#### 疑问
```text
1.启动路由规则，它触发了那些动作？
  a.什么时候加入ConditionRouter？
    Notify回调刷新RegistryDirectory.notify(categoryList);
    // routers
    if (routerUrls != null && routerUrls.size() > 0) {
        List<Router> routers = toRouters(routerUrls);
        if (routers != null) { // null - do nothing
            setRouters(routers);
        }
    }        
  b.ConditionRouter是怎么过滤的？
    for (Invoker<T> invoker : invokers) {
        if (matchThen(invoker.getUrl(), url)) {
            result.add(invoker);
        }
    }
        -->matchCondition(thenCondition, url, param, null)
            -->最终会将过滤结果设置到methodInvokerMap
2.路由规则有哪些实现类？    
    ConditionRouter：条件路由，后台管理的路由配置都是条件路由。
    ScriptRouter：脚本路由  
```
