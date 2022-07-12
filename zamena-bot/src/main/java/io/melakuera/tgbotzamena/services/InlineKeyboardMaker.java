package io.melakuera.tgbotzamena.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/*
 * Строитель встроенных клавиатур для ответа 
 * (InlineKeyboardButton: https://core.telegram.org/bots/api#inlinekeyboardmarkup)
 */
@Component
public class InlineKeyboardMaker {
	
	public InlineKeyboardMarkup getInlineKeyboardMarkup() {
		
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		
		rowInline.add(InlineKeyboardButton.builder()
				.text("Да")
				.callbackData("quit_yes")
				.build());
		
		rowInline.add(InlineKeyboardButton.builder()
				.text("Нет")
				.callbackData("quit_no")
				.build());
		
		rowsInline.add(rowInline);
		
		inlineKeyboardMarkup.setKeyboard(rowsInline);
		
		return inlineKeyboardMarkup;
	}
}
