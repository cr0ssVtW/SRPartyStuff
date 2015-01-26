package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * @author Cross
 */
public class ShadowKnight implements Listener
{
    public Party plugin;
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    
    public ShadowKnight (Party plugin)
    {
        this.plugin = plugin;
    }
    
    // Cooldowns
    public int SiphonCD = 30;
    public int CocoonCD = 30;
    public int GraspCD = 18;
    public int DarknessCD = 45;
    
    // Values
        //Siphon Boosts
    public int durBoost = 200; // 10 seconds
    //public int ampBoost = 1; // +1 power
    
        //Durations
    
    public int SiphonTimes = 2;
    public int CocoonDur = 4;
    public int GraspDur = 4;
    public int DarknessDur = 200; // ticks
    
        //Life to steal
    public int SiphonSteal = 2;
    public int CocoonSteal = 2;
    public int CocoonDmg = 1;
    public double LeechAmount = 1;
    
        // Distance from player checks
    public int GraspDis = 15;
    public int CocoonDis = 12;
    public int SiphonDis = 15;
    public int SiphonPDis = 25;
    public int DarknessDis = 20;
    
    //Hashes
        // Elapsed time for Cocoon check
    public HashMap<String, Integer> CocoonElapse = new HashMap<String, Integer>();
        // Task ID to pull for cancellation (player.getName() and TaskID)
    public HashMap<String, Integer> CocoonID = new HashMap<String, Integer>();
    
        //CD Hashes
    public HashMap<String, Integer> CocoonTimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> SiphonTimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> GraspTimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> DarknessTimer = new HashMap<String, Integer>();
    
    
    public HashSet<String> isGrabbed = new HashSet<String>();
    public HashMap<String, Location> GraspLoc = new HashMap<String, Location>();
    
    public static HashSet<String> isInvis = new HashSet<String>();
    


