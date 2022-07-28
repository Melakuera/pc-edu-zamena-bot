package io.melakuera.tgbotzamena.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.melakuera.tgbotzamena.db.TelegramChat;
import io.melakuera.tgbotzamena.db.TelegramChatService;
import io.melakuera.tgbotzamena.db.ZamenaService;
import io.melakuera.tgbotzamena.telegram.ZamenaPinnerBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Обработчик замен
@Service
@RequiredArgsConstructor
@Slf4j
public class ZamenaHandler {

	private final PdfDocumentHandler documentHandler;
	private final ZamenaPinnerBot bot;	
	private final TelegramChatService dbTelegramChatService;
	private final ZamenaService zamenaService;
	
	private static final String MARKDOWN = "Markdown";
	private static final String GET_ERROR = "Что-то произошло критическое: {}";
	
	/**
	 * Когда из GUI приложение доставлено новая замена (PDF файл) 
	 * @param zamenaFile PDF файл
	 * @return ответ
	 */
	public Map<String, String> onZamenaRecieved(final MultipartFile zamenaFile) {
		
		log.info("Обработчик замен начал свою работу...");
		
		// Парсим PDF файл
		Map<String, List<String>> zamenaData = documentHandler.parsePdfDoc(zamenaFile);
		Map<String, String> response = new HashMap<>();
		
		if (zamenaData.size() == 0) {
			response.put("status", "fail");
			response.put("timestamp", LocalDateTime.now().toString());
			response.put("massege", "PDF-Документ невалидный");	
			
			log.info("Обработчик замен завершил свою работу");
			
			return response;
			
		}
		zamenaService.putZamena(zamenaData);
		
		sendZamena(zamenaData);
		
		response.put("status", "ok");
		response.put("timestamp", LocalDateTime.now().toString());
		response.put("massege", "PDF-Документ успешно загружен");
		response.put("size", Long.toString(zamenaFile.getSize()));
		response.put("filename", zamenaFile.getName());
		
		log.info("Обработчик замен завершил свою работу");
		
		return response;
	}

