package io.melakuera.tgbotzamena.db;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.lang.NonNull;

import lombok.Data;

/*
 * Модель представляющий телеграм-группу в Телеграмме
 */
@Document("telegram_chats")
@Data
public class TelegramChat {

	// Id группы достающийся через API Telegram bot
	@Id
	@NonNull
	private String telegramChatId;
	
	// Группа из колледжа на которую они подписаны
	@Field("target")
	@NonNull
	private String target;
	
	// Последная закрепленная сообщение в группе телеграм. 
	// Требуется чтобы повторно не отправлять сообщения
	@Field("recent_pinned_message_text")
	private String recentPinnedMessageText;
	
	// Список id юзеров, которые подписаны на входящие замены
	@Field("subscribed_users_id")
	private List<String> subscribedUsersId;

	public TelegramChat(String telegramChatId, String target) {
		super();
		this.telegramChatId = telegramChatId;
		this.target = target;
		this.recentPinnedMessageText = "";
		this.subscribedUsersId = new ArrayList<>();
	}
}