    @EventHandler
    public void onDeath(EntityDeathEvent event)
    {
        Entity e = event.getEntity();
        if (e instanceof Player)
        {
            Player p = (Player) e;
            
            if (isGrabbed.contains(p.getName()))
            {
                isGrabbed.remove(p.getName());
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage (EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player))
        {
            return;
        }
        
        if (!(event instanceof EntityDamageByEntityEvent))
        {
            return;
        }
        
        EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent) event;
        
        if (!(event_EE.getDamager() instanceof Player))
        {
            return;
        }
        
        
        Player target = (Player) event.getEntity();
        Player attacker = (Player) event_EE.getDamager();
        
        if (PL.stuntarget.containsKey(attacker.getName().toLowerCase()) || Paladin.isStunned.contains(attacker.getName()) || ChaosCrusader.chaosstunned.containsKey(attacker.getName()))
        {
            event.setCancelled(true);
            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
            return;
        }
        
        if (Party.ghost.contains(attacker.getName()))
        {
            event.setCancelled(true);
            event.setDamage(0);
            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Ghosts cannot attack!");
            return;
        }
        
        
        if (isInvis.contains(attacker.getName()))
        {
            if (!(target.hasPotionEffect(PotionEffectType.BLINDNESS)))
            {
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,40,2));
                target.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + attacker.getName() + "'s " + ChatColor.DARK_PURPLE + "Darkness blinds you!");
                attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "Your Darkness blinds " + ChatColor.GOLD + target.getName() + "!");
            }
            
            if (attacker.getNoDamageTicks() < target.getMaximumNoDamageTicks()/3.0F)
            {
                double heal = attacker.getHealth() + LeechAmount;
                
                int targetparty = -1;               
                int myparty = -2;

                if (plugin.party.containsKey(attacker.getName().toLowerCase()))
                {
                    myparty = plugin.party.get(attacker.getName().toLowerCase());
                }
                
                if (heal > attacker.getMaxHealth())
                {
                    attacker.setHealth(attacker.getMaxHealth());
                }
                else
                {
                    attacker.setHealth(heal);
                }
                
                for (Player p : plugin.getServer().getOnlinePlayers())
                {
                  World pworld = p.getWorld();
                  if (attacker.getWorld().equals(pworld)) 
                  {
                    Location ploc = p.getLocation();
                    if (ploc.distance(attacker.getLocation()) < 25.0)
                    {
                        if (plugin.party.containsKey(p.getName().toLowerCase()))
                        {
                            targetparty = plugin.party.get(p.getName().toLowerCase());
                        }
                        
                        if (myparty == targetparty && p != attacker)
                        {
                            double pheal = p.getHealth() + LeechAmount;
                            
                            if (pheal > p.getMaxHealth())
                            {
                                p.setHealth(p.getMaxHealth());
                            }
                            else
                            {
                                p.setHealth(pheal);
                            }
                        }
                    }
                  }
                }
                
            }
        }
        
        
    }
    
    @EventHandler
    public void onPlayerRespawn (PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,0,0));
        
    }
    
    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event)
    {
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
        
        if (player.hasPermission("sr.shadowknight.pvp"))
        {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                if (!(player.getItemInHand().getTypeId() == 352 || player.getItemInHand().getTypeId() == 352 || player.getItemInHand().getTypeId() == 268 
                        || player.getItemInHand().getTypeId() == 267 || player.getItemInHand().getTypeId() == 276 || player.getItemInHand().getTypeId() == 283 
                        || player.getItemInHand().getTypeId() == 370))
                {
                    return;
                }
                
                if (player.getInventory().getChestplate() == null || player.getInventory().getLeggings() == null)
                {
                    return;
                }
                
                if ((player.getInventory().getChestplate().getTypeId() == 307 && player.getInventory().getLeggings().getTypeId() == 308) 
                        || player.isOp() || player.hasPermission("sr.shadowknight.bypass")) // if iron chest/legs
                {
                    int time = plugin.getCurrentTime();
                    
                    if (player.getItemInHand().getTypeId() == 352) // if bone
                    {
                        if (player.isSneaking())
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
                            
                            // do grasp
                            if (GraspTimer.containsKey(player.getName()))
                            {
                                int timecheck = GraspTimer.get(player.getName());
                                int totaltime = time - timecheck;
                                
                                if ((totaltime > GraspCD) || (totaltime < 0))
                                {
                                    assert player != null;
                                    Entity target = null;
                                    Player target2 = null;

                                    double targetDistanceSquared = 0;
                                    final double radiusSquared = 1;
                                    final Vector l = player.getEyeLocation().toVector(), n = player.getLocation().getDirection().normalize();
                                    final double cos45 = Math.cos(Math.PI / 4);

                                    for (final LivingEntity other : player.getWorld().getEntitiesByClass(LivingEntity.class))
                                    {
                                        if (other == player)
                                            continue;
                                        if (target == null || targetDistanceSquared > other.getLocation().distanceSquared(player.getLocation()))
                                        {
                                          final Vector t = other.getLocation().add(0, 1, 0).toVector().subtract(l);
                                            if (n.clone().crossProduct(t).lengthSquared() < radiusSquared && t.normalize().dot(n) >= cos45)
                                            {
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
                                       
                                       

                                       target2 = (Player) target;

                                       Location ploc = player.getLocation();
                                       Location tloc = target2.getLocation();

                                       if (ploc.distance(tloc) > GraspDis)
                                       {
                                           player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target out of range.");
                                           return;
                                       }

                                       // do grasp

                                       if (isGrabbed.contains(target2.getName()))
                                       {
                                           player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "The Shadows already have that player...");
                                           return;
                                       }
                                       
                                       GraspTimer.put(player.getName(), time);
                                       
                                       isGrabbed.add(target2.getName());
                                       GraspLoc.put(target2.getName(), tloc);
                                       for (Player p : Bukkit.getOnlinePlayers())
                                       {
                                           if (p.getWorld().equals(target2.getWorld()))
                                           {
                                               if (p.getLocation().distance(tloc) <= GraspDis)
                                               {
                                                   if (p != player && p != target2)
                                                   {
                                                       p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s " 
                                                               + ChatColor.DARK_PURPLE + "shadows Grasp " + ChatColor.GOLD + target2.getName() + "!");
                                                   }
                                               }
                                           }
                                       }

                                     //  Packet62NamedSoundEffect packet = new Packet62NamedSoundEffect("ambient.cave.cave3", tloc.getBlockX(), tloc.getBlockY(), tloc.getBlockZ(), 1.0F, 1.0F);
                                     //  ((CraftPlayer)target2).getHandle().playerConnection.sendPacket(packet);
                                       target2.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,120,3));

                                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "You Grasp " + ChatColor.GOLD + target2.getName() + "!");
                                       target2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.DARK_PURPLE + " shadows Grasp you!");

                                       new Grasp(target2);

                                    }
                                    else
                                    {
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must have a target.");
                                        return;
                                    }
                                }
                                else
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + (GraspCD - totaltime) + " seconds before casting Shadow Grasp!");
                                    return;
                                }
                            }
                            else // if not in timer hash
                            {
                                
                                
                                assert player != null;
                                Entity target = null;
                                Player target2 = null;

                                double targetDistanceSquared = 0;
                                final double radiusSquared = 1;
                                final Vector l = player.getEyeLocation().toVector(), n = player.getLocation().getDirection().normalize();
                                final double cos45 = Math.cos(Math.PI / 4);

                                for (final LivingEntity other : player.getWorld().getEntitiesByClass(LivingEntity.class))
                                {
                                    if (other == player)
                                        continue;
                                    if (target == null || targetDistanceSquared > other.getLocation().distanceSquared(player.getLocation()))
                                    {
                                      final Vector t = other.getLocation().add(0, 1, 0).toVector().subtract(l);
                                        if (n.clone().crossProduct(t).lengthSquared() < radiusSquared && t.normalize().dot(n) >= cos45)
                                        {
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

                                   
                                   
                                   
                                   target2 = (Player) target;

                                   Location ploc = player.getLocation();
                                   Location tloc = target2.getLocation();

                                   if (ploc.distance(tloc) > GraspDis)
                                   {
                                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target out of range.");
                                       return;
                                   }

                                   // do grasp

                                   if (isGrabbed.contains(target2.getName()))
                                   {
                                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "The Shadows already have that player...");
                                       return;
                                   }
                                   
                                   GraspTimer.put(player.getName(), time);
                                   isGrabbed.add(target2.getName());
                                   GraspLoc.put(target2.getName(), tloc);
                                   for (Player p : Bukkit.getOnlinePlayers())
                                   {
                                       if (p.getWorld().equals(target2.getWorld()))
                                       {
                                           if (p.getLocation().distance(tloc) <= GraspDis)
                                           {
                                               if (p != player && p != target2)
                                               {
                                                   p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s " 
                                                           + ChatColor.DARK_PURPLE + "shadows Grasp " + ChatColor.GOLD + target2.getName() + "!");
                                               }
                                           }
                                       }
                                   }

                                   
                                  // Packet62NamedSoundEffect packet = new Packet62NamedSoundEffect("ambient.cave.cave3", tloc.getBlockX(), tloc.getBlockY(), tloc.getBlockZ(), 1.0F, 1.0F);
                                  // ((CraftPlayer)target2).getHandle().playerConnection.sendPacket(packet);
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,120,3));
                                   
                                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "You Grasp " + ChatColor.GOLD + target2.getName() + "!");
                                   target2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.DARK_PURPLE + " shadows Grasp you!");

                                   new Grasp(target2);

                                }
                                else
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must have a target.");
                                    return;
                                }

                            }
                            
                               
                        }
                        else // if not sneaking.. do siphon
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
                            // do siphon
                            
                            boolean canSiphon = false;
                            
                            if (SiphonTimer.containsKey(player.getName()))
                            {
                                int stimer = SiphonTimer.get(player.getName());
                                int totaltime = time - stimer;
                                
                                if ((totaltime > SiphonCD) || (totaltime < 0))
                                {
                                    canSiphon = true;
                                    SiphonTimer.remove(player.getName());
                                }
                                else
                                {
                                    canSiphon = false;
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + (SiphonCD - totaltime) + " seconds before you can Siphon.");
                                    return;
                                }
                            }
                            else
                            {
                                canSiphon = true;
                            }
                            
                            if (canSiphon)
                            {
                                SiphonTimer.put(player.getName(), time);
                                
                                Block targetBlock = player.getTargetBlock(null, 100);
                                Location blockLoc = targetBlock.getLocation();

                                if (player.getLocation().distance(blockLoc) > SiphonPDis)
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are too far away to reach the target area.");
                                    return;
                                }

                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "You Siphon the targeted area!");

                                

                                int targetparty = -1;
                                int myparty = -2;

                                if (plugin.party.containsKey(player.getName().toLowerCase()))
                                {
                                    myparty = plugin.party.get(player.getName().toLowerCase());
                                }

                                int count = 0;
                                
                                for (Player target : plugin.getServer().getOnlinePlayers())
                                {
                                    if (target.getWorld().equals(pworld))
                                    {
                                        if (target.getLocation().distance(blockLoc) < SiphonDis)
                                        { 
                                            if (plugin.party.containsKey(target.getName().toLowerCase()))
                                            {
                                                targetparty = plugin.party.get(target.getName().toLowerCase());
                                            }

                                            if (myparty != targetparty && target != player)
                                            {
                                                Collection c = target.getActivePotionEffects();

                                                if (c.size() > 0)
                                                {
                                                    count++;
                                                    
                                                    if (count >= SiphonTimes)
                                                    {
                                                        return;
                                                    }
                                                    
                                                    PotionEffect[] pegood = new PotionEffect[c.size()];
                                                    PotionEffect pe;
                                                    int i = 0;
                                                    int amountgood = 0;

                                                    Iterator<PotionEffect> it = c.iterator();

                                                    boolean isScout = false;
                                                    
                                                    if (target.hasPermission("scout.move"))
                                                    {
                                                        isScout = true;
                                                    }
                                                    
                                                    while (it.hasNext())
                                                    {
                                                        pe = it.next();

                                                        if (pe.getType().equals(PotionEffectType.FIRE_RESISTANCE)
                                                                || pe.getType().equals(PotionEffectType.DAMAGE_RESISTANCE) || pe.getType().equals(PotionEffectType.INCREASE_DAMAGE)
                                                                || pe.getType().equals(PotionEffectType.INVISIBILITY) || pe.getType().equals(PotionEffectType.JUMP)
                                                                || pe.getType().equals(PotionEffectType.NIGHT_VISION) || pe.getType().equals(PotionEffectType.REGENERATION)
                                                                || pe.getType().equals(PotionEffectType.SPEED) || pe.getType().equals(PotionEffectType.WATER_BREATHING)
                                                                || pe.getType().equals(PotionEffectType.ABSORPTION))
                                                        {
                                                            if (isScout)
                                                            {
                                                                if (pe.getType().equals(PotionEffectType.SPEED) || pe.getType().equals(PotionEffectType.JUMP))
                                                                {
                                                                    return;
                                                                }
                                                            }
                                                            
                                                            pegood[i] = pe;
                                                            amountgood = amountgood + 1;
                                                            i++;
                                                        }
                                                    }

                                                    if (amountgood > 0)
                                                    {
                                                        Random rand = new Random();
                                                        int random = rand.nextInt(amountgood);

                                                        PotionEffect pot = pegood[random];
                                                        
                                                        PotionEffectType type = pot.getType();
                                                        
                                                        int dur = pot.getDuration();
                                                        int amp = pot.getAmplifier();

                                                        String name = "";

                                                        if (type.equals(PotionEffectType.INCREASE_DAMAGE))
                                                        {
                                                            name = "Increased Damage";
                                                        }

                                                        if (type.equals(PotionEffectType.DAMAGE_RESISTANCE))
                                                        {
                                                            name = "Increased Defense";
                                                        }

                                                        if (type.equals(PotionEffectType.SPEED))
                                                        {
                                                            name = "Speed";
                                                        }

                                                        if (type.equals(PotionEffectType.REGENERATION))
                                                        {
                                                            name = "Regen";
                                                        }

                                                        if (type.equals(PotionEffectType.JUMP))
                                                        {
                                                            name = "Jump";
                                                        }

                                                        if (type.equals(PotionEffectType.FIRE_RESISTANCE))
                                                        {
                                                            name = "Fire Resist";
                                                        }

                                                        if (type.equals(PotionEffectType.NIGHT_VISION))
                                                        {
                                                            name = "Night Vision";
                                                        }

                                                        if (type.equals(PotionEffectType.WATER_BREATHING))
                                                        {
                                                            name = "Water Breathing";
                                                        }
                                                        
                                                        if (type.equals(PotionEffectType.ABSORPTION))
                                                        {
                                                            name = "Absorption";
                                                        }

                                                        if (type.equals(PotionEffectType.SPEED))
                                                        {
                                                            if (isScout)
                                                            {
                                                                return;
                                                            }
                                                        }
                                                        
                                                        if (type.equals(PotionEffectType.JUMP))
                                                        {
                                                            if (isScout)
                                                            {
                                                                return;
                                                            }
                                                        }
                                                        
                                                        player.addPotionEffect(new PotionEffect(type,dur + durBoost, amp));
                                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "You Siphon " + ChatColor.GOLD + name 
                                                                + ChatColor.DARK_PURPLE + " from " + ChatColor.GOLD + target.getName() + "!");

                                                        double phealth = player.getHealth();
                                                        if ((phealth + SiphonSteal) > player.getMaxHealth())
                                                        {
                                                            player.setHealth(player.getMaxHealth());
                                                        }
                                                        else
                                                        {
                                                            player.setHealth(phealth + SiphonSteal);
                                                        }


                                                        target.removePotionEffect(pot.getType());
                                                        target.addPotionEffect(new PotionEffect(type,0,0));

                                                        target.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.DARK_PURPLE + " Siphons your " 
                                                                + ChatColor.GOLD + name + "!");
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        } // end Siphon
                        
                    } // end bone
                    
                    
                    if (player.getItemInHand().getTypeId() == 268 || player.getItemInHand().getTypeId() == 267 
                            || player.getItemInHand().getTypeId() == 276 || player.getItemInHand().getTypeId() == 283) // if any sword
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
                        
                        // do Cocoon
                        
                        if (CocoonTimer.containsKey(player.getName()))
                        {
                            int ctimer = CocoonTimer.get(player.getName());
                            int totaltime = time - ctimer;
                            
                            if ((totaltime > CocoonCD) || (totaltime < 0))
                            {
                                for (Player p : Bukkit.getOnlinePlayers())
                                {
                                    if (p.getWorld().equals(player.getWorld()))
                                    {
                                        if (p.getLocation().distance(player.getLocation()) < CocoonDis)
                                        {
                                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.DARK_PURPLE + " wraps themself in a shadowy cocoon.");
                                        }
                                    }
                                }
                                
                                CocoonTimer.put(player.getName(), time);
                                CocoonElapse.put(player.getName(), 0);
                                
                                if (!(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)))
                                {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,2));
                                }

                                if (!(player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)))
                                {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,100,2));
                                }
                                
                                new Cocoon(player);
                                
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + (CocoonCD - totaltime) + " seconds to Shadow Cocoon.");
                                return;
                            }
                        }
                        else
                        {
                            for (Player p : Bukkit.getOnlinePlayers())
                            {
                                if (p.getWorld().equals(player.getWorld()))
                                {
                                    if (p.getLocation().distance(player.getLocation()) < CocoonDis)
                                    {
                                        p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.DARK_PURPLE + " wraps themself in a shadowy cocoon.");
                                    }
                                }
                            }

                            CocoonTimer.put(player.getName(), time);
                            CocoonElapse.put(player.getName(), 0);
                            
                            if (!(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)))
                            {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,2));
                            }
                                
                            if (!(player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)))
                            {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,100,2));
                            }

                            new Cocoon(player);
                        }
                        
                        
                        
                    } // end swords
                    
                    
                    if (player.getItemInHand().getTypeId() == 370) // if ghast tear
                    {
                        // do darkness
                        if (DarknessTimer.containsKey(player.getName()))
                        {
                            int newtime = DarknessTimer.get(player.getName());
                            int totaltime = (time - newtime);
                            
                            if ((totaltime > DarknessCD) || (totaltime < 0))
                            {
                                DarknessTimer.put(player.getName(), time);
                                
                                if (plugin.party.containsKey(player.getName().toLowerCase()))
                                {
                                    int pnum = plugin.party.get(player.getName().toLowerCase());

                                    for (Player p : Bukkit.getOnlinePlayers())
                                    {
                                        if (plugin.party.containsKey(p.getName().toLowerCase()))
                                        {
                                            int pnum2 = plugin.party.get(p.getName().toLowerCase());

                                            if (pnum2 == pnum)
                                            {
                                                if (p.getWorld().equals(player.getWorld()))
                                                {
                                                    if (p.getLocation().distance(player.getLocation()) < DarknessDis)
                                                    {
                                                        if (p != player)
                                                        {
                                                            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, DarknessDur, 2));
                                                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "The shadows gather around you...");   
                                                        }
                                                    }
                                                }
                                            }   
                                        }
                                    }
                                }
                                
                                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, DarknessDur, 2));
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "You gather your shadows around you...");
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + (DarknessCD - totaltime) + " seconds to Gather Darkness.");
                                return;
                            }
                            
                        }
                        else
                        {
                            DarknessTimer.put(player.getName(), time);
                            
                            if (plugin.party.containsKey(player.getName().toLowerCase()))
                            {
                                int pnum = plugin.party.get(player.getName().toLowerCase());
                            
                                for (Player p : Bukkit.getOnlinePlayers())
                                {
                                    if (plugin.party.containsKey(p.getName().toLowerCase()))
                                    {
                                        int pnum2 = plugin.party.get(p.getName().toLowerCase());
                                    
                                        if (pnum2 == pnum)
                                        {
                                            if (p.getWorld().equals(player.getWorld()))
                                            {
                                                if (p.getLocation().distance(player.getLocation()) < DarknessDis)
                                                {
                                                    if (p != player)
                                                    {
                                                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, DarknessDur, 2));
                                                        isInvis.add(p.getName());
                                                        new Invis(p);
                                                        p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "The shadows gather around you...");
                                                    }
                                                }
                                            }
                                        }   
                                    }
                                }
                            }
                            
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, DarknessDur, 2));
                            isInvis.add(player.getName());
                            new Invis(player);
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "You gather your shadows around you...");
                        }
                    }                    
                    
                } // end armor check
                else
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing " + ChatColor.WHITE + "Iron Chest & Legs " + ChatColor.RED + "to use that ability.");
                    return;
                } 
                
            } // end right click check
            
        }// end shadowknight.pvp perm
    } // end interact
    
    
    
    
    
    private class Invis implements Runnable
    {
        Player player;
        int taskId;

        public Invis (Player player)
        {
            this.player = player;
            this.taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ShadowKnight.this.plugin, this, DarknessDur);
        }
        @Override
        public void run() 
        {
            if (isInvis.contains(player.getName()))
            {
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,0,0));
                }   
                
                isInvis.remove(player.getName());
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "The Darkness fades...");
            }
        }
    }
    
    private class Grasp implements Runnable
    {
        Player target2;
        int taskId;
        int i = 0;

        
        public Grasp (Player target2)
        {
            this.target2 = target2;
            this.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ShadowKnight.this.plugin, this, 0, 20);
        }
        @Override
        public void run() 
        {
            i++;
            
            if (i >= GraspDur + 5)
            {
                Party.log.info(String.format("ShadowKnight Grasp Thread errant: Cancelling Task ID: " + taskId));
                Bukkit.getServer().getScheduler().cancelTask(taskId);
            }
            
            if ( (!(target2.isOnline())) || (!(isGrabbed.contains(target2.getName()))) )
            {
                isGrabbed.remove(target2.getName());
                Bukkit.getServer().getScheduler().cancelTask(taskId);
                return;
            }
             
            if (isGrabbed.contains(target2.getName()) && (!(target2.isDead())) )
            {
                Location newLoc = GraspLoc.get(target2.getName());
                
                if (i > GraspDur)
                {
                    isGrabbed.remove(target2.getName());
                    
                    target2.teleport(newLoc);
                    target2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "The shadows have pulled you back to them...");
                    target2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,120,4));
                    
                    Bukkit.getServer().getScheduler().cancelTask(taskId);
                    return;
                }
            }
            else
            {
                Bukkit.getServer().getScheduler().cancelTask(taskId);
                return;
            }
        }
        
    }
    
    private class Cocoon implements Runnable
    {
        Player player;
        int taskId;

        int i = 0;
        public Cocoon (Player player)
        {
            this.player = player;
            this.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ShadowKnight.this.plugin, this, 0, 20);
            CocoonID.put(player.getName(), taskId);
        }
        
        
        @Override
        public void run() 
        {
            i++;
            
            if (i >= 15)
            {
                Party.log.info(String.format("ShadowKnight Cocoon Thread Errant: Cancelling Task ID: " + taskId));
                Bukkit.getServer().getScheduler().cancelTask(taskId);
            }
            
            if (CocoonElapse.containsKey(player.getName()))
            {
                int timecheck = CocoonElapse.get(player.getName());
                int timecheck2 = timecheck + 1;
                
                World pworld = player.getWorld();
                Location ploc = player.getLocation();
                
                CocoonElapse.remove(player.getName());
                
                CocoonElapse.put(player.getName(), timecheck2);
                
                
                double phealth = player.getHealth();
                
                int myparty = -1;
                int targetparty = -2;
                
                if (plugin.party.containsKey(player.getName().toLowerCase()))
                {
                    myparty = plugin.party.get(player.getName().toLowerCase());
                }
                
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    if (p.getWorld().equals(pworld))
                    {
                        if (p.getLocation().distance(ploc) < CocoonDis)
                        {
                            if (plugin.party.containsKey(p.getName().toLowerCase()))
                            {
                                targetparty = plugin.party.get(p.getName().toLowerCase());
                            }
                            
                            if (myparty != targetparty && p != player)
                            {
                                double targethp = p.getHealth();
                                double newThp = targethp - CocoonDmg;
                                
                                if (newThp < 2)
                                {
                                    p.setHealth(2);
                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Shadow Cocoon"
                                            + ChatColor.RESET + ChatColor.DARK_PURPLE + " drains your health!");
                                    p.playEffect(EntityEffect.HURT);
                                }
                                else
                                {
                                    
                                    if (p.getHealth() - 1 > 1)
                                    {
                                        p.damage(1, player);
                                    }
                                    
                                    p.setHealth(newThp);
                                    
                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Shadow Cocoon"
                                            + ChatColor.RESET + ChatColor.DARK_PURPLE + " drains your health!");
                                    p.playEffect(EntityEffect.HURT);
                                }
                                
                                double newPhp = phealth + CocoonSteal;
                                
                                if (newPhp > player.getMaxHealth())
                                {
                                    player.setHealth(player.getMaxHealth());
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "You drain health from " + ChatColor.GOLD + p.getName() + "!");
                                }
                                else
                                {
                                    player.setHealth(newPhp);
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "You drain health from " + ChatColor.GOLD + p.getName() + "!");
                                }
                            }
                        }
                    }
                }
                
                
                if (timecheck >= CocoonDur)
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your cocoon fades.");
                    
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (p.getWorld().equals(pworld))
                        {
                            if (p.getLocation().distance(ploc) < CocoonDis)
                            {
                                if (p != player)
                                {
                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.DARK_PURPLE + "Cocoon" + ChatColor.GOLD + " fades.");
                                }
                            }
                        }
                    }
                    
                    if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
                    {
                        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,0,0));
                    }
                    
                    CocoonElapse.remove(player.getName());
                    
                    if (CocoonID.containsKey(player.getName()))
                    {
                        CocoonID.remove(player.getName());
                    }
                    
                    Bukkit.getServer().getScheduler().cancelTask(this.taskId);
                }
            }
            else
            {
                if (CocoonID.containsKey(player.getName()))
                {
                    CocoonID.remove(player.getName());
                }
                
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Cocoon failed. Let cr0ss know.");
                Bukkit.getServer().getScheduler().cancelTask(this.taskId);
            }
        }
    }
    
}
