package it.skyhash.git.dungeons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public static Main instance;
	public static Map<String,Dungeon> dungeonMap;
	public static List<Dungeon> dungeons;
	private static File DUNG_DATA_FOLDER;
    private static DungeonStorage storage;
	
	public static Main getInstance()
	{
		return instance;
	}
	
	@Override
	public void onEnable()
	{
		System.out.println("[SkyHash] Loading Dungeons...");
		instance = this;
		Events();
		Commands();
		dungeonMap = new HashMap();
		dungeons = new ArrayList();
		DUNG_DATA_FOLDER = new File(getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator);
		storage = new DungeonStorage();
		runLoad();
		System.out.println("[SkyHash] Succesfully loaded Dungeons!");
	}
	
	@Override
	public void onDisable()
	{
		try
	    {
	      storage.saveDungeons();
	    }
	    catch (IOException localIOException) {
	    	System.out.println("There was an error saving dungeons!");
		}
		instance = null;
	}
	
	private void Events()
	{
		registerEvent(new ListenerGUI());
	}
	
	public static Map<String,Dungeon> getMap()
	{
		return dungeonMap;
	}
	
	public static List<Dungeon> getDungeons()
	{
		return dungeons;
	}
	
	private void registerEvent(Listener listener)
	{
	   Bukkit.getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public void Commands()
	{
		getCommand("dg").setExecutor(new DungeonCMD());
	}
	
	public void runLoad()
	{
		if (!DUNG_DATA_FOLDER.exists())
		{
			if (DUNG_DATA_FOLDER.mkdirs()) 
			{
				getLogger().info("[SkyHash]Created dungeons data folder.");
			}
		}
		else 
		{
			try
			{
				storage.loadHolo();
			}catch (IOException localIOException) {}
		}
	}
	
	public static DungeonStorage getDungeonStorage()
	{
	    return storage;
	}
	
	public static File getSignDataFolder()
	{
	    return DUNG_DATA_FOLDER;
	}

}
