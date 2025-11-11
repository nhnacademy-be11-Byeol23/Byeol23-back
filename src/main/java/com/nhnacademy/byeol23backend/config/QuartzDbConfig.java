package com.nhnacademy.byeol23backend.config;

import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Profile("prod")
@Configuration
public class QuartzDbConfig {
    @Bean
    @QuartzDataSource
    @ConfigurationProperties(prefix = "spring.datasource-quartz")
    public DataSource quartzDbDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public PlatformTransactionManager quartzDbTransactionManager() {
        return new DataSourceTransactionManager(quartzDbDataSource());
    }
}
