package sr.party;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

/**
 *
 * @author Cross
 */
public class EnchantStuff implements Listener
{
    public Party plugin;
    
    public EnchantStuff (Party plugin)
    {
        this.plugin = plugin;
    }
    
    /*
    @EventHandler
    public void onSignEdit (SignChangeEvent event)
    {
        if (event.getPlayer().isOp())
        {
            return;
        }
        
        for (String s : event.getLines())
        {
            if (s.toLowerCase().contains("exp") && (s.toLowerCase().contains("[Trade]")))
            {
                event.setLine(1, "No EXP trading.");
                event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No EXP Trading.");
                event.setCancelled(true);
            }
        }
    }
    */
    
    @EventHandler
    public void onEnchant (EnchantItemEvent event)
    {
        if (event.getEnchantsToAdd().containsKey(Enchantment.THORNS))
        {
            int lvl = event.getEnchantsToAdd().get(Enchantment.THORNS);
            event.getEnchantsToAdd().remove(Enchantment.THORNS);
            
            Boolean protProj = true;
            
            if (event.getEnchantsToAdd().containsKey(Enchantment.PROTECTION_PROJECTILE))
            {
                protProj = false;
                event.getEnchantsToAdd().put(Enchantment.PROTECTION_FIRE, lvl);
            }
            else
            {
                event.getEnchantsToAdd().put(Enchantment.PROTECTION_PROJECTILE, lvl);
            }
            
            if (protProj)
            {
                event.getEnchanter().sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Thorns tried to be on this, but instead you received Projectile Protection!");
            }
            else
            {
                event.getEnchanter().sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Thorns tried to be on this, but instead you received Fire Protection!");
            }
            
        }
        
        if (event.getEnchantsToAdd().containsKey(Enchantment.FIRE_ASPECT))
        {
            int lvl = event.getEnchantsToAdd().get(Enchantment.FIRE_ASPECT);
            event.getEnchantsToAdd().remove(Enchantment.FIRE_ASPECT);
            
            event.getEnchantsToAdd().put(Enchantment.LOOT_BONUS_MOBS, lvl);
        }
    }
    
    @EventHandler
    public void onLevelGain (PlayerLevelChangeEvent event)
    {
        Player player = event.getPlayer();
        
        if (player.isOp() || player.hasPermission("sr.exp.bypass"))
        {
            player.setLevel(50);
            return;
        }
        
        if (event.getNewLevel() > 15)
        {
            player.setLevel(15);
        }
        
        if (player.getLevel() > 15)
        {
            player.setLevel(15);
        }
    }
    
    @EventHandler
    public void onXPGain (PlayerExpChangeEvent event)
    {
        Player player = event.getPlayer();
        
        if (player.isOp() || player.hasPermission("sr.exp.bypass"))
        {
            player.setExp(4000);
            event.setAmount(0);
            return;
        }
        
        if (player.getExp() > 255)
        {
            player.setExp(255);
            event.setAmount(0);
        }
        
        if (player.getLevel() > 15)
        {
            player.setLevel(15);
            event.setAmount(0);
        }
    }
}
