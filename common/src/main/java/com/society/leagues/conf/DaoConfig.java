package com.society.leagues.conf;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DaoConfig {
    private static Logger logger = LoggerFactory.getLogger(DaoConfig.class);
    @Value("${db-host:localhost}")
    String dbhost;
    @Value("${db-username:league}")
    String username;
    @Value("${db-password:}")
    String password;
    @Value("${db:league}")
    String db;

    @Bean
    @Profile("!test")
    DataSource getDataSource() {
        DataSource derbyDataSource = getDerby();
        //Test class path will use a embedded database
        if (derbyDataSource != null)
            return derbyDataSource;

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
    @Profile("!test")
    JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    @Bean
    @Profile("test")
    JdbcTemplate getDerbyJdbcTemplate() {
        JdbcTemplate template =  getDerbyTemplate();
          try {
            template.update("CREATE SCHEMA " + username);
        } catch (Throwable ignore) {
        }

        return template;
    }
    
    @Bean
    //@Profile("test")
    NamedParameterJdbcTemplate getDerbyJdbcNamedTemplate() {
        return new NamedParameterJdbcTemplate(getDerby());
    }
    
    @Bean
    @Profile("test")
    public DataSource getDerbyDataSource() {
        return getDerby();
    }

    public static JdbcTemplate getDerbyTemplate() {
        return new JdbcTemplate(getDerby());
    }

    public static DataSource getDerby() {
        String className = "org.apache.derby.jdbc.EmbeddedDriver";
        String prefix = "";
        try {
            Class.forName(className);
            try {
                Class.forName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
                className = "net.sf.log4jdbc.sql.jdbcapi.DriverSpy";
                prefix = "jdbc:log4:";
            } catch (Throwable ignore) {
            }
            logger.warn("Using derby embedded database");
        } catch (Throwable e) {
            return null;
        }

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(prefix + "jdbc:derby:memory:derbyDB;create=true");
        dataSource.setDriverClassName(className);
        return dataSource;
    }

}