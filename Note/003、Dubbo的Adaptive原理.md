#### Adaptive注解在类和方法上的区别？
```text
1、注解在类上：代表由人工实现编码，即实现一个装饰类(设计模式中的装饰模式)。Adaptive 注解在类上的情况很少，
在Dubbo中，仅有两个类被Adaptive注解了，分别是AdaptiveCompiler和AdaptiveExtensionFactory。
2、注解在方法上：代表自动生成和编译一个动态的adaptive类。例如：Protocol$Adaptive.
```

#### ExtensionLoader.getAdaptiveExtension()源码解析
```text
getAdaptiveExtension()// 为cachedAdaptiveInstance赋值
    -->createAdaptiveExtension();
        -->getAdaptiveExtensionClass()
            -->getExtensionClasses();
                -->loadExtensionClasses();
                    -->loadFile[META-INF/dubbo/internal/]
            -->createAdaptiveExtensionClass();// 自动生成和编译一个动态的adpative类，这个类是一个代理类
                -->createAdaptiveExtensionClassCode();
```

#### 关于loadFile的一些细节
```text
目的：通过读取配置文件META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol中的内容，存储在缓存变量中
cachedAdaptiveClass// 如果该类上含有adative注解就赋值，例如AdaptiveExtensionFactory，而例如Protocol在这个环节是没有的。
cachedWrapperClasses// 只有该类上没有adative注解，并且构造函数中包含目标接口的类型。
    例如protocol里面的spi就只有ProtocolFilterWrapper和ProtocolListenerWrapper能命中
cachedActivates// 剩余的类，包含Activate注解，缓存进cachedActivates
cachedNames// 除前面缓存过的类，其余类都缓存在这里
```