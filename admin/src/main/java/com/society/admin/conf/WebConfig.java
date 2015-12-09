package com.society.admin.conf;

import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired DateTimeSerializer dateTimeSerializer;
    @Autowired DateTimeDeSerializer dateTimeDeSerializer;
    @Value("${pretty-print:true}") boolean prettyPrint;

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        return builder.indentOutput(prettyPrint).dateFormat(new SimpleDateFormat("yyyy-MM-dd")).
                defaultViewInclusion(true).
                serializers(dateTimeSerializer).
                deserializerByType(LocalDateTime.class, dateTimeDeSerializer);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String cwd = System.getProperty("user.dir");
        for(String d: Arrays.asList("img","js","css","fonts")) {
            //registry.addResourceHandler("/" + d + "/**").addResourceLocations("file://./src/main/resources/public/" + d +"/");
            System.out.println("Path  file://" + cwd + "/src/main/resources/public/" + d +"/");
            registry.addResourceHandler("/" + d + "/**").addResourceLocations("file://" + cwd + "/src/main/resources/public/" + d +"/").setCachePeriod(0);
        }
    }
}
