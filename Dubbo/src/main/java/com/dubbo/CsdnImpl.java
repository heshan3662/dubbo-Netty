package com.dubbo ;

import com.dubbo.registry.RpcAnnotation;

@RpcAnnotation(ICsdn.class)
public class CsdnImpl implements ICsdn{

    public String sayHello(String msg) {
        return " hello  8888 " + msg +"";
    }
}