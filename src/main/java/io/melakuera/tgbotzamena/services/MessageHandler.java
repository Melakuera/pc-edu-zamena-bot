package io.melakuera.tgbotzamena.services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import io.melakuera.tgbotzamena.db.DbTelegramChatService;
import io.melakuera.tgbotzamena.enums.BotMessages;
import io.melakuera.tgbotzamena.telegram.ZamenaPinnerBot;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandler {

	private final PdfDocumentHandler documentHandler;
	private final ZamenaPinnerBot bot;	
	private final DbTelegramChatService dbTelegramChatService;
	private final InlineKeyboardMaker inlineKeyboardMaker;
	
	private static final String regex = 
			"Выбрана группа:\\s[ЭкСС|СССК|ЭССС|КС|ПКС]{2,4}\\s[1-3]-\\d{2}";
	
	@Value("${telegram.bot-username}")
	private String botUsername;
	@Value("${telegram.example-gif-url}")
	private String botExampleGifUrl;
	
	@Async
	// @Scheduled(cron = "0 08-15 23 * * *")
	public void getCurrentZamena() throws IOException {
		
		log.info("Executing getCurrentZamena()...");
		
		List<String> data = documentHandler.getZamenaDataByGroup();
		
		
		
	}

	@SneakyThrows
	public BotApiMethod<?> handleMessage(Message message) {
		
		String inputChatId = message.getChatId().toString();
		String messageText = message.getText();
		
		if (messageText.matches("/start")) {
			return SendMessage.builder()
					.text(BotMessages.START.getMessage())
					.chatId(inputChatId)
					.build();	
		}
		else if (messageText.matches("/start".concat(botUsername))) {
			
			return SendMessage.builder()
					.text(String.format(
							BotMessages.START_IN_GROUP.getMessage(), botExampleGifUrl))
					.parseMode("Markdown")
					.chatId(inputChatId)
					.build();	
		}
		else if (messageText.matches(regex)) {
			
			// Выбрана группа: ПКС 3-21 -> [Выбрана группа] , [ ПКС 3-21] -> ПКС 3-21
			String target = message.getText().split(":")[1].substring(1);
			boolean ifChatExistsThenUpdateTarget = 
					dbTelegramChatService.ifChatExistsThenUpdateTarget(inputChatId, target);
			
			if (ifChatExistsThenUpdateTarget) {
				return SendMessage.builder()
						.text(String.format(
								BotMessages.CONGRATULATION_IF_EXISTS.getMessage(), target))
						.chatId(inputChatId)
						.build();
			}
			
			dbTelegramChatService.insertChat(inputChatId, target);
			
			return SendMessage.builder()
					.text(String.format(BotMessages.CONGRATULATION.getMessage(), target))
					.chatId(inputChatId)
					.build();
		}
		else if (messageText.matches("/in".concat(botUsername))) {
			
			String userId = message.getFrom().getId().toString();
			
			try {
				dbTelegramChatService.insertUserByChat(inputChatId, userId);
			} catch (Exception e) {
				// Если юзер уже присутствует в списке
				return SendMessage.builder()
						.text(BotMessages.MENTION_ERROR.getMessage())
						.chatId(inputChatId)
						.build();
			}
			return SendMessage.builder()
					.text(BotMessages.APPLY_MENTION.getMessage())
					.chatId(inputChatId)
					.build();
		}
		
		else if (messageText.matches("/out".concat(botUsername))) {
			
			String userId = message.getFrom().getId().toString();
			
			try {
				dbTelegramChatService.removeUserByChat(inputChatId, userId);
			} catch (Exception e) {
				// Если юзер отсутствует в списке
				return SendMessage.builder()
						.text(BotMessages.MENTION_ERROR.getMessage())
						.chatId(inputChatId)
						.build();
			}
			return SendMessage.builder()
					.text(BotMessages.APPLY_MENTION.getMessage())
					.chatId(inputChatId)
					.build();
		}
		else if (messageText.matches("/info".concat(botUsername)) ||
					messageText.matches("/info")) {
			
			String target;
			try {
				target = dbTelegramChatService.getTargetByChatId(inputChatId);
			} catch (Exception e) {
				target = "никакую группу";
			}
			return SendMessage.builder()
					.text(String.format(
							BotMessages.INFO.getMessage(), target, botExampleGifUrl))
					.parseMode("Markdown")
					.chatId(inputChatId)
					.build();	
		}
		else if (messageText.matches("/quit".concat(botUsername))) {
			
			var inlineKeyboardMarkup = inlineKeyboardMaker.getInlineKeyboardMarkup();
			
			return SendMessage.builder()
					.text(String.format(
							BotMessages.QUIT.getMessage(), botExampleGifUrl))
					.chatId(inputChatId)
					.replyMarkup(inlineKeyboardMarkup)
					.build();	
		}
		return null;
	}
}
