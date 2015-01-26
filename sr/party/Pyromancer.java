package sr.party;

import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * @author Cross
 */
public class Pyromancer implements Listener
{
    public Party plugin;
    
    
    public Pyromancer (Party plugin)
    {
        this.plugin = plugin;
    }
   
    
   /*
    * 
    * PHOENIX STUFF
    * 
    */
    
    
    public static HashMap<String, Integer> PhoenixTimer = new HashMap<String, Integer>();
    
   
    public int PhoenixCD = 150;
    
    public int NovaCount = 5;
    
    public int PhoenixFireTicks = 80;
    
    public int PhoenixTicks = 60;
    
    public int PhoenixTicks2 = 2;
    
    public int PhoenixDur = 6;
    
    public int PhoenixRegen = 5;
    
    public int PhoenixDist = 9;
    
    public int PhoenixDamage = 5;
    
    
    public int PhoenixReborn = 600;
    public int PhoenixRebornTickDelay = 900; // 60 seconds.
    
    public static HashMap<String, Integer> PhoenixRebornTimer = new HashMap<String, Integer>();
    public static HashSet<String> isPhoenix = new HashSet<String>();
    /*
    * 
    */
    
    public int smallFireCD = 4;
    public int bigFireCD = 7;
    public int barrageCD = 15;
    
    
   public static HashMap<Player, Integer> fireball2 = new HashMap();
   public static HashMap<Player, Integer> smallfireball = new HashMap();
   public static HashMap<Player, Integer> fireballbarrage = new HashMap();
   public static HashMap<Player, Integer> smallfireballbarrage = new HashMap();
   
   public static HashMap<String, Integer> fireballEntID = new HashMap<String, Integer>();
   
  
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        PlayerInventory inv = event.getPlayer().getInventory();
        ItemStack weap = inv.getItemInHand();

        int currenttime = plugin.getCurrentTime();
        Player player = event.getPlayer();
        boolean fb = false;
        boolean sfb = false;
        boolean fbbarrage = false;
        boolean sfbbarrage = false;
        boolean blink = false;
        if (Party.ghost.contains(player.getName()))
        {
          event.setCancelled(true);
          player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can not do that while in ghost form");
          return;
        }
        if (PL.stuntarget.containsKey(player.getName().toLowerCase()) || Paladin.isStunned.contains(player.getName()) || ChaosCrusader.chaosstunned.containsKey(player.getName()))
        {
              event.setCancelled(true);
              player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                      return;
        }
   
        
        if (event.getPlayer().hasPermission("pyromancer.fire"))
        {            
            if (weap.getTypeId() == 369)
            {
                if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR))
                {
                    if (fireballbarrage.containsKey(player))
                    {
                        int fbbarragetimer = ((Integer)fireballbarrage.get(player)).intValue();
                        int totaltime = currenttime - fbbarragetimer;
                        if ((totaltime > barrageCD) || (totaltime < 0))
                        {
                            fbbarrage = false;
                        }
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can shoot Fireball Barrage in " + (barrageCD - totaltime) + " seconds.");
                            fbbarrage = true;
                        }
                    }

          
                    if (!fbbarrage)
          
