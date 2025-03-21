package com.example.workpush.config;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Autowired
    private MyJobFactory myJobFactory;

    //创建调度器工厂
    @Bean(name = "SchedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean factoryBean=new SchedulerFactoryBean();
        factoryBean.setJobFactory(myJobFactory);
        return factoryBean;
    }


    @Bean(name="scheduler")
    public Scheduler scheduler(){
        return schedulerFactoryBean().getScheduler();
    }
}
