package com.example.workpush.quartzJob;

import com.example.workpush.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

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

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String res = "";
        String to = jobExecutionContext.getJobDetail().getJobDataMap().getString("to");
        String key = jobExecutionContext.getJobDetail().getJobDataMap().getString("key");
        String categoryType = jobExecutionContext.getJobDetail().getJobDataMap().getString("categoryType");
        long before = System.currentTimeMillis();
        // TODO 使用线程池并发发起请求
        // new ThreadPoolExecutor(5, 8, 1000, TimeUnit.MILLISECONDS)
        // 阿里系
        String data1 = alibabaWorkService.pushWork(to, categoryType, key);
        if(!data1.isEmpty()) {
            res += "阿里有职位更新：\n";
            res += data1;
        }
        // 美团
        String data2 = meiTuanWorkService.pushWork(to, categoryType, key);
        if(data2 != null && !data2.isEmpty()) {
            res += "美团有职位更新：\n";
            res += data2;
        }
        // 京东
        String data3 = jingDongWorkService.pushWork(to, categoryType, key);
        if(data3 != null && !data3.isEmpty()) {
            res += "京东有职位更新：\n";
            res += data3;
        }
        // 小红书
        String data4 = xhsWorkService.pushWork(to, categoryType, key);
        if(data4 != null && !data4.isEmpty()) {
            res += "小红书有职位更新：\n";
            res += data4;
        }
        // 快手
        String data5 = kuaiShoService.pushWork(to, categoryType, key);
        if(data5 != null && !data5.isEmpty()) {
            res += "快手有职位更新：\n";
            res += data5;
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
}
