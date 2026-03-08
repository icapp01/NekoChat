package hanamuramiyu.core;

import hanamuramiyu.chat.ChatManager;
import hanamuramiyu.chat.MentionManager;
import hanamuramiyu.chat.PrivateMessageManager;
import hanamuramiyu.commands.ChatCommand;
import hanamuramiyu.commands.IgnoreCommand;
import hanamuramiyu.commands.IgnoreListCommand;
import hanamuramiyu.commands.MsgCommand;
import hanamuramiyu.commands.ReplyCommand;
import hanamuramiyu.commands.ToggleChatCommand;
import hanamuramiyu.commands.UnignoreCommand;
import hanamuramiyu.config.ConfigManager;
import hanamuramiyu.hooks.PlaceholderAPIHook;
import hanamuramiyu.listeners.ChatListener;
import hanamuramiyu.listeners.DeathListener;
import hanamuramiyu.listeners.JoinLeaveListener;
import hanamuramiyu.listeners.LocaleChangeListener;
import hanamuramiyu.managers.CooldownManager;
import hanamuramiyu.managers.IgnoreManager;
import hanamuramiyu.managers.LanguageManager;
import hanamuramiyu.scheduler.SchedulerManager;
import hanamuramiyu.util.updater.UpdateChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NekoChat extends JavaPlugin {
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private ChatManager chatManager;
    private MentionManager mentionManager;
    private PrivateMessageManager privateMessageManager;
    private SchedulerManager schedulerManager;
    private CooldownManager cooldownManager;
    private IgnoreManager ignoreManager;
    private Metrics metrics;
    private boolean luckPermsEnabled = false;
    private net.luckperms.api.LuckPerms luckPermsApi;

    private final AtomicInteger threadCounter = new AtomicInteger(1);

    @Override
    public void onEnable() {
        getLogger().info("NekoChat " + getDescription().getVersion() + " starting");

        this.schedulerManager = new SchedulerManager(this);

        this.configManager = new ConfigManager(this);
        this.languageManager = new LanguageManager(this, configManager);
        this.cooldownManager = new CooldownManager(this);
        this.ignoreManager = new IgnoreManager(this);

        checkLuckPerms();
        PlaceholderAPIHook.init(this);

        this.mentionManager = new MentionManager(this);
        this.privateMessageManager = new PrivateMessageManager(this, ignoreManager);
        this.chatManager = new ChatManager(this, mentionManager, privateMessageManager);

        ChatCommand chatCommand = new ChatCommand(this);
        getCommand("chat").setExecutor(chatCommand);
        getCommand("chat").setTabCompleter(chatCommand);

        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));

        ToggleChatCommand toggleChatCommand = new ToggleChatCommand(this);
        getCommand("togglechat").setExecutor(toggleChatCommand);
        getCommand("togglechat").setTabCompleter(toggleChatCommand);

        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("unignore").setExecutor(new UnignoreCommand(this));
        getCommand("ignorelist").setExecutor(new IgnoreListCommand(this));

        getServer().getPluginManager().registerEvents(new ChatListener(this, chatManager), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new LocaleChangeListener(this), this);

        metrics = new Metrics(this, 29864);

        UpdateChecker.checkForUpdates(this);

        getLogger().info("NekoChat enabled successfully.");
    }

    @Override
    public void onDisable() {
        if (schedulerManager != null) schedulerManager.cancelAllTasks();
        getLogger().info("NekoChat disabled.");
    }

    public void reloadNekoChatConfig() {
        schedulerManager.runTaskAsync(() -> {
            configManager.reload();
            languageManager.reload();
            checkLuckPerms();
            chatManager.reload();
            mentionManager.reload();
            privateMessageManager.reload();
            cooldownManager.clearAll();
            getLogger().info("Configuration reloaded.");
        });
    }

    private void checkLuckPerms() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            RegisteredServiceProvider<net.luckperms.api.LuckPerms> provider = Bukkit.getServicesManager().getRegistration(net.luckperms.api.LuckPerms.class);
            if (provider != null) {
                luckPermsApi = provider.getProvider();
                luckPermsEnabled = true;
                getLogger().info("LuckPerms integration enabled.");
            } else {
                luckPermsEnabled = false;
            }
        } else {
            luckPermsEnabled = false;
        }
    }

    public String getMessage(String path) {
        return configManager.getMessage(path, configManager.getDefaultLanguage());
    }

    public Component getMiniMessage(String path) {
        String msg = getMessage(path);
        return MiniMessage.miniMessage().deserialize(msg);
    }

    public String getMessage(Player player, String path) {
        if (player == null) return getMessage(path);
        String langCode = languageManager.getLanguage(player);
        return configManager.getMessage(path, langCode);
    }

    public Component getMiniMessage(Player player, String path) {
        if (player == null) return getMiniMessage(path);
        String msg = getMessage(player, path);
        return MiniMessage.miniMessage().deserialize(msg);
    }

    public String getFormat(String path) {
        return getConfig().getString("formats." + path, "");
    }

    public Component getFormatComponent(Player viewer, String path, TagResolver... extraResolvers) {
        String format = getFormat(path);
        format = PlaceholderAPIHook.apply(viewer, format);

        Component prefixComponent = Component.empty();
        Component suffixComponent = Component.empty();
        if (luckPermsEnabled && viewer != null) {
            String prefixStr = getLuckPermsPrefix(viewer);
            String suffixStr = getLuckPermsSuffix(viewer);
            if (!prefixStr.isEmpty()) {
                prefixComponent = MiniMessage.miniMessage().deserialize(prefixStr);
            }
            if (!suffixStr.isEmpty()) {
                suffixComponent = MiniMessage.miniMessage().deserialize(suffixStr);
            }
        }

        TagResolver[] allResolvers = new TagResolver[extraResolvers.length + 2];
        System.arraycopy(extraResolvers, 0, allResolvers, 0, extraResolvers.length);
        allResolvers[extraResolvers.length] = Placeholder.component("luckperms_prefix", prefixComponent);
        allResolvers[extraResolvers.length + 1] = Placeholder.component("luckperms_suffix", suffixComponent);

        return MiniMessage.miniMessage().deserialize(format, TagResolver.resolver(allResolvers));
    }

    private String getLuckPermsPrefix(Player player) {
        if (!luckPermsEnabled) return "";
        try {
            var user = luckPermsApi.getUserManager().getUser(player.getUniqueId());
            if (user == null) return "";
            var meta = user.getCachedData().getMetaData();
            String prefix = meta.getPrefix();
            return prefix != null ? prefix : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getLuckPermsSuffix(Player player) {
        if (!luckPermsEnabled) return "";
        try {
            var user = luckPermsApi.getUserManager().getUser(player.getUniqueId());
            if (user == null) return "";
            var meta = user.getCachedData().getMetaData();
            String suffix = meta.getSuffix();
            return suffix != null ? suffix : "";
        } catch (Exception e) {
            return "";
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public MentionManager getMentionManager() {
        return mentionManager;
    }

    public PrivateMessageManager getPrivateMessageManager() {
        return privateMessageManager;
    }

    public SchedulerManager getSchedulerManager() {
        return schedulerManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public IgnoreManager getIgnoreManager() {
        return ignoreManager;
    }
}