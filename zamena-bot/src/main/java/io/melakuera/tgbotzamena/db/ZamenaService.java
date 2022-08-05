package io.melakuera.tgbotzamena.db;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZamenaService {
	
	private final ZamenaRepo zamenaRepo;
	
	/**
	 * Заменяет единственный экземпляр в БД
	 * @param zamenaData новые распарсенные данные о замене (не должно быть null)
	 */
	public void putZamena(Map<String, List<String>> zamenaData) {
		var currentZamena = zamenaRepo.getCurrentZamena();
		currentZamena.setZamenaData(zamenaData);
		zamenaRepo.save(currentZamena);
	}
	
	public Map<String, List<String>> getGroupZamenaByGroup(String group) {
		Map<String, List<String>> zamenaData = zamenaRepo.getCurrentZamena().getZamenaData();
		List<String> groupZamena = zamenaData.get(group);
		List<String> headText = zamenaData.get("head");
		
		if (groupZamena == null) return Collections.emptyMap();
		
		Map<String, List<String>> map = new HashMap<>();
		map.put(group, groupZamena);
		map.put("head", headText);
		
		return map;
	}
}
