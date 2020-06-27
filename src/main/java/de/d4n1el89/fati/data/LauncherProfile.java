package de.d4n1el89.fati.data;

import java.util.List;

import com.google.gson.annotations.JsonAdapter;

import lombok.Getter;

@JsonAdapter(LauncherProfileDeserializer.class)
public class LauncherProfile {

	@Getter
	private final List<MCAccount> mcAccounts;

	public LauncherProfile(List<MCAccount> mcAccounts) {
		this.mcAccounts = mcAccounts;
	}
}
