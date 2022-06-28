package io.melakuera.tgbotzamena.db;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.lang.NonNull;

import lombok.Data;

@Document("telegram_chats")
@Data
public class TelegramChat {

	@Id
	@NonNull
	@Field("telegram_chat_id")
	private String telegramChatId;
	
	@Field("target")
	@NonNull
	private String target;
	
	@Field("recent_pinned_message_text")
	private String recentPinnedMessageText;
	
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
