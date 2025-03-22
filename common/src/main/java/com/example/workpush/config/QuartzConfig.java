package com.example.workpush.config;

import jakarta.annotation.Resource;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {

    @Autowired
    private MyJobFactory myJobFactory;
    @Resource
    private DataSource dataSource;

    //创建调度器工厂
    @Bean(name = "SchedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean factoryBean=new SchedulerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setJobFactory(myJobFactory);
        return factoryBean;
    }


    @Bean(name="scheduler")
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }
}
