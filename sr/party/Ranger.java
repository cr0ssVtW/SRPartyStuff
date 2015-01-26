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
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * @author Cross
 */
public class Ranger implements Listener
{
    public Party plugin;
    
    public Ranger (Party plugin)
    {
        this.plugin = plugin;
    }
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    
    public int NatureCD = 35;
    public int NatureDur = 15;
    
    public int NatureSpeedDur = 160;
    
    public int SpeedAmp = 2;
    
    public double NatureHeal = 4;
    public double NatureChance = 0.50;
    public double NatureRange = 12;
    
    public double multi = 2.0D;

    public int ShadowsCD = 25;
    public int ShadowsDur = 5;
    public int ShadowSpeedDur = 220;
    
    public int ChainShotCD = 7;
    public double ChainShotRange = 16;
    public int ChainSlowDur = 100;
    public int ChainSlowAmp = 4;
    public double ChainShotDmg = 10;
    
    
    public double BarkSkinReduction = 0.65D;
    
    public HashMap<String, Double> NatureDamage = new HashMap<String, Double>();
    public HashMap<String, Integer> NatureTimer = new HashMap<String, Integer>();
    public HashSet<String> NatureActive = new HashSet<String>();
    
    public HashMap<String, Integer> ShadowsTimer = new HashMap<String, Integer>();
    public HashSet<String> inShadows = new HashSet<String>();
    
    public HashMap<String, Integer> ChainShotTimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> ChainShotAmount = new HashMap<String, Integer>();
    public HashSet<String> isChainShot = new HashSet<String>();
    
    public HashMap<String, Integer> shotCount = new HashMap<String, Integer>();
    public HashMap<String, UUID> shotTarget = new HashMap<String, UUID>();
    public HashSet<UUID> shotID = new HashSet<UUID>();
    
    public HashMap<String, Integer> shoveCD = new HashMap<String, Integer>();
    public int ShoveCD = 5;
    private double ShoveVelocity = (12 / 7D);
    private double ShoveYVelocity = (5 / 10D);
    
