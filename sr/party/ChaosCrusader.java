
package sr.party;

import java.util.HashMap;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * @author Acidsin
 */
public class ChaosCrusader implements Listener
{
    public Party plugin;
    
    public ChaosCrusader(Party plugin)
    {
        this.plugin = plugin;
    }
    
    public PL pl;
    
    public HashMap<Player, Integer> blink = new HashMap<Player, Integer>();
    public HashMap<Player, Integer> chaosswitch = new HashMap<Player, Integer>();
    public HashMap<String, Integer> chaosstun = new HashMap<String, Integer>();
    public HashMap<Player, Integer> rightclickcd = new HashMap<Player, Integer>();
    public static HashMap<String, Integer> chaosstunned = new HashMap<String, Integer>();
    
    public HashMap<String, Integer> CritCD = new HashMap<String, Integer>();
    
    public HashMap<String, Integer> stunTimer = new HashMap<String, Integer>();
    
    public int ICD = 5;
    public int BlinkCD = 15;
    public int StunCD = 24;
    public int SwitchCD = 45;
    
    private int randomStunTime = 1;
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        int iteminhand = player.getItemInHand().getTypeId();
        int ability = 0; //No ability

        if (PL.stuntarget.containsKey(player.getName().toLowerCase()) || Paladin.isStunned.contains(player.getName()) || ChaosCrusader.chaosstunned.containsKey(player.getName()))
        {
              event.setCancelled(true);
              player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
              return;
        }
            if (chaosstunned.containsKey(player.getName()))
            {
                int stuntime = chaosstunned.get(player.getName());
                int time = plugin.getCurrentTime();
                int totaltime = time - stuntime;
                int stunduration = randomStunTime - totaltime;
                
                if (totaltime < randomStunTime && totaltime > 0)
                {
                  //  player.sendMessage(ChatColor.GOLD + "You are stunned and can not move for " + stunduration + " seconds.");
                    event.setCancelled(true);
                    return;
                }
                
                if (totaltime > randomStunTime || totaltime < 0)
                {
                    chaosstunned.remove(player.getName());
                }

            }

