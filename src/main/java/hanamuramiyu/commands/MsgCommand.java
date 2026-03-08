package hanamuramiyu.commands;

import hanamuramiyu.core.NekoChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MsgCommand implements CommandExecutor {

    private final NekoChat plugin;

    public MsgCommand(NekoChat plugin) {
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

        if (args.length < 2) {
            player.sendMessage(plugin.getMiniMessage(player, "msg-usage"));
            return true;
        }

        String targetName = args[0];
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        plugin.getPrivateMessageManager().sendPrivateMessage(player, targetName, message);
        return true;
    }
}