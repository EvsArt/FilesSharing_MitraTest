package com.artevseev.filessharing_testmitra.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.util.unit.DataSize;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FileUploadConfig {

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(128));
        factory.setMaxRequestSize(DataSize.ofMegabytes(128));
        return factory.createMultipartConfig();
    }

}