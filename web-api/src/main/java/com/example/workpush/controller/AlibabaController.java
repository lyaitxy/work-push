package com.example.workpush.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.config.SearchProperties;
import com.example.workpush.entity.AliJob;
import com.example.workpush.quartzJob.WorkPushJob;
import com.example.workpush.service.AlibabaWorkService;
import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Ali")
@Slf4j
public class AlibabaController {

    @Resource
    private AlibabaWorkService alibabaWorkService;
    @GetMapping("/getData")
    public void getData() {
        try {
            // 开启任务调度
            JobDetail jobDetail = JobBuilder.newJob(WorkPushJob.class).withDescription("开启工作推送服务").withIdentity("work-push").build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 9,12,19 * * ?");
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withDescription("触发器描述")
                    .withIdentity("007","1")
                    .withSchedule(cronScheduleBuilder)
                    .startAt(new Date())  // 开始时间为当前时间
                    .build();

            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
