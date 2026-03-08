package hanamuramiyu.listeners;

import hanamuramiyu.core.NekoChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    private final NekoChat plugin;
    private boolean deathEnabled;
    private boolean showReason;

    public DeathListener(NekoChat plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfigManager().getBukkitConfig();
        deathEnabled = config.getBoolean("chat.death.enabled", true);
        showReason = config.getBoolean("chat.death.show-reason", true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!deathEnabled) {
            event.deathMessage(null);
            return;
        }

        Component reason = showReason && event.deathMessage() != null ? event.deathMessage() : Component.empty();
        Component message = plugin.getFormatComponent(event.getPlayer(), "death-format",
                Placeholder.component("reason", reason)
        );
        event.deathMessage(message);
    }
}