package io.melakuera.tgbotzamena.services;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/*
 * Парсер сайта https://pc.edu.kg
 */
@Service
@Slf4j
public class WebSiteParser {

	private final String ROOT_URL = 
			"https://pc.edu.kg/index.php/studenty";
	private final String CSS_SELECTOR_TAG_A = 
			"div.span2:nth-child(4) > div:nth-child(1) > div:nth-child(3) > a:nth-child(1)";
	
	/*
	 * Возвращает ссылку на pdf документ
	 */
	public String getZamenaPdfDocumentLink() throws IllegalAccessException {

		Document doc = null;
		try {
			doc = Jsoup.connect(ROOT_URL).get();		
		} catch (IOException e) {
			log.warn("Что-то пошло не так: {}", e.getMessage());
			throw new IllegalAccessException();
		}
		Element tagA = doc.select(CSS_SELECTOR_TAG_A).first();
		String hrefAttrValue = tagA.attr("href");
		
		return "https://pc.edu.kg/" + hrefAttrValue;
	}
}