	/*
	 *  Отправляет по всем телеграм-группам свежую распарсенную замену
	 *  
	 *  zamenaData может быть след. формата:
	 *  {
	 *  	head=[ЗАМЕНА НА ПЯТНИЦУ – 01 ИЮЛЯ (ЗНАМЕНАТЕЛЬ) 2022г],
	 *  	ЭкСС 1-21=[3п Физвоспитание Теркулова У.А.],
	 *  	ЭССС 1-21=[2п Физвоспитание Теркулова У.А. 43, 3п Математика Абышов И.С.],
	 *  	ЭССС 2-21=[4п Математика Абышов И.С. 43],
	 *  	ЭССС 3-21=[5п Математика Лемякина Л.Б.],
	 *  	ПКС 1-21=[4п Кырг.язык Абдыкеримова/Каримова 65/37]
	 *  }
	 */
	public void sendZamena(Map<String, List<String>> zamenaData) {
		
		log.info("Начата отправка замен");
		
		// Получает все телеграм-группы в БД
		List<TelegramChat> chats = dbTelegramChatService.findAll();
		
		// Проходится по телеграм-группам
		for (TelegramChat chat : chats) {
			
			String chatId = chat.getTelegramChatId(); // id телеграм-группы
			String target = chat.getTarget(); // группа на которую подписана телеграм-группа
			String actualRecentPinnedMessageText = chat.getRecentPinnedMessageText(); // last закрепленное сообщение ботом
			List<String> groupZamena = zamenaData.get(target); // замена для данной группы (target группа, на которую подписана телеграм-группа)
			
			if (groupZamena == null) continue; // если группа, на которую подписана телеграм-группа не существует в zamenaData,
										       // то значит, что на замены на данную группы нету
			
			// TODO
			int actualRecentPinnedMessageDayOfMonth = 
					getDayOfMonthByHeadText(actualRecentPinnedMessageText); // получаем дату 
			int zamenaMessageDayOfMonth = 
					getDayOfMonthByHeadText(zamenaData.get("head").toString());
			
			if (actualRecentPinnedMessageDayOfMonth != zamenaMessageDayOfMonth) { // сверяемся с датами 
				
				// Извлекаем данные новой замены (groupZamena) лишь данной группы (target)
				StringBuilder groupZamenaResult = new StringBuilder(); // Н: [2п Физвоспитание Теркулова У.А. 43, 3п Математика Абышов И.С.], -> 
				for (String zamena : groupZamena) {					   // 2п Физвоспитание Теркулова У.А. 43 
					groupZamenaResult.append(zamena + "\n");		   // 3п Математика Абышов И.С.
				}
				
				// Извлекаем заголовок ( ЗАМЕНА НА ПЯТНИЦУ – 01 ИЮЛЯ (ЗНАМЕНАТЕЛЬ) 2022г )
				String headText = zamenaData.get("head").toString();
				String headTextResult = headText
						.substring(1, headText.length() - 1) + "\n";
				
				// Складываем тексты:
				String result = (headTextResult + groupZamenaResult).strip();

				// Отправляем сообщение
				var messageZamenaData = SendMessage.builder()
						.text(result)
						.chatId(chatId)
						.build();	
				Message sendedMessage;
				try {
					sendedMessage = bot.execute(messageZamenaData);
				} catch (TelegramApiException e) {
					log.error(GET_ERROR, e.getMessage());
					Arrays.stream(e.getStackTrace()).forEach(x -> 
						log.error(x.toString()));
					return;
				}
				
				log.info("Замена разослано чату с id {} и с содержимым: \n{}", 
						chat.getTelegramChatId(), result);
				
				// Закрепляем сообщение
				var pinChatMessage = PinChatMessage.builder()
						.chatId(chatId)
						.messageId(sendedMessage.getMessageId())
						.build();
				try {
					bot.execute(pinChatMessage);
				} catch (TelegramApiException e) {
					log.error(GET_ERROR, e.getMessage());
					Arrays.stream(e.getStackTrace()).forEach(x -> 
						log.error(x.toString()));
					return;
				}
				
				log.info("Сообщение c id {} содержащий новую "
						+ "замены, закреплена чату с id {}", 
							sendedMessage.getMessageId(), chatId);
				
				// Заменяем last закрепленное сообщение
				try {
				dbTelegramChatService
					.updateRecentPinnedMessageText(chatId, headTextResult);
				} catch (Exception e) {
					log.warn(e.getMessage());
					Arrays.stream(e.getStackTrace()).forEach(x -> log.warn(x.toString()));
					return;
				}
				
				// Упоминаем юзеров
				mentionUsers(chatId, chat.getSubscribedUsersId());
				
				log.info("Замены отправлены");
			}
		}
	}
	
	/**
	 * Упоминает всех юзеров в заданной телеграм-группе
	 * @param chatId id телеграм-группы (не должно быть null)
	 * @param subscribedUsersId список id юзеров, подписавшиеся на замены группы (не должно быть null)
	 */
	private void mentionUsers(String chatId, List<String> subscribedUsersId) {
		
		StringBuilder sb = new StringBuilder();
		
		subscribedUsersId.forEach(userId -> {
			
			try {
				ChatMember chatMember = bot.execute(GetChatMember.builder()
						.chatId(chatId)
						.userId(Long.parseLong(userId))
						.build());
				String chatMemberFirstName = chatMember.getUser().getFirstName();
				
				sb.append(String.format(
						"[%s](tg://user?id=%s) ", chatMemberFirstName, userId));
				
				bot.execute(SendMessage.builder()
						.text(sb.toString())
						.chatId(chatId)
						.parseMode(MARKDOWN)
						.build());
				
			} catch (NumberFormatException | TelegramApiException e) {
				log.error("Что пошло не так: {}", e.getMessage());
				Arrays.stream(e.getStackTrace()).forEach(x -> 
					log.error(x.toString()));
			}
		});
		log.info("В чате c id {} упомянуто {} юзеров", 
				chatId, subscribedUsersId.size());
	}
	
	// Извлекает день из headText
	// Н: ЗАМЕНА НА ПЯТНИЦУ – 01 ИЮЛЯ (ЗНАМЕНАТЕЛЬ) 2022г -> 01 -> 1
	private int getDayOfMonthByHeadText(String headText) {
		
		Pattern patter = Pattern.compile("\\d{2}");
		Matcher matcher = patter.matcher(headText);
		int dayOfMonth = -1;
		if (matcher.find()) {
		    String t = headText.substring(matcher.start(), matcher.end());
		    dayOfMonth = Integer.parseInt(t);
		}
		return dayOfMonth;
	}

	
}
