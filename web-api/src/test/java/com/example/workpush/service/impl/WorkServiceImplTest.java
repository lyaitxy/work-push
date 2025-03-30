package com.example.workpush.service.impl;

import com.example.workpush.service.JingDongWorkService;
import com.example.workpush.service.MeiTuanWorkService;
import com.example.workpush.service.XHSWorkService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class WorkServiceImplTest {

    @Resource
    private JingDongWorkService jingDongWorkService;
    @Resource
    private MeiTuanWorkService meiTuanWorkService;
    @Resource
    private XHSWorkService xhsWorkService;

    @Test
    void test1() throws InterruptedException {
        String s = jingDongWorkService.pushWork("1412997562@qq.com", "暑期实习", "算法");
        Thread.sleep(5000);
        log.info("结果为：{}", s);
    }

    @Test
    void test2() throws InterruptedException {
        String s = meiTuanWorkService.pushWork("1412997562@qq.com", "暑期实习", "算法");
        Thread.sleep(5000);
        log.info("结果为：{}", s);
    }

    @Test
    void test3() throws InterruptedException {
        String s = xhsWorkService.pushWork("1412997562@qq.com", "暑期实习", "算法");
        Thread.sleep(5000);
        log.info("结果为：{}", s);
    }
}