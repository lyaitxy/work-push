package com.example.workpush.service.impl;

import com.example.workpush.service.JingDongWorkService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@Slf4j
class JingDongWorkServiceImplTest {

    @Resource
    private JingDongWorkService jingDongWorkService;

    @Test
    void pushWork() throws InterruptedException {
        String s = jingDongWorkService.pushWork("1412997562@qq.com", "暑期实习", "算法");
        Thread.sleep(5000);
        log.info("结果为：{}", s);
    }
}