package com.hajj.hajj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HajjApplication {

	public static void main(String[] args) {
		SpringApplication.run(HajjApplication.class, args);
	}

}
