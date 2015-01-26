package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import sr.party.Utils.BlockUtils;
import sr.party.Utils.FadeUtils;

/**
 *
 * @author AstramG
 */
public class Archmage implements Listener
{
    public Party plugin;
    
    
    public Archmage (Party plugin)
    {
        this.plugin = plugin;
    }
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    public HashSet<Block> noBreak = new HashSet<Block>();
    /*
    This Archmage class is created by Astram specifically for Savage Realms!

    Required Items:

      Any Chestplate
      Golden Boots & Golden Hat
      No leggings

      Diamond Hoe:
        - Passive: Mages Bane
        - Right Click: Magic Shot
      Eye of the Ender:
        - Left Click: Chain Lightning
        - Right Click: Blink
      Blaze Rod:
        - Left Click: Holy Spirit
        - Right Click: Dragon Spirit
      Iron Ingot:
        - Right Click: Encase

    Skills:

      Blink:
      Teleports up to a range of 15 blocks.
      20 second cooldown

      Gale:
      Pushes nearby players back a bit.
      20 second cooldown

      Magic Shot:
      Shoot 3 arrows from a diamond hoe at once
      5 second cooldown

      Dragon Spirit:
      Ride on a fireball for 5 seconds and then deal 5 full hearts damage to nearby players on the explosion and sets them on fire for 5 seconds
      Also fall damage does not happen on the fall
      100 second cooldown

      Chain Lightning:
      Deals 3 hearts of damage to players within 15 blocks and applies confusion for 6 seconds to them
      20 second cooldown

      Holy Spirit:
      Heals 4 hearts and applies speed 2 for 8 seconds to all party members
      22 second cooldown

      Encase:
      Seals the caster in glass for 8 seconds taking no damage from surrounding enemies
      (Can not blink out of the glass)
      60 second cooldown

      Mage's Bane (Passive):
      Diamond hoes' deal 6 times the normal damage (1 damage less than iron sword) and has a chance to apply a negative potion effect to the target
      15% Hunger
      10% Slowness
      5% Blindness
    */
    
    /*
     * My Stuff
     */
    public int FadeCD = 15;
    public int FadeDist = 25;
    public boolean passThroughCeiling = false;
    public boolean smokeTrail = true;
    
    public BlockUtils blockUtils;
    public FadeUtils fadeUtils;
    /*
     * 
     */
    
    public ArrayList<String> blinkCooldown = new ArrayList<String>();
    public ArrayList<String> fireballCooldown = new ArrayList<String>();
    public ArrayList<String> chainLightningCooldown = new ArrayList<String>();
    public ArrayList<String> partyHealCooldown = new ArrayList<String>();
    public ArrayList<String> fireballRiders = new ArrayList<String>();
    public ArrayList<UUID> fireballids = new ArrayList<UUID>();
    public ArrayList<String> encaseCooldown = new ArrayList<String>();
    public ArrayList<String> inGlass = new ArrayList<String>();
    public ArrayList<String> arrowCooldown = new ArrayList<String>();
    public ArrayList<String> galeCooldown = new ArrayList<String>();
    public ArrayList<Arrow> magicArrows = new ArrayList<Arrow>();
    
    @EventHandler
    public void onArrowHit(ProjectileHitEvent event)
    {
        if(event.getEntity() instanceof Arrow) 
        {
            Arrow arrow = (Arrow) event.getEntity();
            if (magicArrows.contains(arrow)) 
            {
                arrow.remove();
                magicArrows.remove(arrow);
            }
        }
    }
    
