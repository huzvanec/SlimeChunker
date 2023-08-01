package cz.jeme.programu.slimechunker.slimemap;

import cz.jeme.programu.slimechunker.Namespaces;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!Namespaces.SLIME_CHUNK_FILLED_MAP.has(item)) return;
            MapMeta meta = (MapMeta) item.getItemMeta();
            MapView map = meta.getMapView();
            if (maps.contains(map)) return;
            assert map != null;
            for (MapRenderer renderer : map.getRenderers()) {
                if (renderer instanceof SlimeChunkMapRenderer) return;
            }
            map.addRenderer(new SlimeChunkMapRenderer());
            maps.add(map);
        }
    }
}
