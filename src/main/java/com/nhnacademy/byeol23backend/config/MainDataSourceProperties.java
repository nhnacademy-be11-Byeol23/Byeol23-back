package com.nhnacademy.byeol23backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource-data")
@Data
public class MainDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    private Dbcp2 dbcp2 = new Dbcp2();

    @Data
    public static class Dbcp2{
        private int initialSize;
        private int maxTotal;
        private int minIdle;
        private int maxIdle;
    }
}
