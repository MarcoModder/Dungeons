package it.skyhash.git.dungeons;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.util.IdUtil;

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.skills.SkillTrigger;
import net.milkbowl.vault.economy.Economy;

public class ListenerGUI implements Listener {
	
	public static Economy economy = null;
	
	@EventHandler
    public void onClick(InventoryClickEvent e)
    {
        if(e.getInventory().equals(DungeonCMD.inv))
        {
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != null)
            {
                e.setCancelled(true);
                Player p = (Player) e.getWhoClicked();
                if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName())
                {
                    ItemStack is = e.getCurrentItem();
                    if(is.getType() == Material.ARROW)
                    {
                    	e.setCancelled(true);
                    }
                    else if(is.getType() == Material.STAINED_GLASS_PANE)
                    {
                    	e.setCancelled(true);
                    }
                    else if(is.getType() == Material.EMERALD)
                    {
                    	e.setCancelled(true);
                    }
                    else
                    {
                    	e.setCancelled(true);
                    	String s = is.getItemMeta().getDisplayName();
                    	s = s.replace("§a", "");
                    	//Backtrack to the object
                    	Dungeon d = Main.getMap().get(s);
                    	if(d.getGameStatus())
                    	{
                    		p.sendMessage(Utils.logo + "§cThis arena is already in-game!");
                			return;
                    	}
                    	else
                    	{
                        	if(setupEconomy())
                        	{
                        		double ec = economy.getBalance(p);
                        		if(ec < d.getCost())
                        		{
                        			p.sendMessage(Utils.logo + "§cYou don't have enough money!");
                                	p.closeInventory();
                        			return;
                        		}
                        		if(ec >= d.getCost())
                        		{
                        			economy.withdrawPlayer(p, d.getCost());
                        			d.setGameStatus(true);
                        			p.sendMessage(Utils.logo + "§cYour faction will be teleported to " + d.getName() + "!");
                                	p.closeInventory();
                        			teleport(p,d);
                        		}
                        	}
                    	}
                    }
                }
            }
        }
    }
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
 
        return (economy != null);
	}
	
	
	public void teleport(Player p,Dungeon d)
	{
		MPlayer mplayer = MPlayer.get(IdUtil.getId(p.getName()));
		World w = d.getWorld();
		//Get faction object
		Faction f = mplayer.getFaction();
		for(MPlayer mp : f.getMPlayers())
		{
			if(mp.isOnline())
			{
				if(mp.getPlayer().getWorld().equals(w))
				{
					mp.getPlayer().sendMessage(Utils.logo + "§dYou will be teleported to §b"+d.getName()+" §din §b5 §dseconds.");
					d.getLastLoc().put(mp.getPlayer(), mp.getPlayer().getLocation());
					d.addPlayerToList(mp.getPlayer());
					DungeonManager.addDungeon(mp.getPlayer(), d);
				}
			}
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable()
        {
          public void run()
          {
        	for(Player z : d.getPlayers())
      		{
      			z.teleport(d.getLocation());
      			z.sendMessage(Utils.logo + "§dYou have been successfully teleported to §b"+d.getName()+"!");
            	bossBar(z,d);
      		}
  			spawnMobs(d.getLocation(),d);
  			d.startTimer();
          }
        }, 100);
	}
	
	
	public void spawnMobs(Location loc,Dungeon d)
	{
		BukkitAPIHelper api = new BukkitAPIHelper();
		try {
			for(String s : d.getMobs())
			{
				MythicMob mob = api.getMythicMob(s);
				api.spawnMythicMob(s, loc);
				DungeonManager.getDungMobs().put(mob.getDisplayName(), d);
				DungeonManager.test.add(s);
				d.increaseMobsAlive();
			}
		} catch (InvalidMobTypeException e) {

		}
	}
	
	public void bossBar(Player p,Dungeon d)
	{
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			public void run() {
				if(DungeonManager.getDungeons().containsKey(p))
				{
					ActionBarAPI.sendActionBar(p, "§aTime left in arena§9: §b"+ Utils.convert(d.getTime()));
				}
			}
		}, 0L, 100L); //Repeating it every second
	}
	
	
	
	@EventHandler
	public void onJoin(PlayerDeathEvent e)
	{
		if(!(e.getEntity() instanceof Player))
		{
			return;
		}
		Player p = e.getEntity();
		if(DungeonManager.getDungeons().containsKey(p))
		{
			Dungeon d = DungeonManager.getDungeon(p);
			d.getPlayers().remove(p);
			DungeonManager.getDungeons().remove(p);
			p.sendMessage(Utils.logo+"§cYou died! You will get teleported back to this world spawn!");
			p.teleport(p.getWorld().getSpawnLocation());
		}
	}

	
	@EventHandler
	public void onMobDeath(EntityDeathEvent e)
	{
		String b = e.getEntity().getCustomName();
		try
		{
			b.length();
		}catch(java.lang.NullPointerException x)
		{
			return;
		}
		String a = e.getEntity().getCustomName();
		char[] c = a.toCharArray();
		for(int i=0;i<a.toCharArray().length;i++)
		{
			if(c[i] == '§')
			{
				c[i] = 'Z';
				c[i+1] = 'J';
			}
			if(c[i] == ' ')
			{
				c[i] = '9';
			}
		}
		String str = "";

		for (Character v : c)
		{
		    str += v.toString();
		}
		
		String finale = str.replaceAll("ZJ", "");
		String finalee = finale.replaceAll("9", "");
		
		if(DungeonManager.test.contains(finalee))
		{
			Dungeon d = DungeonManager.getDungeonFromLoc(e.getEntity().getCustomName());
			d.decreaseMobsAlive();
			if(d.getMobsAlive() == 0)
			{
				Utils.dungFinish(d);
			}
		}
	}
	
}
