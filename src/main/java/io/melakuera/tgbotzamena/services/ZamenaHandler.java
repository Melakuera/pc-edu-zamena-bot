package io.melakuera.tgbotzamena.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.melakuera.tgbotzamena.db.DbTelegramChatService;
import io.melakuera.tgbotzamena.db.TelegramChat;
import io.melakuera.tgbotzamena.telegram.ZamenaPinnerBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZamenaHandler {

	private final PdfDocumentHandler documentHandler;
	private final ZamenaPinnerBot bot;	
	private final DbTelegramChatService dbTelegramChatService;
	
	private static final String MARKDOWN = "Markdown";
	private static final String GET_ERROR = "Что-то произошло критическое: {}";
	
	@Scheduled(cron = "${cron.expression}")
	public void getCurrentZamena() {
		
		log.info("Обработчик замен начал свою работу...");
		
		Map<String, List<String>> zamenaData;
		try {
			zamenaData = documentHandler.getZamenaDataByGroup();
		} catch (IllegalAccessException e) {
			log.error(GET_ERROR, e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(x -> 
				log.error(x.toString()));
			return;
		}
		
		if (!zamenaData.isEmpty()) {
		
			List<TelegramChat> chats = dbTelegramChatService.findAll();
			
			for (TelegramChat chat : chats) {
				
				String chatId = chat.getTelegramChatId();
				String target = chat.getTarget();
				String actualRecentPinnedMessageText;
				try {
					actualRecentPinnedMessageText = chat.getRecentPinnedMessageText();
				} catch (Exception e) {
					log.warn(e.getMessage());
					Arrays.stream(e.getStackTrace()).forEach(x -> log.warn(x.toString()));
					return;
				}
				List<String> groupZamena = zamenaData.get(target);
				
				if (groupZamena == null) continue;
				
				int actualRecentPinnedMessageDayOfMonth = 
						getDayOfMonthByHeadText(actualRecentPinnedMessageText);
				int zamenaMessageDayOfMonth = 
						getDayOfMonthByHeadText(zamenaData.get("head").toString());
				
				if (actualRecentPinnedMessageDayOfMonth != zamenaMessageDayOfMonth) {
					
					StringBuilder groupZamenaResult = new StringBuilder();
					for (String zamena : groupZamena) {
						groupZamenaResult.append(zamena + "\n");
					}
					
					String headText = zamenaData.get("head").toString();
					String headTextResult = headText
							.substring(1, headText.length() - 1) + "\n";
					
					String result = (headTextResult + groupZamenaResult).strip();
					
					var messageZamenaData = SendMessage.builder()
							.text(result)
							.chatId(chatId)
							.build();	
					Message sendedMessage;
					try {
						sendedMessage = bot.execute(messageZamenaData);
					} catch (TelegramApiException e) {
						log.error(GET_ERROR, e.getMessage());
						Arrays.stream(e.getStackTrace()).forEach(x -> 
							log.error(x.toString()));
						return;
					}
					
					log.info("Замена разослано чату с id {} и с содержимым: \n{}", 
							chat.getTelegramChatId(), result);
					
					var pinChatMessage = PinChatMessage.builder()
							.chatId(chatId)
							.messageId(sendedMessage.getMessageId())
							.build();
					
					try {
						bot.execute(pinChatMessage);
					} catch (TelegramApiException e) {
						log.error(GET_ERROR, e.getMessage());
						Arrays.stream(e.getStackTrace()).forEach(x -> 
							log.error(x.toString()));
						return;
					}
					
					log.info("Сообщение c id {} содержащий новую "
							+ "замены, закреплена чату с id {}", 
								sendedMessage.getMessageId(), chatId);
					
					try {
					dbTelegramChatService
						.updateRecentPinnedMessageText(chatId, headTextResult);
					} catch (Exception e) {
						log.warn(e.getMessage());
						Arrays.stream(e.getStackTrace()).forEach(x -> log.warn(x.toString()));
						return;
					}
					mentionUsers(chatId, chat.getSubscribedUsersId());
					
					log.info("Обработчик замен завершил свою работу");
				}
			}
		}
	}
	
	public void mentionUsers(String chatId, List<String> subscribedUsersId) {
		
		StringBuilder sb = new StringBuilder();
		
		subscribedUsersId.forEach(userId -> {
			
			try {
				ChatMember chatMember = bot.execute(GetChatMember.builder()
						.chatId(chatId)
						.userId(Long.parseLong(userId))
						.build());
				String chatMemberFirstName = chatMember.getUser().getFirstName();
				
				sb.append(String.format(
						"[%s](tg://user?id=%s) ", chatMemberFirstName, userId));
				
				bot.execute(SendMessage.builder()
						.text(sb.toString())
						.chatId(chatId)
						.parseMode(MARKDOWN)
						.build());
				
			} catch (NumberFormatException | TelegramApiException e) {
				log.error("Что пошло не так: {}", e.getMessage());
				Arrays.stream(e.getStackTrace()).forEach(x -> 
					log.error(x.toString()));
			}
		});
		log.info("В чате c id {} упомянуто {} юзеров", 
				chatId, subscribedUsersId.size());
	}
	
	private int getDayOfMonthByHeadText(String headText) {
		
		Pattern patter = Pattern.compile("\\d{2}");
		Matcher matcher = patter.matcher(headText);
		int dayOfMonth = -1;
		if (matcher.find()) {
		    String t = headText.substring(matcher.start(), matcher.end());
		    dayOfMonth = Integer.parseInt(t);
		}
		return dayOfMonth;
	}
}
