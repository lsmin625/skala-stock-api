package com.sk.skala.stockapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = { RedisRepositoriesAutoConfiguration.class })
public class RestApiBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiBaseApplication.class, args);
	}

}
