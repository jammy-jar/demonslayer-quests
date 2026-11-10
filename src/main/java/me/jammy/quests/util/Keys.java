package me.jammy.quests.util;

import me.jammy.quests.OasisQuests;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class Keys<T> {

    public static final Keys<String> CUSTOM_ITEM = string("custom_item");
    public static final Keys<String> QUEST = string("quest");

    private final PersistentDataType<T, T> type;
    private final NamespacedKey key;

    public static <T> Keys<T> of(PersistentDataType<T, T> type, String key) {
        return new Keys<>(type, key);
    }

    public static Keys<String> string(String key) {
        return new Keys<>(PersistentDataType.STRING, key);
    }

    public static Keys<Integer> integer(String key) {
        return new Keys<>(PersistentDataType.INTEGER, key);
    }

    private Keys(PersistentDataType<T, T> type, String key) {
        this(type, new NamespacedKey(OasisQuests.instance(), key));
    }

    private Keys(PersistentDataType<T, T> type, NamespacedKey key) {
        this.type = type;
        this.key = key;
    }

    public T read(PersistentDataHolder holder) {
        if (holder == null)
            return null;
        return holder.getPersistentDataContainer().get(key, type);
    }

    public void write(PersistentDataHolder holder, T value) {
        if (holder != null)
            holder.getPersistentDataContainer().set(key, type, value);
    }

    public void remove(PersistentDataHolder holder) {
        if (holder != null)
            holder.getPersistentDataContainer().remove(key);
    }

    public boolean has(PersistentDataHolder holder) {
        return holder != null && holder.getPersistentDataContainer().has(key, type);
    }

}