    @EventHandler
    public void explosionPrime(ExplosionPrimeEvent event) 
    {
        if (event.getEntityType() == EntityType.FIREBALL) 
        {
            event.setCancelled(true);
            Location loc = event.getEntity().getLocation();
            event.getEntity().getWorld().createExplosion(loc, 0.0F);
            fireballids.remove(event.getEntity().getUniqueId());
            Entity fireball = event.getEntity();
            List<Entity> entities = fireball.getNearbyEntities(7.0D, 7.0D, 7.0D);
            for (Entity e : entities) 
            {
                if (e instanceof Player) 
                {
                    Player player = (Player) e;
                    
                    if (fireballRiders.contains(player.getName()))
                    {
                        player = Bukkit.getPlayer(player.getName());
                    }
                    
                    int myparty = -1;
                    
                    if (plugin.party.containsKey(player.getName().toLowerCase()))
                    {
                        myparty = plugin.party.get(player.getName().toLowerCase());
                    }
                    
                    if (e != fireball.getPassenger() && (Player) e != player) 
                    {
                        Player p = (Player) e;
                        int yourparty = -2;
                        if (plugin.party.containsKey(p.getName().toLowerCase()))
                        {
                            yourparty = plugin.party.get(p.getName().toLowerCase());
                        }
                        
                        if (myparty != yourparty)
                        {
                            LivingEntity le = (LivingEntity) e;
                            le.damage(10.0D, player);
                            le.setFireTicks(20*5);
                        }  
                    }
                }
            }
        }
    }
    
    
    public boolean hasArmorRequirements(Player player) 
    {
        ItemStack helmet = new ItemStack(Material.AIR, 1);
        ItemStack chestplate = new ItemStack(Material.AIR, 1);
        ItemStack leggings = new ItemStack(Material.AIR, 1);
        ItemStack boots = new ItemStack(Material.AIR, 1);
        if (player.getInventory().getHelmet() != null) 
        {
            helmet = player.getInventory().getHelmet();
        }
        if (player.getInventory().getChestplate() != null) 
        {
            chestplate = player.getInventory().getChestplate();
        }
        if (player.getInventory().getLeggings() != null) 
        {
            leggings = player.getInventory().getLeggings();
        }
        if (player.getInventory().getBoots() != null) 
        {
            boots = player.getInventory().getBoots();
        }
        if (helmet.getType() == Material.GOLD_HELMET) 
        {
            if (chestplate.getType() != Material.AIR) 
            {
                if (leggings.getType() == Material.AIR) 
                {
                    if (boots.getType() == Material.GOLD_BOOTS) 
                    {
                            return true;
                    }
                }
            }
        }
        return false;
    }
    
