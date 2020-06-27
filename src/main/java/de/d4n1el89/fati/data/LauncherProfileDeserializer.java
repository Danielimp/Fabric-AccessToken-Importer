package de.d4n1el89.fati.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class LauncherProfileDeserializer implements JsonDeserializer<LauncherProfile> {

	@Override
	public LauncherProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		final JsonObject authenticationDatabase = json.getAsJsonObject().getAsJsonObject("authenticationDatabase");

		final List<MCAccount> mcAccounts = new ArrayList<>();

		// Iterating over MCAccounts
		Set<Entry<String, JsonElement>> jsonMCAccounts = authenticationDatabase.entrySet();
		for (Entry<String, JsonElement> mcAccount : jsonMCAccounts) {
			mcAccounts.add(getMCAccount(mcAccount.getValue().getAsJsonObject()));
		}

		return new LauncherProfile(mcAccounts);
	}

	private MCAccount getMCAccount(JsonObject mcAccount) {

		String accessToken = mcAccount.get("accessToken").getAsString();
		String username = mcAccount.get("username").getAsString();

		return new MCAccount(accessToken, username, getProfiles(mcAccount.getAsJsonObject("profiles")));
	}

	private List<Profile> getProfiles(JsonObject profiles) {

		List<Profile> theProfiles = new ArrayList<>();

		// Iterating over profiles
		Set<Entry<String, JsonElement>> jsonProfiles = profiles.entrySet();
		for (Entry<String, JsonElement> profile : jsonProfiles) {
			theProfiles.add(new Profile(profile.getValue().getAsJsonObject().get("displayName").getAsString()));
		}

		return theProfiles;
	}
}
