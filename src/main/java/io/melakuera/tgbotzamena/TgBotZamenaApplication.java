package io.melakuera.tgbotzamena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.melakuera.tgbotzamena.services.PdfDocumentHandler;
import io.melakuera.tgbotzamena.services.WebSiteParser;

@SpringBootApplication
public class TgBotZamenaApplication {

	public static void main(String[] args) throws IllegalAccessException {
		SpringApplication.run(TgBotZamenaApplication.class, args);
		
		PdfDocumentHandler handler = new PdfDocumentHandler(new WebSiteParser());
		var data = handler.getZamenaDataByGroup();
		
		data.forEach((x, y) -> {;
			System.out.println(x + ":");
			y.forEach(System.out::println);
		});
	}

}
