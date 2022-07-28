package io.melakuera.tgbotzamena.db;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.Data;

/*
 *  Модель представляющий замену.
 *  В БД храниться всегда в одном экземпляре
 */
@Document
@Data
public class Zamena {
	
	// Строка представляющий один и тот же экземпляр
	@NonNull
	private String currentZamena;
	// распарсенные данные о замене 
	@NonNull
	private Map<String, List<String>> zamenaData;
	
	public Zamena(Map<String, List<String>> zamenaData) {
		super();
		this.currentZamena = "single_data";
		this.zamenaData = zamenaData;
	}	
}
