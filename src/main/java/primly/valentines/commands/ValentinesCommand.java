package primly.valentines.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import primly.valentines.Valentines;

public class ValentinesCommand implements CommandExecutor {

    private final Valentines plugin;

    public ValentinesCommand(Valentines plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("valentines.reload")) {
                sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.no-permission"));
                return true;
            }

            plugin.reloadPlugin();
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("plugin.reload"));
            return true;
        }

        if (!sender.hasPermission("valentines.use")) {
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.no-permission"));
            return true;
        }

        plugin.getPerformanceMonitor().incrementMetric("commands_executed");

        sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("help.header"));
        sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("help.gui"));

        return true;
    }
}