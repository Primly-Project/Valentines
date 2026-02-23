package primly.valentines.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import primly.valentines.Valentines;
import primly.valentines.data.Achievement;
import primly.valentines.data.Marriage;
import primly.valentines.data.PlayerData;

import java.text.SimpleDateFormat;
import java.util.*;

public class GuiManager {

    private final Valentines plugin;
    private final Map<UUID, String> playerSearchMode = new HashMap<>();

    public GuiManager(Valentines plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        String title = plugin.getLanguageManager().getMessage("gui.main-menu-title");
        Inventory gui = Bukkit.createInventory(null, 36, title);

        plugin.getPerformanceMonitor().incrementMetric("gui_opens");

        ItemStack statsItem = createItem(Material.BOOK,
                plugin.getLanguageManager().getMessage("gui.stats-button-title"),
                plugin.getLanguageManager().getMessageList("gui.stats-button-lore"));
        gui.setItem(10, statsItem);

        ItemStack marriagesItem = createItem(Material.CAKE,
                plugin.getLanguageManager().getMessage("gui.marriages-button-title"),
                plugin.getLanguageManager().getMessageList("gui.marriages-button-lore"));
        gui.setItem(12, marriagesItem);

        if (plugin.getConfig().getBoolean("effect.enabled", true)) {
            ItemStack settingsItem = createItem(Material.REDSTONE,
                    plugin.getLanguageManager().getMessage("gui.settings-button-title"),
                    plugin.getLanguageManager().getMessageList("gui.settings-button-lore"));
            gui.setItem(14, settingsItem);
        }

        if (plugin.getConfig().getBoolean("leaderboard.enabled", true)) {
            ItemStack leaderboardItem = createItem(Material.GOLDEN_APPLE,
                    plugin.getLanguageManager().getMessage("gui.leaderboard-button-title"),
                    plugin.getLanguageManager().getMessageList("gui.leaderboard-button-lore"));
            gui.setItem(16, leaderboardItem);
        }

        if (plugin.getConfig().getBoolean("mood.enabled", true)) {
            ItemStack moodItem = createItem(Material.OAK_SIGN,
                    plugin.getLanguageManager().getMessage("gui.mood-button-title"),
                    plugin.getLanguageManager().getMessageList("gui.mood-button-lore"));
            gui.setItem(20, moodItem);
        }

        if (plugin.getConfig().getBoolean("achievements.enabled", true)) {
            ItemStack achievementsItem = createItem(Material.ROSE_BUSH,
                    plugin.getLanguageManager().getMessage("gui.achievements-button-title"),
                    plugin.getLanguageManager().getMessageList("gui.achievements-button-lore"));
            gui.setItem(22, achievementsItem);
        }

        ItemStack playerSearchItem = createItem(Material.COMPASS,
                plugin.getLanguageManager().getMessage("gui.player-search-button-title"),
                plugin.getLanguageManager().getMessageList("gui.player-search-button-lore"));
        gui.setItem(24, playerSearchItem);

        player.openInventory(gui);
    }

    public void openStatsMenu(Player player) {
        String title = plugin.getLanguageManager().getMessage("gui.stats-menu-title");
        Inventory gui = Bukkit.createInventory(null, 36, title);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            player.closeInventory();
            return;
        }

        Map<String, String> hugPlaceholders = new HashMap<>();
        hugPlaceholders.put("count", String.valueOf(data.getHugsReceived()));
        ItemStack hugItem = createItem(Material.EMERALD,
                plugin.getLanguageManager().getMessage("gui.hugs-title"),
                plugin.getLanguageManager().getMessageList("gui.hugs-lore", hugPlaceholders));
        gui.setItem(10, hugItem);

        Map<String, String> kissPlaceholders = new HashMap<>();
        kissPlaceholders.put("count", String.valueOf(data.getKissesReceived()));
        ItemStack kissItem = createItem(Material.ROSE_BUSH,
                plugin.getLanguageManager().getMessage("gui.kisses-title"),
                plugin.getLanguageManager().getMessageList("gui.kisses-lore", kissPlaceholders));
        gui.setItem(12, kissItem);

