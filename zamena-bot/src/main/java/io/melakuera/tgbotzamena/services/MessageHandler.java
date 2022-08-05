package io.melakuera.tgbotzamena.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.melakuera.tgbotzamena.constants.BotMessages;
import io.melakuera.tgbotzamena.db.TelegramChatService;
import io.melakuera.tgbotzamena.db.ZamenaService;
import io.melakuera.tgbotzamena.telegram.ZamenaPinnerBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Обработчик обычный сообщений
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandler {

	private final TelegramChatService dbTelegramChatService;
	private final InlineKeyboardMaker inlineKeyboardMaker;
	private final GifHandler gifHandler;
	private final ZamenaService zamenaService;
	private final ZamenaHandler zamenaHandler;
	private final ZamenaPinnerBot bot;
	
	private static final String MARKDOWN = "Markdown";
	private static final String GET_ERROR = "Что-то произошло критическое: {}";
	private static final String REGEX = 
			"Выбрана группа:\\s[ЭкСС|СССК|ЭССС|КС|ПКС]{2,4}\\s[1-3]-\\d{2}";
	
	@Value("${telegram.bot-username}")
	private String botUsername;

	// Точка входа
	public BotApiMethod<?> handleMessage(Message message) {
		
		String chatId = message.getChatId().toString();
		String messageText = message.getText();
		
		if (messageText.matches("/start.*")) {
			
			var sendAnimBuilder = SendAnimation.builder()
				.caption(String.format(
							BotMessages.START_IN_GROUP))
				.parseMode(MARKDOWN);
			
			gifHandler.sendExampleGif(chatId, sendAnimBuilder);
		}
		
		// При выборе группы из колледжка 
		else if (messageText.matches(REGEX)) {
			
			// Н: Выбрана группа: ПКС 3-21 -> [Выбрана группа] , [ ПКС 3-21] -> ПКС 3-21
			String target = message.getText().split(":")[1].substring(1);
			boolean isChatExists = 
					dbTelegramChatService.updateTarget(chatId, target);
			Map<String, List<String>> groupZamena = zamenaService.getGroupZamenaByGroup(target);
			
			// если таковой чат существует
			if (isChatExists) {

				log.info("Чат с id {} поменял группу на {}", 
						chatId, target);
				
				if (groupZamena.isEmpty())
					return SendMessage.builder()
							.text(String.format(BotMessages.CONGRATULATION_IF_EXISTS, target))
							.chatId(chatId)
							.parseMode(MARKDOWN)
							.build();
				
				try {
					bot.execute(SendMessage.builder()
							.text(String.format(BotMessages.CONGRATULATION_IF_EXISTS, target))
							.chatId(chatId)
							.parseMode(MARKDOWN)
							.build());
				} catch (TelegramApiException e) {
						log.error(GET_ERROR, e.getMessage());
						Arrays.stream(e.getStackTrace()).forEach(x -> 
							log.error(x.toString()));
				}
				zamenaHandler.sendZamenaToOne(chatId, target, groupZamena);
				return null;
			}
			
			dbTelegramChatService.insertChat(chatId, target);
			
			log.info("Чат с id {} подписался на группу {}", chatId, target);
			
			if (groupZamena.isEmpty())
				return SendMessage.builder()
						.text(String.format(BotMessages.CONGRATULATION, target))
						.chatId(chatId)
						.parseMode(MARKDOWN)
						.build();
			try {
			bot.execute(SendMessage.builder()
					.text(String.format(BotMessages.CONGRATULATION, target))
					.chatId(chatId)
					.parseMode(MARKDOWN)
					.build());
			} catch (TelegramApiException e) {
				log.error(GET_ERROR, e.getMessage());
				Arrays.stream(e.getStackTrace()).forEach(x -> 
					log.error(x.toString()));
			}
			zamenaHandler.sendZamenaToOne(chatId, target, groupZamena);
			
			
			
		}
		else if (messageText.matches("/info.*")) {
			
			String target = dbTelegramChatService.getTarget(chatId);
			
			target = target.isBlank() ? 
					"не подписаны ни на одну группу" : "подписаны на " + target;
			
			return SendMessage.builder()
					.text(String.format(
							BotMessages.INFO, 
							target, botUsername.replace("_", "\\_")))
					.parseMode(MARKDOWN)
					.chatId(chatId)
					.build();
				
		}
		else if (messageText.matches("/in.*")) {
			
			String userId = message.getFrom().getId().toString();
			
			boolean isUserAddedToChat;
			try {
				isUserAddedToChat = dbTelegramChatService.addUserToChat(chatId, userId);
			} catch (Exception e) {
				return SendMessage.builder()
						.text(BotMessages.GROUP_NOT_APPLY_ERROR)
						.chatId(chatId)
						.build();
			}
			
			// Если юзер уже присутствует в списке
			if (!isUserAddedToChat)
				return SendMessage.builder()
						.text(BotMessages.MENTION_ERROR)
						.chatId(chatId)
						.build();
			
			return SendMessage.builder()
					.text(BotMessages.APPLY_MENTION)
					.chatId(chatId)
					.build();
		}
		
		else if (messageText.matches("/out.*")) {
			
			String userId = message.getFrom().getId().toString();
			
			boolean isUserRemovedFromChat;
			try {
				isUserRemovedFromChat = dbTelegramChatService.removeUserFromChat(chatId, userId);
			} catch (Exception e) {
				return SendMessage.builder()
						.text(BotMessages.GROUP_NOT_APPLY_ERROR)
						.chatId(chatId)
						.build();
			}
			
			// Если юзер отсутствует в списке
			if (!isUserRemovedFromChat)
				return SendMessage.builder()
						.text(BotMessages.MENTION_ERROR)
						.chatId(chatId)
						.build();
			
			return SendMessage.builder()
					.text(BotMessages.APPLY_MENTION)
					.chatId(chatId)
					.build();
		}
		else if (messageText.matches("/quit.*")) {
			
			var inlineKeyboardMarkup = inlineKeyboardMaker.getInlineKeyboardMarkup();
						
			return SendMessage.builder()
					.text(BotMessages.QUIT)
					.chatId(chatId)
					.replyMarkup(inlineKeyboardMarkup)
					.build();	
		}
		return null;
	}
}
