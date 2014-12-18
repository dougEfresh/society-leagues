package com.society.leagues.conf;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(String.format("jdbc:mysql://%s/%s",dbhost,db));
        dataSource.setUsername(username);
        if (password != null && !password.isEmpty())
            dataSource.setPassword(password);

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }

    @Bean
    JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }
}