package primly.valentines.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primly.valentines.events.MoodSetEvent;
import primly.valentines.Valentines;
import primly.valentines.data.Marriage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoodCommand implements CommandExecutor {

    private final Valentines plugin;
    private final List<String> validMoods = Arrays.asList("very-good", "good", "neutral", "bad", "very-bad");

    public MoodCommand(Valentines plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (!player.hasPermission("valentines.use")) {
            player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getPrefix() + "&cUsage: &d/mood <very-good|good|neutral|bad|very-bad>");
            return true;
        }

        String mood = args[0].toLowerCase();
        if (!validMoods.contains(mood)) {
            player.sendMessage(plugin.getPrefix() + "&cInvalid mood! Valid moods: very-good, good, neutral, bad, very-bad");
            return true;
        }

        if (plugin.getConfig().getBoolean("mood.require-marriage", false)) {
            if (!plugin.getMarriageManager().isMarried(player.getUniqueId())) {
                player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("mood.not-married"));
                return true;
            }
        }

        long cooldownSeconds = plugin.getConfig().getLong("cooldowns.mood-change", 120);
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "mood")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "mood");
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(remaining));
            player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.cooldown", placeholders));
            return true;
        }

        plugin.getCooldownManager().setCooldown(player.getUniqueId(), "mood", cooldownSeconds * 1000);
        plugin.getPlayerDataManager().updateMood(player.getUniqueId(), mood);

        plugin.getServer().getPluginManager().callEvent(new MoodSetEvent(player, mood));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("mood", getMoodDisplay(mood));
        player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("mood.updated", placeholders));

        if (plugin.getConfig().getBoolean("mood.partner-notifications", true)) {
            Marriage marriage = plugin.getMarriageManager().getMarriage(player.getUniqueId());
            if (marriage != null) {
                Player partner = Bukkit.getPlayer(marriage.getPartner(player.getUniqueId()));
                if (partner != null) {
                    placeholders.put("partner", player.getName());
                    placeholders.put("mood", getMoodDisplay(mood));
                    partner.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("mood.partner-mood-changed", placeholders));
                }
            }
        }

        return true;
    }

    private String getMoodDisplay(String mood) {
        switch (mood) {
            case "very-good": return "Very Good";
            case "good": return "Good";
            case "neutral": return "Neutral";
            case "bad": return "Bad";
            case "very-bad": return "Very Bad";
            default: return "Unknown";
        }
    }
}