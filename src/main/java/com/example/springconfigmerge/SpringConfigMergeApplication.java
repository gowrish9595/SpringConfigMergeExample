package com.example.springconfigmerge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringConfigMergeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringConfigMergeApplication.class, args);
    }
}
