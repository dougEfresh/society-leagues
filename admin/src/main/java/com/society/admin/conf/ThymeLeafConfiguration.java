package com.society.admin.conf;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
    //@Profile(value = "dev")
    public ITemplateResolver defaultTemplateResolver() {
      return iTemplateResolver(true);
    }

    //@Bean
    //@Profile(value = "!dev")
    public ITemplateResolver classPathTemplateResolver() {
        return iTemplateResolver(false);
    }

    private ITemplateResolver iTemplateResolver(boolean dev) {
        String cwd = System.getProperty("user.dir");
        TemplateResolver resolver = new FileTemplateResolver();
//        resolver.setResourceResolver(thymeleafResourceResolver());
        resolver.setSuffix(properties.getSuffix());
        resolver.setPrefix(cwd + "/src/main/resources/templates/");
        resolver.setTemplateMode(properties.getMode());
        resolver.setCharacterEncoding(properties.getEncoding().name());
        resolver.setCacheable(false);
        return resolver;

    }
}