        Map<String, String> likePlaceholders = new HashMap<>();
        likePlaceholders.put("count", String.valueOf(data.getLikesReceived()));
        ItemStack likeItem = createItem(Material.DIAMOND,
                plugin.getLanguageManager().getMessage("gui.likes-title"),
                plugin.getLanguageManager().getMessageList("gui.likes-lore", likePlaceholders));
        gui.setItem(14, likeItem);

        Marriage marriage = plugin.getMarriageManager().getMarriage(player.getUniqueId());
        if (marriage != null) {
            UUID partnerUuid = marriage.getPartner(player.getUniqueId());
            Player partner = Bukkit.getPlayer(partnerUuid);

            Map<String, String> marriagePlaceholders = new HashMap<>();
            marriagePlaceholders.put("partner", partner != null ? partner.getName() : "Unknown");
            marriagePlaceholders.put("days", String.valueOf(marriage.getDaysMarried()));

            ItemStack marriageItem = createItem(Material.GOLDEN_APPLE,
                    plugin.getLanguageManager().getMessage("gui.marriage-title"),
                    plugin.getLanguageManager().getMessageList("gui.marriage-lore", marriagePlaceholders));
            gui.setItem(16, marriageItem);
        } else {
            ItemStack singleItem = createItem(Material.APPLE,
                    plugin.getLanguageManager().getMessage("gui.single-title"),
                    plugin.getLanguageManager().getMessageList("gui.single-lore"));
            gui.setItem(16, singleItem);
        }

        String currentMood = data.getMood();
        String moodDisplay = getMoodDisplay(currentMood);

        Map<String, String> moodPlaceholders = new HashMap<>();
        moodPlaceholders.put("mood", moodDisplay);
        ItemStack moodItem = createItem(Material.TOTEM_OF_UNDYING,
                plugin.getLanguageManager().getMessage("gui.mood-display-title"),
                plugin.getLanguageManager().getMessageList("gui.mood-display-lore", moodPlaceholders));
        gui.setItem(22, moodItem);

        ItemStack backItem = createItem(Material.ARROW,
                plugin.getLanguageManager().getMessage("gui.back-button"),
                new ArrayList<>());
        gui.setItem(31, backItem);

