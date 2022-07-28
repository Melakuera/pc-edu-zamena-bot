package io.melakuera.tgbotzamena.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import io.melakuera.tgbotzamena.db.TelegramChatService;
import io.melakuera.tgbotzamena.enums.BotMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Обработчик колбэк запросов
 * (https://core.telegram.org/bots/api#callbackquery)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackQueryHandler {

	private final TelegramChatService dbTelegramChatService;
	
	// Точка входа
	public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
		
		String query = callbackQuery.getData();
		String chatId = callbackQuery.getMessage().getChatId().toString();
		int messageId = callbackQuery.getMessage().getMessageId();

		if (query.matches("quit_yes")) {
			
			boolean result = dbTelegramChatService.deleteChat(chatId);
			
			if (!result) {
				
				return EditMessageText.builder()
						.text(BotMessages.GROUP_NOT_APPLY_ERROR.getMessage())
						.messageId(messageId)
						.chatId(chatId)
						.build();
				
			}
			
			log.info("Чат с id {} отписался от замен", chatId);

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
