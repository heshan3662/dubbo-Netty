package com.dubbo;

import com.dubbo.proxy.RpcClientProxy;
import com.dubbo.proxy.RpcProxyHandler;
import com.dubbo.registry.IServiceDiscovery;
import com.dubbo.registry.ServiceDiscoveryImpl;
import sun.applet.Main;

import java.util.HashMap;
import java.util.Map;

public class ClientDemo{
    public static void main(String[] args) {
          IServiceDiscovery serviceDiscoveryser = new ServiceDiscoveryImpl();
//        String url = serviceDiscoveryser.discover("com.dubbo.ICsdn");
        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscoveryser);
        ICsdn icsdn = rpcClientProxy.create(ICsdn.class) ;
        System.out.println(icsdn.sayHello("java"));

        String  a  = icsdn.sayHello("hello java");
        System.out.println(a);
    }
}