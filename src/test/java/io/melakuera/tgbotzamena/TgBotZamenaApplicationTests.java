package io.melakuera.tgbotzamena;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.melakuera.tgbotzamena.services.MessageHandler;

@SpringBootApplication
class TgBotZamenaApplicationTests {
	
	@MockBean
	private MessageHandler messageHandler;
	
	@Test
	void shouldValidateQuery() {
		
	}
}
