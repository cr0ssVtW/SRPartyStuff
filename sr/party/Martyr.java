package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class Martyr implements Listener
{
    public Party plugin;
    public Warlock lock;
    
    public Martyr (Party plugin)
    {
        this.plugin = plugin;
    }
    
    public static HashMap<String, Integer> martyrtimer = new HashMap();
    public static HashMap<String, Integer> martyrhealtimer = new HashMap();
    public static HashMap<String, Integer> SacrificeTimer = new HashMap<String, Integer>();
    public static HashMap<String, Integer> SacrificeCoolDown = new HashMap<String, Integer>();
    
    public static HashMap<String, Double> Shield = new HashMap<String, Double>();
    
    public int SacImmune = 30;
    public int SacCD = 60;
    public int SacDur = 900; 
    public double SacRange = 20;
    public double multi = 3.0D;
    
    public int RegenCD = 25;
   
    public double swapMargin = 12;
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    
    
    @EventHandler
    public void onWorldChange (PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        
        if (Shield.containsKey(player.getName()))
        {
            Shield.remove(player.getName());
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have changed worlds and lost your shield.");
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof LivingEntity)
        {
            LivingEntity target = (LivingEntity) event.getEntity();
            Player player = null;
            
            if (event.getEntity() instanceof Player)
            {
                player = (Player) target;
            }
            
            if (event instanceof EntityDamageByEntityEvent)
            {
                EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
                
                if (event_EE.getDamager() instanceof Player)
                {
                    double dmgcheck = event.getDamage();
                    
                    Player attacker =(Player)event_EE.getDamager();
                    
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
                    
                    
                    if (target.hasPotionEffect(PotionEffectType.getById(22)))
                    {
                        attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + player.getName() + "'s Shield absorbs your attack!");
                    }
                    
                    /*
                    if (Shield.containsKey(player.getName()))
                    {
                        int samount = (int) Math.round(Shield.get(player.getName()));

                        double damage = event.getDamage();
                        
                        if (damage > samount)
                        {
                            event.setDamage(damage - samount);
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your shield has been broken!");
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Your hit destroys their shield!");
                            Shield.remove(player.getName());
                            
                        }
                        else
                        {
                            double dmg = event.getDamage();
                            double newshield = Math.round(samount - dmg);
                            
                            String prefix = "";
                            String suffix = "§e" + Double.toString(newshield);

                            Shield.put(player.getName(), newshield);

                            event.setDamage(0);
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Your shield absorbs a hit!");
                            attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your target's shield absorbs your hit!");
                        }
                    }
                    */
                    
                    if (attacker.hasPermission("martyr.use"))
                    {
                        if (attacker.getItemInHand() != null)
                        {
                            if ((attacker.getInventory().getHelmet() != null) && (attacker.getInventory().getChestplate() != null) 
                                    && (attacker.getInventory().getLeggings() != null) && (attacker.getInventory().getBoots() != null))
                            {
                                PlayerInventory attackerinv = attacker.getInventory();
                                ItemStack attackerhelm = attackerinv.getHelmet();
                                ItemStack attackerchest = attackerinv.getChestplate();
                                ItemStack attackerlegs = attackerinv.getLeggings();
                                ItemStack attackershoes = attackerinv.getBoots();
                                
                                if ((attackerhelm.getTypeId() == 298) && (attackerchest.getTypeId() == 315) && (attackerlegs.getTypeId() == 316) && (attackershoes.getTypeId() == 301))
                                {
                                    double attackerhealth = attacker.getHealth();
                                    double playerhealth = target.getHealth();
                                    double eventdamage = event.getDamage();
                                    
                                    double extradamage = eventdamage + ((attacker.getMaxHealth() - attackerhealth) / 3);
                                    
                                    double extradamagemob = eventdamage + ((attacker.getMaxHealth() - attackerhealth) / 5);
                                    
                                    event.setDamage(extradamagemob);
                                    
                                    if (target instanceof Player)
                                    {
                                        event.setDamage(extradamage);
                                    }
                                    
                                    
                                    
                                    String attackername = attacker.getName();
                                    String playername = "";
                                    if (player != null)
                                    {
                                        playername = player.getName();
                                    }
                                    
                                    if (attacker.getItemInHand() != null)
                                    {
                                        if (attacker.getItemInHand().getTypeId() == 283)
                                        {
                                            int myparty = -1;
                                            int targetparty = -2;
                                            
                                            if (player != null)
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
                                                
                                                if (plugin.party.containsKey(player.getName().toLowerCase()))
                                                {
                                                    targetparty = plugin.party.get(player.getName().toLowerCase());
                                                }
                                            }
                                            
                                            if (plugin.party.containsKey(attacker.getName().toLowerCase()))
                                            {
                                                myparty = plugin.party.get(attacker.getName().toLowerCase());
                                            }
                                            
                                            if (myparty != targetparty)
                                            {
                                                double healthlower = attackerhealth - 2;
                                                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 2));
                                                if (healthlower < 1)
                                                {
                                                    attacker.setHealth(1);
                                                }
                                                else
                                                {
                                                    attacker.setHealth(healthlower);
                                                }
                                            }
                                        }
                                        
                                        if (attacker.getItemInHand().getTypeId() == 286)
                                        {
                                            if (target instanceof Player)
                                            {
                                                if (martyrtimer.containsKey(attacker.getName()))
                                                {
                                                    int time = plugin.getCurrentTime();
                                                    int switchtime = ((Integer)martyrtimer.get(attacker.getName())).intValue();
                                                    int totaltime = time - switchtime;

                                                    if ((totaltime > 150) || (totaltime < 0))
                                                    {

                                                        double hpcheck = (attackerhealth + swapMargin);
                                                        if (hpcheck > player.getMaxHealth())
                                                        {
                                                            player.setHealth(player.getMaxHealth());
                                                        }
                                                        else
                                                        {
                                                            player.setHealth(hpcheck);
                                                        }

                                                        double hpcheck2 = (playerhealth - swapMargin);
                                                        if (hpcheck2 < swapMargin)
                                                        {
                                                            attacker.setHealth(swapMargin);
                                                        }
                                                        else
                                                        {
                                                            attacker.setHealth(playerhealth - swapMargin);
                                                        }

                                                        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,1));

                                                        if (player != null)
                                                        {
                                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + attackername + ChatColor.GOLD + " used 'Switch Now!' to change life totals with you!");
                                                        }
                                                        attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You used 'Switch Now!' and changed life totals with " + ChatColor.RED + playername);

                                                        martyrtimer.put(attacker.getName(), Integer.valueOf(time));
                                                    }
                                                    else
                                                    {
                                                        int timeleft = 150  - totaltime;
                                                        attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You have " + timeleft + " seconds until you can use 'Switch Now!' again.");

                                                    }
                                                }
                                                else
                                                {
                                                    int time = plugin.getCurrentTime();
                                                    double hpcheck = (attackerhealth + swapMargin);
                                                    if (hpcheck > player.getMaxHealth())
                                                    {
                                                        player.setHealth(player.getMaxHealth());
                                                    }
                                                    else
                                                    {
                                                        player.setHealth(hpcheck);
                                                    }

                                                    double hpcheck2 = (playerhealth - swapMargin);
                                                    if (hpcheck2 < swapMargin)
                                                    {
                                                        attacker.setHealth(swapMargin);
                                                    }
                                                    else
                                                    {
                                                        attacker.setHealth(playerhealth - swapMargin);
                                                    }

                                                    attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,1));

                                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + attackername + ChatColor.GOLD + " used 'Switch Now!' to change life totals with you!");
                                                    attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You used 'Switch Now!' and changed life totals with " + ChatColor.RED + playername);

                                                    martyrtimer.put(attacker.getName(), Integer.valueOf(time));
                                                }
                                            }
                                            else
                                            {
                                                attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You can only Switch Now! with players.");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } // end entitydamage
    
    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event)
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
        
        if (player.hasPermission("martyr.use"))
        {
            if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))
            {
                ItemStack item = player.getItemInHand();
                
                if (item == null) {
                    return;
                }
                
                if (item != null)
                {
                    if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null
                            && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null)
                    {
                        if ((player.getInventory().getHelmet().getTypeId() == 298) && (player.getInventory().getChestplate().getTypeId() == 315) 
                            && (player.getInventory().getLeggings().getTypeId() == 316) && (player.getInventory().getBoots().getTypeId() == 301))
                        {
                            if (item.getTypeId() == 286)
                            {
                                int time = plugin.getCurrentTime();

                                if (martyrhealtimer.containsKey(player.getName()))
                                {
                                    int switchtime = martyrhealtimer.get(player.getName());
                                    int totaltime = time - switchtime;

                                    if ((totaltime > RegenCD) || (totaltime < 0))
                                    {
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You use Regeneration to heal your wounds!");
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 3));
                                        martyrhealtimer.put(player.getName(), time);
                                    }
                                    else
                                    {
                                        int timeleft = RegenCD - totaltime;
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have " + timeleft + " seconds until you can use Regeneration.");
                                    }
                                }
                                else
                                {

                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You use Regeneration to heal your wounds!");
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 4));
                                    martyrhealtimer.put(player.getName(), time);
                                }
                            } // end item 286

                            if (item.getTypeId() == 371) // gold nugget
                            {
                                
                                int time = plugin.getCurrentTime();

                                if (SacrificeTimer.containsKey(player.getName()))
                                {
                                    int healtime = SacrificeTimer.get(player.getName());
                                    int totaltime = time - healtime;

                                    if ((totaltime > SacCD) || (totaltime < 0))
                                    {
                                        double phealth = player.getHealth();
                                        double newhealth = Math.ceil(phealth * 0.6D);

                                        if (newhealth < 1)
                                        {
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You do not have enough health to Sacrifice.");
                                            return;
                                        }
                                        else
                                        {
                                            double shieldamount = (Math.ceil((phealth - newhealth) * multi));
                                            int amp = (int) (shieldamount / 4);
                                            double heartamount = Math.ceil(amp * 2.05);
                                            
                                            if (plugin.party.containsKey(player.getName().toLowerCase()))
                                            {
                                                SacrificeTimer.put(player.getName(), time);
                                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You sacrifice some life to Shield your allies for ~" + (heartamount + 1) + " hearts.");
                                                player.setHealth(newhealth);
                                                
                                                int partymembers = plugin.party.get(player.getName().toLowerCase());

                                                for (Player partyplayer : plugin.getServer().getOnlinePlayers())
                                                {
                                                    if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                                                    {
                                                        int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());

                                                        if ((partyplayers == partymembers) && (partyplayer != player))
                                                        {
                                                            if (partyplayer.getWorld().equals(player.getWorld()))
                                                            {
                                                                if (partyplayer.getLocation().distance(player.getLocation()) < SacRange)
                                                                {
                                                                    boolean canShield = false;
                                                                    
                                                                    if (SacrificeCoolDown.containsKey(partyplayer.getName()))
                                                                    {
                                                                        int sactime = SacrificeCoolDown.get(partyplayer.getName());
                                                                        int totaltime2 = time - sactime;
                                                                        
                                                                        if (totaltime2 > SacImmune || totaltime2 < 0)
                                                                        {
                                                                            canShield = true;
                                                                        }
                                                                        else
                                                                        {
                                                                            canShield = false;
                                                                        }
                                                                    }
                                                                    else
                                                                    {
                                                                        canShield = true;
                                                                    }
                                                                    
                                                                    if (canShield)
                                                                    {
                                                                        partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + 
                                                                        " shields you for ~" + (heartamount + 1) +" hearts.");

                                                                        
                                                                        partyplayer.addPotionEffect(new PotionEffect(PotionEffectType.getById(22),SacDur,amp));
                                                                        SacrificeCoolDown.put(partyplayer.getName(), time);
                                                                    }
                                                                    else
                                                                    {
                                                                        partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED +
                                                                                " tried to shield you but you've been shielded too recently!");
                                                                    }
                                                                    
                                                                }
                                                                else
                                                                {
                                                                    partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were out of range to receive " 
                                                                            + ChatColor.GOLD + player.getName() + "'s Sacrifice");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else
                                            if (plugin.maHandler != null && plugin.maHandler.isPlayerInArena(player))
                                            {
                                                SacrificeTimer.put(player.getName(), time);
                                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You sacrifice some life to Shield your allies for ~" + (heartamount + 1) + " hearts.");
                                                player.setHealth(newhealth);
                                                
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
                                                                    if (p.getLocation().distance(player.getLocation()) < SacRange)
                                                                    {
                                                                        boolean canShield = false;
                                                                    
                                                                        if (SacrificeCoolDown.containsKey(p.getName()))
                                                                        {
                                                                            int sactime = SacrificeCoolDown.get(p.getName());
                                                                            int totaltime2 = time - sactime;

                                                                            if (totaltime2 > SacImmune || totaltime2 < 0)
                                                                            {
                                                                                canShield = true;
                                                                            }
                                                                            else
                                                                            {
                                                                                canShield = false;
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            canShield = true;
                                                                        }

                                                                        if (canShield)
                                                                        {
                                                                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + 
                                                                            " shields you for ~" + (heartamount + 1) +" hearts.");


                                                                            p.addPotionEffect(new PotionEffect(PotionEffectType.getById(22),SacDur,amp));
                                                                            SacrificeCoolDown.put(p.getName(), time);
                                                                        }
                                                                        else
                                                                        {
                                                                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED +
                                                                                    " tried to shield you but you've been shielded too recently!");
                                                                        }
                                                                       
                                                                    }
                                                                    else
                                                                    {
                                                                        p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were out of range to receive " 
                                                                                + ChatColor.GOLD + player.getName() + "'s Sacrifice");
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
                                            else
                                            {
                                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are not in a party to Sacrifice for.");
                                                return;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + (SacCD - totaltime) + " seconds to Sacrifice.");
                                        return;
                                    }
                                }
                                else
                                {
                                        double phealth = player.getHealth();
                                        double newhealth = Math.ceil(phealth * 0.6D);

                                        if (newhealth < 1)
                                        {
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You do not have enough health to Sacrifice.");
                                            return;
                                        }
                                        else
                                        {
                                            
                                            
                                            double shieldamount = (Math.ceil((phealth - newhealth) * multi));
                                            int amp = (int) (shieldamount / 4);
                                            double heartamount = Math.ceil(amp * 2.05);
                                            
                                            if (plugin.party.containsKey(player.getName().toLowerCase()))
                                            {
                                                SacrificeTimer.put(player.getName(), time);
                                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You sacrifice some life to Shield your allies for ~" + (heartamount + 1) + " hearts.");
                                                player.setHealth(newhealth);
                                                
                                                int partymembers = plugin.party.get(player.getName().toLowerCase());

                                                for (Player partyplayer : plugin.getServer().getOnlinePlayers())
                                                {
                                                    if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                                                    {
                                                        int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());

                                                        if ((partyplayers == partymembers) && (partyplayer != player))
                                                        {
                                                            if (partyplayer.getWorld().equals(player.getWorld()))
                                                            {
                                                                if (partyplayer.getLocation().distance(player.getLocation()) < 20)
                                                                {
                                                                    boolean canShield = false;
                                                                    
                                                                    if (SacrificeCoolDown.containsKey(partyplayer.getName()))
                                                                    {
                                                                        int sactime = SacrificeCoolDown.get(partyplayer.getName());
                                                                        int totaltime2 = time - sactime;

                                                                        if (totaltime2 > SacImmune || totaltime2 < 0)
                                                                        {
                                                                            canShield = true;
                                                                        }
                                                                        else
                                                                        {
                                                                            canShield = false;
                                                                        }
                                                                    }
                                                                    else
                                                                    {
                                                                        canShield = true;
                                                                    }

                                                                    if (canShield)
                                                                    {
                                                                        partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + 
                                                                        " shields you for ~" + (heartamount + 1) +" hearts.");


                                                                        partyplayer.addPotionEffect(new PotionEffect(PotionEffectType.getById(22),SacDur,amp));
                                                                        SacrificeCoolDown.put(partyplayer.getName(), time);
                                                                    }
                                                                    else
                                                                    {
                                                                        partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED +
                                                                                " tried to shield you but you've been shielded too recently!");
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were out of ranged to receive " 
                                                                            + ChatColor.GOLD + player.getName() + "'s Sacrifice");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else
                                            if (plugin.maHandler != null && plugin.maHandler.isPlayerInArena(player))
                                            {
                                                SacrificeTimer.put(player.getName(), time);
                                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You sacrifice some life to Shield your allies for ~" + (heartamount + 1) + " hearts.");
                                                player.setHealth(newhealth);
                                                
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
                                                                    if (p.getLocation().distance(player.getLocation()) < SacRange)
                                                                    {
                                                                        boolean canShield = false;
                                                                    
                                                                        if (SacrificeCoolDown.containsKey(p.getName()))
                                                                        {
                                                                            int sactime = SacrificeCoolDown.get(p.getName());
                                                                            int totaltime2 = time - sactime;

                                                                            if (totaltime2 > SacImmune || totaltime2 < 0)
                                                                            {
                                                                                canShield = true;
                                                                            }
                                                                            else
                                                                            {
                                                                                canShield = false;
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            canShield = true;
                                                                        }

                                                                        if (canShield)
                                                                        {
                                                                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + 
                                                                            " shields you for ~" + (heartamount + 1) +" hearts.");


                                                                            p.addPotionEffect(new PotionEffect(PotionEffectType.getById(22),SacDur,amp));
                                                                            SacrificeCoolDown.put(p.getName(), time);
                                                                        }
                                                                        else
                                                                        {
                                                                            p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED +
                                                                                    " tried to shield you but you've been shielded too recently!");
                                                                        }
                                                                    }
                                                                    else
                                                                    {
                                                                        p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were out of range to receive " 
                                                                                + ChatColor.GOLD + player.getName() + "'s Sacrifice");
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
                                            else
                                            {
                                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are not in a party to Sacrifice for.");
                                                return;
                                            }



                                        }


                                } // end else on phealth check

                            } // end else on timer
                        }
                    }
                    
                   
                } // item null
                
            } // end right click
           
        }
       
    }
   
}
    
    
    
   

