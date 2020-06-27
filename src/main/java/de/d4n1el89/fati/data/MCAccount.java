package de.d4n1el89.fati.data;

import java.util.List;

import de.d4n1el89.fati.util.Config;
import lombok.Getter;

public class MCAccount {

	@Getter
	private final String accessToken;
	@Getter
	private final String username;
	@Getter
	private final List<Profile> profiles;

	public MCAccount(String accessToken, String username, List<Profile> profiles) {
		this.accessToken = accessToken;
		this.username = username;
		this.profiles = profiles;
	}

	public String toProgramArgument(Config config) {
		return "--username " + config.minecraftUsername + " --uuid " + config.uuid + " --accessToken "
				+ accessToken;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		profiles.forEach(p -> sb.append(p.getDisplayName()).append(", "));

		return "MCAccount: username=" + username + ", accessToken=" + accessToken.substring(0, 15)
				+ "*censored*, profiles: " + sb.toString();
	}
}
