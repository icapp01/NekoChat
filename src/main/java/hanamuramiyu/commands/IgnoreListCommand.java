package hanamuramiyu.commands;

import hanamuramiyu.core.NekoChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class IgnoreListCommand implements CommandExecutor {
    private final NekoChat plugin;

    public IgnoreListCommand(NekoChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Set<UUID> ignored = plugin.getIgnoreManager().getIgnored(player);
        if (ignored.isEmpty()) {
            player.sendMessage(plugin.getMiniMessage(player, "ignorelist-empty"));
            return true;
        }

        Component header = plugin.getMiniMessage(player, "ignorelist-header");
        player.sendMessage(header);

        String format = plugin.getMessage(player, "ignorelist-entry");
        for (UUID uuid : ignored) {
            Player target = plugin.getServer().getPlayer(uuid);
            String name = target != null ? target.getName() : uuid.toString();
            Component entry = MiniMessage.miniMessage().deserialize(format, Placeholder.unparsed("player", name));
            player.sendMessage(entry);
        }
        return true;
    }
}