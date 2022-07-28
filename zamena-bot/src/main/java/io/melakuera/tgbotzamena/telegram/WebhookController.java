package io.melakuera.tgbotzamena.telegram;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.melakuera.tgbotzamena.services.ZamenaHandler;
import lombok.RequiredArgsConstructor;

/*
 * Контроллер на которую приходят обновления 
 */
@RestController
@RequiredArgsConstructor
public class WebhookController {

	private final ZamenaPinnerBot zamenaPinnerBot;
	private final ZamenaHandler zamenaHandler;
	
	// При наличии обновлений через вебхук делегируем его на класс наследующий SpringWebhookBot
	@PostMapping
	BotApiMethod<?> onUpdateRecieved(@RequestBody Update update) {
		return zamenaPinnerBot.onWebhookUpdateReceived(update);
	}
	
	// Когда от GUI приложение пришел запрос
	@PostMapping("/zamena")
	Map<?, ?> onZamenaRecieved(@RequestParam("zamena") MultipartFile zamenaFile) {

		return zamenaHandler.onZamenaRecieved(zamenaFile);
	}
}
