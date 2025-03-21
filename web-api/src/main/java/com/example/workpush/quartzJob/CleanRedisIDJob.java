package com.example.workpush.quartzJob;

import jakarta.annotation.Resource;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CleanRedisIDJob implements Job {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        redisTemplate.delete("id");
    }
}
