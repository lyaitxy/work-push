package com.example.workpush.config;

import jakarta.annotation.Resource;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

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
//        factoryBean.setDataSource(dataSource);
        factoryBean.setJobFactory(myJobFactory);

        Properties props = new Properties();
        props.put("org.quartz.scheduler.instanceName", "MyScheduler");
        props.put("org.quartz.threadPool.threadCount", "20");
        props.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        props.put("org.quartz.jobStore.tablePrefix", "QRTZ_");

        // 指定 Quartz 使用名为 quartz_db 的数据源
        props.put("org.quartz.jobStore.dataSource", "quartz_db");

        // 定义数据源 quartz_db
        props.put("org.quartz.dataSource.quartz_db.driver", "com.mysql.cj.jdbc.Driver");
        props.put("org.quartz.dataSource.quartz_db.URL", "jdbc:mysql://localhost:3306/quartz_db?useSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
        props.put("org.quartz.dataSource.quartz_db.user", "root");
        props.put("org.quartz.dataSource.quartz_db.password", "ly20030321");
        props.put("org.quartz.dataSource.quartz_db.maxConnections", "10");
        factoryBean.setQuartzProperties(props);
        return factoryBean;
    }


    @Bean(name="scheduler")
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }
}
