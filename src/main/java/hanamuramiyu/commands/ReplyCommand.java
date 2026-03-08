package hanamuramiyu.commands;

import hanamuramiyu.core.NekoChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand implements CommandExecutor {

    private final NekoChat plugin;

    public ReplyCommand(NekoChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!plugin.getConfigManager().getBukkitConfig().getBoolean("chat.private.enabled", true)) {
            player.sendMessage(plugin.getMiniMessage(player, "no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getMiniMessage(player, "reply-usage"));
            return true;
        }

        String message = String.join(" ", args);
        plugin.getPrivateMessageManager().reply(player, message);
        return true;
    }
}