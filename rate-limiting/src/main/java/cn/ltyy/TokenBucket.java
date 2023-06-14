package cn.ltyy;

/**
 * @Description 令桶牌算法
 * 令牌桶按时间和比率恢复数量，若数量为0则不在处理请求
 * @Date 2023/6/14 14:30
 * @@author lit
 */
public class TokenBucket {
    /**
     * 上次请求时间
     */
    private long lastTime;

    /**
     * 令牌放入速率
     */
    private double rate;

    /**
     * 令牌桶容量
     */
    private long capacity;

    /**
     * 当前令牌数量
     */
    private long tokens;

    public TokenBucket(double rate, long capacity){
        this.rate = rate;
        this.capacity = capacity;
        lastTime = System.currentTimeMillis();
        tokens = capacity;
    }

    public synchronized boolean getToken(){
        long now = System.currentTimeMillis();
        long timeElapsed = now - lastTime;

        tokens += timeElapsed * rate;

        if (tokens > capacity) {
            tokens = capacity;
        }

        lastTime = now;
        if (tokens >= 1) {
            tokens--;
            return true;
        } else {
            return false;
        }
    }
}
