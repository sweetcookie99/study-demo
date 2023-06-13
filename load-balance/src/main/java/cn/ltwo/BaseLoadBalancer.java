package cn.ltwo;


import java.util.List;

/**
 * @Description TODO
 * @Date 2023/6/13 10:50
 * @@author lit
 */
public abstract class BaseLoadBalancer {

    protected abstract Server selectServer(List<Server> serverList);

    // 服务器类
    protected static class Server {
        private String ip;
        private int port;

        public Server(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        // getter和setter方法省略
    }
}
