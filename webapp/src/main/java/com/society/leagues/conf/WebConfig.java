package com.society.leagues.conf;

import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Configuration
public class WebConfig  {
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

    /*
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (environment.acceptsProfiles("dev")) {
            String cwd = System.getProperty("user.dir");
            registry.addResourceHandler("/**")
                        .addResourceLocations("file://" + cwd + "/src/main/resources/static/")
                        .setCachePeriod(0);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CookieInterceptor()).addPathPatterns("/*");
        super.addInterceptors(registry);
    }
    */
}
