package primly.valentines.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primly.valentines.events.HugGivenEvent;
import primly.valentines.events.HugReceivedEvent;
import primly.valentines.Valentines;

import java.util.HashMap;
import java.util.Map;

public class HugCommand implements CommandExecutor {

    private final Valentines plugin;

    public HugCommand(Valentines plugin) {
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
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("hug.usage"));
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.player-offline"));
            return true;
        }

        if (receiver.equals(sender)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("hug.self"));
            return true;
        }

        long cooldownSeconds = plugin.getConfig().getLong("cooldowns.hug", 45);
        if (plugin.getCooldownManager().hasCooldown(sender.getUniqueId(), "hug")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(sender.getUniqueId(), "hug");
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("command", "hug");
            placeholders.put("time", String.valueOf(remaining));
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.cooldown", placeholders));
            return true;
        }

        plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "hug", cooldownSeconds * 1000);

        plugin.getPlayerDataManager().incrementStat(receiver.getUniqueId(), "hugs");
        plugin.getPlayerDataManager().incrementGivenStat(sender.getUniqueId(), "hugs");

        plugin.getServer().getPluginManager().callEvent(new HugReceivedEvent(receiver, sender));
        plugin.getServer().getPluginManager().callEvent(new HugGivenEvent(sender, receiver));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", receiver.getName());
        sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("hug.sent", placeholders));

        placeholders.put("player", sender.getName());
        receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("hug.received", placeholders));

        plugin.getEffectManager().playHugEffect(receiver.getLocation());

        return true;
    }
}