    @EventHandler
    public void onEntityDamage (EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity))
        {
            return;
        }
        
        LivingEntity target = (LivingEntity) event.getEntity();
        
        Player ptarget = null;
        
        if (target instanceof Player)
        {
            ptarget = (Player) target;
        }
        
        if (!(event instanceof EntityDamageByEntityEvent))
        {
            return;
        }
        
        EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent) event;
        
        if (event_EE.getDamager() instanceof Player)
        {
            Player attacker = (Player) event_EE.getDamager();
            
            if (PL.stuntarget.containsKey(attacker.getName().toLowerCase()) || Paladin.isStunned.contains(attacker.getName()) || ChaosCrusader.chaosstunned.containsKey(attacker.getName()))
            {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                return;
            }
            
            if (attacker.isOp() || attacker.hasPermission("sr.ranger.bypass"))
            {
                return;
            }
            else
            if (attacker.hasPermission("sr.ranger.cast"))
            {
                if (attacker.getItemInHand().getType() == Material.BOW)
                {
                    boolean canShove = false;
                    
                    if (shoveCD.containsKey(attacker.getName()))
                    {
                        int time = plugin.getCurrentTime();
                        int timecheck = shoveCD.get(attacker.getName());
                        int totaltime = time - timecheck;
                        
                        if (totaltime > ShoveCD || totaltime < 0)
                        {
                            shoveCD.remove(attacker.getName());
                            canShove = true;
                        }
                        else
                        {
                            canShove = false;
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Can't shove for another " + (ShoveCD - totaltime) + " seconds.");
                        }
                    }
                    else
                    {
                        canShove = true;
                    }
                    
                    if (canShove)
                    {
                        Vector v = attacker.getLocation().getDirection();
                        v.setY(0).normalize().multiply(ShoveVelocity).setY(ShoveYVelocity);
                        target.setVelocity(v);
                        if (target instanceof Player)
                        {
                            Player ptarget2 = (Player) target;
                            ptarget2.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + attacker.getName() + ChatColor.RED + " shoves you back!");
                        }
                        attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You shove your target back!");
                        
                        int time = plugin.getCurrentTime();
                        shoveCD.put(attacker.getName(), time);
                    }
                }
                else
                {
                    attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Rangers can only shoot arrows or melee with a bow to deal damage.");
                    event.setCancelled(true);
                    return;
                }
                
            }
        }
        
        if (!(event_EE.getDamager() instanceof Arrow))
        {
            return;
        }
        
        Projectile projectile = (Projectile) event_EE.getDamager();
        
        if (ptarget != null)
        {
            if (ptarget.hasPermission("sr.ranger.cast"))
            {
                double dmg = event.getDamage();

                double newDmg = dmg * BarkSkinReduction;

                event.setDamage(newDmg);
            }
        }
        
        /*
        else
        {
            double dmg = event.getDamage();
            double newDmg = dmg + 2;
            double newDmg2 = newDmg * 1.33;
            
            event.setDamage(newDmg2);
        }
        */
        
        if (!(projectile.getShooter() instanceof Player))
        {
            return;
        }

        Player shooter = (Player) projectile.getShooter();
        
        if (shooter.hasPermission("sr.ranger.cast"))
        {
            UUID id = projectile.getUniqueId();
            if (shotID.contains(id))
            {
               // Bukkit.broadcastMessage("Hit player. with id: " + id);
                
                if (shotTarget.containsKey(shooter.getName()))
                {
                    UUID targetID = shotTarget.get(shooter.getName());

                    if (target.getUniqueId().equals(targetID))
                    {
                        if (shotCount.containsKey(shooter.getName()))
                        {
                            if (target instanceof Player)
                            {
                                World pworld = shooter.getWorld();

                                BlockVector pt = BukkitUtil.toVector(shooter.getLocation().getBlock());

                                RegionManager regionManager = worldGuard.getRegionManager(pworld);
                                ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                                if (!(set.allows(DefaultFlag.PVP)))
                                {
                                    shotCount.remove(shooter.getName());
                                    return;
                                }
                            }
                            
                            int oldcount = shotCount.get(shooter.getName());
                            int count = oldcount + 1;
                            shotCount.put(shooter.getName(), count);

                            //Bukkit.broadcastMessage("The count is: " + count);
                            if (count == 2)
                            {
                                double dmg = event.getDamage();
                                double newDmg = dmg + 1;
                                event.setDamage(newDmg);
                                if (NatureActive.contains(shooter.getName()))
                                {
                                    if (NatureDamage.containsKey(shooter.getName()))
                                    {
                                        double olddmg = NatureDamage.get(shooter.getName());
                                        NatureDamage.remove(shooter.getName());
                                        NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                    }
                                    else
                                    {
                                        NatureDamage.put(shooter.getName(), dmg);
                                    }
                                }
                               // shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "2 hits.");
                                if (ptarget != null)
                                {
                                    shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 2 (+1)" + "(" + ChatColor.GOLD + ptarget.getName() + ChatColor.DARK_GREEN +  ")");
                                }
                                else
                                {
                                    shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 2 (+1)");
                                }
                            }

                            if (count == 3)
                            {
                                double dmg = event.getDamage();
                                double newDmg = dmg + 3;
                                event.setDamage(newDmg);
                                if (NatureActive.contains(shooter.getName()))
                                {
                                    if (NatureDamage.containsKey(shooter.getName()))
                                    {
                                        double olddmg = NatureDamage.get(shooter.getName());
                                        NatureDamage.remove(shooter.getName());
                                        NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                    }
                                    else
                                    {
                                        NatureDamage.put(shooter.getName(), dmg);
                                    }
                                }
                              //  shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "3 hits!");
                                if (ptarget != null)
                                {
                                    shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 3 (+3)" + "(" + ChatColor.GOLD + ptarget.getName() + ChatColor.DARK_GREEN +  ")");
                                }
                                else
                                {
                                    shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 3 (+3)");
                                }
                            }

                            if (count == 4)
                            {
                                double dmg = event.getDamage();
                                double newDmg = dmg + 5;
                                event.setDamage(newDmg);
                                if (NatureActive.contains(shooter.getName()))
                                {
                                    if (NatureDamage.containsKey(shooter.getName()))
                                    {
                                        double olddmg = NatureDamage.get(shooter.getName());
                                        NatureDamage.remove(shooter.getName());
                                        NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                    }
                                    else
                                    {
                                        NatureDamage.put(shooter.getName(), dmg);
                                    }
                                }
                               // shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "4 hits!");
                                if (ptarget != null)
                                {
                                    shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 4 (+5)" + "(" + ChatColor.GOLD + ptarget.getName() + ChatColor.DARK_GREEN +  ")");
                                }
                                else
                                {
                                    shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 4 (+5)");
                                }
                            }

                            if (count >= 5)
                            {
                                double dmg = event.getDamage();
                                double newDmg = dmg + 7;
                                if (NatureActive.contains(shooter.getName()))
                                {
                                    if (NatureDamage.containsKey(shooter.getName()))
                                    {
                                        double olddmg = NatureDamage.get(shooter.getName());
                                        NatureDamage.remove(shooter.getName());
                                        NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                    }
                                    else
                                    {
                                        NatureDamage.put(shooter.getName(), dmg);
                                    }
                                }
                                target.getWorld().strikeLightningEffect(target.getLocation());
                                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,100,2));
                                shooter.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,1));
                                event.setDamage(newDmg);
                                shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "The Spirit of Nature graces you with the elements!");
                                shotCount.remove(shooter.getName());
                                shotTarget.remove(shooter.getName());
                            }
                        }
                    }
                    else
                    {
                        shotTarget.put(shooter.getName(), target.getUniqueId());
                        shotCount.put(shooter.getName(), 1);
                        //Bukkit.broadcastMessage("The count is: 1");
                        if (ptarget != null)
                        {
                            World pworld = shooter.getWorld();

                            BlockVector pt = BukkitUtil.toVector(shooter.getLocation().getBlock());

                            RegionManager regionManager = worldGuard.getRegionManager(pworld);
                            ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                            if (!(set.allows(DefaultFlag.PVP)))
                            {
                                shotCount.remove(shooter.getName());
                                return;
                            }
                            else
                            {
                                shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 1" + "(" + ChatColor.GOLD + ptarget.getName() + ChatColor.DARK_GREEN +  ")");
                                if (NatureActive.contains(shooter.getName()))
                                {
                                    if (NatureDamage.containsKey(shooter.getName()))
                                    {
                                        double olddmg = NatureDamage.get(shooter.getName());
                                        double newDmg = event.getDamage();
                                        NatureDamage.remove(shooter.getName());
                                        NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                    }
                                    else
                                    {
                                        NatureDamage.put(shooter.getName(), event.getDamage());
                                    }
                                }
                            }
                            
                        }
                        else
                        {
                            shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 1");
                            if (NatureActive.contains(shooter.getName()))
                            {
                                if (NatureDamage.containsKey(shooter.getName()))
                                {
                                    double olddmg = NatureDamage.get(shooter.getName());
                                    double newDmg = event.getDamage();
                                    NatureDamage.remove(shooter.getName());
                                    NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                }
                                else
                                {
                                    NatureDamage.put(shooter.getName(), event.getDamage());
                                }
                            }
                        }
                    }
                }
                else
                {
                    shotTarget.put(shooter.getName(), target.getUniqueId());
                    shotCount.put(shooter.getName(), 1);
                    //Bukkit.broadcastMessage("The count is: 1");
                    if (ptarget != null)
                    {
                        World pworld = shooter.getWorld();

                        BlockVector pt = BukkitUtil.toVector(shooter.getLocation().getBlock());

                        RegionManager regionManager = worldGuard.getRegionManager(pworld);
                        ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                        if (!(set.allows(DefaultFlag.PVP)))
                        {
                            shotCount.remove(shooter.getName());
                            return;
                        }
                        else
                        {
                            shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 1" + "(" + ChatColor.GOLD + ptarget.getName() + ChatColor.DARK_GREEN +  ")");
                            if (NatureActive.contains(shooter.getName()))
                            {
                                if (NatureDamage.containsKey(shooter.getName()))
                                {
                                    double olddmg = NatureDamage.get(shooter.getName());
                                    double newDmg = event.getDamage();
                                    NatureDamage.remove(shooter.getName());
                                    NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                }
                                else
                                {
                                    NatureDamage.put(shooter.getName(), event.getDamage());
                                }
                            }
                        }
                    }
                    else
                    {
                        shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Count: 1");
                        if (NatureActive.contains(shooter.getName()))
                        {
                            if (NatureDamage.containsKey(shooter.getName()))
                            {
                                double olddmg = NatureDamage.get(shooter.getName());
                                double newDmg = event.getDamage();
                                NatureDamage.remove(shooter.getName());
                                NatureDamage.put(shooter.getName(), olddmg + newDmg);
                            }
                            else
                            {
                                NatureDamage.put(shooter.getName(), event.getDamage());
                            }
                        }
                    }
                }
                
                shotID.remove(id);
            }
            
            
            if (NatureActive.contains(shooter.getName()))
            {
                Location aloc = projectile.getLocation();

                double random = Math.random();

                if (random <= NatureChance)
                {
                    int targetparty = -1;

                    int myparty = -2;

                    if (plugin.party.containsKey(shooter.getName().toLowerCase()))
                    {
                        myparty = plugin.party.get(shooter.getName().toLowerCase());
                    }

                    shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "You convoke the Spirit of Nature!");

                    for (Entity e : projectile.getNearbyEntities(NatureRange, NatureRange, NatureRange))
                    {
                        if (e instanceof LivingEntity)
                        {
                            LivingEntity ent = (LivingEntity) e;
                            
                            if (ent instanceof Player)
                            {
                                if (ent.getWorld().equals(projectile.getWorld()))
                                {
                                    if (ent.getLocation().distance(aloc) <= NatureRange)
                                    {
                                        
                                        Player p = (Player) ent;
                                        if (plugin.party.containsKey(p.getName().toLowerCase()))
                                        {
                                            targetparty = plugin.party.get(p.getName().toLowerCase());
                                        }

                                        if (myparty != targetparty && p != shooter)
                                        {
                                            p.getWorld().strikeLightningEffect(p.getLocation());
                                            p.damage(NatureHeal);
                                            //p.addPotionEffect(new PotionEffect(PotionEffectType.POISON,100,1));
                                            //Bukkit.broadcastMessage("Hit " + p.getName() + " with Wither.");
                                            double heal = shooter.getHealth() + NatureHeal;

                                            if (heal > shooter.getMaxHealth())
                                            {
                                                shooter.setHealth(shooter.getMaxHealth());
                                            }
                                            else
                                            {
                                                shooter.setHealth(heal);
                                            }

                                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "The Spirit of Nature strikes!");
                                        }
                                    
                                        /* // not a player
                                        else
                                        {
                                            ent.getWorld().strikeLightningEffect(ent.getLocation());
                                            ent.damage(NatureHeal);

                                            double heal = shooter.getHealth() + NatureHeal;

                                            if (heal > shooter.getMaxHealth())
                                            {
                                                shooter.setHealth(shooter.getMaxHealth());
                                            }
                                            else
                                            {
                                                shooter.setHealth(heal);
                                            }
                                        }
                                        */
                                    
                                    }
                                }
                            }
                            
                        }
                        
                    }
                }                    
            }
            
            
            
            if (isChainShot.contains(shooter.getName()))
            {
                isChainShot.remove(shooter.getName());
                
                event.setDamage(ChainShotDmg);
                
                double dmg = event.getDamage();
                double newDmg = dmg;
                int targetparty = -1;
                                    
                int myparty = -2;

                if (plugin.party.containsKey(shooter.getName().toLowerCase()))
                {
                    myparty = plugin.party.get(shooter.getName().toLowerCase());
                }
                int x = 1;
                
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,ChainSlowDur,ChainSlowAmp));
                if (ptarget != null)
                {
                    ptarget.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You've been slowed by " + shooter.getName());
                }
                
                for (Entity e : shooter.getNearbyEntities(ChainShotRange, ChainShotRange, ChainShotRange))
                {
                    if (e instanceof LivingEntity)
                    {
                        LivingEntity ent = (LivingEntity) e;
                        if (ent.getWorld().equals(target.getWorld()))
                        {
                            if (ent.getLocation().distance(target.getLocation()) <= ChainShotRange)
                            {
                                if (ent instanceof Player)
                                {
                                    World pworld = shooter.getWorld();

                                    BlockVector pt = BukkitUtil.toVector(shooter.getLocation().getBlock());

                                    RegionManager regionManager = worldGuard.getRegionManager(pworld);
                                    ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                                    if (!(set.allows(DefaultFlag.PVP)))
                                    {
                                        return;
                                    }
                                    
                                    
                                    Player p = (Player) ent;
                                    if (plugin.party.containsKey(p.getName().toLowerCase()))
                                    {
                                        targetparty = plugin.party.get(p.getName().toLowerCase());
                                    }
                                    
                                    if ((myparty != targetparty && p != shooter) && (myparty != targetparty && p != target))
                                    {
                                        x++;

                                        if (x > 3)
                                        {
                                            return;
                                        }

                                        newDmg = newDmg - 2;
                                        
                                        if (newDmg <= 0)
                                        {
                                            event.setCancelled(true);
                                            return;
                                        }

                                        //Bukkit.broadcastMessage("shooter is: " + shooter.getName());
                                        //Bukkit.broadcastMessage("Target is: " + p.getName());
                                        //Bukkit.broadcastMessage("Damage is: " + newDmg);
                                        
                                        if (NatureActive.contains(shooter.getName()))
                                        {
                                            if (NatureDamage.containsKey(shooter.getName()))
                                            {
                                                double olddmg = NatureDamage.get(shooter.getName());
                                                NatureDamage.remove(shooter.getName());
                                                NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                            }
                                            else
                                            {
                                                NatureDamage.put(shooter.getName(), event.getDamage());
                                            }
                                        }
                                        
                                        p.damage(newDmg);
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,ChainSlowDur,ChainSlowAmp));
                                        p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You've been slowed by " + shooter.getName());

                                        //Bukkit.broadcastMessage("X IS: " + x + " && p IS: " + p.getName());
                                    }
                                }
                                /*
                                else
                                {
                                    x++;
                                    if (x > 3)
                                    {
                                        return;
                                    }
                                    
                                    newDmg = newDmg - 2;

                                    if (newDmg <= 0)
                                    {
                                        event.setCancelled(true);
                                        return;
                                    }
                                    
                                    if (NatureActive.contains(shooter.getName()))
                                    {
                                        if (NatureDamage.containsKey(shooter.getName()))
                                        {
                                            double olddmg = NatureDamage.get(shooter.getName());
                                            NatureDamage.remove(shooter.getName());
                                            NatureDamage.put(shooter.getName(), olddmg + newDmg);
                                        }
                                        else
                                        {
                                            NatureDamage.put(shooter.getName(), event.getDamage());
                                        }
                                    }
                                    
                                    
                                    ent.damage(newDmg);
                                    ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,ChainSlowDur,ChainSlowAmp));
                                }
                                */ //not  a player
                            }
                        }
                    }
                }
            }
        }
        
    }
    
    @EventHandler
    public void onEntityShoot (EntityShootBowEvent event)
    {
        Projectile projectile = (Projectile) event.getProjectile();

        if (!(projectile.getShooter() instanceof Player)) 
        {
            return;
        }

        Player player = (Player) projectile.getShooter();
        
        if (!(player.hasPermission("sr.ranger.cast")))
        {
            return;
        }
        
        float force = event.getForce();
        
        if (force >= 0.30)
        {
            UUID id = projectile.getUniqueId();
            shotID.add(id);
        }
        else
        {
            if (shotTarget.containsKey(player.getName()))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Didn't draw bow back far enough. Count reset.");
                shotTarget.remove(player.getName());
            }
        }
        
        
        //Bukkit.broadcastMessage("UUID shot is: " + id);
        
        if (isChainShot.contains(player.getName()))
        {
            int currenttime = plugin.getCurrentTime();

            ChainShotTimer.put(player.getName(), currenttime);
        }
    }

    @EventHandler
    public void onProjectileHit (ProjectileHitEvent event)
    {
        Entity entity = event.getEntity();
        
        if (!(entity instanceof Arrow))
        {
            return;
        }
        
        if (entity instanceof Arrow)
        {
            Arrow arrow = (Arrow) entity;
            LivingEntity en = arrow.getShooter();
            
            if (!(en instanceof Player))
            {
                return;
            }
            
            if (en instanceof Player)
            {
                final Player player = (Player) en;
                
                if (!(player.hasPermission("sr.ranger.cast")))
                {
                    return;
                }
                
                final UUID id = arrow.getUniqueId();
                
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(shotID.contains(id))
                        {
                            shotTarget.remove(player.getName());
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "Shot missed. Count reset.");
                            //Bukkit.broadcastMessage("Shot Traget Cleared.");
                            //Bukkit.broadcastMessage("ID on hit is: " + id + " and removed.");
                            shotID.remove(id);
                        }
                    }
                }, 1L);
                
                
                if (inShadows.contains(player.getName()))
                {
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (p != player)
                        {
                            if (!(p.canSee(player)))
                            {
                                p.showPlayer(player);
                            }
                        }
                    }
                }

            }
        }
    }
    
    @EventHandler
    public void onQuit (PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        
        if (inShadows.contains(player.getName()))
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (p != player)
                {
                    if (!(p.canSee(player)))
                    {
                        p.showPlayer(player);
                    }
                }
            }
            
            inShadows.remove(player.getName());
        }
    }
    
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
        
        if (inShadows.contains(player.getName()))
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (p != player)
                {
                    if (!(p.canSee(player)))
                    {
                        p.showPlayer(player);
                    }
                }
            }
        }
        
        /*
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            
        } // end Right Click
        */
        
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            ItemStack item = player.getItemInHand();
            
            if (item != null)
            {
                if (!(player.hasPermission("sr.ranger.cast")))
                {
                    return;
                }
                
                
                if (item.getTypeId() == 261) // Bow // Chain Shot
                {
                    if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null
                            && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null)
                    {
                        if (player.getInventory().getHelmet().getTypeId() == 306 && player.getInventory().getChestplate().getTypeId() == 299 // iron helm and leather chest
                                && player.getInventory().getLeggings().getTypeId() == 300 && player.getInventory().getBoots().getTypeId() == 301) // leather legs and boots
                        {
                            
                            Boolean canCast = false;
                            int currenttime = plugin.getCurrentTime();

                            if (ChainShotTimer.containsKey(player.getName()))
                            {
                                int oldtime = ChainShotTimer.get(player.getName());
                                int totaltime = currenttime - oldtime;

                                if (totaltime > ChainShotCD || totaltime < 0)
                                {
                                    canCast = true;
                                }
                                else
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Cannot Chain Shot again for another " 
                                            + (ChainShotCD - totaltime) + " seconds.");
                                    return;
                                }
                            }
                            else
                            {
                                canCast = true;
                            }
                            
                            if (canCast)
                            {
                                isChainShot.add(player.getName());
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "You ready your Chain Shot.");
                            }
                            
                        } // end armor check
                    } // end armor null check
                } // end item id check
                
                if (item.getTypeId() == 370) // Ghast Tear // Hide in Shadows
                {
                    if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null
                            && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null)
                    {
                        if (player.getInventory().getHelmet().getTypeId() == 306 && player.getInventory().getChestplate().getTypeId() == 299 // iron helm and leather chest
                                && player.getInventory().getLeggings().getTypeId() == 300 && player.getInventory().getBoots().getTypeId() == 301) // leather legs and boots
                        {
                            
                            Boolean canCast = false;
                            int currenttime = plugin.getCurrentTime();

                            if (ShadowsTimer.containsKey(player.getName()))
                            {
                                int oldtime = ShadowsTimer.get(player.getName());
                                int totaltime = currenttime - oldtime;

                                if (totaltime > ShadowsCD || totaltime < 0)
                                {
                                    canCast = true;
                                }
                                else
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Cannot Hide In Shadows again for another " + (ShadowsCD - totaltime) + " seconds.");
                                    return;
                                }
                            }
                            else
                            {
                                canCast = true;
                            }
                            
                            if (canCast)
                            {
                                ShadowsTimer.put(player.getName(), currenttime);
                                inShadows.add(player.getName());
                                
                                new HideInShadows(player);
                                
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,ShadowSpeedDur,SpeedAmp));
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "You Hide In Shadows.");
                            }
                            
                         } // end armor check
                         else
                         {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing an Iron Helmet and Leather Chest/Legs/Boots to cast this.");
                         }
                    } // end armor null check
                    else
                    {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing an Iron Helmet and Leather Chest/Legs/Boots to cast this.");
                    }
                } // end item id check
                
                if (item.getTypeId() == 18) // leaves // Spirit of Nature
                {
                    if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null
                            && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null)
                    {
                        if (player.getInventory().getHelmet().getTypeId() == 306 && player.getInventory().getChestplate().getTypeId() == 299 // iron helm and leather chest
                                && player.getInventory().getLeggings().getTypeId() == 300 && player.getInventory().getBoots().getTypeId() == 301) // leather legs and boots
                        {
                            
                            Boolean canCast = false;
                            int currenttime = plugin.getCurrentTime();

                            if (NatureTimer.containsKey(player.getName()))
                            {
                                int oldtime = NatureTimer.get(player.getName());
                                int totaltime = currenttime - oldtime;

                                if (totaltime > NatureCD || totaltime < 0)
                                {
                                    canCast = true;
                                }
                                else
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Can't invoke the Spirit of Nature for another " + (NatureCD - totaltime) + " seconds.");
                                    return;
                                }
                            }
                            else
                            {
                                canCast = true;
                            }
                            
                            if (canCast)
                            {
                                NatureTimer.put(player.getName(), currenttime);
                                NatureActive.add(player.getName());
                                
                                new Nature(player);
                                
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,NatureSpeedDur,SpeedAmp));
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You invoke the " + ChatColor.DARK_GREEN + "Spirit of Nature!");
                                
                                for (Player p : Bukkit.getOnlinePlayers())
                                {
                                    if (p.getWorld().equals(player.getWorld()))
                                    {
                                        if (p.getLocation().distance(player.getLocation()) < 15)
                                        {
                                            if (p != player)
                                            {
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName() + " is invoking the " + ChatColor.DARK_GREEN + "Spirit of Nature");
                                            }
                                        }
                                    }
                                }
                            }
                            
                            
                        } // end armor check
                        else
                        {
                           player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing an Iron Helmet and Leather Chest/Legs/Boots to cast this.");
                        }
                    } // end armor null check
                    else
                    {
                       player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing an Iron Helmet and Leather Chest/Legs/Boots to cast this.");
                    }
                } // end item id check
            } // end item null check     
        } // end Left Click
    } // end interact
    
    
    private class HideInShadows implements Runnable
    {
        Player player;
        int taskID;
        
        int x;
        public HideInShadows(Player player)
        {
            x = 0;
            this.player = player;
            this.taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 40);
        }
        
        @Override
        public void run()
        {
            x++;
            
            if (x > ShadowsDur + 1)
            {
                if (inShadows.contains(player.getName()))
                {
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (p != player)
                        {
                            if (!(p.canSee(player)))
                            {
                                p.canSee(player);
                            }
                        }
                        
                    }
                    
                    inShadows.remove(player.getName());
                }
                
                Party.log.log(Level.WARNING, "Hide in Shadows task ran longer than expected. Killing task ID " + taskID);
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                return;
            }
            
            if (inShadows.contains(player.getName()))
            {
                if (x <= ShadowsDur)
                {
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (p.canSee(player))
                        {
                            if (p != player)
                            {
                                p.hidePlayer(player);
                            }
                        }
                    }
                        
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,ShadowSpeedDur,SpeedAmp));
                }
                else
                {
                    if (inShadows.contains(player.getName()))
                    {
                        inShadows.remove(player.getName());
                    }
                    
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (!(p.canSee(player)))
                        {
                            if (p != player)
                            {
                                p.showPlayer(player);
                            }
                        }
                    }
                    
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are no longer hiding in shadows!");
                    
                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                    return;
                }
            }
            else
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    if (p.getWorld().equals(player.getWorld()))
                    {
                        if (p != player)
                        {
                            if (!(p.canSee(player)))
                            {
                                p.showPlayer(player);
                            }
                        }
                    }
                }
                
                if (inShadows.contains(player.getName()))
                {
                    inShadows.remove(player.getName());
                }
                
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are no longer hiding in shadows!");
                
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                return;
            }
        }
    }
    
    private class Nature implements Runnable
    {
        Player player;
        int taskID;
        
        int x;
        
        double HP;
        
        Color chestStart;
        Color legStart;
        Color bootStart;
        
        Color dye;
        
        public Nature(Player player)
        {
            x = 0;
            this.player = player;
            this.taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
            
            HP = player.getHealth();
            
            dye = Color.fromRGB(34,139,34);
            
        }
        
        @Override
        public void run()
        {
            x++;
            
            if (x > NatureDur + 2)
            {
                if (NatureActive.contains(player.getName()))
                {
                    NatureActive.remove(player.getName());
                }
                
                
                Party.log.log(Level.WARNING, "Nature task ran longer than expected. Killing task ID " + taskID);
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                return;
            }
            
            if (NatureActive.contains(player.getName()))
            {
                if (x <= NatureDur)
                {
                    if (player.hasPotionEffect(PotionEffectType.SLOW))
                    {
                        player.removePotionEffect(PotionEffectType.SLOW);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,0,0));
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "The Spirit of Nature zephyrs you!");
                        
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,NatureSpeedDur,SpeedAmp));
                    }
                    
                    double newHP = player.getHealth();

                    if (player.getInventory().getChestplate().getTypeId() == 299 && player.getInventory().getChestplate() != null)
                    {
                        ItemStack chest = player.getInventory().getChestplate();
                        ItemMeta meta = chest.getItemMeta();
                        
                        LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                        
                        chestStart = lam.getColor();
                        
                        lam.setColor(dye);
                        
                        chest.setItemMeta(lam);
                    }
                    
                    if (player.getInventory().getLeggings().getTypeId() == 300 && player.getInventory().getLeggings() != null)
                    {
                        ItemStack legs = player.getInventory().getLeggings();
                        
                        ItemMeta meta = legs.getItemMeta();
                        
                        LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                        
                        legStart = lam.getColor();
                        
                        lam.setColor(dye);
                        legs.setItemMeta(lam);
                    }

                    if (player.getInventory().getBoots().getTypeId() == 301 && player.getInventory().getBoots() != null)
                    {
                        ItemStack boots = player.getInventory().getBoots();
                        
                        ItemMeta meta = boots.getItemMeta();
                        
                        LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                        
                        bootStart = lam.getColor();
                        
                        lam.setColor(dye);
                        boots.setItemMeta(lam);
                    }

                    //Bukkit.broadcastMessage("Lost is now: " + lost);
                }
                else
                {
                    if (NatureActive.contains(player.getName()))
                    {
                        NatureActive.remove(player.getName());
                    }
                    
                    if (player.getInventory().getChestplate().getTypeId() == 299 && player.getInventory().getChestplate() != null)
                    {
                        ItemStack chest = player.getInventory().getChestplate();
                        ItemMeta meta = chest.getItemMeta();
                        
                        LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                        
                        lam.setColor(chestStart);
                        chest.setItemMeta(lam);
                    }
                    
                    if (player.getInventory().getLeggings().getTypeId() == 300 && player.getInventory().getLeggings() != null)
                    {
                        ItemStack legs = player.getInventory().getLeggings();
                        
                        ItemMeta meta = legs.getItemMeta();
                        
                        LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                        
                        
                        lam.setColor(legStart);
                        legs.setItemMeta(lam);
                    }

                    if (player.getInventory().getBoots().getTypeId() == 301 && player.getInventory().getBoots() != null)
                    {
                        ItemStack boots = player.getInventory().getBoots();
                        
                        ItemMeta meta = boots.getItemMeta();
                        
                        LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                        
                        lam.setColor(bootStart);
                        boots.setItemMeta(lam);
                    }
                    
                    double dmgdone = 0;
                    if (NatureDamage.containsKey(player.getName()))
                    {
                        dmgdone = NatureDamage.get(player.getName());
                        NatureDamage.remove(player.getName());
                    }
                    
                    if (dmgdone > 0)
                    {
                        double newValue = dmgdone / 3;
                        double newAmount = newValue / 7;
                        int absorbAmount = (int) Math.ceil(newAmount * 2.05);
                        
                        if (player.hasPotionEffect(PotionEffectType.ABSORPTION))
                        {
                            player.removePotionEffect(PotionEffectType.ABSORPTION);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,600,absorbAmount));
                        }
                        else
                        {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,600,absorbAmount));
                        }
                        
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_GREEN + "You are no longer channeling the Spirit of Nature, but retain some protection. ");
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are no longer channeling the Spirit of Nature. ");
                    }
                    
                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                    return;
                }
            }
            else
            {
                if (player.getInventory().getChestplate().getTypeId() == 299 && player.getInventory().getChestplate() != null)
                {
                    ItemStack chest = player.getInventory().getChestplate();
                    ItemMeta meta = chest.getItemMeta();

                    LeatherArmorMeta lam = (LeatherArmorMeta) meta;

                    lam.setColor(chestStart);
                    chest.setItemMeta(lam);
                }

                if (player.getInventory().getLeggings().getTypeId() == 300 && player.getInventory().getLeggings() != null)
                {
                    ItemStack legs = player.getInventory().getLeggings();

                    ItemMeta meta = legs.getItemMeta();

                    LeatherArmorMeta lam = (LeatherArmorMeta) meta;


                    lam.setColor(legStart);
                    legs.setItemMeta(lam);
                }

                if (player.getInventory().getBoots().getTypeId() == 301 && player.getInventory().getBoots() != null)
                {
                    ItemStack boots = player.getInventory().getBoots();

                    ItemMeta meta = boots.getItemMeta();

                    LeatherArmorMeta lam = (LeatherArmorMeta) meta;

                    lam.setColor(bootStart);
                    boots.setItemMeta(lam);
                }
                
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are no longer channeling the Spirit of Nature.");
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                return;
            }
            
            
        }
    }
    
    
    public ItemStack setColor (ItemStack item, Color color)
    {
        LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
        
        if (color != null)
        {
            lam.setColor(color);
            item.setItemMeta(lam);
        }
        
        return item;
    }
    
}
