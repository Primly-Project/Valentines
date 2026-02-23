package primly.valentines.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import primly.valentines.Valentines;
import primly.valentines.events.DivorceEvent;
import primly.valentines.events.HugGivenEvent;
import primly.valentines.events.HugReceivedEvent;
import primly.valentines.events.KissGivenEvent;
import primly.valentines.events.KissReceivedEvent;
import primly.valentines.events.LikeGivenEvent;
import primly.valentines.events.LikeReceivedEvent;
import primly.valentines.events.MarriageCreatedEvent;
import primly.valentines.events.MoodSetEvent;

public class AchievementListener implements Listener {

    private final Valentines plugin;

    public AchievementListener(Valentines plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHugReceived(HugReceivedEvent event) {
        plugin.getAchievementManager().checkHugReceived(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHugGiven(HugGivenEvent event) {
        plugin.getAchievementManager().checkHugGiven(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKissReceived(KissReceivedEvent event) {
        plugin.getAchievementManager().checkKissReceived(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKissGiven(KissGivenEvent event) {
        plugin.getAchievementManager().checkKissGiven(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLikeReceived(LikeReceivedEvent event) {
        plugin.getAchievementManager().checkLikeReceived(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLikeGiven(LikeGivenEvent event) {
        plugin.getAchievementManager().checkLikeGiven(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMarriageCreated(MarriageCreatedEvent event) {
        plugin.getAchievementManager().checkMarriage(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDivorce(DivorceEvent event) {
        plugin.getAchievementManager().checkDivorce(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMoodSet(MoodSetEvent event) {
        plugin.getAchievementManager().checkMoodSet(event.getPlayer());
    }
}