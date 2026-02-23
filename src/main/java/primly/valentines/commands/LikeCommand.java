package primly.valentines.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primly.valentines.events.LikeGivenEvent;
import primly.valentines.events.LikeReceivedEvent;
import primly.valentines.Valentines;

import java.util.HashMap;
import java.util.Map;

public class LikeCommand implements CommandExecutor {

    private final Valentines plugin;

    public LikeCommand(Valentines plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player sender)) {
            commandSender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (!sender.hasPermission("valentines.use")) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("like.usage"));
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.player-offline"));
            return true;
        }

        if (receiver.equals(sender)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("like.self"));
            return true;
        }

        if (plugin.getFileStorage().hasLiked(sender.getUniqueId(), receiver.getUniqueId())) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", receiver.getName());
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("like.already-liked", placeholders));
            return true;
        }

        long cooldownSeconds = plugin.getConfig().getLong("cooldowns.like", 240);
        if (plugin.getCooldownManager().hasCooldown(sender.getUniqueId(), "like")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(sender.getUniqueId(), "like");
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(remaining));
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("like.cooldown", placeholders));
            return true;
        }

        plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "like", cooldownSeconds * 1000);

        plugin.getFileStorage().saveLike(sender.getUniqueId(), receiver.getUniqueId());

        plugin.getPlayerDataManager().incrementStat(receiver.getUniqueId(), "likes");
        plugin.getPlayerDataManager().incrementGivenStat(sender.getUniqueId(), "likes");

        plugin.getServer().getPluginManager().callEvent(new LikeReceivedEvent(receiver, sender));
        plugin.getServer().getPluginManager().callEvent(new LikeGivenEvent(sender, receiver));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", receiver.getName());
        sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("like.sent", placeholders));

        placeholders.put("player", sender.getName());
        receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("like.received", placeholders));

        return true;
    }
}