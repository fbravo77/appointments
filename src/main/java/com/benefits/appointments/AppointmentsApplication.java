package com.benefits.appointments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppointmentsApplication {
	public static void main(String[] args) {SpringApplication.run(AppointmentsApplication.class, args);
	}

}
