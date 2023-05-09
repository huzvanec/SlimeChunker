package cz.jeme.programu.slimechunker;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {
    private final File dataFolder;

    public static FileConfiguration yaml;

    public Config(File dataFolder) {
        this.dataFolder = dataFolder;
        reload();
    }

    public void reload() {
        File configFile = new File(dataFolder + File.separator + "config.yml");
        yaml = YamlConfiguration.loadConfiguration(configFile);
    }
}
