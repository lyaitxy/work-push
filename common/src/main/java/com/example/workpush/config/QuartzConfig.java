package com.example.workpush.config;

import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class QuartzConfig {

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        return new SpringBeanJobFactory() {
            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
                // 1. 先用默认方式创建 Job 实例
                Object job = super.createJobInstance(bundle);

                // 2. 让 Spring 进行依赖注入
                AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
                factory.autowireBean(job);

                return job;
            }
        };
    }
}
