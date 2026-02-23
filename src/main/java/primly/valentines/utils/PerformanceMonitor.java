package primly.valentines.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import primly.valentines.Valentines;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMonitor {

    private final Valentines plugin;
    private final ConcurrentHashMap<String, AtomicLong> metrics;
    private final MemoryMXBean memoryBean;
    private BukkitTask monitorTask;
    private long lastGCTime = 0;

    public PerformanceMonitor(Valentines plugin) {
        this.plugin = plugin;
        this.metrics = new ConcurrentHashMap<>();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        initializeMetrics();
    }

    private void initializeMetrics() {
        metrics.put("commands_executed", new AtomicLong(0));
        metrics.put("effects_rendered", new AtomicLong(0));
        metrics.put("gui_opens", new AtomicLong(0));
        metrics.put("data_saves", new AtomicLong(0));
        metrics.put("marriages_created", new AtomicLong(0));
    }

    public void startMonitoring() {
        if (!plugin.getConfig().getBoolean("performance.monitoring-enabled", true)) {
            return;
        }

        int interval = plugin.getConfig().getInt("performance.monitor-interval", 300);

        monitorTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkPerformance();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, interval * 20L);
    }

    public void stopMonitoring() {
        if (monitorTask != null) {
            monitorTask.cancel();
            monitorTask = null;
        }
    }

    private void checkPerformance() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long usedMemory = heapUsage.getUsed();
        long maxMemory = heapUsage.getMax();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

        int threshold = plugin.getConfig().getInt("performance.memory-threshold", 85);
        if (memoryUsagePercent > threshold) {
            plugin.getLogger().warning("High memory usage detected: " + String.format("%.1f", memoryUsagePercent) + "%");
            suggestGarbageCollection();
        }

        if (plugin.getConfig().getBoolean("performance.detailed-logging", false)) {
            logDetailedMetrics();
        }
    }

    private void suggestGarbageCollection() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastGCTime > 300000) {
            System.gc();
            lastGCTime = currentTime;
            plugin.getLogger().info("Performed garbage collection to free memory");
        }
    }

    private void logDetailedMetrics() {
        plugin.getLogger().info("=== Valentines Performance Metrics ===");
        plugin.getLogger().info("Commands executed: " + metrics.get("commands_executed").get());
        plugin.getLogger().info("Effects rendered: " + metrics.get("effects_rendered").get());
        plugin.getLogger().info("GUI opens: " + metrics.get("gui_opens").get());
        plugin.getLogger().info("Data saves: " + metrics.get("data_saves").get());
        plugin.getLogger().info("Marriages created: " + metrics.get("marriages_created").get());
        plugin.getLogger().info("Online players: " + Bukkit.getOnlinePlayers().size());

        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        plugin.getLogger().info("Memory usage: " + formatBytes(heapUsage.getUsed()) + " / " + formatBytes(heapUsage.getMax()));
    }

    public void incrementMetric(String metric) {
        AtomicLong counter = metrics.get(metric);
        if (counter != null) {
            counter.incrementAndGet();
        }
    }

    public long getMetric(String metric) {
        AtomicLong counter = metrics.get(metric);
        return counter != null ? counter.get() : 0;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}