package io.melakuera.tgbotzamena.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ZamenaRepo extends MongoRepository<Zamena, String>{
	
	@Query("{ 'current_zamena': 'single_data' }")
	Zamena getCurrentZamena();
}
