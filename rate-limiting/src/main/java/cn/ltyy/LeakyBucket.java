package cn.ltyy;

import java.time.Instant;

/**
 * @Description TODO
 * @Date 2023/6/14 15:03
 * @@author lit
 */
public class LeakyBucket {
    private int capacity; //漏桶容量
    private int rate; //漏水速率
    private int water; //当前水量
    private Instant timestamp; //上次漏水时间

    public LeakyBucket(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.water = 0;
        this.timestamp = Instant.now();
    }

    public synchronized boolean allow() { //判断是否允许通过
        Instant now = Instant.now();
        long duration = now.toEpochMilli() - timestamp.toEpochMilli(); //计算距上次漏水过去了多久
        int outflow = (int) (duration * rate / 1000); //计算过去的时间内漏出的水量
        water = Math.max(0, water - outflow); //更新当前水量，不能小于0
        if (water < capacity) { //如果漏桶还没满，放行
            water++;
            timestamp = now;
            return true;
        }
        return false; //否则拒绝通过
    }
}
