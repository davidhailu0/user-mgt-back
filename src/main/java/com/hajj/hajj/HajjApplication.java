package com.hajj.hajj;

import com.hajj.hajj.model.HUjjaj;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class HajjApplication {

	public static void main(String[] args) {
		SpringApplication.run(HajjApplication.class, args);
	}

}
