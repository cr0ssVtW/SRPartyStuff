package sr.party;


import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Acidsin
 */

public class Nightlord implements Listener
{
    
    public Party plugin;

    public Nightlord (Party plugin)
    {
        this.plugin = plugin;
    }
    

    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();

    private int tickSpeed = 1;
    int blockType = 119;
    int blockData = 1;
    int range = 7;
    
    public HashMap<String, Integer> diseasedearth = new HashMap<String, Integer>();
    public HashMap<String, Integer> batswarm = new HashMap<String, Integer>();
    public HashMap<String, Integer> cripplingdisease = new HashMap<String, Integer>();
    public HashMap<String, Integer> voidzone = new HashMap<String, Integer>();
    public HashMap<String, Integer> night = new HashMap<String, Integer>();
    public HashMap<String, Long> timer = new HashMap<String, Long>();
    public HashSet<String> batswarmaffected = new HashSet<String>();
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        if (PL.stuntarget.containsKey(player.getName().toLowerCase()) || Paladin.isStunned.contains(player.getName()) || ChaosCrusader.chaosstunned.containsKey(player.getName()))
        {
              event.setCancelled(true);
              player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
              return;
        }
        
        Material iteminhand = player.getItemInHand().getType();
        int ability = 0; //No ability
            
        World w = player.getWorld();
        World pvp = Bukkit.getWorld("pvp");
        World world = Bukkit.getWorld("world");

        boolean rightarea = true;
        boolean rightitem = false;
            
        if (iteminhand == Material.DIAMOND_HOE || iteminhand == Material.IRON_HOE || iteminhand == Material.GOLD_HOE)
        {
            rightitem = true;
        }
        
