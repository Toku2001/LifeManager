package com.github.toku2001;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Toku2001Application {

	public static void main(String[] args) {
		SpringApplication.run(Toku2001Application.class, args);
	}

}
