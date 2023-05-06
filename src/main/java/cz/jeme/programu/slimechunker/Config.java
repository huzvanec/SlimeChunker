package cz.jeme.programu.slimechunker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	public static final String CONFIG_FILE_NAME = "config.yml";

	private File dataFolder;
	private File configFile;
	private FileConfiguration configFileYaml;
	private List<ConfigurationSection> configFileYamlSections = new ArrayList<ConfigurationSection>();

	private Map<String, String> sectionNames = new HashMap<String, String>();

	{
		sectionNames.put("MESSAGE", "Messages");
		sectionNames.put("YES", "Yes slimechunk");
		sectionNames.put("NO", "No slimechunk");
		sectionNames.put("WRONG ENVIROMENT", "Wrong dimension");
	}

	public String yesMessage;
	public String noMessage;
	public String wrongEnviromentMessage;

	public Config(File dataFolder) {
		this.dataFolder = dataFolder;
		refreshConfig();
	}

	public void refreshConfig() {
		refreshVars();

		if (!configFile.exists()) {
			try {
				if (!dataFolder.exists()) {
					dataFolder.mkdir();
				}
				configFile.createNewFile();
			} catch (IOException e) {
				SlimeChunker.serverLog(Level.SEVERE,
						"Unable to create config file! SlimeChunker will not work properly!");
				return;
			}
			setDefaults();
		}

		String messageSectionName = sectionNames.get("MESSAGE") + ".";
		yesMessage = configFileYaml.getString(messageSectionName + sectionNames.get("YES"));
		noMessage = configFileYaml.getString(messageSectionName + sectionNames.get("NO"));
		wrongEnviromentMessage = configFileYaml.getString(messageSectionName + sectionNames.get("WRONG ENVIROMENT"));
	}

	public void refreshVars() {
		configFile = new File(dataFolder + File.separator + CONFIG_FILE_NAME);
		configFileYaml = YamlConfiguration.loadConfiguration(configFile);

		for (String key : configFileYaml.getKeys(false)) {
			configFileYamlSections.add(configFileYaml.getConfigurationSection(key));
		}
	}

	private void setDefaults() {
		String messageSectionName = sectionNames.get("MESSAGE") + ".";
		configFileYaml.set(messageSectionName + sectionNames.get("YES"), "§2This is a slime chunk!");
		configFileYaml.set(messageSectionName + sectionNames.get("NO"), "§4This isn't a slime chunk!");
		configFileYaml.set(messageSectionName + sectionNames.get("WRONG ENVIROMENT"),
				"§5There are no slime chunks in this dimension!");
		try {
			configFileYaml.save(configFile);
		} catch (IOException e) {
			SlimeChunker.serverLog(Level.SEVERE, "Unable to update config file! SlimeChunker will not work properly!");
		}
	}
}
