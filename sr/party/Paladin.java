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
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

 

public class Paladin implements Listener
{

    Random randomGenerator = new Random();
    public Party plugin;
    

    public PL pl;

    public Paladin(Party plugin)
    {
        this.plugin = plugin;
    }
    
    public short fullDura = -445;
    
    /*
    public static HashMap<String, Integer> ShieldSlamTimer = new HashMap<String, Integer>();
    public int ShieldVelocity = 7;
    public int ShieldSlamCD = 20;
    public int ShieldRange = 3;
    private double SlamVelocity = (16 / 10D);
    private double SlamYVelocity = (3 / 10D);
    */
    
    public int StunDur = 3;
    public static HashSet<String> isStunned = new HashSet<String>();
    
    public static HashMap<String, Integer> paladinspell = new HashMap<String, Integer>();
    public static HashMap<String, Integer> HoLTimer = new HashMap<String, Integer>();
    public static HashMap<String, Integer> HRTimer = new HashMap<String, Integer>();
    public static HashMap<String, Integer> HSTimer = new HashMap<String, Integer>();
    
    public static HashMap<String, Double> HShealth = new HashMap<String, Double>();
    public static HashMap<String, Integer> HSelapse = new HashMap<String, Integer>();
    
    public static HashSet<String> HSon = new HashSet<String>();
    
    private static HashMap<String, Integer> HSID = new HashMap<String, Integer>();
    
    public int HoLamount = 12;
    public int HRamount = 10;
    public int HSamount = 10;
    
    public int HoLcd = 12;
    public int HRcd = 12;
    public int HScd = 300;
    
    public HashMap<String, Integer> stunTimer = new HashMap<String, Integer>();
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
        
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
        
