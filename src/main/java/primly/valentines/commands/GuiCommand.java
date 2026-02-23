package primly.valentines.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import primly.valentines.Valentines;

public class GuiCommand implements CommandExecutor {

    private final Valentines plugin;

    public GuiCommand(Valentines plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (!player.hasPermission("valentines.use")) {
            player.sendMessage(plugin.getLanguageManager().getMessage("general.no-permission"));
            return true;
        }

        plugin.getGuiManager().openMainMenu(player);
        return true;
    }
}