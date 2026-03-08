package hanamuramiyu.managers;

import hanamuramiyu.core.NekoChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager implements Listener {
    private final NekoChat plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager(NekoChat plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startCleanupTask();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }

    private void startCleanupTask() {
        plugin.getSchedulerManager().runTaskTimerAsync(() -> {
            long now = System.currentTimeMillis();
            cooldowns.entrySet().removeIf(entry -> {
                Map<String, Long> playerCDs = entry.getValue();
                playerCDs.entrySet().removeIf(cd -> {
                    int cdSeconds = getCooldownSeconds(cd.getKey());
                    return now >= cd.getValue() + cdSeconds * 1000L;
                });
                return playerCDs.isEmpty();
            });
        }, 6000L, 6000L);
    }

    public boolean isOnCooldown(UUID uuid, String action) {
        Map<String, Long> playerCD = cooldowns.get(uuid);
        if (playerCD == null) return false;
        Long last = playerCD.get(action);
        if (last == null) return false;
        int cd = getCooldownSeconds(action);
        return System.currentTimeMillis() < last + cd * 1000L;
    }

    public void setCooldown(UUID uuid, String action) {
        cooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(action, System.currentTimeMillis());
    }

    public int getRemainingSeconds(UUID uuid, String action) {
        Map<String, Long> playerCD = cooldowns.get(uuid);
        if (playerCD == null) return 0;
        Long last = playerCD.get(action);
        if (last == null) return 0;
        int cd = getCooldownSeconds(action);
        long remaining = (last + cd * 1000L - System.currentTimeMillis()) / 1000L;
        return (int) Math.max(0, remaining);
    }

    public void clearCooldowns(UUID uuid) {
        cooldowns.remove(uuid);
    }

    public void clearAll() {
        cooldowns.clear();
    }

    private int getCooldownSeconds(String action) {
        FileConfiguration config = plugin.getConfigManager().getBukkitConfig();
        return switch (action) {
            case "chat" -> config.getInt("cooldowns.chat", 0);
            case "msg" -> config.getInt("cooldowns.msg", 1);
            case "reply" -> config.getInt("cooldowns.reply", 1);
            default -> 0;
        };
    }
}