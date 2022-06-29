package io.melakuera.tgbotzamena.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import io.melakuera.tgbotzamena.enums.FacultyType;
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

	private static final String KEY_WORD = "ЗАМЕНА";
	private final List<String> keysGroup = getFacultyTypeRusNames();

	/*
	 * Возвращает замену по заданной группе
	 */
	public Map<String, List<String>> getZamenaDataByGroup() throws IllegalAccessException {

		String pdfDocLink = webSiteParser.getZamenaPdfDocumentLink();
		Map<String, List<String>> zamenaData = new HashMap<>();
		String[] pdfTexts;

		try {
			pdfStripper = new PDFTextStripper();
			pdfResource = new UrlResource(pdfDocLink);

			pdfDoc = PDDocument.load(pdfResource.getInputStream());

			pdfTexts = pdfStripper.getText(pdfDoc).split("\n");

		} catch (IOException e) {
			log.error("Что-то критическое произошло: {}", e.getMessage());
			return Collections.emptyMap();
		}
		
		for (int i = 0; i < pdfTexts.length; i++) {

			String text = pdfTexts[i].strip();

			// Например: ЗАМЕНА НА ПЯТНИЦУ – 24 ИЮНЯ (ЧИСЛИТЕЛЬ) 2022г
			if (text.contains(KEY_WORD.toLowerCase()) || text.contains(KEY_WORD)) {
				zamenaData.put("head", List.of(text));
			}

			// КС 1-21 4п Адабият Каримова Э.М. 37
			if (keysGroup.contains(text.split(" ")[0])) {

				String groupName = text.substring(0, 8).replaceFirst(" ", "");
				String classInfo = text.substring(9, text.length()).strip();
				List<String> groupZamena = new ArrayList<>();

				groupZamena.add(classInfo);
				i++;

				while (i < pdfTexts.length && pdfTexts[i].strip().matches("^\\d.+")) {
					groupZamena.add(pdfTexts[i].strip());
					i++;
				}
				zamenaData.put(groupName, groupZamena);
			}
		}
		try {
			pdfDoc.close();
		} catch (IOException e) {
			log.error("Что-то критическое произошло: {}", e.getMessage());
			return Collections.emptyMap();
		}

		if (zamenaData.size() <= 1) {
			return Collections.emptyMap();
		}
		return zamenaData;
	}

	private List<String> getFacultyTypeRusNames() {

		return Arrays.stream(FacultyType.values()).map(FacultyType::getRusName).toList();
	}
}