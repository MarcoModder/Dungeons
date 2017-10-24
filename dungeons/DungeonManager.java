package it.skyhash.git.dungeons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;


public class DungeonManager {
	

	private static Map<Player,Dungeon> instance = new HashMap();
	private static Map<String,Dungeon> mobs = new HashMap();
	public static List<String> test = new ArrayList();
	
	
	
	public static void addDungeon(Player pl,Dungeon dun)
	{
		instance.put(pl, dun);
	}
	
	public static void addDungeonMob(String pl,Dungeon dun)
	{
		mobs.put(pl, dun);
	}
	
	public static Dungeon getDungeonFromLoc(String p)
	{
		return mobs.get(p);
	}
	
	
	public static Map<Player,Dungeon> getDungeons()
	{
		return instance;
	}
	
	public static Map<String,Dungeon> getDungMobs()
	{
		return mobs;
	}	
	
	public static Dungeon getDungeon(Player p)
	{
		return instance.get(p);
	}
	
	public static Player getPlayer(Dungeon d)
	{
		return Utils.getKeyByValue(instance, d);
	}

}
