package primly.valentines.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primly.valentines.events.MarriageCreatedEvent;
import primly.valentines.Valentines;
import primly.valentines.data.Marriage;
import primly.valentines.data.MarriageProposal;

import java.util.HashMap;
import java.util.Map;

public class MarryCommand implements CommandExecutor {

    private final Valentines plugin;

    public MarryCommand(Valentines plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player proposer)) {
            commandSender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (!proposer.hasPermission("valentines.marry")) {
            proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length == 0) {
            proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("confirm")) {
            return handleConfirm(proposer);
        }

        if (args[0].equalsIgnoreCase("deny")) {
            return handleDeny(proposer);
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.player-offline"));
            return true;
        }

        if (target.equals(proposer)) {
            proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.self"));
            return true;
        }

        long proposalCooldown = plugin.getConfig().getLong("marriage.proposal-cooldown", 30);
        if (plugin.getCooldownManager().hasCooldown(proposer.getUniqueId(), "proposal")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(proposer.getUniqueId(), "proposal");
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(remaining));
            proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.proposal-cooldown", placeholders));
            return true;
        }

        if (plugin.getMarriageManager().isMarried(proposer.getUniqueId())) {
            Marriage marriage = plugin.getMarriageManager().getMarriage(proposer.getUniqueId());
            Player partner = Bukkit.getPlayer(marriage.getPartner(proposer.getUniqueId()));
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("partner", partner != null ? partner.getName() : "Unknown");
            proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.already-married", placeholders));
            return true;
        }

        if (plugin.getMarriageManager().isMarried(target.getUniqueId())) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", target.getName());
            proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.target-already-married", placeholders));
            return true;
        }

        plugin.getCooldownManager().setCooldown(proposer.getUniqueId(), "proposal", proposalCooldown * 1000);

        plugin.getMarriageManager().createProposal(proposer.getUniqueId(), target.getUniqueId());

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());
        proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.proposal-sent", placeholders));

        placeholders.put("player", proposer.getName());
        target.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.proposal-received", placeholders));

        return true;
    }

    private boolean handleConfirm(Player receiver) {
        MarriageProposal proposal = plugin.getMarriageManager().getProposal(receiver.getUniqueId());
        if (proposal == null) {
            receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.no-pending-proposals"));
            return true;
        }

        Player proposer = Bukkit.getPlayer(proposal.proposer());
        if (proposer == null) {
            receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.proposer-offline"));
            plugin.getMarriageManager().removeProposal(receiver.getUniqueId());
            return true;
        }

        plugin.getMarriageManager().createMarriage(proposal.proposer(), receiver.getUniqueId());

        plugin.getServer().getPluginManager().callEvent(new MarriageCreatedEvent(receiver, proposer));
        plugin.getServer().getPluginManager().callEvent(new MarriageCreatedEvent(proposer, receiver));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", receiver.getName());
        proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.accept-sender", placeholders));

        placeholders.put("player", proposer.getName());
        receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.accept-receiver", placeholders));

        plugin.getEffectManager().playMarriageEffect(receiver.getLocation());
        plugin.getEffectManager().playMarriageEffect(proposer.getLocation());

        return true;
    }

    private boolean handleDeny(Player receiver) {
        MarriageProposal proposal = plugin.getMarriageManager().getProposal(receiver.getUniqueId());
        if (proposal == null) {
            receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.no-pending-proposals"));
            return true;
        }

        Player proposer = Bukkit.getPlayer(proposal.proposer());

        plugin.getMarriageManager().removeProposal(receiver.getUniqueId());

        Map<String, String> placeholders = new HashMap<>();
        if (proposer != null) {
            placeholders.put("player", receiver.getName());
            proposer.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.decline-sender", placeholders));
        }

        placeholders.put("player", proposer != null ? proposer.getName() : "Unknown");
        receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.decline-receiver", placeholders));

        return true;
    }
}