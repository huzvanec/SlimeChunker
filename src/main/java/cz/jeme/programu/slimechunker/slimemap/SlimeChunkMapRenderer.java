package cz.jeme.programu.slimechunker.slimemap;

import cz.jeme.programu.slimechunker.Config;
import cz.jeme.programu.slimechunker.SlimeChunker;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class SlimeChunkMapRenderer extends MapRenderer {
    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        MapView.Scale scale = map.getScale();
        int chunks = 0;

        switch (scale) {
            case CLOSEST:
                chunks = 8;
                break;
            case CLOSE:
                chunks = 16;
                break;
            case NORMAL:
                chunks = 32;
                break;
            case FAR:
                chunks = 64;
                break;
            case FARTHEST:
                chunks = 128;
                break;
        }
        draw(map, canvas, player, chunks);
    }

    private void draw(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player, int chunks) {
        int scale = (chunks * 16) / 128;
        int x0 = map.getCenterX() - (chunks / 2) * 16;
        int z0 = map.getCenterZ() - (chunks / 2) * 16;

        for (int zChunk = 0; zChunk < chunks; zChunk++) {
            for (int xChunk = 0; xChunk < chunks; xChunk++) {
                if (SlimeChunker.isSlimeChunk(player.getWorld().getSeed(), x0 + 16 * xChunk, z0 + 16 * zChunk)) {
                    for (int z = 0; z < 16 / scale; z++) {
                        for (int x = 0; x < 16 / scale; x++) {
                            byte color = (byte) Config.yaml.getInt("items.map.map-color");
                            canvas.setPixel(xChunk * (16 / scale) + x, zChunk * (16 / scale) + z, color);
                        }
                    }
                }
            }
        }


    }
}
