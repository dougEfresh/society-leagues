package com.society.admin.conf;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring4.resourceresolver.SpringResourceResourceResolver;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
public class ThymeLeafConfiguration {
    @Autowired ThymeleafProperties properties;

    @Value("${spring.thymeleaf.templates_root:}")
    private String templatesRoot;

    @Autowired Environment environment;

    @Bean
    public TemplateResolver defaultTemplateResolver() {
        return iTemplateResolver(environment.acceptsProfiles("dev"));
    }

    @Bean
    public SpringResourceResourceResolver thymeleafResourceResolver() {
        return new SpringResourceResourceResolver();
    }

    private TemplateResolver iTemplateResolver(boolean dev) {
        String cwd = System.getProperty("user.dir");
        if (dev) {
            TemplateResolver resolver = new FileTemplateResolver();resolver.setSuffix(properties.getSuffix());
            resolver.setPrefix(cwd + "/src/main/resources/templates/");
            resolver.setTemplateMode(properties.getMode());
            resolver.setCharacterEncoding(properties.getEncoding().name());
            resolver.setCacheable(false);
            return resolver;
        }

        TemplateResolver resolver = new TemplateResolver();
        resolver.setResourceResolver(thymeleafResourceResolver());
        resolver.setPrefix(this.properties.getPrefix());
        resolver.setSuffix(this.properties.getSuffix());
        resolver.setTemplateMode(this.properties.getMode());
        if (this.properties.getEncoding() != null) {
            resolver.setCharacterEncoding(this.properties.getEncoding().name());
        }
        resolver.setCacheable(this.properties.isCache());
        Integer order = this.properties.getTemplateResolverOrder();
        if (order != null) {
            resolver.setOrder(order);
        }
        return resolver;
    }
}