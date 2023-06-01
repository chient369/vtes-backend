package com.vtes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.vtes", "com.vtes.*" })
@EnableFeignClients
public class VtesApplication {

	public static void main(String[] args) {
		SpringApplication.run(VtesApplication.class, args);
	}

}
