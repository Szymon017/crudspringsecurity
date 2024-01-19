package com.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.demo.model.User;
import com.demo.service.UserService;

@SpringBootApplication
public class SimpleCrudAppApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(SimpleCrudAppApplication.class, args);
	}

	private UserService userService;

	@Autowired
	public SimpleCrudAppApplication(@Lazy UserService userService) {
		super();
		this.userService = userService;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// Creating admin account at first time the application running to login with
		// all permissions.
		User admin = userService.findByUserName("admin");
		if (admin == null) {
			userService.createAdmin();
		}
	}

}
