package sr.party;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import sr.party.Party;

/**
 *
 * @author cr0ss
 */
public class Druid implements Listener
{

    public Party plugin;
    
    public Druid (Party plugin)
    {
        this.plugin = plugin;
    }
   
    public HashMap<String, Integer> offdruidspell = new HashMap<String, Integer>();
    public HashMap<String, Integer> defdruidspell = new HashMap<String, Integer>();
   
    public HashMap<String, Integer> lightningtimer = new HashMap<String, Integer>(); 
    public HashMap<String, Integer> entangletimer = new HashMap<String, Integer>(); 
    
    
    public HashMap<String, Integer> rejuvtimer = new HashMap<String, Integer>(); 
    public HashMap<String, Integer> stoneskintimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> catformtimer = new HashMap<String, Integer>(); 
    
    
    
    
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        
        int calllightning = 1;
        int entangle = 2;
        
        int rejuv = 1;
        int catform = 2;
        int stoneskin = 3;
       
        
        int currenttime = plugin.getCurrentTime();
        
        Player player = event.getPlayer();
        
        
        if (player.hasPermission("druid.use"))
        {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                if (player.getInventory().getItemInHand() != null)
                {
                    if (player.getInventory().getItemInHand().getTypeId() == 37) // yellow flower - defensive
                    {
                        int defspell;
                        
                        if (defdruidspell.containsKey(player.getName().toLowerCase()))
                        {
                            defspell = defdruidspell.get(player.getName().toLowerCase()) + 1;
                            
                            if (defspell > stoneskin)
                            {
                                defspell = 1;
                            }
                        }
                        else
                        {
                            defspell = 1;
                        }
                        
                        if (defspell == rejuv)
                        {
                            player.sendMessage(ChatColor.AQUA + "You ready your " + ChatColor.GOLD + "Rejuvenation" + ChatColor.AQUA + ".");
                        }
                        
                        if (defspell == catform)
                        {
                            player.sendMessage(ChatColor.AQUA + "You ready " + ChatColor.GOLD + "Cat Form" + ChatColor.AQUA + ".");
                        }
                        
                        if (defspell == stoneskin)
                        {
                            player.sendMessage(ChatColor.AQUA + "You ready your " + ChatColor.GOLD + "Stoneskin" + ChatColor.AQUA + ".");
                        }
                        
                        defdruidspell.put(player.getName().toLowerCase(), defspell);
                        
                        event.setCancelled(true);
                         
                    } // end yellow flower (def spell)
                    
                    if (player.getInventory().getItemInHand().getTypeId() == 38) // red flower (off spell)
                    {
                        
                        int offspell;
                        
                        if (offdruidspell.containsKey(player.getName().toLowerCase()))
                        {
                            offspell = offdruidspell.get(player.getName().toLowerCase()) + 1;
                            
                            if (offspell > entangle)
                            {
                                offspell = 1;
                            }
                        }
                        else
                        {
                            offspell = 1;
                        }
                        
                        if (offspell == calllightning)
                        {
                            player.sendMessage(ChatColor.DARK_AQUA + "You ready your " + ChatColor.GOLD + "Call Lightning" + ChatColor.DARK_AQUA + ".");
                        }
                        
                        if (offspell == entangle)
                        {
                            player.sendMessage(ChatColor.DARK_AQUA + "You ready " + ChatColor.GOLD + "Entangling Vines" + ChatColor.DARK_AQUA + ".");
                        }
                        
                        
                        offdruidspell.put(player.getName().toLowerCase(), offspell);
                        
                        event.setCancelled(true);
                    } // end red flower (offensive spell)
                }
                
            } // end RIGHT CLICK
            
            
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                if (player.getInventory().getItemInHand().getTypeId() == 37) // if yellow flower (def spell)
                {
                    if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null
                            && player.getInventory().getBoots() != null)
                    {
                       ItemStack helm = player.getInventory().getHelmet();
                       ItemStack chest = player.getInventory().getChestplate();
                       ItemStack legs = player.getInventory().getLeggings();
                       ItemStack boots = player.getInventory().getBoots(); 
                       
                       if (helm.getTypeId() == 298 && chest.getTypeId() == 299 && legs.getTypeId() == 300 && boots.getTypeId() == 301)
                       {
                           
                       }
                    }
                    
                    
                }
            }
        }
    }
    
    
    
    
}
