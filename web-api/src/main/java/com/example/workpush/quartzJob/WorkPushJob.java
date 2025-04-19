package com.example.workpush.quartzJob;

import com.example.workpush.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Supplier;

@Component
@Slf4j
public class WorkPushJob implements Job {
    // 邮件相关
    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    // 获取结果相关
    @Resource
    private AlibabaWorkService alibabaWorkService;
    @Resource
    private MeiTuanWorkService meiTuanWorkService;
    @Resource
    private JingDongWorkService jingDongWorkService;
    @Resource
    private XHSWorkService xhsWorkService;
    @Resource
    private KuaiShoService kuaiShoService;

    @Resource
    @Qualifier("customThreadPoolExecutor")
    private Executor customThreadPoolExecutor;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String res = "";
        String to = jobExecutionContext.getJobDetail().getJobDataMap().getString("to");
        String key = jobExecutionContext.getJobDetail().getJobDataMap().getString("key");
        String categoryType = jobExecutionContext.getJobDetail().getJobDataMap().getString("categoryType");
        long before = System.currentTimeMillis();
        // 并发任务，调用复用方法
        CompletableFuture<String> aliFuture = fetchJobUpdate(() -> alibabaWorkService.pushWork(to, categoryType, key), "阿里", customThreadPoolExecutor);
        CompletableFuture<String> MTFuture = fetchJobUpdate(() -> meiTuanWorkService.pushWork(to, categoryType, key), "美团", customThreadPoolExecutor);
        CompletableFuture<String> JDFuture = fetchJobUpdate(() -> jingDongWorkService.pushWork(to, categoryType, key), "京东", customThreadPoolExecutor);
        CompletableFuture<String> XHSFuture = fetchJobUpdate(() -> xhsWorkService.pushWork(to, categoryType, key), "小红书", customThreadPoolExecutor);
        CompletableFuture<String> KSFuture = fetchJobUpdate(() -> kuaiShoService.pushWork(to, categoryType, key), "快手", customThreadPoolExecutor);

        CompletableFuture<Void> all = CompletableFuture.allOf(aliFuture, MTFuture, JDFuture, XHSFuture, KSFuture);
        CompletableFuture<String> resultFuture = all.thenApply(v -> {
            try {
                return aliFuture.get() + MTFuture.get() + JDFuture.get() + XHSFuture.get() + KSFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return "";
            }
        });

        try {
            res = resultFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        long after = System.currentTimeMillis();
        log.info("消耗时间为: {}", after - before);
        if(!res.isEmpty()) {
            // 发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("职位更新");
            message.setText(res);
            mailSender.send(message);
        } else {
            log.info("数据为空，不发邮件");
        }
    }

    private CompletableFuture<String> fetchJobUpdate(
            Supplier<String> supplier,
            String platformName,
            Executor executor
    ) {
        return CompletableFuture.supplyAsync(() -> {
            String data = supplier.get();
            return (data != null && !data.isEmpty()) ? platformName + "有职位更新：\n" + data : "";
        }, executor);
    }

}
