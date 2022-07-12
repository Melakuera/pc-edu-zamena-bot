package io.melakuera.tgbotzamena.db;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.Data;

@Document
@Data
public class Zamena {
	
	@NonNull
	private String currentZamena;
	@NonNull
	private Map<String, List<String>> zamenaData;
	
	public Zamena(Map<String, List<String>> zamenaData) {
		super();
		this.currentZamena = "single_data";
		this.zamenaData = zamenaData;
	}
	
	
}
