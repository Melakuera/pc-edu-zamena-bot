package io.melakuera.tgbotzamena;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import io.melakuera.tgbotzamena.services.InlineQueryHandler;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = InlineQueryHandler.class)
@Slf4j
class InlineQueryHandlerTests {
	
	@Autowired
	private InlineQueryHandler inlineQueryHandler;
	
	@Test
	void shouldReturnsEkssArticlesCorrectly_whenTodayIsBeforeSeptember() {
		
		decorator("ЭкСС", "2022-06-01", 21, () -> {
			
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ЭкСС");
			
			assertThat(result.size(), is(3));
			
			assertThat(result.get(0).getTitle(), containsString("21"));
			assertThat(result.get(1).getTitle(), containsString("20"));
			assertThat(result.get(2).getTitle(), containsString("19"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsEkssArticlesCorrectly_whenTodayIsAfterSeptember() {
		
		decorator("ЭкСС", "2023-12-04", 23, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ЭкСС");
			
			assertThat(result.size(), is(3));
			
			assertThat(result.get(0).getTitle(), containsString("23"));
			assertThat(result.get(1).getTitle(), containsString("22"));
			assertThat(result.get(2).getTitle(), containsString("21"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsSsskArticlesCorrectly_whenTodayIsBeforeSeptember() {
		
		decorator("СССК", "2022-06-01", 21, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("СССК");
			
			assertThat(result.size(), is(3));
			
			assertThat(result.get(0).getTitle(), containsString("21"));
			assertThat(result.get(1).getTitle(), containsString("20"));
			assertThat(result.get(2).getTitle(), containsString("19"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsSsskArticlesCorrectly_whenTodayIsAfterSeptember() {
		
		decorator("СССК", "2023-12-04", 23, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("СССК");
			
			assertThat(result.size(), is(3));
			
			assertThat(result.get(0).getTitle(), containsString("23"));
			assertThat(result.get(1).getTitle(), containsString("22"));
			assertThat(result.get(2).getTitle(), containsString("21"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsEsssArticlesCorrectly_whenTodayIsBeforeSeptember() {
		
		decorator("ЭССС", "2022-06-01", 21, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ЭССС");
			
			assertThat(result.size(), is(12));
			
			assertThat(result.get(0).getTitle(), containsString("1-21"));
			assertThat(result.get(1).getTitle(), containsString("2-21"));
			assertThat(result.get(2).getTitle(), containsString("3-21"));
			assertThat(result.get(3).getTitle(), containsString("4-21"));
			assertThat(result.get(4).getTitle(), containsString("1-20"));
			assertThat(result.get(5).getTitle(), containsString("2-20"));
			assertThat(result.get(6).getTitle(), containsString("3-20"));
			assertThat(result.get(7).getTitle(), containsString("4-20"));
			assertThat(result.get(8).getTitle(), containsString("1-19"));
			assertThat(result.get(9).getTitle(), containsString("2-19"));
			assertThat(result.get(10).getTitle(), containsString("3-19"));
			assertThat(result.get(11).getTitle(), containsString("4-19"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsEsssArticlesCorrectly_whenTodayIsAfterSeptember() {
		
		decorator("ЭССС", "2023-12-04", 23, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ЭССС");
			
			assertThat(result.size(), is(12));
			
			assertThat(result.get(0).getTitle(), containsString("1-23"));
			assertThat(result.get(1).getTitle(), containsString("2-23"));
			assertThat(result.get(2).getTitle(), containsString("3-23"));
			assertThat(result.get(3).getTitle(), containsString("4-23"));
			assertThat(result.get(4).getTitle(), containsString("1-22"));
			assertThat(result.get(5).getTitle(), containsString("2-22"));
			assertThat(result.get(6).getTitle(), containsString("3-22"));
			assertThat(result.get(7).getTitle(), containsString("4-22"));
			assertThat(result.get(8).getTitle(), containsString("1-21"));
			assertThat(result.get(9).getTitle(), containsString("2-21"));
			assertThat(result.get(10).getTitle(), containsString("3-21"));
			assertThat(result.get(11).getTitle(), containsString("4-21"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsPksArticlesCorrectly_whenTodayIsBeforeSeptember() {
		
		decorator("ПКС", "2022-06-01", 21, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ПКС");
			
			assertThat(result.size(), is(9));
			
			assertThat(result.get(0).getTitle(), containsString("1-21"));
			assertThat(result.get(1).getTitle(), containsString("2-21"));
			assertThat(result.get(2).getTitle(), containsString("3-21"));
			assertThat(result.get(3).getTitle(), containsString("1-20"));
			assertThat(result.get(4).getTitle(), containsString("2-20"));
			assertThat(result.get(5).getTitle(), containsString("3-20"));
			assertThat(result.get(6).getTitle(), containsString("1-19"));
			assertThat(result.get(7).getTitle(), containsString("2-19"));
			assertThat(result.get(8).getTitle(), containsString("3-19"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsPksArticlesCorrectly_whenTodayIsAfterSeptember() {
		
		decorator("ПКС", "2023-12-04", 23, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("ПКС");
			
			assertThat(result.size(), is(9));
			
			assertThat(result.get(0).getTitle(), containsString("1-23"));
			assertThat(result.get(1).getTitle(), containsString("2-23"));
			assertThat(result.get(2).getTitle(), containsString("3-23"));
			assertThat(result.get(3).getTitle(), containsString("1-22"));
			assertThat(result.get(4).getTitle(), containsString("2-22"));
			assertThat(result.get(5).getTitle(), containsString("3-22"));
			assertThat(result.get(6).getTitle(), containsString("1-21"));
			assertThat(result.get(7).getTitle(), containsString("2-21"));
			assertThat(result.get(8).getTitle(), containsString("3-21"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsKsArticlesCorrectly_whenTodayIsBeforeSeptember() {
		
		decorator("ПКС", "2022-06-01", 21, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("КС");
			
			assertThat(result.size(), is(9));
			
			assertThat(result.get(0).getTitle(), containsString("1-21"));
			assertThat(result.get(1).getTitle(), containsString("2-21"));
			assertThat(result.get(2).getTitle(), containsString("3-21"));
			assertThat(result.get(3).getTitle(), containsString("1-20"));
			assertThat(result.get(4).getTitle(), containsString("2-20"));
			assertThat(result.get(5).getTitle(), containsString("3-20"));
			assertThat(result.get(6).getTitle(), containsString("1-19"));
			assertThat(result.get(7).getTitle(), containsString("2-19"));
			assertThat(result.get(8).getTitle(), containsString("3-19"));
			
			return result;
		});
	}
	
	@Test
	void shouldReturnsKsArticlesCorrectly_whenTodayIsAfterSeptember() {
		
		decorator("ПКС", "2023-12-04", 23, () -> {
			
			List<InlineQueryResultArticle> result = inlineQueryHandler
					.getInlineQueryResultsByFaculty("КС");
			
			assertThat(result.size(), is(9));
			
			assertThat(result.get(0).getTitle(), containsString("1-23"));
			assertThat(result.get(1).getTitle(), containsString("2-23"));
			assertThat(result.get(2).getTitle(), containsString("3-23"));
			assertThat(result.get(3).getTitle(), containsString("1-22"));
			assertThat(result.get(4).getTitle(), containsString("2-22"));
			assertThat(result.get(5).getTitle(), containsString("3-22"));
			assertThat(result.get(6).getTitle(), containsString("1-21"));
			assertThat(result.get(7).getTitle(), containsString("2-21"));
			assertThat(result.get(8).getTitle(), containsString("3-21"));
			
			return result;
		});
	}
	
	// Насмотрелся питона
	private void decorator(
			String groupName,
			String instantExpected, 
			int year,
			Supplier<List<InlineQueryResultArticle>> consumer) {
		
		LocalDate mockedDate = LocalDate.parse(instantExpected);
		try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class)) {
			mockedLocalDate.when(LocalDate::now).thenReturn(mockedDate);
			
			List<InlineQueryResultArticle> result = consumer.get();
			
			logTest(groupName, result, instantExpected, year);
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


























