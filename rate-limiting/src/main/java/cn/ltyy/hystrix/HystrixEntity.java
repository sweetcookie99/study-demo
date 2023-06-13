package cn.ltyy.hystrix;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @Date 2023/6/13 15:01
 * @@author lit
 */
public class HystrixEntity {
    // 窗口请求数
    private AtomicInteger requestCount;
    // 窗口异常数
    private AtomicInteger errorCount;

    public HystrixEntity(){
        this.requestCount=new AtomicInteger(0);
        this.errorCount=new AtomicInteger(0);
    }
    public int getRequestCountValue() {
        return requestCount.get();
    }

    public int getErrorCountValue() {
        return errorCount.get();
    }

    public void resetValue() {
        this.errorCount.set(0);
        this.requestCount.set(0);
    }

    public void addErrorCount(){
        this.errorCount.addAndGet(1);
    }

    public void addRequestCount(){
        this.requestCount.addAndGet(1);
    }
}