        player.openInventory(gui);
    }

    public void openMoodMenu(Player player) {
        String title = plugin.getLanguageManager().getMessage("gui.mood-title");
        Inventory gui = Bukkit.createInventory(null, 36, title);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            player.closeInventory();
            return;
        }

        String currentMood = data.getMood();

        ItemStack veryGoodMood = createItem(
                currentMood.equals("very-good") ? Material.LIME_WOOL : Material.GREEN_WOOL,
                plugin.getLanguageManager().getMessage("gui.mood-very-good-title") +
                        (currentMood.equals("very-good") ? " " + plugin.getLanguageManager().getMessage("gui.effect-selected") : ""),
                plugin.getLanguageManager().getMessageList("gui.mood-very-good-lore"));
        gui.setItem(11, veryGoodMood);

        ItemStack goodMood = createItem(
                currentMood.equals("good") ? Material.LIME_WOOL : Material.YELLOW_WOOL,
                plugin.getLanguageManager().getMessage("gui.mood-good-title") +
                        (currentMood.equals("good") ? " " + plugin.getLanguageManager().getMessage("gui.effect-selected") : ""),
                plugin.getLanguageManager().getMessageList("gui.mood-good-lore"));
        gui.setItem(12, goodMood);

        ItemStack neutralMood = createItem(
                currentMood.equals("neutral") ? Material.LIME_WOOL : Material.WHITE_WOOL,
                plugin.getLanguageManager().getMessage("gui.mood-neutral-title") +
                        (currentMood.equals("neutral") ? " " + plugin.getLanguageManager().getMessage("gui.effect-selected") : ""),
                plugin.getLanguageManager().getMessageList("gui.mood-neutral-lore"));
        gui.setItem(13, neutralMood);

        ItemStack badMood = createItem(
                currentMood.equals("bad") ? Material.LIME_WOOL : Material.ORANGE_WOOL,
                plugin.getLanguageManager().getMessage("gui.mood-bad-title") +
                        (currentMood.equals("bad") ? " " + plugin.getLanguageManager().getMessage("gui.effect-selected") : ""),
                plugin.getLanguageManager().getMessageList("gui.mood-bad-lore"));
        gui.setItem(14, badMood);

        ItemStack veryBadMood = createItem(
                currentMood.equals("very-bad") ? Material.LIME_WOOL : Material.RED_WOOL,
                plugin.getLanguageManager().getMessage("gui.mood-very-bad-title") +
                        (currentMood.equals("very-bad") ? " " + plugin.getLanguageManager().getMessage("gui.effect-selected") : ""),
                plugin.getLanguageManager().getMessageList("gui.mood-very-bad-lore"));
        gui.setItem(15, veryBadMood);

        ItemStack backItem = createItem(Material.ARROW,
                plugin.getLanguageManager().getMessage("gui.back-button"),
                new ArrayList<>());
        gui.setItem(31, backItem);

        player.openInventory(gui);
    }

    public void openLeaderboardMenu(Player player) {
        String title = plugin.getLanguageManager().getMessage("gui.leaderboard-title");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        List<PlayerData> topPlayers = plugin.getPlayerDataManager().getTopPlayers(45);

        for (int i = 0; i < topPlayers.size() && i < 45; i++) {
            PlayerData data = topPlayers.get(i);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("rank", String.valueOf(i + 1));
            placeholders.put("player", data.getName());
            placeholders.put("score", String.valueOf(data.getTotalScore()));
            placeholders.put("hugs", String.valueOf(data.getHugsReceived()));
            placeholders.put("kisses", String.valueOf(data.getKissesReceived()));
            placeholders.put("likes", String.valueOf(data.getLikesReceived()));

            Material material = i < 3 ? Material.GOLDEN_APPLE : Material.APPLE;

            List<String> lore = new ArrayList<>();
            lore.add("&fTotal Love Score: &d" + data.getTotalScore());
            lore.add("&fHugs: &d" + data.getHugsReceived());
            lore.add("&fKisses: &d" + data.getKissesReceived());
            lore.add("&fLikes: &d" + data.getLikesReceived());

            ItemStack playerItem = createItem(material,
                    plugin.getLanguageManager().getMessage("gui.leaderboard-entry-title", placeholders),
                    lore);

            gui.setItem(i, playerItem);
        }

        ItemStack backItem = createItem(Material.ARROW,
                plugin.getLanguageManager().getMessage("gui.back-button"),
                new ArrayList<>());
        gui.setItem(49, backItem);

        player.openInventory(gui);
    }

    public void openMarriagesMenu(Player player) {
        String title = plugin.getLanguageManager().getMessage("gui.marriages-title");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        List<Marriage> marriages = plugin.getMarriageManager().getAllMarriages();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < marriages.size() && i < 45; i++) {
            Marriage marriage = marriages.get(i);
            Player player1 = Bukkit.getPlayer(marriage.player1());
            Player player2 = Bukkit.getPlayer(marriage.player2());

            String player1Name = player1 != null ? player1.getName() : "Unknown";
            String player2Name = player2 != null ? player2.getName() : "Unknown";

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player1", player1Name);
            placeholders.put("player2", player2Name);
            placeholders.put("days", String.valueOf(marriage.getDaysMarried()));
            placeholders.put("date", dateFormat.format(new Date(marriage.marriageDate())));

            ItemStack marriageItem = createItem(Material.CAKE,
                    plugin.getLanguageManager().getMessage("gui.marriage-couple-title", placeholders),
                    plugin.getLanguageManager().getMessageList("gui.marriage-couple-lore", placeholders));

            gui.setItem(i, marriageItem);
        }

        ItemStack backItem = createItem(Material.ARROW,
                plugin.getLanguageManager().getMessage("gui.back-button"),
                new ArrayList<>());
        gui.setItem(49, backItem);

        player.openInventory(gui);
    }

    public void openSettingsMenu(Player player) {
        String title = plugin.getLanguageManager().getMessage("gui.settings-title");
        Inventory gui = Bukkit.createInventory(null, 36, title);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            player.closeInventory();
            return;
        }

        String currentEffect = data.getEffectType();

        ItemStack heartEffect = createItem(
                currentEffect.equals("heart") ? Material.REDSTONE_BLOCK : Material.RED_WOOL,
                plugin.getLanguageManager().getMessage("gui.effect-heart") +
                        (currentEffect.equals("heart") ? " " + plugin.getLanguageManager().getMessage("gui.effect-selected") : ""),
                new ArrayList<>());
        gui.setItem(10, heartEffect);

        ItemStack spiralEffect = createItem(
                currentEffect.equals("spiral") ? Material.REDSTONE_BLOCK : Material.YELLOW_WOOL,
                plugin.getLanguageManager().getMessage("gui.effect-spiral") +
                        (currentEffect.equals("spiral") ? " " + plugin.getLanguageManager().getMessage("gui.effect-selected") : ""),
                new ArrayList<>());
        gui.setItem(12, spiralEffect);

        ItemStack cloudEffect = createItem(
                currentEffect.equals("cloud") ? Material.REDSTONE_BLOCK : Material.WHITE_WOOL,
                plugin.getLanguageManager().getMessage("gui.effect-cloud") +
                        (currentEffect.equals("cloud") ? " " + plugin.getLanguageManager().getMessage("gui.effect-selected") : ""),
                new ArrayList<>());
        gui.setItem(14, cloudEffect);

        ItemStack effectToggle = createItem(
                data.isEffectEnabled() ? Material.LIME_DYE : Material.GRAY_DYE,
                data.isEffectEnabled() ? plugin.getLanguageManager().getMessage("gui.effect-selected") :
                        plugin.getLanguageManager().getMessage("gui.effect-disabled"),
                new ArrayList<>());
        gui.setItem(16, effectToggle);

        ItemStack backItem = createItem(Material.ARROW,
                plugin.getLanguageManager().getMessage("gui.back-button"),
                new ArrayList<>());
        gui.setItem(31, backItem);

        player.openInventory(gui);
    }

    public void openAchievementsMenu(Player player) {
        String title = plugin.getLanguageManager().getMessage("gui.achievements-menu-title");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        if (data == null) {
            player.closeInventory();
            return;
        }

        Achievement[] achievements = Achievement.values();
        for (int i = 0; i < achievements.length && i < 45; i++) {
            Achievement achievement = achievements[i];
            boolean hasAchievement = data.hasAchievement(achievement.getKey());

            Material material = hasAchievement ? Material.LIME_DYE : Material.GRAY_DYE;
            String status = hasAchievement ? "&a✔ " : "&7✘ ";

            String name = plugin.getLanguageManager().getMessage("achievements." + achievement.getKey() + ".name");
            String description = plugin.getLanguageManager().getMessage("achievements." + achievement.getKey() + ".description");

            List<String> lore = new ArrayList<>();
            lore.add("&f" + description);
            lore.add("");
            lore.add(hasAchievement ? "&aUnlocked!" : "&7Locked");

            ItemStack achievementItem = createItem(material,
                    status + "&d" + name,
                    lore);

            gui.setItem(i, achievementItem);
        }

        int unlocked = plugin.getAchievementManager().getUnlockedCount(player.getUniqueId());
        int total = Achievement.values().length;
        double percentage = plugin.getAchievementManager().getProgressPercentage(player.getUniqueId());

        List<String> progressLore = new ArrayList<>();
        progressLore.add("&fUnlocked: &d" + unlocked + "&f/&d" + total);
        progressLore.add("&fProgress: &d" + String.format("%.1f", percentage) + "%");

        ItemStack progressItem = createItem(Material.BOOK,
                "&d&lAchievement Progress",
                progressLore);
        gui.setItem(49, progressItem);

        ItemStack backItem = createItem(Material.ARROW,
                plugin.getLanguageManager().getMessage("gui.back-button"),
                new ArrayList<>());
        gui.setItem(48, backItem);

        player.openInventory(gui);
    }

    public void openPlayerProfile(Player viewer, Player target) {
        Map<String, String> titlePlaceholders = new HashMap<>();
        titlePlaceholders.put("player", target.getName());
        String title = plugin.getLanguageManager().getMessage("gui.player-profile-title", titlePlaceholders);
        Inventory gui = Bukkit.createInventory(null, 36, title);

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        if (data == null) {
            viewer.closeInventory();
            return;
        }

        List<String> profileLore = new ArrayList<>();
        profileLore.add(ChatColor.WHITE + "Total Score: " + ChatColor.LIGHT_PURPLE + data.getTotalScore());
        profileLore.add(ChatColor.WHITE + "Hugs: " + ChatColor.LIGHT_PURPLE + data.getHugsReceived());
        profileLore.add(ChatColor.WHITE + "Kisses: " + ChatColor.LIGHT_PURPLE + data.getKissesReceived());
        profileLore.add(ChatColor.WHITE + "Likes: " + ChatColor.LIGHT_PURPLE + data.getLikesReceived());

        ItemStack playerInfo = createItem(Material.PLAYER_HEAD,
                ChatColor.LIGHT_PURPLE + target.getName() + "'s Profile",
                profileLore);
        gui.setItem(4, playerInfo);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());

        ItemStack hugButton = createItem(Material.EMERALD,
                plugin.getLanguageManager().getMessage("gui.hug-button-title", placeholders),
                plugin.getLanguageManager().getMessageList("gui.hug-button-lore", placeholders));
        gui.setItem(10, hugButton);

        ItemStack kissButton = createItem(Material.ROSE_BUSH,
                plugin.getLanguageManager().getMessage("gui.kiss-button-title", placeholders),
                plugin.getLanguageManager().getMessageList("gui.kiss-button-lore", placeholders));
        gui.setItem(12, kissButton);

        ItemStack likeButton = createItem(Material.DIAMOND,
                plugin.getLanguageManager().getMessage("gui.like-button-title", placeholders),
                plugin.getLanguageManager().getMessageList("gui.like-button-lore", placeholders));
        gui.setItem(14, likeButton);

        if (!plugin.getMarriageManager().isMarried(viewer.getUniqueId()) &&
                !plugin.getMarriageManager().isMarried(target.getUniqueId())) {
            ItemStack marryButton = createItem(Material.GOLDEN_APPLE,
                    plugin.getLanguageManager().getMessage("gui.marry-button-title", placeholders),
                    plugin.getLanguageManager().getMessageList("gui.marry-button-lore", placeholders));
            gui.setItem(16, marryButton);
        }

        String targetMood = data.getMood();
        String moodDisplay = getMoodDisplay(targetMood);

        ItemStack targetMoodItem = createItem(Material.TOTEM_OF_UNDYING,
                ChatColor.LIGHT_PURPLE + target.getName() + "'s Mood",
                List.of(ChatColor.WHITE + "Current mood: " + moodDisplay));
        gui.setItem(22, targetMoodItem);

        ItemStack backItem = createItem(Material.ARROW,
                plugin.getLanguageManager().getMessage("gui.back-button"),
                new ArrayList<>());
        gui.setItem(31, backItem);

        viewer.openInventory(gui);
    }

    private String getMoodDisplay(String mood) {
        switch (mood) {
            case "very-good":
                return ChatColor.GREEN + "😄 Very Good";
            case "good":
                return ChatColor.YELLOW + "😊 Good";
            case "neutral":
                return ChatColor.WHITE + "😐 Neutral";
            case "bad":
                return ChatColor.RED + "😞 Bad";
            case "very-bad":
                return ChatColor.DARK_RED + "😢 Very Bad";
            default:
                return ChatColor.GRAY + "❓ Unknown";
        }
    }

    public void startPlayerSearch(Player player) {
        playerSearchMode.put(player.getUniqueId(), "searching");
        player.closeInventory();
        player.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("player-search.prompt"));
    }

    public boolean isInSearchMode(Player player) {
        return playerSearchMode.containsKey(player.getUniqueId());
    }

    public void handlePlayerSearch(Player searcher, String playerName) {
        playerSearchMode.remove(searcher.getUniqueId());

        Player target = Bukkit.getPlayer(playerName);
        if (target == null || !target.isOnline()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", playerName);
            searcher.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("player-search.not-found", placeholders));
            return;
        }

        if (target.equals(searcher)) {
            openStatsMenu(searcher);
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());
        searcher.sendMessage(plugin.getPrefix() + plugin.getLanguageManager().getMessage("player-search.searching", placeholders));

        openPlayerProfile(searcher, target);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);

            item.setItemMeta(meta);
        }

        return item;
    }
}