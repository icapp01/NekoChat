package hanamuramiyu.listeners;

import hanamuramiyu.core.NekoChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {
    private final NekoChat plugin;
    private boolean joinEnabled;
    private boolean quitEnabled;

    public JoinLeaveListener(NekoChat plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfigManager().getBukkitConfig();
        joinEnabled = config.getBoolean("chat.join.enabled", true);
        quitEnabled = config.getBoolean("chat.quit.enabled", true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getLanguageManager().updatePlayerLanguage(player);

        if (!joinEnabled) {
            event.joinMessage(null);
            return;
        }

        Component message = plugin.getFormatComponent(player, "join-format",
                Placeholder.component("player", player.displayName()),
                Placeholder.unparsed("username", player.getName())
        );
        event.joinMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPrivateMessageManager().clearLastSender(player.getUniqueId());
        plugin.getCooldownManager().clearCooldowns(player.getUniqueId());

        if (!quitEnabled) {
            event.quitMessage(null);
            return;
        }

        Component message = plugin.getFormatComponent(player, "quit-format",
                Placeholder.component("player", player.displayName()),
                Placeholder.unparsed("username", player.getName())
        );
        event.quitMessage(message);
    }
}