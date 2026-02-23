package primly.valentines.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import primly.valentines.events.*;
import primly.valentines.Valentines;

import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {

    private final Valentines plugin;

    public PlayerListener(Valentines plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPlayerDataManager().loadPlayerData(event.getPlayer());

        plugin.getMarriageManager().loadMarriage(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer().getUniqueId());

        plugin.getCooldownManager().clearCooldowns(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGuiManager().isInSearchMode(player)) {
            event.setCancelled(true);
            String message = event.getMessage();

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getGuiManager().handlePlayerSearch(player, message);
            });
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();

        if (title.contains("Valentines") || title.contains("❤") || title.contains("Mood") ||
                title.contains("Stats") || title.contains("Leaderboard") || title.contains("Couples") ||
                title.contains("Settings") || title.contains("Profile") || title.contains("Achievement")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
                return;
            }

            String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

            if (title.equals(plugin.getLanguageManager().getMessage("gui.main-menu-title"))) {
                handleMainMenuClick(player, itemName);
            }
            else if (title.equals(plugin.getLanguageManager().getMessage("gui.mood-title"))) {
                handleMoodMenuClick(player, itemName);
            }
            else if (title.equals(plugin.getLanguageManager().getMessage("gui.stats-menu-title")) ||
                    title.equals(plugin.getLanguageManager().getMessage("gui.leaderboard-title")) ||
                    title.equals(plugin.getLanguageManager().getMessage("gui.marriages-title")) ||
                    title.equals(plugin.getLanguageManager().getMessage("gui.settings-title")) ||
                    title.equals(plugin.getLanguageManager().getMessage("gui.achievements-menu-title")) ||
                    title.contains("Profile")) {
                handleSubMenuClick(player, itemName, title);
            }
        }
    }

    private void handleMainMenuClick(Player player, String itemName) {
        if (itemName.equals(plugin.getLanguageManager().getMessage("gui.stats-button-title"))) {
            plugin.getGuiManager().openStatsMenu(player);
        } else if (itemName.equals(plugin.getLanguageManager().getMessage("gui.leaderboard-button-title"))) {
            plugin.getGuiManager().openLeaderboardMenu(player);
        } else if (itemName.equals(plugin.getLanguageManager().getMessage("gui.marriages-button-title"))) {
            plugin.getGuiManager().openMarriagesMenu(player);
        } else if (itemName.equals(plugin.getLanguageManager().getMessage("gui.settings-button-title"))) {
            plugin.getGuiManager().openSettingsMenu(player);
        } else if (itemName.equals(plugin.getLanguageManager().getMessage("gui.mood-button-title"))) {
            plugin.getGuiManager().openMoodMenu(player);
        } else if (itemName.equals(plugin.getLanguageManager().getMessage("gui.achievements-button-title"))) {
            plugin.getGuiManager().openAchievementsMenu(player);
        } else if (itemName.equals(plugin.getLanguageManager().getMessage("gui.player-search-button-title"))) {
            plugin.getGuiManager().startPlayerSearch(player);
        }
    }

    private void handleMoodMenuClick(Player player, String itemName) {
        if (itemName.equals(plugin.getLanguageManager().getMessage("gui.back-button"))) {
            plugin.getGuiManager().openMainMenu(player);
            return;
        }

        String mood = null;
        String moodMessage = null;

        if (itemName.contains("Very Good")) {
            mood = "very-good";
            moodMessage = "Very Good";
        } else if (itemName.contains("Good") && !itemName.contains("Very")) {
            mood = "good";
            moodMessage = "Good";
        } else if (itemName.contains("Neutral")) {
            mood = "neutral";
            moodMessage = "Neutral";
        } else if (itemName.contains("Bad") && !itemName.contains("Very")) {
            mood = "bad";
            moodMessage = "Bad";
        } else if (itemName.contains("Very Bad")) {
            mood = "very-bad";
            moodMessage = "Very Bad";
        }

        if (mood != null) {
            plugin.getPlayerDataManager().updateMood(player.getUniqueId(), mood);

            plugin.getServer().getPluginManager().callEvent(new MoodSetEvent(player, mood));

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("mood", moodMessage);
            player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("mood.updated", placeholders));

            plugin.getGuiManager().openMoodMenu(player);
        }
    }

    private void handleSubMenuClick(Player player, String itemName, String title) {
        if (itemName.equals(plugin.getLanguageManager().getMessage("gui.back-button"))) {
            plugin.getGuiManager().openMainMenu(player);
            return;
        }

        if (title.equals(plugin.getLanguageManager().getMessage("gui.settings-title"))) {
            handleSettingsClick(player, itemName);
        }

        if (title.contains("Profile")) {
            handlePlayerProfileClick(player, itemName, title);
        }
    }

    private void handleSettingsClick(Player player, String itemName) {
        if (itemName.contains("Heart")) {
            plugin.getPlayerDataManager().updateEffectType(player.getUniqueId(), "heart");
            plugin.getGuiManager().openSettingsMenu(player);
        } else if (itemName.contains("Spiral")) {
            plugin.getPlayerDataManager().updateEffectType(player.getUniqueId(), "spiral");
            plugin.getGuiManager().openSettingsMenu(player);
        } else if (itemName.contains("Cloud")) {
            plugin.getPlayerDataManager().updateEffectType(player.getUniqueId(), "cloud");
            plugin.getGuiManager().openSettingsMenu(player);
        } else if (itemName.contains("Selected") || itemName.contains("Disabled")) {
            plugin.getPlayerDataManager().toggleEffects(player.getUniqueId());
            plugin.getGuiManager().openSettingsMenu(player);
        }
    }

    private void handlePlayerProfileClick(Player sender, String itemName, String title) {
        String cleanTitle = title.replaceAll("§[0-9a-fk-or]", "").replace("❤", "").trim();
        String targetName = "";

        if (cleanTitle.contains("'s Profile")) {
            targetName = cleanTitle.replace("'s Profile", "").trim();
        }

        Player receiver = Bukkit.getPlayer(targetName);

        if (receiver == null || !receiver.isOnline()) {
            sender.closeInventory();
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.player-offline"));
            return;
        }

        if (itemName.contains("Hug")) {
            sender.closeInventory();
            executePlayerCommand(sender, receiver, "hug");
        } else if (itemName.contains("Kiss")) {
            sender.closeInventory();
            executePlayerCommand(sender, receiver, "kiss");
        } else if (itemName.contains("Like")) {
            sender.closeInventory();
            executePlayerCommand(sender, receiver, "like");
        } else if (itemName.contains("Marry")) {
            sender.closeInventory();
            executePlayerCommand(sender, receiver, "marry");
        }
    }

    private void executePlayerCommand(Player sender, Player receiver, String action) {
        long cooldownSeconds = plugin.getConfig().getLong("cooldowns." + action, 60);
        if (plugin.getCooldownManager().hasCooldown(sender.getUniqueId(), action)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(sender.getUniqueId(), action);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("command", action);
            placeholders.put("time", String.valueOf(remaining));
            sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("general.cooldown", placeholders));
            return;
        }

        switch (action.toLowerCase()) {
            case "hug":
                plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "hug", cooldownSeconds * 1000);
                plugin.getPlayerDataManager().incrementStat(receiver.getUniqueId(), "hugs");
                plugin.getPlayerDataManager().incrementGivenStat(sender.getUniqueId(), "hugs");
                plugin.getServer().getPluginManager().callEvent(new HugReceivedEvent(receiver, sender));
                plugin.getServer().getPluginManager().callEvent(new HugGivenEvent(sender, receiver));
                sendActionMessages(sender, receiver, "hug");
                plugin.getEffectManager().playHugEffect(receiver.getLocation());
                break;

            case "kiss":
                plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "kiss", cooldownSeconds * 1000);
                plugin.getPlayerDataManager().incrementStat(receiver.getUniqueId(), "kisses");
                plugin.getPlayerDataManager().incrementGivenStat(sender.getUniqueId(), "kisses");
                plugin.getServer().getPluginManager().callEvent(new KissReceivedEvent(receiver, sender));
                plugin.getServer().getPluginManager().callEvent(new KissGivenEvent(sender, receiver));
                sendActionMessages(sender, receiver, "kiss");
                plugin.getEffectManager().playKissEffect(receiver.getLocation());
                break;

            case "like":
                if (plugin.getFileStorage().hasLiked(sender.getUniqueId(), receiver.getUniqueId())) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player", receiver.getName());
                    sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("like.already-liked", placeholders));
                    return;
                }
                plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "like", cooldownSeconds * 1000);
                plugin.getFileStorage().saveLike(sender.getUniqueId(), receiver.getUniqueId());
                plugin.getPlayerDataManager().incrementStat(receiver.getUniqueId(), "likes");
                plugin.getPlayerDataManager().incrementGivenStat(sender.getUniqueId(), "likes");
                plugin.getServer().getPluginManager().callEvent(new LikeReceivedEvent(receiver, sender));
                plugin.getServer().getPluginManager().callEvent(new LikeGivenEvent(sender, receiver));
                sendActionMessages(sender, receiver, "like");
                break;

            case "marry":
                long proposalCooldown = plugin.getConfig().getLong("marriage.proposal-cooldown", 30);
                if (plugin.getCooldownManager().hasCooldown(sender.getUniqueId(), "proposal")) {
                    long remaining = plugin.getCooldownManager().getRemainingCooldown(sender.getUniqueId(), "proposal");
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("time", String.valueOf(remaining));
                    sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.proposal-cooldown", placeholders));
                    return;
                }

                if (plugin.getMarriageManager().isMarried(sender.getUniqueId()) ||
                        plugin.getMarriageManager().isMarried(receiver.getUniqueId())) {
                    sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.already-married"));
                    return;
                }

                plugin.getCooldownManager().setCooldown(sender.getUniqueId(), "proposal", proposalCooldown * 1000);
                plugin.getMarriageManager().createProposal(sender.getUniqueId(), receiver.getUniqueId());

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("player", receiver.getName());
                sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.proposal-sent", placeholders));

                placeholders.put("player", sender.getName());
                receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.proposal-received", placeholders));
                break;
        }
    }

    private void sendActionMessages(Player sender, Player receiver, String action) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", receiver.getName());
        sender.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage(action + ".sent", placeholders));

        placeholders.put("player", sender.getName());
        receiver.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage(action + ".received", placeholders));
    }
}