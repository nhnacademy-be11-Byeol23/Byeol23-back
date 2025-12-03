package com.nhnacademy.byeol23backend.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.nhnacademy.byeol23backend.bookset", "com.nhnacademy.byeol23backend.cartset", "com.nhnacademy.byeol23backend.couponset", "com.nhnacademy.byeol23backend.memberset", "com.nhnacademy.byeol23backend.orderset", "com.nhnacademy.byeol23backend.pointset", "com.nhnacademy.byeol23backend.reviewset", "com.nhnacademy.byeol23backend.like"},
        entityManagerFactoryRef = "dataDbEntityManagerFactory",
        transactionManagerRef = "dataDbTransactionManager"
)
@EnableConfigurationProperties(MainDataSourceProperties.class)
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource mainDataSource(MainDataSourceProperties prop) {

        BasicDataSource ds = new BasicDataSource();

        ds.setUrl(prop.getUrl());
        ds.setUsername(prop.getUsername());
        ds.setPassword(prop.getPassword());
        ds.setDriverClassName(prop.getDriverClassName());

        // === DBCP2 커스텀 설정 적용 ===
        MainDataSourceProperties.Dbcp2 dbcp = prop.getDbcp2();
        ds.setInitialSize(dbcp.getInitialSize());
        ds.setMaxTotal(dbcp.getMaxTotal());
        ds.setMinIdle(dbcp.getMinIdle());
        ds.setMaxIdle(dbcp.getMaxIdle());

        return ds;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean dataDbEntityManagerFactory(@Qualifier("mainDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource);
        em.setPackagesToScan("com.nhnacademy.byeol23backend");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager dataDbTransactionManager(@Qualifier("mainDataSource") DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(dataDbEntityManagerFactory(dataSource).getObject());

        return transactionManager;
    }
}
