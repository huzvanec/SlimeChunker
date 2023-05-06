package cz.jeme.programu.slimechunker;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class SlimeChunker extends JavaPlugin implements Listener {

    public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + ChatColor.BOLD + "SlimeChunker"
            + ChatColor.DARK_GRAY + "]: ";

    private Config config;

    @Override
    public void onEnable() {
        config = new Config(getDataFolder());
        PluginCommand slimechunk = getCommand("slimechunk");
        PluginCommand slimechunker = getCommand("slimechunker");
        assert slimechunk != null : "Couldn't find slimechunk command!";
        assert slimechunker != null : "Couldn't find slimechunker command!";
        slimechunk.setTabCompleter(new CommandTabCompleter());
        slimechunker.setTabCompleter(new CommandTabCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        String commandName = command.getName();
        if (commandName.equalsIgnoreCase("slimechunk")) {
            slimechunkCommand(sender);
            return true;
        }
        if (commandName.equalsIgnoreCase("slimechunker")) {
            slimechunkerCommand(sender, args);
            return true;
        }
        return false;
    }

    private void slimechunkCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "This command is only runnable as a player!");
            return;
        }
        Player player = (Player) sender;

        Chunk chunk = player.getLocation().getChunk();

        World world = player.getWorld();

        String message;
        if (world.getEnvironment() == Environment.NORMAL) {
            if (chunk.isSlimeChunk()) {
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
    }

    private void slimechunkerCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Not enough arguments!");
            return;
        }
        if (args.length > 1) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Too many arguments!");
            return;
        }
        if (args[0].equals("reload")) {
            config.refreshConfig();
            sender.sendMessage(PREFIX + ChatColor.GREEN + "Config reloaded!");
            return;
        }
        sender.sendMessage(PREFIX + ChatColor.RED + "Unknown command!");
    }

    public static void serverLog(Level level, String message) {
        Bukkit.getServer().getLogger().log(level, ChatColor.stripColor(PREFIX) + message);
    }
}