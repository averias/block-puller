package com.averiasconnect.blockpuller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlockPullerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockPullerApplication.class, args);
	}

}
