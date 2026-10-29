package com.msa.msa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.msa.msa.client")
public class MsaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsaServiceApplication.class, args);
    }
}