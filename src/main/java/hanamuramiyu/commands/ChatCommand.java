package hanamuramiyu.commands;

import hanamuramiyu.core.NekoChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChatCommand implements TabExecutor {
    private final NekoChat plugin;

    public ChatCommand(NekoChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("nekochat.admin")) {
                sender.sendMessage(plugin.getMiniMessage(sender instanceof Player ? (Player) sender : null, "no-permission"));
                return true;
            }
            plugin.reloadNekoChatConfig();
            sender.sendMessage(plugin.getMiniMessage(sender instanceof Player ? (Player) sender : null, "reload-success"));
            return true;
        }
        sendHelp(sender);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partial = args[0].toLowerCase();
            if ("help".startsWith(partial)) completions.add("help");
            if ("reload".startsWith(partial) && sender.hasPermission("nekochat.admin")) completions.add("reload");
            return completions;
        }
        return List.of();
    }

    private void sendHelp(CommandSender sender) {
        Component help = MiniMessage.miniMessage().deserialize(
                plugin.getMessage(sender instanceof Player ? (Player) sender : null, "chat-help")
        );
        sender.sendMessage(help);
    }
}