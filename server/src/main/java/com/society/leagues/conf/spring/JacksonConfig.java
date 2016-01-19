package com.society.leagues.conf.spring;

import com.society.leagues.converters.DateDeSerializer;
import com.society.leagues.converters.DateSerializer;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@SuppressWarnings("unused")
public class JacksonConfig {
    @Autowired  DateTimeSerializer dateTimeSerializer;
    @Autowired  DateTimeDeSerializer dateTimeDeSerializer;
    @Autowired
    DateSerializer dateSerializer;
    @Autowired
    DateDeSerializer dateDeSerializer;
    @Value("${pretty-print:false}") boolean prettyPrint = false;

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        return builder.indentOutput(prettyPrint).dateFormat(new SimpleDateFormat("yyyy-MM-dd")).
                defaultViewInclusion(false).
                serializers(dateSerializer).
                deserializerByType(LocalDateTime.class, dateTimeDeSerializer).
                deserializerByType(LocalDate.class,dateDeSerializer);
    }
}
