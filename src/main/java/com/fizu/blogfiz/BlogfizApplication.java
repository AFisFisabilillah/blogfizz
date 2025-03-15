package com.fizu.blogfiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BlogfizApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogfizApplication.class, args);
	}

}
