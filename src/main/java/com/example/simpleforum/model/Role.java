package com.example.simpleforum.model;

public enum Role {
	USER("一般"), 
	ADMIN("管理者");

	private final String label;

	Role(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}