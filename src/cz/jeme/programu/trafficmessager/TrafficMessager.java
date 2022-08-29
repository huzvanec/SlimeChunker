package cz.jeme.programu.trafficmessager;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TrafficMessager extends JavaPlugin implements Listener {

	private String joinMessage = null;
	private String leaveMessage = null;

	@Override
	public void onEnable() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(this, this);
		File dataDir = getDataFolder();

		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		prepareConfig();
	}

	@Override
	public void onDisable() {
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		String replacedPlayerName = (joinMessage.replaceAll("\\{PLAYERNAME\\}", playerName));
		String colorsTranslated = ChatColor.translateAlternateColorCodes('§', replacedPlayerName);
		event.setJoinMessage(colorsTranslated);

	}
	
	@EventHandler
	public void onPlayerLeaveEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		String replacedPlayerName = (leaveMessage.replaceAll("\\{PLAYERNAME\\}", playerName));
		String colorsTranslated = ChatColor.translateAlternateColorCodes('§', replacedPlayerName);
		event.setQuitMessage(colorsTranslated);

	}

	private void prepareConfig() {
		String configFileName = "config.yml";
		File configFile = new File(getDataFolder(), configFileName);
		if (!(configFile.exists())) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				serverLog(Level.SEVERE, "TrafficMessager: Cannot create file \"" + configFileName + "\"!" + e);
			}
		}
		FileConfiguration configFileYaml = YamlConfiguration.loadConfiguration(configFile);
		String sectionName = "Messages";
		ConfigurationSection section = configFileYaml.getConfigurationSection(sectionName);
		if (section == null) {
			section = configFileYaml.createSection(sectionName);
		}
		Set<String> sectionKeys = section.getKeys(false);
		if (sectionKeys.size() == 0) {
			section.set("Join Message", "§6{PLAYERNAME} §ejoined the game");
			section.set("Leave Message", "§6{PLAYERNAME} §eleft the game");
			try {
				configFileYaml.save(configFile);
			} catch (IOException e) {
				serverLog(Level.SEVERE, "TrafficMessager: Cannot save file \"" + configFileName + "\"!" + e);
			}
		}
		joinMessage = section.getString("Join Message");
		leaveMessage = section.getString("Leave Message");

	}

	private void serverLog(Level lvl, String msg) {
		Bukkit.getServer().getLogger().log(lvl, msg);
	}
}