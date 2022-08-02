package io.melakuera.tgbotzamena.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

		String query = inlineQuery.getQuery();
		
		List<InlineQueryResultArticle> articles = 
				getInlineQueryResultsByFaculty(query);

		return AnswerInlineQuery.builder()
				.inlineQueryId(inlineQuery.getId())
				.results(articles)
				.cacheTime(24 * 60 * 60) // 1 день
				.build();
	}

	private List<InlineQueryResultArticle> getInlineQueryResultsByFaculty(
			String facultyRusName) {

		return switch (facultyRusName) {
			case "ЭкСС", "СССК" -> getInlineQueryResultsByEKSSOrSSSK(facultyRusName);
			case "ЭССС" -> getInlineQueryResultsByESSS(facultyRusName);
			case "ПКС", "КС" -> getInlineQueryResultsByPKSOrKS(facultyRusName);
			default -> Collections.emptyList();
		};
	}
	
	// Строитель Inline-клавиатуры для групп ЭкСС или СССК
	private List<InlineQueryResultArticle> getInlineQueryResultsByEKSSOrSSSK(
			String facultyRusName) {
		
		int currentMonth = LocalDate.now().getMonthValue();
		int currentYear = LocalDate.now().getYear() % 100;
		List<InlineQueryResultArticle> result = new ArrayList<>();
		
		if (currentMonth >= 9) {
			
			for (int i = 0; i <= 2; i++) {

				String text = facultyRusName + " 1" + "-" + (currentYear - i);

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
			return result;
			
		}
		for (int i = 1; i <= 3; i++) {

			String text = facultyRusName + " 1" + "-" + (currentYear - i);

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
		return result;
	}
	
	// Строитель Inline-клавиатуры для группы ЭССС
	private List<InlineQueryResultArticle> getInlineQueryResultsByESSS(
			String facultyRusName)  {
		
		int currentMonth = LocalDate.now().getMonthValue();
		int currentYear = LocalDate.now().getYear() % 100;
		List<InlineQueryResultArticle> result = new ArrayList<>();
		
		if (currentMonth >= 9) {
			for (int i = 0; i <= 2; i++)
				for (int j = 1; j <= 4; j++) {
	
					String text = facultyRusName + " " + j + "-" + (currentYear - i);
	
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
			return result;
		} 
		for (int i = 1; i <= 3; i++)
			for (int j = 1; j <= 4; j++) {

				String text = facultyRusName + " " + j + "-" + (currentYear - i);

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
		return result;
	}
	
	// Строитель Inline-клавиатуры для группы ПКС или КС
	private List<InlineQueryResultArticle> getInlineQueryResultsByPKSOrKS(
			String facultyRusName) {
		
		int currentMonth = LocalDate.now().getMonthValue();
		int currentYear = LocalDate.now().getYear() % 100;
		List<InlineQueryResultArticle> result = new ArrayList<>();
		
		// если нын. месяц года позже сентября, то надо: 22 - 21 - 20, иначе: 21 - 20 - 19
		if (currentMonth >= 9) {
			for (int i = 1; i <= 3; i++) {
				
				String text = facultyRusName + " " + i + "-" + currentYear;
		
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
			
			for (int i = 1; i <= 2; i++)
				for (int j = 1; j <= 2; j++) {
					String text = facultyRusName + " " + j + "-" + (currentYear - i);
		
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
			return result;
			
		}
		for (int i = 1; i <= 3; i++) {
	
			String text = facultyRusName + " " + i + "-" + (currentYear - 1) ;
	
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
		
		for (int i = 2; i <= 3; i++)
			for (int j = 1; j <= 2; j++) {
				String text = facultyRusName + " " + j + "-" + (currentYear - i);
	
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
		return result;
	}
}