        if (isStunned.contains(player.getName()))
        {
            int currenttime = plugin.getCurrentTime();
            if (stunTimer.containsKey(player.getName()))
            {
                int checktime = stunTimer.get(player.getName());
                int totaltime2 = (currenttime - checktime);

                if (totaltime2 > (StunDur - 1) || totaltime2 < 0)
                {
                    stunTimer.remove(player.getName());
                    isStunned.remove(player.getName());
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

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamage(EntityDamageEvent event)
  {

    if ((event.getEntity() instanceof Player)) 
    {

      PlayerInventory inv = ((Player)event.getEntity()).getInventory();
 
      Player player = (Player) event.getEntity();
      ItemStack helm = inv.getHelmet();
      ItemStack chest = inv.getChestplate(); 
      ItemStack legs = inv.getLeggings();
      ItemStack shoes = inv.getBoots();
      
   
//Gold Armor Paladin Start

     

      
    if (player.hasPermission("paladin.gold"))
    {
        //Gold Armor Damage Reduction Start

        helm = inv.getHelmet();
        double damage = event.getDamage();

        if (HSon.contains(player.getName()))
        {
            event.setCancelled(true);
            event.setDamage(0);
        }

        if (event.getDamage() <= 0)
        {
            event.setDamage(0);
        }
      
      
      
        if (helm != null)
        {
            if (helm.getTypeId() == 314)
            {

                event.setDamage((int)Math.round(event.getDamage() * 0.75D));

            }
        }

        if (event.getDamage() <= 0) event.setDamage(0);



        chest = inv.getChestplate();

        if (event.getDamage() <= 0) event.setDamage(0);

        if (chest != null)
        {
          if (chest.getTypeId() == 315)
          {

              event.setDamage((int)Math.round(event.getDamage() * 0.65D));


          }
        }

        if (event.getDamage() <= 0) event.setDamage(0);



        legs = inv.getLeggings();

        if (legs != null)
        {
          if (legs.getTypeId() == 316) 
          {

            event.setDamage((int)Math.round(event.getDamage() * 0.65D));


          }          

        }

        if (event.getDamage() <= 0) event.setDamage(0);

        shoes = inv.getBoots();

        if (shoes != null)
        {
          if (shoes.getTypeId() == 317) 
          {

            event.setDamage((int)Math.round(event.getDamage() * 0.75D));



          }

        }

        if (event.getDamage() <= 0) event.setDamage(0);

        //Gold Armor Damage Reduction End

  

        // Gold Armor Paladin End

    } // end paladin.gold perm


     
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
                    
                    if (Party.ghost.contains(attacker.getName()))
                    {
                        event.setCancelled(true);
                        event.setDamage(0);
                        attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Ghosts cannot attack!");
                        return;
                    }
                    
                    
                    if (player.getItemInHand().getTypeId() == 330 && player.hasPermission("paladin.gold"))
                    { //shield
                        
                        if (!(player.getNoDamageTicks() < player.getMaximumNoDamageTicks() / 2.0F))
                        {
                            return;
                        }
                        
                        double random = Math.random();
                        if (random < 0.30)
                        {
                            event.setCancelled(true);
                            event.setDamage(0);
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED + " blocks your attack!");
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You block " + ChatColor.GOLD + attacker.getName() + "'s " + ChatColor.GREEN + "attack!");

                            double dazeChance = Math.random();
                            
                            if (dazeChance < 0.1)
                            {
                                if (!(attacker.hasPotionEffect(PotionEffectType.SLOW)))
                                {
                                    attacker.addPotionEffect(new PotionEffect (PotionEffectType.SLOW, 40, 1));
                                }
                                
                                if (!(attacker.hasPotionEffect(PotionEffectType.SLOW_DIGGING)))
                                {
                                    attacker.addPotionEffect(new PotionEffect (PotionEffectType.SLOW_DIGGING, 40, 1));
                                }
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Your block throws your opponent off balance!");
                                attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are thrown off balance by the block!");
                            }

                            double breakchance = Math.random();
                            if (breakchance > 0.90)
                            {
                                double breakchance2 = Math.random();
                                if (breakchance2 > 0.90)
                                {
                                    player.setItemInHand(null);
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your shield has broken!");
                                    attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You broke their shield with your attack!");
                                }
                            }

                        }
                    }
                    
                    PlayerInventory palinv = attacker.getInventory();
                    ItemStack weapon = palinv.getItemInHand();

                    if(attacker.hasPermission("paladin.gold"))
                    {
                        if(attacker.isOp() || attacker.hasPermission("paladin.goldbypass"))
                        {
                           return;
                        }

                        if(!(weapon.getTypeId() == 283 || weapon.getTypeId() == 330))
                        {
                           event.setDamage(0);
                           attacker.sendMessage(ChatColor.RED + "Paladins can only use Gold weaponry. You deal zero damage.");
                        }
                        else
                        {
                            event.setDamage(event.getDamage() + 2);
                        }

                    }

               } // end event_EE player
               
               if (event_EE.getDamager() instanceof Arrow)
               {
                    Projectile arrow = (Projectile) event_EE.getDamager();

                    if (!(arrow.getShooter() instanceof Player))
                    {
                        return;
                    }
                    
                    Player attacker = (Player) arrow.getShooter();

                    double random = Math.random();

                    if (player.getItemInHand().getTypeId() == 330 && player.hasPermission("paladin.gold"))
                    {
                        if (random < 0.30)
                        {
                            event.setCancelled(true);
                            event.setDamage(0);
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED + " blocks your arrow!");
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You block " + ChatColor.GOLD + attacker.getName() + "'s " + ChatColor.GREEN + "arrow!");

                            double breakchance = Math.random();
                            if (breakchance > 0.95)
                            {
                                double breakchance2 = Math.random();
                                if (breakchance2 > 0.95)
                                {
                                    player.setItemInHand(null);
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your shield has broken!");
                                    attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You broke their shield with your arrow!");   
                                }
                            }
                        }
                    }
                }

        }
   
     } // end instanceof player
    

  } // end entitydamage
  
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event)
  {
      
      final Player player = event.getPlayer();
      PlayerInventory inv = event.getPlayer().getInventory();
      ItemStack weap;

      weap = inv.getItemInHand();
      
      
      
      int handoflight = 1;
      int holyremedy = 2;
      int holyshield = 3;
      
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
        
      if (player.hasPermission("paladin.gold"))
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

                if (item.getTypeId() == 261) // Paladins can't use bows
                {
                    if (player.isOp())
                    {
                        return;
                    }
                    
                    if (player.hasPermission("paladin.bowbypass"))
                    {
                        return;
                    }
                    
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Paladins can only use Gold weaponry.");
                    event.setCancelled(true);
                }
                
                if (item.getTypeId() == 284) // gold shovel
                {
                    int spellcheck;
                    
                    if (paladinspell.containsKey(player.getName()))
                    {
                        spellcheck = paladinspell.get(player.getName()) + 1;
                        
                        if (spellcheck > holyshield)
                        {
                            spellcheck = 1;
                        }
                    }
                    else
                    {
                        spellcheck = 1;
                    }
                    
                    if (spellcheck == handoflight)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.YELLOW + "You ready your " + ChatColor.GOLD + "Hand of Light" + ChatColor.YELLOW + " spell.");
                    }
                    if (spellcheck == holyremedy)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.YELLOW + "You ready your " + ChatColor.GOLD + "Holy Remedy" + ChatColor.YELLOW + " spell.");
                    }
                    if (spellcheck == holyshield)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.YELLOW + "You ready your " + ChatColor.GOLD + "Holy Shield" + ChatColor.YELLOW + " spell.");
                    }
                    
                    paladinspell.put(player.getName(), spellcheck);
                    
                    event.setCancelled(true);
                    
                }
            }
            
            
        } // end right click

           
        
            
        
        
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
        {
            ItemStack item = (ItemStack) player.getItemInHand();
            int currenttime = plugin.getCurrentTime();
            
            if (item == null)
            {
                return;
            }

            if (item != null)
            {
                
                /*
                // begin Shield Slam (iron door = 330)
                if (weap.getTypeId() == 330)
                {
                    if (player.hasPermission("paladin.shield"))
                    {
                        if (weap.getEnchantmentLevel(Enchantment.KNOCKBACK) == 1)
                        {
                            SlamVelocity = (24 / 10D);
                            SlamYVelocity = (4 / 10D);
                        }

                        if (weap.getEnchantmentLevel(Enchantment.KNOCKBACK) >= 2)
                        {
                            SlamVelocity = (30 / 10D);
                            SlamVelocity = (5 / 10D);
                        }

                        int time = plugin.getCurrentTime();

                        if (ShieldSlamTimer.containsKey(player.getName()))
                        {
                            int newtime = ShieldSlamTimer.get(player.getName());
                            int totaltime = time - newtime;

                            if ((totaltime > ShieldSlamCD) || (totaltime < 0))
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
                                   if (target instanceof LivingEntity)
                                   {
                                       if (target instanceof Player)
                                       {
                                           target2 = (Player) target;
                                       }

                                        Location ploc = player.getLocation();
                                        Location tloc = target.getLocation();

                                         if (ploc.distance(tloc) > ShieldRange)
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

                                         Vector v = player.getLocation().getDirection();
                                         v.setY(0).normalize().multiply(SlamVelocity).setY(SlamYVelocity);
                                         target.setVelocity(v);
                                         
                                         for (Entity e : player.getNearbyEntities(ShieldRange, ShieldRange, ShieldRange))
                                         {
                                             if (e instanceof LivingEntity)
                                             {
                                                 World pworld = player.getWorld();
                                                 LivingEntity ent = (LivingEntity) e;
                                                 
                                                 if (ent.getWorld().equals(pworld))
                                                 {
                                                     if (ent.getLocation().distance(player.getLocation()) < ShieldRange)
                                                     {
                                                        
                                                        
                                                        if (ent instanceof Player)
                                                        {
                                                            World plworld = player.getWorld();

                                                            BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                                                            RegionManager regionManager = worldGuard.getRegionManager(plworld);
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
                                                            
                                                            if (myparty != targetparty && p != player)
                                                            {
                                                                p.setVelocity(v);
                                                                boolean canStun = false;

                                                                if (pl.stunImmune.containsKey(p.getName()))
                                                                {
                                                                    int timecheck = pl.stunImmune.get(p.getName());
                                                                    int totalcheck = currenttime - timecheck;

                                                                    if (totalcheck > pl.StunImmune || totalcheck < 0)
                                                                    {
                                                                        canStun = true;
                                                                        pl.stunImmune.remove(p.getName());
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
                                                                    isStunned.add(p.getName());
                                                                    stunTimer.put(p.getName(), time);
                                                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED + ChatColor.BOLD + " Shield Slams "
                                                                        + ChatColor.RESET + ChatColor.RED + "you!");
                                                                    pl.stunImmune.put(p.getName(), time);
                                                                }
                                                                else
                                                                {
                                                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + p.getName() + "is IMMUNE to stuns!");
                                                                }

                                                            }
                                                            
                                                        }
                                                        else
                                                        {
                                                            ent.setVelocity(v);
                                                            ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,100,4));
                                                            ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,100,4));
                                                        }
                                                     }
                                                 }
                                             }
                                         }
                                         
                                           
                                         ShieldSlamTimer.put(player.getName(), time);
                                         player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You Shield Slam!");
                                   }

                                   
                                }

                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + (ShieldSlamCD - totaltime) + " seconds to Shield Slam.");
                                return;
                            }
                        }
                        else
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

                                if (ploc.distance(tloc) > ShieldRange)
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

                                for (Player p : plugin.getServer().getOnlinePlayers())
                                {
                                    World pworld = p.getWorld();
                                    if (player.getWorld().equals(pworld))
                                    {
                                        if (p.getLocation().distance(player.getLocation()) < ShieldRange)
                                        {
                                            if (plugin.party.containsKey(p.getName().toLowerCase()))
                                            {
                                                targetparty = plugin.party.get(p.getName().toLowerCase());
                                            }

                                            if (myparty != targetparty && p != player)
                                            {
                                                Vector v = player.getLocation().getDirection();

                                                v.setY(0).normalize().multiply(SlamVelocity).setY(SlamYVelocity);
                                                p.setVelocity(v);

                                                boolean canStun = false;
                              
                                                if (pl.stunImmune.containsKey(p.getName()))
                                                {
                                                    int timecheck = pl.stunImmune.get(p.getName());
                                                    int totalcheck = currenttime - timecheck;

                                                    if (totalcheck > pl.StunImmune || totalcheck < 0)
                                                    {
                                                        canStun = true;
                                                        pl.stunImmune.remove(p.getName());
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
                                                    isStunned.add(p.getName());
                                                    stunTimer.put(p.getName(), time);
                                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED + ChatColor.BOLD + " Shield Slams "
                                                        + ChatColor.RESET + ChatColor.RED + "you!");
                                                    pl.stunImmune.put(p.getName(), time);
                                                }
                                                else
                                                {
                                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + p.getName() + "is IMMUNE to stuns!");
                                                }
                                            }
                                        }
                                    }
                                }

                                ShieldSlamTimer.put(player.getName(), time);
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You Shield Slam!");
                            }
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You do not have permission to Shield Slam in this area.");
                        return;
                    }
                }
                */ //end shield slam
                
                
                // begin spells with gold shovel (284)
                
                if (weap.getTypeId() != 284)
                {
                    return;
                }
                
                int spellcheck;
                
                if (paladinspell.containsKey(player.getName()))
                {
                    spellcheck = paladinspell.get(player.getName());
                }
                else
                {
                    spellcheck = 1;
                }
                
                if (spellcheck == handoflight)
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
                        
                     if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null
                             && player.getInventory().getBoots() != null)
                     {
                         if (player.getInventory().getHelmet().getType() == Material.GOLD_HELMET && player.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE
                                 && player.getInventory().getLeggings().getType() == Material.GOLD_LEGGINGS && player.getInventory().getBoots().getType() == Material.GOLD_BOOTS)
                         {
                            target2 = (Player) target;
                         
                            Location ploc = player.getLocation();
                            Location tloc = target2.getLocation();

                            if (ploc.distance(tloc) > 30)
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Target out of range.");
                                return;
                            }

                            boolean handoflightcheck;

                           if (HoLTimer.containsKey(player.getName()))
                           {
                               int healold = HoLTimer.get(player.getName());
                               int healtotal = (currenttime - healold);

                               if (healtotal > HoLcd || healtotal < 0) 
                               {
                                   handoflightcheck = false;
                               }
                               else
                               {
                                   handoflightcheck = true;
                                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.GOLD + (HoLcd - healtotal) 
                                           + ChatColor.RED + " seconds to cast " + ChatColor.GOLD + "Hand of Light " + ChatColor.RED + "again.");
                                   return;
                               }
                           }
                           else
                           {
                               handoflightcheck = false;
                           }

                           if (handoflightcheck == false)
                           {
                               double thealth = target2.getHealth();

                               int thung = target2.getFoodLevel();

                               if ((thung + HoLamount) > 20)
                               {
                                   target2.setFoodLevel(20);
                               }
                               else
                               {
                                   target2.setFoodLevel(thung + HoLamount);
                               }

                               if ((thealth + HoLamount) > target2.getMaxHealth())
                               {
                                   target2.setHealth(target2.getMaxHealth());
                               }
                               else
                               {
                                   target2.setHealth(thealth + HoLamount);
                               }

                               // Begin cleansing of debuffs

                               if (target2.hasPotionEffect(PotionEffectType.SLOW))
                               {
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));
                                   player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));

                                   target2.removePotionEffect(PotionEffectType.SLOW);
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,0,0));
                               }

                               if (target2.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
                               {
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));
                                   player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));

                                   target2.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,0,0));
                               }

                               if (target2.hasPotionEffect(PotionEffectType.BLINDNESS))
                               {
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));
                                   player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));

                                   target2.removePotionEffect(PotionEffectType.BLINDNESS);
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,0,0));
                               }
                               if (target2.hasPotionEffect(PotionEffectType.CONFUSION))
                               {                            
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));
                                   player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));

                                   target2.removePotionEffect(PotionEffectType.CONFUSION);
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,0,0));
                               }
                               if (target2.hasPotionEffect(PotionEffectType.POISON))
                               {                            
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));
                                   player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));

                                   target2.removePotionEffect(PotionEffectType.POISON);
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.POISON,0,0));
                               }

                               if (target2.hasPotionEffect(PotionEffectType.WEAKNESS))
                               {                            
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));
                                   player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));

                                   target2.removePotionEffect(PotionEffectType.WEAKNESS);
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,0,0));
                               }

                               if (target2.hasPotionEffect(PotionEffectType.WITHER))
                               {                            
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));
                                   player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,2));

                                   target2.removePotionEffect(PotionEffectType.WITHER);
                                   target2.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,0,0));
                               }



                               // end cleansing of debuffs

                               World pworld = player.getWorld();

                               for (Player p : plugin.getServer().getOnlinePlayers())
                               {
                                 if (p.getWorld().equals(pworld))
                                 {
                                   if (p.getLocation().distance(ploc) < 30)
                                   {
                                       p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.DARK_AQUA + " used Hand of Light on " 
                                               + ChatColor.GOLD + target2.getName());
                                   }
                                 }
                               }

                               HoLTimer.put(player.getName(), currenttime);

                           }
                         } // end armor gold
                     } // end armor null
                     
                     
                  } // end if != null
                  else
                  {
                      player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must have a target.");
                      return;
                  }
                    
                    

                } // end Hand of Light
                
                if (spellcheck == holyremedy)
                {
                    if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null
                             && player.getInventory().getBoots() != null)
                     {
                         if (player.getInventory().getHelmet().getType() == Material.GOLD_HELMET && player.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE
                                 && player.getInventory().getLeggings().getType() == Material.GOLD_LEGGINGS && player.getInventory().getBoots().getType() == Material.GOLD_BOOTS)
                         {
                                boolean holyremedycheck;
                    
                                if (HRTimer.containsKey(player.getName()))
                                {
                                    int healold = HRTimer.get(player.getName());
                                    int healtotal = (currenttime - healold);

                                    if (healtotal > HRcd || healtotal < 0) // 20 second cooldown
                                    {
                                        holyremedycheck = false;
                                    }
                                    else
                                    {
                                        holyremedycheck = true;
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.GOLD + (HRcd - healtotal) 
                                                + ChatColor.RED + " seconds to cast " + ChatColor.GOLD + "Holy Remedy " + ChatColor.RED + "again.");
                                        return;
                                    }
                                }
                                else
                                {
                                    holyremedycheck = false;
                                }

                                if (holyremedycheck == false)
                                {
                                    double ch = player.getHealth();

                                    int ch2 = player.getFoodLevel();

                                    if ((ch2 + HRamount) > 20)
                                    {
                                        player.setFoodLevel(20);
                                    }
                                    else
                                    {
                                        player.setFoodLevel(ch2 + HRamount);
                                    }

                                    if ((ch + HRamount) > player.getMaxHealth())
                                    {
                                        player.setHealth(player.getMaxHealth());
                                    }
                                    else
                                    {
                                        player.setHealth(ch + HRamount);
                                    }

                                    double random = Math.random();

                                    if (random >= 0.25)
                                    {
                                        if (player.hasPotionEffect(PotionEffectType.SLOW))
                                        {
                                            player.removePotionEffect(PotionEffectType.SLOW);
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,0,0));
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You purify your Slow effect away!");
                                        }

                                        if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
                                        {
                                            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,0,0));
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You purify your Swing Slow effect away!");
                                        }

                                        if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
                                        {
                                            player.removePotionEffect(PotionEffectType.WEAKNESS);
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,0,0));
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You purify your Weakness effect away!");
                                        }
                                    }


                                    Location ploc = player.getLocation();

                                    World pworld = player.getWorld();

                                    for (Player p : plugin.getServer().getOnlinePlayers())
                                    {
                                       if (p.getWorld().equals(pworld))
                                       {
                                            if (p.getLocation().distance(ploc) < 30)
                                            {
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.DARK_AQUA + " renews themself using Holy Remedy.");
                                            }
                                       }
                                    }

                                    if (HRTimer.containsKey(player.getName()))
                                    {
                                        HRTimer.remove(player.getName());
                                    }
                                    HRTimer.put(player.getName(), currenttime);

                                }
                         } // end armor gold check
                     } // end armor null check
                    
                } // end holy remedy
                
                if (spellcheck == holyshield)
                {
                    if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null
                             && player.getInventory().getBoots() != null)
                     {
                         if (player.getInventory().getHelmet().getType() == Material.GOLD_HELMET && player.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE
                                 && player.getInventory().getLeggings().getType() == Material.GOLD_LEGGINGS && player.getInventory().getBoots().getType() == Material.GOLD_BOOTS)
                         {
                            boolean holyshieldcheck;
                    
                            Location ploc = player.getLocation();

                            World pworld = player.getWorld();

                            if (HSTimer.containsKey(player.getName()))
                            {
                                int hsold = HSTimer.get(player.getName());
                                int hstotal = (currenttime - hsold);

                                if (hstotal > HScd || hstotal < 0) 
                                {
                                    holyshieldcheck = false;
                                }
                                else
                                {
                                    holyshieldcheck = true;
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.GOLD + (HScd - hstotal) 
                                            + ChatColor.RED + " seconds to cast " + ChatColor.GOLD + "Holy Shield " + ChatColor.RED + "again.");
                                    return;
                                }
                            }
                            else
                            {
                                holyshieldcheck = false;
                            }

                            if (holyshieldcheck == false)
                            {
                                player.setHealth(player.getMaxHealth());

                                double phealth = player.getHealth();

                                HShealth.put(player.getName(), phealth);

                                HSelapse.put(player.getName(), 0);

                                HSTimer.put(player.getName(), currenttime);

                                HSon.add(player.getName());

                                for (Player p : Bukkit.getOnlinePlayers())
                                {
                                    if (p.getWorld().equals(pworld))
                                    {
                                        if (p.getLocation().distance(ploc) < 30)
                                        {
                                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName()+ 
                                                    ChatColor.GOLD + " is surrounded by a " + ChatColor.BOLD + "Holy Aura.");
                                        }
                                    }
                                }

                                new HolyShield(player);

                            } // end holy shield
                         }
                     }
                    
                }
                
                
            }
        }
         
      }
      
  } // end interact
  
  
   
  
  private class HolyShield implements Runnable
  {
      Player player;
      int taskId;
        
      
            
      int i = 0;
      public HolyShield(Player player)
      {
          this.player = player;
          
          this.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Paladin.this.plugin, this, 0, 20);
          
          HSID.put(player.getName(), taskId);
      }
        
      public void run() 
      {
         i++;
         if (i > 15)
         {
              Party.log.info(String.format("Paladin Holy Shield Thread Errant: Cancelling Task ID: " + taskId));
              Bukkit.getServer().getScheduler().cancelTask(taskId);
              return;
         }
        if (HSelapse.containsKey(player.getName()))
        {
          int timecheck = HSelapse.get(player.getName());
          int timecheck2 = timecheck + 1;
                                
          double healthcheck = HShealth.get(player.getName());
                 
          World pworld = player.getWorld();
          Location ploc = player.getLocation();
          
          if (player.getHealth() < healthcheck)
          {
             player.setHealth(healthcheck);
          }
                                    
          HSelapse.put(player.getName(), timecheck2);
                                    

          if (timecheck == 5)
          {
              player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Holy Shield ending in 5 seconds.");
          }
          if (timecheck == 8)
          {
             player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Holy Shield ending in 2 seconds.");
          }
          if (timecheck == 10)
          {   
              
             for (Player p : Bukkit.getOnlinePlayers())
             {
                 if (p.getWorld().equals(pworld))
                 {
                     if (p.getLocation().distance(ploc) < 30)
                     {
                         p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName()+"'s" + ChatColor.GOLD + (ChatColor.BOLD + " Holy Aura ")
                                 + ChatColor.stripColor("") + ChatColor.GOLD + " fades.");
                     }
                 }
             }

             HSelapse.remove(player.getName());
             HShealth.remove(player.getName());
             HSon.remove(player.getName());
             if (HSID.containsKey(player.getName()))
             {
                HSID.remove(player.getName());
             }
             
             Bukkit.getServer().getScheduler().cancelTask(this.taskId);
          }
          
        } // end if check
        else
        {
            if (HSID.containsKey(player.getName()))
            {
                HSID.remove(player.getName());
            }
            
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Script cancelled. Paging Dr. cr0ss!");
            
            Bukkit.getServer().getScheduler().cancelTask(taskId);
        }
          
      }
      
  }
  
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event)
  {
      Player player = event.getPlayer();
      
      if (HSID.containsKey(player.getName()))
      {
         HSID.remove(player.getName());
      }
      if (HSelapse.containsKey(player.getName()))
      {
          HSelapse.remove(player.getName());
      }
      if (HShealth.containsKey(player.getName()))
      {
          HShealth.remove(player.getName());
      }
      if (HSon.contains(player.getName()))
      {
          HSon.remove(player.getName());
      }
  }
  
  
  @EventHandler
  public void onWorldChange(PlayerChangedWorldEvent event)
  {
      
      Player player = event.getPlayer();
      
      if (HSID.containsKey(player.getName()))
      {
         HSID.remove(player.getName());
      }
      if (HSelapse.containsKey(player.getName()))
      {
          HSelapse.remove(player.getName());
      }
      if (HShealth.containsKey(player.getName()))
      {
          HShealth.remove(player.getName());
      }
      if (HSon.contains(player.getName()))
      {
          HSon.remove(player.getName());
      }
  }
  
  
  
}
