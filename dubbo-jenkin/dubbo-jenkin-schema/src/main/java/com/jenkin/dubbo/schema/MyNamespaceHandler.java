package com.jenkin.dubbo.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MyNamespaceHandler extends NamespaceHandlerSupport {


    @Override
    public void init() {
        registerBeanDefinitionParser("people", new MyBeanDefinitionParser());
    }
}
