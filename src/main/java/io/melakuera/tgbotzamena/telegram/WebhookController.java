package io.melakuera.tgbotzamena.telegram;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;

/*
 * Контроллер на которую приходят обновления 
 */
@RestController
@RequiredArgsConstructor
public class WebhookController {

	private final ZamenaPinnerBot zamenaPinnerBot;
	
	// При наличии обновлений через вебхук делегируем его на класс наследующий SpringWebhookBot
	@PostMapping
	BotApiMethod<?> onUpdateRecieved(@RequestBody Update update) {
		return zamenaPinnerBot.onWebhookUpdateReceived(update);
	}
}
