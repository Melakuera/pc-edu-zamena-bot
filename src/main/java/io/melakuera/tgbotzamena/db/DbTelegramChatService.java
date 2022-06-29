package io.melakuera.tgbotzamena.db;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/*
 * Бизнес-логика с БД
 */
@Service
@RequiredArgsConstructor
public class DbTelegramChatService {

	private static final String CHAT_NOT_EXISTS = "Чат с id %s не существует";
	private final TelegramChatRepo telegramChatRepo;

	// Ложит в коллекцию телеграм-группу
	public void insertChat(String chatId, String target) {
		
		if (telegramChatRepo.findById(chatId).isPresent()) {
			throw new IllegalArgumentException(
					String.format("Чат с id %s уже существует", chatId));
		}
		var newChat = new TelegramChat(chatId, target);
		telegramChatRepo.insert(newChat);
	}
	
	// Ложит id юзера, который подписался на группы. 
	// Если таковой уже существует, то 
	public boolean addUserToChat(String chatId, String userId) {
		
		var actualChat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format(CHAT_NOT_EXISTS, chatId));
		});
		List<String> subscribedUsers = actualChat.getSubscribedUsersId();
		
		if (subscribedUsers.contains(userId)) {
			return false;
		}
		subscribedUsers.add(userId);
		
		telegramChatRepo.save(actualChat);
		
		return true;
	}
	
	// Получить последнее закрепленное сообщение в группе телеграм. 
	public String getRecentPinnedMessageText(String chatId) {
		var actualChat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format(CHAT_NOT_EXISTS, chatId));
		});
		return actualChat.getRecentPinnedMessageText();
	}
	
	// Убрать из списка подписанного на замены юзера. 
	public boolean removeUserFromChat(String chatId, String userId) {
		
		var actualChat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format(CHAT_NOT_EXISTS, chatId));
		});
		List<String> subscribedUsers = actualChat.getSubscribedUsersId();
		
		if (subscribedUsers.contains(userId)) {
			return false;
		}
		subscribedUsers.removeIf(userId::equals);
		
		telegramChatRepo.save(actualChat);
		
		return true;
	}
	
	// Получить группу на которую подписана телеграм-группа
	public String getTarget(String chatId) {
		var chat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format(CHAT_NOT_EXISTS, chatId));
		});
		return chat.getTarget();
	}
	
	// Изменить группу на которую подписана телеграм-группа
	public boolean updateTarget(String chatId, String target) {
		var chat = telegramChatRepo.findById(chatId).orElse(null);
		
		if (chat == null) {
			return false;
		}
		chat.setTarget(target);
		telegramChatRepo.save(chat);
		
		return true;
	}
	
	// Удалить телеграм-группу
	public boolean deleteChat(String chatId) {
		
		var chat = telegramChatRepo.findById(chatId).orElse(null);
		
		if (chat == null) {
			return false;
		}
		telegramChatRepo.deleteById(chatId);
		
		return true;
	}
	
	// Получить всех телеграм-групп
	public List<TelegramChat> findAll() {
		return telegramChatRepo.findAll();
	}
	
	// Изменить текст last закрепленного сообщения
	public void updateRecentPinnedMessageText(String chatId, String text) {
		var chat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format(CHAT_NOT_EXISTS, chatId));
		});
		chat.setRecentPinnedMessageText(text);
		telegramChatRepo.save(chat);
	}
}
