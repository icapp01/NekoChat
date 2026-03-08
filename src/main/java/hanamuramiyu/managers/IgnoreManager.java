package hanamuramiyu.managers;

import hanamuramiyu.core.NekoChat;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IgnoreManager implements Listener {
    private final NekoChat plugin;
    private final NamespacedKey ignoredKey;
    private final Map<UUID, Set<UUID>> ignoredCache = new ConcurrentHashMap<>();

    public IgnoreManager(NekoChat plugin) {
        this.plugin = plugin;
        this.ignoredKey = new NamespacedKey(plugin, "ignored");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ignoredCache.remove(event.getPlayer().getUniqueId());
    }

    private void loadPlayer(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        List<String> ignoredStrings = pdc.get(ignoredKey, PersistentDataType.LIST.strings());
        Set<UUID> set = new HashSet<>();
        if (ignoredStrings != null) {
            for (String s : ignoredStrings) {
                try {
                    set.add(UUID.fromString(s));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        ignoredCache.put(player.getUniqueId(), set);
    }

    public boolean isIgnoring(UUID ignorer, UUID ignored) {
        Set<UUID> set = ignoredCache.get(ignorer);
        return set != null && set.contains(ignored);
    }

    public void addIgnore(Player player, UUID target) {
        Set<UUID> set = ignoredCache.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (set.add(target)) {
            savePlayer(player);
        }
    }

    public void removeIgnore(Player player, UUID target) {
        Set<UUID> set = ignoredCache.get(player.getUniqueId());
        if (set != null && set.remove(target)) {
            if (set.isEmpty()) {
                ignoredCache.remove(player.getUniqueId());
                player.getPersistentDataContainer().remove(ignoredKey);
            } else {
                savePlayer(player);
            }
        }
    }

    private void savePlayer(Player player) {
        Set<UUID> set = ignoredCache.get(player.getUniqueId());
        if (set == null || set.isEmpty()) {
            player.getPersistentDataContainer().remove(ignoredKey);
            return;
        }
        List<String> list = new ArrayList<>();
        for (UUID uuid : set) {
            list.add(uuid.toString());
        }
        player.getPersistentDataContainer().set(ignoredKey, PersistentDataType.LIST.strings(), list);
    }

    public Set<UUID> getIgnored(Player player) {
        return new HashSet<>(ignoredCache.getOrDefault(player.getUniqueId(), Collections.emptySet()));
    }
}