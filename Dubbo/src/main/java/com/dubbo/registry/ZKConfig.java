package com.dubbo.registry;

/**
 * zookeeper 配置连接和主目录
 */
public class  ZKConfig{
    public final static String CONNECT_STR= "127.0.0.1:2181";
    //服务放入到registry 主目录下
    public final static String ZK_REGISTRY_PATH = "/registry";

}