package com.dubbo.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public  class RegisterCenterImpl implements IRegisterCenter {
    private    CuratorFramework curatorFramework;
    {
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZKConfig.CONNECT_STR).sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,10)).build();
        curatorFramework.start();
    }

    public void register(String serviceName, String serviceAddress) {
        String servicePath = ZKConfig.ZK_REGISTRY_PATH+"/"+ serviceName;
        try {
            if(curatorFramework.checkExists().forPath(servicePath)==null ){
                curatorFramework.create().creatingParentContainersIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
            }
            String addressPath =  servicePath + "/"+ serviceAddress ;
            String rNode =   curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath,"0".getBytes());
            System.out.println("注册服务成功" +  rNode );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
