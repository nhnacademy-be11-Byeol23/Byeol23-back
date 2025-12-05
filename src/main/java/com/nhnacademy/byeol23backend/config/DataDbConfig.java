package com.nhnacademy.byeol23backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

//@Profile("prod")
//@Configuration
//@EnableJpaRepositories(
//        basePackages = {"com.nhnacademy.byeol23backend.bookset", "com.nhnacademy.byeol23backend.cartset", "com.nhnacademy.byeol23backend.couponset", "com.nhnacademy.byeol23backend.memberset", "com.nhnacademy.byeol23backend.orderset", "com.nhnacademy.byeol23backend.pointset", "com.nhnacademy.byeol23backend.reviewset", "com.nhnacademy.byeol23backend.like"},
//        entityManagerFactoryRef = "dataDbEntityManagerFactory",
//        transactionManagerRef = "dataDbTransactionManager"
//)
public class DataDbConfig {
//
//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix = "spring.datasource-data")
//    public DataSource dataDbDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    @Primary
//    public LocalContainerEntityManagerFactoryBean dataDbEntityManagerFactory(@Qualifier("dataDbDataSource") DataSource dataSource) {
//
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.nhnacademy.byeol23backend");
//        em. setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//
//        HashMap<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.hbm2ddl.auto", "validate");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        properties.put("hibernate.show_sql", true);
//        properties.put("hibernate.format_sql", true);
//        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
//
//        em.setJpaPropertyMap(properties);
//
//        return em;
//    }
//
//    @Bean
//    @Primary
//    public PlatformTransactionManager dataDbTransactionManager(@Qualifier("dataDbDataSource") DataSource dataSource) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(dataDbEntityManagerFactory(dataSource).getObject());
//
//        return transactionManager;
//    }
}
