package primly.valentines.commands;

import primly.valentines.Valentines;
import primly.valentines.data.Achievement;
import primly.valentines.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AchievementsCommand implements CommandExecutor {

    private final Valentines plugin;

    public AchievementsCommand(Valentines plugin) {
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

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            player.sendMessage(plugin.getPrefix() + "&cNo data found!");
            return true;
        }

        int unlocked = plugin.getAchievementManager().getUnlockedCount(player.getUniqueId());
        int total = Achievement.values().length;
        double percentage = plugin.getAchievementManager().getProgressPercentage(player.getUniqueId());

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("unlocked", String.valueOf(unlocked));
        placeholders.put("total", String.valueOf(total));
        placeholders.put("percentage", String.format("%.1f", percentage));

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() + "&d&lYOUR ACHIEVEMENTS"));
        player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("achievements.progress", placeholders));
        player.sendMessage("");

        for (Achievement achievement : Achievement.values()) {
            boolean hasAchievement = data.hasAchievement(achievement.getKey());
            String status = hasAchievement ? "&a✔" : "&7✘";
            String name = plugin.getLanguageManager().getMessage("achievements." + achievement.getKey() + ".name");
            String description = plugin.getLanguageManager().getMessage("achievements." + achievement.getKey() + ".description");
            String message = plugin.getPrefix() + status + " &d" + name + " &7- &f" + description;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        return true;
    }
}