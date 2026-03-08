package hanamuramiyu.chat;

import hanamuramiyu.core.NekoChat;
import hanamuramiyu.managers.IgnoreManager;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PrivateMessageManager {
    private final NekoChat plugin;
    private final IgnoreManager ignoreManager;
    private final Map<UUID, UUID> lastSender = new ConcurrentHashMap<>();
    private boolean enabled;
    private boolean soundEnabled;
    private org.bukkit.Sound bukkitSound;
    private float soundVolume;
    private float soundPitch;

    public PrivateMessageManager(NekoChat plugin, IgnoreManager ignoreManager) {
        this.plugin = plugin;
        this.ignoreManager = ignoreManager;
        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getConfigManager().getBukkitConfig();
        enabled = config.getBoolean("chat.private.enabled", true);
        soundEnabled = config.getBoolean("chat.private.sound.enabled", true);
        soundVolume = (float) config.getDouble("chat.private.sound.volume", 1.0f);
        soundPitch = (float) config.getDouble("chat.private.sound.pitch", 1.0f);

        String soundKey = config.getString("chat.private.sound.sound", "entity.experience_orb.pickup").toLowerCase();
        try {
            bukkitSound = Registry.SOUNDS.get(NamespacedKey.minecraft(soundKey));
            if (bukkitSound == null) {
                throw new IllegalArgumentException("Sound not found in registry");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid private msg sound key: " + soundKey + ". Sounds disabled.");
            soundEnabled = false;
            bukkitSound = null;
        }
    }

    public void sendPrivateMessage(Player sender, String targetName, String message) {
        if (!enabled) {
            sender.sendMessage(plugin.getMiniMessage(sender, "no-permission"));
            return;
        }

        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(plugin.getMiniMessage(sender, "msg-player-offline")
                    .replaceText(b -> b.matchLiteral("<player>").replacement(targetName)));
            return;
        }

        if (sender.equals(target)) {
            sender.sendMessage(plugin.getMiniMessage(sender, "msg-self"));
            return;
        }

        if (ignoreManager.isIgnoring(target.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage(plugin.getMiniMessage(sender, "msg-ignored"));
            return;
        }

        if (plugin.getCooldownManager().isOnCooldown(sender.getUniqueId(), "msg")) {
            int seconds = plugin.getCooldownManager().getRemainingSeconds(sender.getUniqueId(), "msg");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getMessage(sender, "msg-cooldown"),
                    Placeholder.unparsed("seconds", String.valueOf(seconds))
            ));
            return;
        }

        Component sendMsg = plugin.getFormatComponent(sender, "private-format-send",
                Placeholder.unparsed("username_sender", sender.getName()),
                Placeholder.unparsed("username_recipient", target.getName()),
                Placeholder.component("sender", sender.displayName()),
                Placeholder.component("recipient", target.displayName()),
                Placeholder.component("message", Component.text(message))
        );

        Component receiveMsg = plugin.getFormatComponent(target, "private-format-receive",
                Placeholder.unparsed("username_sender", sender.getName()),
                Placeholder.unparsed("username_recipient", target.getName()),
                Placeholder.component("sender", sender.displayName()),
                Placeholder.component("recipient", target.displayName()),
                Placeholder.component("message", Component.text(message))
        );

        sender.sendMessage(sendMsg);
        target.sendMessage(receiveMsg);

        if (soundEnabled && bukkitSound != null) {
            Sound adventSound = Sound.sound(bukkitSound, Sound.Source.PLAYER, soundVolume, soundPitch);
            target.playSound(adventSound);
        }

        lastSender.put(target.getUniqueId(), sender.getUniqueId());
        plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "msg");
    }

    public void reply(Player player, String message) {
        if (!enabled) {
            player.sendMessage(plugin.getMiniMessage(player, "no-permission"));
            return;
        }

        UUID last = lastSender.get(player.getUniqueId());
        if (last == null) {
            player.sendMessage(plugin.getMiniMessage(player, "reply-no-target"));
            return;
        }

        Player target = plugin.getServer().getPlayer(last);
        if (target == null || !target.isOnline()) {
            player.sendMessage(plugin.getMiniMessage(player, "msg-player-offline")
                    .replaceText(b -> b.matchLiteral("<player>").replacement("Unknown")));
            lastSender.remove(player.getUniqueId());
            return;
        }

        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), "reply")) {
            int seconds = plugin.getCooldownManager().getRemainingSeconds(player.getUniqueId(), "reply");
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getMessage(player, "msg-cooldown"),
                    Placeholder.unparsed("seconds", String.valueOf(seconds))
            ));
            return;
        }

        plugin.getCooldownManager().setCooldown(player.getUniqueId(), "reply");
        sendPrivateMessage(player, target.getName(), message);
    }

    public void clearLastSender(UUID uuid) {
        lastSender.remove(uuid);
    }
}