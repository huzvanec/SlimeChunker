package cz.jeme.programu.slimechunker;

import net.kyori.adventure.text.Component;
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

import java.util.List;
import java.util.Map;

public class EventListener implements Listener {

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        ItemStack eventItem = event.getItem();
        World world = player.getWorld();
        if (eventItem == null) return;
        if (!Namespaces.SLIME_CHUNK_EMPTY_MAP.has(eventItem)) return;
        if (!SlimeChunker.config.getBoolean("items.map.enabled")) return;
        event.setCancelled(true);
        if (action == Action.RIGHT_CLICK_BLOCK) {
            String message = SlimeChunker.config.getString("messages.map-right-click-block");
            if (message == null) throw new NullPointerException("messages.map-right-click-block is null!");
            player.sendMessage(Messages.from(message));
            return;
        }
        if (world.getEnvironment() != World.Environment.NORMAL) {
            String message = SlimeChunker.config.getString("messages.wrong-dimension");
            if (message == null) throw new NullPointerException("messages.wrong-dimension is null!");
            player.sendMessage(Messages.from(message));
            return;
        }
        MapView map = Bukkit.createMap(world);
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
            eventItem.setAmount(eventItem.getAmount() - 1);
        }
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        Namespaces.SLIME_CHUNK_FILLED_MAP.set(item, true);
        MapMeta meta = (MapMeta) item.getItemMeta();
        assert meta != null;
        List<String> loreStr = SlimeChunker.config.getStringList("items.map.filled-lore");
        List<Component> lore = loreStr.stream()
                        .map(Messages::from)
                        .toList();

        meta.lore(lore);
        meta.displayName(Messages.from(SlimeChunker.config.getString("items.map.filled-name")));
        int r = SlimeChunker.config.getInt("items.map.text-color.r");
        int g = SlimeChunker.config.getInt("items.map.text-color.g");
        int b = SlimeChunker.config.getInt("items.map.text-color.b");
        meta.setColor(Color.fromRGB(r, g, b));
        meta.setMapView(map);
        item.setItemMeta(meta);
        if (eventItem.getAmount() == 0) {
            assert event.getHand() != null;
            player.getInventory().setItem(event.getHand(), item);
        } else {
            Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);
            for (ItemStack overflowItem : overflow.values()) {
                player.getWorld().dropItem(player.getLocation(), overflowItem);
            }
        }
    }

    @EventHandler
    private void onCraftItem(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        HumanEntity player = event.getWhoClicked();
        if (item == null) return;
        boolean hasPermission = true;
        if (Namespaces.SLIME_CHUNK_EMPTY_MAP.has(item)) {
            if (SlimeChunker.config.getBoolean("items.map.crafting.require-permission")) {
                hasPermission = player.hasPermission("slimechunker.item.map");
            }
        } else if (Namespaces.SLIME_CHUNK_COMPASS.has(item)) {
            if (SlimeChunker.config.getBoolean("items.compass.crafting.require-permission")) {
                hasPermission = player.hasPermission("slimechunker.item.compass");
            }
        }
        if (!hasPermission) {
            event.setCancelled(true);
            String message = SlimeChunker.config.getString("messages.crafting-not-allowed");
            player.sendMessage(Messages.from(message));
        }
    }
}
