package sr.party;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Acidsin
 */

/*
 * This class also houses the Ender Vault stuff for Raid.
 * 
 * 
 */

public class Tailor implements Listener
{
    
    public Party plugin;
    public Tailor (Party plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent event)
    {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        Inventory playerinv = player.getInventory();
        int slot = event.getRawSlot();
        
        boolean right = event.isRightClick();
        boolean shift = event.isShiftClick();
        int limit = 0;
        int itemtype;
        int stacksize;

        //Stop stacks of items that should not be stacked
        if (!(player.hasPermission("more.stacks")))
        {
            for (ItemStack i : playerinv.getContents())
            {
                if (i != null)
                {

                  if (i.getTypeId() == 298 || i.getTypeId() == 299 || i.getTypeId() == 300 || i.getTypeId() == 301 || i.getTypeId() == 302 || i.getTypeId() == 303 || i.getTypeId() == 304 || i.getTypeId() == 304 || i.getTypeId() == 305 || i.getTypeId() == 306 || i.getTypeId() == 307 || i.getTypeId() == 308 || i.getTypeId() == 309 || i.getTypeId() == 310 || i.getTypeId() == 311 || i.getTypeId() == 312 || i.getTypeId() == 313 || i.getTypeId() == 314 || i.getTypeId() == 315 || i.getTypeId() == 316 || i.getTypeId() == 317 )
                  {
                    stacksize = i.getAmount();

                    if (stacksize > 1)
                    {
                        i.setAmount(1);
                    }
                  }
                }

            }
        }

        if (inv.getType() == InventoryType.ENDER_CHEST)
        {
            
            if (player.hasPermission("endervault.three"))
            {
                limit = 2;
            }
        
            if (player.hasPermission("endervault.ten"))
            {
                limit = 9;
            }
        
            if (player.hasPermission("endervault.twenty"))
            {
                limit = 19;
            }
                if (right == true || shift == false)
                {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GOLD + "You can ONLY shift click items to an Ender Vault!");
                    player.sendMessage(ChatColor.GOLD + "Try Shift Left Clicking items into your Ender Vault.");
                }
            
                if (slot < 27)
                {
                    return;
                }
                int size = 0;
            
                for (ItemStack i : inv.getContents())
                {
                    if (i != null)
                    {
                    size = size + 1;
                    }
                
                }
                if (size > limit)
                {
                    player.sendMessage(ChatColor.GOLD + "Your Ender Vault is Full.  Remove items to add something new.");
                    event.setCancelled(true);
                }
        }
        
        
        if (inv.getType() == InventoryType.WORKBENCH || inv.getType() == InventoryType.CRAFTING)
        {

                if (slot < 27 && slot > 9)
                {
                    return;
                }
                boolean containsleather = false;
                boolean containsdye = false;
            
                for (ItemStack i : inv.getContents())
                {
                    if (i != null)
                    {
                        
                        if (i.getTypeId() == 298 || i.getTypeId() == 299 || i.getTypeId() == 300 || i.getTypeId() == 301)
                        {
                            containsleather = true;
                        }
                        if (i.getTypeId() == 351)
                        {
                            containsdye = true;
                        }
                    }
                
                }
                
                if (player.hasPermission("tailor.craft"))
                {
                    return;
                }
                
                if (slot == 0)
                {
                    return;
                }
                
                if (containsleather == true || containsdye == true)
                {
                    player.sendMessage(ChatColor.GOLD + "Only Tailors can dye armor.  Press Escape to Exit out of the inventory Screen." + slot);
                    event.setCancelled(true);
                }
        }
        
    }
}
