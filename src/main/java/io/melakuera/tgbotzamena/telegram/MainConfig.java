package io.melakuera.tgbotzamena.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;


/*
 * Конфигурационный класс 
 */
@Configuration
@EnableScheduling // Включаем возможность выполнения запланированных задач. 
@EnableAsync // Включаем возможность выполнения асинхронных вызовов
public class MainConfig {
	
	@Value("${telegram.webhook-path}")
    private String botPath;

    /*
     * Зарегистрировать вебхук:
     * https://api.telegram.org/bot<токен бота>/setWebhook?url=<url от ngrok>
     * 
     * Получение инфо вебхук данного бота
     * https://api.telegram.org/bot<токен бота>/getWebhookInfo
     */
    @Bean
    SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botPath).build();
    }
    
}
