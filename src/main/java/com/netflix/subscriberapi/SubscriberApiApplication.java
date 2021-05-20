package com.netflix.subscriberapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.netflix.subscriberapi")
@EntityScan("com.netflix.subscriberapi")
public class SubscriberApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriberApiApplication.class, args);
	}

}
