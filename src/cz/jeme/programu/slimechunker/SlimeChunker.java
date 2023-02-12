package cz.jeme.programu.slimechunker;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class SlimeChunker extends JavaPlugin implements Listener {

	public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + ChatColor.BOLD + "SlimeChunker"
			+ ChatColor.DARK_GRAY + "]: ";

	private Config config;

	@Override
	public void onEnable() {
		config = new Config(getDataFolder());
		getCommand("slimechunk").setTabCompleter(new CommandTabCompleter());
		getCommand("slimechunker").setTabCompleter(new CommandTabCompleter());
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String commandName = command.getName();
		if (commandName.equalsIgnoreCase("slimechunk")) {
			return slimechunkCommand(sender);
		}
		if (commandName.equalsIgnoreCase("slimechunker")) {
			return slimechunkerCommand(sender, args);
		}
		return false;
	}

	private boolean slimechunkCommand(CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(PREFIX + ChatColor.RED + "This command is only runnable as a player!");
			return true;
		}
		Player player = (Player) sender;

		Chunk chunk = player.getLocation().getChunk();
		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();

		World world = player.getWorld();

		long seed = world.getSeed();

		String message;
		if (world.getEnvironment() == Environment.NORMAL) {
			if (isSlimeChunk(chunkX, chunkZ, seed)) {
				message = config.yesMessage;
			} else {
				message = config.noMessage;
			}
		} else {
			message = config.wrongEnviromentMessage;
		}
		message = ChatColor.translateAlternateColorCodes('§', message);
		message = ChatColor.translateAlternateColorCodes('&', message);
		player.sendMessage(message);
		return true;
	}

	private boolean slimechunkerCommand(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(PREFIX + ChatColor.RED + "Not enough arguments!");
			return true;
		}
		if (args.length > 1) {
			sender.sendMessage(PREFIX + ChatColor.RED + "Too many arguments!");
			return true;
		}
		if (args[0].equals("reload")) {
			config.refreshConfig();
			sender.sendMessage(PREFIX + ChatColor.GREEN + "Config reloaded!");
			return true;
		}
		sender.sendMessage(PREFIX + ChatColor.RED + "Unknown command!");
		return true;
	}

	private boolean isSlimeChunk(int chunkX, int chunkZ, long seed) {
		Random random = new Random(seed + (long) (chunkX * chunkX * 0x4c1906) + (long) (chunkX * 0x5ac0db)
				+ (long) (chunkZ * chunkZ) * 0x4307a7L + (long) (chunkZ * 0x5f24f) ^ 0x3ad8025fL);
		return random.nextInt(10) == 0;
	}

	public static void serverLog(Level level, String message) {
		Bukkit.getServer().getLogger().log(level, ChatColor.stripColor(PREFIX) + message);
	}
}