package sr.party;

import com.garbagemule.MobArena.MobArena;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.garbagemule.MobArena.MobArenaHandler;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;

/*
 * This Main Class file has:
 *  Tailor & Brewer commands
 *  Ghost commands
 *  Shaman / Nightlord / Duelist commands
 *  /classes and /clear commands
 *  And of course, all the Party commands.
 * 
 */

public class Party extends JavaPlugin
{
   static final Logger log = Logger.getLogger("Minecraft");
   public static Set<String> ghost = new HashSet();
   
   public HashMap<String, Integer> party = new HashMap<String, Integer>();
   public HashMap<String, Integer> temphashmap = new HashMap<String, Integer>();
   public HashMap<String, Integer> invitedplayertime = new HashMap<String, Integer>();
   
   public HashSet<String> pcSpy = new HashSet<String>();
   
   
   public static File arenaFile;
   public static FileConfiguration arenas;
   
   public static MobArenaHandler maHandler;
   
   public String[] splice1;
   public String[] splice2;
   
   public String arenaplayer;
   public String arenatarget;
   
 
    public int a;
    
    public HashSet<String> queryDuel = new HashSet<String>();
    public HashSet<String> isDueling = new HashSet<String>();
    
    public HashSet<String> inArena2 = new HashSet<String>();
    
    public HashSet<Integer> inArena = new HashSet<Integer>();
    public HashMap<String, Integer> intArena = new HashMap<String, Integer>();
    
    public HashMap<String, String> paired = new HashMap<String, String>();
    public HashMap<String, String> paired2 = new HashMap<String, String>();
    
    private HashMap<String, String> duelWaiting = new HashMap<String, String>();
    
    public HashMap<String, Location> playerloc = new HashMap<String, Location>();
    
    public HashMap<String, Integer> money = new HashMap<String, Integer>();
    
    
    public boolean started = false;
    
    public int moneyint;
    
    public int duelIssued;
   
    public static HashMap<String, Integer> potiontimer = new HashMap(); // brewer
    public static HashSet<String> tailor = new HashSet(); //tailor
    public static HashMap<String, Integer> classtimer = new HashMap(); // classes

   final Set<Integer> partynumber = new HashSet();
   public HashMap<Integer, Integer> partysize = new HashMap<Integer, Integer>();
   public int MaxPartySize = 4;
   public HashSet<String> isLeader = new HashSet();
   
   
   public Party plugin;   

   public Shaman shaman;
   
    public static Economy econ = null;
    public static Permission perms = null;
    private static Vault vault = null;
   
    public Warlock lock;
    
