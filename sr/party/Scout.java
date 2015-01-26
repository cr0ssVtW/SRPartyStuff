package sr.party;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

/**
 *
 * @author Cross
 */
public class Scout implements Listener
{
  public Party plugin;
  
  public Scout (Party plugin)
  {
      this.plugin = plugin;
  }
  
  public static HashMap<String, Integer> TLFcd = new HashMap<String, Integer>();
  private int ICD = 12;
  
  //public HashMap<String, Integer> DURRcd = new HashMap<String, Integer>();
  //private int dICD = 6;
  
  public HashMap<String, Integer> FISHcd = new HashMap<String, Integer>();
  private int fICD = 10;
  
  public HashMap<String, Integer> camCD = new HashMap<String, Integer>();
  public HashMap<String, Integer> camCount = new HashMap<String, Integer>();
  
  public int CAMcd = 35;
  public int CAMcount = 3;
  public int CAMslowdur = 60;
  public int CAMslowamp = 1;
  public int CAMdur = 200; // 10 seconds
  
  @EventHandler
  public void onInteract (PlayerInteractEvent event)
  {
      Player player = event.getPlayer();
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
      {
          if (player.getItemInHand() != null)
          {
              if (player.getItemInHand().getType() == Material.DIAMOND_SWORD || player.getItemInHand().getType() == Material.IRON_SWORD || player.getItemInHand().getType() == Material.GOLD_SWORD
                      || player.getItemInHand().getType() == Material.STONE_SWORD || player.getItemInHand().getType() == Material.WOOD_SWORD)
              {
                  if (player.hasPermission("scout.use"))
                  {
                      int currenttime = plugin.getCurrentTime();
                      
                      boolean canCAM = false;
                      
                      if (camCD.containsKey(player.getName()))
                      {
                          int timecheck = camCD.get(player.getName());
                          int totaltime = currenttime - timecheck;
                          
                          if (totaltime > CAMcd || totaltime < 0)
                          {
                              camCD.remove(player.getName());
                              canCAM = true;
                          }
                          else
                          {
                              canCAM = false;
                              player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Come At Me! on cooldown for another " + (CAMcd - totaltime) + " seconds.");
                              return;
                          }
                      }
                      else
                      {
                          canCAM = true;
                      }
                      
                      if (canCAM)
                      {
                          camCD.put(player.getName(), currenttime);
                          camCount.put(player.getName(), CAMcount);
                          for (Player p : Bukkit.getOnlinePlayers())
                          {
                              if (p.getWorld().equals(player.getWorld()))
                              {
                                  if (p.getLocation().distance(player.getLocation()) <= 16)
                                  {
                                      p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + " says, '" + ChatColor.DARK_AQUA + "Come At Me, bro!" + ChatColor.GOLD + "'.");
                                  }
                              }
                          }
                          
                          new CAM(player);
                      }
                  }
              }
          }
      }
  }
  
  
  @EventHandler
  public void onEntityDamage(EntityDamageEvent event)
  {
     
    if ((event.getCause() == EntityDamageEvent.DamageCause.FALL) && ((event.getEntity() instanceof Player)))
    {
      Player player = (Player)event.getEntity();

      if (player.hasPermission("scout.use"))
      {
          if (player.getFallDistance() > 20)
          {
              return;
          }
          
          event.setCancelled(true);
      }

    }
    
    
    
    if (!(event instanceof EntityDamageByEntityEvent)) return;
    EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
    
    
    if ((event_EE.getDamager() instanceof Player))
    {
      Player damager = (Player)event_EE.getDamager();

        if (Party.ghost.contains(damager.getName()))
        {
            event.setCancelled(true);
            event.setDamage(0);
            damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Ghosts cannot attack!");
            return;
        }
        
        if (PL.stuntarget.containsKey(damager.getName().toLowerCase()) || Paladin.isStunned.contains(damager.getName()) || ChaosCrusader.chaosstunned.containsKey(damager.getName()))
      {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
            return;
      }
        
        
      // if damager is not scout
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player)event.getEntity();

            if (player.hasPermission("scout.use"))
            {
                if ((player.getInventory().getChestplate() != null) && (player.getInventory().getLeggings() != null) && (player.getInventory().getBoots() != null) && (player.getInventory().getHelmet() != null))
                {
                    ItemStack chest = player.getInventory().getChestplate();
                    ItemStack legs = player.getInventory().getLeggings();
                    ItemStack boots = player.getInventory().getBoots();
                    ItemStack helm = player.getInventory().getHelmet();

                    if ((chest.getTypeId() == 307) && (legs.getTypeId() == 308) && (helm.getTypeId() == 314) && (boots.getTypeId() == 317))
                    {
                        if (player.getNoDamageTicks() < player.getMaximumNoDamageTicks() / 3.0F)
                        {
                            if (camCount.containsKey(player.getName()))
                            {
                                int count = camCount.get(player.getName());

                                if (count >= 1)
                                {
                                    if (damager.hasPotionEffect(PotionEffectType.SLOW))
                                    {
                                        return;
                                    }
                                    else
                                    {
                                        int newcount = count - 1;
                                        camCount.remove(player.getName());
                                        if (newcount == 0)
                                        {
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your bravado seems to fade.");
                                            return;
                                        }
                                        else
                                        {
                                            camCount.put(player.getName(), newcount);
                                        }

                                        damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, CAMslowdur, CAMslowamp));
                                        damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are taken aback by " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.RED + "bravado, slowing you!");
                                    }
                                }
                            }
                        }
                        
                    }
                }
            }
        }
      
      // if damager is scout
      if ((damager.getInventory().getChestplate() != null) && (damager.getInventory().getLeggings() != null) && (damager.getInventory().getBoots() != null) && (damager.getInventory().getHelmet() != null))
      {
        ItemStack chest = damager.getInventory().getChestplate();
        ItemStack legs = damager.getInventory().getLeggings();
        ItemStack boots = damager.getInventory().getBoots();
        ItemStack helm = damager.getInventory().getHelmet();

        if ((chest.getTypeId() == 307) && (legs.getTypeId() == 308) && (helm.getTypeId() == 314) && (boots.getTypeId() == 317))
        {
          if (damager.hasPermission("scout.use"))
          {
              
            LivingEntity monster = (LivingEntity)event.getEntity();

            

            double random = Math.random();

            int time = plugin.getCurrentTime();
            
            if (monster.getNoDamageTicks() < monster.getMaximumNoDamageTicks()/3.0F)
            {

                if ((random > 0.05D) && (random < 0.35D))
                {
                    if (monster instanceof Player)
                    {
                        monster.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
                    }
                }
                
                if ((random > 0.35D) && (random < 0.6D))
                {
                    if (!(TLFcd.containsKey(damager.getName())))
                    {
                        if (damager.hasPotionEffect(PotionEffectType.POISON))
                        {
                            damager.removePotionEffect(PotionEffectType.POISON);
                            damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON,0,0));
                        }

                        damager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
                        damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You Proc " + ChatColor.GOLD + "That Loving Feeling " + ChatColor.AQUA + "you have increased Regen!");

                        TLFcd.put(damager.getName(), time);
                    }
                    else
                    {
                        int timecheck = TLFcd.get(damager.getName());
                        int totaltime = (time - timecheck);

                        if (totaltime > ICD || totaltime < 0)
                        {
                            TLFcd.remove(damager.getName());
                        }
                    }
                }

                if ((random > 0.6D) && (random < 0.65D))
                {
                    if (!(FISHcd.containsKey(damager.getName())))
                    {
                        event.setDamage(event.getDamage() + 5);

                        damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You Proc " + ChatColor.GOLD + "Fish to the FACE! " + ChatColor.AQUA + "your target takes extra damage!");

                        if ((monster instanceof Player))
                        {
                          Player player = (Player)event.getEntity();
                          player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You were hit by " + ChatColor.GOLD + "Fish to the FACE! OUCH!");
                        }
                        
                        FISHcd.put(damager.getName(), time);
                    }
                    else
                    {
                        int timecheck = FISHcd.get(damager.getName());
                        int totaltime = (time - timecheck);

                        if (totaltime > fICD || totaltime < 0)
                        {
                            FISHcd.remove(damager.getName());
                        }
                    }
                   
                }
            }

          }

        }

      }

    }
    /*
    if ((event.getEntity() instanceof Player))
    {
      Player player = (Player)event_EE.getEntity();

      if (player.hasPermission("scout.use"))
      {
        double damage = event.getDamage() * 2.25D;
        double newdamage = damage;

        event.setDamage(newdamage);
      }
    }
    */
    
  } // end EntityDamage
  
    
  
  
    private class CAM implements Runnable
    {
        int taskID;
        Player player;
        
        public CAM(Player player)
        {
            this.player = player;
            this.taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, CAMdur);
        }
        
        @Override
        public void run() 
        {
            if (camCount.containsKey(player.getName()))
            {
                camCount.remove(player.getName());
                
                if (player.isOnline())
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your bravado seems to fade.");
                }
            }
        }

    }
  
}
