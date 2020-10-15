package com.dubbo.registry;

import java.io.IOException;

public class Test{
    public static void main(String[] args) throws IOException {
        IRegisterCenter registerCenter = new  RegisterCenterImpl();
        registerCenter.register("com.csdn.IJack","127.0.0.1:8082");
         System.in.read();
    }

}