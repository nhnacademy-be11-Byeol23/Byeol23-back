package com.nhnacademy.byeol23backend.bookset.contributor.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum ContributorRole {
	AUTHOR("저자"), TRANSLATOR("역자");

	private final String label;

	public static ContributorRole from(String value) {
		if (value == null) throw new IllegalArgumentException("role is null");
		String v = value.trim();
		for (var r : values()) {
			if (r.name().equalsIgnoreCase(v)) {
				return r;
			}
		}
		for (var r : values()) {
			if (r.label.equals(v)) {
				return r;
			}
		}
		throw new IllegalArgumentException("Unknown role: " + value);
	}
}
