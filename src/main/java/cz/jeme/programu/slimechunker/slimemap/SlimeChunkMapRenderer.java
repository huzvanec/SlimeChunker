package cz.jeme.programu.slimechunker.slimemap;

import cz.jeme.programu.slimechunker.SlimeChunker;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class SlimeChunkMapRenderer extends MapRenderer {
    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        MapView.Scale scale = map.getScale();
        int chunks = switch (scale) {
            case CLOSEST -> 8;
            case CLOSE -> 16;
            case NORMAL -> 32;
            case FAR -> 64;
            case FARTHEST -> 128;
        };

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
                            int r = SlimeChunker.config.getInt("items.map.map-color.r");
                            int g = SlimeChunker.config.getInt("items.map.map-color.g");
                            int b = SlimeChunker.config.getInt("items.map.map-color.b");
                            canvas.setPixelColor(xChunk * (16 / scale) + x, zChunk * (16 / scale) + z, new Color(r, g, b));
                        }
                    }
                }
            }
        }


    }
}
