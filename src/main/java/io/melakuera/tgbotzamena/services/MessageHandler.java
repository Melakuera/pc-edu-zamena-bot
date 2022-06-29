package io.melakuera.tgbotzamena.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.melakuera.tgbotzamena.db.DbTelegramChatService;
import io.melakuera.tgbotzamena.enums.BotMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Обработчик обычный сообщений
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandler {

	private final DbTelegramChatService dbTelegramChatService;
	private final InlineKeyboardMaker inlineKeyboardMaker;
	
	private static final String CHAT_NOT_EXISTS = "Чат с id %s не существует";
	private static final String MARKDOWN = "Markdown";
	private static final String REGEX = 
			"Выбрана группа:\\s[ЭкСС|СССК|ЭССС|КС|ПКС]{2,4}\\s[1-3]-\\d{2}";
	
	@Value("${telegram.bot-username}")
	private String botUsername;
	@Value("${telegram.example-gif-url}")
	private String botExampleGifUrl;

	public BotApiMethod<?> handleMessage(Message message) {
		
		String chatId = message.getChatId().toString();
		String messageText = message.getText();
		
		if (messageText.matches("/start")) {
			return SendMessage.builder()
					.text(BotMessages.START.getMessage())
					.chatId(chatId)
					.build();
		}
		else if (messageText.matches("/start".concat(botUsername))) {
			
			return SendMessage.builder()
					.text(String.format(
							BotMessages.START_IN_GROUP.getMessage(), botExampleGifUrl))
					.parseMode(MARKDOWN)
					.chatId(chatId)
					.build();	
		}
		
		// При выборе группы из колледжка 
		else if (messageText.matches(REGEX)) {
			
			// Н: Выбрана группа: ПКС 3-21 -> [Выбрана группа] , [ ПКС 3-21] -> ПКС 3-21
			String target = message.getText().split(":")[1].substring(1);
			boolean isChatExists = 
					dbTelegramChatService.updateTarget(chatId, target);
			
			// если таковой чат существует
			if (isChatExists) {
				return SendMessage.builder()
						.text(String.format(
								BotMessages.CONGRATULATION_IF_EXISTS.getMessage(), target))
						.chatId(chatId)
						.parseMode(MARKDOWN)
						.build();
			}
			
			dbTelegramChatService.insertChat(chatId, target);
			
			return SendMessage.builder()
					.text(String.format(BotMessages.CONGRATULATION.getMessage(), target))
					.chatId(chatId)
					.parseMode(MARKDOWN)
					.build();
		}
		else if (messageText.matches("/in".concat(botUsername))) {
			
			String userId = message.getFrom().getId().toString();
			
			boolean didUserAddToChat;
			try {
				didUserAddToChat = dbTelegramChatService.addUserToChat(chatId, userId);
			} catch (Exception e) {
				log.warn(CHAT_NOT_EXISTS, chatId);
				return null;
			}
			
			// Если юзер уже присутствует в списке
			if (!didUserAddToChat)
				return SendMessage.builder()
						.text(BotMessages.MENTION_ERROR.getMessage())
						.chatId(chatId)
						.build();
			
			return SendMessage.builder()
					.text(BotMessages.APPLY_MENTION.getMessage())
					.chatId(chatId)
					.build();
		}
		
		else if (messageText.matches("/out".concat(botUsername))) {
			
			String userId = message.getFrom().getId().toString();
			
			boolean didUserRemoveFromChat;
			try {
				didUserRemoveFromChat = dbTelegramChatService.removeUserFromChat(chatId, userId);
			} catch (Exception e) {
				log.warn(CHAT_NOT_EXISTS, chatId);
				return null;
			}
			
			// Если юзер отсутствует в списке
			if (!didUserRemoveFromChat)
				return SendMessage.builder()
						.text(BotMessages.MENTION_ERROR.getMessage())
						.chatId(chatId)
						.build();
			
			return SendMessage.builder()
					.text(BotMessages.APPLY_MENTION.getMessage())
					.chatId(chatId)
					.build();
		}
		else if (messageText.matches("/info".concat(botUsername)) ||
					messageText.matches("/info")) {
			
			String target;
			try {
				target = "на" + dbTelegramChatService.getTarget(chatId);
			} catch (Exception e) {
				log.warn(CHAT_NOT_EXISTS, chatId);
				target = "ни на одну группу";
			}
			return SendMessage.builder()
					.text(String.format(
							BotMessages.INFO.getMessage(), 
								target, botUsername, botExampleGifUrl))
					.parseMode(MARKDOWN)
					.chatId(chatId)
					.build();	
		}
		else if (messageText.matches("/quit".concat(botUsername))) {
			
			var inlineKeyboardMarkup = inlineKeyboardMaker.getInlineKeyboardMarkup();
			
			return SendMessage.builder()
					.text(String.format(
							BotMessages.QUIT.getMessage(), botExampleGifUrl))
					.chatId(chatId)
					.replyMarkup(inlineKeyboardMarkup)
					.build();	
		}
		return null;
	}
}
