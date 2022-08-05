package io.melakuera.tgbotzamena.db;

import java.util.List;

import org.springframework.stereotype.Service;

import io.melakuera.tgbotzamena.constants.FacultyType;
import lombok.RequiredArgsConstructor;

/*
 * Бизнес-логика с телеграм-группами
 */
@Service
@RequiredArgsConstructor
public class TelegramChatService {

	private static final String CHAT_NOT_EXISTS = "Чат с id %s не существует";
	private final TelegramChatRepo telegramChatRepo;

	/**
	 * Возвращает телеграм-группу по заданному шв
	 * 
	 * @param chatId id телеграм-группы (не должно быть null)
	 * @return телеграм-группу
	 */
	public TelegramChat getById(String chatId) {
		return telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(String.format(CHAT_NOT_EXISTS, chatId));});
	}
	/**
	 * Сохраняет заданный телеграм-группу
	 * 
	 * @param chatId id телеграм-группы (не должно быть null)
	 * @param target группа, на которую она подписана (не должно быть null)
	 * @see FacultyType
	 */
	public void insertChat(String chatId, String target) {
		
		// Если такая телеграм-группа существует, то ошибка
		if (telegramChatRepo.findById(chatId).isPresent()) {
			return;
		}
		var newChat = new TelegramChat(chatId, target);
		telegramChatRepo.insert(newChat);
	}
	
	/**
	 * Сохраняет id юзера в список подписавшиеся на замены группы (target)
	 * 
	 * @param chatId id телеграм-группы, в котором состоит данный юзер (не должно быть null)
	 * @param userId id юзера, которую надо сохранить (не должно быть null)
	 * @return false если юзер уже подписан, иначе true
	 * @throws IllegalArgumentException если телеграм-группа не существует
	 * @see FacultyType
	 */
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
	
	/**
	 * Получает последнее закрепленное сообщение телеграм-группы
	 * 
	 * @param chatId id телеграм-группы (не должно быть null)
	 * @return последнее закрепленное сообщение
	 * @throws IllegalArgumentException если телеграм-группа не существует
	 */
	public String getRecentPinnedMessageText(String chatId) {
		var actualChat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format(CHAT_NOT_EXISTS, chatId));
		});
		return actualChat.getRecentPinnedMessageText();
	}
	
	/**
	 * Удалить юзера из списка подписавшиеся на замены группы
	 * 
	 * @param chatId id телеграм-группы, в котором состоит данный юзер (не должно быть null)
	 * @param userId id юзера, которую надо удалить (не должно быть null)
	 * @return false если юзер еще не подписан, иначе true
	 * @throws IllegalArgumentException если телеграм-группа не существует
	 * @see FacultyType
	 */
	public boolean removeUserFromChat(String chatId, String userId) {
		
		var actualChat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format(CHAT_NOT_EXISTS, chatId));
		});
		List<String> subscribedUsers = actualChat.getSubscribedUsersId();
		
		if (!subscribedUsers.contains(userId)) {
			return false;
		}
		subscribedUsers.removeIf(userId::equals);
		
		telegramChatRepo.save(actualChat);
		
		return true;
	}
	
	/**
	 * Получает группу на которую подписана телеграм-группа
	 * 
	 * @param chatId id телеграм-группы (не должно быть null)
	 * @return группы на которую подписана телеграм-группа
	 * @see FacultyType
	 */
	public String getTarget(String chatId) {
		var chat = telegramChatRepo.findById(chatId).orElse(null);
		
		if (chat != null) {
			return chat.getTarget();
		}
		return "";
	}
	
	/**
	 * Изменяет группу на которую подписана телеграм-группа
	 * если таковая, то изменяет target и recentPinnedMessageText 
	 * на пустую строку
	 * @param chatId id телеграм-группы (не должно быть null)
	 * @param target новая группа (не должно быть null)
	 * @return true если удалось изменить иначе false
	 * @see FacultyType
	 */
	public boolean updateTarget(String chatId, String target) {
		var chat = telegramChatRepo.findById(chatId).orElse(null);
		
		if (chat == null) {
			return false;
		}
		chat.setTarget(target);
		chat.setRecentPinnedMessageText("");
		telegramChatRepo.save(chat);
		
		return true;
	}
	
	/**
	 * Удаляет телеграм-группу
	 * 
	 * @param chatId id телеграм-группы (не должно быть null) 
	 * @return true если удалось удалить, иначе false
	 */
	public boolean deleteChat(String chatId) {
		
		var chat = telegramChatRepo.findById(chatId).orElse(null);
		
		if (chat == null) {
			return false;
		}
		telegramChatRepo.deleteById(chatId);
		
		return true;
	}
	
	/**
	 * Получает все телеграм-группы
	 * 
	 * @return список всех телеграм групп
	 */
	public List<TelegramChat> findAll() {
		return telegramChatRepo.findAll();
	}
	
	/**
	 * Изменяет текст last закрепленного сообщения
	 * 
	 * @param chatId id телеграм-группы (не должно быть null) 
	 * @param text новый текст, на которую изменится сообщение (не должно быть null) 
	 * @throws IllegalArgumentException если телеграм-группа не существует
	 */
	public void updateRecentPinnedMessageText(String chatId, String text) {
		var chat = telegramChatRepo.findById(chatId).orElseThrow(() -> {
			throw new IllegalArgumentException(
					String.format(CHAT_NOT_EXISTS, chatId));
		});
		chat.setRecentPinnedMessageText(text);
		telegramChatRepo.save(chat);
	}
}
