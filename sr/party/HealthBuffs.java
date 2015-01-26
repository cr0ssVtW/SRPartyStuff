package sr.party;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author Cross
 */

public class HealthBuffs implements Listener
{
    public Party plugin;
    
    public HealthBuffs(Party plugin)
    {
        this.plugin = plugin;
    }
    
    public double MaxHP = 40;
    
    /*
    @EventHandler
    public void onMove (PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        
        //player.sendMessage("Your Max Health is: " + player.getMaxHealth());
        if (player.getMaxHealth() != MaxHP)
        {
            player.setMaxHealth(MaxHP);
            player.addPotionEffect(new PotionEffect(PotionEffectType.getById(21),20,2));
            player.setHealth(player.getMaxHealth());
        }
        
    }
    */
    
    @EventHandler
    public void onEntityDamage (EntityDamageEvent event)
    {
        Entity entity = event.getEntity();
        
        if (!(entity instanceof Player))
        {
            return;
        }
        
        if (entity instanceof Player)
        {
            Player player = (Player) entity;
            
            if (player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null
                    && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null)
            {
                if (player.hasPermission("naked.pvp"))
                {
                    double dmg = event.getDamage();
                    double newdmg = dmg * 5;
                    event.setDamage(newdmg);
                    //player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You take MASSIVE damage from being naked! Go get geared up!");
                }
            }
            
            if (event.getCause() == DamageCause.FALL)
            {
                double dmg = event.getDamage();
                double newdmg = dmg * 1.15;
                
                event.setDamage(newdmg);
            }
            
            if (event instanceof EntityDamageByEntityEvent)
            {
                EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent) event;
                
                if (event_EE.getDamager() instanceof Arrow)
                {
                    if (player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null
                    && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null)
                    {
                        if (player.hasPermission("naked.pvp"))
                        {
                            double dmg = event.getDamage();
                            double newdmg = dmg * 5;
                            event.setDamage(newdmg);
                            // player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You take MASSIVE damage from being naked! Go get geared up!");
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onQuit (PlayerQuitEvent event)
    {
        event.setQuitMessage("");
    }
    
    @EventHandler
    public void onWorldChange (PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        //Bukkit.broadcastMessage("Player Joined");
        
        if (player.hasPotionEffect(PotionEffectType.SLOW))
        {
            player.removePotionEffect(PotionEffectType.SLOW);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,40,4));

        if (player.hasPermission("sr.bonushealth"))
        {
            player.setMaxHealth(MaxHP);
            player.setHealth(player.getMaxHealth());
        }
        else
        {
            player.setMaxHealth(20);
            player.setHealth(player.getMaxHealth());
        }
        
        
        if (player.getWorld().getName().equalsIgnoreCase("pvp"))
        {
            for (PotionEffect pe : player.getActivePotionEffects())
            {
                PotionEffectType type = pe.getType();

                if (type != PotionEffectType.HEALTH_BOOST)
                {
                    player.removePotionEffect(type);
                    player.addPotionEffect(new PotionEffect(type, 0, 0));
                }
            }
        }
        
        
    }
    
    @EventHandler
    public void onJoin (PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        
        setupScoreboard(player);
        
        event.setJoinMessage("");
        
        if (player.hasPermission("sr.exp.bypass"))
        {
            player.setLevel(50);
        }
        
        for (PotionEffect pe : player.getActivePotionEffects())
        {
            PotionEffectType type = pe.getType();

            if (type != PotionEffectType.HEALTH_BOOST)
            {
                player.removePotionEffect(type);
                player.addPotionEffect(new PotionEffect(type, 0, 0));
            }
        }
        
        //Bukkit.broadcastMessage("Player Joined");
        if (player.hasPotionEffect(PotionEffectType.SLOW))
        {
            player.removePotionEffect(PotionEffectType.SLOW);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,40,4));

        if (player.hasPermission("sr.bonushealth"))
        {
            player.setMaxHealth(MaxHP);
            player.setHealth(player.getMaxHealth());
        }
        else
        {
            player.setMaxHealth(20);
            player.setHealth(player.getMaxHealth());
        }
        
        
        
        
        //player.addPotionEffect(new PotionEffect(PotionEffectType.getById(21),400,4));
    }
    
    
    @EventHandler
    public void onRespawn (PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        //Bukkit.broadcastMessage("Player Respawned");
        
        if (player.hasPotionEffect(PotionEffectType.SLOW))
        {
            player.removePotionEffect(PotionEffectType.SLOW);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,40,6));

        if (player.hasPermission("sr.bonushealth"))
        {
            player.setMaxHealth(MaxHP);
            player.setHealth(player.getMaxHealth());
        }
        else
        {
            player.setMaxHealth(20);
            player.setHealth(player.getMaxHealth());
        }
        
       // player.addPotionEffect(new PotionEffect(PotionEffectType.getById(21),400,4));
    }
    
    @EventHandler
    public void onDeath (PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        //Bukkit.broadcastMessage("Player Respawned");
        
        if (player.hasPotionEffect(PotionEffectType.SLOW))
        {
            player.removePotionEffect(PotionEffectType.SLOW);
        }
        
       // player.addPotionEffect(new PotionEffect(PotionEffectType.getById(21),400,4));
    }
    
    public void setupScoreboard(Player player)
    {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        
        Objective objective = board.registerNewObjective("showhealth", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        int max = (int) player.getMaxHealth();
        objective.setDisplayName("/ " + ChatColor.GREEN + max + ChatColor.DARK_RED + "♥");
        
        player.setScoreboard(board);
        player.setHealth(player.getHealth());
    }
    
}
