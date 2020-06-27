package de.d4n1el89.fati.util;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileHandler {

	private static final Logger LOGGER = LogManager.getLogger(FileHandler.class);

	private static final Gson PRETTY_GSON;

	static {
		PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
	}

	private FileHandler() {
	}

	public static boolean isValidFolder(String folder){
		File file = getFile(folder);
		if(!file.exists()){
			return false;
		}
		return file.isDirectory();
	}

	public static <T> T readJson(String fullFileName, Type ref) {

		try {
			File file = getFile(fullFileName);
			if (!file.exists()) {
				LOGGER.error("FILE '{}' doesn't exist!", file);
				return null;
			}
			byte[] jsonData = Files.readAllBytes(file.toPath());
			return PRETTY_GSON.fromJson(new String(jsonData), ref);
		} catch (Exception e) {
			LOGGER.error("Could not load data of: '{}', message: {}" , fullFileName, e.getMessage());
		}
		return null;
	}

	public static void saveJson(String fullFileName, Object data) {
		try {
			File file = getFile(fullFileName);
			if (!file.exists()) {
				createFile(fullFileName);
			}
			String serialized = PRETTY_GSON.toJson(data);
			Files.write(file.toPath(), serialized.getBytes());
		} catch (Exception e) {
			LOGGER.error("Could not save data of: '{}', message: {}" , fullFileName, e.getMessage());
		}
	}


	private static void createFile(String fullFileName) {
		try {
			File file = new File(fullFileName);
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file);
			writer.close();
		} catch (Exception e) {
			LOGGER.error("Could not create file '{}', message: {}" ,fullFileName, e.getMessage());
		}
	}

	private static File getFile(String fullFileName) {
		return new File(fullFileName);
	}
}
