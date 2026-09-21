package org.optipace.garmentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient 
public class GarmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GarmentServiceApplication.class, args);
	}

}
