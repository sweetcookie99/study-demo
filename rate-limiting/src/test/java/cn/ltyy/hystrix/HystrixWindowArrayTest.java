package cn.ltyy.hystrix;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Description TODO
 * @Date 2023/6/13 16:33
 * @@author lit
 */
class HystrixWindowArrayTest {
    @Test
    public void test(){
        // 初始化 代表监控1s内的指标  窗口数为2
        HystrixWindowArray hystrixWindowArray = new HystrixWindowArray(2, 1000);

// 指标监控  数量+1
        hystrixWindowArray.currentWindow().getHystrixEntity().addErrorCount();
        hystrixWindowArray.currentWindow().getHystrixEntity().addRequestCount();

// 获取所有窗口的指标累计  判断是否超标,也就是1s内的总计
        List<HystrixEntity> hystrixEntities = hystrixWindowArray.values();
        Integer errorCount = hystrixEntities.stream().map(HystrixEntity::getErrorCountValue).reduce(Integer::sum).get();
        Integer requestCount = hystrixEntities.stream().map(HystrixEntity::getRequestCountValue).reduce(Integer::sum).get();

        System.out.println("Request count: " + requestCount+"errorCount: " + errorCount);

    }

}