        if (player.hasPermission("nightlord.cast") && rightitem == true)
        {
             World pworld = player.getWorld();

            BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

            RegionManager regionManager = worldGuard.getRegionManager(pworld);
            ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

            if (!(set.allows(DefaultFlag.PVP)))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Action cancelled. PvP is disabled in this area.");
                return;
            }
        }


        /*
        if (w.equals(pvp) && player.hasPermission("nightlord.cast") && rightitem == true)
        {
            Location playerloc = player.getLocation();
            int y = playerloc.getBlockY();

            if (y < 57)
            {
                player.sendMessage(ChatColor.AQUA + "You can not use Night Lord abilities here.");
                rightarea = false;
            }
        }

        if (w.equals(world) && player.hasPermission("nightlord.cast") && rightitem == true)
        {
            Location playerloc = player.getLocation();
            int x = playerloc.getBlockX();
            int z = playerloc.getBlockZ();

            if (x > -150 && x < 125 && z < 411 && z > 42)
            {
                player.sendMessage(ChatColor.AQUA + "You can not use Night Lord abilities here.");
                rightarea = false;
            }
        }
        */
            
        if (iteminhand == Material.GOLD_HOE && rightarea == true)
        {
           if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR))
           {
            if (player.hasPermission("nightlord.cast"))
            {
                
                boolean offcd = true;
                if (diseasedearth.containsKey(player.getName()))
                {
                    int currenttime = this.getCurrentTime();
                    int oldtime = diseasedearth.get(player.getName());
                    int time = currenttime - oldtime;
                    int finaltime = 60 - time;

                    if (time > 60 || time < 0)
                    {
                        offcd = true;
                        diseasedearth.remove(player);
                    }
                    else
                    {
                     player.sendMessage(ChatColor.DARK_BLUE + "Diseased Earth" + ChatColor.AQUA + " is on cooldown for: " + finaltime + " seconds.");
                     offcd = false;
                    }
                }
                if (offcd == true)
                {
                    if (player.hasPermission("nightlord.cast"))
                    {
                         World pworld = player.getWorld();

                        BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Action cancelled. PvP is disabled in this area.");
                            return;
                        }
                    }
                    
                  int currenttime = this.getCurrentTime();
                  diseasedearth.put(player.getName(), currenttime);
                  player.sendMessage(ChatColor.AQUA + "You cast " + ChatColor.DARK_BLUE + "Diseased Earth" + ChatColor.AQUA + "!");

                  new DiseasedEarthAnimation(player);
                }
             }
           }
               
               
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR))
            {
             if (player.hasPermission("nightlord.cast"))
             { 
                 boolean offcd = true;
                 if (batswarm.containsKey(player.getName()))
                 {
                     int currenttime = this.getCurrentTime();
                     int oldtime = batswarm.get(player.getName());
                     int time = currenttime - oldtime;
                     int finaltime = 25 - time;

                     if (time > 25 || time < 0)
                     {
                         offcd = true;
                         batswarm.remove(player);
                     }
                     else
                     {
                      player.sendMessage(ChatColor.DARK_BLUE + "Bat Swarm" + ChatColor.AQUA + " is on cooldown for: " + finaltime + " seconds.");
                      offcd = false;
                     }
                 }
                 if (offcd == true)
                 {
                     if (player.hasPermission("nightlord.cast"))
                    {
                         World pworld = player.getWorld();

                        BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Action cancelled. PvP is disabled in this area.");
                            return;
                        }
                    }
                     
                   int currenttime = this.getCurrentTime();
                   batswarm.put(player.getName(), currenttime);
                   player.sendMessage(ChatColor.AQUA + "You cast " + ChatColor.DARK_BLUE + "Bat Swarm" + ChatColor.AQUA + "!");

                   Location location = player.getLocation().toVector().add(player.getLocation().getDirection().multiply(3)).toLocation(player.getWorld());
                   int y = location.getBlockY();
                   location.setY(y + 2);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);
                   w.spawnEntity(location, EntityType.BAT);


                    for (Player chatplayer : plugin.getServer().getOnlinePlayers())
                    {



                      Location chatloc = chatplayer.getLocation();
                      if (chatplayer.getWorld().equals(w))
                      {

                        if (location.distance(chatloc) < 10.0D && player != chatplayer)
                        {
                           double damage = 2;
                           long time = player.getWorld().getTime();
                           if (time < 23500 && time > 12500)
                                {
                                   damage = 3;
                                }
                           chatplayer.damage(damage);
                           double newdamage = damage/2;
                           batswarmaffected.add(chatplayer.getName());
                           chatplayer.sendMessage(ChatColor.DARK_BLUE + "Bat Swarm " + ChatColor.AQUA + "hits you for " + newdamage + " hearts of damage and causes you to miss attacks!");

                        }

                      }

                    }


                   new BatRemove(player);
                 }
              }
            }


         }
            
        if (iteminhand == Material.DIAMOND_HOE && rightarea == true)
        {
            if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR))
           {
               if (player.hasPermission("nightlord.cast"))
                {
                boolean offcd = true;
                if (cripplingdisease.containsKey(player.getName()))
                {
                    int currenttime = this.getCurrentTime();
                    int oldtime = cripplingdisease.get(player.getName());
                    int time = currenttime - oldtime;
                    int finaltime = 30 - time;

                    if (time > 30 || time < 0)
                    {
                        offcd = true;
                        cripplingdisease.remove(player);
                    }
                    else
                    {
                     player.sendMessage(ChatColor.DARK_BLUE + "Crippling Disease" + ChatColor.AQUA + " is on cooldown for: " + finaltime + " seconds.");
                     offcd = false;
                    }
                }
                if (offcd == true)
                {
                    if (player.hasPermission("nightlord.cast"))
                    {
                         World pworld = player.getWorld();

                        BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Action cancelled. PvP is disabled in this area.");
                            return;
                        }
                    }
                    
                  int currenttime = this.getCurrentTime();
                  cripplingdisease.put(player.getName(), currenttime);
                  player.sendMessage(ChatColor.AQUA + "You cast " + ChatColor.DARK_BLUE + "Crippling Disease" + ChatColor.AQUA + "!");

                  Location location = player.getLocation().toVector().add(player.getLocation().getDirection().multiply(3)).toLocation(player.getWorld());
                  int y = location.getBlockY();
                  int x = location.getBlockX();
                  int z = location.getBlockZ();
                  location.setY(y + 2);

                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setX(x + 3);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setX(x - 3);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setX(x + 2);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setX(x - 2);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setX(x + 1);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setX(x - 1);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setZ(z + 3);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setZ(z - 3);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setZ(z + 2);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setZ(z - 2);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setZ(z + 1);
                  location.getWorld().createExplosion(location, 0.0f, false);
                  location.setZ(z - 1);
                  location.getWorld().createExplosion(location, 0.0f, false);


              for (Player chatplayer : plugin.getServer().getOnlinePlayers())
              {

                Location chatloc = chatplayer.getLocation();
                    if (chatplayer.getWorld().equals(w))
                    {

                      if (location.distance(chatloc) < 7.0D && player != chatplayer)
                      {
                         double damage = 3;
                         long time = player.getWorld().getTime();

                         chatplayer.damage(damage);
                         double newdamage = damage/2;

                         if (time < 23500 && time > 12500)
                              {
                                 chatplayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 2));
                              }
                         else
                              {
                                 chatplayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                              }
                         chatplayer.sendMessage(ChatColor.DARK_BLUE + "Crippling Disease " + ChatColor.AQUA + "hits you for " + newdamage + " hearts of damage and slows you!");

                      }

                    }

                  }

                }

                }
           }
                
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR))
           {
               if (player.hasPermission("nightlord.cast"))
                {
                boolean offcd = true;
                if (voidzone.containsKey(player.getName()))
                {
                    int currenttime = this.getCurrentTime();
                    int oldtime = voidzone.get(player.getName());
                    int time = currenttime - oldtime;
                    int finaltime = 40 - time;

                    if (time > 40 || time < 0)
                    {
                        offcd = true;
                        voidzone.remove(player);
                    }
                    else
                    {
                     player.sendMessage(ChatColor.DARK_BLUE + "Void Zone" + ChatColor.AQUA + " is on cooldown for: " + finaltime + " seconds.");
                     offcd = false;
                    }
                }
                if (offcd == true)
                {

                    if (player.hasPermission("nightlord.cast"))
                    {
                         World pworld = player.getWorld();

                        BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Action cancelled. PvP is disabled in this area.");
                            return;
                        }
                    }
                    
                  int currenttime = this.getCurrentTime();
                  voidzone.put(player.getName(), currenttime);
                  player.sendMessage(ChatColor.AQUA + "You cast " + ChatColor.DARK_BLUE + "Void Zone");
                  new VoidRemove(player);


                }

                }
           }


        }
        if (iteminhand == Material.IRON_HOE && rightarea == true)
        {
            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR))
           {
               if (player.hasPermission("nightlord.cast"))
                {
                boolean offcd = true;
                if (night.containsKey(player.getName()))
                {
                    int currenttime = this.getCurrentTime();
                    int oldtime = night.get(player.getName());
                    int time = currenttime - oldtime;
                    int finaltime = 600 - time;

                    if (time > 600 || time < 0)
                    {
                        offcd = true;
                        night.remove(player);
                    }
                    else
                    {
                     player.sendMessage(ChatColor.DARK_BLUE + "Night" + ChatColor.AQUA + " is on cooldown for: " + finaltime + " seconds.");
                     offcd = false;
                    }
                }
                if (offcd == true)
                {
                    if (player.hasPermission("nightlord.cast"))
                    {
                         World pworld = player.getWorld();

                        BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Action cancelled. PvP is disabled in this area.");
                            return;
                        }
                    }
                    
                  long time = player.getWorld().getTime();

                  if (time < 23500 && time > 12500)
                          {
                          player.sendMessage(ChatColor.AQUA + "It's already night!");
                            return;
                          }
                  int currenttime = this.getCurrentTime();
                  night.put(player.getName(), currenttime);
                  player.sendMessage(ChatColor.AQUA + "You cast " + ChatColor.DARK_BLUE + "Night" + ChatColor.AQUA + " and darkness falls over the land!");

                       for (Player chatplayer : plugin.getServer().getOnlinePlayers())
                        {

                           if (chatplayer.getWorld().equals(w))
                            {

                               chatplayer.sendMessage(ChatColor.DARK_BLUE + "Darkness " + ChatColor.AQUA + "falls across the land...");
                            }

                        }
                  timer.put(player.getName(), time);
                  new NightRemove(player);
                }

                }
           }


        }

        
    } // ennd player interact
    
    
        

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        if ((event.getEntity() instanceof Player)) 
        {
            Player player = (Player) event.getEntity();
            if(event instanceof EntityDamageByEntityEvent)
            {
             
               EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;

               if(event_EE.getDamager() instanceof Player)
               {
                    Player attacker = ((Player)event_EE.getDamager());
                       
                    if (PL.stuntarget.containsKey(attacker.getName().toLowerCase()) || Paladin.isStunned.contains(attacker.getName()) || ChaosCrusader.chaosstunned.containsKey(attacker.getName()))
                    {
                          event.setCancelled(true);
                          attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                          return;
                    }
                    
                    PlayerInventory inv = ((Player)event.getEntity()).getInventory();
                    ItemStack helm = inv.getHelmet();

                    if (player.getInventory().getHelmet() != null && player.hasPermission("nightlord.cast"))
                     {
                         if (helm.getTypeId() == 302)
                             {
                               event.setDamage((int)Math.round(event.getDamage() * 0.72D));

                                if ((helm.getDurability() < 10) && (helm.getDurability() > -1))
                                   {
                                     short ghelm = -100;
                                     helm.setDurability(ghelm);
                                   }

                               if ((helm.getDurability() < 0) && (helm.getDurability() > -20))
                                   {
                                    short ghelm = 11;
                                    helm.setDurability(ghelm);

                                   }

                             }
                       }

                    if (batswarmaffected.contains(attacker.getName()))
                    {
                        double random = Math.random();

                        long time = player.getWorld().getTime();
                        if (time < 23500 && time > 12500)
                        {
                             if (random < .5)
                             {
                                 event.setDamage(0);
                                 player.sendMessage(ChatColor.AQUA + attacker.getName() + " is affected by " + ChatColor.DARK_BLUE + "Bat Swarm " + ChatColor.AQUA + "causing them to miss!");
                                 attacker.sendMessage(ChatColor.AQUA + "You are affected by " + ChatColor.DARK_BLUE + "Bat Swarm " + ChatColor.AQUA + "and miss!");
                                 return;
                             }
                        }

                        else if (random < .25)
                        {
                          event.setDamage(0);
                          player.sendMessage(ChatColor.AQUA + attacker.getName() + " is affected by " + ChatColor.DARK_BLUE + "Bat Swarm " + ChatColor.AQUA + "causing them to miss!");
                          attacker.sendMessage(ChatColor.AQUA + "You are affected by " + ChatColor.DARK_BLUE + "Bat Swarm " + ChatColor.AQUA + "and miss!");    
                          return;
                        }
                    }
                 
               }
            
            }

       
        }
    
  
    } // end entity damage

    public static HashMap<Player, ItemStack> oldhelm = new HashMap<Player, ItemStack>();
    public static HashSet<Player> helmplayer = new HashSet<Player>();
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e)
    {
        if(e.getEntity() instanceof Player)
        {
            Player player = (Player) e.getEntity();
            for(ItemStack isDrop : e.getDrops())
            {

                if (player.hasPermission("nightlord.cast") && oldhelm.containsKey(player))
                {

                  
                   if(isDrop.getTypeId() == 302)
                   {
                       e.getDrops().remove(isDrop);
                       ItemStack helm = oldhelm.get(player);
                       oldhelm.remove(player);
                       helmplayer.remove(player);
                       e.getDrops().add(helm);
                   }
                
                }
            }
        }
    } // entity death

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        
        if (player.getInventory().getHelmet() != null)
        {

        if (oldhelm.containsKey(player))
        {
            ItemStack helm2 = oldhelm.get(player);
            oldhelm.remove(player);
            helmplayer.remove(player);
            player.getInventory().setHelmet(helm2);
        }
        
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        
        if (player.getInventory().getHelmet() != null)
        {
            

        if (oldhelm.containsKey(player))
        {
            ItemStack helm2 = oldhelm.get(player);
            oldhelm.remove(player);
            helmplayer.remove(player);
            player.getInventory().setHelmet(helm2);
        }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInvClick(InventoryClickEvent event)
    {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        int limit = 0;
        
        if (player.hasPermission("nightlord.cast") && oldhelm.containsKey(player))
        {
            
            if (player.getFireTicks() > 0)
            {
                event.setCancelled(true);
            }
            if (slot == 5)
            {

                ItemStack helm = new ItemStack(Material.CHAINMAIL_HELMET, 1);
                if (event.getCurrentItem().equals(helm))
                {

                    ItemStack helm2 = oldhelm.get(player);
                    oldhelm.remove(player);
                    helmplayer.remove(player);
                    event.setCurrentItem(helm2);
                }
            }
        }
    } // end inv click
    
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {

        Player player = event.getPlayer();

        if (player.hasPermission("nightlord.cast"))
        {
            long time = player.getWorld().getTime();
                        
            if (time < 23500 && time > 12500)
            {
                if (!(player.hasPotionEffect(PotionEffectType.SPEED)))
                {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18000, 0));
                }
                if (!(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)))
                {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 18000, 0));
                }
                if (!(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)))
                {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 18000, 1), true);
                }
            }
            
            
        }
    
        if (oldhelm.containsKey(player))   
        {
            return;
        }
        if (player.getInventory().getHelmet() != null && player.hasPermission("nightlord.cast"))
        {
            if (player.getInventory().getHelmet().getType().equals(Material.GOLD_HELMET) ||player.getInventory().getHelmet().getType().equals(Material.IRON_HELMET) || player.getInventory().getHelmet().getType().equals(Material.DIAMOND_HELMET) || player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
            {
            ItemStack helm = new ItemStack(Material.CHAINMAIL_HELMET, 1);
            ItemStack currenthelm = player.getInventory().getHelmet();
            oldhelm.put(player, currenthelm);
            helmplayer.add(player);
            player.getInventory().setHelmet(helm);
            }
        }
    } // end move

          
    private int getCurrentTime()
    {
        Calendar calendar = new GregorianCalendar();

        int hour = calendar.get(Calendar.HOUR) * 3600;
        int minute = calendar.get(Calendar.MINUTE) * 60;
        int second = calendar.get(Calendar.SECOND);

        return hour + minute + second;
    }
  
  
    private class BatRemove implements Runnable 
    {
        Player player;
        int i;
        int taskId;
      
        public BatRemove(Player player) 
        {
            this.player = player;
            i = 0;

            taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 20L, 20L);
        }
      
      
        public void run() 
        {
            i = i + 1;
            if (i == 6 || i > 6)
            {
                World world = player.getWorld();
                
                for(Entity e : world.getEntities())
                {
                    if ((e instanceof Bat))
                    {
                        e.remove();

                    }
                }
                
                HashSet<String> hashcopy = new HashSet<String>(batswarmaffected);
                Iterator<String> iterator = hashcopy.iterator();
                
                //Iterator<String> iterator = batswarmaffected.iterator();
                while (iterator.hasNext())
                {
                    String s = iterator.next();
                    
                    OfflinePlayer p = Bukkit.getOfflinePlayer(s);
                    Player player = null;
                    if (p.isOnline())
                    {
                        player = (Player) p;
                        if (batswarmaffected.contains(player.getName()))
                        {
                           batswarmaffected.remove(player.getName());
                           player.sendMessage(ChatColor.AQUA + "You are no longer affected by Bat Swarm.");
                        }
                    } 
                }
                
                Bukkit.getServer().getScheduler().cancelTask(taskId);
            }
        }
  
    }

    private class NightRemove implements Runnable 
    {
        Player player;
        int i;
        int taskId;
      
        public NightRemove(Player player) 
        {
            this.player = player;
            World world = player.getWorld();
            i = 0;
            long night = 14000;
            world.setTime(night);
            taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 20L, 20L);
        }
        
        public void run()
        {
            i = i + 1;

            if (i == 30 || i > 30)
            {
                World world = player.getWorld();
                long time = 0;
                if (timer.containsKey(player.getName()))
                {
                    time = timer.get(player.getName());
                }
                timer.remove(player.getName());
                world.setTime(time);
                Bukkit.getServer().getScheduler().cancelTask(taskId);
            }
        }
    }
  
    
    
    
        
    private class VoidRemove implements Runnable 
    {
        Player player;
        int i;
        int taskId;
        HashSet<Block> VoidBlocks;
        
        public VoidRemove(Player player) 
        {
            this.player = player;
            World world = player.getWorld();
            VoidBlocks = new HashSet<Block>();
            i = 0;
                        
            Location location = player.getLocation().toVector().add(player.getLocation().getDirection().multiply(3)).toLocation(player.getWorld());
            Location playerloc = player.getLocation();
                      
            playerloc.setX(playerloc.getBlockX() + 3);
                      
            int check = 1;
            double distance = 0;
            double tempdistance;
                      
            distance = location.distance(playerloc);

            playerloc = player.getLocation();
            playerloc.setX(playerloc.getBlockX() - 3);
            tempdistance = location.distance(playerloc);

            if (tempdistance < distance)
            {
                check = 2;
                distance = tempdistance;
            }

            playerloc = player.getLocation();
            playerloc.setZ(playerloc.getBlockZ() + 3);
            tempdistance = location.distance(playerloc);

            if (tempdistance < distance)
            {
                check = 3;
                distance = tempdistance;
            }

            playerloc = player.getLocation();
            playerloc.setZ(playerloc.getBlockZ() - 3);
            tempdistance = location.distance(playerloc);

            if (tempdistance < distance)
            {
                check = 4;
                distance = tempdistance;
            }

            Location originalloc = player.getLocation();
            playerloc = player.getLocation();
            Block b = playerloc.getBlock();

            if (check == 1)
            {


                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setX(originalloc.getBlockX() + 2);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 3);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 4);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 5);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 6);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 7);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 8);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 1);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setX(originalloc.getBlockX() + 2);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 3);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 4);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 5);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 6);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 7);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 8);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 1);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setX(originalloc.getBlockX() + 2);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 3);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 4);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 5);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 6);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 7);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() + 8);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }
            }

            if (check == 2)
            {

                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setX(originalloc.getBlockX() - 2);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 3);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 4);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 5);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 6);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 7);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 8);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 1);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setX(originalloc.getBlockX() - 2);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 3);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 4);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 5);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 6);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 7);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 8);
                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 1);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setX(originalloc.getBlockX() - 2);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 3);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 4);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 5);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 6);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 7);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setX(originalloc.getBlockX() - 8);
                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }
            }

            if (check == 3)
            {


                playerloc.setZ(originalloc.getBlockZ() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setZ(originalloc.getBlockZ() + 2);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 3);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 4);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 5);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 6);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 7);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 8);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 1);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setZ(originalloc.getBlockZ() + 2);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 3);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 4);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 5);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 6);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 7);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 8);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 1);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setZ(originalloc.getBlockZ() + 2);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 3);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 4);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 5);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 6);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 7);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() + 8);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }
            }

            if (check == 4)
            {


                playerloc.setZ(originalloc.getBlockZ() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setZ(originalloc.getBlockZ() - 2);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 3);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 4);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 5);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 6);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 7);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 8);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 1);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setZ(originalloc.getBlockZ() - 2);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 3);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 4);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 5);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 6);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 7);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 8);
                playerloc.setX(originalloc.getBlockX() + 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 1);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }


                playerloc.setZ(originalloc.getBlockZ() - 2);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 3);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 4);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 5);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 6);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 7);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }

                playerloc.setZ(originalloc.getBlockZ() - 8);
                playerloc.setX(originalloc.getBlockX() - 1);
                b = playerloc.getBlock();
                if (b.getType().equals(Material.AIR))
                {
                b.setType(Material.ENDER_PORTAL);
                VoidBlocks.add(b);
                }
            }


            World w = player.getWorld();
            for (Player chatplayer : plugin.getServer().getOnlinePlayers())
                  {

                     if (chatplayer.getWorld().equals(w))
                      {

                          Iterator<Block> iterator = VoidBlocks.iterator();
                          Block v;
                          Location vzone;
                          while (iterator.hasNext())
                            { 

                                v = iterator.next();
                                vzone = v.getLocation();
                                int cx = chatplayer.getLocation().getBlockX();
                                int cy = chatplayer.getLocation().getBlockY();
                                int cz = chatplayer.getLocation().getBlockZ();
                                int vx = vzone.getBlockX();
                                int vy = vzone.getBlockY();
                                int vz = vzone.getBlockZ();
                                if (cx == vx && cy == vy && cz == vz)
                                {
                                    chatplayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 3));
                                }
                            }

                       }

                  }


              taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 20L, 20L);
      }

      public void run() 
      {
          
            i = i + 1;

            if (i < 8)
            {
                World w = player.getWorld();

                long time = w.getTime();
              double damage = 2;
              
              if (time < 23500 && time > 12500)
              {
                damage = 4;
              }
              
              for (Player chatplayer : plugin.getServer().getOnlinePlayers())
              {

                 if (chatplayer.getWorld().equals(w))
                 {

                    Iterator<Block> iterator = VoidBlocks.iterator();
                    Block v;
                    Location vzone;
                    while (iterator.hasNext())
                      { 
                          v = iterator.next();
                          vzone = v.getLocation();

                          int cx = chatplayer.getLocation().getBlockX();
                          int cy = chatplayer.getLocation().getBlockY();
                          int cz = chatplayer.getLocation().getBlockZ();
                          int vx = vzone.getBlockX();
                          int vy = vzone.getBlockY();
                          int vz = vzone.getBlockZ();
                          if (cx == vx && cy == vy && cz == vz)
                          {
                              chatplayer.damage(damage);
                          }

                      }


                 }

              }
          }
            
          if (i == 8 || i > 8)
          {
              Iterator<Block> iterator = VoidBlocks.iterator();
              Block v;
                      while (iterator.hasNext())
                      {
                          v = iterator.next();
                          v.setType(Material.AIR);
                      }
              Bukkit.getServer().getScheduler().cancelTask(taskId);
          }
      }
  }
  
  
  
  
  private class DiseasedEarthAnimation implements Runnable 
  {
    Player player;
    int i;
    Block center;
    HashSet<Block> fireBlocks;
    int taskId;
    
    public DiseasedEarthAnimation(Player player) 
    {
        this.player = player;

        i = 0;
        center = player.getLocation().getBlock();
        fireBlocks = new HashSet<Block>();

        taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 3L, 3L);
    }

                
    public void run() 
    {
        // remove old fire blocks
        for (Block block : fireBlocks) {
                if (block.getTypeId() == blockType) {
                        block.setTypeIdAndData(0, (byte)0, false);
                }
        }
        fireBlocks.clear();

        i += 1;
        if (i <= range) {
                // set next ring on fire
                int bx = center.getX();
                int y = center.getY();
                int bz = center.getZ();
                for (int x = bx - i; x <= bx + i; x++) {
                        for (int z = bz - i; z <= bz + i; z++) {
                                if (Math.abs(x-bx) == i || Math.abs(z-bz) == i) {
                                        Block b = center.getWorld().getBlockAt(x,y,z);
                                        if (b.getType() == Material.AIR) {
                                                Block under = b.getRelative(BlockFace.DOWN);
                                                if (under.getType() == Material.AIR) {
                                                        b = under;
                                                }
                                                b.setTypeId(blockType, true);
                                                fireBlocks.add(b);
                                        } else if (b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                                                b = b.getRelative(BlockFace.UP);
                                                b.setTypeId(blockType, true);
                                                fireBlocks.add(b);
                                        }
                                }
                        }
                }
        } 
        else if (i > range+1) 
        {
                // stop if done
                Bukkit.getServer().getScheduler().cancelTask(taskId);


                Location playerloc = player.getLocation();

                World playerworld = player.getWorld();
                long time = playerworld.getTime();
                double damage = 2;
                if (time < 23500 && time > 12500)
                {
                    damage = 3;
                }
                for (Player chatplayer : plugin.getServer().getOnlinePlayers())
                {
                    Location chatloc = chatplayer.getLocation();
                    if (chatplayer.getWorld().equals(playerworld))
                    {

                        if (playerloc.distance(chatloc) < 16.0D && player != chatplayer)
                        {
                           damage = damage + 3;
                           //Bukkit.broadcastMessage("Player is: " + chatplayer.getName() + " damage is: " + damage);
                        }
                    }
                }

                World w = player.getWorld();
                World pvp = Bukkit.getWorld("pvp");
                World world = Bukkit.getWorld("world");

                /*
                if (w.equals(world) && player.hasPermission("nightlord.cast"))
                {
                    Location playerloc = player.getLocation();
                    int x = playerloc.getBlockX();
                    int z = playerloc.getBlockZ();

                    if (x > -150 && x < 125 && z < 411 && z > 42)
                    {
                        damage = 0;
                    }
                }
                */

                for (Player chatplayer : plugin.getServer().getOnlinePlayers())
                {
                    Location chatloc = chatplayer.getLocation();
                    if (chatplayer.getWorld().equals(player.getWorld()))
                    {
                        if (chatloc.distance(playerloc) < 16.0D && player != chatplayer)
                        {
                           chatplayer.damage(damage);
                           double newdamage = damage/2;
                           
                           if (player.getHealth() + 2 > player.getMaxHealth())
                           {
                               player.setHealth(player.getMaxHealth());
                           }
                           else
                           {
                               player.setHealth(player.getHealth() + 2);
                           }
                           //Bukkit.broadcastMessage("Player is: " + chatplayer.getName() + " damage is now: " + damage);
                           chatplayer.sendMessage(ChatColor.DARK_BLUE + "Diseased Earth " + ChatColor.AQUA + "hits you for " + newdamage + " hearts of damage!");
                        }
                    }
                }
        } // end else
                
    } // end run
  }
  
  
}

   