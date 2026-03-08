package hanamuramiyu.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration bukkitConfig;
    private final Map<String, YamlConfiguration> languageConfigs = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> messageCache = new ConcurrentHashMap<>(); // langCode -> (path -> message)
    private String defaultLanguage;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        bukkitConfig = plugin.getConfig();
        this.defaultLanguage = bukkitConfig.getString("language", "en-US");
        createLangFiles();
        loadAllLanguageFiles();
        rebuildMessageCache();
    }

    public void reload() {
        plugin.reloadConfig();
        bukkitConfig = plugin.getConfig();
        this.defaultLanguage = bukkitConfig.getString("language", "en-US");
        loadAllLanguageFiles();
        rebuildMessageCache();
    }

    private void createLangFiles() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        String[] languages = {"en-US", "en-GB", "es-ES", "es-419", "ja-JP", "ru-RU", "uk-UA", "zh-CN", "zh-TW"};
        for (String lang : languages) {
            File langFile = new File(langDir, lang + ".yml");
            if (!langFile.exists()) {
                try (InputStream in = plugin.getResource("lang/" + lang + ".yml")) {
                    if (in != null) Files.copy(in, langFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to copy language file for " + lang);
                }
            }
        }
    }

    private void loadAllLanguageFiles() {
        languageConfigs.clear();
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) return;

        File[] files = langDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String langCode = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            languageConfigs.put(langCode, config);
        }
    }

    private void rebuildMessageCache() {
        messageCache.clear();
        for (Map.Entry<String, YamlConfiguration> entry : languageConfigs.entrySet()) {
            String langCode = entry.getKey();
            YamlConfiguration config = entry.getValue();
            Map<String, String> langMap = new HashMap<>();
            for (String key : config.getKeys(true)) {
                if (config.isString(key)) {
                    langMap.put(key, config.getString(key));
                }
            }
            messageCache.put(langCode, langMap);
        }
        if (!messageCache.containsKey(defaultLanguage) && !languageConfigs.isEmpty()) {
            defaultLanguage = languageConfigs.keySet().iterator().next();
        }
    }

    public String getMessage(String path, String langCode) {
        Map<String, String> langMap = messageCache.get(langCode);
        if (langMap == null || !langMap.containsKey(path)) {
            langMap = messageCache.get(defaultLanguage);
        }
        if (langMap != null && langMap.containsKey(path)) {
            return langMap.get(path);
        }
        return "Message not found: " + path;
    }

    public Component getMiniMessage(String path, String langCode) {
        String message = getMessage(path, langCode);
        return MiniMessage.miniMessage().deserialize(message);
    }

    public FileConfiguration getBukkitConfig() {
        return bukkitConfig;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public Map<String, YamlConfiguration> getLanguageConfigs() {
        return languageConfigs;
    }
}