    @EventHandler
    public void fallDamage(EntityDamageEvent event) 
    {
        if (event.getCause() == DamageCause.FALL) 
        {
            if (event.getEntity() instanceof Player) 
            {
                Player player = (Player) event.getEntity();
                if (fireballRiders.contains(player.getName())) 
                {
                    fireballRiders.remove(player.getName());
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void entitydamge(EntityDamageByEntityEvent event) 
    {
        if (event.getDamager() instanceof Player) 
        {
            Player damager = (Player) event.getDamager();
            
            if (PL.stuntarget.containsKey(damager.getName().toLowerCase()) || Paladin.isStunned.contains(damager.getName()) || ChaosCrusader.chaosstunned.containsKey(damager.getName()))
            {
                  event.setCancelled(true);
                  damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                  return;
            }
            
            if (damager.getItemInHand().getType() == Material.DIAMOND_HOE) 
            {
                if (!(damager.hasPermission("sr.archmage.use")))
                {
                    return;
                }
                World pworld = damager.getWorld();

                BlockVector pt = BukkitUtil.toVector(damager.getLocation().getBlock());

                RegionManager regionManager = worldGuard.getRegionManager(pworld);
                ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                if (!(set.allows(DefaultFlag.PVP)))
                {
                    damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                    return;
                }
                
                if (hasArmorRequirements(damager)) 
                {
                    double damage = event.getDamage() * 7;
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    event.setDamage(damage);
                    Random rand = new Random();
                    int num = rand.nextInt(101);
                    if (num >= 70 && num < 85) 
                    {
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20*4, 0));
                            damager.playEffect(entity.getLocation(), Effect.SMOKE, 0);
                            damager.sendMessage(ChatColor.GREEN + "You applied hunger to your target!");
                    }
                    if (num >= 85 && num < 95) 
                    {
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 2));
                            damager.playEffect(entity.getLocation(), Effect.SMOKE, 0);
                            damager.sendMessage(ChatColor.GREEN + "You applied slowness to your target!");
                    }
                    if (num >= 95) 
                    {
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*3, 0));
                            damager.playEffect(entity.getLocation(), Effect.SMOKE, 0);
                            damager.sendMessage(ChatColor.GREEN + "You applied blindness to your target!");
                    }
                } 
                else 
                {
                        damager.sendMessage(ChatColor.DARK_RED + "Invalid armor requirements!");
                }
            }
        }
    }
    
    @EventHandler
    public void blockBreak (BlockBreakEvent event)
    {
        Block block = event.getBlock();
        
        if (noBreak.contains(block))
        {
            event.setCancelled(true);
        }
    }
    
    
    public List<BlockState> createGlass(Location loc) 
    {
        List<BlockState> blocks = new ArrayList<BlockState>();
        List<BlockState> blocks2 = new ArrayList<BlockState>();
        Block below = loc.clone().add(0, -1, 0).getBlock();
        Block front1 = loc.clone().add(1, 1, 0).getBlock();
        Block front2 = loc.clone().add(1, 0, 0).getBlock();
        Block side11 = loc.clone().add(0, 1, 1).getBlock();
        Block side12 = loc.clone().add(0, 0, 1).getBlock();
        Block side21 = loc.clone().add(0, 1, -1).getBlock();
        Block side22 = loc.clone().add(0, 0, -1).getBlock();
        Block back1 = loc.clone().add(-1, 1, 0).getBlock();
        Block back2 = loc.clone().add(-1, 0, 0).getBlock();
        Block top = loc.clone().add(0, 2, 0).getBlock();
        Block mid1 = loc.clone().getBlock();
        Block mid2 = loc.clone().add(0, 1, 0).getBlock();
        blocks = Arrays.asList(below.getState(), front1.getState(), front2.getState(), side11.getState(), side12.getState(), side21.getState(), side22.getState(), back1.getState(), back2.getState(), top.getState(), mid1.getState(), mid2.getState());
        blocks2 = blocks;
        for (BlockState bs : blocks) 
        {
            Block b = bs.getBlock();
            if (b.getType() == Material.AIR) 
            {
                b.setType(Material.GLASS);
            } 
        }
        
        for (BlockState bs : blocks2)
        {
            Block b = bs.getBlock();
            noBreak.add(b);
        }
        
        mid1.setType(Material.AIR);
        mid2.setType(Material.AIR);
        return blocks2;
	
    }
    
    
    @EventHandler
    public void interact(PlayerInteractEvent event) 
    {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        
        if (PL.stuntarget.containsKey(player.getName().toLowerCase()) || Paladin.isStunned.contains(player.getName()) || ChaosCrusader.chaosstunned.containsKey(player.getName()))
        {
              event.setCancelled(true);
              player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
              return;
        }
        
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) 
        {
            if (!(player.hasPermission("sr.archmage.use")))
            {
                return;
            }
            
            if (item.getType() == Material.DIAMOND_HOE) 
            {
                
                if (hasArmorRequirements(player)) 
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
                    if (!(arrowCooldown.contains(player.getName()))) 
                    {
                        arrowCooldown.add(player.getName());
                        player.sendMessage(ChatColor.GREEN + "You've launched magical arrows!");
                        event.setCancelled(true);
                        for (int i = 0; i < 3; i ++) 
                        {
                            Arrow arrow = player.launchProjectile(Arrow.class);
                            Vector nv = player.getLocation().getDirection().multiply(2);
                            arrow.teleport(arrow.getLocation().add(0, i-1, 0));
                            arrow.setVelocity(nv);
                            magicArrows.add(arrow);
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new arrowCooldown(this, player), 20*5);
                    }
                } else {
                        player.sendMessage(ChatColor.DARK_RED + "Invalid armor requirements!");
                }
                event.setCancelled(true);
            }
            
            if (item.getType() == Material.IRON_INGOT) 
            {
                
                if (hasArmorRequirements(player)) 
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
                    
                        if (!(encaseCooldown.contains(player.getName()))) 
                        {
                            Location location = player.getLocation();
                            encaseCooldown.add(player.getName());
                            player.sendMessage(ChatColor.GREEN + "You've encased yourself in glass!");
                            if (player.getLocation().subtract(0, 1, 0).getBlock().getTypeId() == 0) 
                            {
                                player.teleport(player.getLocation().add(0, 1,0));
                            }
                            double x = Math.floor(player.getLocation().getX()) + .5;
                            double z = Math.floor(player.getLocation().getZ()) + .5;
                            player.teleport(new Location(player.getWorld(), x, player.getLocation().getY(), z));
                            List<BlockState> blocks = createGlass(location);
                            inGlass.add(player.getName());
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new glassRemoval(this, player, blocks), 20*8);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new glassCooldown(this, player), 20*60);
                        }
                } 
                else 
                {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid armor requirements!");
                }
            }
            
            if (item.getType() == Material.EYE_OF_ENDER) 
            {
                
                if (hasArmorRequirements(player)) 
                {
                    
                    if (!(blinkCooldown.contains(player.getName()))) 
                    {
                        if (!(inGlass.contains(player.getName()))) 
                        {
                            event.setCancelled(true);
                            
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
                                    
                                    blinkCooldown.add(player.getName());
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new blinkScheduler(this, player), 20*20);

                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You blink away!");
                                    
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
                        else 
                        {
                            player.sendMessage(ChatColor.RED + "You can't Blink while encased!");
                            event.setCancelled(true);
                        }
                    }
                    else 
                    {
                        player.sendMessage(ChatColor.RED + "Blink isn't ready yet!");
                        event.setCancelled(true);
                    }
                } 
                else 
                {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid armor requirements!");
                    event.setCancelled(true);
                }
            }

            if (item.getType() == Material.BLAZE_ROD) 
            {
                if (hasArmorRequirements(player)) 
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
                    
                    if (!(inGlass.contains(player.getName()))) 
                    {
                        if (!(fireballCooldown.contains(player.getName()))) 
                        {
                            Entity fireball = player.launchProjectile(Fireball.class);
                            fireball.getVelocity().multiply(1.25);
                            fireballids.add(fireball.getUniqueId());
                            fireballCooldown.add(player.getName());
                            fireballRiders.add(player.getName());
                            player.sendMessage(ChatColor.GREEN + "Dragon Spirit initiated!");
                            fireball.setPassenger(player);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new fireballRideScheduler(this, fireball, player), 20*5);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new fireballCooldown(this, player), 20*100);
                        } 
                        else 
                        {
                            player.sendMessage(ChatColor.RED + "Dragon Spirit isn't ready yet!");
                        }
                    } 
                    else 
                    {
                        player.sendMessage(ChatColor.RED + "You can't use the Dragon Spirit while encased!");
                    }
                } 
                else 
                {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid armor requirements!");
                }
            }
        } 
        else 
        {
            if (!(player.hasPermission("sr.archmage.use")))
            {
                return;
            }
            
            if (item.getType() == Material.EYE_OF_ENDER) 
            {
                if (hasArmorRequirements(player)) 
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
                    
                    if (!(inGlass.contains(player.getName()))) 
                    {
                        if (!(chainLightningCooldown.contains(player.getName()))) 
                        {
                            chainLightningCooldown.add(player.getName());
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new chainLightningCooldown(this, player), 20*20);
                            
                            int myparty = -1;
                            int yourparty = -2;
                            if (plugin.party.containsKey(player.getName().toLowerCase()))
                            {
                                myparty = plugin.party.get(player.getName().toLowerCase());
                            }
                            
                            for (Entity e : player.getNearbyEntities(15.0D, 15.0D, 15.0D)) 
                            {
                                if (e instanceof Player) 
                                {
                                    if ((Player) e != player) 
                                    {
                                        Player p = (Player) e;
                                        if (plugin.party.containsKey(p.getName().toLowerCase()))
                                        {
                                            yourparty = plugin.party.get(p.getName().toLowerCase());
                                        }
                                        
                                        if (myparty != yourparty)
                                        {
                                            Bukkit.getWorld(e.getWorld().getName()).strikeLightningEffect(e.getLocation());
                                            LivingEntity le = (LivingEntity) e;
                                            le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*7, 1));
                                            le.damage(6.0D);
                                            p.sendMessage(ChatColor.RED + "You've been stuck by Chain Lightning!");
                                        }
                                    }
                                }
                            }
                        } 
                        else 
                        {
                            player.sendMessage(ChatColor.RED + "Chain Lightning isn't ready yet!");
                        }
                    } 
                    else 
                    {
                        player.sendMessage(ChatColor.RED + "You can't use Chain Lightning while encased!");
                    }
                } 
                else 
                {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid armor requirements!");
                }
            }
                
            if (item.getType() == Material.IRON_INGOT) 
            {
                if (hasArmorRequirements(player)) 
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
                    
                    if (!(inGlass.contains(player.getName()))) 
                    {
                        if (!(galeCooldown.contains(player.getName()))) 
                        {
                            List<Entity> entities = player.getNearbyEntities(6.0D, 6.0D, 6.0D);
                            galeCooldown.add(player.getName());
                            player.sendMessage(ChatColor.GREEN + "A gust of wind has appeared!");
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new galeCooldown(this, player), 20*90);
                            for (Entity e : entities) 
                            {
                                if (e instanceof Player) 
                                {
                                    Player p = (Player) e;
                                    if (p != player) 
                                    {
                                        p.sendMessage(ChatColor.RED + "A gust of wind shoots you back!");
                                    }
                                }
                                if (!(player.isSneaking())) 
                                {
                                    e.setVelocity(e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(7.3).setY(1.95));
                                } 
                                else 
                                {
                                    e.setVelocity(e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(-7.3).setY(1.95));
                                }
                            }
                        } 
                        else 
                        {
                            player.sendMessage(ChatColor.RED + "You can't use Gale yet!");
                        }
                    } 
                    else 
                    {
                        player.sendMessage(ChatColor.RED + "You can't use Gale while encased!");
                    }
                } 
                else 
                {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid armor requirements!");
                }
            }
                
            if (item.getType() == Material.BLAZE_ROD) 
            {
                if (hasArmorRequirements(player)) 
                {
                    if (!(partyHealCooldown.contains(player.getName()))) 
                    {
                        partyHealCooldown.add(player.getName());
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new partyHealCooldown(this, player), 20*22);
                        // PARTY HEAL HERE! APPLY THESE EFFECTS TO ALL PARTY MEMBERS
                        int myparty = -1;
                        int yourparty = -2;
                        
                        if (plugin.party.containsKey(player.getName().toLowerCase()))
                        {
                            myparty = plugin.party.get(player.getName().toLowerCase());
                        }
                                                
                        double phealth = player.getHealth();
                        double phealamount = phealth + 8.0D;
                        if (phealamount > player.getMaxHealth())
                        {
                            player.setHealth(player.getMaxHealth());
                        }
                        else
                        {
                            player.setHealth(phealamount);
                        }

                        player.sendMessage(ChatColor.GREEN + "The Holy Spirit renews you!");
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*15, 0));
                                
                        for (Player p : Bukkit.getOnlinePlayers())
                        {
                            if (p.getWorld().equals(player.getWorld()))
                            {
                                if (p.getLocation().distance(player.getLocation()) < 20)
                                {
                                    if (p != player)
                                    {
                                        if (plugin.party.containsKey(p.getName().toLowerCase()))
                                        {
                                            yourparty = plugin.party.get(p.getName().toLowerCase());
                                        }
                                        
                                        if (myparty == yourparty)
                                        {
                                            double health = p.getHealth();
                                            double healamount = health + 8.0D;
                                            if (healamount > p.getMaxHealth())
                                            {
                                                p.setHealth(p.getMaxHealth());
                                            }
                                            else
                                            {
                                                p.setHealth(healamount);
                                            }
                                            p.sendMessage(ChatColor.GREEN + "The Holy Spirit renews you!");
                                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*15, 0));
                                        }
                                    }
                                }
                            }
                        }
