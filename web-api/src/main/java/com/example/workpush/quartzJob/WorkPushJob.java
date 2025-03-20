package com.example.workpush.quartzJob;

import com.example.workpush.service.AlibabaWorkService;
import jakarta.annotation.Resource;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkPushJob implements Job {
    @Resource
    private AlibabaWorkService alibabaWorkService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        alibabaWorkService.pushwork();
    }
}
