package com.example.workpush.controller;

import cn.hutool.core.lang.Validator;
import com.example.workpush.quartzJob.CleanRedisIDJob;
import com.example.workpush.quartzJob.WorkPushJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/Job")
@Slf4j
public class JobController {

    @Resource
    private Scheduler scheduler;

    @GetMapping("/getData")
    public String getData(
            @RequestParam String to,
            @RequestParam(name = "type") String categoryType,
            @RequestParam(required = false, defaultValue = "Java") String key
    ) {

        // 验证邮箱的格式和岗位类型的输入
        if(!Validator.isEmail(to)) {
            return "邮箱格式错误";
        }
        if(!Objects.equals(categoryType, "应届校招") && !Objects.equals(categoryType, "日常实习") && !Objects.equals(categoryType, "暑期实习")) {
            return "岗位类型输入错误";
        }
        try {

            // 开启工作推送任务
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("to", to);
            jobDataMap.put("categoryType", categoryType);
            jobDataMap.put("key", key);
            JobDetail jobDetail1 = JobBuilder.newJob(WorkPushJob.class)
                    .withDescription("开启工作推送服务")
                    .withIdentity("workPushJob" + to + key,"jobGroup")
                    .storeDurably(true)
                    .setJobData(jobDataMap)
                    .build();
            CronScheduleBuilder cron1 = CronScheduleBuilder.cronSchedule("0/10 * * * * ?");
            Trigger trigger1 = TriggerBuilder.newTrigger()
                    .withDescription("工作推送触发器")
                    .withIdentity("workPushTrigger" + to + key,"defaultGroup")
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

            return "正确开启推送！";
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/listJobs")
    public List<Map<String, Object>> listJobs() throws SchedulerException {
        List<Map<String, Object>> jobs = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                jobs.add(Map.of(
                        "jobName", jobKey.getName(),
                        "groupName", jobKey.getGroup(),
                        "jobClass", jobDetail.getJobClass().getName()
                ));
            }
        }
        return jobs;
    }

    @DeleteMapping("/cancel")
    public String cancelScheduledJob(
            @RequestParam String to,
            @RequestParam(name = "type") String categoryType,
            @RequestParam(required = false, defaultValue = "Java") String key
    ) {

        // 验证邮箱的格式和岗位类型的输入
        if(!Validator.isEmail(to)) {
            return "邮箱格式错误";
        }
        if(!Objects.equals(categoryType, "应届校招") && !Objects.equals(categoryType, "日常实习") && !Objects.equals(categoryType, "暑期实习")) {
            return "岗位类型输入错误";
        }
        try {
            JobKey jobKey = new JobKey("workPushJob" + to + key, "jobGroup");
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        return "取消定时成功！";
    }
}
