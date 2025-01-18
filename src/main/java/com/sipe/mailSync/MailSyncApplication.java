package com.sipe.mailSync;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailSyncApplication {

	public static void main(String[] args) {
		// load .env file
		Dotenv dotenv = Dotenv.load();
		// OPEN_API_KEY를 시스템 프로퍼티로 설정
		System.setProperty("OPEN_API_KEY", dotenv.get("OPEN_API_KEY"));

		SpringApplication.run(MailSyncApplication.class, args);
	}

}
