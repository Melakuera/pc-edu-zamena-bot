package io.melakuera.tgbotzamena.db;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
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
	
	// id одного и того же экземпляра
	@Id
	private String id = "single_data";
	// распарсенные данные о замене 
	@NonNull
	private Map<String, List<String>> zamenaData;
	
	public Zamena(Map<String, List<String>> zamenaData) {
		super();
		this.zamenaData = zamenaData;
	}
	
	public static Zamena emptyZamena() {
		return new Zamena(Collections.emptyMap());
	}
	
	public Zamena() {}
}
