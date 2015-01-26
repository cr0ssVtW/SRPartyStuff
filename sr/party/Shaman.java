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
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Shaman implements Listener
{
    public Party plugin;
    
    public Shaman (Party plugin)
    {
        this.plugin = plugin;
    }
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();

    public double dmgReduction = 0.65;
    
    public int CurseCD = 13;
    public int CurseDur = 120;
    
    public int CoWCD = 30;
    public int CoWDur = 300;
    public double CallRange = 32.0;
    
    int taskId;
    
    private Material totem = Material.DEAD_BUSH;
    private int interval = 100; //in ticks
    private int healICD = interval / 20;
    private int healAmount = 9;
    private int totalPulses = 9;
    public double TotemRange = 32.0;
    
    public int breakCD = 60;
    
    public HashMap<String, Integer> HealICD = new HashMap<String, Integer>();
    public HashMap<String, Integer> totemBreakCD = new HashMap<String, Integer>();
    public HashMap<String, Integer> totemHealTimer = new HashMap<String, Integer>();
    
    public HashMap<String, Integer> shamanspell = new HashMap<String, Integer>();
    
    public HashMap<String, Integer> tickedtotemtimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> healingtotemtimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> cowtimer = new HashMap<String, Integer>();
    public HashMap<String, Integer> cursetimer = new HashMap<String, Integer>();
    
    
    
    public HashMap<String, Integer> totemID = new HashMap<String, Integer>();
    
    public HashMap<String, Block> totemBlocks = new HashMap<String, Block>();
    public HashMap<Block, Chunk> chunkTotem = new HashMap<Block, Chunk>();
    
    /*
    @EventHandler
    public void onPlayerMove (PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if (totemBlocks.containsKey(player.getName().toLowerCase()))
        {
            Location ploc = player.getLocation();
            Block block = totemBlocks.get(player.getName().toLowerCase());
        
            Location bloc = block.getLocation();
            
            if (ploc.distance(bloc) > 50)
            {
                block.setType(Material.AIR);
                totemBlocks.remove(player.getName().toLowerCase());
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You moved too far from your totem. It has been removed.");
            }
            
            
            if (healingtotemtimer.containsKey(player.getName().toLowerCase()))
            {
                int currenttime = plugin.getCurrentTime();
                int healingtime = healingtotemtimer.get(player.getName().toLowerCase());
                int ticktime = tickedtotemtimer.get(player.getName().toLowerCase());
                if ((currenttime - healingtime) > 60 || (currenttime - healingtime) < 0)
                {
                    block.setType(Material.AIR);
                    totemBlocks.remove(player.getName().toLowerCase());
                    healingtotemtimer.remove(player.getName().toLowerCase());
                    tickedtotemtimer.remove(player.getName().toLowerCase());
                    
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your totem has been removed.");
                }
                
                if ((currenttime - ticktime) > 5)
                {
                  tickedtotemtimer.remove(player.getName().toLowerCase());
                  
                  tickedtotemtimer.put(player.getName().toLowerCase(), currenttime);
                  
                  if (plugin.party.containsKey(player.getName().toLowerCase()))
                  {
                    int partymembers = plugin.party.get(player.getName().toLowerCase());
                    
                    final Block totemblock = totemBlocks.get(player.getName().toLowerCase());
                    Location totemloc = totemblock.getLocation();
                                        
                    World totemworld = totemloc.getWorld();
                    
                    if (! totemBlocks.containsKey(player.getName().toLowerCase())) return;
                    
                    for (final Player partyplayer : plugin.getServer().getOnlinePlayers())
                    {
                        if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                        {
                             int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());
                             if (partyplayers == partymembers && partyplayer != player)
                             {
                                  Location partyloc = partyplayer.getLocation();
                                  if (partyplayer.getWorld().equals(totemworld))
                                  {
                                                
                                     if (totemloc.distance(partyloc) < 25.0)
                                     {

                                         partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s" + ChatColor.BLUE + " Healing Totem renews you.");
                                                                    
                                           int pph = partyplayer.getHealth();
                                           int ppheal = pph + healAmount;
                                                                    
                                           if (ppheal >= 20)
                                           {
                                                partyplayer.setHealth(20);
                                           }
                                           else
                                           {                    
                                               partyplayer.setHealth(ppheal);
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

    }
    */
    
    @EventHandler
    public void onChunkUnload (ChunkUnloadEvent event)
    {
        Chunk chunk = event.getChunk();
        
        if (chunkTotem.size() > 0)
        {
            if (chunkTotem.containsValue(chunk))
            {
                for (Block b : chunkTotem.keySet())
                {
                    if (b.getType() == totem)
                    {
                        b.setType(Material.AIR);
                    }
                }
            }
            
        }
    }
    
    @EventHandler
    public void onWorldChange (PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        
        if (totemBlocks.containsKey(player.getName().toLowerCase()))
        {    
            Block block = totemBlocks.get(player.getName().toLowerCase());
        
            block.setType(Material.AIR);
            totemBlocks.remove(player.getName().toLowerCase());
            
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You changed worlds causing your totem to be removed.");
        }
            if (totemID.containsKey(player.getName()))
            {
                int i = totemID.get(player.getName());
                
                Bukkit.getServer().getScheduler().cancelTask(i);
                
                totemID.remove(player.getName());
            }
    }
    
    @EventHandler
    public void onPlayerQuit (PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        
        if (totemBlocks.containsKey(player.getName().toLowerCase()))
        {
            Block block = totemBlocks.get(player.getName().toLowerCase());

            block.setType(Material.AIR);
            totemBlocks.remove(player.getName().toLowerCase());
            
        }
        
            if (totemID.containsKey(player.getName()))
            {
                int i = totemID.get(player.getName());
                
                Bukkit.getServer().getScheduler().cancelTask(i);
                
                totemID.remove(player.getName());
            }
        
    }
    
    
    @EventHandler
    public void onPlayerDeath (PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        
        if (totemBlocks.containsKey(player.getName().toLowerCase()))
        {
            Block block = totemBlocks.get(player.getName().toLowerCase());
            block.setType(Material.AIR);
            totemBlocks.remove(player.getName().toLowerCase());
            
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have died. This kills the totem.");
            
            if (totemID.containsKey(player.getName()))
            {
                int i = totemID.get(player.getName());
                
                Bukkit.getServer().getScheduler().cancelTask(i);
                
                totemID.remove(player.getName());
            }
        }
    }
    
    @EventHandler
    public void onDamage (EntityDamageByEntityEvent event)
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
            
            if (damager.hasPermission("shaman.use"))
            {
                double dmg = event.getDamage();
                double nerfdmg = dmg * dmgReduction;
                
                event.setDamage(nerfdmg);
            }
        }
        
        if (event.getDamager() instanceof Projectile)
        {
            Projectile proj = (Projectile) event.getDamager();
            
            if (proj.getShooter() instanceof Player)
            {
                Player damager = (Player) proj.getShooter();
                
                if (damager.hasPermission("shaman.use"))
                {
                    double dmg = event.getDamage();
                    double nerfdmg = dmg * dmgReduction;

                    event.setDamage(nerfdmg);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract (PlayerInteractEvent event)
    {
        int healingtotem = 1;
        int callofwild = 2;
        int curse = 3;
               
        
        int currenttime = plugin.getCurrentTime();
        
        final Player player = event.getPlayer();
        
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


        if (player.hasPermission("shaman.use"))
        {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                if (player.getInventory().getItemInHand() != null)
                {
                    if (player.getInventory().getItemInHand().getType() == Material.WOOD_AXE || player.getInventory().getItemInHand().getType() == Material.STONE_AXE || 
                            player.getInventory().getItemInHand().getType() == Material.GOLD_AXE || player.getInventory().getItemInHand().getType() == Material.IRON_AXE || 
                            player.getInventory().getItemInHand().getType() == Material.DIAMOND_AXE)
                    {
                        // do check for Berserker rage and break totem/cancel task.
                        if (PL.inRage.contains(player.getName()))
                        {
                            boolean canBreak = false;
                            int currentime = plugin.getCurrentTime();
                            int cdtime = 0;
                            if (totemBreakCD.containsKey(player.getName()))
                            {
                                int check = totemBreakCD.get(player.getName());
                                int totaltime = currenttime - check;
                                
                                if (totaltime > breakCD || totaltime < 0)
                                {
                                    canBreak = true;
                                    totemBreakCD.remove(player.getName());
                                }
                                else
                                {
                                    canBreak = false;
                                    cdtime = (breakCD - totaltime);
                                }
                            }
                            
                            if (canBreak)
                            {
                                Block b = event.getClickedBlock();
                            
                                if (totemBlocks.containsValue(b))
                                {
                                    for (String s : totemBlocks.keySet())
                                    {
                                        Block b2 = totemBlocks.get(s);

                                        if (b2.equals(b))
                                        {
                                            String name = s;
                                            totemBlocks.remove(name);
                                            b.setType(Material.AIR);
                                            if (totemID.containsKey(name))
                                            {
                                                int ID = totemID.get(name);
                                                Bukkit.getScheduler().cancelTask(ID);
                                                totemID.remove(name);

                                                totemBreakCD.put(player.getName(), currentime);
                                                
                                                OfflinePlayer offlinecheck = Bukkit.getPlayer(name);
                                                if (offlinecheck.isOnline())
                                                {
                                                    Player shaman = (Player) offlinecheck;
                                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You have DESTROYED " + ChatColor.GOLD + shaman.getName() + "'s " + ChatColor.GREEN + "Healing Totem.");
                                                    shaman.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + ChatColor.RED + " has DESTROYED your Healing Totem.");

                                                }
                                                else
                                                {
                                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You have DESTROYED " + ChatColor.GOLD + name + "'s " + ChatColor.GREEN + "Healing Totem.");
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You've broken a totem too recently. Wait " + cdtime + " more seconds.");
                                event.setCancelled(true);
                                return;
                            }
                            
                        }
                    }
                    
                    if (player.getInventory().getItemInHand().getTypeId() == 352 || player.getInventory().getItemInHand().getTypeId() == 372) // bone or nether wart
                    {
                        int shamspell;
                        
                        if (shamanspell.containsKey(player.getName().toLowerCase()))
                        {
                            shamspell = shamanspell.get(player.getName().toLowerCase()) + 1;
                            
                            if (shamspell > curse)
                            {
                                shamspell = 1;
                            }
                        }
                        else
                        {
                            shamspell = 1;
                        }
                        
                        if (shamspell == healingtotem)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You ready your " + ChatColor.GOLD + "Healing Totem " + ChatColor.AQUA + "spell.");
                        }
                        
                        if (shamspell == callofwild)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You ready your " + ChatColor.GOLD + "Call of the Wild " + ChatColor.AQUA + "spell.");
                        }
                        
                        if (shamspell == curse)
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You ready your " + ChatColor.GOLD + "Curse " + ChatColor.AQUA + "spell.");
                        }
                        
                        shamanspell.put(player.getName().toLowerCase(), shamspell);
                        
                        event.setCancelled(true);
                        
                        
                    }
                }
            } // End Right Click
            
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                if (player.getInventory().getItemInHand().getTypeId() == 352 || player.getInventory().getItemInHand().getTypeId() == 372) // bone or nether wart
                {
                    if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null && player.getInventory().getLeggings() != null
                            && player.getInventory().getBoots() != null)
                    {
                        ItemStack helm = player.getInventory().getHelmet();
                        ItemStack chest = player.getInventory().getChestplate();
                        ItemStack legs = player.getInventory().getLeggings();
                        ItemStack boots = player.getInventory().getBoots();
                        
                        
                        if (helm.getTypeId() == 298 && chest.getTypeId() == 299 && legs.getTypeId() == 300 && boots.getTypeId() == 301) // if leather
                        {
                            int spellcheck;
                            
                            if (shamanspell.containsKey(player.getName().toLowerCase()))
                            {
                                spellcheck = shamanspell.get(player.getName().toLowerCase());
                            }
                            else
                            {
                                spellcheck = 1;
                            }
                            
                            if (spellcheck == 1) //healing totem
                            {
                             if (player.hasPermission("shaman.use.totem"))
                             {
                                if (player.getLocation().getBlock().getType() != Material.AIR)
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Failed. You must place the totem on open ground.");
                                    return;
                                }
                                
                                boolean healingtotemcheck;
                                
                                if (healingtotemtimer.containsKey(player.getName().toLowerCase()))
                                {
                                    int healold = healingtotemtimer.get(player.getName().toLowerCase());
                                    
                                    int healtotal = (currenttime - healold);
                                    
                                    if (healtotal > 60 || healtotal < 0) // 60 second cooldown
                                    {
                                        healingtotemcheck = false;
                                    }
                                    else
                                    {
                                        healingtotemcheck = true;
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.DARK_AQUA + (60 - healtotal) + ChatColor.RED + " seconds " 
                                                + ChatColor.RED + "to cast " + ChatColor.GOLD + "Healing Totem " + ChatColor.RED + "again.");
                                    }
                                }
                                else
                                {
                                    healingtotemcheck = false;
                                }
                                
                                if (healingtotemcheck == false)
                                {
                                    Location ploc = player.getLocation();
                                    Block block = ploc.getBlock();
                                    
                                    //Location newLoc = new Location(player.getWorld(),oldblock.getX(),oldblock.getY() + 1, oldblock.getZ());
                                    //Block block = newLoc.getBlock();
                                    
                                    if (totemBlocks.containsKey(player.getLocation()))
                                    {
                                        Block blockreplace = totemBlocks.get(player.getName().toLowerCase()); // get old block spot
                                        
                                        blockreplace.setType(Material.AIR); // make sure its set to air
                                        
                                        
                                        block.setType(totem);
                                        totemBlocks.put(player.getName().toLowerCase(), block); // replace old spot in hash with new spot
                                        Chunk chunk = player.getWorld().getChunkAt(block);
                                        chunkTotem.put(block, chunk);
                                        
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You place your " + ChatColor.GOLD + "Healing Totem" + ChatColor.BLUE + " on the ground.");
                                        
                                        
                                    }
                                    else
                                    {
                                        totemBlocks.put(player.getName().toLowerCase(), block);
                                        Chunk chunk = player.getWorld().getChunkAt(block);
                                        chunkTotem.put(block, chunk);
                                        
                                        block.setType(totem);
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You place your " + ChatColor.GOLD + "Healing Totem" + ChatColor.BLUE + " on the ground.");
                                    }
                                    
                                    new Totem(player);
                                    
                                    healingtotemtimer.put(player.getName().toLowerCase(), currenttime);
                                    
                                }
                             } // end totem permission
                             else
                             {
                                 player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You do not have permission to place a totem here.");
                             }
                             
                             
                            } // end spell 1 - healing totem
                            
                            if (spellcheck == 2) // call of the wild
                            {
                             if (player.hasPermission("shaman.use.callofthewild"))
                             {
                                boolean cowcheck;
                                
                                if (cowtimer.containsKey(player.getName().toLowerCase()))
                                {
                                    int cowold = cowtimer.get(player.getName().toLowerCase());
                                    
                                    int cowtotal = (currenttime - cowold);
                                    
                                    if (cowtotal > CoWCD || cowtotal < 0) 
                                    {
                                        cowcheck = false;
                                    }
                                    else
                                    {
                                        cowcheck = true;
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.DARK_AQUA + (CoWCD - cowtotal) + ChatColor.RED + " seconds " 
                                                + ChatColor.RED + "to cast " + ChatColor.GOLD + "Call of the Wild " + ChatColor.RED + "again.");
                                    }
                                }
                                else
                                {
                                    cowcheck = false;
                                }
                                
                                if (cowcheck == false)
                                {
                                    cowtimer.put(player.getName().toLowerCase(), currenttime);
                                    
                                    Location ploc = player.getLocation();
                                    
                                    if (player.hasPotionEffect(PotionEffectType.SPEED))
                                    {
                                        player.removePotionEffect(PotionEffectType.SPEED);
                                    }
                                    
                                    if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE));
                                    {
                                        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                    }
                                    
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, CoWDur, 1));
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, CoWDur, 0));
                                    
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You cast " + ChatColor.GOLD + "Call of the Wild " + ChatColor.BLUE + "increasing speed and damage.");
                                    
                                    if (plugin.party.containsKey(player.getName().toLowerCase()))
                                    {
                                        int partymembers = plugin.party.get(player.getName().toLowerCase());
                                        
                                        Location shamanloc = player.getLocation();
                                        World shamanworld = player.getWorld();
                                        
                                        for (Player partyplayer : plugin.getServer().getOnlinePlayers())
                                        {
                                            if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                                            {
                                                int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());
                                                if (partyplayers == partymembers && partyplayer != player)
                                                {
                                                    Location partyloc = partyplayer.getLocation();
                                                    if (partyplayer.getWorld().equals(shamanworld))
                                                    {
                                                        if (shamanloc.distance(partyloc) < CallRange)
                                                        {
                                                            if (partyplayer.hasPotionEffect(PotionEffectType.SPEED))
                                                            {
                                                                partyplayer.removePotionEffect(PotionEffectType.SPEED);
                                                            }
                                    
                                                            if (partyplayer.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE));
                                                            {
                                                                partyplayer.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                                            }
                                                            
                                                            partyplayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, CoWDur, 1));
                                                            partyplayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, CoWDur, 0));
                                                            
                                                            partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You have been buffed by " + ChatColor.GOLD + "Call of the Wild.");
                                                        }
                                                        else
                                                        {
                                                            partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You were too far away to receive " + ChatColor.GOLD + "Call of the Wild.");
                                                        }
                                                    }
                                                    else
                                                    {
                                                        partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Due to being in another realm, you didn't receive " + ChatColor.GOLD + "Call of the Wild.");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    if (plugin.maHandler != null && plugin.maHandler.isPlayerInArena(player))
                                    {
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
                                                        if (p.getLocation().distance(player.getLocation()) < CallRange)
                                                        {
                                                            if (p != player)
                                                            {
                                                                if (p.hasPotionEffect(PotionEffectType.SPEED))
                                                                {
                                                                    p.removePotionEffect(PotionEffectType.SPEED);
                                                                }

                                                                if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE));
                                                                {
                                                                    p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                                                }

                                                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1));
                                                                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0));
                                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You have been buffed by " + ChatColor.GOLD + "Call of the Wild.");
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
                                    
                                }
                             }
                            } // end spellcheck 2 - call of the wild
                            
                            
                            if (spellcheck == 3) // curse
                            {
                             if (player.hasPermission("shaman.use.curse"))
                             {
                                
                                
                                boolean cursecheck;
                                
                                if (cursetimer.containsKey(player.getName().toLowerCase()))
                                {
                                    int curseold = cursetimer.get(player.getName().toLowerCase());
                                    
                                    int cursetotal = (currenttime - curseold);
                                    
                                    if (cursetotal > CurseCD || cursetotal < 0) // cooldown
                                    {
                                        cursecheck = false;
                                    }
                                    else
                                    {
                                        cursecheck = true;
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wait " + ChatColor.DARK_AQUA + (CurseCD - cursetotal) + ChatColor.RED + " seconds " 
                                                + ChatColor.RED + "to cast " + ChatColor.GOLD + "Curse " + ChatColor.RED + "again.");
                                    }
                                }
                                else
                                {
                                    cursecheck = false;
                                }
                                
                                if (cursecheck == false)
                                {
                                    cursetimer.put(player.getName().toLowerCase(), currenttime);
                                    
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You " + ChatColor.GOLD + "Curse " + ChatColor.BLUE + "the targetted area.");
                                    
                                    Block targetBlock = player.getTargetBlock(null, 100);
                                    
                                    
                                    Location blockLoc = targetBlock.getLocation();
                                    
                                    Location ploc = player.getLocation();

                                    int targetparty = -1;
                                    
                                    int myparty = -2;
                                    
                                    if (plugin.party.containsKey(player.getName().toLowerCase()))
                                    {
                                        myparty = plugin.party.get(player.getName().toLowerCase());
                                    }
                                    
                                    if (ploc.distance(blockLoc) < 10.0)
                                    {
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "Cursed yourself!");
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, CurseDur, 1));
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, CurseDur, 1));
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,CurseDur,1));
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,CurseDur,0));
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,CurseDur,0));
                                    }
                                    
                                    for (LivingEntity e : targetBlock.getWorld().getLivingEntities())
                                    {
                                        if (e instanceof Player)
                                        {
                                            Player p = (Player) e;
                                            World pworld = p.getWorld();
                                            if (player.getWorld().equals(pworld) && (p != player))
                                            {
                                              Location targetloc = p.getLocation();
                                              if (targetloc.distance(blockLoc) < 10.0)
                                              {
                                                World plworld = player.getWorld();

                                                BlockVector pt = BukkitUtil.toVector(player.getLocation().getBlock());

                                                RegionManager regionManager = worldGuard.getRegionManager(plworld);
                                                ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                                                if (!(set.allows(DefaultFlag.PVP)))
                                                {
                                                    return;
                                                }

                                                if (plugin.party.containsKey(p.getName().toLowerCase()))
                                                {
                                                    targetparty = plugin.party.get(p.getName().toLowerCase());
                                                }

                                                if (myparty != targetparty)
                                                {
                                                    p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been " + ChatColor.GOLD + "Cursed " + ChatColor.RED + "by " + ChatColor.GOLD + player.getName() + ".");
                                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "Cursed " + ChatColor.GOLD + p.getName() + ".");
                                                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, CurseDur, 3));
                                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, CurseDur, 3));
                                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,CurseDur,5));
                                                    p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,CurseDur,1));
                                                    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,CurseDur,0));

                                                    double ph = p.getHealth();

                                                    if ((ph - 3) < 1)
                                                    {
                                                        p.setHealth(1);
                                                    }
                                                    else
                                                    {
                                                        p.setHealth(ph - 3);
                                                    }
                                                }
                                              }
                                            }
                                        }
                                      
                                    }
                                }
                             }
                            }// end spellcheck 3 - curse
                                
                        }
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wear leather armor to cast this spell.");
                            return;
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must wear leather armor to cast this spell.");
                        return;
                    }
                }
            }
            
        }
    } // end player interact
    
    
    
    
    private class Totem implements Runnable
    {
        Player player;
        int taskID;
        int tick;
        
        public Totem(Player player)
        {
            this.player = player;
            this.taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Shaman.this.plugin, this, 0, interval);
            this.tick = 0;
            
            totemID.put(player.getName(), taskID);
        }
        
        @Override
        public void run() 
        {
           if (totemBlocks.containsKey(player.getName().toLowerCase()))
           {
               Location ploc = player.getLocation();
               Block block = totemBlocks.get(player.getName().toLowerCase());
               Location bloc = block.getLocation();
           
               World tworld = block.getWorld();
           
                tick++;
           
                if (ploc.distance(bloc) > 35)
                {
                    block.setType(Material.AIR);
                    
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You moved too far from your totem. It has been destroyed.");
                    
                    if (totemID.containsKey(player.getName()))
                    {    
                        totemID.remove(player.getName());
                    }
                    
                    totemBlocks.remove(player.getName().toLowerCase());
                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                }
           
                if (tick >= totalPulses)
                {
                    if (plugin.party.containsKey(player.getName().toLowerCase()))
                    {
                        int partymembers = plugin.party.get(player.getName().toLowerCase());
                    
                        for (final Player partycheck : plugin.getServer().getOnlinePlayers())
                        {
                           if (plugin.party.containsKey(partycheck.getName().toLowerCase()))
                           {
                                int pcheck = plugin.party.get(partycheck.getName().toLowerCase());
                           
                                if (pcheck == partymembers && partycheck != player)
                                {
                                     partycheck.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s" + ChatColor.RED + " Healing Totem has expired.");
                                      player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Healing Totem has expired.");
                                }
                            }
                        }
                    }    
                    else
                    {
                         player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Healing Totem has expired.");
                    }
               
                    block.setType(Material.AIR);
                    totemBlocks.remove(player.getName().toLowerCase());
                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                    return;
                }

                         
                /*
                 * Do self HEal
                 */
                boolean canHeal = false;
                int currenttime = plugin.getCurrentTime();

                if (HealICD.containsKey(player.getName()))
                {
                    int healcheck = HealICD.get(player.getName());
                    int totaltime = currenttime - healcheck;

                    if (totaltime > healICD || totaltime < 0)
                    {
                        canHeal = true;
                    }
                }
                else
                {
                    canHeal = true;
                }

                if (canHeal)
                {
                    double ph = player.getHealth();
                    double shamanheal = healAmount / 2;

                    if ((ph + shamanheal) >= player.getMaxHealth())
                    {
                        player.setHealth(player.getMaxHealth());
                    }
                    else
                    {
                        player.setHealth(ph + shamanheal);
                    }


                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + "Your " + ChatColor.AQUA + "Healing Totem" + ChatColor.AQUA + " renews you.");
                     for (Player p : plugin.getServer().getOnlinePlayers())
                     {
                         if (p.getWorld().equals(player.getWorld()))
                         {
                             if (p.getLocation().distance(ploc) < TotemRange)
                             {
                                 p.playSound(ploc, Sound.WATER, 25, 25);
                             }
                         }
                     }

                    HealICD.put(player.getName(), currenttime);
                }
                else
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your" + ChatColor.AQUA + " Healing Totem " + ChatColor.RED + "cannot affect you. Another Totem healed too recently.");
                }
           
                


                 /*
                  * Do party heal
                  */
                if (plugin.party.containsKey(player.getName().toLowerCase()))
                {
                    int partymembers = plugin.party.get(player.getName().toLowerCase());

                    for (final Player partyplayer : plugin.getServer().getOnlinePlayers())
                    {
                        if (plugin.party.containsKey(partyplayer.getName().toLowerCase()))
                        {
                            int partyplayers = plugin.party.get(partyplayer.getName().toLowerCase());

                            if (partyplayers == partymembers && partyplayer != player)
                            {
                                Location partyloc = partyplayer.getLocation();

                                if (partyplayer.getWorld().equals(tworld))
                                {
                                    if (bloc.distance(partyloc) < TotemRange)
                                    {
                                        if (partyplayer.isDead())
                                        {
                                            return;
                                        }

                                        boolean canHeal2 = false;
                                        
                                        if (HealICD.containsKey(partyplayer.getName()))
                                        {
                                            int healcheck = HealICD.get(partyplayer.getName());
                                            int totaltime = currenttime - healcheck;
                                            
                                            if (totaltime > healICD || totaltime < 0)
                                            {
                                                canHeal2 = true;
                                            }
                                        }
                                        else
                                        {
                                            canHeal2 = true;
                                        }
                                        
                                        if (canHeal2)
                                        {
                                            partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s" 
                                                + ChatColor.AQUA + " Healing Totem renews you.");

                                            for (Player everyone : plugin.getServer().getOnlinePlayers())
                                            {
                                                if (everyone.getWorld().equals(partyplayer.getWorld()))
                                                {
                                                    if (everyone.getLocation().distance(partyloc) < 25)
                                                    {
                                                        everyone.playSound(partyloc, Sound.WATER, 25, 25);
                                                    }
                                                }
                                            }

                                            double pph = partyplayer.getHealth();
                                            if ((pph + healAmount) >= partyplayer.getMaxHealth())
                                            {
                                                partyplayer.setHealth(partyplayer.getMaxHealth());
                                            }
                                            else
                                            {
                                                partyplayer.setHealth(pph + healAmount);
                                            }
                                            
                                            HealICD.put(partyplayer.getName(), currenttime);
                                        }
                                        else
                                        {
                                            partyplayer.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s" + ChatColor.AQUA + " Healing Totem " + ChatColor.RED + "cannot affect you. Another Totem healed too recently.");
                                        }

                                    }
                                }
                            }
                        }
                    }

                } // end party check
                else
                if (plugin.maHandler != null && plugin.maHandler.isPlayerInArena(player))
                {
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
                                
                                if (p.getWorld().equals(block.getWorld()))
                                {
                                    if (p != player)
                                    {
                                        if (p.getLocation().distance(bloc) < TotemRange)
                                        {
                                            boolean canHeal2 = false;
                                        
                                            if (HealICD.containsKey(p.getName()))
                                            {
                                                int healcheck = HealICD.get(p.getName());
                                                int totaltime = currenttime - healcheck;

                                                if (totaltime > healICD || totaltime < 0)
                                                {
                                                    canHeal2 = true;
                                                }
                                            }
                                            else
                                            {
                                                canHeal2 = true;
                                            }

                                            if (canHeal2)
                                            {
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s" 
                                                    + ChatColor.AQUA + " Healing Totem renews you.");

                                                for (Player everyone : plugin.getServer().getOnlinePlayers())
                                                {
                                                    if (everyone.getWorld().equals(p.getWorld()))
                                                    {
                                                        if (everyone.getLocation().distance(p.getLocation()) < 25)
                                                        {
                                                            everyone.playSound(p.getLocation(), Sound.WATER, 25, 25);
                                                        }
                                                    }
                                                }

                                                double pph = p.getHealth();
                                                if ((pph + healAmount) >= p.getMaxHealth())
                                                {
                                                    p.setHealth(p.getMaxHealth());
                                                }
                                                else
                                                {
                                                    p.setHealth(pph + healAmount);
                                                }

                                                HealICD.put(p.getName(), currenttime);
                                            }
                                            else
                                            {
                                                p.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s" + ChatColor.AQUA + " Healing Totem " + ChatColor.RED + "cannot affect you. Another Totem healed too recently.");
                                            }
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
           

           
        
           }// end if check
           else
           {
               if (totemID.containsKey(player.getName()))
               {
                   totemID.remove(player.getName());
               }
                   
               player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_RED + "Failed. Player not found in block hash. Send this to cr0ss to fix!");
               
               Bukkit.getServer().getScheduler().cancelTask(taskID);
           }
        
        } 
        
    }
    
    
    public void cleanUP()
    {
        if (totemBlocks.size() > 0)
        {
            for (Block b : totemBlocks.values())
            {
                if (!(b.getType() == Material.AIR))
                {
                    b.setType(Material.AIR);
                }
            }
        }
    }
   
}
