package com.hajj.hajj;

import com.hajj.hajj.model.HUjjaj;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
public class HajjApplication {

	public static void main(String[] args) {
		SpringApplication.run(HajjApplication.class, args);
	}

}
