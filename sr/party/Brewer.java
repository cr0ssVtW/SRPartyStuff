package sr.party;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Acidsin
 */

/*
 * This class file also houses onBlockBreak + onBlockDamage events for RPG world dungeon stuff.
 * 
 */

public class Brewer implements Listener
{
    public Party plugin;
    public Brewer(Party plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) 
    {
        double random = Math.random();
        double random2 = Math.random();
        ItemStack potion;
        if (random < .7 || random2 < .9)
        {
            return;
        }
        if (e.getEntity() instanceof Zombie) 
        {
            Zombie b = (Zombie) e.getEntity();
            if(b.getKiller() != null) 
            {
                Player p = b.getKiller();
                if (p.hasPermission("brewer.brew"))
                {
                   Location loc = b.getLocation();
                   potion = new ItemStack(373, 1, (short) 8194);
                   b.getWorld().dropItemNaturally(loc, potion);
                }
           }
        }
        
        if (e.getEntity() instanceof Blaze) 
        {

            Blaze b = (Blaze) e.getEntity();
            if(b.getKiller() != null) 
            {
                Player p = b.getKiller();
                if (p.hasPermission("brewer.brew"))
                {
                   Location loc = b.getLocation();
                   potion = new ItemStack(373, 1, (short) 8195);
                   b.getWorld().dropItemNaturally(loc, potion);
                   p.sendMessage(ChatColor.GOLD + "Your Brewer powers cause a potion to drop!");
                }
           }
        }

        if (e.getEntity() instanceof Spider) 
        {

            Spider b = (Spider) e.getEntity();
            if(b.getKiller() != null) 
            {
                Player p = b.getKiller();
                if (p.hasPermission("brewer.brew"))
                {
                   Location loc = b.getLocation();
                   potion = new ItemStack(373, 1, (short) 8193);
                   b.getWorld().dropItemNaturally(loc, potion);
                   p.sendMessage(ChatColor.GOLD + "Your Brewer powers cause a potion to drop!");
                }
           }
        }
        
        if (e.getEntity() instanceof PigZombie) 
        {

            PigZombie b = (PigZombie) e.getEntity();
            if(b.getKiller() != null) 
            {
                Player p = b.getKiller();
                if (p.hasPermission("brewer.brew"))
                {
                   Location loc = b.getLocation();
                   potion = new ItemStack(373, 1, (short) 8197);
                   b.getWorld().dropItemNaturally(loc, potion);
                   p.sendMessage(ChatColor.GOLD + "Your Brewer powers cause a potion to drop!");
                }
           }
        }
        
        if (e.getEntity() instanceof Skeleton) 
        {

            Skeleton b = (Skeleton) e.getEntity();
            if(b.getKiller() != null) 
            {
                Player p = b.getKiller();
                if (p.hasPermission("brewer.brew"))
                {
                   Location loc = b.getLocation();
                   potion = new ItemStack(373, 1, (short) 8201);
                   b.getWorld().dropItemNaturally(loc, potion);
                   p.sendMessage(ChatColor.GOLD + "Your Brewer powers cause a potion to drop!");
                }
           }
        }
        
        if (e.getEntity() instanceof Creeper) {

            Creeper b = (Creeper) e.getEntity();
            if(b.getKiller() != null) {
                Player p = b.getKiller();
                if (p.hasPermission("brewer.brew"))
                {
                   Location loc = b.getLocation();
                   potion = new ItemStack(373, 1, (short) 8193);
                   b.getWorld().dropItemNaturally(loc, potion);
                   p.sendMessage(ChatColor.GOLD + "Your Brewer powers cause a potion to drop!");
                }
           }
        }
        
        if (e.getEntity() instanceof Silverfish) {

            Silverfish b = (Silverfish) e.getEntity();
            if(b.getKiller() != null) {
                Player p = b.getKiller();
                if (p.hasPermission("brewer.brew"))
                        {
                           Location loc = b.getLocation();
                           potion = new ItemStack(373, 1, (short) 16385);
                           b.getWorld().dropItemNaturally(loc, potion);
                           p.sendMessage(ChatColor.GOLD + "Your Brewer powers cause a potion to drop!");
                        }
           }
        }
        
        if (e.getEntity() instanceof CaveSpider) 
        {

            CaveSpider b = (CaveSpider) e.getEntity();
            if(b.getKiller() != null) 
            {
                Player p = b.getKiller();
                if (p.hasPermission("brewer.brew"))
                {
                   Location loc = b.getLocation();
                   potion = new ItemStack(373, 1, (short) 16393);
                   b.getWorld().dropItemNaturally(loc, potion);
                   p.sendMessage(ChatColor.GOLD + "Your Brewer powers cause a potion to drop!");
                }
           }
        }
    } // end entity death
    
    /*
     * Start RPG world dungeon stuff
     */
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();


        String world = event.getPlayer().getWorld().getName();
        if (world.contentEquals("rpg"))
        {

            if (event.getBlock().getTypeId() == 95)
            {

                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, 10));
            }
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event)
    {
        Player player = event.getPlayer();

        String world = event.getPlayer().getWorld().getName();
        if (world.contentEquals("rpg"))
        {

            if (event.getBlock().getTypeId() == 133)
            {

                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 10));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 4));
            }
        }
    }
    
    /*
     * END RPG world stuff
     * 
     */
}
