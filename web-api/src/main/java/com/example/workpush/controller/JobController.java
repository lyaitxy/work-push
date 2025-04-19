package com.example.workpush.controller;

import cn.hutool.core.lang.Validator;
import com.example.workpush.quartzJob.WorkPushJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/Job")
@Slf4j
public class JobController {

    @Resource
    private Scheduler scheduler;
    @Resource
    private RedissonClient redissonClient;

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
        JobKey jobKey = new JobKey("workPushJob" + to + categoryType + key, "jobGroup");
        RLock lock = redissonClient.getLock(to + categoryType + key);
        boolean isLock = lock.tryLock();
        if(isLock) {
            try {
            // 判断当前jobKey是否存在
            boolean isExist = scheduler.checkExists(jobKey);
            if (isExist) {
                return "当前定时任务已存在，无需重复设置";
            }
            // 开启工作推送任务
            JobDataMap jobDataMap = new JobDataMap();

            jobDataMap.put("to", to);
            jobDataMap.put("categoryType", categoryType);
            jobDataMap.put("key", key);
            JobDetail jobDetail1 = JobBuilder.newJob(WorkPushJob.class)
                    .withDescription("开启工作推送服务")
                    .withIdentity(jobKey)
                    .storeDurably(true)
                    .setJobData(jobDataMap)
                    .build();
            int[] options = {0, 10, 20, 30, 40, 50};
            CronScheduleBuilder cron = CronScheduleBuilder.cronSchedule("0 " + options[ThreadLocalRandom.current().nextInt(options.length)] + " 10,14,18 * * ?");
            TriggerKey triggerKey = new TriggerKey("workPushTrigger" + to + categoryType + key, "jobGroup");
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withDescription("工作推送触发器")
                    .withIdentity(triggerKey)
                    .withSchedule(cron)
                    .build();
            scheduler.scheduleJob(jobDetail1, Set.of(trigger), true);
            scheduler.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            return "正确开启推送！";
        } else return  "请不要发送同一请求";
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
            JobKey jobKey = new JobKey("workPushJob" + to + categoryType + key, "jobGroup");
            // 判断当前jobKey是否存在
            boolean isExist = scheduler.checkExists(jobKey);
            if(!isExist) {
                return "当前定时任务不存在，无需取消";
            }
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        return "取消定时成功！";
    }
}
