package cz.jeme.programu.slimechunker;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World.Environment;

public class SlimeChunker extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("slimechunk")) { // Command was sent
			if (sender instanceof ConsoleCommandSender) {
				sender.sendMessage("This command is only runnable as player");
				return true;
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				Chunk chunk = player.getLocation().getChunk();

				String playerName = player.getName();
				int chunkX = chunk.getX();
				int chunkZ = chunk.getZ();
				long seed = player.getWorld().getSeed();

				if (player.getWorld().getEnvironment() == Environment.NORMAL) {

					if (isSlimeChunk(chunkX, chunkZ, seed)) {
						tellraw(playerName, "{\"text\":\"You found a slime chunk! Yeey!\",\"color\":\"#55FF00\"}");
					} else {
						tellraw(playerName, "{\"text\":\"No slimechunks here. Keep looking!\",\"color\":\"#FF0003\"}");
					}
				} else {
					tellraw(playerName, "{\"text\":\"You can't use SlimeChunker in this dimension!\",\"color\":\"#FF0003\"}");
				}
				return true;
			}
		}
		return false;
	}

	private boolean isSlimeChunk(int x, int z, long seed) {
		Random rnd = new Random(seed + (long) (x * x * 0x4c1906) + (long) (x * 0x5ac0db) + (long) (z * z) * 0x4307a7L
				+ (long) (z * 0x5f24f) ^ 0x3ad8025fL);
		return rnd.nextInt(10) == 0;

	}

	private void tellraw(String selector, String message) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + selector + " " + message);
	}
}