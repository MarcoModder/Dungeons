package it.skyhash.git.dungeons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class Utils {
	
	public static String logo;
	public static Economy economy = null;
	
	
	public static ItemStack addGlow(ItemStack item){
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
	
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	public static String convert(int seconds) {
		Date d = new Date(seconds * 1000L);
		SimpleDateFormat df = new SimpleDateFormat("mm:ss"); // HH for 0-23
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String time = df.format(d);
		return time;
    }
	
	public static void dungFinish(Dungeon d)
	{
		for(Player p : d.getPlayers())
		{
			p.sendMessage(Utils.logo + "§bCongratulations you have completed §d"+d.getName());
			p.sendMessage(Utils.logo + "§cYou will be teleported to your last location!");
			p.teleport(d.getLastLoc().get(p));
			for(ItemStack i : d.getPrizes())
			{
				p.getInventory().addItem(i);
			}
			if(setupEconomy())
        	{
				economy.depositPlayer(p,d.getMoneyPrize());
        	}
			p.playSound(d.getLastLoc().get(p), Sound.ENDERDRAGON_GROWL, 1, 10);	
			d = DungeonManager.getDungeon(p);
			DungeonManager.getDungeons().remove(p);
		}
		d.setGameStatus(false);
		d.removePlayers();
		//Mobs
		Entity[] ent = d.getLocation().getChunk().getEntities();
		for(Entity e : ent)
		{
			Location loc = new Location(e.getWorld(),e.getLocation().getX(),e.getLocation().getY()-200,e.getLocation().getZ());
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getUniqueId() != e.getUniqueId())
				{
					e.teleport(loc);
				}
			}
		}		
	}
	
	private static boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
 
        return (economy != null);
	}

}
