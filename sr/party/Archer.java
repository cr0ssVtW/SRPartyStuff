package sr.party;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Archer implements Listener
{
	final int REQUIRED_COOLDOWN = 10;
	Random randomGenerator = new Random();
	public Party plugin;
	boolean hasperm;
        private Entity larrow;
        private LivingEntity shooter;
	
        public HashMap<String, Integer> leaptimer = new HashMap<String, Integer>();
        public HashMap<String, Float> forceHash = new HashMap<String, Float>();
        private int leapCD = 17;
        private double leapVelocity = (40 / 9D);
        private double leapYVelocity = (10 / 7D);
       // private double sneakleapVelocity = (16 / 10D);
       // private double sneakleapYVelocity = (18 / 10D);
        public HashSet<String> isLeaping = new HashSet<String>();
        
	public Archer(Party plugin)
	{
		this.plugin = plugin;
	}

        
        
         /*    
        
        @EventHandler(priority = EventPriority.LOW)
        public void onProjectileHit(ProjectileHitEvent event) 
	{

		Entity entity = event.getEntity();
		if (entity instanceof Arrow)
		{
			Arrow arrow = (Arrow) event.getEntity();

			if (arrows.remove(arrow) && arrow.getShooter() instanceof Player)
			{
                            Player player = (Player)arrow.getShooter();
				if ((this.randomGenerator.nextInt(100) < plugin.getConfig().getInt("LightningChance", 10)))
					entity.getWorld().strikeLightning(entity.getLocation());
			}

		}

	}

	/* End Lightning */
        
        

        @EventHandler
        public void onMove (PlayerMoveEvent event)
        {
            Player player = event.getPlayer();
            
            if (player.isOp())
            {
                return;
            }
            
            /*
            if (player.hasPermission("archerarmor.use"))
            {
                if (player.getInventory().getHelmet() != null)
                {
                    if (!(player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET))
                    {
                        ItemStack helm = player.getInventory().getHelmet();

                        player.getInventory().addItem(helm);
                        player.getInventory().setHelmet(null);

                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Marksmen must wear Leather Armor!");
                    }
                }

                if (player.getInventory().getChestplate() != null)
                {
                    if (!(player.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE))
                    {
                        ItemStack chest = player.getInventory().getChestplate();

                        player.getInventory().addItem(chest);
                        player.getInventory().setChestplate(null);


                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Marksmen must wear Leather Armor!");
                    }
                }

                if (player.getInventory().getLeggings() != null)
                {
                    if (!(player.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS))
                    {
                        ItemStack legs = player.getInventory().getLeggings();

                        player.getInventory().addItem(legs);
                        player.getInventory().setLeggings(null);


                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Marksmen must wear Leather Armor!");
                    }
                }

                if (player.getInventory().getBoots() != null)
                {
                    if (!(player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS))
                    {
                        ItemStack boots = player.getInventory().getBoots();

                        player.getInventory().addItem(boots);
                        player.getInventory().setBoots(null);


                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Marksmen must wear Leather Armor!");
                    }
                }
            }
            */
            
            
        }
        
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event)
	{
            
		double basedmg = event.getDamage();
                
                if (!(event.getEntity() instanceof Player))
                {
                    return;
                }
                
                Player player = (Player) event.getEntity();
                
                if (isLeaping.contains(player.getName()))
                {
                    if (event.getCause() == DamageCause.FALL)
                    {
                        event.setCancelled(true);
                        isLeaping.remove(player.getName());
                        player.playSound(player.getLocation(), Sound.FALL_SMALL, 25, 25);
                        //player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,60,2));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,40,1));
                        return;
                    }
                }
                
                if (!(event instanceof EntityDamageByEntityEvent)) return;
		EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent) event;
                // BEGIN GLOBAL LEATHER DMG REDUCTION INCREASE
                if (player.getInventory().getHelmet() != null)
                {
                    if (player.getInventory().getHelmet().getTypeId() == 298)
                    {
                        event.setDamage((int)Math.round(event.getDamage() * 0.95D));
                    }
                    
                }
                
                if (player.getInventory().getChestplate() != null)
                {
                
                    if (player.getInventory().getChestplate().getTypeId() == 299)
                    {
                        event.setDamage((int)Math.round(event.getDamage() * 0.90D));
                    }
                    
                }
                
                if (player.getInventory().getLeggings() != null)
                {
                
                    if (player.getInventory().getLeggings().getTypeId() == 300)
                    {
                        event.setDamage((int)Math.round(event.getDamage() * 0.90D));
                    }
                    
                }
                
                if (player.getInventory().getBoots() != null)
                {
                
                    if (player.getInventory().getBoots().getTypeId() == 301)
                    {
                        event.setDamage((int)Math.round(event.getDamage() * 0.95D));
                    }
                    
                }
                
                // DMG REDUCTION END
                
		if (!(event_EE.getDamager() instanceof Arrow)) return;
	

                

		Projectile projectile = (Projectile) event_EE.getDamager();
		if (arrows.remove(projectile))
		{
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You've been hit by a " + ChatColor.GOLD + "Poison Shot.");
                    LivingEntity target = (LivingEntity) event.getEntity();
                    
                        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 2));

			
		}

		if (cripplearrow.remove(projectile))
		{
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You took an " + ChatColor.GOLD + "Arrow to the Knee! " + ChatColor.RED + "Your adventurer career is over.");
                    LivingEntity target = (LivingEntity) event.getEntity();
                        
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 3));

		}
                
                if (!(projectile.getShooter() instanceof Player))
                {
                    return;
                }
                
                Player attacker = (Player) projectile.getShooter();
                
                
                if (!(attacker instanceof Player)) return;
                
                if (Party.ghost.contains(attacker.getName()))
                {
                    event.setCancelled(true);
                    event.setDamage(0);
                    attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Ghosts cannot attack!");
                    return;
                }
                
               // Bukkit.broadcastMessage("Damage is: " + event.getDamage());
                
                //   REPLACED BY OMEGA'S THING IN SHOOTEVENT
                 
                if (attacker.hasPermission("archerarmor.use"))
                {
                    PlayerInventory inv = attacker.getInventory();
                    org.bukkit.inventory.ItemStack helm = inv.getHelmet();
                    org.bukkit.inventory.ItemStack chest = inv.getChestplate();
                    org.bukkit.inventory.ItemStack legs = inv.getLeggings();
                    org.bukkit.inventory.ItemStack boots = inv.getBoots();
                   // int damage = event.getDamage();
                   // int bonusdamage = 0;
                    int multiplier = 1;
                    
                    double helmdamage = 0;
                    double chestdamage = 0;
                    double legdamage = 0;
                    double bootdamage = 0;
                    
                    float newForce = 0;
                    
                    if (forceHash.containsKey(attacker.getName()))
                    {
                        newForce = forceHash.get(attacker.getName());
                        forceHash.remove(attacker.getName());
                    }
                    
                    if (attacker.getInventory().getHelmet() != null)
                    {
                        if (helm.getTypeId() == 298)
                        {
                            if (newForce > 0 && newForce <= 0.15)
                            {
                                helmdamage = 0;
                            }
                            if (newForce > 0.15 && newForce <= 0.25)
                            {
                                helmdamage = 0;
                            }
                            if (newForce > 0.25 && newForce <= 0.4)
                            {
                                helmdamage = 0;
                            }
                            if (newForce > 0.4 && newForce <= 0.6)
                            {
                                helmdamage = 0;
                            }
                            if (newForce > 0.6 && newForce <= 0.8)
                            {
                                helmdamage = 0;
                            }
                            if (newForce > 0.8 && newForce < 1.0)
                            {
                                helmdamage = 1;
                            }
                            if (newForce == 1.0)
                            {
                                helmdamage = 1;
                            }
                        }
                    }

                    if (attacker.getInventory().getChestplate() != null)
                    {
                    
                        if (chest.getTypeId() == 299)
                        {
                            if (newForce > 0 && newForce <= 0.15)
                            {
                                chestdamage = 0;
                            }
                            if (newForce > 0.15 && newForce <= 0.25)
                            {
                                chestdamage = 0;
                            }
                            if (newForce > 0.25 && newForce <= 0.4)
                            {
                                chestdamage = 0;
                            }
                            if (newForce > 0.4 && newForce <= 0.6)
                            {
                                chestdamage = 0;
                            }
                            if (newForce > 0.6 && newForce <= 0.8)
                            {
                                chestdamage = 1;
                            }
                            if (newForce > 0.8 && newForce < 1.0)
                            {
                                chestdamage = 2;
                            }
                            if (newForce == 1.0)
                            {
                                chestdamage = 2;
                            }
                        }
                    }
                    
                    if (attacker.getInventory().getLeggings() != null)
                    {
                        if (legs.getTypeId() == 300)
                        {
                            if (newForce > 0 && newForce <= 0.15)
                            {
                                legdamage = 0;
                            }
                            if (newForce > 0.15 && newForce <= 0.25)
                            {
                                legdamage = 0;
                            }
                            if (newForce > 0.25 && newForce <= 0.4)
                            {
                                legdamage = 0;
                            }
                            if (newForce > 0.4 && newForce <= 0.6)
                            {
                                legdamage = 0;
                            }
                            if (newForce > 0.6 && newForce <= 0.8)
                            {
                                legdamage = 1;
                            }
                            if (newForce > 0.8 && newForce < 1.0)
                            {
                                legdamage = 2;
                            }
                            if (newForce == 1.0)
                            {
                                legdamage = 2;
                            }
                        }
                    
                    }
                    
                    if (attacker.getInventory().getBoots() != null)
                    {
                    
                        if (boots.getTypeId() == 301)
                        {
                           if (newForce > 0 && newForce <= 0.15)
                            {
                                bootdamage = 0;
                            }
                            if (newForce > 0.15 && newForce <= 0.25)
                            {
                                bootdamage = 0;
                            }
                            if (newForce > 0.25 && newForce <= 0.4)
                            {
                                bootdamage = 0;
                            }
                            if (newForce > 0.4 && newForce <= 0.6)
                            {
                                bootdamage = 0;
                            }
                            if (newForce > 0.6 && newForce <= 0.8)
                            {
                                bootdamage = 0;
                            }
                            if (newForce > 0.8 && newForce < 1.0)
                            {
                                bootdamage = 1;
                            }
                            if (newForce == 1.0)
                            {
                                bootdamage = 1;
                            } 
                        }
                    }
                    
                    double totalbonus = (helmdamage + chestdamage + legdamage + bootdamage);
                    
                    double totaldamage = Math.round((event.getDamage() * multiplier) + totalbonus);
                    event.setDamage(totaldamage);

                    //Bukkit.broadcastMessage("Base damage is: " + basedmg + "\nBonusDamage is: " + totalbonus + "\nTotal Damage is: " + totaldamage);
                } // end archerarmor.use
  
	} // end entity damage
   


	
	private int getCurrentTime()
	{
		Calendar calendar = new GregorianCalendar();

		int hour = calendar.get(Calendar.HOUR) * 3600;
		int minute = calendar.get(Calendar.MINUTE) * 60;
		int second = calendar.get(Calendar.SECOND);

		return hour + minute + second;
	}

	public static HashMap<Player, Integer> hm = new HashMap<Player, Integer>();
	public static HashSet<Projectile> arrows = new HashSet<Projectile>();
        
        public static HashMap<Player, Integer> crippleplayer = new HashMap<Player, Integer>();
        public static HashSet<Projectile> cripplearrow = new HashSet<Projectile>();

        public static HashMap<Player, String> leftclick = new HashMap<Player, String>();

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event)
	{
            Projectile projectile = (Projectile) event.getProjectile();

            if (!(projectile.getShooter() instanceof Player)) 
            {
                return;
            }
		
            Player player = (Player) projectile.getShooter();
            
            if (player.hasPermission("archerarmor.use"))
            {
                forceHash.put(player.getName(), event.getForce());
                
                int armorbonus = 0;
                if(player.getEquipment().getChestplate() != null)
                {
                    if(player.getEquipment().getChestplate().getType().equals(Material.LEATHER_CHESTPLATE))
                    {
                            armorbonus = armorbonus + 7;
                    }
                }

                if(player.getEquipment().getLeggings() != null)
                {
                    if(player.getEquipment().getLeggings().getType().equals(Material.LEATHER_LEGGINGS))
                    {
                            armorbonus = armorbonus + 5;
                    }
                }
                if(player.getEquipment().getHelmet() != null)
                {
                    if(player.getEquipment().getHelmet().getType().equals(Material.LEATHER_HELMET))
                    {
                            armorbonus = armorbonus + 3;
                    }
                }
                if(player.getEquipment().getBoots() != null)
                {
                    if(player.getEquipment().getBoots().getType().equals(Material.LEATHER_BOOTS))
                    {
                            armorbonus = armorbonus + 2;
                    }
                }
                
                float draw = event.getForce();
                
                projectile.setVelocity(projectile.getVelocity().multiply(1 + (0.5 * ((armorbonus*5) /100))*draw));
                //Bukkit.broadcastMessage("Armor Bonus is: " + armorbonus);
            }
            
            
            if (player.hasPermission("archerpoison.use"))
            {
		
		if (!player.isSneaking()) 
                {
                    return;
                } 
                if (Party.ghost.contains(player.getName())
                {
                   event.setCancelled(true);
                   player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Ghosts cannot attack!");
                   return;
                }
            

		

                String whichattack2 = (String) leftclick.get(player);

	      if (whichattack2 == null)
	      {
                  
		int currenttime = getCurrentTime();
		Integer integer = (Integer) hm.get(player);
		if (integer != null)
		{
			
			int cooldown = (integer.intValue() + REQUIRED_COOLDOWN) - currenttime;
                        
                        if (cooldown > 10)
                        {
                            cooldown = 0;
                        }
                        
			if (cooldown > 0)
			{
				player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Poison Shot " + ChatColor.RED + "on cooldown for: " + ChatColor.GOLD + cooldown + ChatColor.RED + " seconds");
				return;
			}
		}
		
		
		hm.put(player, currenttime);
		arrows.add(projectile);
              }

	      if ("Poison".equals(whichattack2) )
	      {
                  
		int currenttime = getCurrentTime();
		Integer poisoninteger = (Integer) hm.get(player);
		if (poisoninteger != null)
		{
			

			

			int poisoncooldown = (poisoninteger.intValue() + REQUIRED_COOLDOWN) - currenttime;
                        
                        if (poisoncooldown > 10)
                        {
                            poisoncooldown = 0;
                        }
                        
                        
			if (poisoncooldown > 0)
			{
				player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Poison Shot " + ChatColor.RED + "on cooldown for: " + ChatColor.GOLD + poisoncooldown + ChatColor.RED + " seconds");
				return;
			}
		}
		
		
		hm.put(player, currenttime);
		arrows.add(projectile);
                
              }

              /* Arrow to the Knee */
              
	      if ("Cripple".equals(whichattack2) )
	      {
                  
		int currenttime = getCurrentTime();
		Integer crippleinteger = (Integer) crippleplayer.get(player);
		if (crippleinteger != null)
		{
			int cripplecooldown = (crippleinteger.intValue() + REQUIRED_COOLDOWN) - currenttime;
                        
                        if (cripplecooldown > 12)
                        {
                            cripplecooldown = 0;
                        }
                        
			if (cripplecooldown > 0)
			{
				player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Arrow to the Knee " + ChatColor.RED + "on cooldown for: " + ChatColor.GOLD + cripplecooldown + ChatColor.RED + " seconds");
				return;
			}
		}
		
		
		crippleplayer.put(player, currenttime);
		cripplearrow.add(projectile);
                
                
                    PlayerInventory inv = player.getInventory();
                    org.bukkit.inventory.ItemStack helm = inv.getHelmet();
                    org.bukkit.inventory.ItemStack chest = inv.getChestplate();
                    org.bukkit.inventory.ItemStack legs = inv.getLeggings();
                    org.bukkit.inventory.ItemStack boots = inv.getBoots();
                    
                    if (helm != null && chest != null && legs != null && boots != null)
                    {
                        if (helm.getTypeId() == 298 && chest.getTypeId() == 299 && legs.getTypeId() == 300 && boots.getTypeId() == 301)
                        {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 1));
                        }
                    }
              }
             }
	}
        

