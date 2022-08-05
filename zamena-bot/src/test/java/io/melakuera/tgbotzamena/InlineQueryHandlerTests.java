package io.melakuera.tgbotzamena;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import com.google.common.collect.Lists;

import io.melakuera.tgbotzamena.services.InlineQueryHandler;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = InlineQueryHandler.class)
@Slf4j
class InlineQueryHandlerTests {

	@Autowired
	private InlineQueryHandler inlineQueryHandler;

	@ParameterizedTest
	@CsvSource({
			"2022-06-01, 21", // Новенькие еще не пришли, а 19-годники еще не закончили обучение и все это происходит в 2022 году (21, 20, 19)
			"2023-02-02, 22", // Новенькие не пришли, а 20-годники еще не закончили и это в 2023 году (22, 21, 20)
			"2022-10-03, 22", // Новенькие не пришли, а 19-годники закончили обучение и это в 2022 году (22, 21, 20)
			"2023-12-04, 23"  // Новенькие не пришли, а 20-годники закончили обучение и это в 2023 году (23, 22, 21)
	})
	@DisplayName("ЭкСС")
<<<<<<< HEAD
	void shouldReturnsEkssArticlesCorrectly(
=======
	void shouldReturnsEkssArticlesCorrectly_whenTodayIsBeforeSeptember(
>>>>>>> ab804fd0422c26f1b66b699054eca3dfd7ebd3ca
			String instantExpected, int mockYear) {

		LocalDate mockedDate = LocalDate.parse(instantExpected);

		try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class)) {
			mockedLocalDate.when(LocalDate::now).thenReturn(mockedDate);

			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ЭкСС");
			
			assertThat(result.size(), is(3));
			int tempYear = mockYear;

			for (var el : result) {
				assertThat(el.getTitle(), containsString(Integer.toString(tempYear)));
				--tempYear;
			}
			
			// logging
			logTest("ЭкСС", result, instantExpected, mockYear);
		}
	}
	
	@ParameterizedTest
	@CsvSource({
			"2022-06-01, 21",
			"2023-02-02, 22",
			"2022-10-03, 22",
			"2023-12-04, 23"
	})
	@DisplayName("СССК")
<<<<<<< HEAD
	void shouldReturnsSsskArticlesCorrectly(
=======
	void shouldReturnsSsskArticlesCorrectly_whenTodayIsBeforeSeptember(
>>>>>>> ab804fd0422c26f1b66b699054eca3dfd7ebd3ca
			String instantExpected, int mockYear) {

		LocalDate mockedDate = LocalDate.parse(instantExpected);

		try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class)) {
			mockedLocalDate.when(LocalDate::now).thenReturn(mockedDate);

			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("СССК");
			
			assertThat(result.size(), is(3));
			int tempYear = mockYear;

			for (var el : result) {
				assertThat(el.getTitle(), containsString(Integer.toString(tempYear)));
				--tempYear;
			}
			
			logTest("СССК", result, instantExpected, mockYear);
		}
	}
	
	@ParameterizedTest
	@CsvSource({
			"2022-06-01, 21",
			"2023-02-02, 22",
			"2022-10-03, 22",
			"2023-12-04, 23"
	})
	@DisplayName("ПКС")