                    {
                         fireballbarrage.put(player, Integer.valueOf(currenttime));
                         Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(14)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                         Location loc2 = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(8)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                         Location loc3 = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(12)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                         Location loc4 = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(10)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                         Location loc5 = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(4)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                         

                         Fireball fireball2 = (Fireball)player.getWorld().spawn(loc2, Fireball.class);
                         Vector v2 = loc2.getDirection().normalize();
                         fireball2.setVelocity(v2.multiply(v2.length() * 2));
                         
                         Fireball fireball3 = (Fireball)player.getWorld().spawn(loc3, Fireball.class);
                         Vector v3 = loc3.getDirection().normalize();
                         fireball2.setVelocity(v3.multiply(v3.length() * 2));
                        
                         Fireball fireball4 = (Fireball)player.getWorld().spawn(loc4, Fireball.class);
                         Vector v4 = loc4.getDirection().normalize();
                         fireball2.setVelocity(v4.multiply(v4.length() * 2));
                        
                         Fireball fireball5 = (Fireball)player.getWorld().spawn(loc5, Fireball.class);
                         Vector v5 = loc5.getDirection().normalize();
                         fireball2.setVelocity(v5.multiply(v5.length() * 2));
                         
                         fireball2.setShooter(player);
                         fireball3.setShooter(player);
                         fireball4.setShooter(player);
                         fireball5.setShooter(player);
                         
                         player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "FIREBALL BARRAGE");
                    }
                }

        
                if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR))
                {
                    if (smallfireballbarrage.containsKey(player))
                    {
                        int sfbbarragetimer = ((Integer)smallfireballbarrage.get(player)).intValue();
                        int totaltime = currenttime - sfbbarragetimer;
                        if ((totaltime > barrageCD) || (totaltime < 0))
                        {
                            sfbbarrage = false;
                        }
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can shoot Small Fireball Barrage in " + (barrageCD - totaltime) + " seconds.");
                            sfbbarrage = true;
                        }

                    }
                    
                    if (!sfbbarrage)
                    {
                        smallfireballbarrage.put(player, Integer.valueOf(currenttime));

                        //Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(14)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                        Location loc2 = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(8)).toLocation(player.getWorld(), player.getLocation().getYaw() + 4.0F, player.getLocation().getPitch() + 4.0F);
                        Location loc3 = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(12)).toLocation(player.getWorld(), player.getLocation().getYaw() + 8.0F, player.getLocation().getPitch() + 8.0F);
                        Location loc4 = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(10)).toLocation(player.getWorld(), player.getLocation().getYaw() - 4.0F, player.getLocation().getPitch() - 4.0F);

                        SmallFireball fireball2 = (SmallFireball)player.getWorld().spawn(loc2, SmallFireball.class);
                        Vector v = loc2.getDirection().normalize();
                        fireball2.setVelocity(v.multiply(v.length() * 2));
                        
                        SmallFireball fireball3 = (SmallFireball)player.getWorld().spawn(loc3, SmallFireball.class);
                        Vector v2 = loc3.getDirection().normalize();
                        fireball2.setVelocity(v2.multiply(v2.length() * 2));
                        
                        SmallFireball fireball4 = (SmallFireball)player.getWorld().spawn(loc4, SmallFireball.class);
                        Vector v3 = loc4.getDirection().normalize();
                        fireball2.setVelocity(v3.multiply(v3.length() * 2));
                        
                        fireball2.setShooter(player);
                        fireball3.setShooter(player);
                        fireball4.setShooter(player);
                        
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "SMALL FIREBALL BARRAGE");
                    }
                }
            }

      
            if (weap.getTypeId() == 377)
            {
                if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR))
                {
                    /*
                    if (fireballEntID.containsKey(player.getName()))
                    {
                        int id = fireballEntID.get(player.getName());
                        
                        Entity ent = null;
                        
                        for (Entity e : Bukkit.getWorld(player.getWorld().getName()).getEntities())
                        {
                            if (e.getEntityId() == id)
                            {
                                ent = e;
                            }
                        }
                        
                        if (ent == null)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No Fireball to ride!");
                        }
                        else
                        if (ent instanceof Fireball)
                        {
                            Fireball fireb = (Fireball) ent;
                            
                            if (fireb.getPassenger() != null)
                            {
                                if (fireb.getPassenger() == player)
                                {
                                    player.leaveVehicle();
                                    fireb.setIsIncendiary(true);
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You stop riding.");
                                    fireb.remove();
                                }
                            }
                            else
                            {
                                fireb.setIsIncendiary(false);
                                fireb.setPassenger(player);
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You ride the fireball!");
                            }
                            
                        }
                        
                        fireballEntID.remove(player.getName());
                    }
                    */
                    
                    if (fireball2.containsKey(player))
                    {
            
                        int fbtimer = ((Integer)fireball2.get(player)).intValue();
                        int totaltime = currenttime - fbtimer;
                        if ((totaltime > bigFireCD) || (totaltime < 0))
                        {
                            fb = false;
                        }
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can shoot a Fireball in " + (bigFireCD - totaltime) + " seconds.");
                            fb = true;
                        }
                    }

          
                    if (!fb)
                    {
                        fireball2.put(player, Integer.valueOf(currenttime));
                        Location locagain = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                        Fireball fireball = player.getWorld().spawn(locagain, Fireball.class);
                        fireball.setShooter(player);
                       // Bukkit.broadcastMessage("Fireball velocity is: " + fireball.getVelocity());
                        Vector v = locagain.getDirection().normalize();
                        fireball.setVelocity(v.multiply(v.length() * 2));
                        
                        fireballEntID.put(player.getName(), fireball.getEntityId());
                       //Bukkit.broadcastMessage("Fireball velocity is NOW: " + fireball.getVelocity());
                    }

                }

                if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR))
                {
                    if (smallfireball.containsKey(player))
                    {
                        int sfbtimer = ((Integer)smallfireball.get(player)).intValue();
                        int totaltime = currenttime - sfbtimer;
                        
                        if ((totaltime > smallFireCD) || (totaltime < 0))
                        {
                            sfb = false;
                        }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can shoot Small Fireball in " + (smallFireCD - totaltime) + " seconds.");
                        sfb = true;
                    }

                    }

                    if (!sfb)
                    {
                        smallfireball.put(player, Integer.valueOf(currenttime));
                        Location locagain = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
                        SmallFireball localSmallFireball1 = (SmallFireball)player.getWorld().spawn(locagain, SmallFireball.class);
                        localSmallFireball1.setShooter(player);
                        
                        Vector v = locagain.getDirection().normalize();
                        localSmallFireball1.setVelocity(v.multiply(v.length() * 2));
                    }
                }
      
            }
            
            if (weap.getTypeId() == 378) // magma cream
            {
              if (player.hasPermission("sr.pyromancer.phoenix"))
              {
                  if (player.isSneaking())
                  {
                      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                      {
                        int time = plugin.getCurrentTime();

                        if (PhoenixTimer.containsKey(player.getName()))
                        {
                            int phxtime = PhoenixTimer.get(player.getName());
                            int totaltime = time - phxtime;

                            if ((totaltime > PhoenixCD) || (totaltime < 0))
                            {
                                PhoenixTimer.put(player.getName(), time);
                                isChanneling.add(player.getName());

                                new LaunchPhoenix(player);

                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.LIGHT_PURPLE + "You begin channeling the Phoenix. Remain sneaking.");
                                // Paladin.isStunned.add(player.getName());
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot channel the Phoenix again for another " + (PhoenixCD - totaltime) + " seconds.");
                                return;
                            }
                        }
                        else
                        {
                            isChanneling.add(player.getName());
                            new LaunchPhoenix(player);

                            PhoenixTimer.put(player.getName(), time);
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.LIGHT_PURPLE + "You begin channeling the Phoenix. Remain sneaking.");
                            // Paladin.isStunned.add(player.getName());
                        }
                      }
                  }
                        
              }
            }
            
            
    
        }

    } // end player interact
    
    
    public HashMap<String, ItemStack[]> playerInventory = new HashMap<String, ItemStack[]>();
    public HashMap<String, ItemStack[]> playerArmor = new HashMap<String, ItemStack[]>();
    public HashMap<String, Location> deathLoc = new HashMap<String, Location>();
    
    @EventHandler
    public void onPlayerDeath (PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        
        if (isPhoenix.contains(player.getName()))
        {
            ItemStack[] parmor = player.getInventory().getArmorContents();
            ItemStack[] pinv = player.getInventory().getContents();
            
            playerInventory.put(player.getName(), pinv);
            playerArmor.put(player.getName(), parmor);
            
            deathLoc.put(player.getName(), player.getLocation());
            
            event.setDeathMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.DARK_RED + " rises again...");
            
            
            event.getDrops().clear();
            
            
            isPhoenix.remove(player.getName());
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "The Phoenix has left you.");
        }
        
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn (PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        
       
        
        if (deathLoc.containsKey(player.getName()))
        {
            ItemStack[] parmor = null;
            ItemStack[] pinv = null;
            
            if (playerArmor.containsKey(player.getName()))
            {
                parmor = playerArmor.get(player.getName());
            }
            
            if (playerInventory.containsKey(player.getName()))
            {
                pinv = playerInventory.get(player.getName());
            }
            
            if (parmor != null)
            {
                player.getInventory().setArmorContents(parmor);
            }
            
            if (pinv != null)
            {
                player.getInventory().setContents(pinv);
            }
            
            
            playerInventory.remove(player.getName());
            playerArmor.remove(player.getName());
            
            
            Location ploc = deathLoc.get(player.getName());
            
            // player.sendMessage("Teleport to " + ploc.toString());
            
            event.setRespawnLocation(ploc);
            
            
            int myparty = -1;
            int targetparty = -2;
            
            if (plugin.party.containsKey(player.getName().toLowerCase()))
            {
                myparty = plugin.party.get(player.getName().toLowerCase());
            }
            
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (p.getWorld().equals(player.getWorld()))
                {
                    if (p.getLocation().distance(ploc) < PhoenixDist)
                    {
                        if (plugin.party.containsKey(p.getName().toLowerCase()))
                        {
                            targetparty = plugin.party.get(p.getName().toLowerCase());
                        }
                        
                        if (targetparty != myparty && p != player)
                        {
                            if (p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
                            {
                                p.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,0,0));
                                
                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Phoenix Reborn removes your Fire Protection!");
                            }
                            
                            p.setFireTicks(PhoenixFireTicks);
                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are seared by " + ChatColor.GOLD + player.getName() + "'s Phoenix Reborn!");
                        }
                    }
                }
            }
            
            deathLoc.remove(player.getName());
        }
    }
    
    
    public HashSet<String> isChanneling = new HashSet<String>();
    public HashSet<String> isDone = new HashSet<String>();
    
    
    private class PhoenixRebornTimerCountDown implements Runnable
    {
        Player player;
        int taskId;
        
        public PhoenixRebornTimerCountDown (Player player)
        {
            this.player = player;
            this.taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Pyromancer.this.plugin, this, PhoenixRebornTickDelay);
        }
        
        @Override
        public void run()
        {
            if (isPhoenix.contains(player.getName()))
            {
                if (player.isOnline())
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "The Phoenix has left you.");
                }
                isPhoenix.remove(player.getName());
            }
            
            
        }
    }
    
    private class LaunchPhoenix implements Runnable
    {
        Player player;
        int taskId;
        World pworld;
        int i;
        
        public LaunchPhoenix (Player player)
        {
            i = 0;
            this.player = player;
            this.pworld = player.getWorld();
            
            this.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Pyromancer.this.plugin, this, 0, PhoenixTicks);
        }
        
        @Override
        public void run()
        {
            i++;
            if (i > NovaCount + 5)
            {
                Party.log.info(String.format("LaunchPhoenix Thread Errant: Cancelling Task ID: " + taskId));
                Bukkit.getServer().getScheduler().cancelTask(taskId);
                return;
            }
            
            if (player.isOnline() && (!(player.isDead())))
            {
                if (!(isChanneling.contains(player.getName())))
                {
                    Bukkit.getServer().getScheduler().cancelTask(taskId);
                    return;
                }

                
                if (i <= NovaCount)
                {
                    int myparty = -1;
                    int targetparty = -2;
                    
                    if (plugin.party.containsKey(player.getName().toLowerCase()))
                    {
                        myparty = plugin.party.get(player.getName().toLowerCase());
                    }
                    
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (p.getWorld().equals(pworld) && p != player)
                        {
                            if (p.getLocation().distance(player.getLocation()) < PhoenixDist)
                            {
                                if (plugin.party.containsKey(p.getName().toLowerCase()))
                                {
                                    targetparty = plugin.party.get(p.getName().toLowerCase());
                                }
                                else
                                {
                                    targetparty = -2;
                                }
                                
                                if (myparty != targetparty && p != player)
                                {
                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are seared by " + ChatColor.GOLD + player.getName() + "'s Phoenix Wave!");
                                    
                                    if (p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
                                    {
                                        p.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,0,0));
                                        
                                        p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "The Phoenix Wave removes your Fire Protection!");
                                    }
                                    
                                    // p.damage(PhoenixDamage);
                                }
                            }
                        }
                    }
                    
                    new Phoenix(player);
                }
                else
                {   
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are done channeling the Phoenix.");
                    
                    int currenttime = plugin.getCurrentTime();
                    
                    if (PhoenixRebornTimer.containsKey(player.getName()))
                    {    
                        int timeCheck = PhoenixRebornTimer.get(player.getName());
                        int totaltime = (currenttime - timeCheck);
                        
                        if ((totaltime > PhoenixReborn) || (totaltime < 0))
                        {
                            isPhoenix.add(player.getName());
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.YELLOW + "You are protected by the heat of the Phoenix.");
                            
                            
                            PhoenixRebornTimer.put(player.getName(), currenttime);
                            
                            new PhoenixRebornTimerCountDown(player);
                        }
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "The Phoenix cannot be called upon yet. Wait " + (PhoenixReborn - totaltime) + " seconds.");
                        }
                        
                    }
                    else
                    {
                        isPhoenix.add(player.getName());
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.YELLOW + "You are protected by the heat of the Phoenix.");
                        
                        
                        PhoenixRebornTimer.put(player.getName(), currenttime);
                        
                        new PhoenixRebornTimerCountDown(player);
                    }

                    isDone.add(player.getName());
                    //stop
                    if (isChanneling.contains(player.getName()))
                    {
                        isChanneling.remove(player.getName());
                    }
                    if (Paladin.isStunned.contains(player.getName()))
                    {
                        Paladin.isStunned.remove(player.getName());
                    }

                    Bukkit.getServer().getScheduler().cancelTask(taskId);
                    return;
                }
            }
            else
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You died while channeling the Phoenix.");

                isDone.add(player.getName());
                //stop
                if (isChanneling.contains(player.getName()))
                {
                    isChanneling.remove(player.getName());
                }
                if (Paladin.isStunned.contains(player.getName()))
                {
                    Paladin.isStunned.remove(player.getName());
                }
              
                Bukkit.getServer().getScheduler().cancelTask(taskId);
                return;
            }
            
        }
    }
    
    private class Phoenix implements Runnable
    {
        Player player;
        int taskId;

        int i;
        int o;
        int count;
        
        Block center;
        
        HashSet<Block> fireBlock;
        
        public Phoenix (Player player)
        {
            this.player = player;
            this.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Pyromancer.this.plugin, this, 0, PhoenixTicks2);
            
            i = 0;
            o = 0;
            count = 0;
            fireBlock = new HashSet<Block>();
            
            if (isDone.contains(player.getName()))
            {
                isDone.remove(player.getName());
            }
                
            center = player.getLocation().getBlock();
        }
        
        int a = 0;
        
        
        @Override
        public void run() 
        {
            a++;

            // Bukkit.broadcastMessage("A is " + a);
            
            if (isDone.contains(player.getName()))
            {
                isDone.remove(player.getName());
                
                if (isChanneling.contains(player.getName()))
                {
                    isChanneling.remove(player.getName());
                }

                if (Paladin.isStunned.contains(player.getName()))
                {
                    Paladin.isStunned.remove(player.getName());
                }
                
                if (fireBlock.size() > 0)
                {
                    for (Block b : fireBlock)
                    {
                        if (b.getType() == Material.FIRE)
                        {
                            b.setType(Material.AIR);
                        }
                    }
                    fireBlock.clear();
                }
                
                Bukkit.getServer().getScheduler().cancelTask(taskId);
                return;
            }


            
            if (!(player.isSneaking()))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cancel channeling the Phoenix.");
                //stop
                if (isChanneling.contains(player.getName()))
                {
                    isChanneling.remove(player.getName());
                }

                if (Paladin.isStunned.contains(player.getName()))
                {
                    Paladin.isStunned.remove(player.getName());
                }

                if (fireBlock.size() > 0)
                {
                    for (Block b : fireBlock)
                    {
                        if (b.getType() == Material.FIRE)
                        {
                            b.setType(Material.AIR);
                        }
                    }
                    fireBlock.clear();
                }

                Bukkit.getServer().getScheduler().cancelTask(taskId);
                return;
            }

            if (a > PhoenixDur && player.hasPermission("pyromancer.phoenix"))
            {
                if (fireBlock.size() > 0)
                {
                    for (Block b : fireBlock)
                    {
                        if (b.getType() == Material.FIRE)
                        {
                            b.setType(Material.AIR);
                        }
                    }
                    fireBlock.clear();
                }
                
                // Party.log.info(String.format("Phoenix Thread Errant: Cancelling Task ID: " + taskId));
                
                Bukkit.getServer().getScheduler().cancelTask(taskId);
                return;
            }

            if (a <= PhoenixDur)
            {
                if (isChanneling.contains(player.getName()) && (!(player.isDead())))
                {
                    double phealth = player.getHealth();
                    double newhealth = phealth + PhoenixRegen;
                    if (newhealth > player.getMaxHealth())
                    {
                        player.setHealth(player.getMaxHealth());
                    }
                    else
                    {
                        player.setHealth(newhealth);
                    }

                    // do fire

                    i = i + 1;
                    o = i - 1;


                    int bx = center.getX();
                    int y = center.getY();
                    int bz = center.getZ();

                    for (int x = bx - i; x <= bx + i; x++)
                    {
                        for (int z = bz - i; z <= bz + i; z++)
                        {
                            if (Math.abs(x-bx) == i || Math.abs(z - bz) == i)
                            {   
                                Block b = center.getWorld().getBlockAt(x,y,z);
                                
                                if (b.getType() == Material.AIR || b.getType() == Material.LONG_GRASS)
                                {
                                    fireBlock.add(b);
                                    b.setType(Material.FIRE);
                                }
                                
                                b = center.getWorld().getBlockAt(x,y + 1,z);
                                
                                if (b.getType() == Material.AIR || b.getType() == Material.LONG_GRASS)
                                {
                                    fireBlock.add(b);
                                    b.setType(Material.FIRE);
                                }
                                
                                b = center.getWorld().getBlockAt(x,y - 1,z);
                                
                                if (b.getType() == Material.AIR || b.getType() == Material.LONG_GRASS)
                                {
                                    fireBlock.add(b);
                                    b.setType(Material.FIRE);
                                }
                               
                                
                            }
                        }
                    }

                    for (int x = bx - o; x <= bx + o; x++)
                    {
                        for (int z = bz - o; z <= bz + o; z++)
                        {
                            if (Math.abs(x-bx) == o || Math.abs(z - bz) == o)
                            {
                                Block b = center.getWorld().getBlockAt(x,y,z);
                                
                                fireBlock.remove(b);
                                
                                if (b.getType() == Material.FIRE)
                                {
                                    b.setType(Material.AIR);
                                }
                                
                                b = center.getWorld().getBlockAt(x,y + 1,z);
                                
                                fireBlock.remove(b);
                                
                                if (b.getType() == Material.FIRE)
                                {
                                    b.setType(Material.AIR);
                                }
                                
                                b = center.getWorld().getBlockAt(x,y - 1,z);
                                
                                fireBlock.remove(b);

                                if (b.getType() == Material.FIRE)
                                {
                                    b.setType(Material.AIR);
                                }
                            }
                        }
                    }

                    if (o == PhoenixDur - 2)
                    {
                        o = i;

                        for (int x = bx - o; x <= bx + o; x++)
                        {
                            for (int z = bz - o; z <= bz + o; z++)
                            {
                                if (Math.abs(x-bx) == o || Math.abs(z - bz) == o)
                                {
                                    Block b = center.getWorld().getBlockAt(x,y,z);
                                    
                                    fireBlock.remove(b);
                                    
                                    if (b.getType() == Material.FIRE)
                                    {
                                        b.setType(Material.AIR);
                                    }
                                    
                                    
                                    b = center.getWorld().getBlockAt(x,y + 1,z);
                                
                                    fireBlock.remove(b);

                                    if (b.getType() == Material.FIRE)
                                    {
                                        b.setType(Material.AIR);
                                    }
                                    
                                    b = center.getWorld().getBlockAt(x,y - 1,z);
                                
                                    fireBlock.remove(b);

                                    if (b.getType() == Material.FIRE)
                                    {
                                        b.setType(Material.AIR);
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cancel channeling the Phoenix.");
                    if (isChanneling.contains(player.getName()))
                    {
                        isChanneling.remove(player.getName());
                    }

                    if (Paladin.isStunned.contains(player.getName()))
                    {
                        Paladin.isStunned.remove(player.getName());
                    }
                    
                    if (fireBlock.size() > 0)
                    {
                        for (Block b : fireBlock)
                        {
                            if (b.getType() == Material.FIRE)
                            {
                                b.setType(Material.AIR);
                            }
                        }
                        fireBlock.clear();
                    }
                    
                    Bukkit.getServer().getScheduler().cancelTask(taskId);
                }
            }
            
        }// end run
            
        
    }
    
    
    @EventHandler
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Fireball)
        {
            Fireball entity = (Fireball) event.getEntity();

            //Bukkit.broadcastMessage("Entity is Fireball");
            
            if (entity.getShooter() instanceof Player)
            {
                //Bukkit.broadcastMessage("Shooter is Player");
                Player shooter = (Player) entity.getShooter();
                if (shooter.hasPermission("pyromancer.fire"))
                {
                    Location fbloc = entity.getLocation();

                    entity.remove();
                    //Bukkit.broadcastMessage("Hit Fireball");
                    for (Player playersnear : Bukkit.getOnlinePlayers())
                    {
                        if (playersnear.getWorld().equals(entity.getWorld()))
                        {
                            if (playersnear.getLocation().distance(fbloc) < 12)
                            {
                                if (playersnear != shooter)
                                {
                                    playersnear.damage(event.getDamage(), shooter);
                                    playersnear.playSound(fbloc, Sound.EXPLODE, 1, 2);
                                    //Bukkit.broadcastMessage("Exploding");
                                }
                            }
                        }
                    }


                }
            }
        }
        
        if (event.getEntity() instanceof SmallFireball)
        {
            SmallFireball entity = (SmallFireball) event.getEntity();

            if (entity.getShooter() instanceof Player)
            {
                Player shooter = (Player) entity.getShooter();
                if (shooter.hasPermission("pyromancer.fire"))
                {
                    Location fbloc = entity.getLocation();

                    entity.remove();
                    //Bukkit.broadcastMessage("Hit Small Fireball");
                    for (Player playersnear : Bukkit.getOnlinePlayers())
                    {
                        if (playersnear.getWorld().equals(entity.getWorld()))
                        {
                            if (playersnear.getLocation().distance(fbloc) < 8)
                            {
                                if (playersnear != shooter)
                                {
                                    playersnear.damage(event.getDamage(), shooter);
                                    playersnear.playSound(fbloc, Sound.EXPLODE, 1, 2);
                                    //Bukkit.broadcastMessage("Exploding 2");
                                }
                            }
                        }
                    }


                }
            }
        }
    }
    @EventHandler
    public void onEntityDamage (EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player)event.getEntity();
            
            PlayerInventory inv = ((Player)event.getEntity()).getInventory();

            ItemStack helm = inv.getHelmet();
            ItemStack chest = inv.getChestplate();
            ItemStack legs = inv.getLeggings();
            ItemStack shoes = inv.getBoots();
            
            
            if (event.getCause() == DamageCause.FIRE_TICK)
            {
                if (player.hasPermission("pyromancer.phoenix") || player.hasPermission("pyromancer.fire"))
                {
                    event.setCancelled(true);
                    player.setFireTicks(0);
                    event.setDamage(0);
                }
            }
            
            if (event instanceof EntityDamageByEntityEvent)
            {                
                EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent)event;
                Entity attacker2 = mobevent.getDamager();
                
                if (player.hasPermission("pyromancer.phoenix"))
                {
                    // regen health here
                }
                
                if (player.hasPermission("pyromancer.fire"))
                {
                    if (attacker2 instanceof Arrow)
                    {
                        double arrowdamage = event.getDamage();
                        double newtotal = arrowdamage + 3;
                        event.setDamage(newtotal);
                    }
                    
                    if (attacker2 instanceof Player)
                    {
                        Player fireattacker = (Player)mobevent.getDamager();
                        
                        if (PL.stuntarget.containsKey(fireattacker.getName().toLowerCase()) || Paladin.isStunned.contains(fireattacker.getName()) || ChaosCrusader.chaosstunned.containsKey(fireattacker.getName()))
                        {
                              event.setCancelled(true);
                              fireattacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                                          return;
                        }
                        
                      //  int attackNoDmg = fireattacker.getNoDamageTicks();
                      //  int targetNoDmg = player.getMaximumNoDamageTicks();
                        
                      //  Bukkit.broadcastMessage("Attacker No Dmg: " + attackNoDmg);
                     //   Bukkit.broadcastMessage("Target No Dmg: " + targetNoDmg);
                      //  Bukkit.broadcastMessage("Target No Dmg / 2.0F : " + (targetNoDmg/2.0F));
                        
                        if (fireattacker.getNoDamageTicks() < player.getMaximumNoDamageTicks()/3.0F)
                        {
                            double random = Math.random();
                        
                            if (random >= 0.75)
                            {
                                fireattacker.setFireTicks(60);
                                fireattacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.YELLOW + "Attacking the Pyromancer sets you on fire!");
                            }
                        }
                        
                    }
                    
                }
                
                if (attacker2 instanceof Fireball)
                {
                    double fireballdamage = event.getDamage() + 6;
                    event.setDamage(fireballdamage);
                }
            }
        }
    }
    
    
    
    
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
      if (event.getBlock().getTypeId() == 87)
      {
        return;
      }
      
      event.setCancelled(true);
    }
    
    // fireball cleanup
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {

        Player p = event.getPlayer();


        if (isChanneling.contains(p.getName()))
        {
            isChanneling.remove(p.getName());
        }
        
        if (isDone.contains(p.getName()))
        {
            isDone.remove(p.getName());
        }
        
        if (!p.hasPermission("pyromancer.fire")) 
        {
            return;
        }
        World world = p.getWorld();
        String worldname = world.getName();



        if (!worldname.equalsIgnoreCase("world") && (!worldname.equalsIgnoreCase("pvp"))) 
        {
            return;
        }


        Location ploc = p.getLocation();

        Block block = ploc.getBlock();
        Chunk chunk = block.getChunk();

        int px = ploc.getBlockX();
        int y = ploc.getBlockY();
        int pz = ploc.getBlockZ();

        Location loc = new Location(ploc.getWorld(), px + 16, y, pz);
        Location loc2 = new Location(ploc.getWorld(), px - 16, y, pz);
        Location loc3 = new Location(ploc.getWorld(), px, y, pz + 16);
        Location loc4 = new Location(ploc.getWorld(), px, y, pz - 16);
        Location loc5 = new Location(ploc.getWorld(), px - 16, y, pz - 16);
        Location loc6 = new Location(ploc.getWorld(), px + 16, y, pz - 16);
        Location loc7 = new Location(ploc.getWorld(), px - 16, y, pz + 16);
        Location loc8 = new Location(ploc.getWorld(), px + 16, y, pz - 16);

        Block cblock = loc.getBlock();
        Block cblock2 = loc2.getBlock();
        Block cblock3 = loc3.getBlock();
        Block cblock4 = loc4.getBlock();
        Block cblock5 = loc5.getBlock();
        Block cblock6 = loc6.getBlock();
        Block cblock7 = loc7.getBlock();
        Block cblock8 = loc8.getBlock();

        Chunk cchunk = cblock.getChunk();
        Chunk cchunk2 = cblock2.getChunk();
        Chunk cchunk3 = cblock3.getChunk();
        Chunk cchunk4 = cblock4.getChunk();
        Chunk cchunk5 = cblock5.getChunk();
        Chunk cchunk6 = cblock6.getChunk();
        Chunk cchunk7 = cblock7.getChunk();
        Chunk cchunk8 = cblock8.getChunk();


        for (Entity ent : chunk.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

        for (Entity ent : cchunk.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

        for (Entity ent : cchunk2.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

        for (Entity ent : cchunk3.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

        for (Entity ent : cchunk4.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

        for (Entity ent : cchunk5.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

        for (Entity ent : cchunk6.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

        for (Entity ent : cchunk7.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

        for (Entity ent : cchunk8.getEntities())
        {
          if ((ent instanceof Fireball))
          {
            ent.remove();

          }

          if (!(ent instanceof SmallFireball))
            continue;
          ent.remove();

        }

    } // end player quit
    
}