        if (player.hasPermission("chaos.perm"))
        {
            if (iteminhand == 283)
            {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
                {
                    ability = 1; //blink strike
                }

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
                {
                    ability = 2 ; //Chaos Switch
                }
            }

            if (iteminhand == 276)
            {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
                {
                ability = 3; //Chaos Stun
                }

            }

             assert player != null;
             Entity target = null;
             double targetDistanceSquared = 0;
             final double radiusSquared = 1;
             final Vector l = player.getEyeLocation().toVector(), n = player.getLocation().getDirection().normalize();
             final double cos45 = Math.cos(Math.PI / 4);
             for (final LivingEntity other : player.getWorld().getEntitiesByClass(LivingEntity.class)) {
                  if (other == player)
                     continue;
                  if (target == null || targetDistanceSquared > other.getLocation().distanceSquared(player.getLocation())) {
                     final Vector t = other.getLocation().add(0, 1, 0).toVector().subtract(l);
                      if (n.clone().crossProduct(t).lengthSquared() < radiusSquared && t.normalize().dot(n) >= cos45) {
                         target = other;
                         targetDistanceSquared = target.getLocation().distanceSquared(player.getLocation());
                     }
                 }
             }

            if (target != null)
            {
                if (!(target instanceof Player))
                {
                    return;
                }

                Player target2 = (Player) target;

                   Location player1loc = player.getLocation();
                   Location targetloc = target2.getLocation();
                   int x1 = 0;
                   int y1 = 0;
                   int z1 = 0;
                   int x2 = 0;
                   int y2 = 0;
                   int z2 = 0;
                   int x3 = player1loc.getBlockX();
                   int y3 = player1loc.getBlockY();
                   int z3 = player1loc.getBlockZ();
                   int x4 = targetloc.getBlockX();
                   int y4 = targetloc.getBlockY();
                   int z4 = targetloc.getBlockZ();

                   if (x3 >= x4)
                   {
                       x1 = x4;
                       x2 = x3;
                   }
                   if (x3 < x4)
                   {
                       x1 = x3;
                       x2 = x4;
                   }

                   if (y3 >= y4)
                   {
                       y1 = y4;
                       y2 = y3;
                   }
                   if (y3 < y4)
                   {
                       y1 = y3;
                       y2 = y4;
                   }

                   if (z3 >= z4)
                   {
                       z1 = z4;
                       z2 = z3;
                   }
                   if (z3 < z4)
                   {
                       z1 = z3;
                       z2 = z4;
                   }

                   Block b;
                   World world = player.getWorld();
                   int amountofnonair = 0;
                   for(int x = x1; x <= x2; x++)
                   {

                      for(int y = y1; y <= y2; y++)
                      {

                          for(int z = z1; z <= z2; z++)
                          {
                              b = world.getBlockAt(x, y, z);

                              if (!(b.getType().equals(Material.AIR)))
                              {
                                  amountofnonair = amountofnonair + 1;
                              }

                          }

                      }

                   }

                   if (amountofnonair >= 6)
                   {

                       return;
                   }
                if (ability == 1)
                {


                    if (blink.containsKey(player))
                    {
                        int time = plugin.getCurrentTime();
                        int oldtime = blink.get(player);
                        int timedifference = time - oldtime;
                        int totaltime = BlinkCD - timedifference;

                        if (timedifference < BlinkCD && timedifference > 0)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Blink Strike is on cooldown for " + totaltime + " seconds.");
                            return;
                        }

                    }
                    Location playerloc = player.getLocation();
                    Location loc = target2.getLocation();
                    double distance = playerloc.distance(loc);

                    if (distance > 30.0)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You are too far away to Blink Strike to that Player.");
                        return;
                    }
                    
                    player.teleport(loc);
                    int time = plugin.getCurrentTime();
                    blink.put(player, time);
                    //player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 1));



                }

                if (ability == 2)
                {


                    if (chaosswitch.containsKey(player))
                    {
                        int time = plugin.getCurrentTime();
                        int oldtime = chaosswitch.get(player);
                        int timedifference = time - oldtime;
                        int totaltime = SwitchCD - timedifference;

                        if (timedifference < SwitchCD && timedifference > 0)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Chaos Switch is on cooldown for " + totaltime + " seconds.");
                            return;
                        }

                    }
                    Location playerloc = player.getLocation();
                    Location loc = target2.getLocation();
                    double distance = playerloc.distance(loc);

