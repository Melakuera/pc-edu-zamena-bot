package io.melakuera.tgbotzamena.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ZamenaRepo extends MongoRepository<Zamena, String>{
	
	@Query("{ '_id': 'single_data' }")
	Zamena getCurrentZamena();
}
