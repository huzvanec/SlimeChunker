package cz.jeme.programu.slimechunker;

import cz.jeme.programu.slimechunker.slimemap.SlimeChunkMapRunnable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class SlimeChunker extends JavaPlugin {

    public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + ChatColor.BOLD + "SlimeChunker"
            + ChatColor.DARK_GRAY + "]: ";

    public static ItemStack slimeChunkCompass = new ItemStack(Material.COMPASS);
    public static ItemStack slimeChunkMap = new ItemStack(Material.MAP);


    @Override
    public void onEnable() {
        saveDefaultConfig();
        new Config(getDataFolder());

        PluginCommand slimechunk = getCommand("slimechunk");
        assert slimechunk != null : "Couldn't find slimechunk command!";
        slimechunk.setTabCompleter(new CommandTabCompleter());

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EventListener(), this);

        if (Config.yaml.getBoolean("items.compass.enabled")) {
            new SlimeChunkCompassRunnable().runTaskTimer(this, 0L, 1L);
            registerItem(slimeChunkCompass, "compass", "slime_chunk_compass");
        }

        if (Config.yaml.getBoolean("items.map.enabled")) {
            new SlimeChunkMapRunnable().runTaskTimer(this, 0L, 1L);
            registerItem(slimeChunkMap, "map", "slime_chunk_map");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        String commandName = command.getName();
        if (commandName.equalsIgnoreCase("slimechunk")) {
            slimechunkCommand(sender);
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
                message = Config.yaml.getString("messages.is-a-slime-chunk");
            } else {
                message = Config.yaml.getString("messages.not-a-slime-chunk");
            }
        } else {
            message = Config.yaml.getString("messages.wrong-dimension");
        }
        if (message == null) throw new NullPointerException("message is null!");
        player.sendMessage(translateColor(message));
    }

    public void registerItem(ItemStack item, String name, String key) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.WHITE + translateColor(Config.yaml.getString("items." + name + ".name")));
        List<String> lore = Config.yaml.getStringList("items." + name + ".lore");
        List<String> loreTranslated = new ArrayList<>();
        for (String loreLine : lore) {
            loreTranslated.add(translateColor(loreLine));
        }
        meta.setLore(loreTranslated);
        item.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, key), item);
        List<String> crafting = Config.yaml.getStringList("items." + name + ".crafting.recipe");
        recipe.shape(
                crafting.get(0),
                crafting.get(1),
                crafting.get(2)
        );
        List<String> ingredients = Config.yaml.getStringList("items." + name + ".crafting.ingredients");
        for (String ingredientLine : ingredients) {
            String[] ingredient = ingredientLine.split("=");
            Material material = Material.getMaterial(ingredient[1].toUpperCase());
            if (material == null) throw new NullPointerException("Unknown material: " + ingredient[1]);
            recipe.setIngredient(ingredient[0].charAt(0), material);
        }
        Bukkit.addRecipe(recipe);
    }

    public static void serverLog(Level level, String message) {
        Bukkit.getServer().getLogger().log(level, ChatColor.stripColor(PREFIX) + message);
    }

    public static boolean isSlimeChunk(long seed, int xBlock, int zBlock) {
        int xChunk = xBlock >> 4;
        int zChunk = zBlock >> 4;
        Random random = new Random(seed + (xChunk * xChunk * 4987142) + (xChunk * 5947611) + (zChunk * zChunk) * 4392871L + (zChunk * 389711) ^ 0x3AD8025FL);
        return (random.nextInt(10) == 0);
    }

    public static String translateColor(String string) {
        String paragraph = ChatColor.translateAlternateColorCodes('§', string);
        return ChatColor.translateAlternateColorCodes('&', paragraph);
    }
}