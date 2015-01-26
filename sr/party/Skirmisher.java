package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public class Skirmisher implements Listener
{

    public Party plugin;

    public Skirmisher(Party plugin)
    {
        this.plugin = plugin;
    }
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    
    public HashMap<String, Integer> bowtimer = new HashMap();
    public HashMap<String, Integer> salvotimer = new HashMap();
    public HashMap<String, Integer> evadetimer = new HashMap();
    public HashMap<String, Integer> leftsidesteptimer = new HashMap();
    public HashMap<String, Integer> rightsidesteptimer = new HashMap();
    
    public HashSet<String> isEvade = new HashSet<String>();
    public HashMap<String, Integer> arrowCount = new HashMap<String, Integer>();
    
    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event)
    {
        Player player = event.getPlayer();
        Item item = event.getItem();
        
        if (player.hasPermission("sr.skirmisher"))
        {
            if (player.getGameMode() == GameMode.CREATIVE)
            {
                return;
            }
            
            if (item.getItemStack().getType() == Material.ARROW)
            {
                ItemStack stack = item.getItemStack();
                
                int amount = stack.getAmount();
                if (arrowCount.containsKey(player.getName()))
                {
                    event.setCancelled(true);
                    item.remove();
                    
                    int count = arrowCount.get(player.getName());
                    
                    int newcount = count + amount;
                    
                    arrowCount.remove(player.getName());
                    arrowCount.put(player.getName(), newcount);
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Picked up and added " + ChatColor.GOLD + amount + ChatColor.GREEN +
                            " arrows to quiver.");
                    
                }
            }
        }
        
        
        
        
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
            
        if (!(player.hasPermission("sr.skirmisher")))
        {
            return;
        }
        
        ItemStack iteminhand = player.getItemInHand();
        Material iteminhandmat = iteminhand.getType();
        int ability = 0;

        PlayerInventory inv2 = player.getInventory();
        boolean nullarmor = true;
        if (inv2.getBoots() != null && inv2.getChestplate() != null && inv2.getHelmet() != null && inv2.getLeggings() != null)
        {
            nullarmor = false;
        }
        if (nullarmor == true)
        {
            return;
        }
            
        ItemStack boots = inv2.getBoots();
        ItemStack leggings = inv2.getLeggings();
        ItemStack chestplate = inv2.getChestplate();
        ItemStack helmet = inv2.getHelmet();
                
        boolean hasleather = false;
        if (helmet.getType() == Material.LEATHER_HELMET && leggings.getType() == Material.LEATHER_LEGGINGS && chestplate.getType() == Material.LEATHER_CHESTPLATE && boots.getType() == Material.LEATHER_BOOTS)
        {
            hasleather = true;
        }
        if (hasleather == false)
        {
            return;
        }
                
        if (iteminhandmat == Material.GOLD_NUGGET)
        {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
            {
                ability = 1; //side step left
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
            {
                ability = 2 ; //side step right

            }
        }
        
        if (iteminhandmat == Material.DIAMOND)
        {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
            {
                ability = 3; //evade
            }
        }
        
        if (iteminhandmat == Material.BOW)
        {

            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
            {
                ability = 5; //salvo
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                ability = 4; // insta shot
            }
            
        }
        
        if (ability == 5 || ability == 4)
        {
            Inventory inv = player.getInventory();
            
            if (player.getGameMode() == GameMode.CREATIVE)
            {
                return;
            }
            
            if (!arrowCount.containsKey(player.getName()) && !player.getInventory().contains(Material.ARROW))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must have arrows in your inventory or Quiver to shoot!");
                event.setCancelled(true);
                return;
            }
            
            if (player.getInventory().contains(Material.ARROW))
            {
                int arrowcount = 0;
                int oldcount = 0;
                if (arrowCount.containsKey(player.getName()))
                {
                    oldcount = arrowCount.get(player.getName());
                }
                
                ItemStack[] it = player.getInventory().getContents();
                for (ItemStack i : it)
                {
                    if (i != null)
                    {
                        if (i.getType().equals(Material.ARROW))
                        {
                            int size = i.getAmount();
                            arrowcount = arrowcount + size;
                            player.getInventory().remove(i);
                        }
                    }
                }

                int totalcount = arrowcount + oldcount;
                
                if (arrowCount.containsKey(player.getName()))
                {
                    arrowCount.remove(player.getName());
                }
                
                arrowCount.put(player.getName(), totalcount);
                
                if (oldcount != 0)
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Added " + ChatColor.GOLD + arrowcount + ChatColor.GREEN + " arrows to your Quiver. Total: " + (totalcount));
                }
                else
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Added " + ChatColor.GOLD + arrowcount + ChatColor.GREEN + " arrows to your Quiver.");
                }
            }
            
            if (arrowCount.containsKey(player.getName()))
            {
                int count = arrowCount.get(player.getName());
                
                if (!(count > 0))
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You're out of arrows!");
                    arrowCount.remove(player.getName());
                    event.setCancelled(true);
                    return;
                }
            }

        }
            
        if (ability == 0)
        {
            return;
        }
        
        double x = 0;
        double z = 0;
        int dir = direction(player);
        
        if (ability == 1 || ability == 2)
        {
            if (dir == 0 || dir == 16)
            {
                z = 5;
            }
            if (dir == 2)
            {
                z = 5;
                x = -5;
            }
            if (dir == 4)
            {
                x = -5;
            }
            if (dir == 6)
            {
                z = -5;
                x = -5;
            }
            if (dir == 8)
            {
                z = -5;
            }
            if (dir == 10)
            {
                z = -5;
                x = 5;
            }
            if (dir == 12)
            {
                x = 5;
            }
            if (dir == 14)
            {
                z = 5;
                x = 5;
            }
        }
        
        if (ability == 1)
        {
            if (leftsidesteptimer.containsKey(player.getName()))
            {
                int oldtime = leftsidesteptimer.get(player.getName());
                int currenttime = getCurrentTime();
                int totaltime = currenttime - oldtime;

                if (totaltime > 7 || totaltime < 0)
                {
                    leftsidesteptimer.remove(player.getName());
                }
                else
                {
                    int timeoncd = 7 - totaltime;
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Left Side Step is on cooldown for " + timeoncd + " second(s).");
                    return;
                }
            }
            
            Location userfeet = player.getEyeLocation();
            userfeet.setY(userfeet.getY() - 2);
            if(userfeet.getBlock().isEmpty() == false
            && userfeet.getBlock().getType() != Material.SIGN
            && userfeet.getBlock().getType() != Material.SIGN_POST
            && userfeet.getBlock().getType() != Material.WALL_SIGN
            && userfeet.getBlock().getType() != Material.VINE
            && userfeet.getBlock().getType() != Material.LADDER
            && userfeet.getBlock().getType() != Material.SUGAR_CANE_BLOCK
            && userfeet.getBlock().getType() != Material.TORCH
            && userfeet.getBlock().getType() != Material.REDSTONE_TORCH_ON
            && userfeet.getBlock().getType() != Material.REDSTONE_TORCH_OFF
            && userfeet.getBlock().getType() != Material.STONE_BUTTON
            && userfeet.getBlock().getType() != Material.WOOD_BUTTON
            && userfeet.getBlock().getType() != Material.LEVER
            && userfeet.getBlock().getType() != Material.TRIPWIRE_HOOK
            && userfeet.getBlock().getType() != Material.TRIPWIRE)
            {
                Vector unitVector = new Vector(x, 0.3, z);
                player.setVelocity(unitVector);
                leftsidesteptimer.put(player.getName(), getCurrentTime());
                new SideStep(player, player.getLocation());
            }
        }
        
        if (ability == 2)
        {
            if (rightsidesteptimer.containsKey(player.getName()))
            {
                int oldtime = rightsidesteptimer.get(player.getName());
                int currenttime = getCurrentTime();
                int totaltime = currenttime - oldtime;

                if (totaltime > 7 || totaltime < 0)
                {
                    rightsidesteptimer.remove(player.getName());
                }
                else
                {
                    int timeoncd = 7 - totaltime;
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Right Side Step is on cooldown for " + timeoncd + " second(s).");
                    return;
                }
            }

            Location userfeet = player.getEyeLocation();
            userfeet.setY(userfeet.getY() - 2);
            if(userfeet.getBlock().isEmpty() == false
            && userfeet.getBlock().getType() != Material.SIGN
            && userfeet.getBlock().getType() != Material.SIGN_POST
            && userfeet.getBlock().getType() != Material.WALL_SIGN
            && userfeet.getBlock().getType() != Material.VINE
            && userfeet.getBlock().getType() != Material.LADDER
            && userfeet.getBlock().getType() != Material.SUGAR_CANE_BLOCK
            && userfeet.getBlock().getType() != Material.TORCH
            && userfeet.getBlock().getType() != Material.REDSTONE_TORCH_ON
            && userfeet.getBlock().getType() != Material.REDSTONE_TORCH_OFF
            && userfeet.getBlock().getType() != Material.STONE_BUTTON
            && userfeet.getBlock().getType() != Material.WOOD_BUTTON
            && userfeet.getBlock().getType() != Material.LEVER
            && userfeet.getBlock().getType() != Material.TRIPWIRE_HOOK
            && userfeet.getBlock().getType() != Material.TRIPWIRE)
            {
                Vector unitVector = new Vector(-x, 0.3, -z);
                player.setVelocity(unitVector);
                rightsidesteptimer.put(player.getName(), getCurrentTime());
                new SideStep(player, player.getLocation());
            }
            
        }
            
        if (ability == 3)
        {
            if (!(player.hasPermission("sr.skirmisher.evade")))
            {
                return;
            }
            
            if (evadetimer.containsKey(player.getName()))
            {
                int oldtime = evadetimer.get(player.getName());
                int currenttime = getCurrentTime();
                int totaltime = currenttime - oldtime;

                if (totaltime > 20 || totaltime < 0)
                {
                    evadetimer.remove(player.getName());
                }
                else
                {
                    int timeoncd = 20 - totaltime;
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Evade is on cooldown for " + timeoncd + " second(s).");
                    return;
                }
            }

            Location userfeet = player.getEyeLocation();
            userfeet.setY(userfeet.getY() - 2);
            if(userfeet.getBlock().isEmpty() == false
            && userfeet.getBlock().getType() != Material.SIGN
            && userfeet.getBlock().getType() != Material.SIGN_POST
            && userfeet.getBlock().getType() != Material.WALL_SIGN
            && userfeet.getBlock().getType() != Material.VINE
            && userfeet.getBlock().getType() != Material.LADDER
            && userfeet.getBlock().getType() != Material.SUGAR_CANE_BLOCK
            && userfeet.getBlock().getType() != Material.TORCH
            && userfeet.getBlock().getType() != Material.REDSTONE_TORCH_ON
            && userfeet.getBlock().getType() != Material.REDSTONE_TORCH_OFF
            && userfeet.getBlock().getType() != Material.STONE_BUTTON
            && userfeet.getBlock().getType() != Material.WOOD_BUTTON
            && userfeet.getBlock().getType() != Material.LEVER
            && userfeet.getBlock().getType() != Material.TRIPWIRE_HOOK
            && userfeet.getBlock().getType() != Material.TRIPWIRE)
            {
                if (dir == 0 || dir == 16)
                {
                    x = 9;
                }
                if (dir == 2)
                {
                    x = 9;
                    z = 9;
                }
                if (dir == 4)
                {
                    z = 9;
                }
                if (dir == 6)
                {
                    x = -9;
                    z = -9;
                }
                if (dir == 8)
                {
                    x = -9;
                }
                if (dir == 10)
                {
                    x = -9;
                    z = -9;
                }
                if (dir == 12)
                {
                    z = -9;
                }
                if (dir == 14)
                {
                    x = 9;
                    z = -9;
                }

                int i = 0;
                FireworkEffect.Type fireworkeffect = FireworkEffect.Type.BURST;
                DyeColor fireworkcolor = DyeColor.GREEN;
                while (i < 2)
                {
                    if (i == 1)
                    {
                        fireworkcolor = DyeColor.YELLOW;
                    }

                    Firework fw = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    FireworkEffect effect = FireworkEffect.builder().trail(true).flicker(false).withColor(fireworkcolor.getFireworkColor()).with(fireworkeffect).build();
                    fwm.clearEffects();
                    fwm.addEffect(effect);
                    try 
                    {
                        Field f = fwm.getClass().getDeclaredField("power");
                        f.setAccessible(true);
                        try 
                        {
                            f.set(fwm, -2);
                        } catch (IllegalAccessException ex) 
                        {
                            plugin.log.log(Level.SEVERE, null, ex);
                        }
                    } catch (NoSuchFieldException e1) 
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (SecurityException e1) 
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IllegalArgumentException e1) 
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    fw.setFireworkMeta(fwm);


                    i = i + 1;

                }
                Vector unitVector = new Vector(x, 0.85, z);
                player.setVelocity(unitVector);
                evadetimer.put(player.getName(), getCurrentTime());

                if (isEvade.contains(player.getName()))
                {
                    isEvade.remove(player.getName());
                }

                isEvade.add(player.getName());
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40, 6));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 6));
            }
            
        }
            
        if (ability == 4)
        {
            if (bowtimer.containsKey(player.getName()))
            {
                int oldtime = bowtimer.get(player.getName());
                int currenttime = getCurrentTime();
                int totaltime = currenttime - oldtime;

                if (totaltime > 1 || totaltime < 0)
                {
                    bowtimer.remove(player.getName());
                }
                else
                {
                    int timeoncd = 1 - totaltime;
                    //player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Bow Shot on cooldown for " + timeoncd + " seconds.");
                    return;
                }
            }

            
            if (arrowCount.containsKey(player.getName()))
            {
                int count = arrowCount.get(player.getName());
                int newcount = count - 1;
                arrowCount.remove(player.getName());
                arrowCount.put(player.getName(), newcount);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "Arrow Count: " + ChatColor.GOLD + newcount);
            }
                    
            Inventory inv = player.getInventory();
            inv.removeItem(new ItemStack(Material.ARROW, 1));

            
            //player.launchProjectile(Arrow.class);
            Arrow a = (Arrow) player.launchProjectile(Arrow.class);
            a.setShooter(player);
            a.setVelocity(player.getLocation().getDirection().multiply(8));
            
            player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 2);
            //arrow.setMetadata("ArrowType", new MyMetadata(this, "stick"));
            bowtimer.put(player.getName(), getCurrentTime());
            event.setCancelled(true);
            
            return;
        }
        
        if (ability == 5)
        {
            if (player.isSneaking())
            {
                //empty quiver
                if (arrowCount.containsKey(player.getName()))
                {
                    int count = arrowCount.get(player.getName());
                    
                    player.getInventory().addItem(new ItemStack(Material.ARROW, count));
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Emptied Quiver.");
                    
                    arrowCount.remove(player.getName());
                    return;
                }
            }
            
            World pworld = player.getWorld();

            BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

            RegionManager regionManager = worldGuard.getRegionManager(pworld);
            ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

            if (!(set.allows(DefaultFlag.PVP)))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Cannot Rain down Acid in a non-PvP area. How inhuman of you!");
                event.setCancelled(true);
                return;
            }
            
            if (salvotimer.containsKey(player.getName()))
            {
                int oldtime = salvotimer.get(player.getName());
                int currenttime = getCurrentTime();
                int totaltime = currenttime - oldtime;

                if (totaltime > 30 || totaltime < 0)
                {
                    salvotimer.remove(player.getName());
                }
                else
                {
                    int timeoncd = 30 - totaltime;
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Acid Rain on cooldown for " + timeoncd + " second(s).");
                    return;
                }
            }
            
            if (arrowCount.containsKey(player.getName()))
            {
                int count = arrowCount.get(player.getName());
                int newcount = count - 1;
                arrowCount.remove(player.getName());
                arrowCount.put(player.getName(), newcount);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_PURPLE + "Arrow Count: " + ChatColor.GOLD + newcount);
            }

            Inventory inv = player.getInventory();
            inv.removeItem(new ItemStack(Material.ARROW, 1));

            //player.launchProjectile(Arrow.class);
            Arrow a = (Arrow) player.getWorld().spawn(player.getEyeLocation(), Arrow.class);
            a.setShooter(((LivingEntity) player));
            a.setVelocity(player.getLocation().getDirection().multiply(3));
            a.setMetadata("acidrain", new FixedMetadataValue(plugin, true));
            salvotimer.put(player.getName(), getCurrentTime());
            event.setCancelled(true);
            return;

        }
    } // end interact
    
                   
    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event)
    {
        Projectile projectile = event.getEntity();
        if(!(projectile.hasMetadata("acidrain")))
        {
            return;
        }
        
        if (!(projectile.getShooter() instanceof Player))
        {
            return;
        }
        
        Location location = projectile.getLocation();
        Player shooter = (Player) projectile.getShooter();
        new AcidRain(location, shooter);
    }
               
    @EventHandler(ignoreCancelled = true)
    public void onBowShot(EntityDamageByEntityEvent event)
    {

        if(event.getCause() != DamageCause.PROJECTILE)
        {
            return;
        }
            
        Projectile projectile = (Projectile)event.getDamager();
        
        if (!(projectile instanceof Arrow))
        {
            return;
        }
        if(!(projectile.getShooter() instanceof Player))
        {
            return;
        }
 
        Entity hitbyarrow = event.getEntity();
        
        if (!(hitbyarrow instanceof Player || hitbyarrow instanceof Creeper || hitbyarrow instanceof Zombie || hitbyarrow instanceof Skeleton || hitbyarrow instanceof PigZombie || hitbyarrow instanceof Villager))
        {
            return;
        }
        Player shooter = (Player)projectile.getShooter();
        
        if (!(shooter.hasPermission("sr.skirmisher")))
        {
            return;
        }
        
        if (shooter.getItemInHand().containsEnchantment(Enchantment.ARROW_FIRE))
        {
            projectile.setFireTicks(40);
        }
        
        if (shooter.getItemInHand().containsEnchantment(Enchantment.ARROW_DAMAGE))
        {
            int level = shooter.getItemInHand().getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
            
            double dmg = event.getDamage();
            double newdamage = (dmg + level) + 1;
            event.setDamage(newdamage);
        }

        if (projectile.getLocation().getY() - hitbyarrow.getLocation().getY() > 1.52)
        {
            double firstdamage = event.getDamage();
            double newdamage = firstdamage * 1.5;
            event.setDamage(newdamage);
            
            shooter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Headshot!");
            shooter.playSound(shooter.getLocation(), Sound.ANVIL_LAND, 1, 2);
            if (event.getEntity() instanceof Player)
            {
                Player target = (Player) event.getEntity();
                target.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Headshot! from " + ChatColor.GOLD + shooter.getName());
                target.playSound(target.getLocation(), Sound.ANVIL_LAND, 1, 2);
            }
        }
        
        
    }
              
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if (!(player.hasPermission("sr.skirmisher")))
        {
            return;
        }

        PlayerInventory inv = player.getInventory();
        if (inv.getBoots() != null && inv.getChestplate() != null && inv.getHelmet() != null && inv.getLeggings() != null)
        {
            ItemStack boots = inv.getBoots();
            ItemStack leggings = inv.getLeggings();
            ItemStack chestplate = inv.getChestplate();
            ItemStack helmet = inv.getHelmet();

            if (boots.hasItemMeta() && leggings.hasItemMeta() && chestplate.hasItemMeta() && helmet.hasItemMeta())
            {
                return;
            }
            
            ItemStack lhelm = new ItemStack(Material.LEATHER_HELMET);
            ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS);
            ItemStack lchest = new ItemStack(Material.LEATHER_CHESTPLATE);
            ItemStack llegs = new ItemStack(Material.LEATHER_LEGGINGS);

            if (helmet.getType() == Material.LEATHER_HELMET && leggings.getType() == Material.LEATHER_LEGGINGS && chestplate.getType() == Material.LEATHER_CHESTPLATE && boots.getType() == Material.LEATHER_BOOTS)
            {
                LeatherArmorMeta helmmeta = (LeatherArmorMeta) helmet.getItemMeta();
                helmmeta.setColor(Color.YELLOW);
                helmet.setItemMeta(helmmeta);
                player.getInventory().setHelmet(helmet);

                LeatherArmorMeta chestmeta = (LeatherArmorMeta) chestplate.getItemMeta();
                chestmeta.setColor(Color.YELLOW);
                chestplate.setItemMeta(chestmeta);
                player.getInventory().setChestplate(chestplate);

                LeatherArmorMeta legsmeta = (LeatherArmorMeta) leggings.getItemMeta();
                legsmeta.setColor(Color.YELLOW);
                leggings.setItemMeta(legsmeta);
                player.getInventory().setLeggings(leggings);

                LeatherArmorMeta bootsmeta = (LeatherArmorMeta) boots.getItemMeta();
                bootsmeta.setColor(Color.YELLOW);
                boots.setItemMeta(bootsmeta);
                player.getInventory().setBoots(boots);


            }
        }

    }
 /*               @EventHandler
public void onPlayerJoin(PlayerJoinEvent event)
        {
            Player player = event.getPlayer();
            if (!(player.hasPermission("sr.skirmisher")))
            {
                return;
            }
            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.SPEED);
            
        }
*/

        
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        Entity e = event.getEntity();
        if (!(e instanceof LivingEntity))
        {
            return;
        }
        LivingEntity le = (LivingEntity) e;
        if (!(le instanceof Player))
        {
            return;
        }
        Player player = (Player) le;
        if (!(player.hasPermission("sr.skirmisher")))
        {
            return;
        }
        
        DamageCause cause = event.getCause();
        if (cause == DamageCause.FALL)
        {
            if (isEvade.contains(player.getName()))
            {
                event.setCancelled(true);
                isEvade.remove(player.getName());
            }
        }

    }
          
    private int getCurrentTime()
    {
        Calendar calendar = new GregorianCalendar();

        int hour = calendar.get(Calendar.HOUR) * 3600;
        int minute = calendar.get(Calendar.MINUTE) * 60;
        int second = calendar.get(Calendar.SECOND);

        return hour + minute + second;
    }
          
    public int direction(Player player)
    {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) 
        {
            return 0; //S > E
        } else if (22.5 <= rotation && rotation < 67.5) 
        {
            return 2; //SW > SE
        } else if (67.5 <= rotation && rotation < 112.5) 
        {
            return 4; //W > E
        } else if (112.5 <= rotation && rotation < 157.5) 
        {
            return 6; //NW > SW
        } else if (157.5 <= rotation && rotation < 202.5) 
        {
            return 8; //N > W
        } else if (202.5 <= rotation && rotation < 247.5) 
        {
            return 10; //NE > NW
        } else if (247.5 <= rotation && rotation < 292.5) 
        {
            return 12; //E > N
        } else if (292.5 <= rotation && rotation < 337.5) 
        {
            return 14; //SE > NE
        } else if (337.5 <= rotation && rotation < 360.0) 
        {
            return 16; //S > E
        } else 
        {
            return -1;
        }
    }
                                                             
    public class SideStep implements Runnable 
    {
        Player p;
        Location start;
        int i;
        int taskId;
        
        public SideStep(Player p, Location start) 
        {
            this.p = p;
            this.start = start;

            i = 0;
            taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 5L, 5L);
        }
        
        public void run() 
        {
          
            i = i + 1;
          
            if (!(p.isOnline()))
            {
                i = 20;
            }

            if ( i == 1 || i == 3)
            {
                Location ploc = p.getLocation();
                World pworld = p.getWorld();
                for (Player chatplayer : Bukkit.getServer().getOnlinePlayers())
                {
                    Location chatloc2 = chatplayer.getLocation();
                    if (chatplayer.getWorld().equals(pworld))
                    {  
                        if (ploc.distance(chatloc2) < 3.0D)
                        {
                            if (chatplayer != p)
                            {
                                World cworld = chatplayer.getWorld();

                                BlockVector pt = BukkitUtil.toVector(chatplayer.getLocation().getBlock());

                                RegionManager regionManager = worldGuard.getRegionManager(cworld);
                                ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                                if (!(set.allows(DefaultFlag.PVP)))
                                {
                                    return;
                                }
                                
                                chatplayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2));
                            }
                        }
                    }
                }
            }
            if (i >= 5)
            {
                Bukkit.getServer().getScheduler().cancelTask(taskId);
            }
        }
    } // end sidestep runnable
                                                                    
                                                                      
    public class AcidRain implements Runnable 
    {
        Location start;
        int i;
        int taskId;
        Player player;
        public AcidRain(Location start, Player player) 
        {
            this.start = start;
            this.player = player;
            i = 0;
            taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 20L, 20L);
        }
        
        public void run() 
        {
            i = i + 1;


            if ( i == 1 || i == 2 || i == 3 || i == 4 || i == 5)
            {
             // p.getWorld().createExplosion(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), 1, false, false);

                FireworkEffect.Type fireworkeffect = FireworkEffect.Type.BALL_LARGE;
                DyeColor fireworkcolor = DyeColor.GREEN;
                Firework fw = (Firework) start.getWorld().spawn(start, Firework.class);
                FireworkMeta fwm = fw.getFireworkMeta();
                FireworkEffect effect = FireworkEffect.builder().trail(true).flicker(false).withColor(fireworkcolor.getFireworkColor()).with(fireworkeffect).build();
                fwm.clearEffects();
                fwm.addEffect(effect);
                try {
                Field f = fwm.getClass().getDeclaredField("power");
                f.setAccessible(true);
                try 
                {
                    f.set(fwm, -2);
                } catch (IllegalAccessException ex) {
                   plugin.log.log(Level.SEVERE, null, ex);
                }
                } catch (NoSuchFieldException e1) {
                // TODO Auto-generated catch block
                  e1.printStackTrace();
                } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                  e1.printStackTrace();
                } catch (IllegalArgumentException e1) {
                // TODO Auto-generated catch block
                  e1.printStackTrace();
                }

                fw.setFireworkMeta(fwm);
            }
         
            if ( i == 1 || i == 2 || i == 3 || i == 4 || i == 5)
            {
                Location ploc = start;
              
                int myparty = -1;
                int targetparty = -2;
                
                if (plugin.party.containsKey(player.getName().toLowerCase()))
                {
                    myparty = plugin.party.get(player.getName().toLowerCase());
                }
                
                World pworld = start.getWorld();
                for (Player chatplayer : Bukkit.getServer().getOnlinePlayers())
                {
                    Location chatloc2 = chatplayer.getLocation();
                    if (chatplayer.getWorld().equals(pworld))

                    {  
                        if (ploc.distance(chatloc2) < 7.0D)
                        {
                            if (chatplayer != player)
                            {
                                if (plugin.party.containsKey(chatplayer.getName().toLowerCase()))
                                {
                                    targetparty = plugin.party.get(chatplayer.getName().toLowerCase());
                                }
                                
                                if (myparty != targetparty)
                                {
                                    chatplayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
                                    chatplayer.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 3));
                                    chatplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are standing in " + ChatColor.GOLD + player.getName() + ChatColor.RED + "'s Acid Rain!");
                                }
                            }
                        }

                    }
                }
          }
            
          if (i >= 5)
          {
              Bukkit.getServer().getScheduler().cancelTask(taskId);
          }
      
        }
        
    } // end acidrain runnable

} // end class