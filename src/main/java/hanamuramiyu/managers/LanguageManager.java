package hanamuramiyu.managers;

import hanamuramiyu.config.ConfigManager;
import hanamuramiyu.core.NekoChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageManager implements Listener {
    private final NekoChat plugin;
    private final ConfigManager configManager;
    private final Map<UUID, String> playerLanguageCache = new ConcurrentHashMap<>();
    private boolean autoDetect;

    public LanguageManager(NekoChat plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        reload();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerLanguageCache.remove(event.getPlayer().getUniqueId());
    }

    public void reload() {
        playerLanguageCache.clear();
        autoDetect = plugin.getConfigManager().getBukkitConfig().getBoolean("auto-detect-client-language", true);
    }

    public String getLanguage(Player player) {
        return playerLanguageCache.computeIfAbsent(player.getUniqueId(), k -> detectLanguage(player));
    }

    public void updatePlayerLanguage(Player player) {
        playerLanguageCache.put(player.getUniqueId(), detectLanguage(player));
    }

    private String detectLanguage(Player player) {
        if (autoDetect) {
            String locale = player.getLocale();
            if (locale != null && !locale.isEmpty()) {
                String[] parts = locale.split("_");
                if (parts.length == 2) {
                    String lang = parts[0].toLowerCase() + "-" + parts[1].toUpperCase();
                    if (configManager.getLanguageConfigs().containsKey(lang)) {
                        return lang;
                    }
                }
            }
        }
        return configManager.getDefaultLanguage();
    }
}