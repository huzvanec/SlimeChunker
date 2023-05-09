package cz.jeme.programu.slimechunker;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlimeChunkCompassRunnable extends BukkitRunnable {
    private final DecimalFormat formatter = new DecimalFormat("00.00");

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = player.getWorld();
            if (!hasSlimeChunkCompass(player)) {
                player.setCompassTarget(world.getSpawnLocation());
                continue;
            }
            if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                String message = Config.yaml.getString("messages.map-right-click-block");
                if (message == null) throw new NullPointerException("messages.map-right-click-block is null!");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                return;
            }
            Location coords = SlimeChunkCompassRunnable.findClosestSlimeChunkCoords(player);
            if (coords == null) {
                String message = Config.yaml.getString("messages.compass-no-slime-chunks");
                if (message == null) throw new NullPointerException("messages.compass-no-slime-chunks is null!");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(SlimeChunker.translateColor(message)));
                return;
            }

            player.setCompassTarget(coords);
            double distance = player.getLocation().distance(coords);
            double x = coords.getX();
            double z = coords.getZ();
            String text = "X: " + x + "; Z: " + z + "; Distance: " + formatter.format(distance) + " blocks";
            ChatColor chatColor;
            if (player.getLocation().getChunk().isSlimeChunk()) {
                chatColor = ChatColor.GREEN;
            } else {
                chatColor = ChatColor.RED;
            }
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(chatColor + text));
        }
    }

    public static boolean hasSlimeChunkCompass(Player player) {
        PlayerInventory inventory = player.getInventory();
        return isSlimeChunkCompass(inventory.getItemInMainHand()) || isSlimeChunkCompass(inventory.getItemInOffHand());
    }

    public static boolean isSlimeChunkCompass(ItemStack item) {
        return (item.isSimilar(SlimeChunker.slimeChunkCompass));
    }

    public static List<Chunk> findClosestSlimeChunks(Player player) {
        World world = player.getWorld();
        Chunk currentChunk = player.getLocation().getChunk();
        if (currentChunk.isSlimeChunk()) {
            return Collections.singletonList(currentChunk);
        }
        int currentX = currentChunk.getX();
        int currentZ = currentChunk.getZ();
        for (int i = 1; i < 5; i++) {
            List<Chunk> chunks = new ArrayList<>();
            int xi = currentX - i;
            int zi = currentZ + i - 1;
            while (xi <= currentX + i) {
                chunks.add(world.getChunkAt(xi, currentZ - i));
                chunks.add(world.getChunkAt(xi, currentZ + i));
                xi++;
            }
            while (zi > currentZ - i) {
                chunks.add(world.getChunkAt(currentX - i, zi));
                chunks.add(world.getChunkAt(currentX + i, zi));
                zi--;
            }
            List<Chunk> slimeChunks = new ArrayList<>();
            for (Chunk chunk : chunks) {
                if (chunk.isSlimeChunk()) {
                    slimeChunks.add(chunk);
                }
            }
            if (slimeChunks.size() > 0) return slimeChunks;
        }
        return null;
    }

    public static Location findClosestSlimeChunkCoords(Player player) {
        List<Chunk> chunks = findClosestSlimeChunks(player);
        if (chunks == null) return null;
        double lowestDistance = Double.MAX_VALUE;
        Location closestLocation = null;
        for (Chunk chunk : chunks) {
            Block block = chunk.getBlock(0, player.getLocation().getBlockY(), 0);
            Location location = new Location(player.getWorld(), block.getX() + 8.5, player.getLocation().getY(), block.getZ() + 8.5);
            double distance = player.getLocation().distance(location);
            if (distance < lowestDistance) {
                lowestDistance = distance;
                closestLocation = location;
            }
        }
        return closestLocation;
    }
}
