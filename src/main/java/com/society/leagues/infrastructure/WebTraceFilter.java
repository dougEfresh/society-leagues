package com.society.leagues.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebTraceFilter {

    @Autowired TraceRepository traceRepository;
    @Value("${dumpRequest:false}")
    boolean dumpRequests;

    @Bean
    public WebRequestTraceFilter webTraceFilterLogger() {
        WebRequestTraceFilter traceFilter = new WebRequestTraceFilter(traceRepository);
        traceFilter.setDumpRequests(dumpRequests);
        return traceFilter;
    }

}
