package sr.party;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Cross
 */
public class Duelist implements Listener
{

    
    public Party plugin;
    
    public Duelist(Party plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreProcess (PlayerCommandPreprocessEvent event)
    {
        String[] split = event.getMessage().split("\\s+");
        String cmd = split[0].substring(1).toLowerCase();
        Player player = event.getPlayer();
        
        if (plugin.paired.containsKey(player.getName()) || plugin.paired2.containsKey(player.getName()))
        {
            if (!(cmd.equalsIgnoreCase("spawn")))
            {
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You can only /spawn during a duel. Finish them!");
            }
        }
    }
    
    @EventHandler
    public void onWorldChange (PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        String pname = player.getName();
        
        
        World pvp = Bukkit.getWorld("pvp");
        
        
        
        if (! event.getFrom().equals(pvp))
        {
            return;
        }
        
        if (plugin.paired.containsKey(pname))
        {
            String winner = plugin.paired.get(pname);
            Player pwin = plugin.getServer().getPlayer(winner);
            
            // tleeport back to their old location
            
            Location winloc = plugin.playerloc.get(pwin.getName());
            
            pwin.teleport(winloc);
            
            plugin.playerloc.remove(pwin.getName());
            
            
            // win stuff
            
            int subtotal;
            double round = 0;
            if (plugin.money.containsKey(pwin.getName()))
            {
                subtotal = plugin.money.get(pwin.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(pwin.getName());
            }
            
            if (plugin.money.containsKey(player.getName()))
            {
                subtotal = plugin.money.get(player.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(player.getName());
            }
            
            
            Party.econ.depositPlayer(pwin.getName(), round);
            
            pwin.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You won the duel against " + ChatColor.GOLD + player.getName()
                    + ChatColor.GREEN +  " and have won " + ChatColor.GOLD + round + " gold!");
            
            if (pwin.hasPermission("sr.duelist.duel"))
            {
                double rand = Math.random();
                
                if (rand > 0.95)
                {
                    pwin.sendMessage(ChatColor.DARK_GRAY + "[SR " + ChatColor.AQUA + "For your victory, cr0ss has bestowed you with matched earnings, granting another " 
                            + ChatColor.GREEN + round + ChatColor.AQUA + " gold!");
                    Party.econ.depositPlayer(pwin.getName(), round);
                }
            }
            
            
            if (plugin.isDueling.contains(pwin.getName()))
            {
                plugin.isDueling.remove(pwin.getName());
            }
            
            if (plugin.isDueling.contains(pname))
            {
                plugin.isDueling.remove(pname);
            }
            
            if (plugin.queryDuel.contains(pwin.getName()))
            {
                plugin.queryDuel.remove(pwin.getName());
            }
            
            if (plugin.queryDuel.contains(pname))
            {
                plugin.queryDuel.remove(pname);
            }

            
            ///
            
            if (plugin.intArena.containsKey(pwin.getName()))
            {
                int arenanum = plugin.intArena.get(pwin.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
            
            if (plugin.intArena.containsKey(player.getName()))
            {
                int arenanum = plugin.intArena.get(player.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
                    
                    
            
            ///
            if (plugin.paired.containsKey(pwin.getName()))
            {
                plugin.paired.remove(pwin.getName());
            }
            
            if (plugin.paired2.containsKey(pwin.getName()))
            {
                plugin.paired2.remove(pwin.getName());
            }
            
            if (plugin.paired.containsKey(player.getName()))
            {
                plugin.paired.remove(player.getName());
            }
            
            if (plugin.paired2.containsKey(player.getName()))
            {
                plugin.paired2.remove(player.getName());
            }
            
            return;
        }
        
        
        //// 2
        
        
        if (plugin.paired2.containsKey(pname))
        {
            String winner = plugin.paired2.get(pname);
            
            Player pwin = plugin.getServer().getPlayer(winner);
            
            // tleeport back to their old location
            
            Location winloc = plugin.playerloc.get(pwin.getName());
            
            pwin.teleport(winloc);
            
            plugin.playerloc.remove(pwin.getName());
            
            // win stuff
            
            
            
            int subtotal;
            double round = 0;
            if (plugin.money.containsKey(pwin.getName()))
            {
                subtotal = plugin.money.get(pwin.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(pwin.getName());
            }
            
            if (plugin.money.containsKey(player.getName()))
            {
                subtotal = plugin.money.get(player.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(player.getName());
            }
            
            Party.econ.depositPlayer(pwin.getName(), round);
            
            pwin.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You won the duel against " + ChatColor.GOLD + player.getName()
                    + ChatColor.GREEN +  " and have won " + ChatColor.GOLD + round + " gold!");
            
            if (pwin.hasPermission("sr.duelist.duel"))
            {
                double rand = Math.random();
                
                if (rand > 0.95)
                {
                    pwin.sendMessage(ChatColor.DARK_GRAY + "[SR " + ChatColor.AQUA + "For your victory, cr0ss has bestowed you with matched earnings, granting another " 
                            + ChatColor.GREEN + round + ChatColor.AQUA + " gold!");
                    Party.econ.depositPlayer(pwin.getName(), round);
                }
            }
            
            if (plugin.isDueling.contains(pwin.getName()))
            {
                plugin.isDueling.remove(pwin.getName());
            }
            
            if (plugin.isDueling.contains(pname))
            {
                plugin.isDueling.remove(pname);
            }
            
            if (plugin.queryDuel.contains(pwin.getName()))
            {
                plugin.queryDuel.remove(pwin.getName());
            }
            
            if (plugin.queryDuel.contains(pname))
            {
                plugin.queryDuel.remove(pname);
            }

            
            ///
            
            if (plugin.intArena.containsKey(pwin.getName()))
            {
                int arenanum = plugin.intArena.get(pwin.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
            
            if (plugin.intArena.containsKey(player.getName()))
            {
                int arenanum = plugin.intArena.get(player.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
                    
                    
            
            ///
            if (plugin.paired.containsKey(pwin.getName()))
            {
                plugin.paired.remove(pwin.getName());
            }
            
            if (plugin.paired2.containsKey(pwin.getName()))
            {
                plugin.paired2.remove(pwin.getName());
            }
            
            if (plugin.paired.containsKey(player.getName()))
            {
                plugin.paired.remove(player.getName());
            }
            
            if (plugin.paired2.containsKey(player.getName()))
            {
                plugin.paired2.remove(player.getName());
            }
        }
      
    }
    
    
    
    
    
    
    
    
    @EventHandler
    public void onPlayerQuit (PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        
        String pname = player.getName();
        
        if (plugin.isDueling.contains(player.getName()) && (!(plugin.paired.containsKey(player.getName()))) && (!(plugin.paired2.containsKey(player.getName()))))
        {
            plugin.isDueling.remove(player.getName());
        }
        
        if (plugin.queryDuel.contains(player.getName()) && (!(plugin.paired.containsKey(player.getName()))) && (!(plugin.paired2.containsKey(player.getName()))))
        {
            plugin.queryDuel.remove(player.getName());
        }
        
        if (plugin.paired.containsKey(pname))
        {
            String winner = plugin.paired.get(pname);
            Player pwin = plugin.getServer().getPlayer(winner);
            

            
            // win stuff
            
            int subtotal;
            double round = 0;
            if (plugin.money.containsKey(pwin.getName()))
            {
                subtotal = plugin.money.get(pwin.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(pwin.getName());
            }
            
            if (plugin.money.containsKey(player.getName()))
            {
                subtotal = plugin.money.get(player.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(player.getName());
            }
            
            
            Party.econ.depositPlayer(pwin.getName(), round);
            
            pwin.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You won the duel against " + ChatColor.GOLD + player.getName()
                    + ChatColor.GREEN +  " and have won " + ChatColor.GOLD + round + " gold!");
            
            
            if (pwin.hasPermission("sr.duelist.duel"))
            {
                double rand = Math.random();
                
                if (rand > 0.95)
                {
                    pwin.sendMessage(ChatColor.DARK_GRAY + "[SR " + ChatColor.AQUA + "For your victory, cr0ss has bestowed you with matched earnings, granting another " 
                            + ChatColor.GREEN + round + ChatColor.AQUA + " gold!");
                    Party.econ.depositPlayer(pwin.getName(), round);
                }
            }
            
            if (plugin.isDueling.contains(pwin.getName()))
            {
                plugin.isDueling.remove(pwin.getName());
            }
            
            if (plugin.isDueling.contains(pname))
            {
                plugin.isDueling.remove(pname);
            }
            
            if (plugin.queryDuel.contains(pwin.getName()))
            {
                plugin.queryDuel.remove(pwin.getName());
            }
            
            if (plugin.queryDuel.contains(pname))
            {
                plugin.queryDuel.remove(pname);
            }

            ///
            
            if (plugin.intArena.containsKey(pwin.getName()))
            {
                int arenanum = plugin.intArena.get(pwin.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
            
            if (plugin.intArena.containsKey(player.getName()))
            {
                int arenanum = plugin.intArena.get(player.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
                    
                    
            
            ///
            if (plugin.paired.containsKey(pwin.getName()))
            {
                plugin.paired.remove(pwin.getName());
            }
            
            if (plugin.paired2.containsKey(pwin.getName()))
            {
                plugin.paired2.remove(pwin.getName());
            }
            
            if (plugin.paired.containsKey(player.getName()))
            {
                plugin.paired.remove(player.getName());
            }
            
            if (plugin.paired2.containsKey(player.getName()))
            {
                plugin.paired2.remove(player.getName());
            }
            
            
            // teleport back to their old location
            
            Location winloc = plugin.playerloc.get(pwin.getName());
            
            pwin.teleport(winloc);
            
            plugin.playerloc.remove(pwin.getName());
            
            return;
        }
        
        if (plugin.paired2.containsKey(pname))
        {
            String winner = plugin.paired2.get(pname);
            
            Player pwin = plugin.getServer().getPlayer(winner);

            // win stuff

            int subtotal;
            double round = 0;
            if (plugin.money.containsKey(pwin.getName()))
            {
                subtotal = plugin.money.get(pwin.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(pwin.getName());
            }
            
            if (plugin.money.containsKey(player.getName()))
            {
                subtotal = plugin.money.get(player.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(player.getName());
            }
            
            Party.econ.depositPlayer(pwin.getName(), round);
            
            pwin.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You won the duel against " + ChatColor.GOLD + player.getName()
                    + ChatColor.GREEN +  " and have won " + ChatColor.GOLD + round + " gold!");
            
            if (pwin.hasPermission("sr.duelist.duel"))
            {
                double rand = Math.random();
                
                if (rand > 0.95)
                {
                    pwin.sendMessage(ChatColor.DARK_GRAY + "[SR " + ChatColor.AQUA + "For your victory, cr0ss has bestowed you with matched earnings, granting another " 
                            + ChatColor.GREEN + round + ChatColor.AQUA + " gold!");
                    Party.econ.depositPlayer(pwin.getName(), round);
                }
            }
            
            if (plugin.isDueling.contains(pwin.getName()))
            {
                plugin.isDueling.remove(pwin.getName());
            }
            
            if (plugin.isDueling.contains(pname))
            {
                plugin.isDueling.remove(pname);
            }
            
            if (plugin.queryDuel.contains(pwin.getName()))
            {
                plugin.queryDuel.remove(pwin.getName());
            }
            
            if (plugin.queryDuel.contains(pname))
            {
                plugin.queryDuel.remove(pname);
            }
            
            ///
            
            if (plugin.intArena.containsKey(pwin.getName()))
            {
                int arenanum = plugin.intArena.get(pwin.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
            
            if (plugin.intArena.containsKey(player.getName()))
            {
                int arenanum = plugin.intArena.get(player.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
                    
                    
            
            ///
            
            
            if (plugin.paired.containsKey(pwin.getName()))
            {
                plugin.paired.remove(pwin.getName());
            }
            
            if (plugin.paired2.containsKey(pwin.getName()))
            {
                plugin.paired2.remove(pwin.getName());
            }
            
            if (plugin.paired.containsKey(player.getName()))
            {
                plugin.paired.remove(player.getName());
            }
            
            if (plugin.paired2.containsKey(player.getName()))
            {
                plugin.paired2.remove(player.getName());
            }
            
            // tleeport back to their old location
            
            Location winloc = plugin.playerloc.get(pwin.getName());
            
            pwin.teleport(winloc);
            
            plugin.playerloc.remove(pwin.getName());
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    @EventHandler
    public void onDeath (PlayerDeathEvent event)
    {
        final Player player = event.getEntity();
        
        String pname = player.getName();
        
        
        
        if (plugin.paired.containsKey(pname))
        {
            String winner = plugin.paired.get(pname);
            final Player pwin = plugin.getServer().getPlayer(winner);
            
            

            // win stuff
            
            pwin.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You have 10 seconds to collect your loot.");
            
            
            int subtotal;
            double round = 0;
            if (plugin.money.containsKey(pwin.getName()))
            {
                subtotal = plugin.money.get(pwin.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                
                plugin.money.remove(pwin.getName());
                 
            }
            
            if (plugin.money.containsKey(player.getName()))
            {
                subtotal = plugin.money.get(player.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                plugin.money.remove(player.getName());
                
            }
            
            
            Party.econ.depositPlayer(pwin.getName(), round);
            
            
            
            pwin.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You won the duel against " + ChatColor.GOLD + player.getName()
                    + ChatColor.GREEN +  " and have won " + ChatColor.GOLD + round + " gold!");
            
            if (pwin.hasPermission("sr.duelist.duel"))
            {
                double rand = Math.random();
                
                if (rand > 0.95)
                {
                    pwin.sendMessage(ChatColor.DARK_GRAY + "[SR " + ChatColor.AQUA + "For your victory, cr0ss has bestowed you with matched earnings, granting another " 
                            + ChatColor.GREEN + round + ChatColor.AQUA + " gold!");
                    Party.econ.depositPlayer(pwin.getName(), round);
                }
            }
            
            if (plugin.isDueling.contains(pwin.getName()))
            {
                plugin.isDueling.remove(pwin.getName());   
            }
            
            if (plugin.isDueling.contains(pname))
            {
                plugin.isDueling.remove(pname);
            }
            
            if (plugin.queryDuel.contains(pwin.getName()))
            {
                plugin.queryDuel.remove(pwin.getName());
            }
            
            if (plugin.queryDuel.contains(pname))
            {
                plugin.queryDuel.remove(pname);
            }
            
            ///
            
            if (plugin.intArena.containsKey(pwin.getName()))
            {
                int arenanum = plugin.intArena.get(pwin.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
            
            if (plugin.intArena.containsKey(player.getName()))
            {
                int arenanum = plugin.intArena.get(player.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
                    
                    
            
            ///
            
            
            if (plugin.paired.containsKey(pwin.getName()))
            {
                plugin.paired.remove(pwin.getName());
            }
            
            if (plugin.paired2.containsKey(pwin.getName()))
            {
                plugin.paired2.remove(pwin.getName());
            }
            
            if (plugin.paired.containsKey(player.getName()))
            {
                plugin.paired.remove(player.getName());
            }
            
            if (plugin.paired2.containsKey(player.getName()))
            {
                plugin.paired2.remove(player.getName());
            }
            
            // tleeport back to their old location
            
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
            {

                @Override
                public void run() 
                {
                    Location winloc = plugin.playerloc.get(pwin.getName());
                    
                    pwin.teleport(winloc);
                    
            
                    plugin.playerloc.remove(pwin.getName());
            
                }
            }, 400L);
            
            return;
        }
        
        if (plugin.paired2.containsKey(pname))
        {
            String winner = plugin.paired2.get(pname);
            
            final Player pwin = plugin.getServer().getPlayer(winner);

            
            // win stuff
            
            pwin.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.BLUE + "You have 10 seconds to collect your loot.");
            
            int subtotal;
            double round = 0;
            if (plugin.money.containsKey(pwin.getName()))
            {
                subtotal = plugin.money.get(pwin.getName());
                double winnings = (subtotal * 0.9);
                round = Math.round(winnings);
                plugin.money.remove(pwin.getName());
            }
            
            if (plugin.money.containsKey(player.getName()))
            {
                subtotal = plugin.money.get(player.getName());
                
                double winnings = (subtotal * 0.9);
                
                round = Math.round(winnings);
                plugin.money.remove(player.getName());
            }
            
            Party.econ.depositPlayer(pwin.getName(), round);
            
            pwin.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "You won the duel against " + ChatColor.GOLD + player.getName()
                    + ChatColor.GREEN +  " and have won " + ChatColor.GOLD + round + " gold!");
            
            if (pwin.hasPermission("sr.duelist.duel"))
            {
                double rand = Math.random();
                
                if (rand > 0.95)
                {
                    pwin.sendMessage(ChatColor.DARK_GRAY + "[SR " + ChatColor.AQUA + "For your victory, cr0ss has bestowed you with matched earnings, granting another " 
                            + ChatColor.GREEN + round + ChatColor.AQUA + " gold!");
                    Party.econ.depositPlayer(pwin.getName(), round);
                }
            }
            
            if (plugin.isDueling.contains(pwin.getName()))
            {
                plugin.isDueling.remove(pwin.getName());
            }
            
            if (plugin.isDueling.contains(pname))
            {
                plugin.isDueling.remove(pname);
            }
            
            if (plugin.queryDuel.contains(pwin.getName()))
            {
                plugin.queryDuel.remove(pwin.getName());
            }
            
            if (plugin.queryDuel.contains(pname))
            {
                plugin.queryDuel.remove(pname);
            }
            
            ///
            
            if (plugin.intArena.containsKey(pwin.getName()))
            {
                int arenanum = plugin.intArena.get(pwin.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
            
            if (plugin.intArena.containsKey(player.getName()))
            {
                int arenanum = plugin.intArena.get(player.getName());
                
                if (plugin.inArena.contains(arenanum))
                {
                    plugin.inArena.remove(arenanum);
                }
            }
                    
            ///
            
            if (plugin.paired.containsKey(pwin.getName()))
            {
                plugin.paired.remove(pwin.getName());
            }
            
            if (plugin.paired2.containsKey(pwin.getName()))
            {
                plugin.paired2.remove(pwin.getName());
            }
            
            if (plugin.paired.containsKey(player.getName()))
            {
                plugin.paired.remove(player.getName());
            }
            
            if (plugin.paired2.containsKey(player.getName()))
            {
                plugin.paired2.remove(player.getName());
            }
            
            // tleeport back to their old location
            
            
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
            {

                @Override
                public void run() 
                {
                    Location winloc = plugin.playerloc.get(pwin.getName());
                    
                    pwin.teleport(winloc);
            
                    plugin.playerloc.remove(pwin.getName());

                }
            }, 400L);
            
        }
    }
    
    
}
