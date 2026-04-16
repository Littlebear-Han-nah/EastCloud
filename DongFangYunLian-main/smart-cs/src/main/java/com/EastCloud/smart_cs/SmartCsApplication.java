package com.EastCloud.smart_cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartCsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartCsApplication.class, args);
	}

}