<<<<<<< HEAD
	void shouldReturnsPksArticlesCorrectly(
=======
	void shouldReturnsPksArticlesCorrectly_whenTodayIsBeforeSeptember(
>>>>>>> ab804fd0422c26f1b66b699054eca3dfd7ebd3ca
			String instantExpected, int mockYear) {

		LocalDate mockedDate = LocalDate.parse(instantExpected);

		try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class)) {
			mockedLocalDate.when(LocalDate::now).thenReturn(mockedDate);

			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ПКС");
			
			assertThat(result.size(), is(7));
			int tempYear = mockYear; // ПКС 3-21 -> 21
			int tempGroupNumber = 1; // ПКС 3-21 -> 3
			
			// [ПКС 1-21, ПКС 2-21, ПКС 3-21, ПКС 1-20, ПКС 2-20, ПКС 1-19, ПКС 2-19] -> [ПКС 1-21, ПКС 2-21, ПКС 3-21]
			for (var el : result.stream().limit(3).toList()) {
				assertThat(el.getTitle(), containsString(
						Integer.toString(tempGroupNumber)+"-"+Integer.toString(tempYear)));
				tempGroupNumber++;
			}
			
			tempYear = --mockYear; // ПКС 3-21 -> 21
			tempGroupNumber = 1; // ПКС 3-21 -> 3
			// [ПКС 1-21, ПКС 2-21, ПКС 3-21, ПКС 1-20, ПКС 2-20, ПКС 1-19, ПКС 2-19] -> [ПКС 1-20, ПКС 2-20, ПКС 1-19, ПКС 2-19] -> [[ПКС 1-20, ПКС 2-20], [ПКС 1-19, ПКС 2-19]]
			List<List<InlineQueryResultArticle>> chunks = Lists.partition(result.stream().skip(3).toList(), 2);
			for (var chunk : chunks) {
				for (var el : chunk) {
					assertThat(el.getTitle(), containsString(
							Integer.toString(tempGroupNumber)+"-"+Integer.toString(tempYear)));
					tempGroupNumber++;
				}
				tempYear = --mockYear;
				tempGroupNumber = 1;
			}
			
			logTest("ПКС", result, instantExpected, mockYear);
		}
	}
	
	// Не хочу в один тест выносить, так красивее!
	@ParameterizedTest
	@CsvSource({
			"2022-06-01, 21",
			"2023-02-02, 22",
			"2022-10-03, 22",
			"2023-12-04, 23"
	})
	@DisplayName("КС")
<<<<<<< HEAD
	void shouldReturnsKsArticlesCorrectly(
=======
	void shouldReturnsKsArticlesCorrectly_whenTodayIsBeforeSeptember(
>>>>>>> ab804fd0422c26f1b66b699054eca3dfd7ebd3ca
			String instantExpected, int mockYear) {

		LocalDate mockedDate = LocalDate.parse(instantExpected);

		try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class)) {
			mockedLocalDate.when(LocalDate::now).thenReturn(mockedDate);

			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("КС");
			
			assertThat(result.size(), is(7));
			int tempYear = mockYear;
			int tempGroupNumber = 1;
			
			for (var el : result.stream().limit(3).toList()) {
				assertThat(el.getTitle(), containsString(
						Integer.toString(tempGroupNumber)+"-"+Integer.toString(tempYear)));
				tempGroupNumber++;
			}
			
			tempYear = --mockYear;
			tempGroupNumber = 1; 
			List<List<InlineQueryResultArticle>> chunks = Lists.partition(result.stream().skip(3).toList(), 2);
			
			for (var chunk : chunks) {
				for (var el : chunk) {
					assertThat(el.getTitle(), containsString(
							Integer.toString(tempGroupNumber)+"-"+Integer.toString(tempYear)));
					tempGroupNumber++;
				}
				tempYear = --mockYear;
				tempGroupNumber = 1;
			}
			
			logTest("КС", result, instantExpected, mockYear);
		}
	}
	
	@ParameterizedTest
	@CsvSource({
			"2022-06-01, 21",
			"2023-02-02, 22",
			"2022-10-03, 22",
			"2023-12-04, 23"
	})
	@DisplayName("ЭССС")
<<<<<<< HEAD
	void shouldReturnsEsssArticlesCorrectly(
=======
	void shouldReturnsEsssArticlesCorrectly_whenTodayIsBeforeSeptember(
>>>>>>> ab804fd0422c26f1b66b699054eca3dfd7ebd3ca
			String instantExpected, int mockYear) {

		LocalDate mockedDate = LocalDate.parse(instantExpected);

		try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class)) {
			mockedLocalDate.when(LocalDate::now).thenReturn(mockedDate);

			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ЭССС");
			
			assertThat(result.size(), is(12));
			
			int tempYear = mockYear; 
			int tempGroupNumber = 1; 
			List<List<InlineQueryResultArticle>> chunks = Lists.partition(result, 4);
			
			for (var chunk : chunks) {
				for (var el : chunk) {
					assertThat(el.getTitle(), containsString(
							Integer.toString(tempGroupNumber)+"-"+Integer.toString(tempYear)));
					tempGroupNumber++;
				}
				tempYear = --mockYear;
				tempGroupNumber = 1;
			}
			
			logTest("ЭССС", result, instantExpected, mockYear);
		}
	}
	
	private void logTest(
			String facultyType, List<InlineQueryResultArticle> result, 
			String instantExpected, int mockYear) {
		
		log.info(facultyType+":");
		result.forEach(el -> log.info("instantExpected: "+instantExpected+", mockYear: "+
				mockYear+", Element's title: "+el.getTitle()));
	}
}