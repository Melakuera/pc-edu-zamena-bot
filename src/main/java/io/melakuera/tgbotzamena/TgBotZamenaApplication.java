package io.melakuera.tgbotzamena;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.melakuera.tgbotzamena.services.ZamenaHandler;

@SpringBootApplication
public class TgBotZamenaApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(TgBotZamenaApplication.class, args);
	}
	
	@Bean
	CommandLineRunner runner(ZamenaHandler zamenaHandler) {
		return args -> zamenaHandler.getCurrentZamena();
	}
}
