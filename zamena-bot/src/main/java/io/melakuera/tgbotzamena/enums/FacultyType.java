package io.melakuera.tgbotzamena.enums;

// Константы представляющий наименование факультета
public enum FacultyType {
	
	PKS("ПКС"), KS("КС"), ESSS("ЭССС"),
	EKSS("ЭкСС"), SSSK("СССК");
	
	private final String rusName;

	private FacultyType(String rusName) {
		this.rusName = rusName;
	}

	public String getRusName() {
		return rusName;
	}
}
