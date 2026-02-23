package primly.valentines.managers;

import org.bukkit.entity.Player;
import primly.valentines.Valentines;
import primly.valentines.data.PlayerData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    
    private final Valentines plugin;
    private final Map<UUID, PlayerData> playerDataCache = new HashMap<>();
    
    public PlayerDataManager(Valentines plugin) {
        this.plugin = plugin;
    }
    
    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = plugin.getFileStorage().loadPlayerData(uuid);
        
        if (data == null) {
            data = new PlayerData(
                uuid,
                player.getName(),
                0, 0, 0,
                "heart",
                true,
                "unknown",
                System.currentTimeMillis(),
                0, 0, 0,
                new java.util.HashSet<>()
            );
            plugin.getFileStorage().savePlayerData(data);
        } else {
            if (!data.getName().equals(player.getName())) {
                data.setName(player.getName());
                plugin.getFileStorage().savePlayerData(data);
            }
        }
        
        playerDataCache.put(uuid, data);
    }
    
    public void unloadPlayerData(UUID uuid) {
        PlayerData data = playerDataCache.remove(uuid);
        if (data != null) {
            data.setLastSeen(System.currentTimeMillis());
            plugin.getFileStorage().savePlayerData(data);
        }
    }
    
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.get(uuid);
    }
    
    public void incrementStat(UUID uuid, String statType) {
        PlayerData data = playerDataCache.get(uuid);
        if (data != null) {
            switch (statType) {
                case "hugs":
                    data.setHugsReceived(data.getHugsReceived() + 1);
                    break;
                case "kisses":
                    data.setKissesReceived(data.getKissesReceived() + 1);
                    break;
                case "likes":
                    data.setLikesReceived(data.getLikesReceived() + 1);
                    break;
            }
            plugin.getFileStorage().savePlayerData(data);
        }
    }

    public void incrementGivenStat(UUID uuid, String statType) {
        PlayerData data = playerDataCache.get(uuid);
        if (data != null) {
            switch (statType) {
                case "hugs":
                    data.setHugsGiven(data.getHugsGiven() + 1);
                    break;
                case "kisses":
                    data.setKissesGiven(data.getKissesGiven() + 1);
                    break;
                case "likes":
                    data.setLikesGiven(data.getLikesGiven() + 1);
                    break;
            }
            plugin.getFileStorage().savePlayerData(data);
        }
    }

    public void savePlayerData(PlayerData data) {
        plugin.getFileStorage().savePlayerData(data);
    }
    
    public void updateEffectType(UUID uuid, String effectType) {
        PlayerData data = playerDataCache.get(uuid);
        if (data != null) {
            data.setEffectType(effectType);
            plugin.getFileStorage().savePlayerData(data);
        }
    }
    
    public void updateMood(UUID uuid, String mood) {
        PlayerData data = playerDataCache.get(uuid);
        if (data != null) {
            data.setMood(mood);
            plugin.getFileStorage().savePlayerData(data);
        }
    }
    
    public void toggleEffects(UUID uuid) {
        PlayerData data = playerDataCache.get(uuid);
        if (data != null) {
            data.setEffectEnabled(!data.isEffectEnabled());
            plugin.getFileStorage().savePlayerData(data);
        }
    }
    
    public List<PlayerData> getTopPlayers(int limit) {
        return plugin.getFileStorage().getTopPlayers(limit);
    }
    
    public void saveAllData() {
        plugin.getPerformanceMonitor().incrementMetric("data_saves");
        for (PlayerData data : playerDataCache.values()) {
            plugin.getFileStorage().savePlayerData(data);
        }
    }
}