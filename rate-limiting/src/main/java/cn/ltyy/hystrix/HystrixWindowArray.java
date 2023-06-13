package cn.ltyy.hystrix;

import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 滑动窗口的具体实现
 * @Date 2023/6/13 15:12
 * @@author lit
 */
public class HystrixWindowArray {
    // 单个窗口的长度
    private int windowLengthInMs;
    // 窗口数量
    private int sampleCount;
    // 所有窗口的总长度
    private int intervalInMs;
    // 窗口数组
    private final AtomicReferenceArray<HystrixWindow> array;

    /**
     * The conditional (predicate) update lock is used only when current bucket is deprecated.
     */
    private final ReentrantLock updateLock = new ReentrantLock();

    public HystrixWindowArray(int sampleCount, int intervalInMs){

        this.windowLengthInMs = intervalInMs/ sampleCount;
        this.intervalInMs = intervalInMs;
        this.sampleCount = sampleCount;

        this.array = new AtomicReferenceArray<>(sampleCount);
    }

    /**
     * 计算当前时间所在的窗口下表索引
     * @param timeMillis
     * @return
     */
    private int calculateTimeIdx(long timeMillis){
        long timeId = timeMillis / windowLengthInMs;

        return (int) (timeId%array.length());
    }

    /**
     * 获取当前时间所在的窗口开始时间
     */
    private long calculateWindowStart(long timeMillis) {
        return timeMillis - timeMillis % windowLengthInMs;
    }

    private HystrixEntity newEmptyWindowValue(long timeMillis){
        return new HystrixEntity();
    }

    /**
     * 获取当前窗口
     */
    public HystrixWindow currentWindow() {
        return currentWindow(System.currentTimeMillis());
    }


    /**
     * 重置一个窗口
     */
    private HystrixWindow resetWindowTo(HystrixWindow window, long startTime){
        return window.resetTo(startTime);
    }


    private HystrixWindow currentWindow(long timeMillis){
        if (timeMillis < 0) return null;

        int idx = calculateTimeIdx(timeMillis);

        long windowStart = calculateWindowStart(timeMillis);

        while (true) {
            HystrixWindow old = array.get(idx);
            if (old == null){
                //获取为空说明窗口未创建，新创建一个窗口
                HystrixWindow newWindow = new HystrixWindow(windowLengthInMs, windowStart, newEmptyWindowValue(timeMillis));
                if (array.compareAndSet(idx, null, newWindow)){
                    return newWindow;
                }else {
                    Thread.yield();
                }
            }else if(windowStart == old.getWindowStartInMs()){
                return old;
            }else if(windowStart > old.getWindowStartInMs()){
                // 如果窗口已经存在，而且窗口开始时间比之前的窗口开始时间要大
                // 说明原来的窗口已经过时了，需要替换一个新的窗口
                // 所以加锁防止竞争
                if (updateLock.tryLock()) {
                    try {
                        // 这里我选择直接重置之前的窗口（）
                        return resetWindowTo(old, windowStart);
                    } finally {
                        updateLock.unlock();
                    }
                } else {
                    // Contention failed, the thread will yield its time slice to wait for bucket available.
                    Thread.yield();
                }
            }else if (windowStart < old.getWindowStartInMs()) {
                // 窗口的开始时间比之前的窗口开始时间还会小，这种属于异常情况
                // 要是真出现了也只能新建一个窗口返回了
                return new HystrixWindow(windowLengthInMs, windowStart, newEmptyWindowValue(timeMillis));
            }
        }
    }

    /**
     * 获取当前窗口内的值
     */
    public HystrixEntity getWindowValue() {
        return getWindowValue(System.currentTimeMillis());
    }

    public HystrixEntity getWindowValue(long timeMillis) {
        if (timeMillis < 0) {
            return null;
        }
        int idx = calculateTimeIdx(timeMillis);
        HystrixWindow bucket = array.get(idx);
        if (bucket == null || !bucket.isTimeInWindow(timeMillis)) {
            return null;
        }
        return bucket.getHystrixEntity();
    }

    public List<HystrixEntity> values() {
        return values(System.currentTimeMillis());
    }

    private List<HystrixEntity> values(long timeMillis) {
        if (timeMillis < 0) {
            return new ArrayList<HystrixEntity>();
        }
        int size = array.length();
        List<HystrixEntity> result = new ArrayList<HystrixEntity>(size);

        for (int i = 0; i < size; i++) {
            HystrixWindow window = array.get(i);
            if (window == null || isWindowDeprecated(timeMillis, window)) {
                continue;
            }
            result.add(window.getHystrixEntity());
        }
        return result;
    }

    /**
     * 判断窗口是否有效
     */
    public boolean isWindowDeprecated(long time, HystrixWindow window) {
        return time - window.getWindowStartInMs() > intervalInMs;
    }
}
