package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import net.minecraft.server.v1_7_R1.EntitySkeleton;
import net.minecraft.server.v1_7_R1.Item;
import net.minecraft.server.v1_7_R1.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftSkeleton;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import sr.party.Utils.BlockUtils;
import sr.party.Utils.FadeUtils;

/**
 *
 * @author Cross
 */
public class Warlock implements Listener
{
    public Party plugin;
    public BlockUtils blockUtils;
    public FadeUtils fadeUtils;
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    
    public Warlock (Party plugin)
    {
        this.plugin = plugin;
    }
    
    public int BoneShieldCD = 120;
    public int BoneShieldDur = 30;
    public double ShieldHP = 60;
    public int AbsorbAmp = 14; // (amp * 4) + 4
    
    //
    
    public int FadeCD = 15;
    public int FadeDist = 25;
    public boolean passThroughCeiling = false;
    public boolean smokeTrail = true;
    
    //
    public int LeechCD = 15;
    public int LeechDist = 20;
    public int LeechAmount = 2;
    public int LeechDur = 10;
    
    // Prison
    
    public int PrisonCD = 30;
    public int PrisonDist = 15;
    public int PrisonDur = 8;
    
    //
    // Sicken
    public int SickenCD = 7;
    public int SickenDist = 20;
    public int SickenDMG = 3;
    public int SickenPoisonDur = 40;
    

    // Bone Shield Hashes
    public HashSet<String> hasBoneShield = new HashSet<String>();
    public HashMap<String, Integer> BoneShieldTimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> PlayerID1 = new HashMap<String, Integer>();
    public HashMap<String, Integer> PlayerID2 = new HashMap<String, Integer>();
    public HashMap<Integer, Entity> SkeletonID = new HashMap<Integer, Entity>();
    
    public HashMap<String, Double> BoneShieldHealth = new HashMap<String, Double>(); 
    
    //
    
    // Fade Hashes
    public HashMap<String, Integer> FadeTimer = new HashMap<String, Integer>();
    
    //
    
    // Leech Hashes
    public HashMap<String, Integer> LeechTimer = new HashMap<String, Integer>();
    public HashSet<String> isLeeched = new HashSet<String>();
    
    //
    
    // Sicken Hashes
    public HashMap<String, Integer> SickenTimer = new HashMap<String, Integer>();
    public HashSet<String> isSickened = new HashSet<String>();
    
    
    //
    
    // Imprison Hashes
    public HashMap<String, Integer> PrisonTimer = new HashMap<String, Integer>();
    public HashSet<String> isPrisoned = new HashSet<String>();
    
    
    @EventHandler
    public void onMove (PlayerMoveEvent event)
    {
        if (isPrisoned.contains(event.getPlayer().getName()))
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        Entity ent = event.getEntity();
        
        if (SkeletonID.containsKey(ent.getEntityId()))
        {
            event.setCancelled(true);
        }
        
    }
    
