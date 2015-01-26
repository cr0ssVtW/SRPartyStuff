package sr.party;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Assassin implements Listener
{
    public Party plugin;
    
    public Assassin (Party plugin)
    {
        this.plugin = plugin;
    }
    
    //Stealth
    public HashMap<String, Integer> stealthCD = new HashMap<String, Integer>();
    public HashSet<String> isStealthed = new HashSet<String>();
    public int stealthDur = 15;
    public int StealthCD = 12;
    public double hearts = 2;
    public double multi = 1.75;
    public double magehearts = 4;
    public double magemulti = 2;
    
    public HashMap<String, Integer> ssCD = new HashMap<String, Integer>();
    public HashMap<String, Integer> ssCount = new HashMap<String, Integer>();
    public HashMap<String, String> ssLast = new HashMap<String, String>();
    public int SSCD = 30;
    public int SSRange = 32;
    
    public HashMap<String, Integer> poisonCD = new HashMap<String, Integer>();
    public HashMap<String, String> poisonSelected = new HashMap<String, String>();
    public HashMap<String, String> poisonChoosing = new HashMap<String, String>();
    public HashMap<String, Integer> poisonCount = new HashMap<String, Integer>();
    public HashMap<String, Integer> poisonICD = new HashMap<String, Integer>();
    public int PoisonUses = 3;
    public int PoisonCD = 60;
    public int PICD = 5;
    public double pDeadlyDmg = 20;
    public double pFeastingLeech = 4;
    public int pNumbingDur = 120;
    public int pNumbingStr = 3;
    public int pSickeningDur = 100;
    public int pSickeningStr = 2;
    
    @EventHandler
    public void onDamageTaken (EntityDamageEvent event)
    {
        Entity ent = event.getEntity();
        
        if (ent instanceof Player)
        {
            Player player = (Player) ent;
            
            if (isStealthed.contains(player.getName()))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have taken damage while Stealthed!");
                unStealth(player.getName());
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent evt)
    {
    	Player player = evt.getPlayer();
    	if(isStealthed.contains(player.getName()))
    	{
    		 unStealth(player.getName()); //unstealth if player logsout
    	}
    }
    
    @EventHandler
    public void onDamageDone (EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player target = (Player) event.getEntity();
            
            if (event.getDamager() instanceof Player)
            {
                Player player = (Player) event.getDamager();

                if (PL.stuntarget.containsKey(player.getName().toLowerCase()) || Paladin.isStunned.contains(player.getName()) || ChaosCrusader.chaosstunned.containsKey(player.getName()))
                {
                      event.setCancelled(true);
                      player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
                      return;
                }
                
                int myparty = -1;
                int targetparty = -2;
                
                if (plugin.party.containsKey(player.getName().toLowerCase()))
                {
                    myparty = plugin.party.get(player.getName().toLowerCase());
                }
                
                if (plugin.party.containsKey(target.getName().toLowerCase()))
                {
                    targetparty = plugin.party.get(target.getName().toLowerCase());
                }
                
                if (myparty != targetparty)
                {
                    if (isStealthed.contains(player.getName()))
                    {
                        if (target.hasPermission("mage.bane"))
                        {
                            double hp = target.getHealth();
                            if ((hp - magehearts) < 1)
                            {
                                target.setHealth(1);
                            }
                            else
                            {
                                target.setHealth(hp - magehearts);
                            }

                            double dmg = event.getDamage();
                            double newdmg = dmg * magemulti;

                            event.setDamage(newdmg);
                            
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You Sneak Attack your target!");
                        }
                        else
                        {
                            double hp = target.getHealth();
                            if ((hp - hearts) < 1)
                            {
                                target.setHealth(1);
                            }
                            else
                            {
                                target.setHealth(hp - hearts);
                            }

                            double dmg = event.getDamage();
                            double newdmg = dmg * multi;

                            event.setDamage(newdmg);
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You Sneak Attack your target!");
                        }

                        unStealth(player.getName());
                    } // end stealth
                    
                    if (poisonSelected.containsKey(player.getName()))
                    {
                        doPoison(player.getName(), target.getName(), false);
                    }

                } // end party check
                
            }

            if (event.getDamager() instanceof Projectile)
            {
                Projectile proj = (Projectile) event.getDamager();

                if (proj.getShooter() instanceof Player)
                {
                    Player player = (Player) proj.getShooter();

                    int myparty = -1;
                    int targetparty = -2;

                    if (plugin.party.containsKey(player.getName().toLowerCase()))
                    {
                        myparty = plugin.party.get(player.getName().toLowerCase());
                    }

                    if (plugin.party.containsKey(target.getName().toLowerCase()))
                    {
                        targetparty = plugin.party.get(target.getName().toLowerCase());
                    }
                    
                    if (myparty != targetparty)
                    {
                        if (isStealthed.contains(player.getName()))
                        {
                            if (target.hasPermission("mage.bane"))
                            {
                                double hp = target.getHealth();
                                if ((hp - magehearts) < 1)
                                {
                                    target.setHealth(1);
                                }
                                else
                                {
                                    target.setHealth(hp - magehearts);
                                }

                                double dmg = event.getDamage();
                                double newdmg = dmg * magemulti;

                                event.setDamage(newdmg);
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You Sneak Attack your target!");
                            }
                            else
                            {
                                double hp = target.getHealth();
                                if ((hp - hearts) < 1)
                                {
                                    target.setHealth(1);
                                }
                                else
                                {
                                    target.setHealth(hp - hearts);
                                }

                                double dmg = event.getDamage();
                                double newdmg = dmg * multi;

                                event.setDamage(newdmg);
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.AQUA + "You Sneak Attack your target!");
                            }

                            unStealth(player.getName());
                        } // end stealth
                        
                        if (poisonSelected.containsKey(player.getName()))
                        {
                            doPoison(player.getName(), target.getName(), false);
                        }
                        
                    } // end party check
                } // end shooter Player check
            } // projectile check
        }
        
    }
    
    
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        
        if (PL.stuntarget.containsKey(player.getName().toLowerCase()) || Paladin.isStunned.contains(player.getName()) || ChaosCrusader.chaosstunned.containsKey(player.getName()))
        {
              event.setCancelled(true);
              player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have been stunned and cannot interact.");
              return;
        }
        
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (player.getItemInHand() != null)
            {
                if (player.getItemInHand().getType() == Material.GHAST_TEAR)
                {
                    if (player.hasPermission("assassin.shadowstep"))
                    {
                        if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null
                                && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null)
                        {
                            if (player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET && player.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE
                                    && player.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS && player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS)
                            {
                                if (player.isSneaking())
                                {
                                    if (ssCount.containsKey(player.getName()))
                                    {
                                        ssCount.remove(player.getName());
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Shadow Step count cleared. Now on cooldown.");
                                        
                                        if (ssLast.containsKey(player.getName()))
                                        {
                                            ssLast.remove(player.getName());
                                        }
                                        
                                        int currenttime = plugin.getCurrentTime();
                                        
                                        if (ssCD.containsKey(player.getName()))
                                        {
                                            ssCD.remove(player.getName());
                                        }
                                        
                                        ssCD.put(player.getName(), currenttime);
                                        
                                        return;
                                    }
                                }
                                
                                boolean canSS = false;
                                int currenttime = plugin.getCurrentTime();
                                if (ssCD.containsKey(player.getName()))
                                {
                                    
                                    int timecheck = ssCD.get(player.getName());
                                    int totaltime = currenttime - timecheck;
                                    
                                    if (totaltime > SSCD || totaltime < 0)
                                    {
                                        canSS = true;
                                    }
                                    else
                                    {
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Cannot Shadow Step for another " + (SSCD - totaltime) + " seconds.");
                                        return;
                                    }
                                }
                                else
                                {
                                    canSS = true;
                                }
                                
                                if (canSS)
                                {
                                    ShadowStep(player.getName());
                                }
                                
                            } // end armor check
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing full leather armor to Shadow Step.");
                                return;
                            }
                        } // end armor null check
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing full leather armor to Shadow Step.");
                            return;
                        }
                    } // end stealth perm check
                } // end material check ghast tear
            } // end null check hand
        } // end right click check
        
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            if (player.getItemInHand() != null)
            {
                if (player.getItemInHand().getType() == Material.POTION)
                {
                    if (player.hasPermission("assassin.poison"))
                    {
                        if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null
                                && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null)
                        {
                            if (player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET && player.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE
                                    && player.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS && player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS)
                            {
                                String deadly = "Deadly";
                                String feasting = "Feasting";
                                String numbing = "Numbing";
                                String sickening = "Sickening";
                                
                                if (player.isSneaking())
                                {
                                    if (poisonChoosing.containsKey(player.getName()))
                                    {
                                        String type = poisonChoosing.get(player.getName());
                                        poisonChoosing.remove(player.getName());
                                        if (type.equals(deadly))
                                        {
                                            poisonChoosing.put(player.getName(), feasting);
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You select your Feasting Poison.");
                                        }
                                        if (type.equals(feasting))
                                        {
                                            poisonChoosing.put(player.getName(), numbing);
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You select your Numbing Poison.");
                                        }
                                        if (type.equals(numbing))
                                        {
                                            poisonChoosing.put(player.getName(), sickening);
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You select your Sickening Poison.");
                                        }
                                        if (type.equals(sickening))
                                        {
                                            poisonChoosing.put(player.getName(), deadly);
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You select your Deadly Poison.");
                                        }
                                    }
                                    else
                                    {
                                        poisonChoosing.put(player.getName(), deadly);
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You select your Deadly Poison.");
                                    }
                                }
                                else
                                {
                                    boolean canPoison = false;
                                    int currenttime = plugin.getCurrentTime();
                                    if (poisonCD.containsKey(player.getName()))
                                    {
                                        int timecheck = poisonCD.get(player.getName());
                                        int totaltime = currenttime - timecheck;

                                        if (totaltime > PoisonCD || totaltime < 0)
                                        {
                                            poisonCD.remove(player.getName());
                                            canPoison = true;
                                        }
                                        else
                                        {
                                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Cannot Apply Poison for another " + (PoisonCD - totaltime) + " seconds.");
                                            return;
                                        }
                                    }
                                    else
                                    {
                                        canPoison = true;
                                    }

                                    if (canPoison)
                                    {
                                        String type = deadly;
                                        if (poisonChoosing.containsKey(player.getName()))
                                        {
                                            type = poisonChoosing.get(player.getName());
                                        }

                                        poisonCD.put(player.getName(), currenttime);
                                        poisonSelected.put(player.getName(), type);
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You apply " + ChatColor.GOLD + type + " Poison " 
                                                + ChatColor.DARK_AQUA + "to your weapons.");
                                    }
                                }
                                
                                
                            }
                        }
                    }
                }
                
                if (player.getItemInHand().getType() == Material.GHAST_TEAR)
                {
                    if (player.hasPermission("assassin.stealth"))
                    {
                        if (player.getInventory().getHelmet() != null && player.getInventory().getChestplate() != null
                                && player.getInventory().getLeggings() != null && player.getInventory().getBoots() != null)
                        {
                            if (player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET && player.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE
                                    && player.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS && player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS)
                            {
                                if (isStealthed.contains(player.getName()))
                                {
                                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are already Stealthed.");
                                    return;
                                }
                                
                                boolean canStealth = false;
                                int currenttime = plugin.getCurrentTime();
                                if (stealthCD.containsKey(player.getName()))
                                {
                                    int timecheck = stealthCD.get(player.getName());
                                    int totaltime = currenttime - timecheck;
                                    
                                    if (totaltime > StealthCD || totaltime < 0)
                                    {
                                        canStealth = true;
                                    }
                                    else
                                    {
                                        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Cannot Stealth for another " + (StealthCD - totaltime) + " seconds.");
                                        return;
                                    }
                                }
                                else
                                {
                                    canStealth = true;
                                }
                                
                                if (canStealth)
                                {
                                    onStealth(player.getName());
                                }
                                
                            } // end armor check
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing full leather armor to Stealth.");
                                return;
                            }
                        } // end armor null check
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You must be wearing full leather armor to Stealth.");
                            return;
                        }
                    } // end stealth perm check
                } // end material check ghast tear
            } // end null check hand
        } // end left click
    }
    
    public void ShadowStep(String name)
    {   
        Player player = Bukkit.getPlayer(name);
        int count = 0;
        
        String lastName = "null";
        if (ssLast.containsKey(player.getName()))
        {
            lastName = ssLast.get(player.getName());
            //Bukkit.broadcastMessage("SS Old Target is: " + lastName);
        }
        
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if ((p != player) && (player.canSee(p)))
            {
                if (!(p.getName().equals(lastName)))
                {
                    if (p.getWorld().equals(player.getWorld()))
                    {
                        if (p.getLocation().distance(player.getLocation()) < SSRange)
                        {
                            count++;
                            //Bukkit.broadcastMessage("SS Player Count is: " + count + " | Name is: " + p.getName());
                        }
                    }
                }
            }
        }
        
        if (count > 0)
        {
            Random rand = new Random();
            int num = rand.nextInt(count) + 1;
            
           // Bukkit.broadcastMessage("Num is: " + num);
            
            if (num == 0)
            {
               // Bukkit.broadcastMessage("Num was 0. Set to 1.");
                num = 1;
            }

            count = 0;

            int myparty = -1;
            if (plugin.party.containsKey(player.getName().toLowerCase()))
            {
                myparty = plugin.party.get(player.getName().toLowerCase());
            }
            
            
            String pname = "null";
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if ((p != player) && (player.canSee(p)))
                {
                    if (!(p.getName().equals(lastName)))
                    {
                        if (p.getWorld().equals(player.getWorld()))
                        {
                            if (p.getLocation().distance(player.getLocation()) < SSRange)
                            {
                                count++;

                                //Bukkit.broadcastMessage("new Count is: " + count + " | num is: " + num);
                                if (count == num)
                                {
                                    pname = p.getName();
                                    //Bukkit.broadcastMessage("pname is; " + pname);
                                    
                                    /*
                                     * Check party and do poison if no
                                     */
                                    int targetparty = -2;
                                    if (plugin.party.containsKey(p.getName().toLowerCase()))
                                    {
                                        targetparty = plugin.party.get(p.getName().toLowerCase());
                                    }
                                    
                                    if (myparty != targetparty)
                                    {
                                        // do poison if not in party
                                         
                                        if (poisonSelected.containsKey(player.getName()))
                                        {
                                            doPoison(player.getName(), p.getName(), true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            //Bukkit.broadcastMessage("SS Target is: " + pname);
            if (pname.equalsIgnoreCase("null"))
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No one to Shadowstep to (Error 2).");
                return;
            }
            
            lastName = pname;
            
            
            Player target = Bukkit.getPlayer(pname);

            
            
            int sscount = 0;
            if (ssCount.containsKey(player.getName()))
            {
                sscount = ssCount.get(player.getName());
            }
            
            sscount++;

            if (target.getWorld().equals(player.getWorld()))
            {
                player.teleport(target.getLocation());
            }
            else
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Something went wrong. Worlds don't match.");
            }


            if (ssLast.containsKey(player.getName()))
            {
                ssLast.remove(player.getName());
            }

            ssLast.put(name, lastName);

            if (ssCount.containsKey(player.getName()))
            {
                ssCount.remove(player.getName());
            }

            ssCount.put(player.getName(), sscount);


            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "Shadow Step Count: " + sscount);
            
            
            if (sscount >= 3)
            {
                if (ssCount.containsKey(player.getName()))
                {
                    ssCount.remove(player.getName());
                }

                if (ssLast.containsKey(player.getName()))
                {
                    ssLast.remove(player.getName());
                }

                int time = plugin.getCurrentTime();
                ssCD.put(name, time);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You have exhausted your energy.");
            }
            
        }
        else
        {
            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No targets to Shadow Step to.");
        }
    }
    
    public void doPoison(String pName, String tName, boolean isSS)
    {
        Player player = Bukkit.getPlayer(pName);
        Player target = Bukkit.getPlayer(tName);
        
        if (poisonSelected.containsKey(player.getName()))
        {
            String type = poisonSelected.get(player.getName());
            int time = plugin.getCurrentTime();

            boolean canPoison = false;
            if (poisonICD.containsKey(player.getName()))
            {
                int pcheck = poisonICD.get(player.getName());
                int totaltime = time - pcheck;

                if (totaltime > PICD || totaltime < 0)
                {
                    poisonICD.remove(player.getName());
                    canPoison = true;
                }
            }
            else
            {
                canPoison = true;
            }
            
            if (isSS)
            {
                canPoison = true;
            }
            
            if (canPoison)
            {
                if (poisonICD.containsKey(player.getName()))
                {
                    poisonICD.remove(player.getName());
                    poisonICD.put(player.getName(), time);
                }
                else
                {
                    poisonICD.put(player.getName(), time);
                }
                
                
                int pcount = 0;
                
                if (poisonCount.containsKey(player.getName()))
                {
                    pcount = poisonCount.get(player.getName());
                    poisonCount.remove(player.getName());
                }
                
                pcount++;
                poisonCount.put(player.getName(), pcount);

              //  Bukkit.broadcastMessage("Poison is: " + type);
              //  Bukkit.broadcastMessage("Poison Count is: " + pcount);
                
                
                if (pcount > 3)
                {
                    poisonCount.remove(player.getName());
                    poisonSelected.remove(player.getName());
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "Your Poison has expired.");
                    return;
                }
               

                if (type.equalsIgnoreCase("deadly"))
                {
                    target.damage(pDeadlyDmg, player);
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You rend " + ChatColor.GOLD + target.getName() 
                            + ChatColor.DARK_AQUA + " with your " + ChatColor.GOLD + "Deadly Poison");
                    target.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s Deadly Poison " + ChatColor.RED + "rends you for extra damage.");
                }

                if (type.equalsIgnoreCase("feasting"))
                {
                    target.damage(pFeastingLeech, player);

                    double php = player.getHealth();
                    if (php + pFeastingLeech > player.getMaxHealth())
                    {
                        player.setHealth(player.getMaxHealth());
                    }
                    else
                    {
                        player.setHealth(php + pFeastingLeech);
                    }

                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You siphon life from " + ChatColor.GOLD + target.getName() 
                            + ChatColor.DARK_AQUA + " with your " + ChatColor.GOLD + "Feasting Poison");
                    target.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s Feasting Poison " + ChatColor.RED + "drains life from you!");
                }

                if (type.equalsIgnoreCase("numbing"))
                {
                    if (target.hasPotionEffect(PotionEffectType.SLOW))
                    {
                        target.removePotionEffect(PotionEffectType.SLOW);
                    }
                    if (target.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
                    {
                        target.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    }
                    if (target.hasPotionEffect(PotionEffectType.SPEED))
                    {
                        Iterator<PotionEffect> it = target.getActivePotionEffects().iterator();

                        int duration = 0;
                        int level = 0;

                        while (it.hasNext())
                        {
                            PotionEffect pe = it.next();
                            if (pe.getType() == PotionEffectType.SPEED)
                            {
                                duration = pe.getDuration();
                                level = pe.getAmplifier();
                            }
                        }
                        int newlevel = level - 1;
                        if (newlevel < 0)
                        {
                            newlevel = 0;
                        }

                        target.removePotionEffect(PotionEffectType.SPEED);
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,duration,newlevel));
                    }

                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,pNumbingDur,pNumbingStr));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,pNumbingDur,pNumbingStr));

                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You hinder " + ChatColor.GOLD + target.getName() 
                            + ChatColor.DARK_AQUA + " with your " + ChatColor.GOLD + "Numbing Poison");
                    target.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s Numing Poison " + ChatColor.RED + "hinders your speed!");
                }

                if (type.equalsIgnoreCase("sickening"))
                {
                    if (target.hasPotionEffect(PotionEffectType.POISON))
                    {
                        target.removePotionEffect(PotionEffectType.POISON);
                    }
                    if (target.hasPotionEffect(PotionEffectType.CONFUSION))
                    {
                        target.removePotionEffect(PotionEffectType.CONFUSION);
                    }

                    target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,pSickeningDur,pSickeningStr));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON,pSickeningDur,pSickeningStr));

                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You infect " + ChatColor.GOLD + target.getName() 
                            + ChatColor.DARK_AQUA + " with your " + ChatColor.GOLD + "Sickening Poison");
                    target.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GOLD + player.getName() + "'s Sickening Poison " + ChatColor.RED + "festers within you!");
                }
            }
        } // end poison
    }
    
    public void onStealth(String name)
    {
        Player player = Bukkit.getPlayer(name);
        isStealthed.add(name);
        
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if ((!p.equals(player)) && (p.canSee(player)))
            {
                if (!p.hasPermission("assassin.see"))
                {
                    p.hidePlayer(player);
                }
            }
        }
        
        if (!(player.hasPotionEffect(PotionEffectType.SPEED)))
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,120,2));
        }
        
        player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.DARK_AQUA + "You slip into the shadows...");
        new Stealth(player);
    }
    
    public void unStealth(String name)
    {
        Player player = Bukkit.getPlayer(name);
        
        if (isStealthed.contains(name))
        {
            isStealthed.remove(name);
            int currenttime = plugin.getCurrentTime();
        
            if (stealthCD.containsKey(name))
            {
                stealthCD.remove(name);
            }

            stealthCD.put(name, currenttime);

            for (Player p : Bukkit.getServer().getOnlinePlayers())
            {
                if ((!p.equals(player)) && (!(p.canSee(player))))
                {
                    p.showPlayer(player);
                }
            }

            if (player.isOnline())
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are no longer stealthed.");
            }
        }
        

    }
    
    
    private class Stealth implements Runnable
    {
        Player player;
        int x;
        int taskID;
        
        public Stealth (Player player)
        {
            this.player = player;
            this.x = 0;
            this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
        }

        @Override
        public void run() 
        {
            x++;
            
            if (x > stealthDur || (!(isStealthed.contains(player.getName()))))
            {
                if(player.isOnline())
                {
                unStealth(player.getName());
                }
                Bukkit.getScheduler().cancelTask(taskID);
            }
        }
        
    }
}