package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Cross
 */
public class Prospector implements Listener
{
    public Party plugin;
    
    public Prospector (Party plugin)
    {
        this.plugin = plugin;
    }
    
    public HashMap<String, Integer> diamond = new HashMap<String, Integer>();
    public HashMap<String, Integer> gold = new HashMap<String, Integer>();
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        
        if (player.hasPermission("prospector.use"))
        {
            ItemStack held = player.getItemInHand();
            
            if (held.getType() == Material.DIAMOND_PICKAXE || held.getType() == Material.GOLD_PICKAXE || held.getType() == Material.IRON_PICKAXE || held.getType() == Material.STONE_PICKAXE
                    || held.getType() == Material.WOOD_PICKAXE);
            {
                Block block = event.getBlock();
                
                if (block.getTypeId() == 01 || block.getTypeId() == 15 || block.getTypeId() == 14 || block.getTypeId() == 56
                        || block.getTypeId() == 21|| block.getTypeId() == 73 || block.getTypeId() == 16
                        || block.getTypeId() == 04 || block.getTypeId() == 48 || block.getTypeId() == 74
                        || block.getTypeId() == 87 || block.getTypeId() == 112 || block.getTypeId() == 89
                        || block.getTypeId() == 98 || block.getTypeId() == 24 || block.getTypeId() == 49)
                {
                    
                    World pworld = player.getWorld();

                    BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                    RegionManager regionManager = worldGuard.getRegionManager(pworld);
                    ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                    if (!(worldGuard.canBuild(player, player.getLocation().getBlock())))
                    {
                        return;
                    }
                    
                    int check;
                    double random = Math.random();

                    if (random < 0.2)
                    {
                        Location loc = block.getLocation();
                        int id = block.getTypeId();
                        Material mat = Material.getMaterial(id); 
                       
                        
                        if (id == 14) // if gold ore
                        {
                            if (gold.containsKey(player.getName().toLowerCase()))
                            {
                                check = gold.get(player.getName().toLowerCase());
                                
                                if (check >= 60)
                                {
                                    return;
                                }
                            }
                            else
                            {
                                check = 0;
                            }
                            
                            gold.put(player.getName().toLowerCase(), (check + 2));
                            ItemStack drops = new ItemStack(mat);
                            
                            // player.sendMessage("Your gold ore is: " + gold.get(player.getName().toLowerCase()) + " / 60");
                            
                            block.getWorld().dropItemNaturally(loc, drops);
                            block.getWorld().dropItemNaturally(loc, drops);
                            
                            player.sendMessage(ChatColor.GREEN + "You discover some extra gold ore!");
                            
                            return;
                            
                        }
                        
                        if (id == 56) // if diamond ore
                        {
                            if (diamond.containsKey(player.getName().toLowerCase()))
                            {
                                check = diamond.get(player.getName().toLowerCase());
                                
                                
                                if (check >= 30)
                                {
                                    return;
                                }
                                
                            }
                            else
                            {
                                check = 0;
                            }
                            
                            diamond.put(player.getName().toLowerCase(), (check + 5));
                            
                            mat = Material.getMaterial(264);
                            block.getWorld().dropItemNaturally(loc, new ItemStack (mat));
                            player.sendMessage(ChatColor.GREEN + "You discover some extra diamonds!");
                            
                            // player.sendMessage("Your diamond is: " + diamond.get(player.getName().toLowerCase()) + " / 30");
                            
                            return;
                            
                            
                        }
                        
                        if (id == 16) // if coal ore
                        {
                            mat = Material.getMaterial(263);
                        }
                        
                        if (id == 73 || id == 74) // if redstone
                        {
                            mat = Material.getMaterial(331);
                            block.getWorld().dropItemNaturally(loc, new ItemStack (mat));
                            block.getWorld().dropItemNaturally(loc, new ItemStack (mat));
                            return;
                        }
                        
                        if (id == 01) // if stone
                        {
                            mat = Material.getMaterial(04);
                        }
                        
                        if (id == 49) // if glowstone
                        {
                            mat = Material.getMaterial(348);
                            block.getWorld().dropItemNaturally(loc, new ItemStack (mat));
                            block.getWorld().dropItemNaturally(loc, new ItemStack (mat));
                            return;
                        }
                            
                        
                        ItemStack drops = new ItemStack(mat);
                        
                        
                        
                        
                        
                        block.getWorld().dropItemNaturally(loc, drops);
                        player.sendMessage(ChatColor.GREEN + "You discover some extra material!");
                    }
                    else
                        return;
                }
                
            }
        }

    } // end block break
    
    @EventHandler
    public void onFishingEvent (PlayerFishEvent event)
    {
        Player player = event.getPlayer();
        
        State caught = State.CAUGHT_FISH;
        
        if (event.getState() == caught)
        {
            if (player.hasPermission("prospector.fishing"))
            {
                double random = Math.random();
                
                if (random < 0.4) // 40% chance on successful fish
                {
                    Location ploc = player.getLocation();
                    
                    Material mat = Material.getMaterial(349);
                    double random2 = Math.random();
                    
                    String msg = "";
                    
                    if (random2 > 0 && random2 < 0.05) // diamond chance
                    {
                        mat = Material.getMaterial(264);
                        msg = ChatColor.AQUA + "The fish had a diamond in its mouth... somehow. Who cares? It's yours now!";
                    }
                    
                    if (random2 > 0.05 && random2 < 0.15) // gold chance
                    {
                        mat = Material.getMaterial(41);
                        msg = ChatColor.AQUA + "You hooked a gold block along with the fish!";
                    }
                    
                    if (random2 > 0.15 && random2 < 0.4) // lilypad
                    {
                        mat = Material.getMaterial(111);
                        msg = ChatColor.AQUA + "You caught a fish along with a Lily Pad!";
                    }
                    
                    if (random2 > 0.4 && random2 < 0.50) // diamond sword
                    {
                        mat = Material.getMaterial(276);
                        msg = ChatColor.GOLD + "You failed, but manage to scrounge up an old Diamond Sword!";
                        
                        ItemStack success = new ItemStack(mat);
                     
                        player.getWorld().dropItemNaturally(ploc, success);
                        player.sendMessage(msg);
                        
                        event.setCancelled(true);
                        return;
                    }
                    
                    if (random2 > 0.50 && random2 < 0.65) // (enchanted) iron helm
                    {
                        
                        mat = Material.getMaterial(306);
                        
                        double random3 = Math.random();
                        
                        if (random3 > 0 && random3 < 0.20)  // 20%
                        {
                            ItemStack ench = new ItemStack(mat);
                            ench.addEnchantment(Enchantment.OXYGEN, 1);
                            
                            player.getWorld().dropItemNaturally(ploc, ench);
                            player.sendMessage(ChatColor.GOLD + "You failed to catch a fish, but managed to grab an old enchanted helmet!");
                            event.setCancelled(true);
                            return;
                        }
                        
                        if (random3 > 0.20 && random3 < 0.40)  // 20%
                        {
                            ItemStack ench = new ItemStack(mat);
                            ench.addEnchantment(Enchantment.WATER_WORKER, 1);
                            
                            player.getWorld().dropItemNaturally(ploc, ench);
                            player.sendMessage(ChatColor.GOLD + "You failed to catch a fish, but managed to grab an old enchanted helmet!");
                            event.setCancelled(true);
                            return;
                        }
                        
                        if (random3 > 0.40 && random3 < 0.60)  // 20%
                        {
                            ItemStack ench = new ItemStack(mat);
                            
                            ench.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                            
                            player.getWorld().dropItemNaturally(ploc, ench);
                            player.sendMessage(ChatColor.GOLD + "You failed at fishing, but managed to snag an old enchanted helmet!");
                            event.setCancelled(true);
                            return;
                        }
                        else
                        
                        msg = ChatColor.GOLD + "You failed to catch a fish, but managed to pick up a rusty helmet!";
                    }
                    
                    if (random2 > 0.65 && random2 < 0.70) // disc1
                    {
                        mat = Material.getMaterial(2257);
                        msg = ChatColor.AQUA + "You hooked a catfish! Oh wait, that's Cat and a Fish.";
                    }
                    
                    if (random2 > 0.70 && random2 < 0.75) // disc2
                    {
                        mat = Material.getMaterial(2262);
                        msg = ChatColor.AQUA + "You caught a record and a fish!";
                    }
                    
                    if (random2 > 0.75 && random2 < 0.80) // disc3
                    {
                        mat = Material.getMaterial(2260);
                        msg = ChatColor.GOLD + "You snagged a disc, but forget a fish!";
                        
                        
                        ItemStack success = new ItemStack(mat);                    
                        player.getWorld().dropItemNaturally(ploc, success);
                        player.sendMessage(msg);
                        
                        event.setCancelled(true);
                        return;
                    }
                    
                    if (random2 > 0.80 && random2 < 0.85) // Fishing Rod
                    {
                        mat = Material.getMaterial(346);
                        msg = ChatColor.AQUA + "You hooked a fish still attached to another rod!";
                    }
                    
                    if (random2 > 0.85 && random2 < 0.92) // leather boots (maybe enchanted)
                    {
                        
                        mat = Material.getMaterial(301);
                        
                        double random3 = Math.random();
                        
                        if (random3 > 0 && random3 < 0.15)
                        {
                            ItemStack ench = new ItemStack(mat);
                            
                            ench.addEnchantment(Enchantment.PROTECTION_FALL, 1);
                            
                            player.getWorld().dropItemNaturally(ploc, ench);
                            player.sendMessage(ChatColor.GOLD + "You failed, but managed to pick up a pair of old enchanted boots!");
                            event.setCancelled(true);
                            return;
                        }
                        
                        if (random3 > 0.15 && random3 < 0.30)
                        {
                            ItemStack ench = new ItemStack(mat);
                            
                            ench.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
                            
                            player.getWorld().dropItemNaturally(ploc, ench);
                            player.sendMessage(ChatColor.GOLD + "You failed, but managed to pick up a pair of old enchanted boots!");
                            event.setCancelled(true);
                            return;
                        }
                        
                        if (random3 > 0.3 && random3 < 0.45)
                        {
                            ItemStack ench = new ItemStack(mat);
                            
                            ench.addEnchantment(Enchantment.PROTECTION_FIRE, 3);
                            
                            player.getWorld().dropItemNaturally(ploc, ench);
                            player.sendMessage(ChatColor.GOLD + "You failed, but managed to pick up a pair of old enchanted boots!");
                            event.setCancelled(true);
                            return;
                        }
                        
                        if (random3 > 0.6) // 40%
                        {
                            ItemStack ench = new ItemStack(mat);
                            
                            ench.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                            
                            player.getWorld().dropItemNaturally(ploc, ench);
                            player.sendMessage(ChatColor.GOLD + "You failed, but managed to pick up a pair of old enchanted boots!");
                            event.setCancelled(true);
                            return;
                        }
                        else
                        
                        msg = ChatColor.GOLD + "You failed, but managed to pick up some old leather boots!";
                    }
                    
                    if (random2 > 0.92) // potions
                    {
                        short heal = 8229; // healing potion 2
                        short regen = 8193; // regen 1 (45 duration one)
                        short fire = 8195; // fire 1
                        short strength = 8201; // strength 1
                        
                        
                        short regensplash = 16385; // regen1 splash
                        short healsplash = 16421; // heal splash
                        
                        double random3 = Math.random();
                        
                        if (random3 > 0 && random3 < 0.15) // heal 
                        {
                           ItemStack potion = new ItemStack(Material.POTION, 1, heal);
                        
                           player.getWorld().dropItemNaturally(ploc, potion);
                           msg = ChatColor.GOLD + "You failed to fish, but hooked a potion in the water!";
                           
                           player.sendMessage(msg);
                        
                           event.setCancelled(true);
                           return;
                        }
                        
                        if (random3 > 0.15 && random3 < 0.30) // regen 
                        {
                           ItemStack potion = new ItemStack(Material.POTION, 1, regen);
                        
                           player.getWorld().dropItemNaturally(ploc, potion);
                           msg = ChatColor.GOLD + "You failed to fish, but hooked a potion in the water!";
                           
                           player.sendMessage(msg);
                        
                           event.setCancelled(true);
                           return;
                        }
                        
                        if (random3 > 0.30 && random3 < 0.45) // fire 
                        {
                           ItemStack potion = new ItemStack(Material.POTION, 1, fire);
                        
                           player.getWorld().dropItemNaturally(ploc, potion);
                           msg = ChatColor.GOLD + "You failed to fish, but hooked a potion in the water!";
                           
                           player.sendMessage(msg);
                        
                           event.setCancelled(true);
                           return;
                        }
                        
                        if (random3 > 0.45 && random3 < 0.60) // strength 
                        {
                           ItemStack potion = new ItemStack(Material.POTION, 1, strength);
                        
                           player.getWorld().dropItemNaturally(ploc, potion);
                           msg = ChatColor.GOLD + "You failed to fish, but hooked a potion in the water!";
                           
                           player.sendMessage(msg);
                        
                           event.setCancelled(true);
                           return;
                        }
                        
                        if (random3 > 0.60 && random3 < 0.75) // regensplash 
                        {
                           ItemStack potion = new ItemStack(Material.POTION, 1, regensplash);
                        
                           player.getWorld().dropItemNaturally(ploc, potion);
                           msg = ChatColor.GOLD + "You failed to fish, but hooked a potion in the water!";
                           
                           player.sendMessage(msg);
                        
                           event.setCancelled(true);
                           return;
                        }
                        
                        if (random3 > 0.75 && random3 < 0.90) // healsplash 
                        {
                           ItemStack potion = new ItemStack(Material.POTION, 1, healsplash);
                        
                           player.getWorld().dropItemNaturally(ploc, potion);
                           msg = ChatColor.GOLD + "You failed to fish, but hooked a potion in the water!";
                           
                           player.sendMessage(msg);
                        
                           event.setCancelled(true);
                           return;
                        }
                        
                        mat = Material.getMaterial(374);
                        msg = ChatColor.RED + "You failed to fish, and the potion you grabbed leaked! Oh no!";
                        
                    }
                    
                    
                    
                    ItemStack success = new ItemStack(mat);
                    
                    Material mat2 = Material.RAW_FISH;
                    
                    ItemStack extrafish = new ItemStack(mat2, 2);
                    
                    player.getWorld().dropItemNaturally(ploc, success);
                    player.getWorld().dropItemNaturally(ploc, extrafish);
                    player.sendMessage(msg);
                }
            } // end perm
            
        } // end caught
        
    }
}
