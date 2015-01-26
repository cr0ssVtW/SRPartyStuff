package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/*
 * This class file houses Berserker and Bard plus other random Party stuff.
 * 
 */
public class PL implements Listener
{
    public Party plugin;
    
    public PL(Party plugin)
    {
        this.plugin = plugin;
    }
    
    public double HymnRange = 32;
    public int inspireCD = 30;
    public double inspireAmount = 12;
    public double inspirePartyAmount = (Math.ceil(inspireAmount * 1.33) / 2);
    public int melodyCD = 35;
    public int cleanseCD = 24;
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    // DisguiseCraftAPI dcAPI = DisguiseCraft.getAPI();
    
   public static HashSet<String> inRage = new HashSet<String>();

   
   public int RageCD = 30;
   public int RageDur = 15;
   public int RageBuffDur = 240;
   public int RageKillHeal = 4;
   
   public int StunTimer = 2;
   
   
   
   public HashMap<String, Double> hphash = new HashMap<String, Double>();
   public HashMap<String, Double> hphash2 = new HashMap<String, Double>();
   
   public static HashMap<String, Integer> stunImmune = new HashMap<String, Integer>();
   
   public static int StunImmune = 10;

   public HashMap<String, Integer> sprintTimer = new HashMap<String, Integer>();
   
   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event)
   {
       Player player = event.getPlayer();
       
       if (stuntarget.containsKey(player.getName().toLowerCase()))
       {
           stuntarget.remove(player.getName().toLowerCase());
       }
       
   }


   @EventHandler(priority = EventPriority.LOW)
   public void onPlayerMove(PlayerMoveEvent event)
   {
       
       Player player = event.getPlayer();
       
       if (stunImmune.containsKey(player.getName()))
       {
           int time = plugin.getCurrentTime();
           int oldtime = stunImmune.get(player.getName());
           int totaltime = (time - oldtime);
           
           if (totaltime > StunImmune || totaltime < 0)
           {
               stunImmune.remove(player.getName());
               player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are no longer IMMUNE to stuns.");
           }
       }
       
       if (plugin.invitedplayertime.containsKey(player.getName().toLowerCase()))
       {
           
           int time = plugin.getCurrentTime();
           int oldtime = plugin.invitedplayertime.get(player.getName().toLowerCase());
           
           int totaltime = (time - oldtime);
           
           
                   
           if (totaltime > 60 || totaltime < 0)
           {
               
               if (plugin.temphashmap.containsKey(player.getName().toLowerCase()))
               {
                   
                   plugin.temphashmap.remove(player.getName().toLowerCase());
                   
               }
               
               plugin.invitedplayertime.remove(player.getName().toLowerCase());
               
               player.sendMessage(ChatColor.RED + "Your party invite has expired.");
               
               
           }
           
       }
       
       if (player.hasPotionEffect(PotionEffectType.SLOW))
       {
           Vector slowspeed = player.getVelocity();
           
           if (player.isSprinting())
           {
               int time = plugin.getCurrentTime();
               if (sprintTimer.containsKey(player.getName()))
               {
                   int timecheck = sprintTimer.get(player.getName());
                   int totaltime = time - timecheck;
                   
                   player.setVelocity(slowspeed);
                   
                   if (totaltime > 3)
                   {
                       sprintTimer.remove(player.getName());
                       player.setVelocity(slowspeed);
                   }
               }
               else
               {
                   player.sendMessage(ChatColor.RED + "You cannot sprint while slowed.");
                   player.setVelocity(slowspeed);
                   sprintTimer.put(player.getName(), time);
               }
               
               
           }
       }
       
       
       
       if (stuntarget.containsKey(player.getName().toLowerCase()))
       {
           int currenttime = plugin.getCurrentTime();
           int stuntime = stuntarget.get(player.getName().toLowerCase());
           int totaltime = (currenttime - stuntime);
           
           if (totaltime > StunTimer || totaltime < 0)
           {
               stuntarget.remove(player.getName().toLowerCase());
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
   
   @EventHandler
   public void onEntityDamage(EntityDamageEvent event)
   {
       
       Entity entity = event.getEntity();
       
       Boolean isplayer = false;
       
       if (entity instanceof Player)
       {
           isplayer = true;
       }
       
     if (isplayer == true)
     {
           
       
       Player target = (Player) event.getEntity();
       
       // Party Prot
       
       
       
       
       if (plugin.party.containsKey(target.getName().toLowerCase()))
       {
           
        if (event.getCause() == DamageCause.POISON || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA
             || event.getCause() == DamageCause.LIGHTNING || event.getCause() == DamageCause.SUFFOCATION || event.getCause() == DamageCause.DROWNING || event.getCause() == DamageCause.FALL
             || event.getCause() == DamageCause.STARVATION || event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION
                || event.getCause() == DamageCause.MAGIC || event.getCause() == DamageCause.VOID || event.getCause() == DamageCause.MELTING
                || event.getCause() == DamageCause.SUICIDE || event.getCause() == DamageCause.CONTACT || event.getCause() == DamageCause.WITHER)
          {

              return;

          }
          
         
           
         if (event.getCause() == DamageCause.PROJECTILE && (!(event.getEntity() instanceof Player)))
         {
             return;
         }       
       
         EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
       
         
         if (event_EE.getDamager() instanceof Arrow)
         {
            Projectile projectile = (Projectile) event_EE.getDamager();
            
            if (!(projectile.getShooter() instanceof Player))
            {
                    return;
            }
                
            Player attacker = (Player) projectile.getShooter();
                
            
	    if (!(attacker instanceof Player)) return;
             
            /*
            if (target instanceof Player)
            {
                if (inRage.contains(target.getName()))
                {
                     double dmg = event.getDamage();
                     double newdmg = Math.ceil(dmg / 2);
                     event.setDamage(0);
                     event.setCancelled(true);
                     if ((target.getHealth() - newdmg) <= 1)
                     {
                         target.damage(dmg, attacker);
                     }
                     else
                     {
                         target.damage(newdmg);
                     }
                }
            }
            */
            
            if (attacker.hasPermission("bard.cast"))
            {
                double dmg = event.getDamage();
                double newdmg = Math.round(dmg * 0.65D);
                
                event.setDamage(newdmg);
            }
            
             if (plugin.party.containsKey(attacker.getName().toLowerCase()) && (plugin.party.containsKey(target.getName().toLowerCase())))
             {
                 int party = plugin.party.get(target.getName().toLowerCase());
                 int party2 = plugin.party.get(attacker.getName().toLowerCase());
                 
                 if (party == party2)
                 {
                     event.setCancelled(true);
                     attacker.sendMessage(ChatColor.GREEN + "You cannot hurt a party member.");                     
                 }
                         
             }
             
         }
         
         
         
         if (event_EE.getDamager() instanceof Player)
         {
             Player attacker = ((Player)event_EE.getDamager());
             
            if (Party.ghost.contains(attacker.getName()))
            {
                event.setCancelled(true);
                event.setDamage(0);
                attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Ghosts cannot attack!");
                return;
            }
            
            if (attacker.hasPermission("bard.cast"))
            {
                double dmg = event.getDamage();
                double newdmg = Math.round(dmg * 0.65D);
                
                event.setDamage(newdmg);
            }
            
            /*
            if (target instanceof Player)
            {
                if (inRage.contains(target.getName()))
                {
                     double dmg = event.getDamage();
                     double newdmg = Math.ceil(dmg / 2);
                     event.setDamage(0);
                     event.setCancelled(true);
                     if ((target.getHealth() - newdmg) <= 1)
                     {
                         target.damage(dmg, attacker);
                     }
                     else
                     {
                         target.damage(newdmg);
                     }
                }
            }
            */
            
            if (PL.stuntarget.containsKey(attacker.getName().toLowerCase()) || Paladin.isStunned.contains(attacker.getName()) || ChaosCrusader.chaosstunned.containsKey(attacker.getName()))
                {
                      event.setCancelled(true);
                      attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                      return;
                }
               
              if (plugin.party.containsKey(attacker.getName().toLowerCase()) && (plugin.party.containsKey(target.getName().toLowerCase())))
              {
                int party = plugin.party.get(target.getName().toLowerCase());
                int party2 = plugin.party.get(attacker.getName().toLowerCase()); 
                
                
                if (party == party2)
                {
                    event.setCancelled(true);
                    
                    attacker.sendMessage(ChatColor.GREEN + "You cannot hurt a party member.");
                    
                    
                }
              
              
              }
           

           
           
           
           
           
         }
         
        
                
                
                

         
         
       }
     
       
       
       
       
       // MMO stuff
       
       
       
       if (event.getCause() == DamageCause.POISON || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA
            || event.getCause() == DamageCause.LIGHTNING || event.getCause() == DamageCause.SUFFOCATION || event.getCause() == DamageCause.DROWNING || event.getCause() == DamageCause.FALL
            || event.getCause() == DamageCause.STARVATION || event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION
               || event.getCause() == DamageCause.MAGIC || event.getCause() == DamageCause.VOID || event.getCause() == DamageCause.MELTING 
               || event.getCause() == DamageCause.SUICIDE || event.getCause() == DamageCause.CONTACT || event.getCause() == DamageCause.WITHER)
       {
           
           return;
           
       }
       
       if (event.getCause() == DamageCause.PROJECTILE && (!(event.getEntity() instanceof Player)))
       {
           return;
       }
       
       
       EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
       
       
       if (event_EE.getDamager() instanceof Player)
       {
           
           Player attacker = ((Player)event_EE.getDamager());
        
           
           
         /*  // fuck disguise
           
           if (dcAPI.isDisguised(attacker))
           {
               dcAPI.getDisguise(attacker);
               dcAPI.undisguisePlayer(attacker);
               
               
               attacker.sendMessage(ChatColor.RED + "You cannot attack while transformed. You have been removed from the disguise.");
               event.setCancelled(true);
           }
            * 
            */
           
           // Fuck fire Aspect
           
           ItemStack attackerinHand = attacker.getItemInHand();
           
           if (attackerinHand.containsEnchantment(Enchantment.FIRE_ASPECT))
           {
               event.setCancelled(true);
               target.setFireTicks(0);
               attacker.sendMessage(ChatColor.YELLOW + "You extinguish the flames on your target.");
           }
           
           
              
              // Start Rend
              if (attacker.hasPermission("warrior.bleed")) 
              {
                int party = 1;
                int party2 = 2;
                
          
         
                if (plugin.party.containsKey(attacker.getName().toLowerCase()) && (plugin.party.containsKey(target.getName().toLowerCase())))
        
                {
                    party = plugin.party.get(target.getName().toLowerCase());
                    party2 = plugin.party.get(attacker.getName().toLowerCase());    
                }
         
                if (party != party2)
         
                {
                Player player = (Player) event.getEntity();

                  if (!(attacker.getItemInHand() == null))
                  {
                      
                      
                      
                      if (attacker.getItemInHand().getTypeId() == 283 || attacker.getItemInHand().getTypeId() == 276 || attacker.getItemInHand().getTypeId() == 272
                              || attacker.getItemInHand().getTypeId() == 268 || attacker.getItemInHand().getTypeId() == 267)
                      {
                          
                          double chance = 0.05;
                          double roll = Math.random();
                          
                          if (roll < chance)
                          {

                              if (hphash.containsKey(player.getName().toLowerCase()))
                              {
                                  double currenthealth2 = player.getHealth();
                                  double healthset = hphash.get(player.getName().toLowerCase());
                                  
                                  
                                  if (!(currenthealth2 == healthset))
                                  {
                                      attacker.sendMessage(ChatColor.GOLD + "You rend your target with a poisoned blade.");
                                      
                                      player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 2));
                                      player.sendMessage(ChatColor.RED + "You begin to bleed from a poisoned blade."); 
                                        
                                      hphash.remove(player.getName().toLowerCase());
                                     
                                  }
                                  else
                                      return;
                                  
                              }
                              else
                              {
                                  double currenthealth = player.getHealth();
                                  
                                  hphash.put(player.getName().toLowerCase(), currenthealth);
                                  
                              
                                  /*
                                   * targetent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2));
                                   * attacker.sendMessage(ChatColor.GOLD + "You rend your target with a poisoned blade.");
                                   */ 
                                  
                              }
                         
                          }
                      
                      }
                  }
                }
              } // End Rend / Warrior Bleed
              
              
              // Start Berserker Headbutt
              
              if (attacker.hasPermission("berserker.use"))
              {
                  int party = 1;
                int party2 = 2;
                
                
          
                int currenttime = plugin.getCurrentTime();
                
         
                if (plugin.party.containsKey(attacker.getName().toLowerCase()) && (plugin.party.containsKey(target.getName().toLowerCase())))
        
                {
                    party = plugin.party.get(target.getName().toLowerCase());
                    party2 = plugin.party.get(attacker.getName().toLowerCase());    
                }
         
                if (party != party2)
         
                {
                    
                  Player player = (Player) event.getEntity();

                  if (!(attacker.getItemInHand() == null))
                  {
                      ItemStack weapon = attacker.getInventory().getItemInHand();
                      
                      if (!(weapon.getTypeId() == 258 || weapon.getTypeId() == 272 || weapon.getTypeId() == 279 || weapon.getTypeId() == 286 || weapon.getTypeId() == 275))
                      {
                          if (attacker.isOp() || attacker.hasPermission("berserker.bypass"))
                          {
                              return;
                          }
                          
                          event.setCancelled(true);
                          attacker.sendMessage(ChatColor.DARK_RED + "Berserkers can only use Axes. You do zero damage.");
                      }
                      
                      
                      if (attacker.getItemInHand().getTypeId() == 258 || attacker.getItemInHand().getTypeId() == 271 || attacker.getItemInHand().getTypeId() == 279
                              || attacker.getItemInHand().getTypeId() == 286 || attacker.getItemInHand().getTypeId() == 275) // if axe
                      {
                         /* double dmg = event.getDamage();
                          
                          double bdmg = dmg;
                          
                          event.setDamage(bdmg);
                          */
                          
                          if (bset.contains(attacker.getName().toLowerCase()))
                          {
                              boolean canStun = false;
                              
                              if (stunImmune.containsKey(player.getName()))
                              {
                                  int timecheck = stunImmune.get(player.getName());
                                  int totalcheck = currenttime - timecheck;
                                  
                                  if (totalcheck > StunImmune || totalcheck < 0)
                                  {
                                      canStun = true;
                                      stunImmune.remove(player.getName());
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
                                    stuntarget.put(player.getName().toLowerCase(), currenttime);
                              
                                    attacker.sendMessage(ChatColor.GREEN + "You " + ChatColor.GOLD + "Headbutt" + ChatColor.GREEN + " your target, stunning them.");

                                    headbutttimer.put(attacker.getName().toLowerCase(), currenttime);
                                    bset.remove(attacker.getName().toLowerCase());
                                    stunImmune.put(player.getName(), currenttime);
                              }
                              else
                              {
                                  attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target is immune to stuns.");
                                  return;
                              }
                              
                          }
                          
                          
                      }
                  }
                }
                
                
                
                
                
              } // End Berserker
                  
                  
              
       
              
              
              
              
              
              
              
              
              
              
              // Start Warrior Riposte
              if (target instanceof Player)
              {
                  if (target.hasPermission("warrior.bleed"))
                  {
                      int party = 1;
                      int party2 = 2;

                      if (event_EE.getDamager() instanceof Projectile)
                      {
                          return;
                      }
                      
                      if (plugin.party.containsKey(attacker.getName().toLowerCase()) && (plugin.party.containsKey(target.getName().toLowerCase())))
                      {
                          party = plugin.party.get(target.getName().toLowerCase());
                          party2 = plugin.party.get(attacker.getName().toLowerCase());    
                      }
         
                      if (party != party2)
                      {
                          if (target.getInventory().getHelmet() != null && target.getInventory().getChestplate() != null // Preventing null pointers
                              && target.getInventory().getLeggings() != null && target.getInventory().getBoots() != null)
                          { 
                              if ((target.getInventory().getHelmet().getTypeId() == 306 || target.getInventory().getHelmet().getTypeId() == 310) //If target is wearing
                                   && (target.getInventory().getChestplate().getTypeId() == 307 || target.getInventory().getChestplate().getTypeId() == 311) 
                                   && (target.getInventory().getLeggings().getTypeId() == 308 || target.getInventory().getLeggings().getTypeId() == 312) 
                                   && (target.getInventory().getBoots().getTypeId() == 309 || target.getInventory().getBoots().getTypeId() == 313)) // iron or diamond
                              {
                                  if (target.getItemInHand().getTypeId() == 283 || target.getItemInHand().getTypeId() == 276 || target.getItemInHand().getTypeId() == 272 // If target is wielding a sword
                                      || target.getItemInHand().getTypeId() == 268 || target.getItemInHand().getTypeId() == 267)
                                  {
                                      double chance = 0.97;
                                      double roll = Math.random();
                
                                      if (roll > chance)
                                      {
                      
                                          double dmg = event.getDamage();
                                          double attackerdmg = (dmg / 3);

                                          if (event_EE.getDamager() instanceof Projectile)
                                          {
                                              return;
                                          }
                                          
                                          if (event_EE.getDamager() instanceof Player)
                                          { 
                                                World plworld = target.getWorld();

                                                BlockVector pt = BukkitUtil.toVector(target.getLocation().getBlock());

                                                RegionManager regionManager = worldGuard.getRegionManager(plworld);
                                                ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                                                if (!(set.allows(DefaultFlag.PVP)))
                                                {
                                                    return;
                                                }
                                                else
                                                {
                                                    attacker.sendMessage(ChatColor.RED + "Your enemy ripostes, reflecting damage.");
                                                }
                                                
                                          }
                                          LivingEntity attacker2 = (LivingEntity) event_EE.getDamager();
                      
                                          event.setDamage(0);
                                          
                                          attacker2.damage(attackerdmg);

                                          target.sendMessage(ChatColor.GREEN + "You riposte an attack!");
                      
                      
                                          
                                      }
                                  }                  
                              }                              
                          }
                      }
                  } // end riposte
                  
                  
              } 
       }
       
       
     
       
       
       // Archer
       
       
       if (event_EE.getDamager() instanceof Arrow)
         {
            Projectile projectile = (Projectile) event_EE.getDamager();
            
            
            if (!(projectile.getShooter() instanceof Player))
            {
                    return;
            }
                
            Player attacker = (Player) projectile.getShooter();
            
           if (!(attacker instanceof Player)) return;
           
           
           if (attacker.hasPermission("archer.daze"))
           {
           // Fixing monster issue - look into this
              
             int party = 1;
             int party2 = 2;
             
            if (plugin.party.containsKey(attacker.getName().toLowerCase()) && (plugin.party.containsKey(target.getName().toLowerCase())))
            {
                
              party = plugin.party.get(target.getName().toLowerCase());
              party2 = plugin.party.get(attacker.getName().toLowerCase());    
            }
              
             if (party != party2)
             {
                 

              // Daze
                
                Location loc = target.getLocation();
                
                double chance = 0.05;
                double chance2 = 0.95;
                
                double roll = Math.random();
                
                if (roll < chance)
                {
                   loc.setPitch(90.0F);
                   
                   target.sendMessage(ChatColor.RED + "You've been dazed!");
                   
                   if (attacker instanceof Player)
                   {
                      attacker.sendMessage(ChatColor.GOLD + "You've dazed your target.");
                   }
                }
                
                if (roll > chance2)
                {
                  loc.setPitch(-90.0F);
                  target.sendMessage(ChatColor.RED + "You've been dazed!");
                  
                  if (attacker instanceof Player)
                  {
                     attacker.sendMessage(ChatColor.GOLD + "You've dazed your target.");
                  }
                }
                
                target.teleport(loc);
                
                // Fire Arrow
                
                
                double firechance = 0.30;
                
                double roll2 = Math.random();
                
                if (roll2 < firechance)
                {
                    
                    if (hphash2.containsKey(target.getName().toLowerCase()))
                    {
                      double currenthealth2 = target.getHealth();
                      double healthset = hphash2.get(target.getName().toLowerCase());
                                       
                      if (!(currenthealth2 == healthset))
                      {
                        target.setFireTicks(60);
                        target.sendMessage(ChatColor.RED + "You've been ignited by a fire arrow!");                                       
                        hphash2.remove(target.getName().toLowerCase());
                        
                        if (attacker instanceof Player)
                        {
                           attacker.sendMessage(ChatColor.GOLD + "You have ignited your target.");
                        }
                                     
                     
                      }          
                      else         
                          return;      
                    }     
                    else      
                    {                 
                        double currenthealth = target.getHealth();       
                        hphash2.put(target.getName().toLowerCase(), currenthealth);
                    }
                    
                    
                    

                    
                    
                }
                
             }
            
            } // end daze
           
            
            
            
            
            
            
         }
     }
   
       
       
       
       
       
       
       
       
       
       
       
       
   } // end onEntityDamage
   
   /*
    * BEGIN INTERACT
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    */
   
   
   public HashMap<String, Integer> hymnnumber = new HashMap<String, Integer>();
   public HashMap<String, Integer> bardtimer = new HashMap<String, Integer>();
   
   public HashMap<String, Integer> spellnumber = new HashMap<String, Integer>();
   
   public HashMap<String, Integer> healtimer = new HashMap<String, Integer>();
   
   public HashMap<String, Integer> cleansetimer = new HashMap<String, Integer>();
   
   public HashMap<String, Integer> slowtimer = new HashMap<String, Integer>();
   
   // berserker
   public HashMap<String, Integer> bcast = new HashMap<String, Integer>();
   public HashMap<String, Integer> headbutttimer = new HashMap<String, Integer>();
   public HashMap<String, Integer> shouttimer = new HashMap<String, Integer>();
   public HashMap<String, Integer> ragetimer = new HashMap<String, Integer>();
   
   
   
   public static HashMap<String, Integer> stuntarget = new HashMap<String, Integer>();
   
   public HashSet<String> bset = new HashSet<String>();
   
   
   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event)
   {
       int strength = 1;
       int speed = 2;
       int regen = 3;
       int protection = 4;
       int fire = 5;
       
       int heal = 1;
       int snare = 2;
       int cleanse = 3;
       
       int currenttime = plugin.getCurrentTime();
       
       
       
       Player player = event.getPlayer();
       
       if (PL.stuntarget.containsKey(player.getName().toLowerCase()) || Paladin.isStunned.contains(player.getName()) || ChaosCrusader.chaosstunned.containsKey(player.getName()))
        {
              event.setCancelled(true);
              player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
              return;
        }
       
        if (Party.ghost.contains(player.getName()))
        {
          event.setCancelled(true);
          player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You can not do that while in ghost form");
          return;
        }
        
       if (stuntarget.containsKey(player.getName().toLowerCase()))
       {
           
           int stuntime = stuntarget.get(player.getName().toLowerCase());
           int totaltime = (currenttime - stuntime);
           
           if (totaltime < 2)
           {
              event.setCancelled(true);
              player.sendMessage(ChatColor.RED + "You have been stunned and cannot interact.");
           }
           else
           {
               stuntarget.remove(player.getName().toLowerCase());
           }
       }
       
       
       if (player.hasPermission("bard.cast"))
       {
           if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
           {
               
               ItemStack inv = player.getInventory().getItemInHand();
               
               if (inv.getTypeId() == 348) // glowstone dust
               {
                   
                   int hymn;
                   
                   if (hymnnumber.containsKey(player.getName().toLowerCase()))
                   {
                       
                      hymn = hymnnumber.get(player.getName().toLowerCase()) + 1;
                      
                      if (hymn > fire)
                      {
                          hymn = 1;
                      }
                      
                   }
                   else
                   {
                      hymn = 1;
                   }
                   
                   if (hymn == strength)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You ready your " + ChatColor.GOLD + "Hymn of Strength" + ChatColor.BLUE + ".");
                   }
                   
                   if (hymn == speed)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You ready your " + ChatColor.GOLD + "Hymn of Speed" + ChatColor.BLUE + ".");
                   }
                   
                   if (hymn == regen)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You ready your " + ChatColor.GOLD + "Hymn of Regeneration" + ChatColor.BLUE + ".");
                   }
                   
                   if (hymn == protection)
                   {
                       
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You ready your " + ChatColor.GOLD + "Hymn of Protection" + ChatColor.BLUE + ".");
                   }
                   
                   if (hymn == fire)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You ready your " + ChatColor.GOLD + "Hymn of Fire Resistance" + ChatColor.BLUE + ".");
                       
                   }
                   
                   hymnnumber.put(player.getName().toLowerCase(), hymn);
               
                   
                   event.setCancelled(true);
               } // end dust
               
               
               // check for clock
               
               if (inv.getTypeId() == 347)
               {
                   int spell;
                   
                   if (spellnumber.containsKey(player.getName().toLowerCase()))
                   {
                       
                      spell = spellnumber.get(player.getName().toLowerCase()) + 1;
                      
                      if (spell > cleanse)
                      {
                          spell = 1;
                      }
                      
                   }
                   else
                   {
                      spell = 1;
                   }
                   
                   if (spell == heal)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You ready your " + ChatColor.GOLD + "Inspiration" + ChatColor.GREEN + " spell.");
                   }
                   
                   if (spell == snare)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You ready your " + ChatColor.RED + "Chilling Melody" + ChatColor.GREEN + " spell.");
                   }
                   
                   if (spell == cleanse)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You ready your " + ChatColor.GOLD + "Cleanse" + ChatColor.GREEN + " spell.");
                   }
                   
                   spellnumber.put(player.getName().toLowerCase(), spell);
                   
               }
               
               
               
           } // end right click
           
           
           if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
           {
               if (player.getInventory().getItemInHand().getTypeId() == 348) // glowstone dust
               {
                   if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null
                     && player.getInventory().getBoots() != null)
                   {                   
                       ItemStack helm = player.getInventory().getHelmet();
                       ItemStack chest = player.getInventory().getChestplate();
                       ItemStack legs = player.getInventory().getLeggings();
                       ItemStack boots = player.getInventory().getBoots(); 
                       
                       if (helm.getTypeId() == 306 && chest.getTypeId() == 307 && legs.getTypeId() == 308 && boots.getTypeId() == 309)
                       {
                           
                           boolean bardtime;
                           
                           if (bardtimer.containsKey(player.getName().toLowerCase()))
                           {
                               
                               int oldtime = bardtimer.get(player.getName().toLowerCase());
                               int totaltime = (currenttime - oldtime);
                               
                               if (totaltime > 15 || totaltime < 0)
                               {
                                   bardtime = false;
                               }
                               else
                               {
                                   bardtime = true;
                                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot cast for another " + ChatColor.AQUA + (15 - totaltime) + ChatColor.RED + " seconds.");
                               }
                           }
                           else
                           {
                               bardtime = false;
                           }
                           
                           if (bardtime == false)
                           {
                               int spellcheck;
                           
                               if (hymnnumber.containsKey(player.getName().toLowerCase()))
                               {
                                   spellcheck = hymnnumber.get(player.getName().toLowerCase());
                               }
                               else
                               {
                                   spellcheck = 1;
                               }
                               
                               PotionEffectType littlepot = PotionEffectType.INCREASE_DAMAGE; // default Strength
                               int duration = 600;
                               int level = 1;
                               String effectname = ChatColor.GREEN + "";
                               
                               if (spellcheck == strength)
                               {                                 
                                   littlepot = PotionEffectType.INCREASE_DAMAGE;
                                   effectname = ChatColor.GREEN + "Hymn of Strength.";
                                   level = 0;
                               }
                               
                               if (spellcheck == speed)
                               {
                                   littlepot = PotionEffectType.SPEED;
                                   effectname = ChatColor.GREEN + "Hymn of Speed.";
                               }
                               
                               if (spellcheck == regen)
                               {
                                   littlepot = PotionEffectType.REGENERATION;
                                   effectname = ChatColor.GREEN + "Hymn of Regeneration.";
                               }
                               
                               if (spellcheck == protection)
                               {
                                   littlepot = PotionEffectType.DAMAGE_RESISTANCE;
                                   effectname = ChatColor.GREEN + "Hymn of Protection.";
                               }
                               
                               if (spellcheck == fire)
                               {
                                   littlepot = PotionEffectType.FIRE_RESISTANCE;
                                   effectname = ChatColor.GREEN + "Hymn of Fire Resistance.";
                                   duration = 400;
                               }
                               
                               
                               
                               bardtimer.put(player.getName().toLowerCase(), currenttime);
                               
                               if (player.hasPotionEffect(littlepot))
                               {
                                   player.removePotionEffect(littlepot);
                                   player.addPotionEffect(new PotionEffect(littlepot, duration, level));
                               }
                               else
                               {
                                   player.addPotionEffect(new PotionEffect(littlepot, duration, level));
                               }

                               player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You buff yourself with " + effectname);
                               
                               if (plugin.party.containsKey(player.getName().toLowerCase()))
                               {
                                   int partymembers = plugin.party.get(player.getName().toLowerCase());
                                   
                                    Location bardloc = player.getLocation();
                                    
                                    World playerworld = player.getWorld();
                                   
                                    for (Player partyplayer : plugin.getServer().getOnlinePlayers())
                                    {
                                        if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                                        {
                                            int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());
                                            if (partyplayers == partymembers && partyplayer != player)
                                            {
                                              Location partyloc = partyplayer.getLocation();
                                              if (partyplayer.getWorld().equals(playerworld))
                                              {
                                                
                                                if (bardloc.distance(partyloc) < HymnRange)
                                                {
                                                    if (partyplayer.hasPotionEffect(littlepot))
                                                    {
                                                        partyplayer.removePotionEffect(littlepot);
                                                        partyplayer.addPotionEffect(new PotionEffect(littlepot, duration, level)); // send buff to all party members
                                                    }
                                                    else
                                                    {
                                                        partyplayer.addPotionEffect(new PotionEffect(littlepot, duration, level)); // send buff to all party members
                                                    }
                                                    
                                                    partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You have been buffed by " + ChatColor.GOLD + player.getDisplayName() + ChatColor.AQUA + "'s " + effectname);   
                                                }
                                                else
                                                {
                                                    partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were too far away to receive " + ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + "'s " + effectname);
                                                }
                                              }
                                              else
                                              {
                                                  partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Due to being in another realm. you didn't receive " + ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + "'s " + effectname);
                                              }
                                            }
                                        }
                                    }
                               }
                               else
                               if (plugin.maHandler != null && plugin.maHandler.isPlayerInArena(player))
                               {
                                   for (Player p : Bukkit.getOnlinePlayers())
                                   {
                                       if (plugin.maHandler.isPlayerInArena(p))
                                       {
                                            World pworld = player.getWorld();
                                            BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());
                                            BlockVector pt2 = BukkitUtil.toVector(p.getLocation().getBlock());
                                            RegionManager regionManager = worldGuard.getRegionManager(pworld);

                                            boolean inSame = false;

                                            Iterator<ProtectedRegion> regions = worldGuard.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).iterator();
                                            Iterator<ProtectedRegion> regions2 = worldGuard.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation()).iterator();
                                            HashMap<String, ProtectedRegion> hm1 = new HashMap<String, ProtectedRegion>();

                                            while (regions.hasNext())
                                            {
                                                ProtectedRegion reg = regions.next();
                                                hm1.put(player.getName(), reg);
                                            }

                                            while (regions2.hasNext())
                                            {
                                                ProtectedRegion reg = regions2.next();
                                                if (hm1.containsKey(player.getName()))
                                                {
                                                    for (ProtectedRegion ls : hm1.values())
                                                    {
                                                        if (reg.equals(ls))
                                                        {
                                                            inSame = true;
                                                        }
                                                    }
                                                }
                                            }

                                            if (inSame)
                                            {
                                                hm1.remove(player.getName());
                                               if (p.getWorld().equals(player.getWorld()))
                                               {
                                                   if (p != player)
                                                   {
                                                       if (p.getLocation().distance(player.getLocation()) < HymnRange)
                                                       {
                                                           if (p.hasPotionEffect(littlepot))
                                                           {
                                                               p.removePotionEffect(littlepot);
                                                               p.addPotionEffect(new PotionEffect(littlepot, duration, level)); // send buff to all party members
                                                           }
                                                           else
                                                           {
                                                               p.addPotionEffect(new PotionEffect(littlepot, duration, level)); // send buff to all party members
                                                           }

                                                           p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You have been buffed by " + ChatColor.GOLD + player.getDisplayName() + ChatColor.AQUA + "'s " + effectname);   
                                                       }
                                                       else
                                                       {
                                                           p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were too far away to receive " + ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + "'s " + effectname);
                                                       }
                                                   }
                                               }
                                           }
                                            else
                                            {
                                                hm1.remove(player.getName());
                                            }
                                       }
                                   }
                               }
                               
                               
                               
                           }
                           
                       }
                       else
                       {
                           player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing full iron armor to cast spells.");
                       }
                   }
                   else
            
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing full iron armor to cast spells.");
                   }   
               }
               
               // clock (bard spells)
               
               if (player.getInventory().getItemInHand().getTypeId() == 347)
               {
                   if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null
                     && player.getInventory().getBoots() != null)
                   {                   
                       ItemStack helm = player.getInventory().getHelmet();
                       ItemStack chest = player.getInventory().getChestplate();
                       ItemStack legs = player.getInventory().getLeggings();
                       ItemStack boots = player.getInventory().getBoots(); 
                       
                       if (helm.getTypeId() == 306 && chest.getTypeId() == 307 && legs.getTypeId() == 308 && boots.getTypeId() == 309)
                       {

                          
                           int spellcheck;
                           
                           if (spellnumber.containsKey(player.getName().toLowerCase()))
                           {
                                   spellcheck = spellnumber.get(player.getName().toLowerCase());
                           }
                           else
                           {
                                   spellcheck = 1;
                           }
                           
                          
                           
                           if (spellcheck == 1) // if heal
                           {
                               boolean healcheck;
                               
                               if (healtimer.containsKey(player.getName().toLowerCase()))
                               {
                                   int healold = healtimer.get(player.getName().toLowerCase());
                                   
                                   int healtotal = (currenttime - healold);
                                   
                                   if (healtotal > inspireCD || healtotal < 0) // 30 sec cooldown
                                   {
                                       healcheck = false;
                                       healtimer.remove(player.getName().toLowerCase());
                                   }
                                   else
                                   {
                                       healcheck = true;
                                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot cast " + ChatColor.GOLD + "Inspiration " + ChatColor.RED + "again for " + ChatColor.AQUA + (inspireCD - healtotal) + ChatColor.RED + " seconds.");
                                   }
                                   
                               }
                               else
                               {
                                   healcheck = false;
                               }
                               
                               if (healcheck == false)
                               {
                                   healtimer.put(player.getName().toLowerCase(), currenttime);
                                   
                                   double healplayer = player.getHealth() + inspireAmount;
                                   
                                   if (healplayer >= player.getMaxHealth())
                                   {
                                       healplayer = player.getMaxHealth();
                                   }
                                   
                                   player.setHealth(healplayer);
                                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You heal yourself for " + ChatColor.GOLD + (inspireAmount / 2) + ChatColor.GREEN + " hearts.");
                                   
                                   if (plugin.party.containsKey(player.getName().toLowerCase()))
                                   {
                                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You " + ChatColor.GOLD + "Inspire" + ChatColor.GREEN + " your party, healing them for " + ChatColor.GOLD + inspirePartyAmount + ChatColor.GREEN + " hearts.");
                                       
                                       int partymembers = plugin.party.get(player.getName().toLowerCase());
                                   
                                       Location bardloc = player.getLocation();
                                    
                                    
                                       World playerworld = player.getWorld();
                                       
                                       double partyhealth;
                                   
                                       for (Player partyplayer : plugin.getServer().getOnlinePlayers())
                                       {
                                           if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                                           {
                                             
                                               int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());
                                           
                                               if (partyplayers == partymembers && partyplayer != player)
                                          
                                               {
                                             
                                                   Location partyloc = partyplayer.getLocation();
                                                if (partyplayer.getWorld().equals(playerworld))
                                                {
                                              
                                                   if (bardloc.distance(partyloc) < 25.0)
                                              
                                                   {
                                                
                                                       partyhealth = partyplayer.getHealth() + inspirePartyAmount;
                                                       if (partyhealth >= partyplayer.getMaxHealth())
                                                       {
                                                         partyhealth = partyplayer.getMaxHealth();   
                                                       }
                                                       
                                                       partyplayer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 0));
                                                       
                                                       partyplayer.setHealth(partyhealth);
                                                       partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You have been " + ChatColor.GOLD + "Inspired " + ChatColor.GREEN + "by " + ChatColor.GOLD + player.getDisplayName() + ".");   
                                              
                                                   }
                                              
                                                   else
                                               
                                                   {
                                                
                                                       partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were too far away to receive " + ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + "'s " + ChatColor.GOLD + "Inspiration!");
                                             
                                                   } 
                                                }
                                          
                                               }
                                      
                                           }
                                   
                                       }
                                   
                                   }
                                   else
                                   {
                                       if (plugin.maHandler != null && plugin.maHandler.isPlayerInArena(player))
                                        {
                                            for (Player p : Bukkit.getOnlinePlayers())
                                            {
                                                if (plugin.maHandler.isPlayerInArena(p))
                                                {
                                                    World pworld = player.getWorld();
                                                    BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());
                                                    BlockVector pt2 = BukkitUtil.toVector(p.getLocation().getBlock());
                                                    RegionManager regionManager = worldGuard.getRegionManager(pworld);

                                                    boolean inSame = false;

                                                    Iterator<ProtectedRegion> regions = worldGuard.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).iterator();
                                                    Iterator<ProtectedRegion> regions2 = worldGuard.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation()).iterator();
                                                    HashMap<String, ProtectedRegion> hm1 = new HashMap<String, ProtectedRegion>();

                                                    while (regions.hasNext())
                                                    {
                                                        ProtectedRegion reg = regions.next();
                                                        hm1.put(player.getName(), reg);
                                                    }

                                                    while (regions2.hasNext())
                                                    {
                                                        ProtectedRegion reg = regions2.next();
                                                        if (hm1.containsKey(player.getName()))
                                                        {
                                                            for (ProtectedRegion ls : hm1.values())
                                                            {
                                                                if (reg.equals(ls))
                                                                {
                                                                    inSame = true;
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (inSame)
                                                    {
                                                        hm1.remove(player.getName());
                                                        if (p.getWorld().equals(player.getWorld()))
                                                        {
                                                            if (p != player)
                                                            {
                                                                if (p.getLocation().distance(player.getLocation()) < HymnRange)
                                                                {
                                                                    double phealth = p.getHealth() + inspirePartyAmount;
                                                                    if (phealth >= p.getMaxHealth())
                                                                    {
                                                                        phealth = p.getMaxHealth();   
                                                                    }

                                                                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 0));
                                                                    
                                                                    p.setHealth(phealth);
                                                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You have been " + ChatColor.GOLD + "Inspired " + ChatColor.GREEN + "by " + ChatColor.GOLD + player.getDisplayName() + ".");   
                                                                }
                                                                else
                                                                {
                                                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were too far away to receive " + ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + "'s Inspiration!");
                                                                }
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        hm1.remove(player.getName());
                                                    }
                                                }
                                            }
                                        }
                                   }
                                      
                                   
                               }
                             
                           } // END HEAL
                           
                           if (spellcheck == 2) // start snare
                           {
                               boolean snaretimer;
                               
                               World pworld = player.getWorld();

                                BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                                RegionManager regionManager = worldGuard.getRegionManager(pworld);
                                ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                                if (!(set.allows(DefaultFlag.PVP)))
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                                    return;
                                }
                               
                               if (slowtimer.containsKey(player.getName().toLowerCase()))
                               {
                                   int oldsnare = slowtimer.get(player.getName().toLowerCase());
                                   
                                   int totalsnare = (currenttime - oldsnare);
                                   
                                   if (totalsnare > melodyCD || totalsnare < 0)
                                   {
                                       snaretimer = false;
                                   }
                                   else
                                   {
                                       snaretimer = true;
                                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot cast " + ChatColor.GOLD + "Chilling Melody " + ChatColor.RED + "again for " + ChatColor.AQUA + (melodyCD - totalsnare) + ChatColor.RED + " seconds.");
                                   }
                               }
                               else
                               {
                                   snaretimer = false;
                               }
                               
                               if (snaretimer == false)
                               {
                                   Location bardlocation = player.getLocation();
                                   int bardparty = -1;
                                   
                                   if (plugin.party.containsKey(player.getName().toLowerCase()))
                                   {
                                       bardparty = plugin.party.get(player.getName().toLowerCase());
                                   }
                                   
                                   
                                   boolean applysnare;
                                   slowtimer.put(player.getName().toLowerCase(), currenttime);
                                   
                                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You cast " + ChatColor.GOLD + "Chilling Melody" + ChatColor.GREEN + ", slowing all nearby enemies.");
                                   
                                   World playerworld = player.getWorld();
                                   
                                   for (Player partyplayer : plugin.getServer().getOnlinePlayers())
                                   {
                                       
                                       Location partyplayerloc = partyplayer.getLocation();
                                       
                                     if (partyplayer.getWorld().equals(playerworld))
                                     { 
                                       
                                       if (bardlocation.distance(partyplayerloc) < 16)
                                       {
                                         if (partyplayer != player)
                                         {
                                           if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                                           {
                                               int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());
                                               
                                               if (partyplayers == bardparty)
                                               {
                                                 applysnare = false;
                                               }
                                               else
                                               {
                                                  applysnare = true;
                                               }
                                               
                                           }
                                           else
                                           {
                                               applysnare = true;
                                           }
                                           
                                           if (applysnare == true)
                                           {
                                               partyplayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 4));
                                               partyplayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 120, 4));
                                               partyplayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 0));
                                               partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been snared by " + ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + "'s " + ChatColor.GOLD + "Chilling Melody.");
                                           }
                                           
                                         }

                                       }
                                     }
                                   }
                                       
                                               
                                   
                               }
                               
                           } // end Chilling Melody
                           
                           if (spellcheck == 3) // start Cleanse
                           {
                               
                               
                               boolean purgetimer;
                               
                               if (cleansetimer.containsKey(player.getName().toLowerCase()))
                               {
                                   
                                   int cleanseold = cleansetimer.get(player.getName().toLowerCase());
                                   int totalpurge = (currenttime - cleanseold);
                                   
                                   if (totalpurge > cleanseCD || totalpurge < 0)
                                   {
                                       
                                       purgetimer = false;
                                   }
                                   else
                                   {
                                       purgetimer = true;
                                       
                                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot cast " + ChatColor.GOLD + "Cleanse " + ChatColor.RED + "again for " + ChatColor.AQUA + (cleanseCD - totalpurge) + ChatColor.RED + " seconds.");
                                   }
                                   
                               }
                                       
                               else
                               {
                                   
                                   purgetimer = false;
                               }
                               
                               if (purgetimer == false)
                               {
                                   cleansetimer.put(player.getName().toLowerCase(), currenttime);
                                   
                                   
                                   
                                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GRAY + "[SR] " + ChatColor.GREEN + "You cleanse yourself of all negative effects, also granting increased regeneration for short duration.");
                                   
                                   int nofire = 0;
                                   
                                   player.setFireTicks(nofire);
                                   
                                    Collection<PotionEffect> effects = player.getActivePotionEffects();
                                    
                                    /*
                                   if (player.hasPotionEffect(PotionEffectType.REGENERATION))
                                   {
                                       player.removePotionEffect(PotionEffectType.REGENERATION);
                                       player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
                                   }
                                   else
                                   {
                                       player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
                                   }
                                   */
                                                                     
                                   if (player.hasPotionEffect(PotionEffectType.SLOW))
                                   {
                                       player.removePotionEffect(PotionEffectType.SLOW);
                                       player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 0));
                                   }
                                   
                                   if (player.hasPotionEffect(PotionEffectType.BLINDNESS))
                                   {
                                       player.removePotionEffect(PotionEffectType.BLINDNESS);
                                       player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 0));
                                   }
                                   
                                   if (player.hasPotionEffect(PotionEffectType.CONFUSION))
                                   {
                                       player.removePotionEffect(PotionEffectType.CONFUSION);
                                       player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 0, 0));
                                   }
                                   
                                   if (player.hasPotionEffect(PotionEffectType.POISON))
                                   {
                                       player.removePotionEffect(PotionEffectType.POISON);
                                       player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 0, 0));
                                   }
                                   
                                   if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
                                   {
                                       player.removePotionEffect(PotionEffectType.WEAKNESS);
                                       player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 0, 0));
                                   }
                                   
                                   
                               }
                               
                           }
                           
                           
                              
                               
                        
                       }
                       else
                       {
                           player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot cast unless you are in full iron armor.");
                       }
                               
                           
                     
                    
                   }
                                         
                   else
                   {
                          
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot cast unless you are in full iron armor.");
                      
                   }
                           
                           
                           
                       
                  
                 
               }
                   
             
           }
           
         
       } // END BARD CLASS
       
       
       // BEGIN BERSERKER
       
       
        int stunshout = 1;
        int rage = 2;
        

        
        if (player.hasPermission("berserker.use"))
        { 
           if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
           {
               
               ItemStack inv = player.getInventory().getItemInHand();
               
               if (inv.getTypeId() == 261 && (!(player.isOp() || player.hasPermission("berserker.bypass"))))
               {
                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Berserkers cannot use bows.");
                   event.setCancelled(true);
                   return;
               }
               
               if (inv.getTypeId() == 367) // if rotten flesh
               {
                   
                   int shout;
                   
                   if (bcast.containsKey(player.getName().toLowerCase()))
                   {
                       
                      shout = bcast.get(player.getName().toLowerCase()) + 1;
                      
                      if (shout > rage)
                      {
                          shout = 1;
                      }
                      
                   }
                   else
                   {
                      shout = 1;
                   }
                   
                   if (shout == stunshout)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You ready your " + ChatColor.RED + "Stunning Shout" + ChatColor.GREEN + ".");
                   }
                   
                   if (shout == rage)
                   {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You ready your " + ChatColor.RED + "Berserker Rage" + ChatColor.GREEN + ".");
                   }
                   
                   bcast.put(player.getName().toLowerCase(), shout);
               
                  
               } // end rotten flesh right click
               
               // begin right click with axe
               
               if (inv.getTypeId() == 258 || inv.getTypeId() == 271 || inv.getTypeId() == 279 || inv.getTypeId() == 286 || inv.getTypeId() == 275) // if axe
               {
                   // do stun thing
                   boolean htimer;
                  
                   if (headbutttimer.containsKey(player.getName().toLowerCase()))
                   
                   {
                   
                       int oldtimer = headbutttimer.get(player.getName().toLowerCase());
                       int totaltimer = (currenttime - oldtimer);
                       
                       if (totaltimer > 35 || totaltimer < 0)
                       {
                           htimer = false;  
                       }
                               
                       else
                              
                       {
                           htimer = true;
                           player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot " + ChatColor.GOLD + "Headbutt " + ChatColor.RED + "again for " + ChatColor.AQUA + (35 - totaltimer) + ChatColor.RED + " seconds.");
                       }
                         
                   }
                            
                   else
                   {
                       htimer = false;
                   }
                               
                   if (htimer == false)
                   {
                                   
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You ready your " + ChatColor.GOLD + "Headbutt" + ChatColor.GREEN + ".");
                       bset.add(player.getName().toLowerCase());
                   }
                   
                   
               } // end headbutt ability with axe

           } // end right click
           
           if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
           {
              if (player.getInventory().getItemInHand().getTypeId() == 367) // if zombie flesh
               {
                   if (player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null)
                   {
                       ItemStack chest = player.getInventory().getChestplate();
                       ItemStack legs = player.getInventory().getLeggings();
                       
                       
                       if (chest.getTypeId() == 299 && legs.getTypeId() == 300)
                       {
                         int shoutcheck = 1;
                         
                         if (bcast.containsKey(player.getName().toLowerCase()))
                         {
                             shoutcheck = bcast.get(player.getName().toLowerCase());
                         }
                         
                         boolean shout;
                       
                         if (shoutcheck == 1)
                         {
                             World pworld = player.getWorld();

                            BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                            RegionManager regionManager = worldGuard.getRegionManager(pworld);
                            ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                            if (!(set.allows(DefaultFlag.PVP)))
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                                return;
                            }
                            
                             if (shouttimer.containsKey(player.getName().toLowerCase()))
                             {
                             
                                 int oldsnare = shouttimer.get(player.getName().toLowerCase());
                             
                                 int totalsnare = (currenttime - oldsnare);
                                   
                                 if (totalsnare > 45 || totalsnare < 0)
                                 {
                                     shout = false;
                                 }
                                      else
                                      {
                                          shout = true;
                                         player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot " + ChatColor.GOLD + "Stunning Shout " + ChatColor.RED + "again for " + ChatColor.AQUA + (45 - totalsnare) + ChatColor.RED + " seconds.");
                                      }
                                  }
                                  else
                                  {
                                      shout = false;
                                  }
                               
                                  if (shout == false)
                                  {
                                      Location bardlocation = player.getLocation();
                                      int bardparty = -1;
                                    
                                      if (plugin.party.containsKey(player.getName().toLowerCase()))
                                      {
                                       bardparty = plugin.party.get(player.getName().toLowerCase());
                                      }
                                   
                                   
                                      boolean applysnare;
                                      shouttimer.put(player.getName().toLowerCase(), currenttime);
                                   
                                      player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You " + ChatColor.GOLD + "Stunning Shout" + ChatColor.GREEN + ", stunning all nearby enemies.");
                                      
                                      
                                      World playerworld = player.getWorld();
                                     
                                    for (Player partyplayer : plugin.getServer().getOnlinePlayers())
                                    {
                                       
                                      Location partyplayerloc = partyplayer.getLocation();
                                     if (partyplayer.getWorld().equals(playerworld))
                                     {  
                                          
                                       if (bardlocation.distance(partyplayerloc) < 10)
                                       {
                                         if (partyplayer != player)
                                         {
                                               
                                           
                                           if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                                           {
                                               int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());
                                               
                                               if (partyplayers == bardparty)
                                               {
                                                 applysnare = false;
                                               }
                                               else
                                               {
                                                  applysnare = true;
                                               }
                                               
                                           }
                                           else
                                           {
                                               applysnare = true;
                                           }
                                           
                                           if (applysnare == true)
                                           {
                                               
                                               boolean canStun = false;
                                               
                                                if (stunImmune.containsKey(partyplayer.getName()))
                                                {
                                                    int timecheck = stunImmune.get(partyplayer.getName());
                                                    int totalcheck = currenttime - timecheck;

                                                    if (totalcheck > StunImmune || totalcheck < 0)
                                                    {
                                                        canStun = true;
                                                        stunImmune.remove(partyplayer.getName());
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
                                                    stuntarget.put(partyplayer.getName().toLowerCase(), currenttime);
                                               
                                                    partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned by " + ChatColor.GOLD + player.getDisplayName() 
                                                            + ChatColor.RED + "'s " + ChatColor.GOLD + "Stunning Shout.");
                                                    
                                                    stunImmune.put(partyplayer.getName(), currenttime);
                                                }
                                               
                                               
                                           }
                                                   
                                                       
                                                   
                                       
                                         }
                                        }

                                       }
                                   }
                                  }
                                       
                                               
                                   
                               } // stun shout end
                         
                         
                         if (shoutcheck == 2)
                         {
                             
                       
                             if (ragetimer.containsKey(player.getName().toLowerCase()))
                         
                             {
                             
                                 int oldsnare = ragetimer.get(player.getName().toLowerCase());
                             
                                 int totalsnare = (currenttime - oldsnare);
                                   
                                 if (totalsnare > RageCD || totalsnare < 0)
                                 {
                                     shout = false;
                                 }
                                 else
                                 {
                                     shout = true;
                                     player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot " + ChatColor.GOLD + "Rage " + ChatColor.RED + "again for " + ChatColor.AQUA + (RageCD - totalsnare) + ChatColor.RED + " seconds.");
                                 }
                                  
                             }
                             else
                           
                             {
                                shout = false;
                                 
                             }
                               
                                  
                             if (shout == false)
                             {
                                
                                 if (player.hasPotionEffect(PotionEffectType.SLOW))
                                 {
                                     player.removePotionEffect(PotionEffectType.SLOW);
                                     player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 0));
                                 }
                                 
                                 if (player.hasPotionEffect(PotionEffectType.POISON))
                                 {
                                     player.removePotionEffect(PotionEffectType.POISON);
                                     player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 0, 0));
                                 }
                                 
                                 if (player.hasPotionEffect(PotionEffectType.SPEED))
                                 {
                                     player.removePotionEffect(PotionEffectType.SPEED);
                                 }
                                 
                                 if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
                                 {
                                     player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                 }
                                 
                                 player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, RageBuffDur, 3));
                                 player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, RageBuffDur, 0));
                                 player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,RageBuffDur,0));
                                 player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,RageBuffDur,0));
                                 
                                 inRage.add(player.getName());
                                 new Rage(player);
                                 ragetimer.put(player.getName().toLowerCase(), currenttime);
                                 
                                 player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You go into a " + ChatColor.RED + "Berserker Rage" + ChatColor.GREEN + "!");
                                 
                             }
                         
                               
                           
                         } // end Rage
                           
                           
                           
                       }
                       else
                       {
                           player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing " + ChatColor.GOLD + "Leather Chest & Legs" + ChatColor.RED + " to shout.");
                       }
                       
                   }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing " + ChatColor.GOLD + "Leather Chest & Legs" + ChatColor.RED + " to shout.");
                    }
               }
           }
  
        }// end berserker

   }  // end interact
            
   @EventHandler
   public void onEntityDeath (EntityDeathEvent event)
   {
       Entity entity = event.getEntity();
       
       
       
       EntityDamageEvent killer = entity.getLastDamageCause();
       
       if (entity instanceof Player)
       {
           Player p = (Player) entity;
           
           if (inRage.contains(p.getName()))
           {
               inRage.remove(p.getName());
           }
       }
       
       if (killer instanceof Player)
       {
           Player p = (Player) killer;
           
           if (p.hasPermission("nightlord.cast"))
           {
               long time = p.getWorld().getTime();
               
               if (time < 23500 && time > 12500)
               {
                   double heal = p.getHealth() + 4;
                   if (heal > p.getMaxHealth())
                   {
                       p.setHealth(p.getMaxHealth());
                   }
                   else
                   {
                       p.setHealth(heal);
                   }
                   
                   p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "The Darkness rewards your strength.");
               }
           }
           if (inRage.contains(p.getName()))
           {
               double phealth = p.getHealth();
               
               if ((phealth + RageKillHeal) > p.getMaxHealth())
               {
                   p.setHealth(p.getMaxHealth());
               }
               else
               {
                   p.setHealth(phealth + RageKillHeal);
               }
               
               p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Your kill fuels your bloodlust, healing you for 2 hearts!");
           }
       }
   }
   
   private class Rage implements Runnable
    {
        Player player;
        int taskID;
        
        int x;
        
        public Rage(Player player)
        {
            x = 0;
            this.player = player;
            this.taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
        }

        @Override
        public void run() 
        {
            x++;
            
            
            if (x > (RageDur + 5))
            {
                if (inRage.contains(player.getName()))
                {
                    inRage.remove(player.getName());
                }

                Party.log.log(Level.WARNING, "Berserker Rage task ran longer than expected. Killing task ID " + taskID);
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                return;
            }
            
            if (inRage.contains(player.getName()))
            {
                if (x > RageDur)
                {
                    inRage.remove(player.getName());
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Rage has ended.");
                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                }

                if (x <= RageDur)
                {
                    if (player.hasPotionEffect(PotionEffectType.SLOW))
                    {
                        player.removePotionEffect(PotionEffectType.SLOW);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,0,0));
                    }
                    
                    if (player.hasPotionEffect(PotionEffectType.BLINDNESS))
                    {
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,0,0));
                    }
                    
                    if (player.hasPotionEffect(PotionEffectType.POISON))
                    {
                        player.removePotionEffect(PotionEffectType.POISON);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,0,0));
                    }
                }
            }
            else
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Rage has ended.");
                Bukkit.getServer().getScheduler().cancelTask(taskID);
            }

        }
        
    }
   
}


               
               
                                      
           
     

       
       

       
       

 
    
    


