package hanamuramiyu.chat;

import hanamuramiyu.core.NekoChat;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionManager {
    private final NekoChat plugin;
    private boolean enabled;
    private boolean soundEnabled;
    private float soundVolume;
    private float soundPitch;
    private boolean requirePermission;
    private String othersFormat;
    private String selfFormat;
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([a-zA-Z0-9_]{1,16})");
    private org.bukkit.Sound bukkitSound;

    public MentionManager(NekoChat plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getConfigManager().getBukkitConfig();
        enabled = config.getBoolean("chat.mention.enabled", true);
        soundEnabled = config.getBoolean("chat.mention.sound.enabled", true);
        soundVolume = (float) config.getDouble("chat.mention.sound.volume", 0.5f);
        soundPitch = (float) config.getDouble("chat.mention.sound.pitch", 1.2f);
        requirePermission = config.getBoolean("chat.mention.require-permission", false);
        othersFormat = plugin.getMessage("mention-format-others");
        selfFormat = plugin.getMessage("mention-format-self");

        String soundKey = config.getString("chat.mention.sound.sound", "entity.experience_orb.pickup").toLowerCase();
        try {
            bukkitSound = Registry.SOUNDS.get(NamespacedKey.minecraft(soundKey));
            if (bukkitSound == null) {
                throw new IllegalArgumentException("Sound not found in registry");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid mention sound key: " + soundKey + ". Mention sounds disabled.");
            soundEnabled = false;
            bukkitSound = null;
        }
    }

    public Set<UUID> findMentions(Player sender, String message) {
        Set<UUID> mentioned = new HashSet<>();
        if (!enabled || !sender.hasPermission("nekochat.mention")) return mentioned;

        Matcher matcher = MENTION_PATTERN.matcher(message);
        while (matcher.find()) {
            String targetName = matcher.group(1);
            Player target = plugin.getServer().getPlayer(targetName);
            if (target != null && target.isOnline()) {
                if (requirePermission && !target.hasPermission("nekochat.mention")) continue;
                mentioned.add(target.getUniqueId());
            }
        }
        return mentioned;
    }

    public void notifyMentions(Player sender, Set<UUID> mentioned) {
        if (!enabled || mentioned.isEmpty()) return;

        for (UUID uuid : mentioned) {
            Player target = plugin.getServer().getPlayer(uuid);
            if (target != null && target.isOnline()) {
                Component notification = MiniMessage.miniMessage().deserialize(
                        plugin.getMessage(target, "mention-notify"),
                        TagResolver.resolver(Placeholder.component("player", sender.displayName()))
                );
                target.sendActionBar(notification);

                if (soundEnabled && bukkitSound != null) {
                    Sound adventSound = Sound.sound(bukkitSound, Sound.Source.PLAYER, soundVolume, soundPitch);
                    target.playSound(adventSound);
                }
            }
        }
    }

    public Component getOthersFormatComponent(Player viewer, Player mentioned) {
        return MiniMessage.miniMessage().deserialize(
                plugin.getMessage(viewer, "mention-format-others"),
                Placeholder.component("player", mentioned.displayName())
        );
    }

    public Component getSelfFormatComponent(Player viewer, Player mentioned) {
        return MiniMessage.miniMessage().deserialize(
                plugin.getMessage(viewer, "mention-format-self"),
                Placeholder.component("player", mentioned.displayName())
        );
    }

    public boolean isEnabled() {
        return enabled;
    }
}