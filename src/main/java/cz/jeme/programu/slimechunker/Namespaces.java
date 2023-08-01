package cz.jeme.programu.slimechunker;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public enum Namespaces {
    SLIME_CHUNK_EMPTY_MAP(SlimeChunker.namespacedKey("slime_chunk_empty_map"), PersistentDataType.BOOLEAN),
    SLIME_CHUNK_FILLED_MAP(SlimeChunker.namespacedKey("slime_chunk_filled_map"), PersistentDataType.BOOLEAN),
    SLIME_CHUNK_COMPASS(SlimeChunker.namespacedKey("slime_chunk_compass"), PersistentDataType.BOOLEAN);

    public final NamespacedKey namespacedKey;
    public final String key;
    public final PersistentDataType<?, ?> type;

    Namespaces(NamespacedKey namespacedKey, PersistentDataType<?, ?> type) {
        this.namespacedKey = namespacedKey;
        this.key = namespacedKey.getKey();
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public <T, Z> void set(@NotNull PersistentDataHolder holder, @NotNull Z value) {
        holder.getPersistentDataContainer().set(namespacedKey, (PersistentDataType<T, Z>) type, value);
    }

    public <Z> void set(@NotNull ItemStack item, @NotNull Z value) {
        ItemMeta meta = item.getItemMeta();
        set(meta, value);
        item.setItemMeta(meta);
    }

    @SuppressWarnings("unchecked")
    public <T, Z> Z get(PersistentDataHolder holder) {
        if (holder == null) return null;
        return holder.getPersistentDataContainer().get(namespacedKey, (PersistentDataType<T, Z>) type);
    }

    public <Z> Z get(@NotNull ItemStack item) {
        return get(item.getItemMeta());
    }

    public boolean has(PersistentDataHolder holder) {
        if (holder == null) return false;
        return holder.getPersistentDataContainer().has(namespacedKey);
    }

    public boolean has(@NotNull ItemStack item) {
        return has(item.getItemMeta());
    }
}
