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
                            -->invoker.invoke(invocation);
                                -->InvokerWrapper.invoke(Invocation invocation);
                                    -->ProtocolFilterWrapper.invoke(invocation);// invoker=ProtocolFilterWrapper
                                        -->ConsumerContextFilter.invoke(next, invocation);// filter=ConsumerContextFilter,next=ProtocolFilterWrapper
                                            -->ProtocolFilterWrapper.invoke(invocation);
                                                -->FutureFilter.invoke(next, invocation);// next=ProtocolFilterWrapper
                                                    -->ProtocolFilterWrapper.invoke(invocation);
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
