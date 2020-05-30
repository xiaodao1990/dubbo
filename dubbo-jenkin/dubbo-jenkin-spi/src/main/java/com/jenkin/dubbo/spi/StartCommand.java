package com.jenkin.dubbo.spi;

public class StartCommand implements Command {
    @Override
    public void execute() {
        System.out.println("start-------------");
    }
}
