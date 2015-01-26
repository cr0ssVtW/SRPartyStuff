package sr.party;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Cross
 */
public class Durability implements Listener
{
    public Party plugin;
    
    public Durability(Party plugin)
    {
        this.plugin = plugin;
    }
    
    WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
    
    public Short GSwordfullDura = 750;
    public Short GSwordcurDura = 750;
    
    public Short GHelmFullDura = 400;
    public Short GHelmCurDura = 400;
    
    public Short GChestFullDura = 550;
    public Short GChestCurDura = 550;
    
    public Short GLegsFullDura = 550;
    public Short GLegsCurDura = 550;
    
    public Short GBootsFullDura = 400;
    public Short GBootsCurDura = 400;
    
    
    public Short LHelmFullDura = 250;
    public Short LHelmCurDura = 250;
    
    public Short LChestFullDura = 375;
    public Short LChestCurDura = 375;
    
    public Short LLegsFullDura = 375;
    public Short LLegsCurDura = 375;
    
    public Short LBootsFullDura = 250;
    public Short LBootsCurDura = 250;
    
    
    @EventHandler
    public void onEntityDamageByEntity (EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player target = (Player) event.getEntity();
            
            if (target.hasPermission("paladin.gold") || target.hasPermission("sr.goldarmor") || target.hasPermission("sr.leatherarmor"))
            {
                ItemStack helm;
                ItemStack chest;
                ItemStack legs;
                ItemStack boots;
                
                if (event.getDamager() instanceof Player)
                {
                    World pworld = target.getWorld();

                    BlockVector pt = BukkitUtil.toVector(target.getLocation().getBlock());

                    RegionManager regionManager = worldGuard.getRegionManager(pworld);
                    ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                    if (!(set.allows(DefaultFlag.PVP)))
                    {
                        return;
                    }
                }

                if (target.getInventory().getHelmet() != null)
                {
                    helm = target.getInventory().getHelmet();
                    
                    if (helm.getTypeId() == 298) // leather helm check
                    {
                        ItemMeta meta = helm.getItemMeta();
                        Boolean containsDur = false;

                        if (meta.hasLore())
                        {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                            Iterator<String> it = lore.iterator();

                            String dur;
                            int i = 0;

                            while (it.hasNext())
                            {
                                dur = it.next();

                                if (dur.contains("Durability: "))
                                {
                                    containsDur = true;


                                    int start = dur.indexOf("(");
                                    int end = dur.indexOf("/");

                                    String durability = dur.substring(start + 1, end);

                                    if (Party.isShort(durability))
                                    {
                                        short newDur = Short.parseShort(durability);

                                        newDur = (short) (newDur - 1);

                                        durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + LHelmFullDura + ")";

                                        lore.set(i, durability);
                                        meta.setLore(lore);
                                        helm.setItemMeta(meta);

                                        if (newDur >= (short) 225)
                                        {
                                            helm.setDurability((short)5);
                                        }
                                        if (newDur > (short) 200 && newDur < (short) 250)
                                        {
                                            helm.setDurability((short)10);
                                        }

                                        if (newDur > (short) 175 && newDur < (short) 200)
                                        {
                                            helm.setDurability((short) 17);
                                        }

                                        if (newDur > (short) 125 && newDur < (short) 175)
                                        {
                                            helm.setDurability((short) 25);
                                        }

                                        if (newDur > (short) 85 && newDur < (short) 125)
                                        {
                                            helm.setDurability((short) 37);
                                        }

                                        if (newDur > (short) 50 && newDur < (short) 85)
                                        {
                                            helm.setDurability((short) 45);
                                        }

                                        if (newDur > (short) 0 && newDur < (short) 50)
                                        {
                                            helm.setDurability((short) 50);
                                        }

                                        if (newDur <= 0)
                                        {
                                            helm.setDurability((short) 56);
                                            target.getInventory().setHelmet(null);
                                        }

                                    }


                                }
                                i = i + 1;
                            } // end while
                            
                        } // end if meta
                        
                        if (containsDur == false)
                        {
                            short curDur = helm.getDurability();

                            if (curDur >= (short) 0 && curDur <= (short) 5)
                            {
                                LHelmCurDura = (short) 250;
                            }

                            if (curDur > (short) 5 && curDur < (short) 10)
                            {
                                LHelmCurDura = (short) 200;
                            }

                            if (curDur >= (short) 10 && curDur < (short) 17)
                            {
                                LHelmCurDura = (short) 175;
                            }

                            if (curDur >= (short) 17 && curDur < (short) 29)
                            {
                                LHelmCurDura = (short) 125;
                            }

                            if (curDur >= (short) 29 && curDur < (short) 41)
                            {
                                LHelmCurDura = (short) 85;
                            }

                            if (curDur >= (short) 41 && curDur < (short) 49)
                            {
                                LHelmCurDura = (short) 50;
                            }

                            if (curDur >= (short) 49 && curDur < (short) 54)
                            {
                                LHelmCurDura = (short) 20;
                            }

                            if (curDur >= (short) 54 && curDur < (short) 56)
                            {
                                LHelmCurDura = (short) 1;
                            }
                            
                            ArrayList<String> lore = new ArrayList<String>();

                            if (meta.hasLore())
                            {
                                lore = (ArrayList<String>) meta.getLore();
                            }

                            String asdf = ChatColor.GRAY + "Durability: " + "(" + LHelmCurDura + "/" + LHelmFullDura + ")";
                            lore.add(asdf);

                            meta.setLore(lore);
                            helm.setItemMeta(meta);
                        }
                        
                        
                    } // end leather helm check
                    
                    if (helm.getTypeId() == 314) // gold helm
                    {
                        ItemMeta meta = helm.getItemMeta();
                        Boolean containsDur = false;

                        if (meta.hasLore())
                        {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                            Iterator<String> it = lore.iterator();

                            String dur;
                            int i = 0;

                            while (it.hasNext())
                            {
                                dur = it.next();

                                if (dur.contains("Durability: "))
                                {
                                    containsDur = true;


                                    int start = dur.indexOf("(");
                                    int end = dur.indexOf("/");

                                    String durability = dur.substring(start + 1, end);

                                    if (Party.isShort(durability))
                                    {
                                        short newDur = Short.parseShort(durability);

                                        newDur = (short) (newDur - 1);

                                        durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + GHelmFullDura + ")";

                                        lore.set(i, durability);
                                        meta.setLore(lore);
                                        helm.setItemMeta(meta);

                                        if (newDur >= (short) 375)
                                        {
                                            helm.setDurability((short)5);
                                        }
                                        if (newDur > (short) 325 && newDur < (short) 375)
                                        {
                                            helm.setDurability((short)10);
                                        }

                                        if (newDur > (short) 275 && newDur < (short) 325)
                                        {
                                            helm.setDurability((short) 17);
                                        }

                                        if (newDur > (short) 225 && newDur < (short) 275)
                                        {
                                            helm.setDurability((short) 29);
                                        }

                                        if (newDur > (short) 150 && newDur < (short) 225)
                                        {
                                            helm.setDurability((short) 41);
                                        }

                                        if (newDur > (short) 100 && newDur < (short) 150)
                                        {
                                            helm.setDurability((short) 55);
                                        }

                                        if (newDur > (short) 0 && newDur < (short) 100)
                                        {
                                            helm.setDurability((short) 66);
                                        }

                                        if (newDur <= 0)
                                        {
                                            helm.setDurability((short) 77);
                                            target.getInventory().setHelmet(null);
                                        }

                                    }


                                }
                                i = i + 1;
                            } // end while
                            
                        } // end if meta
                        
                        if (containsDur == false)
                        {
                            short curDur = helm.getDurability();

                            if (curDur >= (short) 0 && curDur <= (short) 5)
                            {
                                GHelmCurDura = (short) 400;
                            }

                            if (curDur > (short) 5 && curDur < (short) 10)
                            {
                                GHelmCurDura = (short) 300;
                            }

                            if (curDur >= (short) 10 && curDur < (short) 17)
                            {
                                GHelmCurDura = (short) 250;
                            }

                            if (curDur >= (short) 17 && curDur < (short) 29)
                            {
                                GHelmCurDura = (short) 225;
                            }

                            if (curDur >= (short) 29 && curDur < (short) 41)
                            {
                                GHelmCurDura = (short) 175;
                            }

                            if (curDur >= (short) 41 && curDur < (short) 55)
                            {
                                GHelmCurDura = (short) 125;
                            }

                            if (curDur >= (short) 55 && curDur < (short) 66)
                            {
                                GHelmCurDura = (short) 125;
                            }

                            if (curDur >= (short) 66 && curDur < (short) 77)
                            {
                                GHelmCurDura = (short) 1;
                            }
                            
                            ArrayList<String> lore = new ArrayList<String>();

                            if (meta.hasLore())
                            {
                                lore = (ArrayList<String>) meta.getLore();
                            }

                            String asdf = ChatColor.GRAY + "Durability: " + "(" + GHelmCurDura + "/" + GHelmFullDura + ")";
                            lore.add(asdf);

                            meta.setLore(lore);
                            helm.setItemMeta(meta);
                        }
                        
                        
                    } // end gold helm check
                } // end helm null check
                
                if (target.getInventory().getChestplate() != null)
                {
                    chest = target.getInventory().getChestplate();
                    
                    if (chest.getTypeId() == 299) // leather chest check
                    {
                        ItemMeta meta = chest.getItemMeta();
                        Boolean containsDur = false;

                        if (meta.hasLore())
                        {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                            Iterator<String> it = lore.iterator();

                            String dur;
                            int i = 0;

                            while (it.hasNext())
                            {
                                dur = it.next();

                                if (dur.contains("Durability: "))
                                {
                                    containsDur = true;


                                    int start = dur.indexOf("(");
                                    int end = dur.indexOf("/");

                                    String durability = dur.substring(start + 1, end);

                                    if (Party.isShort(durability))
                                    {
                                        short newDur = Short.parseShort(durability);

                                        newDur = (short) (newDur - 1);

                                        durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + LChestFullDura + ")";

                                        lore.set(i, durability);
                                        meta.setLore(lore);
                                        chest.setItemMeta(meta);

                                        if (newDur >= (short) 350)
                                        {
                                            chest.setDurability((short)4);
                                        }
                                        if (newDur > (short) 300 && newDur < (short) 350)
                                        {
                                            chest.setDurability((short)9);
                                        }

                                        if (newDur > (short) 245 && newDur < (short) 300)
                                        {
                                            chest.setDurability((short) 18);
                                        }

                                        if (newDur > (short) 205 && newDur < (short) 245)
                                        {
                                            chest.setDurability((short) 30);
                                        }

                                        if (newDur > (short) 160 && newDur < (short) 205)
                                        {
                                            chest.setDurability((short) 48);
                                        }

                                        if (newDur > (short) 125 && newDur < (short) 160)
                                        {
                                            chest.setDurability((short) 60);
                                        }

                                        if (newDur > (short) 85 && newDur < (short) 125)
                                        {
                                            chest.setDurability((short) 65);
                                        }
                                        
                                        if (newDur > (short) 60 && newDur < (short) 85)
                                        {
                                            chest.setDurability((short) 70);
                                        }
                                        
                                        if (newDur > (short) 40 && newDur < (short) 60)
                                        {
                                            chest.setDurability((short) 74);
                                        }
                                        
                                        if (newDur > (short) 20 && newDur < (short) 40)
                                        {
                                            chest.setDurability((short) 76);
                                        }
                                        
                                        if (newDur > (short) 10 && newDur < (short) 20)
                                        {
                                            chest.setDurability((short) 78);
                                        }
                                        
                                        if (newDur > (short) 0 && newDur < (short) 10)
                                        {
                                            chest.setDurability((short) 79);
                                        }

                                        if (newDur <= 0)
                                        {
                                            chest.setDurability((short) 80);
                                            target.getInventory().setChestplate(null);
                                        }

                                    }


                                }
                                i = i + 1;
                            } // end while
                            
                        } // end if meta
                        
                        if (containsDur == false)
                        {
                            short curDur = chest.getDurability();

                            if (curDur >= (short) 0 && curDur <= (short) 5)
                            {
                                LChestCurDura = (short) 375;
                            }

                            if (curDur > (short) 5 && curDur < (short) 15)
                            {
                                LChestCurDura = (short) 325;
                            }

                            if (curDur >= (short) 15 && curDur < (short) 30)
                            {
                                LChestCurDura = (short) 260;
                            }

                            if (curDur >= (short) 30 && curDur < (short) 45)
                            {
                                LChestCurDura = (short) 200;
                            }

                            if (curDur >= (short) 45 && curDur < (short) 55)
                            {
                                LChestCurDura = (short) 150;
                            }

                            if (curDur >= (short) 55 && curDur < (short) 60)
                            {
                                LChestCurDura = (short) 125;
                            }

                            if (curDur >= (short) 60 && curDur < (short) 70)
                            {
                                LChestCurDura = (short) 50;
                            }

                            if (curDur >= (short) 70 && curDur < (short) 81)
                            {
                                LChestCurDura = (short) 1;
                            }
                            
                            ArrayList<String> lore = new ArrayList<String>();

                            if (meta.hasLore())
                            {
                                lore = (ArrayList<String>) meta.getLore();
                            }

                            String asdf = ChatColor.GRAY + "Durability: " + "(" + LChestCurDura + "/" + LChestFullDura + ")";
                            lore.add(asdf);

                            meta.setLore(lore);
                            chest.setItemMeta(meta);
                        }
                        
                        
                    } // end leather chest check
                    
                    if (chest.getTypeId() == 315) // gold chest check
                    {
                        ItemMeta meta = chest.getItemMeta();
                        Boolean containsDur = false;

                        if (meta.hasLore())
                        {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                            Iterator<String> it = lore.iterator();

                            String dur;
                            int i = 0;

                            while (it.hasNext())
                            {
                                dur = it.next();

                                if (dur.contains("Durability: "))
                                {
                                    containsDur = true;


                                    int start = dur.indexOf("(");
                                    int end = dur.indexOf("/");

                                    String durability = dur.substring(start + 1, end);

                                    if (Party.isShort(durability))
                                    {
                                        short newDur = Short.parseShort(durability);

                                        newDur = (short) (newDur - 1);

                                        durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + GChestFullDura + ")";

                                        lore.set(i, durability);
                                        meta.setLore(lore);
                                        chest.setItemMeta(meta);

                                        if (newDur >= (short) 550)
                                        {
                                            chest.setDurability((short)5);
                                        }
                                        if (newDur > (short) 500 && newDur < (short) 550)
                                        {
                                            chest.setDurability((short)10);
                                        }

                                        if (newDur > (short) 450 && newDur < (short) 500)
                                        {
                                            chest.setDurability((short) 20);
                                        }

                                        if (newDur > (short) 400 && newDur < (short) 450)
                                        {
                                            chest.setDurability((short) 30);
                                        }

                                        if (newDur > (short) 350 && newDur < (short) 400)
                                        {
                                            chest.setDurability((short) 40);
                                        }

                                        if (newDur > (short) 300 && newDur < (short) 350)
                                        {
                                            chest.setDurability((short) 50);
                                        }

                                        if (newDur > (short) 250 && newDur < (short) 300)
                                        {
                                            chest.setDurability((short) 60);
                                        }
                                        
                                        if (newDur > (short) 200 && newDur < (short) 250)
                                        {
                                            chest.setDurability((short) 70);
                                        }
                                        
                                        if (newDur > (short) 150 && newDur < (short) 200)
                                        {
                                            chest.setDurability((short) 80);
                                        }
                                        
                                        if (newDur > (short) 100 && newDur < (short) 150)
                                        {
                                            chest.setDurability((short) 90);
                                        }
                                        
                                        if (newDur > (short) 50 && newDur < (short) 100)
                                        {
                                            chest.setDurability((short) 100);
                                        }
                                        
                                        if (newDur > (short) 0 && newDur < (short) 50)
                                        {
                                            chest.setDurability((short) 110);
                                        }

                                        if (newDur <= 0)
                                        {
                                            chest.setDurability((short) 112);
                                            target.getInventory().setChestplate(null);
                                        }

                                    }


                                }
                                i = i + 1;
                            } // end while
                            
                        } // end if meta
                        
                        if (containsDur == false)
                        {
                            short curDur = chest.getDurability();

                            if (curDur >= (short) 0 && curDur <= (short) 5)
                            {
                                GChestCurDura = (short) 550;
                            }

                            if (curDur > (short) 5 && curDur < (short) 15)
                            {
                                GChestCurDura = (short) 500;
                            }

                            if (curDur >= (short) 15 && curDur < (short) 30)
                            {
                                GChestCurDura = (short) 450;
                            }

                            if (curDur >= (short) 30 && curDur < (short) 45)
                            {
                                GChestCurDura = (short) 400;
                            }

                            if (curDur >= (short) 45 && curDur < (short) 60)
                            {
                                GChestCurDura = (short) 350;
                            }

                            if (curDur >= (short) 60 && curDur < (short) 75)
                            {
                                GChestCurDura = (short) 275;
                            }

                            if (curDur >= (short) 75 && curDur < (short) 90)
                            {
                                GChestCurDura = (short) 150;
                            }

                            if (curDur >= (short) 90 && curDur < (short) 112)
                            {
                                GChestCurDura = (short) 1;
                            }
                            
                            ArrayList<String> lore = new ArrayList<String>();

                            if (meta.hasLore())
                            {
                                lore = (ArrayList<String>) meta.getLore();
                            }

                            String asdf = ChatColor.GRAY + "Durability: " + "(" + GChestCurDura + "/" + GChestFullDura + ")";
                            lore.add(asdf);

                            meta.setLore(lore);
                            chest.setItemMeta(meta);
                        }
                        
                        
                    } // end gold chest check
                } // end chest null check
                
                if (target.getInventory().getLeggings() != null)
                {
                    legs = target.getInventory().getLeggings();
                    
                    if (legs.getTypeId() == 300) // leather pants check
                    {
                        ItemMeta meta = legs.getItemMeta();
                        Boolean containsDur = false;

                        if (meta.hasLore())
                        {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                            Iterator<String> it = lore.iterator();

                            String dur;
                            int i = 0;

                            while (it.hasNext())
                            {
                                dur = it.next();

                                if (dur.contains("Durability: "))
                                {
                                    containsDur = true;


                                    int start = dur.indexOf("(");
                                    int end = dur.indexOf("/");

                                    String durability = dur.substring(start + 1, end);

                                    if (Party.isShort(durability))
                                    {
                                        short newDur = Short.parseShort(durability);

                                        newDur = (short) (newDur - 1);

                                        durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + LLegsFullDura + ")";

                                        lore.set(i, durability);
                                        meta.setLore(lore);
                                        legs.setItemMeta(meta);

                                        if (newDur >= (short) 350)
                                        {
                                            legs.setDurability((short)5);
                                        }
                                        if (newDur > (short) 325 && newDur < (short) 350)
                                        {
                                            legs.setDurability((short)8);
                                        }

                                        if (newDur > (short) 275 && newDur < (short) 325)
                                        {
                                            legs.setDurability((short) 15);
                                        }

                                        if (newDur > (short) 235 && newDur < (short) 275)
                                        {
                                            legs.setDurability((short) 25);
                                        }

                                        if (newDur > (short) 190 && newDur < (short) 235)
                                        {
                                            legs.setDurability((short) 35);
                                        }

                                        if (newDur > (short) 150 && newDur < (short) 190)
                                        {
                                            legs.setDurability((short) 45);
                                        }

                                        if (newDur > (short) 115 && newDur < (short) 150)
                                        {
                                            legs.setDurability((short) 52);
                                        }
                                        
                                        if (newDur > (short) 75 && newDur < (short) 115)
                                        {
                                            legs.setDurability((short) 60);
                                        }
                                        
                                        if (newDur > (short) 50 && newDur < (short) 75)
                                        {
                                            legs.setDurability((short) 65);
                                        }
                                        
                                        if (newDur > (short) 30 && newDur < (short) 50)
                                        {
                                            legs.setDurability((short) 70);
                                        }
                                        
                                        if (newDur > (short) 15 && newDur < (short) 30)
                                        {
                                            legs.setDurability((short) 73);
                                        }
                                        
                                        if (newDur > (short) 0 && newDur < (short) 15)
                                        {
                                            legs.setDurability((short) 74);
                                        }

                                        if (newDur <= 0)
                                        {
                                            legs.setDurability((short) 75);
                                            target.getInventory().setLeggings(null);
                                        }

                                    }


                                }
                                i = i + 1;
                            } // end while
                            
                        } // end if meta
                        
                        if (containsDur == false)
                        {
                            short curDur = legs.getDurability();

                            if (curDur >= (short) 0 && curDur <= (short) 5)
                            {
                                LLegsCurDura = (short) 375;
                            }

                            if (curDur > (short) 5 && curDur < (short) 15)
                            {
                                LLegsCurDura = (short) 300;
                            }

                            if (curDur >= (short) 15 && curDur < (short) 30)
                            {
                                LLegsCurDura = (short) 250;
                            }

                            if (curDur >= (short) 30 && curDur < (short) 45)
                            {
                                LLegsCurDura = (short) 175;
                            }

                            if (curDur >= (short) 45 && curDur < (short) 55)
                            {
                                LLegsCurDura = (short) 100;
                            }

                            if (curDur >= (short) 55 && curDur < (short) 65)
                            {
                                LLegsCurDura = (short) 50;
                            }

                            if (curDur >= (short) 65 && curDur < (short) 70)
                            {
                                LLegsCurDura = (short) 25;
                            }

                            if (curDur >= (short) 70 && curDur < (short) 75)
                            {
                                LLegsCurDura = (short) 1;
                            }
                            
                            ArrayList<String> lore = new ArrayList<String>();

                            if (meta.hasLore())
                            {
                                lore = (ArrayList<String>) meta.getLore();
                            }

                            String asdf = ChatColor.GRAY + "Durability: " + "(" + LLegsCurDura + "/" + LLegsFullDura + ")";
                            lore.add(asdf);

                            meta.setLore(lore);
                            legs.setItemMeta(meta);
                        }
                        
                        
                    } // end leather leg check
                    
                    if (legs.getTypeId() == 316) // gold leg check
                    {
                        ItemMeta meta = legs.getItemMeta();
                        Boolean containsDur = false;

                        if (meta.hasLore())
                        {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                            Iterator<String> it = lore.iterator();

                            String dur;
                            int i = 0;

                            while (it.hasNext())
                            {
                                dur = it.next();

                                if (dur.contains("Durability: "))
                                {
                                    containsDur = true;


                                    int start = dur.indexOf("(");
                                    int end = dur.indexOf("/");

                                    String durability = dur.substring(start + 1, end);

                                    if (Party.isShort(durability))
                                    {
                                        short newDur = Short.parseShort(durability);

                                        newDur = (short) (newDur - 1);

                                        durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + GLegsFullDura + ")";

                                        lore.set(i, durability);
                                        meta.setLore(lore);
                                        legs.setItemMeta(meta);

                                        if (newDur >= (short) 550)
                                        {
                                            legs.setDurability((short)5);
                                        }
                                        if (newDur > (short) 500 && newDur < (short) 550)
                                        {
                                            legs.setDurability((short)10);
                                        }

                                        if (newDur > (short) 450 && newDur < (short) 500)
                                        {
                                            legs.setDurability((short) 20);
                                        }

                                        if (newDur > (short) 400 && newDur < (short) 450)
                                        {
                                            legs.setDurability((short) 30);
                                        }

                                        if (newDur > (short) 350 && newDur < (short) 400)
                                        {
                                            legs.setDurability((short) 40);
                                        }

                                        if (newDur > (short) 300 && newDur < (short) 350)
                                        {
                                            legs.setDurability((short) 50);
                                        }

                                        if (newDur > (short) 250 && newDur < (short) 300)
                                        {
                                            legs.setDurability((short) 60);
                                        }
                                        
                                        if (newDur > (short) 200 && newDur < (short) 250)
                                        {
                                            legs.setDurability((short) 70);
                                        }
                                        
                                        if (newDur > (short) 150 && newDur < (short) 200)
                                        {
                                            legs.setDurability((short) 80);
                                        }
                                        
                                        if (newDur > (short) 100 && newDur < (short) 150)
                                        {
                                            legs.setDurability((short) 90);
                                        }
                                        
                                        if (newDur > (short) 50 && newDur < (short) 100)
                                        {
                                            legs.setDurability((short) 100);
                                        }
                                        
                                        if (newDur > (short) 0 && newDur < (short) 50)
                                        {
                                            legs.setDurability((short) 110);
                                        }

                                        if (newDur <= 0)
                                        {
                                            legs.setDurability((short) 112);
                                            target.getInventory().setLeggings(null);
                                        }

                                    }


                                }
                                i = i + 1;
                            } // end while
                            
                        } // end if meta
                        
                        if (containsDur == false)
                        {
                            short curDur = legs.getDurability();

                            if (curDur >= (short) 0 && curDur <= (short) 5)
                            {
                                GLegsCurDura = (short) 550;
                            }

                            if (curDur > (short) 5 && curDur < (short) 15)
                            {
                                GLegsCurDura = (short) 500;
                            }

                            if (curDur >= (short) 15 && curDur < (short) 30)
                            {
                                GLegsCurDura = (short) 450;
                            }

                            if (curDur >= (short) 30 && curDur < (short) 45)
                            {
                                GLegsCurDura = (short) 400;
                            }

                            if (curDur >= (short) 45 && curDur < (short) 60)
                            {
                                GLegsCurDura = (short) 350;
                            }

                            if (curDur >= (short) 60 && curDur < (short) 75)
                            {
                                GLegsCurDura = (short) 275;
                            }

                            if (curDur >= (short) 75 && curDur < (short) 90)
                            {
                                GLegsCurDura = (short) 150;
                            }

                            if (curDur >= (short) 90 && curDur < (short) 112)
                            {
                                GLegsCurDura = (short) 1;
                            }
                            
                            ArrayList<String> lore = new ArrayList<String>();

                            if (meta.hasLore())
                            {
                                lore = (ArrayList<String>) meta.getLore();
                            }

                            String asdf = ChatColor.GRAY + "Durability: " + "(" + GLegsCurDura + "/" + GLegsFullDura + ")";
                            lore.add(asdf);

                            meta.setLore(lore);
                            legs.setItemMeta(meta);
                        }
                        
                        
                    } // end gold legs check
                } // end leg null check
                
                if (target.getInventory().getBoots() != null)
                {
                    boots = target.getInventory().getBoots();
                    
                    if (boots.getTypeId() == 301) // leather boots check
                    {
                        ItemMeta meta = boots.getItemMeta();
                        Boolean containsDur = false;

                        if (meta.hasLore())
                        {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                            Iterator<String> it = lore.iterator();

                            String dur;
                            int i = 0;

                            while (it.hasNext())
                            {
                                dur = it.next();

                                if (dur.contains("Durability: "))
                                {
                                    containsDur = true;


                                    int start = dur.indexOf("(");
                                    int end = dur.indexOf("/");

                                    String durability = dur.substring(start + 1, end);

                                    if (Party.isShort(durability))
                                    {
                                        short newDur = Short.parseShort(durability);

                                        newDur = (short) (newDur - 1);

                                        durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + LBootsFullDura + ")";

                                        lore.set(i, durability);
                                        meta.setLore(lore);
                                        boots.setItemMeta(meta);

                                        if (newDur >= (short) 225)
                                        {
                                            boots.setDurability((short)9);
                                        }
                                        if (newDur > (short) 175 && newDur < (short) 225)
                                        {
                                            boots.setDurability((short)15);
                                        }

                                        if (newDur > (short) 135 && newDur < (short) 175)
                                        {
                                            boots.setDurability((short) 25);
                                        }

                                        if (newDur > (short) 100 && newDur < (short) 135)
                                        {
                                            boots.setDurability((short) 35);
                                        }

                                        if (newDur > (short) 75 && newDur < (short) 100)
                                        {
                                            boots.setDurability((short) 45);
                                        }

                                        if (newDur > (short) 50 && newDur < (short) 75)
                                        {
                                            boots.setDurability((short) 55);
                                        }

                                        if (newDur > (short) 0 && newDur < (short) 50)
                                        {
                                            boots.setDurability((short) 63);
                                        }

                                        if (newDur <= 0)
                                        {
                                            boots.setDurability((short) 65);
                                            target.getInventory().setBoots(null);
                                        }

                                    }


                                }
                                i = i + 1;
                            } // end while
                            
                        } // end if meta
                        
                        if (containsDur == false)
                        {
                            short curDur = boots.getDurability();

                            if (curDur >= (short) 0 && curDur <= (short) 5)
                            {
                                LBootsCurDura = (short) 250;
                            }

                            if (curDur > (short) 5 && curDur < (short) 10)
                            {
                                LBootsCurDura = (short) 225;
                            }

                            if (curDur >= (short) 10 && curDur < (short) 17)
                            {
                                LBootsCurDura = (short) 175;
                            }

                            if (curDur >= (short) 17 && curDur < (short) 29)
                            {
                                LBootsCurDura = (short) 135;
                            }

                            if (curDur >= (short) 29 && curDur < (short) 41)
                            {
                                LBootsCurDura = (short) 100;
                            }

                            if (curDur >= (short) 41 && curDur < (short) 55)
                            {
                                LBootsCurDura = (short) 75;
                            }

                            if (curDur >= (short) 55 && curDur < (short) 60)
                            {
                                LBootsCurDura = (short) 50;
                            }

                            if (curDur >= (short) 60 && curDur < (short) 66)
                            {
                                LBootsCurDura = (short) 1;
                            }
                            
                            ArrayList<String> lore = new ArrayList<String>();

                            if (meta.hasLore())
                            {
                                lore = (ArrayList<String>) meta.getLore();
                            }

                            String asdf = ChatColor.GRAY + "Durability: " + "(" + LBootsCurDura + "/" + LBootsFullDura + ")";
                            lore.add(asdf);

                            meta.setLore(lore);
                            boots.setItemMeta(meta);
                        }
                        
                        
                    } // end leather boots check
                    
                    if (boots.getTypeId() == 317) // if gold
                    {
                        ItemMeta meta = boots.getItemMeta();
                        Boolean containsDur = false;

                        if (meta.hasLore())
                        {
                            ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                            Iterator<String> it = lore.iterator();

                            String dur;
                            int i = 0;

                            while (it.hasNext())
                            {
                                dur = it.next();

                                if (dur.contains("Durability: "))
                                {
                                    containsDur = true;

                                    int start = dur.indexOf("(");
                                    int end = dur.indexOf("/");

                                    String durability = dur.substring(start + 1, end);

                                    if (Party.isShort(durability))
                                    {
                                        short newDur = Short.parseShort(durability);

                                        newDur = (short) (newDur - 1);

                                        durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + GBootsFullDura + ")";

                                        lore.set(i, durability);
                                        meta.setLore(lore);
                                        boots.setItemMeta(meta);

                                        if (newDur >= (short) 375)
                                        {
                                            boots.setDurability((short)9);
                                        }
                                        if (newDur > (short) 325 && newDur < (short) 375)
                                        {
                                            boots.setDurability((short)17);
                                        }

                                        if (newDur > (short) 275 && newDur < (short) 325)
                                        {
                                            boots.setDurability((short) 29);
                                        }

                                        if (newDur > (short) 225 && newDur < (short) 275)
                                        {
                                            boots.setDurability((short) 39);
                                        }

                                        if (newDur > (short) 150 && newDur < (short) 225)
                                        {
                                            boots.setDurability((short) 51);
                                        }

                                        if (newDur > (short) 100 && newDur < (short) 150)
                                        {
                                            boots.setDurability((short) 62);
                                        }

                                        if (newDur > (short) 0 && newDur < (short) 100)
                                        {
                                            boots.setDurability((short) 77);
                                        }

                                        if (newDur <= 0)
                                        {
                                            boots.setDurability((short) 91);
                                            target.getInventory().setBoots(null);
                                        }

                                    }


                                }
                                i = i + 1;
                            } // end while
                            
                        } // end if meta
                        
                        if (containsDur == false)
                        {
                            short curDur = boots.getDurability();

                            if (curDur >= (short) 0 && curDur <= (short) 5)
                            {
                                GBootsCurDura = (short) 400;
                            }

                            if (curDur > (short) 5 && curDur < (short) 10)
                            {
                                GBootsCurDura = (short) 300;
                            }

                            if (curDur >= (short) 10 && curDur < (short) 17)
                            {
                                GBootsCurDura = (short) 250;
                            }

                            if (curDur >= (short) 17 && curDur < (short) 29)
                            {
                                GBootsCurDura = (short) 225;
                            }

                            if (curDur >= (short) 29 && curDur < (short) 41)
                            {
                                GBootsCurDura = (short) 175;
                            }

                            if (curDur >= (short) 41 && curDur < (short) 55)
                            {
                                GBootsCurDura = (short) 125;
                            }

                            if (curDur >= (short) 55 && curDur < (short) 66)
                            {
                                GBootsCurDura = (short) 125;
                            }

                            if (curDur >= (short) 66 && curDur < (short) 77)
                            {
                                GBootsCurDura = (short) 1;
                            }
                            
                            ArrayList<String> lore = new ArrayList<String>();

                            if (meta.hasLore())
                            {
                                lore = (ArrayList<String>) meta.getLore();
                            }

                            String asdf = ChatColor.GRAY + "Durability: " + "(" + GBootsCurDura + "/" + GBootsFullDura + ")";
                            lore.add(asdf);

                            meta.setLore(lore);
                            boots.setItemMeta(meta);
                        }
                        
                        
                    }
                }
                

            } // end perm check
        } // end event instanceof Player
        
        
        
