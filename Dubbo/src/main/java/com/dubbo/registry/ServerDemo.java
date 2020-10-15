package com.dubbo.registry;

import com.dubbo.CsdnImpl;
import com.dubbo.HelloImpl;
import com.dubbo.ICsdn;
import com.dubbo.IHello;

import java.io.IOException;

public  class  ServerDemo{
    public static void main(String[] args) throws IOException {
         ICsdn iCsdn = new CsdnImpl();
        IRegisterCenter registerCenter= new RegisterCenterImpl();
        RpcServer rpcServer = new RpcServer(registerCenter,"127.0.0.1:8888");
        IHello iHello = new HelloImpl();

        rpcServer.bind(iCsdn);
        rpcServer.bind(iHello);
        System.out.println("start ！！！！！！！！！！！！！！！！！！");

        rpcServer.piblisher();
//        System.in.read();
        System.out.println("over！！！！！！！！！！！！！！！！！！");
    }
}