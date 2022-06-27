package io.melakuera.tgbotzamena.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramChatRepo extends MongoRepository<TelegramChat, String>{
}
