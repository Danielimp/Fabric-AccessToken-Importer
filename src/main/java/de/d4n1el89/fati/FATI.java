package de.d4n1el89.fati;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.d4n1el89.fati.util.IdeConfig;
import de.d4n1el89.fati.util.Config;
import de.d4n1el89.fati.util.FileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.reflect.TypeToken;

import de.d4n1el89.fati.data.LauncherProfile;
import de.d4n1el89.fati.data.MCAccount;
import de.d4n1el89.fati.data.Profile;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class FATI {

	private static final Logger LOGGER = LogManager.getLogger(FATI.class);
	private static final String CONFIG_FILE = "config.json";
	private static final String LAUNCHER_PROFILES_FILENAME = "launcher_profiles.json";
	private static final Type TYPE_LAUNCHER = new TypeToken<LauncherProfile>() {
	}.getType();
	private static final Type TYPE_CONFIG = new TypeToken<Config>() {
	}.getType();

	private Config config;

	private void readConfig() throws Exception {
		config = FileHandler.readJson(CONFIG_FILE, TYPE_CONFIG);
		if (config == null) {
			config = new Config();
			FileHandler.saveJson(CONFIG_FILE, config);
			LOGGER.warn("Created initial config for you, you need to adapt it properly!");
			throw new Exception("No valid config!");
		}

		// Validate config
		if(!config.isValid()){
			throw new Exception("No valid config!");
		}
	}

	public void executeImport() {

		LOGGER.info("FATI started.");

		try {
			readConfig();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return;
		}

		// Import launcher profiles
		LauncherProfile launcherProfiles;
		try {
			launcherProfiles = importTokens();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return;
		}

		// Select minecraft account
		MCAccount selectedAccount = null;
		try {
			selectedAccount = findSelectedAccount(launcherProfiles.getMcAccounts());
			LOGGER.info("Found token of the selected minecraft account: '{}'", config.minecraftUsername);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		for (IdeConfig ideConfig : IdeConfig.values()) {

			// Get launch configuration files
			List<Path> launchFiles = findLaunchFiles(config, ideConfig);
			if (launchFiles.isEmpty()) {
				LOGGER.warn("Couldn't find any launch files for '{}'!" , ideConfig.name());
				continue;
			}

			// Modify launch files
			modifyLaunchFiles(launchFiles, selectedAccount, ideConfig);
			LOGGER.info("Found and modified '{}' client launch files for '{}'.", launchFiles.size(), ideConfig.name());
		}

		LOGGER.info("FATI completed!");
	}

	private LauncherProfile importTokens() throws Exception {

		String fullFileName = config.minecraftFolder + File.separator + LAUNCHER_PROFILES_FILENAME;
		LauncherProfile launcherProfiles = FileHandler.readJson(fullFileName, TYPE_LAUNCHER);
		if (launcherProfiles == null) {
			throw new Exception("Could not load " + LAUNCHER_PROFILES_FILENAME);
		}

		if (launcherProfiles.getMcAccounts().isEmpty()) {
			throw new Exception("Could not find any minecraft accounts!");
		}
		return launcherProfiles;
	}

	private MCAccount findSelectedAccount(List<MCAccount> mcAccounts) throws Exception {

		for (MCAccount acc : mcAccounts) {
			for (Profile profile : acc.getProfiles()) {
				if (profile.getDisplayName().equalsIgnoreCase(config.minecraftUsername)) {
					return acc;
				}
			}
		}
		throw new Exception("Could not find the selected minecraft account: '" + config.minecraftUsername + "'!");
	}

	private List<Path> findLaunchFiles(Config config, IdeConfig ideConfig) {

		final List<Path> result = new ArrayList<>();

		final Path workspace = Paths.get(config.workspaceFolder);

		if (!Files.isDirectory(workspace)) {
			LOGGER.error("The given workspace path is not a directory; given path='{}'" , config.workspaceFolder);
			return result;
		}

		try (final DirectoryStream<Path> stream = Files.newDirectoryStream(workspace,
				path -> path.toFile().isDirectory() && !ignoreFolder(path.getFileName().toString()))) {
			stream.forEach(f -> result.addAll(searchSubfolder(f, ideConfig)));
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}

	private List<Path> searchSubfolder(Path p, IdeConfig ideConfig) {

		final List<Path> result = new ArrayList<>();

		p = Paths.get(p.toString(), ideConfig.path);

		try (final DirectoryStream<Path> stream = Files.newDirectoryStream(p, ideConfig.launchFileName)) {
			stream.forEach(result::add);
		} catch (IOException e) {
			// No launch configuration found, do nothing
		}
		return result;
	}

	private boolean ignoreFolder(String folderName) {
		return config.foldersToIgnore.stream().anyMatch(e -> e.equalsIgnoreCase(folderName));
	}

	private void modifyLaunchFiles(List<Path> launchFiles, MCAccount selectedAccount, IdeConfig ideConfig) {

		SAXBuilder builder = new SAXBuilder();
		for (Path launchFile : launchFiles) {
			try{
				modifyLaunchFile(builder, launchFile, selectedAccount, ideConfig);
			}catch(JDOMException | IOException e){
				LOGGER.error("Couldn't modify launch file: '{}', message: '{}'" , launchFile , e.getMessage());
			}
		}
	}

	private void modifyLaunchFile(SAXBuilder builder, Path launchFile, MCAccount selectedAccount, IdeConfig ideConfig) throws JDOMException, IOException {

		Document doc = builder.build(launchFile.toFile());
		List<Element> children = ideConfig.getChildren(doc.getRootElement());
		for (Element element : children) {
			boolean programParameters = false;
			for (Attribute attribute : element.getAttributes()) {
				// 2. write value (aka minecraft account stuff) to the next element
				if(programParameters){
					attribute.setValue(selectedAccount.toProgramArgument(config));
					XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
					xmlOutputter.output(doc, new FileOutputStream(launchFile.toFile()));
					return;
				}
				// 1. find key
				if(attribute.getValue().equalsIgnoreCase(ideConfig.programParamsKey)){
					programParameters = true;
				}
			}
		}
	}
}
