package primly.valentines.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import primly.valentines.Valentines;
import primly.valentines.data.Marriage;
import primly.valentines.data.MarriageProposal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MarriageManager {
    
    private final Valentines plugin;
    private final Map<UUID, Marriage> marriageCache = new HashMap<>();
    private final Map<UUID, MarriageProposal> proposalCache = new HashMap<>();
    
    public MarriageManager(Valentines plugin) {
        this.plugin = plugin;
    }
    
    public void loadMarriage(UUID player) {
        Marriage marriage = plugin.getFileStorage().loadMarriage(player);
        if (marriage != null) {
            marriageCache.put(player, marriage);
        }
    }
    
    public Marriage getMarriage(UUID player) {
        return marriageCache.get(player);
    }
    
    public boolean isMarried(UUID player) {
        return marriageCache.containsKey(player);
    }
    
    public UUID getPartner(UUID player) {
        Marriage marriage = marriageCache.get(player);
        return marriage != null ? marriage.getPartner(player) : null;
    }
    
    public void createMarriage(UUID player1, UUID player2) {
        removeProposal(player1);
        removeProposal(player2);
        
        Marriage marriage = new Marriage(player1, player2, System.currentTimeMillis());
        marriageCache.put(player1, marriage);
        marriageCache.put(player2, marriage);
        
        plugin.getFileStorage().saveMarriage(marriage);
        
        plugin.getPerformanceMonitor().incrementMetric("marriages_created");

        if (plugin.getConfig().getBoolean("marriage.announcement", true)) {
            Player p1 = Bukkit.getPlayer(player1);
            Player p2 = Bukkit.getPlayer(player2);
            
            if (p1 != null && p2 != null) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("player1", p1.getName());
                placeholders.put("player2", p2.getName());
                
                String announcement = plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.announcement", placeholders);
                Bukkit.broadcastMessage(announcement);
            }
        }
    }
    
    public void deleteMarriage(UUID player) {
        Marriage marriage = marriageCache.get(player);
        if (marriage != null) {
            UUID partner = marriage.getPartner(player);
            
            marriageCache.remove(player);
            if (partner != null) {
                marriageCache.remove(partner);
            }
            
            plugin.getFileStorage().deleteMarriage(player);
            
            if (plugin.getConfig().getBoolean("marriage.announcement", true)) {
                Player p1 = Bukkit.getPlayer(player);
                Player p2 = partner != null ? Bukkit.getPlayer(partner) : null;
                
                if (p1 != null && p2 != null) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player1", p1.getName());
                    placeholders.put("player2", p2.getName());
                    
                    String announcement = plugin.getPrefix() + plugin.getLanguageManager().getMessage("marry.divorce-announcement", placeholders);
                    Bukkit.broadcastMessage(announcement);
                }
            }
        }
    }
    
    public void createProposal(UUID proposer, UUID target) {
        MarriageProposal proposal = new MarriageProposal(proposer, target, System.currentTimeMillis());
        proposalCache.put(target, proposal);
        plugin.getFileStorage().saveProposal(proposal);
    }
    
    public MarriageProposal getProposal(UUID target) {
        MarriageProposal proposal = proposalCache.get(target);
        if (proposal == null) {
            proposal = plugin.getFileStorage().loadProposal(target);
            if (proposal != null) {
                proposalCache.put(target, proposal);
            }
        }
        
        if (proposal != null) {
            long timeout = plugin.getConfig().getLong("marriage.proposal-timeout", 45) * 1000;
            if (proposal.isExpired(timeout)) {
                removeProposal(target);
                return null;
            }
        }
        
        return proposal;
    }
    
    public void removeProposal(UUID target) {
        proposalCache.remove(target);
        plugin.getFileStorage().deleteProposal(target);
    }
    
    public List<Marriage> getAllMarriages() {
        return plugin.getFileStorage().getAllMarriages();
    }
}