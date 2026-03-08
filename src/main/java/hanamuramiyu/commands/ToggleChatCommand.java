package hanamuramiyu.commands;

import hanamuramiyu.core.NekoChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToggleChatCommand implements TabExecutor {

    private final NekoChat plugin;

    public ToggleChatCommand(NekoChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("nekochat.admin")) {
            sender.sendMessage(plugin.getMiniMessage(sender instanceof Player ? (Player) sender : null, "no-permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getMiniMessage(sender instanceof Player ? (Player) sender : null, "togglechat-usage"));
            return true;
        }

        String target = args[0].toLowerCase();
        switch (target) {
            case "global" -> {
                boolean newState = !plugin.getChatManager().isGlobalEnabled();
                plugin.getChatManager().setGlobalEnabled(newState);
                sender.sendMessage(plugin.getMiniMessage(sender instanceof Player ? (Player) sender : null, newState ? "togglechat-global-on" : "togglechat-global-off"));
            }
            case "local" -> {
                boolean newState = !plugin.getChatManager().isLocalEnabled();
                plugin.getChatManager().setLocalEnabled(newState);
                sender.sendMessage(plugin.getMiniMessage(sender instanceof Player ? (Player) sender : null, newState ? "togglechat-local-on" : "togglechat-local-off"));
            }
            case "all" -> {
                boolean newState = !(plugin.getChatManager().isGlobalEnabled() && plugin.getChatManager().isLocalEnabled());
                plugin.getChatManager().setGlobalEnabled(newState);
                plugin.getChatManager().setLocalEnabled(newState);
                sender.sendMessage(plugin.getMiniMessage(sender instanceof Player ? (Player) sender : null, newState ? "togglechat-all-on" : "togglechat-all-off"));
            }
            default -> sender.sendMessage(plugin.getMiniMessage(sender instanceof Player ? (Player) sender : null, "togglechat-usage"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partial = args[0].toLowerCase();
            if ("global".startsWith(partial)) completions.add("global");
            if ("local".startsWith(partial)) completions.add("local");
            if ("all".startsWith(partial)) completions.add("all");
            return completions;
        }
        return List.of();
    }
}