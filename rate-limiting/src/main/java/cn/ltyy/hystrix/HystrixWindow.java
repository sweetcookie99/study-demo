package cn.ltyy.hystrix;

/**
 * @Description TODO
 * @Date 2023/6/13 15:02
 * @@author lit
 */
public class HystrixWindow {
    // 窗口的长度 单位：ms
    private final int windowLengthInMs;
    // 窗口的开始时间戳  单位：ms
    private long windowStartInMs;
    // 窗口内存放的实体类
    private HystrixEntity hystrixEntity;
    public HystrixWindow(int windowLengthInMs, long windowStartInMs, HystrixEntity hystrixEntity) {
        this.windowLengthInMs = windowLengthInMs;
        this.windowStartInMs = windowStartInMs;
        this.hystrixEntity = hystrixEntity;
    }

    public int getWindowLengthInMs() {
        return windowLengthInMs;
    }

    public long getWindowStartInMs() {
        return windowStartInMs;
    }

    public HystrixEntity getHystrixEntity() {
        return hystrixEntity;
    }

    public void setHystrixEntity(HystrixEntity hystrixEntity) {
        this.hystrixEntity = hystrixEntity;
    }

    /**
     * 重置窗口
     * @param startTime
     * @return
     */
    public HystrixWindow resetTo(long startTime){
        this.windowStartInMs = startTime;
        hystrixEntity.resetValue();
        return this;
    }

    /**
     * 判断这个时间是否在这个时间戳中
     * @param timeMillis
     * @return
     */
    public boolean isTimeInWindow(long timeMillis) {
        return windowStartInMs <= timeMillis && timeMillis <= windowStartInMs + windowLengthInMs;
    }

}
