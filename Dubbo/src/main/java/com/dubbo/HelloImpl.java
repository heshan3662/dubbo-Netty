package com.dubbo ;

import com.dubbo.registry.RpcAnnotation;

@RpcAnnotation(IHello.class)
public class HelloImpl implements IHello{

    public String  Hello(String msg) {
        return " hello 8889 " + msg;
    }
}