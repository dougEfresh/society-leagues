package com.society.leagues.conf.spring;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@SuppressWarnings("unused")
public class DaoConfig {
    private static Logger logger = LoggerFactory.getLogger(DaoConfig.class);
    @Value("${db-host:localhost}")
    String dbhost;
    @Value("${db-username:league}")
    String username;
    @Value("${db-password:}")
    String password;
    @Value("${db:leagues}")
    String db;
    @Value("${embedded:false}")
    boolean useEmbedded = false;

    public static String dbName = System.currentTimeMillis() + "";

    @Bean
    DataSource getDataSource() {
        if (useEmbedded) {
            return getEmbeddedDatasource();
        }
        String className = "com.mysql.jdbc.Driver";
        String prefix = "";
        try {
            Class.forName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
            className = "net.sf.log4jdbc.sql.jdbcapi.DriverSpy";
            prefix = "jdbc:log4";
        } catch (Throwable ignore) {
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

    @Bean
    NamedParameterJdbcTemplate getNamedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(getDataSource());
    }

    public static DataSource getEmbeddedDatasource() {
        logger.info("***** Using embedded DB ****  " + dbName);
        String className = "org.h2.Driver";
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:" + dbName);
        dataSource.setDriverClassName(className);
        return dataSource;
    }

}