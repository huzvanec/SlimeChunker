package cz.jeme.programu.slimechunker;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Config {
    public static final String CONFIG_FILE_NAME = "config.yml";

    private final File dataFolder;
    private File configFile;
    private FileConfiguration configFileYaml;
    private final Map<String, String> sectionNames = new HashMap<>();

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
