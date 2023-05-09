package cz.jeme.programu.slimechunker;

import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.List;

public class EventListener implements Listener {

    public static final int MAP_ID = 69; // Haha funny id

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (event.getItem() == null) return;
        if (!event.getItem().isSimilar(SlimeChunker.slimeChunkMap)) return;
        if (!Config.yaml.getBoolean("items.map.enabled")) return;
        event.setCancelled(true);
        if (action == Action.RIGHT_CLICK_BLOCK) {
            String message = Config.yaml.getString("messages.map-right-click-block");
            if (message == null) throw new NullPointerException("messages.map-right-click-block is null!");
            player.sendMessage(SlimeChunker.translateColor(message));
            return;
        }
        MapView map = Bukkit.createMap(player.getWorld());
        map.setScale(MapView.Scale.CLOSEST);
        map.setTrackingPosition(true);
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();
        if (x >= 0) {
            map.setCenterX(Math.round(x / 128F) * 128);
        } else {
            map.setCenterX(Math.round((x - 1) / 128F) * 128);
        }
        if (z >= 0) {
            map.setCenterZ(Math.round(z / 128F) * 128);
        } else {
            map.setCenterZ(Math.round((z - 1) / 128F) * 128);
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack eventItem = event.getItem();
            eventItem.setAmount(eventItem.getAmount() - 1);
        }
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        assert meta != null;
        List<String> lore = Config.yaml.getStringList("items.map.filled-lore");
        List<String> loreTranslated = new ArrayList<>();
        for (String loreLine : lore) {
            loreTranslated.add(SlimeChunker.translateColor(loreLine));
        }
        meta.setLore(loreTranslated);
        meta.setDisplayName(ChatColor.WHITE + SlimeChunker.translateColor(Config.yaml.getString("items.map.filled-name")));
        meta.setCustomModelData(MAP_ID);
        List<Integer> rgb = Config.yaml.getIntegerList("items.map.text-color");
        meta.setColor(Color.fromRGB(rgb.get(0), rgb.get(1), rgb.get(2)));
        meta.setMapView(map);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    @EventHandler
    private void onCraftItem(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        HumanEntity player = event.getWhoClicked();
        if (item == null) return;
        boolean hasPermission = true;
        if (item.isSimilar(SlimeChunker.slimeChunkMap)) {
            if (Config.yaml.getBoolean("items.map.crafting.require-permission")) {
                hasPermission = player.hasPermission("slimechunker.item.map");
            }
        } else if (item.isSimilar(SlimeChunker.slimeChunkCompass)) {
            if (Config.yaml.getBoolean("items.compass.crafting.require-permission")) {
                hasPermission = player.hasPermission("slimechunker.item.compass");
            }
        }
        if (!hasPermission) {
            event.setCancelled(true);
            String message = Config.yaml.getString("messages.crafting-not-allowed");
            player.sendMessage(SlimeChunker.translateColor(message));
        }
    }
}
