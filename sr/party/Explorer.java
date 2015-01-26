package sr.party;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Cross
 */
public class Explorer implements Listener
{
    public HashMap<String, Integer> explorer = new HashMap();
    public Party plugin;
    
    public Explorer(Party plugin)
    {
        this.plugin = plugin;
    }
    
  @EventHandler
  public void onEntityDamage(EntityDamageEvent event)
  {


    if (!(event instanceof EntityDamageByEntityEvent)) return;
    EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;

    if (((event_EE.getDamager() instanceof Player)) && (!(event_EE.getEntity() instanceof Player)))
    {
      Player damager = (Player)event_EE.getDamager();

      if (damager.hasPermission("explorer.use"))
      {
        double random = Math.random();
        double random2 = Math.random();

        if (random < 0.1D)
        {
          event.setDamage(event.getDamage() * 4.0D);

          damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "Your Explorer abilities " + ChatColor.GOLD + "Super Strike " + ChatColor.AQUA + "your target");
        }

        if (random > 0.90D)
        {
          int total = 0;
          if (!this.explorer.containsKey(damager.getName().toLowerCase()))
          {
            total = 0;
          }
          else
          {
            total = ((Integer)this.explorer.get(damager.getName().toLowerCase())).intValue();
          }

          if (total < 25)
          {
            ItemStack goldingot = new ItemStack(Material.GOLD_INGOT, 1);
            String itemdropped = "a Gold Ingot!";
            if (random > 0.95D)
            {
              goldingot = new ItemStack(Material.GOLD_INGOT, 3);
              itemdropped = "three Gold Ingots!";
            }

            if ((random > 0.92D) && (random < 0.95D))
            {
              goldingot = new ItemStack(Material.GOLD_INGOT, 5);
              itemdropped = "five Gold Ingots!";
            }

            if (random == 0.9D)
            {
              goldingot = new ItemStack(Material.DIAMOND, 1);
              total += 3;
              itemdropped = "a Diamond!";
            }

            LivingEntity monster = (LivingEntity)event.getEntity();
            Location loc = monster.getLocation();

            monster.getWorld().dropItemNaturally(loc, goldingot);

            damager.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "Your explorer abilities turn up " + ChatColor.GOLD + itemdropped);

            int total2 = total + 1;
            this.explorer.put(damager.getName().toLowerCase(), Integer.valueOf(total2));
          }

        }

      }

    }

    
    
    
    
    
    
    
    

    
// NEWBIE DAMAGE REDUCTION
    
    if ((event.getEntity() instanceof Player))
    {
      Player player = (Player)event_EE.getEntity();

      if (!player.hasPermission("starter.start"))
      {
        double damage = event.getDamage() * 0.8D;
        double newdamage = damage;

        event.setDamage(newdamage);
      }
    }
    
    
  } // end damage 
  
  
// begin explorer move 
    
    
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();

    if (player.hasPermission("explorer.move"))
    {
      if (player.getInventory().getBoots() != null)
      {
        ItemStack boots = player.getInventory().getBoots();

        if (boots.getTypeId() == 317)
        {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 72000, 3));
          //player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 0));
          player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 72000, 0));
        }
      }

    }

    if (player.hasPermission("scout.move"))
    {
      if ((player.getInventory().getChestplate() != null) && (player.getInventory().getLeggings() != null) && 
              (player.getInventory().getBoots() != null) && (player.getInventory().getHelmet() != null))
      {
        ItemStack chest = player.getInventory().getChestplate();
        ItemStack legs = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();
        ItemStack helm = player.getInventory().getHelmet();

        if ((chest.getTypeId() == 307) && (legs.getTypeId() == 308) && (helm.getTypeId() == 314) && (boots.getTypeId() == 317))
        {
          player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12000, 2));
          player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 12000, 2));
        }
        else
        {
            if (player.hasPotionEffect(PotionEffectType.SPEED))
            {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,0,0));
            }
            if (player.hasPotionEffect(PotionEffectType.JUMP))
            {
                player.removePotionEffect(PotionEffectType.JUMP);
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,0,0));
            }
        }
      }
      else
      {
          if (player.hasPotionEffect(PotionEffectType.SPEED))
          {
              player.removePotionEffect(PotionEffectType.SPEED);
              player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,0,0));
          }
          if (player.hasPotionEffect(PotionEffectType.JUMP))
          {
              player.removePotionEffect(PotionEffectType.JUMP);
              player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,0,0));
          }
      }
    }
  }
    
  
}
