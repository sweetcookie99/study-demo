package cn.ltyy;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @Date 2023/6/13 14:37
 * @@author lit
 */
public class FixedWindowRateLimiter {
    private final int limit; // 限制请求数量
    private final AtomicInteger count; // 当前请求数
    private final long interval; // 时间间隔（毫秒）
    private long lastRequestTime; // 上一次请求时间

    private FixedWindowRateLimiter(int limit, long interval){
        this.limit = limit;

        this.interval = interval;

        count = new AtomicInteger(0);

        this.lastRequestTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest(){
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastRequestTime > interval){
            // 如果距离上一次请求时间已经超过了时间间隔，重置请求数和上一次请求时间
            count.set(0);
            lastRequestTime = currentTime;
        }

        if (count.get() <limit){
            // 如果请求数还没有达到限制数量，允许请求并增加请求数
            count.incrementAndGet();
            return true;
        }

        return false;
    }
}
