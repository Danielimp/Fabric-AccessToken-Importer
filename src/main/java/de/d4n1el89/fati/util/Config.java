package de.d4n1el89.fati.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {

	private static final Logger LOGGER = LogManager.getLogger(Config.class);

	public String minecraftFolder = "";
	public String workspaceFolder = "";
	public String minecraftUsername = "";
	public String uuid = "";
	public List<String> foldersToIgnore = new ArrayList<>(Arrays.asList("folderToIgnore"));

	public boolean isValid(){

		if(minecraftFolder.isEmpty() || workspaceFolder.isEmpty() || minecraftUsername.isEmpty() || uuid.isEmpty()){
			LOGGER.error("A field of your config is empty!");
			return false;
		}

		if(!FileHandler.isValidFolder(minecraftFolder) || !FileHandler.isValidFolder(workspaceFolder)){
			LOGGER.error("A path of your config isn't valid!");
			return false;
		}

		return true;
	}
}
