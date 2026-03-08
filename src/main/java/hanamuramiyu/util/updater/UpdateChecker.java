package hanamuramiyu.util.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hanamuramiyu.core.NekoChat;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    public static void checkForUpdates(NekoChat plugin) {
        plugin.getSchedulerManager().runTaskAsync(() -> {
            try {
                String currentVersion = plugin.getDescription().getVersion();
                URL url = new URL("https://api.modrinth.com/v2/project/nekochat/version");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestProperty("User-Agent", "NekoChat/" + currentVersion);
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                        JsonArray versions = JsonParser.parseReader(reader).getAsJsonArray();
                        if (versions.size() > 0) {
                            JsonObject latest = versions.get(0).getAsJsonObject();
                            String latestVersion = latest.get("version_number").getAsString();
                            if (isNewerVersion(latestVersion, currentVersion)) {
                                plugin.getLogger().info("§a[UPDATE] New version available: " + latestVersion);
                                plugin.getLogger().info("§a[UPDATE] Download: https://modrinth.com/plugin/nekochat/version/" + latestVersion);
                                plugin.getLogger().info("§e[UPDATE] Current version: " + currentVersion);
                            }
                        }
                    }
                } else {
                    plugin.getLogger().warning("Update check failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        });
    }

    private static boolean isNewerVersion(String newVersion, String currentVersion) {
        try {
            String[] newParts = newVersion.split("\\.");
            String[] currentParts = currentVersion.split("\\.");
            int maxLength = Math.max(newParts.length, currentParts.length);
            for (int i = 0; i < maxLength; i++) {
                int newPart = i < newParts.length ? parseVersionPart(newParts[i]) : 0;
                int currentPart = i < currentParts.length ? parseVersionPart(currentParts[i]) : 0;
                if (newPart > currentPart) return true;
                if (newPart < currentPart) return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static int parseVersionPart(String part) {
        part = part.split("-")[0];
        part = part.split("\\+")[0];
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}