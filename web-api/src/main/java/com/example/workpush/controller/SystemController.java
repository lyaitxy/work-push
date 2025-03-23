package com.example.workpush.controller;

import com.example.workpush.quartzJob.CleanRedisIDJob;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/sys")
public class SystemController {

    @Resource
    private Scheduler scheduler;

    // 定时清除redis
    @GetMapping("/clean")
    public void clean() throws SchedulerException {
        // 清除redis中的数据
        JobDetail jobDetail = JobBuilder.newJob(CleanRedisIDJob.class)
                .withDescription("清除redis")
                .build();
        CronScheduleBuilder cron = CronScheduleBuilder.cronSchedule("0 0 0 * * ?");
        Trigger trigger = TriggerBuilder.newTrigger()
                .withDescription("清除redis触发器")
                .withIdentity("cleanRedisTrigger","defaultGroup")
                .withSchedule(cron)
                .build();

        scheduler.scheduleJob(jobDetail, Set.of(trigger), true);
    }
}