//        public static HashMap<Player, String> leftclick = new HashMap<Player, Integer>();
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
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                {
                
                    if (player.getItemInHand().getTypeId() == 261) // if bow
                    {
                      if (player.hasPermission("archersnare.use"))
                      {
                    
                  	String whichattack = (String) leftclick.get(player);
                         
                        if (whichattack == null)
		         {
			  leftclick.put(player, "Cripple");

			  player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You ready " + ChatColor.GOLD + "Arrow to the Knee.");
			 }

                        if ("Cripple".equals(whichattack))
                         {
                          leftclick.put(player, "Poison");

                          player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You ready " + ChatColor.GOLD + "Poison Shot.");
                         }
                        
                        if ("Poison".equals(whichattack))
		         {
			  leftclick.put(player, "Nothing");

			  player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You decide not to shoot.");
			 }
                        
                        if ("Nothing".equals(whichattack))
                        {
                            leftclick.put(player, "Cripple");
                            
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You ready " + ChatColor.GOLD + "Arrow to the Knee.");
                        }
                      } // end if perm
                   
                    } // end if bow
                    
                    if (player.getItemInHand().getTypeId() == 288) // if feather
                    {
                      if (player.hasPermission("sr.marksman.leap"))
                      {
                          if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null
                                  && player.getInventory().getBoots() != null)
                          {
                              if (player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET && player.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE
                                      && player.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS && player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS)
                              {
                                  boolean leapcheck;
                                int currenttime = getCurrentTime();

                                if (leaptimer.containsKey(player.getName()))
                                {

                                    int ltimer = leaptimer.get(player.getName());
                                    int totaltime = (currenttime - ltimer);

                                    if (totaltime > leapCD || totaltime < 0)
                                    {
                                        leapcheck = false;
                                    }
                                    else
                                    {
                                        leapcheck = true;
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " 
                                                + ChatColor.AQUA + (leapCD - totaltime) + ChatColor.RED + " seconds to use " + ChatColor.GOLD + "Leap" + ChatColor.RED + " again.");
                                        return;
                                    }
                                }
                                else
                                {
                                    leapcheck = false;
                                }

                                if (leapcheck == false)
                                {

                                  /*
                                  if (player.isSneaking())
                                  {
                                    Vector v = player.getLocation().getDirection();

                                    for (Player p : plugin.getServer().getOnlinePlayers())
                                    {
                                        if (p.getWorld().equals(player.getWorld()))
                                        {
                                            if (p.getLocation().distance(player.getLocation()) < 20)
                                            {
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.DARK_AQUA + " leaps away!");
                                            }
                                        }
                                    }

                                    v.setY(0).normalize().multiply(sneakleapVelocity).setY(sneakleapYVelocity);
                                    player.setVelocity(v);

                                    player.playSound(player.getLocation(), Sound.FALL_BIG, 25, 25);
                                    player.playEffect(EntityEffect.WOLF_SMOKE);

                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,60,2));
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,60,2));

                                    isLeaping.add(player.getName());
                                    leaptimer.put(player.getName(), currenttime);
                                  }
                                  else
                                  {
                                  */

                                    for (Player p : plugin.getServer().getOnlinePlayers())
                                    {
                                        if (p.getWorld().equals(player.getWorld()))
                                        {
                                            if (p.getLocation().distance(player.getLocation()) < 20)
                                            {
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.DARK_AQUA + " leaps away!");
                                            }
                                        }
                                    }

                                    Vector v = player.getLocation().getDirection();
                                    v.setY(0).normalize().multiply(leapVelocity).setY(leapYVelocity);
                                    player.setVelocity(v);

                                    player.playSound(player.getLocation(), Sound.FALL_SMALL, 25, 25);
                                    player.playEffect(EntityEffect.WOLF_SMOKE);
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,60,2));
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,60,2));

                                    isLeaping.add(player.getName());
                                    leaptimer.put(player.getName(), currenttime);
                                //  }
                                }
                              }
                                
                          }
                          
                        
                      } // end perm
                    } // end feather
                }
        } // end interact
}
