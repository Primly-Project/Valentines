package primly.valentines.managers;

import primly.valentines.Valentines;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final Valentines plugin;
    private static final String CONFIG_VERSION_KEY = "config-version";
    private static final String CURRENT_CONFIG_VERSION = "2026+1.3.2-community";

    public ConfigManager(Valentines plugin) {
        this.plugin = plugin;
    }

    public void initializeConfig() {
        plugin.saveDefaultConfig();

        String configVersion = plugin.getConfig().getString(CONFIG_VERSION_KEY, "1.3");

        if (!configVersion.equals(CURRENT_CONFIG_VERSION) && isOlderVersion(configVersion, CURRENT_CONFIG_VERSION)) {
            plugin.getLogger().info("Updating configuration from v" + configVersion + " to v" + CURRENT_CONFIG_VERSION);
            updateConfig(configVersion);
            plugin.getConfig().set(CONFIG_VERSION_KEY, CURRENT_CONFIG_VERSION);
            plugin.saveConfig();
            plugin.getLogger().info("Configuration updated successfully!");
        } else {
            addMissingDefaults();
        }
    }

    private boolean isOlderVersion(String current, String latest) {
        try {
            VersionInfo currentInfo = parseVersion(current);
            VersionInfo latestInfo = parseVersion(latest);

            if (latestInfo.year != currentInfo.year) {
                return currentInfo.year < latestInfo.year;
            }

            if (latestInfo.major != currentInfo.major) {
                return currentInfo.major < latestInfo.major;
            }

            if (latestInfo.minor != currentInfo.minor) {
                return currentInfo.minor < latestInfo.minor;
            }

            int currentStatusPriority = getStatusPriority(currentInfo.status);
            int latestStatusPriority = getStatusPriority(latestInfo.status);

            if (latestStatusPriority != currentStatusPriority) {
                return currentStatusPriority < latestStatusPriority;
            }

            return currentInfo.build < latestInfo.build;

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to parse version strings, using fallback comparison: " + e.getMessage());
            return !current.equals(latest);
        }
    }


    private VersionInfo parseVersion(String version) {
        VersionInfo info = new VersionInfo();

        if (version.matches("^\\d+(\\.\\d+)*$")) {
            info.year = 2025;
            info.major = 0;
            info.minor = Integer.parseInt(version.split("\\.")[0]);
            info.status = "legacy";
            info.build = 0;
            return info;
        }

        String[] yearSplit = version.split("\\+", 2);
        if (yearSplit.length != 2) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }

        info.year = Integer.parseInt(yearSplit[0]);
        String remaining = yearSplit[1];

        String[] statusSplit = remaining.split("-", 2);
        if (statusSplit.length != 2) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }

        String[] versionParts = statusSplit[0].split("\\.");
        if (versionParts.length != 2) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }

        info.major = Integer.parseInt(versionParts[0]);
        info.minor = Integer.parseInt(versionParts[1]);

        String[] statusParts = statusSplit[1].split("\\.");
        if (statusParts.length != 2) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }

        info.status = statusParts[0];
        info.build = Integer.parseInt(statusParts[1]);

        return info;
    }

    private int getStatusPriority(String status) {
        switch (status.toLowerCase()) {
            case "legacy": return 0;
            case "alpha": return 1;
            case "beta": return 2;
            case "dev": return 3;
            case "rc": return 4;
            case "release": return 5;
            default: return 0;
        }
    }

    private void updateConfig(String fromVersion) {
        FileConfiguration config = plugin.getConfig();

        if (isOlderVersion(fromVersion, "2026+1.3.2-community")) {
            if (!config.contains("achievements")) {
                config.set("achievements.enabled", true);
                config.set("achievements.broadcast", true);
                config.set("achievements.sound-enabled", true);
                plugin.getLogger().info("Added achievements configuration");
            }
        }

        addMissingDefaults();
    }

    private void addMissingDefaults() {
        FileConfiguration config = plugin.getConfig();
        boolean modified = false;

        if (!config.contains("achievements.enabled")) {
            config.set("achievements.enabled", true);
            modified = true;
        }

        if (!config.contains("achievements.broadcast")) {
            config.set("achievements.broadcast", true);
            modified = true;
        }

        if (!config.contains("achievements.sound-enabled")) {
            config.set("achievements.sound-enabled", true);
            modified = true;
        }

        if (!config.contains(CONFIG_VERSION_KEY)) {
            config.set(CONFIG_VERSION_KEY, CURRENT_CONFIG_VERSION);
            modified = true;
        }

        if (modified) {
            plugin.saveConfig();
            plugin.getLogger().info("Added missing configuration options");
        }
    }

    private static class VersionInfo {
        int year;
        int major;
        int minor;
        String status;
        int build;

        @Override
        public String toString() {
            return year + "+" + major + "." + minor + "-" + status + "." + build;
        }
    }
}