package io.melakuera.tgbotzamena.db;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZamenaService {
	
	private final ZamenaRepo zamenaRepo;
	
	public void putZamena(Map<String, List<String>> zamenaData) {
		var currentZamena = zamenaRepo.getCurrentZamena();
		if (currentZamena == null) {
			zamenaRepo.insert(new Zamena(zamenaData));
		} else {
			currentZamena.setZamenaData(zamenaData);
			zamenaRepo.save(currentZamena);
		}
	}
}
