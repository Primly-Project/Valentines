package primly.valentines.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primly.valentines.events.KissGivenEvent;
import primly.valentines.events.KissReceivedEvent;
import primly.valentines.Valentines;

import java.util.HashMap;
import java.util.Map;

public class KissCommand implements CommandExecutor {

    private final Valentines plugin;

    public KissCommand(Valentines plugin) {
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
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("kiss.usage"));
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.player-offline"));
            return true;
        }

        if (receiver.equals(sender)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("kiss.self"));
            return true;
        }

        long cooldownSeconds = plugin.getConfig().getLong("cooldowns.kiss", 60);
        if (plugin.getCooldownManager().hasCooldown(sender.getUniqueId(), "kiss")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(sender.getUniqueId(), "kiss");
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("command", "kiss");
            placeholders.put("time", String.valueOf(remaining));
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.cooldown", placeholders));
            return true;
        }

        plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "kiss", cooldownSeconds * 1000);

        plugin.getPlayerDataManager().incrementStat(receiver.getUniqueId(), "kisses");
        plugin.getPlayerDataManager().incrementGivenStat(sender.getUniqueId(), "kisses");

        plugin.getServer().getPluginManager().callEvent(new KissReceivedEvent(receiver, sender));
        plugin.getServer().getPluginManager().callEvent(new KissGivenEvent(sender, receiver));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", receiver.getName());
        sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("kiss.sent", placeholders));

        placeholders.put("player", sender.getName());
        receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("kiss.received", placeholders));

        plugin.getEffectManager().playKissEffect(receiver.getLocation());

        return true;
    }
}