    @EventHandler
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event)
    {
        Entity ent = event.getEntity();
        
        Entity attacker = event.getDamager();
        
        int id = attacker.getEntityId();

        if (SkeletonID.containsKey(id))
        {
            event.setCancelled(true);
            return;
        }
        
        if (attacker instanceof Player)
        {
            Player damager = (Player) attacker;
            if (PL.stuntarget.containsKey(damager.getName().toLowerCase()) || Paladin.isStunned.contains(damager.getName()) || ChaosCrusader.chaosstunned.containsKey(damager.getName()))
            {
                  event.setCancelled(true);
                  damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                              return;
            }
            
            if (damager.isOp() || damager.hasPermission("paladin.goldbypass"))
            {
                return;
            }
            
            if (damager.hasPermission("sr.warlock.weapon"))
            {
                if (damager.getItemInHand().getTypeId() != 283)
                {
                    event.setCancelled(true);
                    event.setDamage(0);
                    damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must use a Gold Sword to deal melee damage.");
                    return;
                }
                else
                {
                    double dmg = event.getDamage();
                    double newdmg = dmg + 2;
                    event.setDamage(newdmg);
                }
            }
            
        }
        
        if (ent instanceof Player)
        {
            Player player = (Player) event.getEntity();
            
            if (!(attacker instanceof Player))
            {
                return;
            }
            
            Player damager = (Player) attacker;

            if (isPrisoned.contains(damager.getName()))
            {
                event.setCancelled(true);
                return;
            }
            
            if (hasBoneShield.contains(player.getName()))
            {
                if (player.hasPotionEffect(PotionEffectType.getById(22)))
                {
                    damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName() + "'s Bone Shield absorbs your attack!");
                }
                else
                {
                    damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have broken " + player.getName() + "'s Bone Shield!");
                    hasBoneShield.remove(player.getName());
                }
            }
            /*
            if (hasBoneShield.contains(player.getName()))
            {
                event.setCancelled(true);

                double HPold = 1;

                if (BoneShieldHealth.containsKey(player.getName()))
                {
                    HPold = BoneShieldHealth.get(player.getName());
                }

                double dmg = event.getDamage();

                double HPnew = (HPold - dmg);

                if (HPnew <= 0)
                {
                    BoneShieldHealth.remove(player.getName());
                    hasBoneShield.remove(player.getName());
                    damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s Bone Shield " + ChatColor.RED + "Breaks!");
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Bone Shield is broken!");
                    return;
                }

              //  int whole = (ShieldHP - HPnew);
              //  double dec = ((double)whole) * ((double) ShieldHP);
                double percent = Math.round((HPnew * 100) / ShieldHP);

                BoneShieldHealth.put(player.getName(), HPnew);

                String displayPercent = "";

                if (percent > 70)
                {
                    displayPercent = "" + ChatColor.GREEN + percent + "%";
                }

                if (percent > 40 && percent <= 70)
                {
                    displayPercent = "" + ChatColor.YELLOW + percent + "%";
                }

                if (percent <= 40)
                {
                    displayPercent = "" + ChatColor.RED + percent + "%";
                }

                damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s Bone Shield absorbs your attack! Now at: " + displayPercent);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Your Bone Shield absorbs an attack! Now at: " + displayPercent);

            }
            */
        }
        
    }
    
    @EventHandler
    public void onDeath (PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        if (isPrisoned.contains(player.getName()))
        {
            isPrisoned.remove(player.getName());
        }
        
        if (hasBoneShield.contains(player.getName()))
        {
            hasBoneShield.remove(player.getName());
        }
        
        if (isSickened.contains(player.getName()))
        {
            isSickened.remove(player.getName());
        }
        
        if (isLeeched.contains(player.getName()))
        {
            isLeeched.remove(player.getName());
        }
        
        if (leechKill.contains(player.getName()))
        {
            event.setDeathMessage(player.getName() + " was leeched to death by " + player.getKiller().getName());
            leechKill.remove(player.getName());
        }
        
        
        for (Player other : Bukkit.getServer().getOnlinePlayers())
        {
            if ((!other.equals(player)) && (!other.canSee(player)))
            {
                other.showPlayer(player);
            }    
        }
    }
    
    protected boolean inRange(Location loc1, Location loc2, int range) 
    {
        return loc1.distanceSquared(loc2) < range*range;
    }
    
    private void teleport (Player player, Location location, HashSet<Location> smokeLocs)
    {
        player.teleport(location);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 2));

        if (smokeTrail && smokeLocs != null)
        {
            for (Location l : smokeLocs)
            {
                l.getWorld().playEffect(l, Effect.SMOKE, 4);
            }
        }
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
        
        if (isPrisoned.contains(player.getName()))
        {
            event.setCancelled(true);
            return;
        }
        
        
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            if (player.hasPermission("sr.warlock.cast"))
            {
                if (player.getItemInHand().getTypeId() == 341) // slime ball
                {
                    if (player.getInventory().getChestplate() == null 
                            || player.getInventory().getLeggings() == null || player.getInventory().getBoots() == null)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }

                    if (player.getInventory().getChestplate().getTypeId() == 315 && player.getInventory().getLeggings().getTypeId() == 316 
                            && player.getInventory().getBoots().getTypeId() == 317)
                    {
                    
                        if (player.getInventory().getHelmet() != null)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must not wear a helmet for this spell.");
                            return;
                        }

                        World pworld = player.getWorld();

                        BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                            return;
                        }

                        Boolean canCast = false;
                        int currenttime = plugin.getCurrentTime();

                        if (LeechTimer.containsKey(player.getName()))
                        {
                            int oldtime = LeechTimer.get(player.getName());
                            int totaltime = currenttime - oldtime;

                            if (totaltime > LeechCD || totaltime < 0)
                            {
                                canCast = true;
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot Fade again for another " + (LeechCD - totaltime) + " seconds.");
                                return;
                            }
                        }
                        else
                        {
                            canCast = true;
                        }

                        if (canCast)
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

                               if (!(target2.getGameMode() == GameMode.SURVIVAL))
                               {
                                   return;
                               }

                               if (isLeeched.contains(target2.getName()))
                               {
                                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target already has Leech applied.");
                                   return;
                               }

                               Location ploc = player.getLocation();
                               Location tloc = target2.getLocation();

                                if (ploc.distance(tloc) > LeechDist)
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target out of range.");
                                    return;
                                }

                                int targetparty = -1;
                                int myparty = -2;

                                if (plugin.party.containsKey(player.getName().toLowerCase()))
                                {
                                    myparty = plugin.party.get(player.getName().toLowerCase());
                                }

                                if (plugin.party.containsKey(target2.getName().toLowerCase()))
                                {
                                    targetparty = plugin.party.get(target2.getName().toLowerCase());
                                }

                                if (myparty != targetparty && target2 != player)
                                {
                                    new Leech(target2, player);
                                    isLeeched.add(target2.getName());
                                    LeechTimer.put(player.getName(), currenttime);

                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You begin leeching " + target2.getName() + "'s life.");
                                    target2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your life is being siphoned by " + player.getName() + "!");
                                }


                            }

                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }
                }
                
            }
            
            if (player.hasPermission("sr.warlock.cast"))
            {
                if (player.getItemInHand().getTypeId() == 381)
                {
                    if (player.getInventory().getChestplate() == null 
                            || player.getInventory().getLeggings() == null || player.getInventory().getBoots() == null)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }

                    if (player.getInventory().getChestplate().getTypeId() == 315 && player.getInventory().getLeggings().getTypeId() == 316 
                            && player.getInventory().getBoots().getTypeId() == 317)
                    {
                    
                        if (player.getInventory().getHelmet() != null)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must not wear a helmet for this spell.");
                            return;
                        }

                        World pworld = player.getWorld();

                        BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
                        

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                            return;
                        }

                        Boolean canCast = false;
                        int currenttime = plugin.getCurrentTime();

                        if (PrisonTimer.containsKey(player.getName()))
                        {
                            int oldtime = PrisonTimer.get(player.getName());
                            int totaltime = currenttime - oldtime;

                            if (totaltime > PrisonCD || totaltime < 0)
                            {
                                canCast = true;
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot Imprison again for another " + (PrisonCD - totaltime) + " seconds.");
                                return;
                            }
                        }
                        else
                        {
                            canCast = true;
                        }

                        if (canCast)
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

                               if (!(target2.getGameMode() == GameMode.SURVIVAL))
                               {
                                   return;
                               }

                               Location ploc = player.getLocation();
                               Location tloc = target2.getLocation();

                                if (ploc.distance(tloc) > PrisonDist)
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target out of range.");
                                    return;
                                }

                                int targetparty = -1;
                                int myparty = -2;

                                if (plugin.party.containsKey(player.getName().toLowerCase()))
                                {
                                    myparty = plugin.party.get(player.getName().toLowerCase());
                                }

                                if (plugin.party.containsKey(target2.getName().toLowerCase()))
                                {
                                    targetparty = plugin.party.get(target2.getName().toLowerCase());
                                }

                                if (myparty != targetparty && target2 != player)
                                {
                                    
                                    isPrisoned.add(target2.getName());
                                    PrisonTimer.put(player.getName(), currenttime);

                                    
                                    target2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been Imprisoned by " + player.getName() + " and are in stasis.");
                                    for (Player p : Bukkit.getOnlinePlayers())
                                    {
                                        if (p.getWorld().equals(target2.getWorld()))
                                        {
                                            if (p.getLocation().distance(target2.getLocation()) <= PrisonDist)
                                            {
                                                if (p != target2)
                                                {
                                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() +  " has" + ChatColor.LIGHT_PURPLE + " Imprisoned " 
                                                            + ChatColor.GOLD + target2.getName());
                                                }
                                            }
                                        }
                                    }
                                    target2.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,5));
                                    target2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,100,5));
                                    target2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,100,5));
                                    target2.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,100,5));
                                    
                                    if (target2.hasPotionEffect(PotionEffectType.POISON))
                                    {
                                        target2.removePotionEffect(PotionEffectType.POISON);
                                        target2.addPotionEffect(new PotionEffect(PotionEffectType.POISON,0,0));
                                    }
                                    
                                    if (target2.hasPotionEffect(PotionEffectType.WITHER))
                                    {
                                        target2.removePotionEffect(PotionEffectType.WITHER);
                                        target2.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,0,0));
                                    }
                                    
                                    new Prison(target2);

                                    Bukkit.getServer().getWorld(target2.getWorld().getName()).playEffect(tloc, Effect.SMOKE, 4);
                                    Bukkit.getServer().getWorld(target2.getWorld().getName()).playEffect(tloc, Effect.EXTINGUISH, 4);


                                    for (Player other : Bukkit.getServer().getOnlinePlayers())
                                    {
                                        if ((!other.equals(target2)) && (other.canSee(target2)))
                                        {
                                            other.hidePlayer(target2);
                                        }    
                                    }

                                }
                            }
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }
                }
                
                
            }
            
            if (player.hasPermission("sr.warlock.cast"))
            {
                if (player.getItemInHand().getTypeId() == 337) // clay ball
                {
                    if (player.getInventory().getChestplate() == null 
                            || player.getInventory().getLeggings() == null || player.getInventory().getBoots() == null)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }

                    if (player.getInventory().getChestplate().getTypeId() == 315 && player.getInventory().getLeggings().getTypeId() == 316 
                            && player.getInventory().getBoots().getTypeId() == 317)
                    {

                        if (player.getInventory().getHelmet() != null)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must not wear a helmet for this spell.");
                            return;
                        }

                        World pworld = player.getWorld();

                        BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                            return;
                        }

                        Boolean canCast = false;
                        int currenttime = plugin.getCurrentTime();

                        if (FadeTimer.containsKey(player.getName()))
                        {
                            int oldtime = FadeTimer.get(player.getName());
                            int totaltime = currenttime - oldtime;

                            if (totaltime > FadeCD || totaltime < 0)
                            {
                                canCast = true;
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot Fade again for another " + (FadeCD - totaltime) + " seconds.");
                                return;
                            }
                        }
                        else
                        {
                            canCast = true;
                        }

                        if (canCast)
                        {

                            float power = (float) 1.0;
                            int range = Math.round(FadeDist * power);
                            if (range <= 0) 
                            {
                                range = 25;
                            }
                            if (range > 125) 
                            {
                                range = 125;
                            }

                            BlockIterator iter;
                            try
                            {
                                iter = new BlockIterator(player, range>0&&range<150?range:150);
                            } catch (IllegalStateException e)
                            {
                                iter = null;
                            }


                            HashSet<Location> trail = new HashSet<Location>();

                            Block prev = null;
                            Block found = null;
                            Block b;

                            if (iter != null)
                            {
                                while (iter.hasNext()) 
                                {
                                    b = iter.next();
                                    if (fadeUtils.getTransparentBlocks().contains((byte)b.getTypeId())) 
                                    {
                                        prev = b;
                                        if (smokeTrail) 
                                        {
                                            trail.add(b.getLocation());
                                        }
                                    } else 
                                    {
                                        found = b;
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No target found or out of range. 1");
                                return;
                            }

                            if (found != null)
                            {
                                Location loc = null;
                                if (range > 0 && !inRange(found.getLocation(), player.getLocation(), range)) 
                                {

                                } 
                                else if (!passThroughCeiling && found.getRelative(0,-1,0).equals(prev)) 
                                {
                                    // trying to move upward
                                    if (BlockUtils.isPathable(prev) && BlockUtils.isPathable(prev.getRelative(0,-1,0))) 
                                    {
                                        loc = prev.getRelative(0,-1,0).getLocation();
                                    }

                                } 
                                else if (BlockUtils.isPathable(found.getRelative(0,1,0)) && BlockUtils.isPathable(found.getRelative(0,2,0))) 
                                {
                                    // try to stand on top
                                    loc = found.getLocation();
                                    loc.setY(loc.getY() + 1);
                                } 
                                else if (prev != null && BlockUtils.isPathable(prev) && BlockUtils.isPathable(prev.getRelative(0,1,0))) 
                                {
                                    // no space on top, put adjacent instead
                                    loc = prev.getLocation();
                                }

                                if (loc != null) 
                                {
                                    loc.setX(loc.getX()+.5);
                                    loc.setZ(loc.getZ()+.5);
                                    loc.setPitch(player.getLocation().getPitch());
                                    loc.setYaw(player.getLocation().getYaw());

                                    teleport(player, loc, trail);

                                    FadeTimer.put(player.getName(), currenttime);
                                } 
                                else 
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No target found or out of range. 2");
                                    return;
                                }
                            } 
                            else 
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No target found or out of range. 3");
                                return;
                            }


                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }
                    
                }
            }
        } // end left click
        
        
        
        
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            
            if (player.hasPermission("sr.warlock.cast"))
            {
                if (player.getItemInHand().getTypeId() == 341) // slime ball
                {
                    if (player.getInventory().getHelmet() != null)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must not wear a helmet for this spell.");
                        event.setCancelled(true);
                        return;
                    }
                    
                    if (player.getInventory().getChestplate() == null 
                            || player.getInventory().getLeggings() == null || player.getInventory().getBoots() == null)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }

                    if (player.getInventory().getChestplate().getTypeId() == 315 && player.getInventory().getLeggings().getTypeId() == 316 
                            && player.getInventory().getBoots().getTypeId() == 317)
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

                        Boolean canCast = false;
                        int currenttime = plugin.getCurrentTime();

                        if (SickenTimer.containsKey(player.getName()))
                        {
                            int oldtime = SickenTimer.get(player.getName());
                            int totaltime = currenttime - oldtime;

                            if (totaltime > SickenCD || totaltime < 0)
                            {
                                canCast = true;
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot cast Sicken again for another " + (SickenCD - totaltime) + " seconds.");
                                return;
                            }
                        }
                        else
                        {
                            canCast = true;
                        }

                        if (canCast)
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

                               if (!(target2.getGameMode() == GameMode.SURVIVAL))
                               {
                                   return;
                               }

                               Location ploc = player.getLocation();
                               Location tloc = target2.getLocation();

                                if (ploc.distance(tloc) > SickenDist)
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target out of range.");
                                    return;
                                }

                                int targetparty = -1;
                                int myparty = -2;

                                if (plugin.party.containsKey(player.getName().toLowerCase()))
                                {
                                    myparty = plugin.party.get(player.getName().toLowerCase());
                                }

                                if (plugin.party.containsKey(target2.getName().toLowerCase()))
                                {
                                    targetparty = plugin.party.get(target2.getName().toLowerCase());
                                }

                                if (myparty != targetparty && target2 != player)
                                {
                                    SickenTimer.put(player.getName(), currenttime);

                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You Sicken " + target2.getName() + "!");
                                    target2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are Sickened by " + player.getName() + "!");

                                    target2.damage(SickenDMG, player);
                                    target2.addPotionEffect(new PotionEffect(PotionEffectType.POISON,SickenPoisonDur,1));

                                }


                            }
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            
            if (player.hasPermission("sr.warlock.boneshield"))
            {
                if (player.getItemInHand().getTypeId() == 337) // clay ball
                {
                    if (player.getInventory().getHelmet() != null)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must not wear a helmet for this spell.");
                        return;
                    }
                    
                    if (player.getInventory().getChestplate() == null 
                            || player.getInventory().getLeggings() == null || player.getInventory().getBoots() == null)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    }

                    if (player.getInventory().getChestplate().getTypeId() == 315 && player.getInventory().getLeggings().getTypeId() == 316 
                            && player.getInventory().getBoots().getTypeId() == 317)
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

                        Boolean canCast = false;
                        int currenttime = plugin.getCurrentTime();

                        if (BoneShieldTimer.containsKey(player.getName()))
                        {
                            int oldtime = BoneShieldTimer.get(player.getName());
                            int totaltime = currenttime - oldtime;

                            if (totaltime > BoneShieldCD || totaltime < 0)
                            {
                                canCast = true;
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot create a Bone Shield for another " + (BoneShieldCD - totaltime) + " seconds.");
                                return;
                            }
                        }
                        else
                        {
                            canCast = true;
                        }

                        if (canCast)
                        {
                            BoneShieldTimer.put(player.getName(), currenttime);
                            // Do Bone Armor

                            String pworldname = pworld.getName();
                            int px = player.getLocation().getBlockX();
                            int py = player.getLocation().getBlockY();
                            int pz = player.getLocation().getBlockZ();

                            Location loc1 = null;
                            Location loc2 = null;
                            try
                            {
                                loc1 = new Location(pworld, px - 1, py, pz);
                                loc2 = new Location(pworld, px + 1, py, pz);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            // Craft Wither Skele's

                            try
                            {
                                Entity skele1 = null;
                                Entity skele2 = null;

                                if (loc1 != null)
                                {
                                    skele1 = Bukkit.getServer().getWorld(pworldname).spawnEntity(loc1, EntityType.SKELETON);
                                    int ID = skele1.getEntityId();

                                    SkeletonID.put(ID, skele1);

                                    PlayerID1.put(player.getName(), ID);
                                }
                                if (loc2 != null)
                                {
                                    skele2 = Bukkit.getServer().getWorld(pworldname).spawnEntity(loc2, EntityType.SKELETON);
                                    int ID2 = skele2.getEntityId();

                                    SkeletonID.put(ID2, skele2);

                                    PlayerID2.put(player.getName(), ID2);
                                }




                                EntitySkeleton ent1 = null;
                                EntitySkeleton ent2 = null;

                                if (skele1 != null)
                                {
                                    ent1 = ((CraftSkeleton)skele1).getHandle();
                                }
                                if (skele2 != null)
                                {
                                    ent2 = ((CraftSkeleton)skele2).getHandle();
                                }


                                if (ent1 != null && ent2 != null)
                                {
                                    ent1.setSkeletonType(1);
                                    ent2.setSkeletonType(1);

                                    ent1.setCustomName(player.getName() + "'s Bone Shield");
                                    ent2.setCustomName(player.getName() + "'s Bone Shield");

                                    ent1.setHealth(100);
                                    ent2.setHealth(100);

                                   // ItemStack weap1 = new ItemStack(Item.DIAMOND_AXE);
                                   // ItemStack weap2 = new ItemStack(Item.DIAMOND_SWORD);
                                   // ent1.setEquipment(0, weap1);
                                   // ent2.setEquipment(0, weap2);
                                }

                                hasBoneShield.add(player.getName());
                                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, AbsorbAmp));
                                BoneShieldHealth.put(player.getName(), ShieldHP);

                                new BoneShield(player);
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You summon a Bone Shield around yourself.");

                            } catch (Exception e)
                            {
                                e.printStackTrace();
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Bone Shield failed to create.");
                            }
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing Gold Chest/Legs/Boots to cast spells.");
                        event.setCancelled(true);
                        return;
                    } 
                    
                }
            }
            
            
        }
    }
    
    private class Prison implements Runnable
    {
        Player player;
        int taskID;
        
        int x;
        double HP;
        int hunger;
        Location loc;
        public Prison (Player player)
        {
            x = 0;
            this.player = player;
            this.taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 10);
            
            HP = player.getHealth();
            hunger = player.getFoodLevel();
            loc = player.getLocation();
        }
        
        @Override
        public void run()
        {
            x++;
            
            
            if (x > PrisonDur + 2)
            {
                if (isPrisoned.contains(player.getName()))
                {
                    isPrisoned.remove(player.getName());
                }
                
                for (Player other : Bukkit.getServer().getOnlinePlayers())
                {
                    if ((!other.equals(player)) && (!other.canSee(player)))
                    {
                        other.showPlayer(player);
                    }    
                }
                
               
                
                Party.log.log(Level.WARNING, "Prison task ran longer than expected. Killing task ID " + taskID);
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                return;
            }
            
            if (isPrisoned.contains(player.getName()))
            {
                if (x <= PrisonDur)
                {
                    double newHP = player.getHealth();
                    int newFood = player.getFoodLevel();
                    if (newHP != HP)
                    {
                        player.setHealth(HP);
                    }
                    
                    if (newFood != hunger)
                    {
                        player.setFoodLevel(hunger);
                    }
                    
                    Bukkit.getServer().getWorld(player.getWorld().getName()).playEffect(player.getLocation(), Effect.SMOKE, 4);
                    Bukkit.getServer().getWorld(player.getWorld().getName()).playEffect(player.getLocation(), Effect.EXTINGUISH, 4);
                    
                }
                else
                {
                    if (isPrisoned.contains(player.getName()))
                    {
                        isPrisoned.remove(player.getName());
                    }
                    
                    for (Player other : Bukkit.getServer().getOnlinePlayers())
                    {
                        if ((!other.equals(player)) && (!other.canSee(player)))
                        {
                            other.showPlayer(player);
                        }    
                    }
                    
                    
                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You are no longer imprisoned.");
                    
                }
            }
            else
            {
                for (Player other : Bukkit.getServer().getOnlinePlayers())
                {
                    if ((!other.equals(player)) && (!other.canSee(player)))
                    {
                        other.showPlayer(player);
                    }    
                }
                
                
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You are no longer imprisoned.");
                Bukkit.getServer().getScheduler().cancelTask(taskID);
            }
        }
    }
    
    public HashSet<String> leechKill = new HashSet<String>();
    private class Leech implements Runnable
    {
        Player player;
        int taskID;
        
        int x;
        String name;
        Player warlock;
        
        public Leech (Player player, Player warlock)
        {
            x = 0;
            this.warlock = warlock;
            this.player = player;
            this.taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 60);
        }
        
        @Override
        public void run()
        {
            x++;

            if (x > (LeechDur + 2) || warlock.isDead() || player.isDead())
            {
                if (isLeeched.contains(player.getName()))
                {
                    isLeeched.remove(player.getName());
                }
                
                Party.log.log(Level.WARNING, "Leech task ran longer than expected. Killing task ID " + taskID);
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                return;
            }
            
            if (isLeeched.contains(player.getName()))
            {
                if (x <= LeechDur)
                {
                    double playerCurHP = player.getHealth();
                    double warlockHP = warlock.getHealth();
                    
                    double newPlayerHP = playerCurHP - LeechAmount;
                    double newwarlockHP = warlockHP + LeechAmount;
                    
                    if (newPlayerHP < 1)
                    {
                        leechKill.add(player.getName());
                        player.damage(10, warlock);
                    }
                    else
                    {
                        if (player.getHealth() - 1 > 1)
                        {
                            player.damage(1);
                        }
                        player.setHealth(newPlayerHP);
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + warlock.getName() + " Leeches life from you!");  
                    }

                    if (newwarlockHP > warlock.getMaxHealth())
                    {
                        newwarlockHP = warlock.getMaxHealth();
                    }
                    
                  
                    warlock.setHealth(newwarlockHP);
                    warlock.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You Leech " + player.getName() + "'s life!");
                   
                    
                }
                else
                {
                    if (isLeeched.contains(player.getName()))
                    {
                        isLeeched.remove(player.getName());
                    }

                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + warlock.getName() + " stops Leeching life from you.");
                    warlock.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You stop Leeching life from " + player.getName());
                }
            }
            else
            {
                if (isLeeched.contains(player.getName()))
                {
                    isLeeched.remove(player.getName());
                }
                
                
                
                Bukkit.getServer().getScheduler().cancelTask(taskID);
            }
        }
        
    }
    
    private class BoneShield implements Runnable
    {
        Player player;
        int taskID;
        
        int x;
        
        public BoneShield(Player player)
        {
            x = 0;
            this.player = player;
            this.taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
        }

        @Override
        public void run() 
        {
            x++;
            
            
            if (x > (BoneShieldDur + 5))
            {
                if (hasBoneShield.contains(player.getName()))
                {
                    hasBoneShield.remove(player.getName());
                }
                
                if (BoneShieldHealth.containsKey(player.getName()))
                {
                    BoneShieldHealth.remove(player.getName());
                }
                
                Party.log.log(Level.WARNING, "BoneShield task ran longer than expected. Killing task ID " + taskID);
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                return;
            }
            
            if (hasBoneShield.contains(player.getName()))
            {
                int skele1 = PlayerID1.get(player.getName());
                int skele2 = PlayerID2.get(player.getName());

                Entity e1 = SkeletonID.get(skele1);
                Entity e2 = SkeletonID.get(skele2);
                
                if (x > BoneShieldDur)
                {
                    if (!(e1.isDead()))
                    {
                        e1.remove();
                    }
                    if (!(e2.isDead()))
                    {
                        e2.remove();
                    }
                    
                    if (SkeletonID.containsKey(skele1))
                    {
                        SkeletonID.remove(skele1);
                    }
                    if (SkeletonID.containsKey(skele2))
                    {
                        SkeletonID.remove(skele2);
                    }
                    
                    if (PlayerID1.containsKey(player.getName()))
                    {
                        PlayerID1.remove(player.getName());
                    }
                    if (PlayerID2.containsKey(player.getName()))
                    {
                        PlayerID2.remove(player.getName());
                    }
                    
                    if (BoneShieldHealth.containsKey(player.getName()))
                    {
                        BoneShieldHealth.remove(player.getName());
                    }
                    
                    hasBoneShield.remove(player.getName());
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Bone Shield has disappeared.");
                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                }

                if (x <= BoneShieldDur)
                {
                    if (e1 != null && e1.getWorld() == player.getWorld())
                    {
                        if (player.getLocation().distance(e1.getLocation()) > 2.0D)
                        {
                            if (e1.getLocation().distance((player.getLocation())) > 15.0D)
                            {
                                e1.teleport(player);
                            }
                            /*
                            else
                            {
                                Navigation nav = ((CraftLivingEntity)e1).getHandle()
                                nav.a(player.getLocation().getX() + 2.0D, player.getLocation().getY(), player.getLocation().getZ() + 2.0D, 0.5F);
                            }
                            */
                        }
                    }

                    if (e2 != null && e2.getWorld() == player.getWorld())
                    {
                        if (player.getLocation().distance(e2.getLocation()) > 2.0D)
                        {
                            if (e2.getLocation().distance((player.getLocation())) > 15.0D)
                            {
                                e2.teleport(player);
                            }
                            /*
                            else
                            {
                                Navigation nav = ((CraftLivingEntity)e1).getHandle().getNavigation();
                                nav.a(player.getLocation().getX() - 2.0D, player.getLocation().getY(), player.getLocation().getZ() - 2.0D, 0.5F);
                            }
                            */
                        }
                    }
                }
            }
            else
            {
                int skele1 = PlayerID1.get(player.getName());
                int skele2 = PlayerID2.get(player.getName());

                Entity e1 = SkeletonID.get(skele1);
                Entity e2 = SkeletonID.get(skele2);
                
                if (!(e1.isDead()))
                {
                    e1.remove();
                }
                if (!(e2.isDead()))
                {
                    e2.remove();
                }

                if (SkeletonID.containsKey(skele1))
                {
                    SkeletonID.remove(skele1);
                }
                if (SkeletonID.containsKey(skele2))
                {
                    SkeletonID.remove(skele2);
                }

                if (PlayerID1.containsKey(player.getName()))
                {
                    PlayerID1.remove(player.getName());
                }
                if (PlayerID2.containsKey(player.getName()))
                {
                    PlayerID2.remove(player.getName());
                }

                if (hasBoneShield.contains(player.getName()))
                {
                    hasBoneShield.remove(player.getName());
                }
                
                
                if (BoneShieldHealth.containsKey(player.getName()))
                {
                    BoneShieldHealth.remove(player.getName());
                }
                
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Bone Shield has disappeared.");
                Bukkit.getServer().getScheduler().cancelTask(taskID);
            }

        }
        
    }
    
    
    public void cleanUP()
    {
        if (SkeletonID.size() > 0)
        {
            for (Entity e : SkeletonID.values())
            {
                if (!e.isDead())
                {
                    e.remove();
                }
            }
        }
    }
}
