package  com.dubbo.registry;

import com.dubbo.loadbalance.LoadBalance;
import com.dubbo.loadbalance.RandomLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public  class ServiceDiscoveryImpl implements IServiceDiscovery {
    private    CuratorFramework curatorFramework;

    List<String> repos =  new ArrayList<String>();

    {
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZKConfig.CONNECT_STR).sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,10)).build();
        curatorFramework.start();
    }

    public String   discover(String serviceName ) {
        String servicePath = ZKConfig.ZK_REGISTRY_PATH+"/"+ serviceName;
        try {
            repos = curatorFramework.getChildren().forPath(servicePath);

//             System.out.println("服务发现成功" + repos    );
        } catch (Exception e) {
            e.printStackTrace();
        }
        //监听 zookeeper 的path节点
        rgisterWatch(servicePath);
        //简单实现随机负载
        LoadBalance loadBalance = new RandomLoadBalance();
        return loadBalance.select(repos);
    }

    private  void  rgisterWatch( final String  path )  {
        PathChildrenCache  childrenCache = new PathChildrenCache(curatorFramework,path,true );
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                repos = curatorFramework.getChildren().forPath(path);
            }
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        try{
            childrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
