package primly.valentines;

import primly.valentines.commands.AchievementsCommand;
import primly.valentines.commands.DivorceCommand;
import primly.valentines.commands.GuiCommand;
import primly.valentines.commands.HugCommand;
import primly.valentines.commands.KissCommand;
import primly.valentines.commands.LikeCommand;
import primly.valentines.commands.MarryCommand;
import primly.valentines.commands.MoodCommand;
import primly.valentines.commands.ValentinesCommand;
import primly.valentines.effects.EffectManager;
import primly.valentines.gui.GuiManager;
import primly.valentines.listeners.AchievementListener;
import primly.valentines.listeners.ChatListener;
import primly.valentines.listeners.PlayerListener;
import primly.valentines.managers.AchievementManager;
import primly.valentines.managers.AnniversaryManager;
import primly.valentines.managers.ConfigManager;
import primly.valentines.managers.CooldownManager;
import primly.valentines.managers.LanguageManager;
import primly.valentines.managers.MarriageManager;
import primly.valentines.managers.PlayerDataManager;
import primly.valentines.storage.FileStorage;
import primly.valentines.utils.PerformanceMonitor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Valentines extends JavaPlugin {

    private static Valentines instance;
    private FileStorage fileStorage;
    private LanguageManager languageManager;
    private PlayerDataManager playerDataManager;
    private MarriageManager marriageManager;
    private CooldownManager cooldownManager;
    private EffectManager effectManager;
    private GuiManager guiManager;
    private AnniversaryManager anniversaryManager;
    private PerformanceMonitor performanceMonitor;
    private AchievementManager achievementManager;
    private ConfigManager configManager;
    private String prefix;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        configManager.initializeConfig();

        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix", "&d&lValentines &7»&r "));

        initializeManagers();

        registerCommands();

        registerListeners();

        if (getConfig().getBoolean("effect.enabled")) {
            effectManager.startEffectTask();
        }

        anniversaryManager.startAnniversaryTask();

        startAutoSaveTask();

        performanceMonitor.startMonitoring();

        getLogger().info("Valentines v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        if (performanceMonitor != null) {
            performanceMonitor.stopMonitoring();
        }

        if (cooldownManager != null) {
            cooldownManager.stopCleanupTask();
        }

        if (effectManager != null) {
            effectManager.stopEffectTask();
        }

        if (anniversaryManager != null) {
            anniversaryManager.stopAnniversaryTask();
        }

        if (playerDataManager != null) {
            playerDataManager.saveAllData();
        }

        if (fileStorage != null) {
            fileStorage.saveAll();
            fileStorage.shutdown();
        }

        getLogger().info("Valentines plugin has been disabled.");
    }

    private void initializeManagers() {
        this.languageManager = new LanguageManager(this);
        this.fileStorage = new FileStorage(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.marriageManager = new MarriageManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.effectManager = new EffectManager(this);
        this.guiManager = new GuiManager(this);
        this.anniversaryManager = new AnniversaryManager(this);
        this.performanceMonitor = new PerformanceMonitor(this);
        this.achievementManager = new AchievementManager(this);
    }

    private void registerCommands() {
        registerCommand("valentines", new ValentinesCommand(this));
        registerCommand("vgui", new GuiCommand(this));
        registerCommand("marry", new MarryCommand(this));
        registerCommand("divorce", new DivorceCommand(this));
        registerCommand("mood", new MoodCommand(this));
        registerCommand("hug", new HugCommand(this));
        registerCommand("kiss", new KissCommand(this));
        registerCommand("like", new LikeCommand(this));
        registerCommand("achievements", new AchievementsCommand(this));
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            getLogger().warning("Command not found in plugin.yml: " + commandName);
            return;
        }

        command.setExecutor(executor);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AchievementListener(this), this);

        if (getConfig().getBoolean("symbol-change")) {
            Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        }
    }

    private void startAutoSaveTask() {
        int autoSaveInterval = getConfig().getInt("auto-save", 30);

        new BukkitRunnable() {
            @Override
            public void run() {
                playerDataManager.saveAllData();
                fileStorage.saveAll();
                getLogger().fine("Auto-saved all player data");
            }
        }.runTaskTimerAsynchronously(this, 0L, autoSaveInterval * 60 * 20L);
    }

    public void reloadPlugin() {
        reloadConfig();
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix", "&d&lValentines &7»&r "));
        languageManager.reloadLanguage();

        if (getConfig().getBoolean("effect.enabled")) {
            effectManager.startEffectTask();
        } else {
            effectManager.stopEffectTask();
        }

        anniversaryManager.stopAnniversaryTask();
        anniversaryManager.startAnniversaryTask();

        getLogger().info(languageManager.getMessage("plugin.reload"));
    }

    public static Valentines getInstance() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    public FileStorage getFileStorage() {
        return fileStorage;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public MarriageManager getMarriageManager() {
        return marriageManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public AnniversaryManager getAnniversaryManager() {
        return anniversaryManager;
    }

    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }
}