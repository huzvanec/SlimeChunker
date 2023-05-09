package cz.jeme.programu.slimechunker.slimemap;

import cz.jeme.programu.slimechunker.Config;
import cz.jeme.programu.slimechunker.EventListener;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SlimeChunkMapRunnable extends BukkitRunnable {
    private final List<MapView> maps = new ArrayList<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
            if (!(meta instanceof MapMeta)) return;
            if (!((MapMeta) meta).hasMapView()) return;
            MapView map = ((MapMeta) meta).getMapView();
            String mapName = ChatColor.stripColor(Config.yaml.getString("items.map.filled-name"));
            String itemName = ChatColor.stripColor(meta.getDisplayName());
            if (!itemName.equals(mapName)) return;
            if (meta.getCustomModelData() != EventListener.MAP_ID) return;
            if (map == null) return;
            if (maps.contains(map)) return;
            for (MapRenderer renderer : map.getRenderers()) {
                if (renderer instanceof SlimeChunkMapRenderer) return;
            }
            map.addRenderer(new SlimeChunkMapRenderer());
            maps.add(map);
        }
    }
}
