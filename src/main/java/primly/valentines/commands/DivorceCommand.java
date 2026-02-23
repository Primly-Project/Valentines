package primly.valentines.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primly.valentines.events.DivorceEvent;
import primly.valentines.Valentines;
import primly.valentines.data.Marriage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DivorceCommand implements CommandExecutor {

    private final Valentines plugin;
    private final ConcurrentHashMap<UUID, Long> divorceConfirmations = new ConcurrentHashMap<>();

    public DivorceCommand(Valentines plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (!player.hasPermission("valentines.divorce")) {
            player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.no-permission"));
            return true;
        }

        Marriage marriage = plugin.getMarriageManager().getMarriage(player.getUniqueId());
        if (marriage == null) {
            player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.not-married"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("confirm")) {
            return handleDivorceConfirm(player, marriage);
        }

        long divorceTimeout = plugin.getConfig().getLong("marriage.divorce-cooldown", 600);
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), "divorce")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), "divorce");
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(remaining));
            player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.divorce-cooldown", placeholders));
            return true;
        }

        UUID partnerUuid = marriage.getPartner(player.getUniqueId());
        Player partner = Bukkit.getPlayer(partnerUuid);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", partner != null ? partner.getName() : "Unknown");
        player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.divorce-confirm", placeholders));

        divorceConfirmations.put(player.getUniqueId(), System.currentTimeMillis() + 30000);

        return true;
    }

    private boolean handleDivorceConfirm(Player player, Marriage marriage) {
        Long confirmTime = divorceConfirmations.get(player.getUniqueId());
        if (confirmTime == null || System.currentTimeMillis() > confirmTime) {
            player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.divorce-timeout"));
            divorceConfirmations.remove(player.getUniqueId());
            return true;
        }

        divorceConfirmations.remove(player.getUniqueId());

        UUID partnerUuid = marriage.getPartner(player.getUniqueId());
        Player partner = Bukkit.getPlayer(partnerUuid);

        plugin.getMarriageManager().deleteMarriage(player.getUniqueId());

        plugin.getServer().getPluginManager().callEvent(new DivorceEvent(player, partner));
        if (partner != null) {
            plugin.getServer().getPluginManager().callEvent(new DivorceEvent(partner, player));
        }

        long divorceTimeout = plugin.getConfig().getLong("marriage.divorce-cooldown", 600);
        plugin.getCooldownManager().setCooldown(player.getUniqueId(), "divorce", divorceTimeout * 1000);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", partner != null ? partner.getName() : "Unknown");
        player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.divorce-initiator", placeholders));

        if (partner != null) {
            placeholders.put("player", player.getName());
            partner.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.divorce-target", placeholders));
        }

        return true;
    }
}