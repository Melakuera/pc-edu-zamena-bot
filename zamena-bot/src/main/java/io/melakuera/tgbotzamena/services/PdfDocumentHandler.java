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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.melakuera.tgbotzamena.constants.FacultyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Обработчик pdf документа
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfDocumentHandler {

	private static final String GET_ERROR = "Что-то произошло критическое: {}";

	private PDFTextStripper pdfStripper;

	private static final String KEY_WORD = "ЗАМЕНА";
	private final List<String> keysGroup = FacultyType.values();

	/*
	 * Парсит замена PDF файл
	 */
	public Map<String, List<String>> parsePdfDoc(MultipartFile zamenaFile) {

		log.info("Парсер pdf-документа начал свою работу...");
		
		Map<String, List<String>> zamenaData = new HashMap<>();
		String[] pdfTexts;
		
		try (PDDocument pdfDoc = PDDocument.load(zamenaFile.getInputStream())) {
			
			pdfStripper = new PDFTextStripper();
			pdfTexts = pdfStripper.getText(pdfDoc).split("\n");
			
			for (int i = 0; i < pdfTexts.length; i++) {

				String text = pdfTexts[i].strip();

				// Например: ЗАМЕНА НА ПЯТНИЦУ – 24 ИЮНЯ (ЧИСЛИТЕЛЬ) 2022г
				if (text.contains(KEY_WORD.toLowerCase()) || text.contains(KEY_WORD)) {
					zamenaData.put("head", List.of(text));
				}

				String[] splittedText = text.split(" ");
				int savedI = i;
				// КС 1-21 4п Адабият Каримова Э.М. 37
				if (keysGroup.contains(splittedText[0])) {

					String groupName = 
							splittedText[0].concat(" ").concat(splittedText[1]);
					String classInfo = String.join(" ", 
							Arrays.copyOfRange(splittedText, 2, splittedText.length));
					List<String> groupZamena = new ArrayList<>();

					groupZamena.add(classInfo);
					i++;

					while (i < pdfTexts.length && pdfTexts[i].strip().matches("^\\d.+")) {
						groupZamena.add(pdfTexts[i].strip());
						i++;
					}
					zamenaData.put(groupName, groupZamena);
				}
				i = savedI;
			}
		} catch (IOException e) {
			log.error(GET_ERROR, e.getMessage());
			return Collections.emptyMap();
		}
		
		log.info("Парсер pdf-документа завершил свою работу. Результат:");
		zamenaData.forEach((key, value) -> log.info("{}: {} ", key, value));
		
		if (zamenaData.size() < 1)
			return Collections.emptyMap();

		return zamenaData;
	}
}