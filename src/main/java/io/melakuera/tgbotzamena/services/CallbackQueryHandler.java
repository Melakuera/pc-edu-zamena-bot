package io.melakuera.tgbotzamena.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import io.melakuera.tgbotzamena.db.DbTelegramChatService;
import io.melakuera.tgbotzamena.enums.BotMessages;
import lombok.RequiredArgsConstructor;

/*
 * Обработчик колбэк запросов
 * (https://core.telegram.org/bots/api#callbackquery)
 */
@Service
@RequiredArgsConstructor
public class CallbackQueryHandler {

	private final DbTelegramChatService dbTelegramChatService;
	
	public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
		
		String query = callbackQuery.getData();
		String chatId = callbackQuery.getMessage().getChatId().toString();
		int messageId = callbackQuery.getMessage().getMessageId();
		
		if (query.matches("quit_yes")) {
			
			boolean result = dbTelegramChatService.deleteChat(chatId);
			
			if (!result) {
				
				return EditMessageText.builder()
						.text(BotMessages.QUIT_YES_ERROR.getMessage())
						.messageId(messageId)
						.chatId(chatId)
						.build();
				
			}
			return EditMessageText.builder()
					.text(BotMessages.QUIT_YES.getMessage())
					.messageId(messageId)
					.chatId(chatId)
					.build();
		}
		else if (query.matches("quit_no")) {
			
			return EditMessageText.builder()
					.text(BotMessages.QUIT_NO.getMessage())
					.messageId(messageId)
					.chatId(chatId)
					.build();
		}
		return null;
	}

}
