package com.society.admin.conf;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.resourceresolver.SpringResourceResourceResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
public class ThymeLeafConfiguration {
    @Autowired ThymeleafProperties properties;

    @Value("${spring.thymeleaf.templates_root:}")
    private String templatesRoot;

    @Bean
    public ITemplateResolver defaultTemplateResolver() {
        String cwd = System.getProperty("user.dir");
        TemplateResolver resolver = new FileTemplateResolver();
//        resolver.setResourceResolver(thymeleafResourceResolver());
        resolver.setSuffix(properties.getSuffix());
        resolver.setPrefix(cwd + "/src/main/resources/templates/");
        resolver.setTemplateMode(properties.getMode());
        //resolver.setCharacterEncoding(properties.getEncoding());
        resolver.setCacheable(false);
        return resolver;
    }
}