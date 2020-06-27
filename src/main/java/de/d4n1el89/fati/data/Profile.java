package de.d4n1el89.fati.data;

import lombok.Getter;

public class Profile {

	@Getter
	private final String displayName;

	public Profile(String displayName) {
		this.displayName = displayName;
	}
}
