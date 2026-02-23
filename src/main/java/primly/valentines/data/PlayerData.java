package primly.valentines.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private String name;
    private int hugsReceived;
    private int kissesReceived;
    private int likesReceived;
    private String effectType;
    private boolean effectEnabled;
    private String mood;
    private long lastSeen;
    private int hugsGiven;
    private int kissesGiven;
    private int likesGiven;
    private final Set<String> unlockedAchievements;
    
    public PlayerData(UUID uuid, String name, int hugsReceived, int kissesReceived,
                     int likesReceived, String effectType, boolean effectEnabled,
                     String mood, long lastSeen, int hugsGiven, int kissesGiven,
                     int likesGiven, Set<String> unlockedAchievements) {
        this.uuid = uuid;
        this.name = name;
        this.hugsReceived = hugsReceived;
        this.kissesReceived = kissesReceived;
        this.likesReceived = likesReceived;
        this.effectType = effectType;
        this.effectEnabled = effectEnabled;
        this.mood = mood;
        this.lastSeen = lastSeen;
        this.hugsGiven = hugsGiven;
        this.kissesGiven = kissesGiven;
        this.likesGiven = likesGiven;
        this.unlockedAchievements = unlockedAchievements != null ? unlockedAchievements : new HashSet<>();
    }
    
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public int getHugsReceived() { return hugsReceived; }
    public int getKissesReceived() { return kissesReceived; }
    public int getLikesReceived() { return likesReceived; }
    public String getEffectType() { return effectType; }
    public boolean isEffectEnabled() { return effectEnabled; }
    public String getMood() { return mood; }
    public long getLastSeen() { return lastSeen; }
    public int getHugsGiven() { return hugsGiven; }
    public int getKissesGiven() { return kissesGiven; }
    public int getLikesGiven() { return likesGiven; }
    public Set<String> getUnlockedAchievements() { return unlockedAchievements; }
    
    public int getTotalScore() {
        return hugsReceived + kissesReceived + likesReceived;
    }
    
    public void setName(String name) { this.name = name; }
    public void setHugsReceived(int hugsReceived) { this.hugsReceived = hugsReceived; }
    public void setKissesReceived(int kissesReceived) { this.kissesReceived = kissesReceived; }
    public void setLikesReceived(int likesReceived) { this.likesReceived = likesReceived; }
    public void setEffectType(String effectType) { this.effectType = effectType; }
    public void setEffectEnabled(boolean effectEnabled) { this.effectEnabled = effectEnabled; }
    public void setMood(String mood) { this.mood = mood; }
    public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }
    public void setHugsGiven(int hugsGiven) { this.hugsGiven = hugsGiven; }
    public void setKissesGiven(int kissesGiven) { this.kissesGiven = kissesGiven; }
    public void setLikesGiven(int likesGiven) { this.likesGiven = likesGiven; }

    public void addAchievement(String achievementKey) {
        unlockedAchievements.add(achievementKey);
    }

    public boolean hasAchievement(String achievementKey) {
        return unlockedAchievements.contains(achievementKey);
    }
}