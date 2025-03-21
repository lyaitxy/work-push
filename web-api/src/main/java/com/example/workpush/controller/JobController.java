package com.example.workpush.controller;

import com.example.workpush.quartzJob.CleanRedisIDJob;
import com.example.workpush.quartzJob.WorkPushJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/Job")
@Slf4j
public class JobController {

    @Resource
    private Scheduler scheduler;

    @GetMapping("/getData")
    public void getData(
            @RequestParam String to,
            @RequestParam(name = "type") String categoryType,
            @RequestParam(required = false, defaultValue = "Java") String key
    ) {

        // TODO 使用正则验证邮箱的格式
        try {

            // 开启工作推送任务
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("to", to);
            jobDataMap.put("categoryType", categoryType);
            jobDataMap.put("key", key);
            JobDetail jobDetail1 = JobBuilder.newJob(WorkPushJob.class)
                    .withDescription("开启工作推送服务")
                    .withIdentity("workPushJob","jobGroup")
                    .setJobData(jobDataMap)
                    .build();
            CronScheduleBuilder cron1 = CronScheduleBuilder.cronSchedule("0/20 * * * * ?");
            Trigger trigger1 = TriggerBuilder.newTrigger()
                    .withDescription("工作推送触发器")
                    .withIdentity("workPushTrigger","defaultGroup")
                    .withSchedule(cron1)
                    .build();
            // 清除redis中的数据
//            JobDetail jobDetail2 = JobBuilder.newJob(CleanRedisIDJob.class)
//                    .withDescription("清除redis")
//                    .usingJobData("to", to)
//                    .build();
//            CronScheduleBuilder cron2 = CronScheduleBuilder.cronSchedule("0 0 0 * * ?");
//            Trigger trigger2 = TriggerBuilder.newTrigger()
//                    .withDescription("清除redis触发器")
//                    .withIdentity("cleanRedisTrigger","defaultGroup")
//                    .withSchedule(cron2)
//                    .build();

            scheduler.scheduleJob(jobDetail1, Set.of(trigger1), true);
//            scheduler.scheduleJob(jobDetail2, Set.of(trigger2), true);

            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
