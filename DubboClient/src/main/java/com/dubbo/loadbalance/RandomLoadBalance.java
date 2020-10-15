package  com.dubbo.loadbalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance{

    public  String  select(List<String> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }
}
