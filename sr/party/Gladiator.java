package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * @author Cross
 */
public class Gladiator implements Listener
{
    public static HashMap<String, Integer> gladspell = new HashMap<String, Integer>();
    
    public static HashMap<String, Integer> HasteTimer = new HashMap<String, Integer>();
    public static HashMap<String, Integer> SandTimer = new HashMap<String, Integer>();
    public static HashMap<String, Integer> WhipTimer = new HashMap<String, Integer>();
    
    public static HashMap<String, Integer> HamCD = new HashMap<String, Integer>();
    public static HashMap<String, Integer> CritCD = new HashMap<String, Integer>();
    
    public Party plugin;
    
    public Gladiator(Party plugin)
    {
        this.plugin = plugin;
    }
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    
    private int hasteCD = 30;
    private int sandCD = 20;
    private int whipCD = 35;
    
    public int whipRange = 20;
    
    private int ICD = 4;
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event)
    {
        /*
        if (!(event.getEntity() instanceof Player))
        {
            return;
        }
        */
        if (!(event.getEntity() instanceof LivingEntity))
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
        
        
        Player attacker = ((Player)event_EE.getDamager());
        LivingEntity target = (LivingEntity) event.getEntity();
        Player player = null;
        
        if (target instanceof Player)
        {
            player = (Player) target;
        }
        
            if (Party.ghost.contains(attacker.getName()))
            {
                event.setCancelled(true);
                event.setDamage(0);
                attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Ghosts cannot attack!");
                return;
            }
            
            if (PL.stuntarget.containsKey(attacker.getName().toLowerCase()) || Paladin.isStunned.contains(attacker.getName()) || ChaosCrusader.chaosstunned.containsKey(attacker.getName()))
            {
                  event.setCancelled(true);
                  attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                  return;
            }
            
        if(attacker.hasPermission("gladiator.slow"))
        {
            ItemStack helditem = attacker.getItemInHand();
        
            if(helditem.getTypeId() == 276 || helditem.getTypeId() == 267)
            {
                if (player != null)
                {
                    World plworld = player.getWorld();

                    BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                    RegionManager regionManager = worldGuard.getRegionManager(plworld);
                    ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                    if (!(set.allows(DefaultFlag.PVP)))
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                        return;
                    }
                    
                    if (!(HamCD.containsKey(player.getName())))
                    {
                        int currenttime = plugin.getCurrentTime();

                        double random = Math.random();
                        if ((random >= 0.85))
                        {
                            if (!(target.hasPotionEffect(PotionEffectType.SLOW)))
                            {
                                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
                                if (player != null)
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You've been hamstrung!");
                                }
                                attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You've hamstrung your target!");

                                HamCD.put(player.getName(), currenttime);
                            }
                        }
                    }
                    else
                    {
                        int currenttime = plugin.getCurrentTime();
                        int timecheck = HamCD.get(player.getName());
                        int totaltime = (currenttime - timecheck);

                        if (totaltime > ICD || totaltime < 0)
                        {
                            HamCD.remove(player.getName());
                        }
                    }
                }
                else
                {
                    double random = Math.random();
                    int tcd = target.getMaximumNoDamageTicks();
                    int acd = attacker.getNoDamageTicks();
                    
                    if (acd < tcd/3.0F)
                    {
                        if (random >= 0.90)
                        {
                            if (!(target.hasPotionEffect(PotionEffectType.SLOW)))
                            {
                                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
                                attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You've hamstrung your target!");
                            }
                            
                        }
                    }
                }
            }
        }
    
        if (attacker.hasPermission("gladiator.powerattack"))
        {
            ItemStack helditem = attacker.getItemInHand();
            if(helditem.getTypeId() == 276 || helditem.getTypeId() == 267)
            {
                if (player != null)
                {
                    if (!(CritCD.containsKey(player.getName())))
                    {
                        int currenttime = plugin.getCurrentTime();
                        double random = Math.random();
                        if ((random <= 0.15))
                        {
                            double dmg = event.getDamage();
                            event.setDamage(dmg * 2);
                            if (player != null)
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You've been critically hit!");
                            }
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You've critically hit your target!");

                            CritCD.put(player.getName(), currenttime);
                        }
                    }
                    else
                    {
                        int currenttime = plugin.getCurrentTime();
                        int timecheck = CritCD.get(player.getName());
                        int totaltime = (currenttime - timecheck);

                        if (totaltime > ICD || totaltime < 0)
                        {
                            CritCD.remove(player.getName());
                        }

                    }
                }
                else
                {
                    double random = Math.random();
                    int tcd = target.getMaximumNoDamageTicks();
                    int acd = attacker.getNoDamageTicks();
                    
                    if (acd < tcd/3.0F)
                    {
                        if (random >= 0.90)
                        {
                            double dmg = event.getDamage();
                            event.setDamage(dmg * 2);
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You've critically hit your target!");
                        }
                    }
                }
                
            }
        }
    } // end Entity Damage
    
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        
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
        
        int haste = 1;
        int sand = 2;
        int whip = 3;
        
        
        if (player.hasPermission("sr.gladiator.cast"))
        {

            if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))
            {
                ItemStack item = (ItemStack) player.getItemInHand();
                
                if (item == null)
                {
                    return;
                }
                
                if (item != null)
                {
                    if (item.getTypeId() == 288) // feather
                    {
                        int spellcheck;
                        
                        if (gladspell.containsKey(player.getName()))
                        {
                            spellcheck = gladspell.get(player.getName()) + 1;
                            
                            if (spellcheck > whip)
                            {
                                spellcheck = 1;
                            }
                        }
                        else
                        {
                            spellcheck = 1;
                        }
                        
                        if (spellcheck == haste)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You ready your " + ChatColor.GOLD + "Haste" + ChatColor.DARK_AQUA + " ability.");
                        }
                        if (spellcheck == sand)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You ready your " + ChatColor.GOLD + "Pocket Sand" + ChatColor.DARK_AQUA + " ability.");
                        }
                        if (spellcheck == whip)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You ready your " + ChatColor.GOLD + "Whip" + ChatColor.DARK_AQUA + " ability.");
                        }
                        
                        gladspell.put(player.getName(), spellcheck);
                        
                        event.setCancelled(true);
                        
                    }
                }
            } // end right click
            
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                ItemStack item = (ItemStack) player.getItemInHand();
                
                int currenttime = plugin.getCurrentTime();
                
                if (item == null)
                {
                    return;
                }
                
                if (item != null)
                {
                    if (item.getTypeId() == 288) // feather
                    {
                        int spellcheck;
                        
                        if (gladspell.containsKey(player.getName()))
                        {
                            spellcheck = gladspell.get(player.getName());
                        }
                        else
                        {
                            spellcheck = 1;
                        }
                        
                        if (spellcheck == haste)
                        {
                            boolean hastecheck;
                            
                            if (HasteTimer.containsKey(player.getName()))
                            {
                                int hasteold = HasteTimer.get(player.getName());
                                int hastetotal = (currenttime - hasteold);
                                
                                if (hastetotal > hasteCD || hastetotal < 0)
                                {
                                    hastecheck = false;
                                }
                                else
                                {
                                    hastecheck = true;
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.GOLD + (hasteCD - hastetotal)
                                            + ChatColor.RED + " seconds to use " + ChatColor.GOLD + "Haste " + ChatColor.RED + "again.");
                                    return;
                                }
                                        
                            }
                            else
                            {
                                hastecheck = false;
                            }
                            
                            if (hastecheck == true)
                            {
                                return;
                            }
                            
                            if (player.hasPotionEffect(PotionEffectType.SLOW))
                            {
                                player.removePotionEffect(PotionEffectType.SLOW);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,0,0));
                            }
                            
                            if (player.hasPotionEffect(PotionEffectType.SPEED))
                            {
                                player.removePotionEffect(PotionEffectType.SPEED);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,140,7));
                            }
                            else
                            {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,140,7));
                            }

                            HasteTimer.put(player.getName(), currenttime);
                            
                            Location ploc = player.getLocation();
                            World pworld = player.getWorld();
                            
                            for (Player p : Bukkit.getOnlinePlayers())
                            {
                                if (p.getWorld().equals(pworld))
                                {
                                    if (p.getLocation().distance(ploc) < 20)
                                    {
                                        p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName() + ChatColor.DARK_AQUA + " has used " + ChatColor.GOLD + "Haste.");
                                    }
                                }
                            }
                        } // end haste
                        
                        if (spellcheck == sand)
                        {
                            World plworld = player.getWorld();

                            BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                            RegionManager regionManager = worldGuard.getRegionManager(plworld);
                            ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                            if (!(set.allows(DefaultFlag.PVP)))
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                                return;
                            }
                            
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
                                
                                if (ploc.distance(tloc) > 10)
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target is out of range.");
                                    return;
                                }
                                
                                boolean sandcheck;
                                
                                if (SandTimer.containsKey(player.getName()))
                                {
                                    int sandold = SandTimer.get(player.getName());
                                    int sandtotal = (currenttime - sandold);
                                    
                                    if (sandtotal > sandCD || sandtotal < 0)
                                    {
                                        sandcheck = false;
                                    }
                                    else
                                    {
                                        sandcheck = true;
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.GOLD + (sandCD - sandtotal)
                                            + ChatColor.RED + " seconds to use " + ChatColor.GOLD + "Pocket Sand " + ChatColor.RED + "again.");
                                        return;
                                    }
                                }
                                else
                                {
                                    sandcheck = false;
                                }
                                
                                if (sandcheck == false)
                                {
                                    if (target2.hasPotionEffect(PotionEffectType.SPEED))
                                    {
                                        target2.removePotionEffect(PotionEffectType.SPEED);
                                        target2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,0,0));
                                    }
                                    
                                    target2.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,300,9));
                                    target2.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,60,4));
                                    target2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20,2));
                                    
                                    World pworld = player.getWorld();
                                    
                                    for (Player p : Bukkit.getOnlinePlayers())
                                    {
                                        if (p.getWorld().equals(pworld))
                                        {
                                            if (p.getLocation().distance(ploc) < 15)
                                            {
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName() + ChatColor.DARK_AQUA + " used " 
                                                        + ChatColor.GOLD + "Pocket Sand" + ChatColor.DARK_AQUA + " on " + ChatColor.GOLD + target2.getName());
                                            }
                                        }
                                    }
                                    
                                    SandTimer.put(player.getName(), currenttime);
                                }
                            }// end if target != null
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must have a target.");
                                return;
                            }
                            
                        } // end of sand
                        
                        if (spellcheck == whip)
                        {
                            if (!player.hasPermission("sr.gladiator.cast.whip"))
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You do not have permissions to use this here.");
                                return;
                            }
                            
                            boolean whipcheck;
                            
                            if (WhipTimer.containsKey(player.getName()))
                            {
                                int whipold = WhipTimer.get(player.getName());
                                int whiptotal = (currenttime - whipold);
                                
                                if (whiptotal > whipCD || whiptotal < 0)
                                {
                                    whipcheck = false;
                                }
                                else
                                {
                                    whipcheck = true;
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.GOLD + (whipCD - whiptotal)
                                            + ChatColor.RED + " seconds to use your " + ChatColor.GOLD + "Whip " + ChatColor.RED + "again.");
                                    return;
                                }
                                        
                            }
                            else
                            {
                                whipcheck = false;
                            }
                            
                            if (whipcheck == false)
                            {
                                World pworld = player.getWorld();
                                Location ploc = player.getLocation();
                                
                                WhipTimer.put(player.getName(), currenttime);
                                
                                int targetparty = -1;
                                int myparty = -2;
                                
                                if (plugin.party.containsKey(player.getName().toLowerCase()))
                                {
                                    myparty = plugin.party.get(player.getName().toLowerCase());
                                }
                                
                                World plworld = player.getWorld();

                                BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                                RegionManager regionManager = worldGuard.getRegionManager(plworld);
                                ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                                if (!(set.allows(DefaultFlag.PVP)))
                                {
                                    for (Entity e : player.getNearbyEntities(whipRange, whipRange, whipRange))
                                    {
                                        if (!(e instanceof Player))
                                        {
                                            if (e instanceof LivingEntity)
                                            {
                                                LivingEntity ent = (LivingEntity) e;
                                                if (ent.getWorld().equals(pworld))
                                                {
                                                    if (ent.getLocation().distance(ploc) < whipRange)
                                                    {
                                                        Vector pl;
                                                        Vector vec;

                                                        ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,40,2));
                                                        pl = ent.getLocation().toVector();
                                                        vec = player.getLocation().toVector().subtract(pl).normalize().multiply(2/10.0*30);
                                                        ent.setVelocity(vec);  
                                                    }
                                                }
                                            }
                                        }
                                        else
                                        {
                                            if (e instanceof Player)
                                            {
                                                Player p = (Player) e;
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName() + ChatColor.DARK_AQUA + " has used their " 
                                                        + ChatColor.GOLD + "Whip" + ChatColor.DARK_AQUA + " ability!");
                                            }
                                        }
                                    }
                                    return;
                                }
                                else
                                {
                                    for (Player p : Bukkit.getOnlinePlayers())
                                    {
                                        if (p.getWorld().equals(pworld))
                                        {

                                            if (p.getLocation().distance(ploc) < 20)
                                            {
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName() + ChatColor.DARK_AQUA + " has used their " 
                                                        + ChatColor.GOLD + "Whip" + ChatColor.DARK_AQUA + " ability!");

                                                if ((p.getLocation().getY()) < player.getLocation().getY())
                                                {
                                                    return;
                                                }

                                                if (plugin.party.containsKey(p.getName().toLowerCase()))
                                                {
                                                    targetparty = plugin.party.get(p.getName().toLowerCase());
                                                }

                                                if (p != player && myparty != targetparty)
                                                {

                                                    Vector pl;
                                                    Vector vec;

                                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,40,2));
                                                    pl = p.getLocation().toVector();
                                                    vec = player.getLocation().toVector().subtract(pl).normalize().multiply(2/10.0*30);
                                                    p.setVelocity(vec);  


                                                }
                                            }
                                        }
                                    }
                                }
                                
                                
                                
                                
                            }
                        }
                        
                    } // end if iteminhand
                    
                } // end if != null
                
            } // end left click
            
        } // end perm
        
    } // end interact
    
}
