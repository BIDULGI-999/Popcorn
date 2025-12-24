package com.bidulgi.productservice.domain.recommendation;

public enum AgeBand {
	A10, A20, A30, A40, A50, A60, SENIOR, UNKNOWN;

	public static AgeBand fromAge(Integer age) {
		if (age == null || age < 0) return UNKNOWN;
		if (age < 20) return A10;
		if (age < 30) return A20;
		if (age < 40) return A30;
		if (age < 50) return A40;
		if (age < 50) return A50;
		if (age < 60) return A60;
		return SENIOR;
	}
}
