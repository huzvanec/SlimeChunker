package cz.jeme.programu.slimechunker;

import cz.jeme.programu.slimechunker.slimemap.SlimeChunkMapRunnable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public class SlimeChunker extends JavaPlugin {
    public static final ItemStack SLIME_CHUNK_COMPASS = new ItemStack(Material.COMPASS);
    public static final ItemStack SLIME_CHUNK_MAP = new ItemStack(Material.MAP);

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reload();

        new SlimeChunkCommand();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EventListener(), this);

        Namespaces.SLIME_CHUNK_EMPTY_MAP.set(SLIME_CHUNK_MAP, true);
        Namespaces.SLIME_CHUNK_COMPASS.set(SLIME_CHUNK_COMPASS, true);

        if (config.getBoolean("items.compass.enabled")) {
            new SlimeChunkCompassRunnable().runTaskTimer(this, 0L, 1L);
            registerItem(SLIME_CHUNK_COMPASS, "compass", "slime_chunk_compass");
        }

        if (config.getBoolean("items.map.enabled")) {
            new SlimeChunkMapRunnable().runTaskTimer(this, 0L, 1L);
            registerItem(SLIME_CHUNK_MAP, "map", "slime_chunk_map");
        }
    }

    public static void reload() {
        config = getPlugin(SlimeChunker.class).getConfig();
    }

    public void registerItem(ItemStack item, String name, String key) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.displayName(Messages.from(config.getString("items." + name + ".name")));
        List<String> loreStr = config.getStringList("items." + name + ".lore");
        List<Component> lore = loreStr.stream()
                .map(Messages::from)
                .toList();
        meta.lore(lore);
        item.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, key), item);
        List<String> crafting = config.getStringList("items." + name + ".crafting.recipe");
        recipe.shape(
                crafting.get(0),
                crafting.get(1),
                crafting.get(2)
        );
        List<String> ingredients = config.getStringList("items." + name + ".crafting.ingredients");
        for (String ingredientLine : ingredients) {
            String[] ingredient = ingredientLine.split("=");
            Material material = Material.getMaterial(ingredient[1].toUpperCase());
            if (material == null) throw new NullPointerException("Unknown material: " + ingredient[1]);
            recipe.setIngredient(ingredient[0].charAt(0), material);
        }
        Bukkit.addRecipe(recipe);
    }

    public static boolean isSlimeChunk(long seed, int xBlock, int zBlock) {
        int xChunk = xBlock >> 4;
        int zChunk = zBlock >> 4;
        Random random = new Random(seed + (xChunk * xChunk * 4987142) + (xChunk * 5947611) + (zChunk * zChunk) * 4392871L + (zChunk * 389711) ^ 0x3AD8025FL);
        return (random.nextInt(10) == 0);
    }

    public static NamespacedKey namespacedKey(String key) {
        return new NamespacedKey(getPlugin(SlimeChunker.class), key);
    }
}