        if (event.getDamager() instanceof Player)
        {
            Player attacker = (Player) event.getDamager();
            // Global damage reduction for time being.
            
            if (event.getEntity() instanceof Player)
            {
                World pworld = attacker.getWorld();

                BlockVector pt = BukkitUtil.toVector(attacker.getLocation().getBlock());

                RegionManager regionManager = worldGuard.getRegionManager(pworld);
                ApplicableRegionSet set = regionManager.getApplicableRegions(pt);

                if (!(set.allows(DefaultFlag.PVP)))
                {
                    //attacker.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "PvP is disabled in this area.");
                    return;
                }
            }

            double dmg = event.getDamage();
            double newdmg = dmg / 1.33;
            
            event.setDamage(newdmg);
            
            if (attacker.getItemInHand().getType() == Material.DIAMOND_PICKAXE || attacker.getItemInHand().getType() == Material.DIAMOND_AXE
                    || attacker.getItemInHand().getType() == Material.IRON_AXE || attacker.getItemInHand().getType() == Material.IRON_PICKAXE
                    || attacker.getItemInHand().getType() == Material.STONE_AXE || attacker.getItemInHand().getType() == Material.STONE_PICKAXE
                    || attacker.getItemInHand().getType() == Material.WOOD_AXE || attacker.getItemInHand().getType() == Material.WOOD_PICKAXE
                    || attacker.getItemInHand().getType() == Material.GOLD_AXE || attacker.getItemInHand().getType() == Material.GOLD_PICKAXE)
            {
                double dmg2 = event.getDamage();
                double newdmg2 = dmg / 1.33;
                event.setDamage(newdmg2);
            }
            
            // end dmg reduction
            
            // Paladin Gold Sword
            if (attacker.hasPermission("paladin.gold") || attacker.hasPermission("sr.goldsword"))
            {
                if (attacker.getItemInHand().getTypeId() == 283)
                {
                    ItemStack weap = (ItemStack) attacker.getItemInHand();

                    if (weap != null)
                    {
                        if(weap.getTypeId() == 283) // gold sword dura check
                        {
                            ItemMeta meta = weap.getItemMeta();
                            boolean containsDur = false;


                            if (meta.hasLore())
                            {
                                ArrayList<String> lore = (ArrayList<String>) meta.getLore();

                                Iterator<String> it = lore.iterator();

                                String dur;
                                int i = 0;

                                while (it.hasNext())
                                {
                                    dur = it.next();

                                    if (dur.contains("Durability: "))
                                    {
                                        containsDur = true;

                                        
                                        int start = dur.indexOf("(");
                                        int end = dur.indexOf("/");
                                                
                                        String durability = dur.substring(start + 1, end);
                                        
                                        if (Party.isShort(durability))
                                        {
                                            short newDur = Short.parseShort(durability);

                                            newDur = (short) (newDur - 1);

                                            durability = ChatColor.GRAY + "Durability: " + "(" + newDur + "/" + GSwordfullDura + ")";

                                            lore.set(i, durability);
                                            meta.setLore(lore);
                                            weap.setItemMeta(meta);

                                            if (newDur >= (short) 750)
                                            {
                                                weap.setDurability((short)5);
                                            }

                                            if (newDur > (short) 550 && newDur < (short) 750)
                                            {
                                                weap.setDurability((short) 10);
                                            }

                                            if (newDur > (short) 350 && newDur < (short) 550)
                                            {
                                                weap.setDurability((short) 15);
                                            }

                                            if (newDur > (short) 225 && newDur < (short) 350)
                                            {
                                                weap.setDurability((short) 20);
                                            }

                                            if (newDur > (short) 125 && newDur < (short) 225)
                                            {
                                                weap.setDurability((short) 25);
                                            }

                                            if (newDur > (short) 0 && newDur < (short) 125)
                                            {
                                                weap.setDurability((short) 29);
                                            }

                                            if (newDur <= 0)
                                            {
                                                weap.setDurability((short) 32);
                                                attacker.setItemInHand(null);
                                            }

                                        }
                                        

                                    }
                                    i = i + 1;
                                }
                            }
                            
                            if (containsDur == false)
                            {
                                short curDur = weap.getDurability();
                                
                                if (curDur >= (short) 0 && curDur <= (short) 5)
                                {
                                    GSwordcurDura = (short) 750;
                                }

                                if (curDur > (short) 5 && curDur < (short) 10)
                                {
                                    GSwordcurDura = (short) 550;
                                }

                                if (curDur >= (short) 10 && curDur < (short) 15)
                                {
                                    GSwordcurDura = (short) 350;
                                }

                                if (curDur >= (short) 15 && curDur < (short) 20)
                                {
                                    GSwordcurDura = (short) 225;
                                }

                                if (curDur >= (short) 20 && curDur < (short) 25)
                                {
                                    GSwordcurDura = (short) 125;
                                }

                                if (curDur >= (short) 25 && curDur < (short) 30)
                                {
                                    GSwordcurDura = (short) 1;
                                }

                                ArrayList<String> lore = new ArrayList<String>();
                                
                                if (meta.hasLore())
                                {
                                    lore = (ArrayList<String>) meta.getLore();
                                }
                                
                                String asdf = ChatColor.GRAY + "Durability: " + "(" + GSwordcurDura + "/" + GSwordfullDura + ")";
                                lore.add(asdf);
                                
                                meta.setLore(lore);
                                weap.setItemMeta(meta);
                            }
                        }
                        // end gold sword dura
                    }
                }
            } // end paladin perm check
            
            
        } // end event.getDamager player
        
        
    }
    
    
}
