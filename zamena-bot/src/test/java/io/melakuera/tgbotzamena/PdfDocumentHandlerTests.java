package io.melakuera.tgbotzamena;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import io.melakuera.tgbotzamena.services.PdfDocumentHandler;

@SpringBootTest(classes = PdfDocumentHandler.class)
class PdfDocumentHandlerTests {

	@Autowired
	private PdfDocumentHandler pdfDocumentHandler;

	@Test
	@Order(1)
	void pdfDocShouldBeExists() throws Exception {
		InputStream stream1 = getClass().getResourceAsStream("valid.pdf");
		InputStream stream2 = getClass().getResourceAsStream("invalid.pdf");
		
		Assertions.assertThat(stream1).isNotNull();
		Assertions.assertThat(stream2).isNotNull();
	}

	@Test
	@Order(2)
	void shouldParsePdfDocCorrectly() throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("valid.pdf");
		MultipartFile pdfDoc = new MockMultipartFile("valid", inputStream);
		Map<String, List<String>> result = pdfDocumentHandler.parsePdfDoc(pdfDoc);

		Assertions.assertThat(result).isNotEmpty();

		Assertions.assertThat(result.get("head").get(0))
				.containsPattern("ЗАМЕНА\\s+НА\\s+ПЯТНИЦУ\\s+–\\s+01\\s+ИЮЛЯ\\s+\\(ЗНАМЕНАТЕЛЬ\\)\\s+2022г");

		// org.hamcrest.MatcherAssert
		assertThat(result.get("ЭкСС 1-21").size(), is(1));
		assertThat(result.get("ЭкСС 1-21").get(0), containsString("3п Физвоспитание"));

		assertThat(result.get("ЭССС 1-21").size(), is(2));
		assertThat(result.get("ЭССС 1-21").get(0), containsString("2п Физвоспитание"));
		assertThat(result.get("ЭССС 1-21").get(1), containsString("3п Математика"));

		assertThat(result.get("ЭССС 2-21").size(), is(1));
		assertThat(result.get("ЭССС 2-21").get(0), containsString("4п Математика"));

		assertThat(result.get("ЭССС 3-21").size(), is(1));
		assertThat(result.get("ЭССС 3-21").get(0), containsString("5п Математика"));

		assertThat(result.get("ПКС 1-21").size(), is(1));
		assertThat(result.get("ПКС 1-21").get(0), containsString("4п Кырг.язык"));
	}
	
	@Test
	@Order(3)
	void shouldReturnEmptyMap() throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("invalid.pdf");
		MultipartFile pdfDoc = new MockMultipartFile("invalid", inputStream);
		Map<String, List<String>> result = pdfDocumentHandler.parsePdfDoc(pdfDoc);
		
		assertThat(result.size(), is(0));
	}
}
