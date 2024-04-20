package com.javadevjournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@Configuration
@EnableJms
public class SpringSecurityForARestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityForARestApiApplication.class, args);
    }
}
