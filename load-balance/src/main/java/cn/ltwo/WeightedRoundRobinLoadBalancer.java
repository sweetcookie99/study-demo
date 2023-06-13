package cn.ltwo;

import java.util.List;

/**
 * @Description TODO
 * @Date 2023/6/13 14:08
 * @@author lit
 */
public class WeightedRoundRobinLoadBalancer {
    // 记录上一次选择的服务器下标
    private int lastIndex = -1;
    // 记录当前权重
    private int currentWeight = 0;
    // 最大权重
    private int maxWeight;
    // 权重的最大公约数
    private int gcdWeight;
    // 服务器列表
    private List<Server> serverList;

    public WeightedRoundRobinLoadBalancer(List<Server> serverList) {
        this.serverList = serverList;
        init();
    }

    // 初始化
    private void init() {
        // 获取最大权重
        maxWeight = getMaxWeight();
        // 获取权重的最大公约数
        gcdWeight = getGcdWeight();
    }

    // 传入服务器列表，返回加权轮询选择的服务器
    public Server selectServer() {
        while (true) {
            // 上一次选择的服务器下标加1
            lastIndex = (lastIndex + 1) % serverList.size();
            // 如果上一次选择的服务器下标为0，重新计算当前权重
            if (lastIndex == 0) {
                currentWeight = currentWeight - gcdWeight;
                if (currentWeight <= 0) {
                    currentWeight = maxWeight;
                    if (currentWeight == 0) {
                        return null;
                    }
                }
            }
            // 获取当前下标的服务器
            Server server = serverList.get(lastIndex);
            // 如果当前服务器的权重大于等于当前权重，返回该服务器
            if (server.getWeight() >= currentWeight) {
                return server;
            }
        }
    }


    // 获取最大权重
    private int getMaxWeight() {
        int maxWeight = 0;
        for (Server server : serverList) {
            int weight = server.getWeight();
            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }
        return maxWeight;
    }

    // 获取权重的最大公约数
    private int getGcdWeight() {
        int gcdWeight = 0;
        for (Server server : serverList) {
            int weight = server.getWeight();
            if (gcdWeight == 0) {
                gcdWeight = weight;
            } else {
                gcdWeight = gcd(gcdWeight, weight);
            }
        }
        return gcdWeight;
    }

    // 求最大公约数
    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }

    // 服务器类
    public static class Server {
        private String ip;
        private int port;
        private int weight; // 权重

        public Server(String ip, int port, int weight) {
            this.ip = ip;
            this.port = port;
            this.weight = weight;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        // getter和setter方法省略
    }
}
