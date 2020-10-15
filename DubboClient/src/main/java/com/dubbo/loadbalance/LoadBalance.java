package  com.dubbo.loadbalance;

import java.util.List;

public  interface LoadBalance{
     String select(List<String> list);
}