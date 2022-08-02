package io.melakuera.tgbotzamena.constants;

import java.util.List;

// Константы представляющий наименование факультета
public class FacultyType {
	
	public static final String PKS = "ПКС";
	public static final String KS = "КС"; 
	public static final String ESSS = "ЭССС";
	public static final String EKSS = "ЭкСС";
	public static final String SSSK = "СССК";

	public static List<String> values() {
		return List.of(PKS, KS, ESSS, EKSS, SSSK);
	}
	private FacultyType() {}
}
