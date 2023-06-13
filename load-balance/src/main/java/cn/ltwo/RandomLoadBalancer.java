package cn.ltwo;

import java.util.List;
import java.util.Random;

/**
 * @Description TODO
 * @Date 2023/6/13 11:05
 * @@author lit
 */
public class RandomLoadBalancer extends BaseLoadBalancer{

    @Override
    protected Server selectServer(List<Server> serverList) {
        // 获取服务器列表大小
        int size = serverList.size();
        // 生成随机数
        int randomIndex = new Random().nextInt(size);

        return serverList.get(randomIndex);
    }
}
