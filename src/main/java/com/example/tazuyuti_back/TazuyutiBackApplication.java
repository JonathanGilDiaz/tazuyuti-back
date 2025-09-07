package com.example.tazuyuti_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TazuyutiBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TazuyutiBackApplication.class, args);
	}

}