    public void setupMobArenaHandler()
    {
        Plugin maPlugin = (MobArena) Bukkit.getServer().getPluginManager().getPlugin("MobArena");

        if (maPlugin == null) 
        {
            return;
        }

        maHandler = new MobArenaHandler();
    }
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
        {
            econ = economyProvider.getProvider();
        }
        return (econ != null);
    }
    
    private boolean setupPermissions() 
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
   
    
    

   public void onEnable()
   {
       
       PluginDescriptionFile pdfFile = getDescription();
       
       // Mob Arena
       
       setupMobArenaHandler();
       
       // load PManager

       // Names.load();
       
       
       loadFiles();
       
       // Misc Loaded
       getServer().getPluginManager().registerEvents(new Durability(this), this);
       getServer().getPluginManager().registerEvents(new EnchantStuff(this), this);
       getServer().getPluginManager().registerEvents(new HealthBuffs(this), this);
       
       // PvE Class Loads
       getServer().getPluginManager().registerEvents(new Ghost(this), this);
       getServer().getPluginManager().registerEvents(new Explorer(this), this);
       getServer().getPluginManager().registerEvents(new Duelist(this), this);
       getServer().getPluginManager().registerEvents(new Tailor(this), this);
       getServer().getPluginManager().registerEvents(new Brewer(this), this);
       
      
       // PvP Class Loads    
       getServer().getPluginManager().registerEvents(new Archer(this), this);
       getServer().getPluginManager().registerEvents(new Archmage(this), this);
       getServer().getPluginManager().registerEvents(new Assassin(this), this);
       getServer().getPluginManager().registerEvents(new ChaosCrusader(this), this);
       getServer().getPluginManager().registerEvents(new Gladiator(this), this);
       getServer().getPluginManager().registerEvents(new Martyr(this), this);
       getServer().getPluginManager().registerEvents(new Nightlord(this), this);
       getServer().getPluginManager().registerEvents(new Paladin(this), this);
       getServer().getPluginManager().registerEvents(new PL(this), this); // houses Berserker and Bard plus other misc stuff
       getServer().getPluginManager().registerEvents(new Pyromancer(this), this);
       getServer().getPluginManager().registerEvents(new Ranger(this), this);
       getServer().getPluginManager().registerEvents(new Scout(this), this);
       getServer().getPluginManager().registerEvents(new ShadowKnight(this), this);
       getServer().getPluginManager().registerEvents(new Shaman(this), this);
       getServer().getPluginManager().registerEvents(new Skirmisher(this), this);
       getServer().getPluginManager().registerEvents(new Warlock(this), this);

       
       
   
       Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
       
           if (!setupEconomy() )
           {
               log.info(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
               getServer().getPluginManager().disablePlugin(this);
               return;
           }
           
           if ((setupEconomy()) && (econ != null)) 
            {
                vault = (Vault) x;

            } 
            else 
            {
               getPluginLoader().disablePlugin(this);
                return;
            }
           
           setupPermissions();
       
       

       this.log.info("[" + pdfFile.getName() + "] (By cr0ssVtW, acidsin) - v" + pdfFile.getVersion() + " loaded.");
   }

   public void onDisable()
	
   {  
       PluginDescriptionFile pdfFile = getDescription();
       this.log.info("[" + pdfFile.getName() + "] (By cr0ssVtW, acidsin) - v" + pdfFile.getVersion() + " unloaded.");
       
       
       /*
        * Clean up for Warlock Skeletons
        */
       
       lock.cleanUP();
       shaman.cleanUP();
       
       /*
        * Clean up for Pyro Balls
        */
        List<World> w = Bukkit.getWorlds();
        int amtbig = 0;
        
        for(World o : w)
        {

            for(Entity e : o.getEntities())
            {

                if ((e instanceof Fireball) || (e instanceof SmallFireball))
                {
                  e.remove();

                  amtbig += 1;
                }

            }

        }
        System.out.println("Amount of Fireballs removed: " + amtbig);
       
       /*
        * 
        */

       
       Iterator<Player> iterator = Nightlord.helmplayer.iterator();
        while (iterator.hasNext())
        {
            Player player = iterator.next();

        if (player.getInventory().getHelmet() != null)
           {

              if (Nightlord.oldhelm.containsKey(player))
                {
                   ItemStack helm2 = Nightlord.oldhelm.get(player);
                   Nightlord.oldhelm.remove(player);
                   Nightlord.helmplayer.remove(player);
                   player.getInventory().setHelmet(helm2);
                }

            }
        }
   }
   
   
   public void loadFiles()
   {
       Boolean exists = new File("plugins/SRParty").exists();
       
       if (!exists)
       {
           Boolean mkdir = new File("plugins/SRParty").mkdir();
       }
       
       arenaFile = new File("plugins/SRParty/arenas.yml");
       
       if (!arenaFile.exists())
       {
           try
           {
               arenaFile.createNewFile();
           } catch (IOException e)
           {
               System.out.println("[SRParty] Failed to create arenas.yml - Check read/write perms.");
               e.printStackTrace();
           }
       }
       
       arenas = YamlConfiguration.loadConfiguration(arenaFile);
   }
   
    public void unStealth(Player player)
    {
        CraftPlayer cplr = (CraftPlayer)player;

        for (Player other : Bukkit.getServer().getOnlinePlayers())
        {
          if ((!other.equals(player)) && (!other.canSee(player)))
          {
             other.showPlayer(player);
          }
            
        }
    }

    public void onStealth(Player player)
    {
        CraftPlayer cplr = (CraftPlayer)player;

        for (Player other : Bukkit.getServer().getOnlinePlayers())
        {
          if ((!other.equals(player)) && (other.canSee(player)))
          {
            if (!other.hasPermission("assassin.see"))
            {
                other.hidePlayer(player);
            }
          }
        }
    }
   
    
   
   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
    {
        
        Player player = null;
        
        if (sender instanceof Player)
        {
            player = (Player)sender;
        }
       
        /*
         * 
         * TAILOR HERE
         * 
         */
        
        if (commandLabel.equalsIgnoreCase("tailor") &&(player.hasPermission("tailor.craft")))
        {
            if (player.getGameMode().equals(GameMode.CREATIVE))
            {
                player.sendMessage(ChatColor.GOLD + "You can not Tailor in Creative Mode.");
                return true;
            }
            if (player.getInventory().getBoots() != null && player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null)
            {
                   if (player.getInventory().getBoots().getTypeId() != 301 && player.getInventory().getHelmet().getTypeId() != 298 && player.getInventory().getChestplate().getTypeId() != 299 && player.getInventory().getLeggings().getTypeId() != 300)
                   {
                     player.sendMessage(ChatColor.GOLD + "You must have Leather items in each Armor slot to use /tailor.");
                     return true;
                   }
                              
                               if (tailor.contains(player.getName()))
                               {
                                   
                                   player.sendMessage(ChatColor.GOLD + "You can only /tailor once per server up time.  Try again later!");
                                   return true;
                               }
                               tailor.add(player.getName());
                               Random randomGenerator = new Random();
                               
                               if ((randomGenerator.nextInt(100) <= 25))
                               {
                                   player.getInventory().getHelmet().setTypeId(306);
                                   player.getInventory().getChestplate().setTypeId(307);
                                   player.getInventory().getLeggings().setTypeId(308);
                                   player.getInventory().getBoots().setTypeId(309);
                                   
                                   player.sendMessage(ChatColor.GOLD + "You tailored your armor into Iron!");
                                   return true;
                               }
                               if ((randomGenerator.nextInt(100) >= 80))
                               {
                                   player.getInventory().getHelmet().setTypeId(314);
                                   player.getInventory().getChestplate().setTypeId(315);
                                   player.getInventory().getLeggings().setTypeId(316);
                                   player.getInventory().getBoots().setTypeId(317);
                                   
                                   player.sendMessage(ChatColor.GOLD + "You tailored your armor into Gold!");
                                   return true;
                               }
                               if ((randomGenerator.nextInt(100) <= 55) && (randomGenerator.nextInt(100) >= 45))
                               {
                                   player.getInventory().getHelmet().setTypeId(310);
                                   player.getInventory().getChestplate().setTypeId(311);
                                   player.getInventory().getLeggings().setTypeId(312);
                                   player.getInventory().getBoots().setTypeId(313);
                                   
                                   player.sendMessage(ChatColor.GOLD + "You tailored your armor into Diamond!");
                                   return true;
                               }
                               
                               player.sendMessage(ChatColor.GOLD + "You failed to tailor your armor, try again next server up time!");
                
                
            }
            else
            {
                player.sendMessage(ChatColor.GOLD + "You must have Leather items in each Armor slot to use /tailor.");
            }
        }
        
        
        /*
         * END TAILOR
         * 
         */
        
        /*
         * 
         * BREWER HERE
         * 
         */
        
        if (commandLabel.equalsIgnoreCase("brew") &&(player.hasPermission("brewer.brew")))
        {
            if (player.getGameMode().equals(GameMode.CREATIVE))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You can not Brew in Creative Mode.");
                return true;
            }

            int stringsize = args.length;
            Inventory inv = player.getInventory();
            int firstslot = inv.firstEmpty();
            
            if (stringsize != 1)
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "The correct format is " + ChatColor.AQUA + " /brew <potionname> ");
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "The correct format is " + ChatColor.AQUA + " /brew <potionname> ");
                return true;
            }
            
           if (firstslot < 0)
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You must have an open item slot in your inventory. " + ChatColor.AQUA + "Empty a slot and try again.");
                return true;
            }
            
            String potionname = args[0];
            double money = econ.getBalance(player.getName());
            ItemStack potion;
            String playername = player.getName();
            boolean pot = false;

            
            if (potionname.equals("regeneration1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                pot = true;
                potion = new ItemStack(373, 1, (short) 8193);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }   
            
            if (potionname.equals("swiftness1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 8194);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }   
            if (potionname.equals("fireresistance1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 8195);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }   
            if (potionname.equals("healing1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 8197);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }   
            if (potionname.equals("strength1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 8201);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }     
           

            if (potionname.equals("regenerationsplash1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 16385);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            } 
            if (potionname.equals("swiftnesssplash1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 16386);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }      
            if (potionname.equals("fireresistancesplash1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 16387);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }      
            if (potionname.equals("healingsplash1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 16389);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }      
            if (potionname.equals("weaknesssplash1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 16392);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }      
            if (potionname.equals("strengthsplash1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 16393);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }      
            if (potionname.equals("slownesssplash1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 16394);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }        
      
            if (potionname.equals("slownesssplash1"))
            {
                if (money < 350)
                {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You do not have enough gold to brew this potion the cost is: " + ChatColor.AQUA + "350 gold.");
                return true;
                }
                
                pot = true;
                potion = new ItemStack(373, 1, (short) 16394);
                inv.setItem(firstslot, potion);
                econ.withdrawPlayer(playername, 350);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You just brewed a potion! The following amount has been deducted from your account: " + ChatColor.AQUA + "350 gold.");
            }           
         
            
            if (pot == false)
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You entered an incorrect potion name.  To get a list of potion names type" + ChatColor.AQUA + "/brewlist.");
            }

        }
        
        if (commandLabel.equalsIgnoreCase("brewlist") && (player.hasPermission("brewer.brew")))
        {
             player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "The following is a list of potions you can brew.  Use /brew potionname to brew that potion");
             player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "Level 1 Potions - Cost 350 Gold: " + ChatColor.GOLD + "regeneration1, swiftness1, fireresistance1, healing1, strength1, regenerationsplash1, swiftnesssplash1, fireresistance1, healingsplash1, weaknesssplash1, strengthsplash1, slownesssplash1.");
        }      
        
        if (commandLabel.equalsIgnoreCase("powerbrew") &&(player.hasPermission("brewer.brew")))
        {
            int timer;
            int timedifference;
            int cooldown;
            int totalcooldown;
            boolean canuse = true;
            
            if (potiontimer.containsKey(player.getName()))
            {
                timer = getCurrentTime() - ((Integer)potiontimer.get(player.getName())).intValue();
                
                
                if (timer < 300 && timer > -1)
                {
                    cooldown = getCurrentTime() - ((Integer)potiontimer.get(player.getName())).intValue();
                    totalcooldown = 300 - cooldown;
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You can use powerbrew again in: " + totalcooldown + " seconds.");
                    canuse = false;
                }
            }
            
            if (canuse == true)
            {


              if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) 
              {
                      player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 1));
                      
              }
              if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) 
              {
                      player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                      player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1));
                      
              }
              if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) 
              {
                      player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 1));
                      
              }
              if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) 
              {
                      player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                      player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 1));
                      
              }
              if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) 
              {
                      player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                      player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 1));
                      
              }
              if (player.hasPotionEffect(PotionEffectType.REGENERATION)) 
              {
                      player.removePotionEffect(PotionEffectType.REGENERATION);
                      player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 1));
                      
              }
              if (player.hasPotionEffect(PotionEffectType.SPEED)) 
              {
                      player.removePotionEffect(PotionEffectType.SPEED);
                      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
                      
              }
                            
              player.sendMessage(ChatColor.GOLD + "All of your potion effects have been greatly strengthened!");
              potiontimer.put(player.getName(), getCurrentTime());
            }
        }   
        
        /*
         * END BREWER
         */
        
        /*
         * MISC COMMANDS HERE
         */
        
        if (commandLabel.equalsIgnoreCase("classes"))
        {        
            Boolean oncooldown = false;
            int cooldown;

            if (classtimer.containsKey(player.getName()))
            {
                cooldown = getCurrentTime() - classtimer.get(player.getName());
                if (cooldown > 30 || cooldown < 0)
                {
                    oncooldown = false;
                }
                else
                {
                    oncooldown = true;
                    player.sendMessage(ChatColor.GOLD + "You can only check your class every 30 seconds.");
                }

            }

            if (oncooldown == true)
            {
                return true;
            }
            
            String owner = "Owner";
            String coowner = "CoOwner";
            String admin = "Admin";
            String jradmin = "JrAdmin";
            String srmod = "SrMod";
            String mod = "Mod";
            String eventmod = "EventMod";
            String chatmod = "ChatMod";
            String jrmod = "jrmod";
            String helper = "Helper";
            String memberplus = "MemberPlus";
            String membervip = "MemberVIP";
            String membervipplus = "MemberVIPPlus";
            String elite = "Elite";
            String hero = "Hero";
            String champion = "Champion";
            String lord = "Lord";
            String lady = "Lady";
            String king = "King";
            String queen = "Queen";
            String emperor = "Emperor";
            String empress = "Empress";
            String pvpvip = "PvPVIP";
            String pvpelite = "PvPElite";
            String pvphero = "PvPHero";
            String pvpchamp = "PvPChamp";
            String pvplord = "PvPLord";
            String pvpking = "PvPKing";
            String pvpgod = "PvPGod";
            String pvpsavage = "PvPSavage";
            String savage = "Savage";
            String savagegod = "SavageGOD";
            String member = "Member";

            classtimer.put(player.getName(), getCurrentTime());
             for (String s : plugin.perms.getPlayerGroups(player))
             {
                 if (!s.equalsIgnoreCase(savage) && !s.equalsIgnoreCase(savagegod) && !s.equalsIgnoreCase(member)
                         && !s.equalsIgnoreCase(emperor) && !s.equalsIgnoreCase(empress) && !s.equalsIgnoreCase(king) && !s.equalsIgnoreCase(queen) 
                         && !s.equalsIgnoreCase(lord) && !s.equalsIgnoreCase(lady) && !s.equalsIgnoreCase(champion) && !s.equalsIgnoreCase(hero) && !s.equalsIgnoreCase(elite) 
                         && !s.equalsIgnoreCase(membervipplus) && !s.equalsIgnoreCase(membervip) && !s.equalsIgnoreCase(memberplus) && !s.equalsIgnoreCase(helper) && !s.equalsIgnoreCase(jrmod) 
                         && !s.equalsIgnoreCase(chatmod) && !s.equalsIgnoreCase(mod) && !s.equalsIgnoreCase(srmod) && !s.equalsIgnoreCase(jradmin) && !s.equalsIgnoreCase(admin) 
                         && !s.equalsIgnoreCase(coowner) && !s.equalsIgnoreCase(owner) && !s.equalsIgnoreCase(pvpvip) && !s.equalsIgnoreCase(pvpelite) && !s.equalsIgnoreCase(pvphero) 
                         && !s.equalsIgnoreCase(pvpchamp) && !s.equalsIgnoreCase(pvplord) && !s.equalsIgnoreCase(pvpking) && !s.equalsIgnoreCase(pvpgod) && !s.equalsIgnoreCase(pvpsavage)
                         && !s.equalsIgnoreCase(eventmod))
                 {
                     player.sendMessage(ChatColor.GOLD + "You are a member of the " + ChatColor.DARK_RED + s + ChatColor.GOLD + " Class");
                 }
             
             }
           
        }
        //
        
        
        if (commandLabel.equalsIgnoreCase("frameclear") &&(player.hasPermission("clear.use")))
        {
            int amtbig = 0;
            World world = player.getWorld();
            
            for(Entity e : world.getEntities())
            {
             
                if ((e instanceof ItemFrame))
                {
                  e.remove();

                  amtbig += 1;
                }
        
            }
            player.sendMessage(ChatColor.AQUA + "You removed " + amtbig + " Item Frames.");
            
        }
        
        if (commandLabel.equalsIgnoreCase("fireclear") &&(player.hasPermission("clear.use")))
        {
            int amtbig = 0;
            World world = player.getWorld();
            
            for(Entity e : world.getEntities())
            {
             
                if ((e instanceof Fireball) || (e instanceof SmallFireball))
                {
                  e.remove();

                  amtbig += 1;
                }
        
            }
            player.sendMessage(ChatColor.AQUA + "You removed " + amtbig + " Fireball Entities.");
            
        }
        
        /*
         * DUELIST HERE
         * 
         */
        
        String name = player.getName();
        
        //Duel
        /*
        if (commandLabel.equalsIgnoreCase("duel"))
        {
            if (! player.hasPermission("sr.duelist.duel"))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be a Duelist to initiate duels.");
                return true;
            }
            
            String delimiter = " ";
            
            
            
            
            if (args.length == 2)
            {
                String pname = args[0];
                
                
                
                boolean exists = false;
                
                for(Player p : getServer().getOnlinePlayers())
                {
                    if (p.getName().equalsIgnoreCase(pname))
                    {
                        exists = true;
                    }
                }
                
                if (!exists)
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "That player does not exist.");
                    return true;
                }
                
                Player target = getServer().getPlayer(pname);
                
                final String dueler = player.getName();
                final String dtarget = target.getName();
                
                
                String betamount = args[1];
                
                if (party.containsKey(player.getName().toLowerCase()))
                {
                    int pnum = party.get(player.getName().toLowerCase());
                    
                    if (party.containsKey(pname.toLowerCase()))
                    {
                        int tnum = party.get(pname.toLowerCase());
                        
                        if (pnum == tnum)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + pname + ChatColor.RED + " is in your party and cannot be dueled.");
                            return true;
                        }
                    }
                }
                
                if (isInteger(betamount))
                {
                    int betamountint = Integer.parseInt(betamount);
                        
                    double pbal = econ.getBalance(player.getName());
                    double tbal = econ.getBalance(target.getName());
                        
                    if (betamountint > pbal)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You do not have enough money to wager this.");
                        return true;
                    }
                    
                    if (betamountint > tbal)
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + target.getName() + " does not have enough money to wager this.");
                        return true;
                    }
                    
                    if (! queryDuel.contains(target.getName()) && ! queryDuel.contains(player.getName()))
                    {

                        int round = Math.round(betamountint);

                        int winnings = betamountint * 2;
                      
                    
                        moneyint = Math.round(winnings);
                    
                        money.put(player.getName(), moneyint);
                        
                        queryDuel.add(player.getName());
                        isDueling.add(player.getName());
                        
                        queryDuel.add(target.getName());
                        
                        duelWaiting.put(target.getName(), player.getName());
                    
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "You have sent " + ChatColor.AQUA + target.getName() + ChatColor.GOLD + " a duel request."
                                + "Wager is " + ChatColor.GREEN + round + " gold.");
                        target.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + player.getName() + ChatColor.GOLD 
                            + " has sent you a duel request. Wager is " + ChatColor.GREEN + round + " gold." + ChatColor.GOLD +" You have 60 seconds to accept with " 
                                + ChatColor.GREEN + "/duelaccept");
                    
                        duelIssued = getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
                        {

                            @Override
                            public void run() 
                            {
                                if (! (isDueling.contains(dtarget)))
                                {
                                    Player p = getServer().getPlayer(dueler);
                                    Player t = getServer().getPlayer(dtarget);
                                    
                                    queryDuel.remove(t.getName());
                                    
                                    queryDuel.remove(p.getName());
                                    isDueling.remove(p.getName());
                                    
                                    money.remove(p.getName());
                                    
                                    
                                    if (duelWaiting.containsKey(t.getName()))
                                    {
                                        t.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your duel request timed out.");
                                        p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your duel request timed out.");
                                    }
                                    
                                }
                            }
                
                        }, 1200L);
                      

                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You or your target are already pending a duel request.");
                        return true;
                    }
                    
                }
                else
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Incorrect format. Must be: /duel targetname betamount (ex: /duel cr0ssVtW 100)");
                    return true;
                }
            }
            else
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Incorrect format. Must be: /duel targetname betamount (ex: /duel cr0ssVtW 100)");
                return true;
            }
            
        } // end duel request
        */
        
        /*
        // Duel Accept
        if (commandLabel.equalsIgnoreCase("duelaccept"))
        {
            if (!(player.hasPermission("sr.duelist.duelaccept")))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You cannot accept a duel in this world.");
                return true;
            }
            
            if (queryDuel.contains(player.getName()))
            {
                String issuerstring = duelWaiting.get(player.getName());
                final Player issuer = getServer().getPlayer(issuerstring);
                
                String accepterstring = player.getName();
                final Player accepter = getServer().getPlayer(accepterstring);
                
                if (money.containsKey(issuer.getName()))
                {
                    int wager = money.get(issuer.getName());

                    if (wager < 0)
                    {
                        wager = 0;
                    }
                }
                int wager = money.get(issuer.getName());
                            
                if (wager < 0)
                {
                    wager = 0;
                }

                double balance = econ.getBalance(accepter.getName());
                double ibalance = econ.getBalance(issuer.getName());

                if (ibalance < wager || balance < wager)
                {
                    issuer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Duelist or Challenged Player no longer has the funds to accept this duel. Ending.");
                    if (money.containsKey(issuer.getName()))
                    {
                        money.remove(issuer.getName());
                    }

                    if (isDueling.contains(issuer.getName()))
                    {
                        isDueling.remove(issuer.getName());
                    }

                    if (isDueling.contains(accepter.getName()))
                    {
                        isDueling.remove(accepter.getName());
                    }

                    if (queryDuel.contains(issuer.getName()))
                    {
                        queryDuel.remove(issuer.getName());
                    }

                    if (queryDuel.contains(accepter.getName()))
                    {
                        queryDuel.remove(accepter.getName());
                    }

                    if (paired.containsKey(issuer.getName()))
                    {
                        paired.remove(issuer.getName());
                    }

                    if (paired.containsKey(accepter.getName()))
                    {
                        paired.remove(accepter.getName());
                    }

                    if (paired2.containsKey(issuer.getName()))
                    {
                        paired2.remove(issuer.getName());
                    }

                    if (paired2.containsKey(accepter.getName()))
                    {
                        paired2.remove(accepter.getName());
                    }

                    if (intArena.containsKey(issuer.getName()))
                    {
                        intArena.remove(issuer.getName());
                    }

                    if (intArena.containsKey(accepter.getName()))
                    {
                        intArena.remove(accepter.getName());
                    }
                    return true;
                }
                
                isDueling.add(player.getName());
                
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You have accepted the duel. Duel begins in 10 seconds.");
                
                
                getServer().getScheduler().cancelTask(duelIssued);
                
                issuer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has accepted the duel. Duel begins in 10 seconds.");
                
                getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        
                        // after 10 seconds, teleport them to their arena.
                        paired.put(accepter.getName(), issuer.getName());
                        paired2.put(issuer.getName(), accepter.getName());
                        

                        
                        int min = 1;
                        int max = arenas.getInt("Arenas");
                        int o = 0;
                        
                        
                        Random rand = new Random();
                        int randomNum = rand.nextInt(max - min + 1) + min;
                        

                        if (inArena.size() == max)
                        {
                            // remove from all hashsets / tell full / end
                            
                            issuer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "All Arenas are full. Please try again later.");
                            accepter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "All Arenas are full. Please try again later.");
                            
                            if (money.containsKey(issuer.getName()))
                            {
                                money.remove(issuer.getName());
                            }
                            
                            if (isDueling.contains(issuer.getName()))
                            {
                                isDueling.remove(issuer.getName());
                            }
                            
                            if (isDueling.contains(accepter.getName()))
                            {
                                isDueling.remove(accepter.getName());
                            }
                            
                            if (queryDuel.contains(issuer.getName()))
                            {
                                queryDuel.remove(issuer.getName());
                            }
                            
                            if (queryDuel.contains(accepter.getName()))
                            {
                                queryDuel.remove(accepter.getName());
                            }
                            
                            if (paired.containsKey(issuer.getName()))
                            {
                                paired.remove(issuer.getName());
                            }
                            
                            if (paired.containsKey(accepter.getName()))
                            {
                                paired.remove(accepter.getName());
                            }
                            
                            if (paired2.containsKey(issuer.getName()))
                            {
                                paired2.remove(issuer.getName());
                            }
                            
                            if (paired2.containsKey(accepter.getName()))
                            {
                                paired2.remove(accepter.getName());
                            }
                            
                            if (intArena.containsKey(issuer.getName()))
                            {
                                intArena.remove(issuer.getName());
                            }
                            
                            if (intArena.containsKey(accepter.getName()))
                            {
                                intArena.remove(accepter.getName());
                            }
                            
                            return;
                        }
                        
                        
                        while (inArena.contains(randomNum))
                        {
                            
                            randomNum = rand.nextInt(max - min + 1) + min;
                        }
                        
                            
                        
                        if (money.containsKey(issuer.getName()))
                        {
                            int wager = money.get(issuer.getName());
                            
                            if (wager < 0)
                            {
                                wager = 0;
                            }
                            
                            double balance = econ.getBalance(accepter.getName());
                            double ibalance = econ.getBalance(issuer.getName());
                            
                            if (ibalance < wager || balance < wager)
                            {
                                return;
                            }
                            
                            int wagersplit = (wager / 2);
                            
                            econ.withdrawPlayer(issuer.getName(), wagersplit);
                            econ.withdrawPlayer(accepter.getName(), wagersplit);
                            
                            issuer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + wagersplit + " has been withdrawn from your account for this duel.");
                            accepter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + wagersplit + " has been withdrawn from your account for this duel.");
                            
                        }

                        
                        arenaplayer = arenas.getString("Arena " + randomNum + " Spawn 1");
                        arenatarget = arenas.getString("Arena " + randomNum + " Spawn 2");
                        

                        inArena.add(randomNum);
                        
                        intArena.put(issuer.getName(), randomNum);
                        
                        splice1 = arenaplayer.split("/");
                        splice2 = arenatarget.split("/");
                        
                        
                        Location ploc = issuer.getLocation();
                        Location tloc = accepter.getLocation();
                        
                        playerloc.put(issuer.getName(), ploc);
                        playerloc.put(accepter.getName(), tloc);
                        
                        try
                        {
                            World world = getServer().getWorld(splice1[0]);
                            World world2 = getServer().getWorld(splice2[0]);
                            ploc = new Location(world,Double.valueOf(splice1[1]),Double.valueOf(splice1[2]),Double.valueOf(splice1[3]));
                            tloc = new Location(world2,Double.valueOf(splice2[1]),Double.valueOf(splice1[2]),Double.valueOf(splice1[3]));
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        
                        accepter.teleport(tloc);
                        issuer.teleport(ploc);
                        
                        accepter.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_RED + "THE DUEL BEGINS!");
                        issuer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_RED + "THE DUEL BEGINS!");
                    }
                }, 200L);
                
            }
            else
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You do not have any pending duels.");
                return true;
            }
        } // end accept
        */
        
        /*
        if (commandLabel.equalsIgnoreCase("darena"))
        {
            if (!(sender.hasPermission("sr.duelist.admin")))
            {
                sender.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You do not have permissions for this.");
                return true;
            }
            
            if (args.length == 1)
            {
                String s = args[0];
                
                if (s.equalsIgnoreCase("new"))
                {
                    if (arenas.getInt("Arenas") == 0)
                    {
                        a = 0;
                    }
                    else
                    {
                        a = arenas.getInt("Arenas");
                    }
                    
                    a++;
                    
                    arenas.set("Arenas", a);
                    
                    try
                    {
                        arenas.save(arenaFile);
                    } catch (IOException ex)
                    {
                        Logger.getLogger(Party.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    sender.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Arena " + ChatColor.YELLOW + a + ChatColor.GREEN + " added!");
                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Incorrect Format. Try: (/darena arenanumber spawnnumber) or (/darena new)");
                    return true;
                }
            }
            
            if (args.length == 2)
            {
                String arenanum = args[0];
                String spawnnum = args[1];
             
              if (isInteger(arenanum))
              {
                if (isInteger(spawnnum))
                {
                    Location ploc = player.getLocation();
                    String split = ploc.getWorld().getName() + "/" + ploc.getX() + "/" + ploc.getY() + "/" + ploc.getZ();
                               
                    arenas.set("Arena " + arenanum + " Spawn " + spawnnum, split);
                
                    sender.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Arena " + ChatColor.YELLOW + arenanum + ChatColor.GREEN + " Spawn " + ChatColor.YELLOW + spawnnum
                        + ChatColor.GREEN + " set!");
                
                    try
                    {
                        arenas.save(arenaFile);
                    } catch (IOException ex)
                    {
                        Logger.getLogger(Party.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                    return true;
                }

              }

            }
            else
            {
                sender.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Incorrect Format. Try: (/darena arenanumber spawnnumber) or (/darena new)");
                return true;
            }
        }
        */
        
        
        
        /*
         * 
         *      END DUELIST
         * 
         */
        
        
        
        
        
        
        
        
        
        
        // Remove Fire or Knockback from weapon
        
        if (commandLabel.equalsIgnoreCase("removeknockback"))
        {
            ItemStack item = player.getItemInHand();

            item.removeEnchantment(Enchantment.ARROW_KNOCKBACK);
            item.removeEnchantment(Enchantment.KNOCKBACK);
            player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.GREEN + "Removed Knockback Enchant from item.");
            return true;
        }
        
        if (commandLabel.equalsIgnoreCase("removefire"))
        {
            ItemStack item = player.getItemInHand();
            if (item.containsEnchantment(Enchantment.ARROW_FIRE))
            {
                item.removeEnchantment(Enchantment.ARROW_FIRE);
                player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.GREEN + "Removed Fire Enchant from item.");
            }
            else
            {
                player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.RED + "This item doesn't have a Fire Enchantment.");
                return true;
            }
            
            if (item.containsEnchantment(Enchantment.FIRE_ASPECT))
            {
                item.removeEnchantment(Enchantment.FIRE_ASPECT);
                player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.GREEN + "Removed Fire Enchant from item.");
            }
            else
            {
                player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.RED + "This item doesn't have a Fire Enchantment.");
                return true;
            }
        }
        
        
        // Ghost Stuff
        
        if ((commandLabel.equalsIgnoreCase("ghost")) && (player.hasPermission("ghost.vanish")))
        {
            onStealth(player);
            ghost.add(name);
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "Your Ghostly powers let you disappear and you begin to float...");
            
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        if ((commandLabel.equalsIgnoreCase("unghost")) && (player.hasPermission("ghost.unvanish")))
        {
            if (player.getWorld() == Bukkit.getWorld("pvp") || player.getWorld() == Bukkit.getWorld("events") || player.getWorld() == Bukkit.getWorld("rpg"))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Ghostly powers cannot be shed in this world.");
                return true;
            }
            
            unStealth(player);
            ghost.remove(name);
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "Your Ghostly powers fade and you reappear.");
            
            player.setAllowFlight(false);
            player.setFlying(false);
    
        }
        
        
        
        // REDO PARTY SYSTEM
        
        // PARTY SHIT:


        
        if (commandLabel.equalsIgnoreCase("pcspy"))
        {
            if (player.hasPermission("srparty.chat.spy"))
            {
                if (pcSpy.contains(player.getName()))
                {
                    pcSpy.remove(player.getName());
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No longer spying on party chat.");
                }
                else
                {
                    pcSpy.add(player.getName());
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Now spying on party chat.");
                }
            }
            else
            {
                return true;
            }
        }
        // Party chat
        
        if (commandLabel.equalsIgnoreCase("pc") && (player.hasPermission("srparty.chat")))
        {
            
          if (party.containsKey(player.getName().toLowerCase()))
          {
            int chatter = party.get(player.getName().toLowerCase());

            String[] argchat = commandLabel.split(" ");
            
            
            int stringsize = args.length;
            
            String chat;
            
            if (!(isLeader.contains(player.getName())))
            {
                chat = ("" + ChatColor.GRAY + "(" + ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + ") ") + ChatColor.AQUA + "";
            }
            else
            {
                chat = ("" + ChatColor.GRAY + "(" + ChatColor.YELLOW + player.getDisplayName() + ChatColor.GRAY + ") ") + ChatColor.AQUA + "";
            }
            
                    
                    
            for (int i = 0; i < stringsize; i++)
            {
                
                String tempchat = args[i] + " ";
                
                chat = chat + tempchat;

            }
            
            
           for (Player partyplayer : getServer().getOnlinePlayers())
           {
               int partyplayers = 0;
               if (party.containsKey(partyplayer.getName().toLowerCase()))
               {
                   partyplayers = party.get(partyplayer.getName().toLowerCase());
               } 
               
               if (partyplayers == chatter || pcSpy.contains(partyplayer.getName()))
               {
                   if (pcSpy.contains(partyplayer.getName()))
                   {
                       partyplayer.sendMessage(ChatColor.GOLD + "["+ChatColor.DARK_GRAY+"SR"+ChatColor.GOLD+"Party] " + ChatColor.AQUA + "(PN: " + chatter + ") "  + chat);
                   }
                   else
                   {
                       partyplayer.sendMessage(ChatColor.GOLD + "["+ChatColor.DARK_GRAY+"SR"+ChatColor.GOLD+"Party] " + ChatColor.AQUA + chat);
                   }
               }
           }
         }
         else
         {
             player.sendMessage(ChatColor.RED + "You are not in a party.");
             return true;
         }
        }
        
       // Create 
        
        
       if ((commandLabel.equalsIgnoreCase("partycreate") || (commandLabel.equalsIgnoreCase("pcreate"))) && (player.hasPermission("srparty.create")))
       {
           
           if (!(party.containsKey(player.getName().toLowerCase())))
           {
            
               player.sendMessage(ChatColor.GOLD + "You've created a party and are now the leader. Invite players with /pinvite playername !");
               int hashsize = partynumber.size() + 1;
            
			   party.put(player.getName().toLowerCase(), hashsize);
               
               partynumber.add(hashsize);
               partysize.put(hashsize, 1);
               isLeader.add(player.getName());
               
               
           }
           else
           {
               player.sendMessage(ChatColor.RED + "You are already in a party.");
               return true;
               
               
           }

          
            
            
             
             
       }
               
       


        
      // Invite
       
      if ((commandLabel.equalsIgnoreCase("partyinvite") || (commandLabel.equalsIgnoreCase("pinvite"))) && (player.hasPermission("srparty.invite")))
      {
           
            
            // String[] invitedplayer = commandLabel.split(" ");
            
            String delimiter = " ";
        if (party.containsKey(player.getName().toLowerCase()))
        {
            
        
          if (args.length > 0)
          {
              
          
            String invitedplayer = args[0].toLowerCase();
            String invitedplayer2 = args[0];
            
            int pcheck = party.get(player.getName().toLowerCase());
            int psize = partysize.get(pcheck) + 1;
            
            if (psize > MaxPartySize)
            {
                player.sendMessage(ChatColor.RED + "Your party is full or has invites pending. To cancel an invite, type: /partycancel PlayerNameYouInvited ");
                return true;
            }
            
            int partynumber2 = party.get(player.getName().toLowerCase());
            OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(invitedplayer);
			
            if (party.containsKey(invitedplayer))
            {
                player.sendMessage(ChatColor.RED + "That player is already in a party.");
                return true;
            }
            
            if (temphashmap.containsKey(invitedplayer))
            {
                player.sendMessage(ChatColor.RED + "That player already has a pending party invite.");
                return true;
            }
            
            if (offlinePlayer.isOnline())
            {
                Player onlinePlayer = getServer().getPlayer(invitedplayer);
                
                onlinePlayer.sendMessage(ChatColor.GOLD + "" + player.getDisplayName() + ChatColor.AQUA + " has sent you a party invite.");
                onlinePlayer.sendMessage(ChatColor.AQUA + "Type " + ChatColor.GOLD + "/partyaccept " + ChatColor.AQUA + "or " + ChatColor.GOLD + "/partydeny " + ChatColor.AQUA + "to accept or deny this invite.");
            }
            else
            {
                player.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }
            
            
            int currenttime = getCurrentTime();
            
            temphashmap.put(invitedplayer, partynumber2);
            
            invitedplayertime.put(invitedplayer, currenttime);
            
            partysize.put(pcheck, psize);
            
            player.sendMessage(ChatColor.GOLD + "You have invited " + invitedplayer2 + " to your party." + " PN:" + partynumber2);
            // player.sendMessage(ChatColor.GOLD + "You have invited " + player.getDisplayName() + " to your party.");
            
            
            
            
            
          
             
             
         }
          else
          {
              player.sendMessage(ChatColor.RED + "Try using " + ChatColor.GOLD + "/partyinvite playername");
              return true;
          }
       }
        else
        {
            player.sendMessage(ChatColor.RED + "You must be in a party to invite other players.");
            return true; 
        }
      }
    
      
     
       
      
      // Accept
      
       if ((commandLabel.equalsIgnoreCase("partyaccept") || (commandLabel.equalsIgnoreCase("paccept"))) && (player.hasPermission("srparty.accept")))
       {

            

            if (temphashmap.containsKey(player.getName().toLowerCase()))
            {
                
                int currenttime = getCurrentTime();
                
                int timeinvited = invitedplayertime.get(player.getName().toLowerCase());
                
                int timedifference = (currenttime - timeinvited);
                
                
                if (timedifference < 60)
                {
                    int partynumber2 = temphashmap.get(player.getName().toLowerCase());
                    for (Player player2 : this.getServer().getOnlinePlayers())
                    {
                        if (player2 != player)
                        {
                            if (party.containsKey(player2.getName().toLowerCase()))
                            {
                              int temppartynumber = party.get(player2.getName().toLowerCase());

                              if (temppartynumber == partynumber2)
                              {
                                player2.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has joined the party.");
                              }
                            }
                        }
                    }
                     
                    temphashmap.remove(player.getName().toLowerCase());
                    invitedplayertime.remove(player.getName().toLowerCase());
                    
                    party.put(player.getName().toLowerCase(), partynumber2);
                    
                    player.sendMessage(ChatColor.GOLD + "You have joined the party.");
                    
                }
                else
                {
                    int partynumber2 = temphashmap.get(player.getName().toLowerCase());
                    int psize = partysize.get(partynumber2) - 1;
                    partysize.put(partynumber2, psize);
					
                    temphashmap.remove(player.getName().toLowerCase());
                    invitedplayertime.remove(player.getName().toLowerCase());
                    
                    
                    player.sendMessage(ChatColor.RED + "Your party invite has expired." + " " + psize + " PN: " + partynumber2);
                    return true;
                }
                
            }
            
            else
            {
                
                
                player.sendMessage(ChatColor.RED + "You have not been invited to a party.");
                return true;
            
            }

             
        }
    
     
       
       // Deny
       
      if ((commandLabel.equalsIgnoreCase("partydeny") || (commandLabel.equalsIgnoreCase("pdeny"))) && (player.hasPermission("srparty.deny")))
      {
             
            if (temphashmap.containsKey(player.getName().toLowerCase()))
            {
             
                int currenttime = getCurrentTime();
                               
                int timeinvited = invitedplayertime.get(player.getName().toLowerCase());
                
                int timedifference = (currenttime - timeinvited);
                
                
                if (timedifference < 60)
                {
                    
                    int partynumber = temphashmap.get(player.getName().toLowerCase());
					
		    int psize = partysize.get(partynumber) - 1;
					
                    partysize.put(partynumber, psize);
					
                    
                    temphashmap.remove(player.getName().toLowerCase());
                    invitedplayertime.remove(player.getName().toLowerCase());
                    
                    player.sendMessage(ChatColor.GREEN + "You have declined the party invite." + " " + psize + "  PN: " + partynumber);
                    
                    
                    
                }
                else
                {
                    temphashmap.remove(player.getName().toLowerCase());
                    invitedplayertime.remove(player.getName().toLowerCase());
                    
                    
                    player.sendMessage(ChatColor.RED + "Your party invite has expired.");
                    return true;
                }
                
                
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You have not been invited to a party.");
                return true;
                
            }
             
             
        }

      
      
      
      // Leave
      
     if ((commandLabel.equalsIgnoreCase("partyleave") || (commandLabel.equalsIgnoreCase("pleave"))) && (player.hasPermission("srparty.leave")))
      {
             
            if (party.containsKey(player.getName().toLowerCase()))
            {
              int partynumber2 = party.get(player.getName().toLowerCase());
              
              for (Player player2 : this.getServer().getOnlinePlayers())
              {
                     
                     
                if (player2 != player)
                {
                    if (party.containsKey(player2.getName().toLowerCase()))
                    {
                      int temppartynumber = party.get(player2.getName().toLowerCase());
                    
                      if (temppartynumber == partynumber2)
                      {
                          player2.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has left the party.");
                      }
                       
                       
                    }
                }
              }
                
                
                
                int psize = partysize.get(partynumber2);
                partysize.put(partynumber2, psize - 1);
                
                if (isLeader.contains(player.getName()))
                {
                    isLeader.remove(player.getName());
                }
                
                party.remove(player.getName().toLowerCase());
                player.sendMessage(ChatColor.GOLD + "You have left the party.");
               
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You are not in a party.");
                return true;
            }
             
             
             
        } // end leave
     
        if ((commandLabel.equalsIgnoreCase("pkick") || commandLabel.equalsIgnoreCase("partykick")) && (player.hasPermission("srparty.create")))
        {
            if (party.containsKey(player.getName().toLowerCase()))
            {
                if (isLeader.contains(player.getName()))
                {
                    if (args.length > 0)
                    {
                        String kickedplayer = args[0].toLowerCase();
                        String kickedplayer2 = args[0];
                        
                        if (party.containsKey(kickedplayer))
                        {
                            if (player.getName().equals(kickedplayer2))
                            {
                                player.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.RED + "You cannot kick yourself.");
                                return true;
                            }
                            
                            Player newPlayer = Bukkit.getPlayer(kickedplayer2);
                            
                            int newpnum = party.get(newPlayer.getName().toLowerCase());
                            int pnum = party.get(player.getName().toLowerCase()); 
                            
                            if (pnum == newpnum)
                            {
                                party.remove(newPlayer.getName().toLowerCase());
                                
                                for (Player partyplayer : Bukkit.getOnlinePlayers())
                                {
                                    if (party.containsKey(partyplayer.getName().toLowerCase()))
                                    {
                                        int pnum2 = party.get(partyplayer.getName().toLowerCase());
                                    
                                        if (pnum == pnum2)
                                        {
                                            partyplayer.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.GOLD 
                                                + newPlayer.getName() + ChatColor.RED + " has been kicked from the party.");
                                        }
                                    }
                                }
                                
                                newPlayer.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.RED + "You have been removed from " 
                                        + ChatColor.GOLD + player.getName() + ChatColor.RED + "'s party.");
                                
                                int psize = partysize.get(pnum);
                                
                                partysize.put(pnum, psize - 1);
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "That player doesn't exist or is not in your party.");
                                return true;
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "That player doesn't exist or is not in your party.");
                            return true;
                        }

                    }
                }
                else
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are not the party leader.");
                    return true;
                }
            }
            else
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are not in a party.");
                return true;
            }
        } // end kick
        
        if (commandLabel.equalsIgnoreCase("partycancel") || commandLabel.equalsIgnoreCase("pcancel"))
        {
            if (player.hasPermission("srparty.invite"))
            {
                if (party.containsKey(player.getName().toLowerCase()))
                {
                    if (args.length > 0)
                    {
                        String kickedplayer = args[0].toLowerCase();
                        String kickedplayer2 = args[0];
                        
                        if (temphashmap.containsKey(kickedplayer))
                        {
                            Player newPlayer = Bukkit.getPlayer(kickedplayer2);
                            
                            int pnum = temphashmap.get(newPlayer.getName().toLowerCase());
                            int psize = partysize.get(pnum);
                            
                            partysize.put(pnum, psize - 1);
                            
                            temphashmap.remove(newPlayer.getName().toLowerCase());
                            invitedplayertime.remove(newPlayer.getName().toLowerCase());
                            
                            for (Player partyplayer : Bukkit.getOnlinePlayers())
                            {
                                if (party.containsKey(partyplayer.getName().toLowerCase()))
                                {
                                    int pnum2 = party.get(partyplayer.getName().toLowerCase());
                                    
                                    if (pnum == pnum2)
                                    {
                                        partyplayer.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.GOLD 
                                                + newPlayer.getName() + "'s " + ChatColor.RED + "invite has been cancelled.");
                                    }
                                }
                            }
                            
                            newPlayer.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.GOLD + player.getName() 
                                    + ChatColor.RED + " has cancelled your invite.");
                        }
                    }
                }
            }
            
        }
     
    
        
        return true;
        
    }
    
   
 
    
    
     public int getCurrentTime()
     {
		Calendar calendar = new GregorianCalendar();

		int hour = calendar.get(Calendar.HOUR) * 3600;
		int minute = calendar.get(Calendar.MINUTE) * 60;
		int second = calendar.get(Calendar.SECOND);

		return hour + minute + second;
     }
    
    
    public static boolean isInteger(String s) 
    {
        try
        {
            Integer.parseInt(s);
        } catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }
    
    public static boolean isShort(String s) 
    {
        try
        {
            Short.parseShort(s);
        } catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }
    
}
