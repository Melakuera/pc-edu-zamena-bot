package io.melakuera.tgbotzamena.db;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DbTelegramChatService {

	private final TelegramChatRepo telegramChatRepo;

	public void insertChat(String chatId, String target) {
		
		if (telegramChatRepo.findById(chatId).isPresent()) {
			throw new IllegalArgumentException(
					String.format("Чат с id %s уже существует", chatId));
		}
		var newChat = new TelegramChat(chatId, target);
		telegramChatRepo.insert(newChat);
	}
	
	public void insertUserByChat(String chatId, String userId) {
		
		var actualChat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format("Чат с id %s не существует", chatId));
		});
		List<String> subscribedUsers = actualChat.getSubscribedUsers();
		boolean isContains = subscribedUsers.contains(userId);
		
		if (isContains) {
			throw new IllegalArgumentException(
					String.format("Юзер с id %s уже присутствует в списке", userId));
		}
		subscribedUsers.add(userId);
		
		telegramChatRepo.save(actualChat);
	}
	
	public void removeUserByChat(String chatId, String userId) {
		
		var actualChat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format("Чат с id %s не существует", chatId));
		});
		List<String> subscribedUsers = actualChat.getSubscribedUsers();
		boolean isContains = subscribedUsers.contains(userId);
		
		if (!isContains) {
			throw new IllegalArgumentException(
					String.format("Юзер с id %s отсутствует в списке", userId));
		}
		subscribedUsers.removeIf(userId::equals);
		
		telegramChatRepo.save(actualChat);
	}
	
	public String getTargetByChatId(String chatId) {
		var chat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format("Чат с id %s не существует", chatId));
		});
		return chat.getTarget();
	}
	
	public boolean ifChatExistsThenUpdateTarget(String chatId, String target) {
		var chat = telegramChatRepo.findById(chatId).orElse(null);
		
		if (chat == null) {
			return false;
		}
		chat.setTarget(target);
		telegramChatRepo.save(chat);
		
		return true;
	}
	
	public boolean ifChatExistsDeleteChat(String chatId) {
		
		var chat = telegramChatRepo.findById(chatId).orElse(null);
		
		if (chat == null) {
			return false;
		}
		telegramChatRepo.deleteById(chatId);
		
		return true;
	}
}
