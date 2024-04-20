package com.javadevjournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.*;

@SpringBootApplication
@Configuration
@EnableJms
public class SideserviceApplication {

	public static void main(String[] args) throws JMSException {
		SpringApplication.run(SideserviceApplication.class, args);
	}

}
