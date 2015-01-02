package com.society.leagues.conf;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DaoConfig {

    @Value("${db-host:localhost}")
    String dbhost;
    @Value("${db-username:league}")
    String username;
    @Value("${db-password:}")
    String password;
    @Value("${db:league}")
    String db;

    @Bean
    DataSource getDataSource() {
        String className = "com.mysql.jdbc.Driver";
        String prefix = "";
        try {
            Class.forName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
            className = "net.sf.log4jdbc.sql.jdbcapi.DriverSpy";
            prefix = "jdbc:log4";
        } catch (Throwable e) {
        }
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(String.format("%sjdbc:mysql://%s/%s",prefix,dbhost,db));
        dataSource.setUsername(username);
        if (password != null && !password.isEmpty())
            dataSource.setPassword(password);

        dataSource.setDriverClassName(className);
        return dataSource;
    }

    @Bean
    JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

}