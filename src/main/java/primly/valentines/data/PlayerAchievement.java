package primly.valentines.data;

import java.util.UUID;

public record PlayerAchievement(UUID playerUuid, Achievement achievement, long unlockedAt) {
}