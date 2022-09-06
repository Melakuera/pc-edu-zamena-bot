package io.melakuera.tgbotzamena.services;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import io.melakuera.tgbotzamena.constants.BotMessages;
import lombok.RequiredArgsConstructor;

/*
 * Обработчик встроенных запросов 
 * (inline queries: https://core.telegram.org/bots/api#inline-mode)
 */
@Service
@RequiredArgsConstructor
public class InlineQueryHandler {

	// точка входа
	public BotApiMethod<?> handleInlineQuery(InlineQuery inlineQuery) {

		String query = inlineQuery.getQuery().strip();
		
		List<InlineQueryResultArticle> articles = 
				getInlineQueryResultsByFaculty(query);
		
		return AnswerInlineQuery.builder()
				.inlineQueryId(inlineQuery.getId())
				.results(articles)
				.build();
	}

	public List<InlineQueryResultArticle> getInlineQueryResultsByFaculty(
			String facultyRusName) {

		return switch (facultyRusName) {
			case "ЭкСС", "СССК" -> getInlineQueryResultsByEkssOrSssk(facultyRusName);
			case "ЭССС" -> getInlineQueryResultsByEsss(facultyRusName);
			case "ПКС", "КС" -> getInlineQueryResultsByPksOrKs(facultyRusName);
			default -> Collections.emptyList();
		};
	}
	
	// Строитель Inline-клавиатуры для групп ЭкСС или СССК
	private List<InlineQueryResultArticle> getInlineQueryResultsByEkssOrSssk(
			String facultyRusName) {
		
		Month currentMonth = LocalDate.now().getMonth();
		int currentYear = LocalDate.now().getYear() % 100;
		List<InlineQueryResultArticle> result = new ArrayList<>();
		
		// если нын. месяц года позже сентября, то надо: 22 - 21 - 20, иначе: 21 - 20 - 19
		if (isAfterSeptember(currentMonth)) {
			
			IntStream.of(currentYear, currentYear - 1, currentYear - 2).forEach(year -> {
				
				buildEkssOrSsskResultArticle(facultyRusName, result, year);
			});
			return result;
			
		}
		
		IntStream.of(currentYear - 1, currentYear - 2, currentYear - 3).forEach(year -> {
			
			buildEkssOrSsskResultArticle(facultyRusName, result, year);
		});
		return result;
	}
	
	// Строитель Inline-клавиатуры для группы ЭССС
	private List<InlineQueryResultArticle> getInlineQueryResultsByEsss(
			String facultyRusName)  {
		
		Month currentMonth = LocalDate.now().getMonth();
		int currentYear = LocalDate.now().getYear() % 100;
		List<InlineQueryResultArticle> result = new ArrayList<>();
		
		if (isAfterSeptember(currentMonth)) {
			
			IntStream.of(currentYear, currentYear - 1, currentYear - 2).forEach(year -> {
				IntStream.of(1, 2, 3, 4).forEach(num -> {
					
					buildEsssOrPksOrKsResultArticle(facultyRusName, result, year, num);
				});
			});
			return result;
		}
		
		IntStream.of(currentYear - 1 , currentYear - 2, currentYear - 3).forEach(year -> {
			IntStream.of(1, 2, 3, 4).forEach(num -> {
				
				buildEsssOrPksOrKsResultArticle(facultyRusName, result, year, num);
			});
		});
		return result;
	}
	
	// Строитель Inline-клавиатуры для группы ПКС или КС
	private List<InlineQueryResultArticle> getInlineQueryResultsByPksOrKs(
			String facultyRusName) {
		
		Month currentMonth = LocalDate.now().getMonth();
		int currentYear = LocalDate.now().getYear() % 100;
		List<InlineQueryResultArticle> result = new ArrayList<>();
		
		if (isAfterSeptember(currentMonth)) {
			
			IntStream.of(currentYear, currentYear - 1, currentYear - 2).forEach(year -> {
				IntStream.of(1, 2, 3).forEach(num -> {
					buildEsssOrPksOrKsResultArticle(facultyRusName, result, year, num);
				});
			});
			return result;
		}
		
		IntStream.of(currentYear - 1 , currentYear - 2, currentYear - 3).forEach(year -> {
			IntStream.of(1, 2, 3).forEach(num -> {
				
				buildEsssOrPksOrKsResultArticle(facultyRusName, result, year, num);
			});
		});
		return result;
	}
	
	private void buildEkssOrSsskResultArticle(String facultyRusName, List<InlineQueryResultArticle> result, int year) {
		String text = facultyRusName + " 1" + "-" + year;

		InputTextMessageContent inputContent = InputTextMessageContent.builder()
				.messageText(
						BotMessages.SUCCESS_APPLY_FACULTY + " " + text)
				.build();

		InlineQueryResultArticle article = InlineQueryResultArticle.builder()
				.title(text)
				.id(text)
				.inputMessageContent(inputContent)
				.build();

		result.add(article);
	}
	
	private void buildEsssOrPksOrKsResultArticle(String facultyRusName, List<InlineQueryResultArticle> result, int year, int num) {
		String text = facultyRusName + " " + num + "-" + year;
		
		InputTextMessageContent inputContent = InputTextMessageContent.builder()
				.messageText(
						BotMessages.SUCCESS_APPLY_FACULTY + " " + text)
				.build();

		InlineQueryResultArticle article = InlineQueryResultArticle.builder()
				.title(text)
				.id(text)
				.inputMessageContent(inputContent)
				.build();

		result.add(article);
	}
	
	private boolean isAfterSeptember(Month currentMonth) {
		return currentMonth.getValue() >= Month.SEPTEMBER.getValue();
	}
}