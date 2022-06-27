package io.melakuera.tgbotzamena.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Обработчик pdf документа
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfDocumentHandler {

	private final WebSiteParser webSiteParser;

	private PDFTextStripper pdfStripper;
	private PDDocument pdfDoc;
	private Resource pdfResource;

	private final String KEY_WORD = "замена";
	private final String KEY_GROUP = "ПКС 3-21";

	/*
	 *  Возвращает замену по заданной группе
	 */
	public List<String> getZamenaDataByGroup() {

		String pdfDocLink = webSiteParser.getZamenaPdfDocumentLink();
		List<String> zamenaData = new ArrayList<>();
		
		try {
			pdfStripper = new PDFTextStripper();
			pdfResource = new UrlResource(pdfDocLink);

			pdfDoc = PDDocument.load(pdfResource.getInputStream());

			String[] texts = pdfStripper.getText(pdfDoc).split("\n");

			for (int i = 0; i < texts.length; i++) {				
				String tempText = texts[i].strip();
				if (tempText.contains(KEY_WORD.toUpperCase()) || tempText.contains(KEY_WORD)) {
					zamenaData.add(tempText);
				}
				if (tempText.contains(KEY_GROUP)) {
					String group = tempText.substring(0, 8);
					String classInfo = tempText.substring(9, tempText.length());
					zamenaData.add(group);
					zamenaData.add(classInfo);
					i++;
					
					while (i < texts.length && texts[i].strip().matches("^\\d.+")) {
						zamenaData.add(texts[i].strip());
						i++;
					}
				}
			}
			pdfDoc.close();
		} catch (IOException e) {
			log.warn("Что-то пошло не так: {}", e.getMessage());
		}
		if (zamenaData.size() <= 1) {
			return null;
		}
		return zamenaData;

	}
}
