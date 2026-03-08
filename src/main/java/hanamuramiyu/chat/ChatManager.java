package hanamuramiyu.chat;

import hanamuramiyu.core.NekoChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager {
    private final NekoChat plugin;
    private boolean globalEnabled;
    private boolean localEnabled;
    private int localRange;
    private String globalPrefix;
    private final MentionManager mentionManager;
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([a-zA-Z0-9_]{1,16})");
    private static final int MAX_MESSAGE_LENGTH = 256;

    public ChatManager(NekoChat plugin, MentionManager mentionManager, PrivateMessageManager privateMessageManager) {
        this.plugin = plugin;
        this.mentionManager = mentionManager;
        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getConfig();
        globalEnabled = config.getBoolean("chat.global.enabled", true);
        localEnabled = config.getBoolean("chat.local.enabled", true);
        localRange = config.getInt("chat.local.range", 100);
        globalPrefix = config.getString("global-prefix", "!");
    }

    public void handleChat(Player sender, String rawMessage) {
        if (!globalEnabled && !localEnabled) {
            sender.sendMessage(plugin.getMiniMessage(sender, "togglechat-all-off"));
            return;
        }

        if (rawMessage.length() > MAX_MESSAGE_LENGTH) {
            sender.sendMessage(plugin.getMiniMessage(sender, "invalid-usage"));
            return;
        }

        ChatMode targetMode = determineTargetMode(rawMessage);
        String message = stripPrefix(rawMessage, targetMode);

        if (message.isEmpty()) {
            sender.sendMessage(plugin.getMiniMessage(sender, "invalid-usage"));
            return;
        }

        if (plugin.getCooldownManager().isOnCooldown(sender.getUniqueId(), "chat")) {
            int seconds = plugin.getCooldownManager().getRemainingSeconds(sender.getUniqueId(), "chat");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getMessage(sender, "msg-cooldown"),
                    Placeholder.unparsed("seconds", String.valueOf(seconds))
            ));
            return;
        }

        if (!isModeAvailable(targetMode)) {
            sender.sendMessage(plugin.getMiniMessage(sender, "togglechat-all-off"));
            return;
        }

        Set<Player> recipients = collectRecipients(sender, targetMode);
        Set<UUID> allMentioned = mentionManager.findMentions(sender, message);

        sendMessages(sender, message, recipients, allMentioned, targetMode);
        notifyMentions(sender, recipients, allMentioned);

        plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "chat");
    }

    private ChatMode determineTargetMode(String rawMessage) {
        if (globalEnabled && rawMessage.startsWith(globalPrefix)) {
            return ChatMode.GLOBAL;
        }
        return ChatMode.LOCAL;
    }

    private String stripPrefix(String rawMessage, ChatMode mode) {
        if (mode == ChatMode.GLOBAL && rawMessage.startsWith(globalPrefix)) {
            return rawMessage.substring(globalPrefix.length()).trim();
        }
        return rawMessage.trim();
    }

    private boolean isModeAvailable(ChatMode mode) {
        return (mode == ChatMode.GLOBAL && globalEnabled) || (mode == ChatMode.LOCAL && localEnabled);
    }

    private Set<Player> collectRecipients(Player sender, ChatMode mode) {
        if (mode == ChatMode.GLOBAL) {
            return new HashSet<>(plugin.getServer().getOnlinePlayers());
        } else {
            return new HashSet<>(sender.getWorld().getNearbyPlayers(sender.getLocation(), localRange));
        }
    }

    private void sendMessages(Player sender, String message, Set<Player> recipients, Set<UUID> allMentioned, ChatMode mode) {
        String formatPath = mode == ChatMode.GLOBAL ? "global-format" : "local-format";

        for (Player recipient : recipients) {
            Component messageComponent = formatMessageWithMentions(message, sender, recipient, allMentioned);
            Component component = plugin.getFormatComponent(recipient, formatPath,
                    Placeholder.unparsed("username", sender.getName()),
                    Placeholder.component("player", sender.displayName()),
                    Placeholder.component("message", messageComponent)
            );
            recipient.sendMessage(component);
        }
    }

    private void notifyMentions(Player sender, Set<Player> recipients, Set<UUID> allMentioned) {
        Set<UUID> mentionedInRange = new HashSet<>();
        for (Player recipient : recipients) {
            if (allMentioned.contains(recipient.getUniqueId())) {
                mentionedInRange.add(recipient.getUniqueId());
            }
        }
        if (!mentionedInRange.isEmpty()) {
            mentionManager.notifyMentions(sender, mentionedInRange);
        }
    }

    private Component formatMessageWithMentions(String rawMessage, Player sender, Player recipient, Set<UUID> mentioned) {
        if (!mentionManager.isEnabled() || mentioned.isEmpty()) {
            return Component.text(rawMessage);
        }

        List<Component> components = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(rawMessage);
        int lastEnd = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            if (start > lastEnd) {
                components.add(Component.text(rawMessage.substring(lastEnd, start)));
            }

            String targetName = matcher.group(1);
            Player target = plugin.getServer().getPlayer(targetName);
            if (target != null && mentioned.contains(target.getUniqueId())) {
                Component mentionComponent = (target.equals(recipient))
                        ? mentionManager.getSelfFormatComponent(recipient, target)
                        : mentionManager.getOthersFormatComponent(recipient, target);
                components.add(mentionComponent);
            } else {
                components.add(Component.text(matcher.group(0)));
            }

            lastEnd = end;
        }

        if (lastEnd < rawMessage.length()) {
            components.add(Component.text(rawMessage.substring(lastEnd)));
        }

        return Component.empty().children(components);
    }

    public boolean isGlobalEnabled() {
        return globalEnabled;
    }

    public void setGlobalEnabled(boolean globalEnabled) {
        this.globalEnabled = globalEnabled;
        plugin.getConfig().set("chat.global.enabled", globalEnabled);
        plugin.saveConfig();
    }

    public boolean isLocalEnabled() {
        return localEnabled;
    }

    public void setLocalEnabled(boolean localEnabled) {
        this.localEnabled = localEnabled;
        plugin.getConfig().set("chat.local.enabled", localEnabled);
        plugin.saveConfig();
    }

    public enum ChatMode { GLOBAL, LOCAL }
}