                    if (distance > 30.0)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You are too far away to Chaos Switch with that Player.");
                        return;
                    }

                    Location currentloc = player.getLocation();
                    player.teleport(loc);
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You Chaos Switch!");
                    target2.teleport(currentloc);
                    target2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You were Chaos Switched!");
                    target2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
                    int time = plugin.getCurrentTime();
                    chaosswitch.put(player, time);


                }

                if (ability == 3)
                {


                    if (chaosstun.containsKey(player.getName()))
                    {
                        int time = plugin.getCurrentTime();
                        int oldtime = chaosstun.get(player.getName());
                        int timedifference = time - oldtime;
                        int totaltime = StunCD - timedifference;

                        if (timedifference < StunCD && timedifference > 0)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Chaos Stun is on cooldown for " + totaltime + " seconds.");
                            return;
                        }

                    }
                    Location playerloc = player.getLocation();
                    Location loc = target2.getLocation();
                    double distance = playerloc.distance(loc);

                    if (distance > 30.0)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You are too far away to Chaos Stun with that Player.");
                        return;
                    }

                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You Chaos Stun!");
                    target2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You were Chaos Stunned!");

                    int time = plugin.getCurrentTime();
                    
                    double random = Math.random();
                    
                    if (random >= 0.01 && random <= 0.25)
                    {
                        randomStunTime = 4;
                    }
                    
                    if (random >= 0.26 && random <= 0.50)
                    {
                        randomStunTime = 3;
                    }
                    
                    if (random >= 0.51 && random <= 0.75)
                    {
                        randomStunTime = 2;
                    }
                    
                    if (random >= 0.76 && random <= 1)
                    {
                        randomStunTime = 1;
                    }
                    
                    
                    boolean canStun = false;
                   
                    
                    if (pl.stunImmune.containsKey(target2.getName()))
                    {
                        int timecheck = pl.stunImmune.get(target2.getName());
                        int totalcheck = time - timecheck;

                        if (totalcheck > pl.StunImmune || totalcheck < 0)
                        {
                            canStun = true;
                            pl.stunImmune.remove(target2.getName());
                        }
                        else
                        {
                            canStun = false;
                        }
                    }
                    else
                    {
                        canStun = true;
                    }
                    
                    if (canStun)
                    {
                        chaosstun.put(player.getName(), time);
                        chaosstunned.put(target2.getName(), time);
                        pl.stunImmune.put(target2.getName(), time);
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target has been stunned recently and is IMMUNE to stuns right now.");

                    }
                    



                }
            }

        }
    } // end player interact
    
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {

      if ((event.getEntity() instanceof Player)) 
      {


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

                 if (attacker.hasPermission("chaos.perm"))
                 {
                    if (!(attacker.getNoDamageTicks() < (attacker.getMaximumNoDamageTicks() / 3.0F)))
                    {
                        return;
                    }
                    
                    Random randomGenerator = new Random();
                    double multiplier = 1;

                    if (!(CritCD.containsKey(attacker.getName())))
                    {
                        int currenttime = plugin.getCurrentTime();
                        int random = randomGenerator.nextInt(100);
                        
                        if ((random <= 20))
                        {
                            multiplier = 2;
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You 2x Crit!");
                        }

                        if ((random >= 80) && (random <= 92))
                        {
                            multiplier = 2.25;
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You 2.25x Crit!");
                        }
                        
                        if ((random >= 93) && (random < 100))
                        {
                            multiplier = 2.5;
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You 2.5x Crit!");
                        }
                        
                        if ((random == 100))
                        {
                            multiplier = 3;
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You 3x Crit!");
                        }
                        
                        double eventdamage = event.getDamage();

                        double extradamage = eventdamage * multiplier;

                        event.setDamage(extradamage);
                        CritCD.put(attacker.getName(), currenttime);
                    }
                    else
                    {
                        int currenttime = plugin.getCurrentTime();
                        int timecheck = CritCD.get(attacker.getName());
                        int totaltime = (currenttime - timecheck);

                        if (totaltime > ICD || totaltime < 0)
                        {
                            CritCD.remove(attacker.getName());
                        }
                    }

                 }
             }
          }
      }
    } // end damage
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        
        if (pl.stunImmune.containsKey(player.getName()))
        {
            int time = plugin.getCurrentTime();
            int oldtime = pl.stunImmune.get(player.getName());
            int totaltime = (time - oldtime);

            if (totaltime > pl.StunImmune || totaltime < 0)
            {
                pl.stunImmune.remove(player.getName());
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are no longer IMMUNE to stuns.");
            }
        }
        
        if (chaosstunned.containsKey(player.getName()))
        {
            int stuntime = chaosstunned.get(player.getName());
            int time = plugin.getCurrentTime();
            int totaltime = time - stuntime;

           // int stunduration = randomStunTime - totaltime;

           if (totaltime > randomStunTime || totaltime < 0)
           {
               chaosstunned.remove(player.getName());
               player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You are no longer stunned.");
           }
           else
           {
               if (player.getFallDistance() == 0)
               {
                   if (player.getNoDamageTicks() < player.getMaximumNoDamageTicks()/2.0F)
                   {
                       Location cancel = event.getFrom();
                       cancel.setPitch(event.getFrom().getPitch());
                       cancel.setYaw(event.getFrom().getYaw());
                       if (!(event.getFrom().getY() > event.getTo().getY()))
                       {
                           player.teleport(cancel);
                           player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1, 2);
                       }
                   }
               }
           }

        }

    }
}
