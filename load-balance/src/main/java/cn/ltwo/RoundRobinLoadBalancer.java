package cn.ltwo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @Date 2023/6/13 11:53
 * @@author lit
 */
public class RoundRobinLoadBalancer extends BaseLoadBalancer{
    private volatile int lastIndex = -1;
    private AtomicInteger currentIndex = new AtomicInteger(0);


    @Override
    protected Server selectServer(List<Server> serverList) {
        int size = serverList.size();
        int current;
        int next;
        if (size == 0){
            return null;
        }

        if (size == 1){
            return serverList.get(0);
        }

        do {
            current = currentIndex.get();
            next = current + 1;
        }while (!currentIndex.compareAndSet(current, next));

        lastIndex = next;

        return serverList.get(lastIndex);
     /*   synchronized (this){
            if (lastIndex > serverList.size()){
                lastIndex = -1;
            }

            int index = lastIndex + 1;

            lastIndex = index;

            return serverList.get(index);
        }*/
    }
}
