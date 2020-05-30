package com.jenkin.dubbo.spi;

import java.util.ServiceLoader;

/**
 * SPI设计目标：
 *  面向对象的设计里，模块之间是基于接口编程，模块之间不对实现类进行硬编码。
 *  一旦代码中涉及具体的实现类，就违反了可插拔原则，如果需要替换一种实现，就需要修改代码。
 *  为了实现在模块装配的时候，不在模块里面写死代码，就需要一种发现机制。
 *  java spi就提供了这样的一种机制。
 *  为某一个接口寻找服务实现的机制，有点类似IOC的思想，就是将装配的控制权转移到代码之外。
 *
 *  SPI具体约定如下：
 *      当服务的提供者(provider),提供了一个接口多种实现时，一般会在jar包的
 *      META-INF/services/目录下，创建该接口的同名文件。该文件里面的内容就是该服务接口具体实现类
 *      的名称。而当外部加载这个模块时，就能通过jar包下META-INF/services/目录下的配置文件得到具体
 *      的实现类名，并加载实例化，完成模块的装配。
 */
public class Main {

    public static void main(String[] args) {
        ServiceLoader<Command> commands = ServiceLoader.load(Command.class);

        for (Command command : commands) {
            command.execute();
        }
    }
}
