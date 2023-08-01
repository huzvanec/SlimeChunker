package cz.jeme.programu.slimechunker;

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
                String message = SlimeChunker.config.getString("messages.wrong-dimension");
                if (message == null) throw new NullPointerException("messages.wrong-dimension is null!");
                player.sendActionBar(Messages.from(message));
                return;
            }
            Location coords = SlimeChunkCompassRunnable.findClosestSlimeChunkCoords(player);
            if (coords == null) {
                String message = SlimeChunker.config.getString("messages.compass-no-slime-chunks");
                if (message == null) throw new NullPointerException("messages.compass-no-slime-chunks is null!");
                player.sendActionBar(Messages.from(message));
                return;
            }

            player.setCompassTarget(coords);
            double distance = player.getLocation().distance(coords);
            double x = coords.getX();
            double z = coords.getZ();
            String text = "X: " + x + "; Z: " + z + "; Distance: " + formatter.format(distance) + " blocks";
            String color;
            if (player.getLocation().getChunk().isSlimeChunk()) {
                color = "<green>";
            } else {
                color = "<red>";
            }
            player.sendActionBar(Messages.from(color + text + Messages.getEscapeTag(color)));
        }
    }

    public static boolean hasSlimeChunkCompass(Player player) {
        PlayerInventory inventory = player.getInventory();
        return isSlimeChunkCompass(inventory.getItemInMainHand()) || isSlimeChunkCompass(inventory.getItemInOffHand());
    }

    public static boolean isSlimeChunkCompass(ItemStack item) {
        return (Namespaces.SLIME_CHUNK_COMPASS.has(item));
    }

    public static List<Chunk> findClosestSlimeChunks(Player player) {
        World world = player.getWorld();
        Chunk currentChunk = player.getLocation().getChunk();
        if (currentChunk.isSlimeChunk()) {
            return Collections.singletonList(currentChunk);
        }
        int currentX = currentChunk.getX();
        int currentZ = currentChunk.getZ();
        for (int i = 1; i < SlimeChunker.config.getInt("items.compass.max-chunk-area"); i++) {
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
