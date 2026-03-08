package hanamuramiyu.hooks;

import hanamuramiyu.core.NekoChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook {
    private static boolean enabled = false;

    public static void init(NekoChat plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            enabled = true;
            plugin.getLogger().info("PlaceholderAPI found, enabling placeholders support.");
        }
    }

    public static String apply(Player player, String text) {
        if (!enabled || player == null) return text;
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
    }
}