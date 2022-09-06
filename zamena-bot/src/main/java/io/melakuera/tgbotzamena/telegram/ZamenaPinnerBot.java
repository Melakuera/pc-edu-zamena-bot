package io.melakuera.tgbotzamena.telegram;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import io.melakuera.tgbotzamena.services.CallbackQueryHandler;
import io.melakuera.tgbotzamena.services.InlineQueryHandler;
import io.melakuera.tgbotzamena.services.MessageHandler;

// Сам бот
@Component
public class ZamenaPinnerBot extends SpringWebhookBot {

	@Value("${telegram.webhook-path}")
    private String webhookpath;
    @Value("${telegram.bot-name}")
    private String botName;
    @Value("${telegram.bot-token}")
    private String botToken;
    
	private final MessageHandler messageHandler;
	private final InlineQueryHandler inlineQueryHandler;
	private final CallbackQueryHandler callbackQueryHandler;
	
	public ZamenaPinnerBot(
			SetWebhook setWebhook, 
			@Lazy MessageHandler messageHandler,
			InlineQueryHandler inlineQueryHandler,
			CallbackQueryHandler callbackQueryHandler) {
		
		super(setWebhook);
		this.messageHandler = messageHandler;
		this.inlineQueryHandler = inlineQueryHandler;
		this.callbackQueryHandler = callbackQueryHandler;
	}

	@Override
	public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

		if (update.hasInlineQuery()) {
			return inlineQueryHandler.handleInlineQuery(update.getInlineQuery());
		}
		else if (update.hasCallbackQuery()) {
			return callbackQueryHandler.handleCallbackQuery(update.getCallbackQuery());
		}
		else if (update.hasMessage() && update.getMessage().hasText()) {
			return messageHandler.handleMessage(update.getMessage());
		} 
		return null;
	}

	@Override
	public String getBotPath() {
		return this.webhookpath;
	}

	@Override
	public String getBotUsername() {
		return this.botName;
	}

	@Override
	public String getBotToken() {
		return this.botToken;
	}
}