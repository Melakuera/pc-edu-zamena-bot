package io.melakuera.tgbotzamena.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation.SendAnimationBuilder;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.melakuera.tgbotzamena.telegram.ZamenaPinnerBot;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GifHandler {
	
	private final ZamenaPinnerBot bot;
	
	public GifHandler(@Lazy ZamenaPinnerBot bot) {
		super();
		this.bot = bot;
	}
	
	private static final String GET_ERROR = "Что-то произошло критическое: {}";
	
	@Value("${telegram.example-gif-file-id}")
	private String exampleGifFileId;
	
	public void sendExampleGif(String chatId, SendAnimationBuilder sendAnimBuilder) {
		
		try {
			bot.execute(sendAnimBuilder
					.animation(new InputFile(exampleGifFileId))
					.chatId(chatId)
					.build());
			
		} catch (TelegramApiException e) {
			log.error(GET_ERROR, e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(x -> 
				log.error(x.toString()));
		}
	}
}