;
                    } 
                    else 
                    {
                        player.sendMessage(ChatColor.RED + "Holy spirit isn't ready yet!");
                    }
                } 
                else 
                {
                    player.sendMessage(ChatColor.DARK_RED + "Invalid armor requirements!");
                }
            }
        }
    } 
	
    public void carefulTeleport(Location loc, Player player) 
    {
        Location locabove = loc.add(0, 1, 0);
        if (locabove.getBlock().getType() != Material.AIR) 
        { //Would be stuck in a block
            player.teleport(locabove);
            player.getLocation().getDirection().multiply(-.25); //Should shoot them back about a block to "unstick" them from being stuck in a block
        } 
        else 
        {
            player.teleport(locabove);
        }
    }
    
    private class arrowCooldown extends BukkitRunnable
    {
        Archmage archmage;
        Player player;
       
        public arrowCooldown(Archmage archmage, Player player) 
        {
            this.archmage = archmage;
            this.player = player;
        }
       
        public void run() 
        {
            archmage.arrowCooldown.remove(player.getName());
        }
    }
    
    private class blinkScheduler extends BukkitRunnable
    {
       
        Archmage archmage;
        Player player;
       
        public blinkScheduler(Archmage archmage, Player player) 
        {
            this.archmage = archmage;
            this.player = player;
        }
       
        public void run() 
        {
            archmage.blinkCooldown.remove(player.getName());
            player.sendMessage(ChatColor.YELLOW + "The cooldown for Blink is finished!");
        }
       
    }
    
    private class chainLightningCooldown extends BukkitRunnable
    {
       
        Archmage archmage;
        Player player;
       
        public chainLightningCooldown(Archmage archmage, Player player) 
        {
            this.archmage = archmage;
            this.player = player;
        }
       
        public void run() 
        {
            archmage.chainLightningCooldown.remove(player.getName());
            player.sendMessage(ChatColor.YELLOW + "You can use chain lighting again!");
        }
 
    }
    
    private class fireballCooldown extends BukkitRunnable
    {
        Archmage archmage;
        Player player;
       
        public fireballCooldown(Archmage archmage, Player player) 
        {
            this.archmage = archmage;
            this.player = player;
        }
       
        public void run()
        {
            archmage.fireballCooldown.remove(player.getName());
            player.sendMessage(ChatColor.YELLOW + "Dragon Spirit is now ready to be used again!");
        }
       
    }
    
    private class fireballRideScheduler extends BukkitRunnable
    {
       
        Entity fireball;
        Archmage archmage;
        Player player;
       
        public fireballRideScheduler(Archmage archmage, Entity fireball, Player player) {
                this.archmage = archmage;
                this.fireball = fireball;
                this.player = player;
        }
       
        public void run() 
        {
            if (archmage.fireballids.contains(fireball.getUniqueId())) 
            { //Detection if the fireball exploded pre-maturely
                fireball.setPassenger(null);
                fireball.remove();
                archmage.fireballids.remove(fireball.getUniqueId());
                Bukkit.getWorld(fireball.getWorld().getName()).createExplosion(fireball.getLocation(), 0);
                List<Entity> entities = fireball.getNearbyEntities(7.0D, 7.0D, 7.0D);
                for (Entity e : entities) 
                {
                    if (e instanceof Player) 
                    {
                        Player player = (Player) e;
                    
                        if (fireballRiders.contains(player.getName()))
                        {
                            player = Bukkit.getPlayer(player.getName());
                        }
                        int myparty = -1;
                        int yourparty = -2;

                        if (plugin.party.containsKey(player.getName().toLowerCase()))
                        {
                            myparty = plugin.party.get(player.getName().toLowerCase());
                        }

                        if (e != fireball.getPassenger() && (Player) e != player) 
                        {
                            Player p = (Player) e;
                            if (plugin.party.containsKey(p.getName().toLowerCase()))
                            {
                                yourparty = plugin.party.get(p.getName().toLowerCase());
                            }

                            if (myparty != yourparty)
                            {
                                LivingEntity le = (LivingEntity) e;
                                le.damage(10.0D);
                                le.setFireTicks(20*5);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private class galeCooldown extends BukkitRunnable
    {
       
        Archmage archmage;
        Player player;
       
        public galeCooldown(Archmage archmage, Player player) 
        {
            this.archmage = archmage;
            this.player = player;
        }
       
        public void run() 
        {
            archmage.galeCooldown.remove(player.getName());
            player.sendMessage(ChatColor.YELLOW + "Gale is ready for use again!");
        }
       
    }
    
    private class glassCooldown extends BukkitRunnable   
    {
       
        Archmage archmage;
        Player player;
       
        public glassCooldown(Archmage archmage, Player player) 
        {
            this.archmage = archmage;
            this.player = player;
        }
       
        public void run() 
        {
            player.sendMessage(ChatColor.YELLOW + "Encase is ready for use again!");
            archmage.encaseCooldown.remove(player.getName());
        }
       
    }
    
    private class glassRemoval extends BukkitRunnable
    {
        Archmage archmage;
        Player player;
        List<BlockState> blocks;
       
        public glassRemoval(Archmage archmage, Player player, List<BlockState> blocks) 
        {
            this.archmage = archmage;
            this.player = player;
            this.blocks = blocks;
        }
       
        public void run() 
        {
            for (BlockState bs : blocks)
            {
                Block b = bs.getBlock();
                if (noBreak.contains(b))
                {
                    noBreak.remove(b);
                }
            }
            
            for (int i = 0; i < blocks.size(); i ++) 
            {
                blocks.get(i).getLocation().getBlock().setType(blocks.get(i).getType());
            }
            player.sendMessage(ChatColor.RED + "The glass faded away!");
            archmage.inGlass.remove(player.getName());
        }
       
    }
        
    private class partyHealCooldown extends BukkitRunnable{
       
        Archmage archmage;
        Player player;
       
        public partyHealCooldown(Archmage archmage, Player player) 
        {
            this.archmage = archmage;
            this.player = player;
        }
       
        public void run() 
        {
            player.sendMessage(ChatColor.YELLOW + "You may now use the holy spirit again!");
            archmage.partyHealCooldown.remove(player.getName());
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
}
