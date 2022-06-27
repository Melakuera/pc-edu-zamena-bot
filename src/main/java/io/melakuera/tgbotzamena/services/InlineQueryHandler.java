package io.melakuera.tgbotzamena.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultGif;

import io.melakuera.tgbotzamena.enums.BotMessages;
import io.melakuera.tgbotzamena.enums.FacultyType;
import lombok.RequiredArgsConstructor;

/*
 * Обработчик встроенных запросов 
 * (inline queries: https://core.telegram.org/bots/api#inline-mode)
 */
@Service
@RequiredArgsConstructor
public class InlineQueryHandler {

	public BotApiMethod<?> handleInlineQuery(InlineQuery inlineQuery) {

		String query = inlineQuery.getQuery();
		boolean result = isValidFaculty(query);
		AnswerInlineQuery answerInlineQuery;

		if (result) {

			List<InlineQueryResultArticle> articles = getInlineQueryResultsByFaculty(query);

			answerInlineQuery = AnswerInlineQuery.builder()
					.inlineQueryId(inlineQuery.getId())
					.results(articles)
					.build();

			return answerInlineQuery;
		}

		InlineQueryResultGif gif = InlineQueryResultGif.builder()
				.id("null")
				.title("Не найдено")
				.gifUrl("https://otvet.imgsmail.ru/download/19204353_9a643b738d79f7f9b7f8e3e776c509f3_800.gif")
				.build();
		
//		InlineQueryResultArticle article = InlineQueryResultArticle.builder()
//				.title("Не найдено")
//				.id("null")
//				.inputMessageContent(InputTextMessageContent.builder()
//						.messageText("Повторите попытку")
//						.build())
//				.build();
		
		return AnswerInlineQuery.builder()
				.inlineQueryId(inlineQuery.getId())
				.result(gif)
				.build();
	}

	private boolean isValidFaculty(String facultyRusName) {

		String[] facultyTypeRusNames = getFacultyTypeRusNames();

		boolean result = Arrays.stream(facultyTypeRusNames).anyMatch(facultyRusName::equals);
		
		return result ? true : false;
	}

	public List<InlineQueryResultArticle> getInlineQueryResultsByFaculty(String facultyRusName) {

		int currentYear = LocalDate.now().getYear() % 100;
		List<InlineQueryResultArticle> results = new ArrayList<>();

		if (facultyRusName.equals(FacultyType.EKSS.getRusName())
				|| facultyRusName.equals(FacultyType.SSSK.getRusName())) {

			for (int i = 1; i <= 3; i++) {

				String text = facultyRusName + " 1" + "-" + (currentYear - i);

				InputTextMessageContent inputContent = InputTextMessageContent.builder()
						.messageText(
								BotMessages.SUCCESS_APPLY_FACULTY.getMessage() + " " + text)
						.build();

				InlineQueryResultArticle article = InlineQueryResultArticle.builder()
						.title(text)
						.id(text)
						.inputMessageContent(inputContent)
						.build();

				results.add(article);
			}
		} else if (facultyRusName.equals(FacultyType.ESSS.getRusName())) {

			for (int i = 1; i <= 3; i++)
				for (int j = 1; j <= 4; j++) {

					String text = facultyRusName + " " + j + "-" + (currentYear - i);

					InputTextMessageContent inputContent = InputTextMessageContent.builder()
							.messageText(
									BotMessages.SUCCESS_APPLY_FACULTY.getMessage() + " " + text)
							.build();

					InlineQueryResultArticle article = InlineQueryResultArticle.builder()
							.title(text)
							.id(text)
							.inputMessageContent(inputContent)
							.build();

					results.add(article);
				}
		} else if (facultyRusName.equals(FacultyType.PKS.getRusName())
				|| facultyRusName.equals(FacultyType.KS.getRusName())) {

			for (int i = 1; i <= 3; i++) {

				String text = facultyRusName + " " + i + "-" + (currentYear - 1) ;

				InputTextMessageContent inputContent = InputTextMessageContent.builder()
						.messageText(
								BotMessages.SUCCESS_APPLY_FACULTY.getMessage() + " " + text)
						.build();

				InlineQueryResultArticle article = InlineQueryResultArticle.builder()
						.title(text)
						.id(text)
						.inputMessageContent(inputContent)
						.build();

				results.add(article);
			}
			
			for (int i = 2; i <= 3; i++)
				for (int j = 1; j <= 2; j++) {
					String text = facultyRusName + " " + j + "-" + (currentYear - i);
	
					InputTextMessageContent inputContent = InputTextMessageContent.builder()
							.messageText(
									BotMessages.SUCCESS_APPLY_FACULTY.getMessage() + " " + text)
							.build();
	
					InlineQueryResultArticle article = InlineQueryResultArticle.builder()
							.title(text)
							.id(text)
							.inputMessageContent(inputContent)
							.build();
	
					results.add(article);
				}
		}
		return results;
	}

	public boolean isValidQuery(String query) {

		int currentYear = LocalDate.now().getYear();

		// Например ПКС 3-21
		String[] facultyTypeRusNames = getFacultyTypeRusNames();
		String facultyRusName = query.split(" ")[0]; // ПКС
		int courseYear = Integer.parseInt(query.split(" ")[0].split("-")[0]); // 3
		int inputYear = Integer.parseInt(query.split(" ")[0].split("-")[1]); // 21

		if (Arrays.stream(facultyTypeRusNames).anyMatch(facultyRusName::equals) && (courseYear > 0 && courseYear <= 3)
				&& (inputYear > courseYear - 3 && inputYear <= currentYear)) {
			return true;
		}
		return false;
	}

	private String[] getFacultyTypeRusNames() {

		FacultyType[] facultyTypes = FacultyType.values();
		String[] facultyTypeNames = new String[facultyTypes.length];

		for (int i = 0; i < facultyTypes.length; i++) {
			facultyTypeNames[i] = facultyTypes[i].getRusName();
		}
		return facultyTypeNames;
	}
}