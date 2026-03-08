package hanamuramiyu.commands;

import hanamuramiyu.core.NekoChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IgnoreCommand implements CommandExecutor {
    private final NekoChat plugin;

    public IgnoreCommand(NekoChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getMiniMessage(player, "ignore-usage"));
            return true;
        }

        String targetName = args[0];
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(plugin.getMiniMessage(player, "player-not-found")
                    .replaceText(b -> b.matchLiteral("<player>").replacement(targetName)));
            return true;
        }

        if (player.equals(target)) {
            player.sendMessage(plugin.getMiniMessage(player, "ignore-self"));
            return true;
        }

        if (plugin.getIgnoreManager().isIgnoring(player.getUniqueId(), target.getUniqueId())) {
            player.sendMessage(plugin.getMiniMessage(player, "ignore-already")
                    .replaceText(b -> b.matchLiteral("<player>").replacement(target.getName())));
            return true;
        }

        plugin.getIgnoreManager().addIgnore(player, target.getUniqueId());
        player.sendMessage(plugin.getMiniMessage(player, "ignore-added")
                .replaceText(b -> b.matchLiteral("<player>").replacement(target.getName())));
        return true;
    }
}