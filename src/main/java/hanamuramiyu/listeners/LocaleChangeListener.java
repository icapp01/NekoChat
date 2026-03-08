package hanamuramiyu.listeners;

import com.destroystokyo.paper.event.player.PlayerClientOptionsChangeEvent;
import hanamuramiyu.core.NekoChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LocaleChangeListener implements Listener {

    private final NekoChat plugin;

    public LocaleChangeListener(NekoChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClientOptionsChange(PlayerClientOptionsChangeEvent event) {
        if (event.hasLocaleChanged()) {
            plugin.getLanguageManager().updatePlayerLanguage(event.getPlayer());
        }
    }
}