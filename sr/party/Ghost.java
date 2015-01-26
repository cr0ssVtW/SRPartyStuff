package sr.party;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Cross
 */
public class Ghost implements Listener
{

    public Party plugin;
    
    
    public Ghost (Party plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        
        if (Party.ghost.size()> 0)
        {
            Iterator<String> it = Party.ghost.iterator();
            String name;
            Player pl;
            while (it.hasNext())
            {
                name = it.next();
                pl = Bukkit.getPlayer(name);

                if (player.canSee(pl) && player != pl)
                {
                    player.hidePlayer(pl);
                    // player.sendMessage(ChatColor.AQUA + "Hiding Ghost: " + pl.getName());
                }
            }
        }
        else
        {
            return;
        }
        
        if (Party.ghost.contains(player.getName()))
        {
            plugin.onStealth(player);
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You return to the land of the dead...");
        }
    }
        
        
    
    
    @EventHandler
    public void onPlayerQuit (PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (Party.ghost.contains(player.getName()))
        {
            plugin.unStealth(player);
        }
    }
    
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamage (EntityDamageEvent event)
    {
        if (!(event instanceof EntityDamageByEntityEvent)) 
        {
            return;
        }
        EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;

        if ((event_EE.getDamager() instanceof Player))
        {
            Player damager = (Player)event_EE.getDamager();
      
            if (Party.ghost.contains(damager.getName()))
            {
                event.setCancelled(true);
                event.setDamage(0);
                damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Ghosts cannot attack!");
                return;
            }
        }
    }
    
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteract (PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (Party.ghost.contains(player.getName()))
        {
          event.setCancelled(true);
          player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can not do that while in ghost form");
          return;
        }
    }
    
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
      Player player = event.getPlayer();
      if (Party.ghost.contains(player.getName()))
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can not do that while in ghost form");
        return;
      }
    }

    @EventHandler 
    public void onBlockBreak(BlockBreakEvent event)
    {
      Player player = event.getPlayer();
      if (Party.ghost.contains(player.getName()))
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can not do that while in ghost form");
        return;
      }
    }

    @EventHandler 
    public void onPlayerPickup(PlayerPickupItemEvent event)
    {
      Player player = event.getPlayer();
      if (Party.ghost.contains(player.getName()))
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can not do that while in ghost form");
        return;
      }
    }
    
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event)
    {
      Player player = event.getPlayer();
      if (Party.ghost.contains(player.getName()))
      {
        event.setCancelled(true);
        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can not do that while in ghost form");
        return;
      }
    }
}
