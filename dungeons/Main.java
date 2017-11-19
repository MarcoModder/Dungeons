package it.skyhash.git.dungeons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import it.skyhash.git.dungeons.FactionsUUID.DungeonCMDD;
import it.skyhash.git.dungeons.FactionsUUID.ListenerUUID;

public class Main
  extends JavaPlugin
{
  public static Main instance;
  public static Map<String, Dungeon> dungeonMap;
  public static List<Dungeon> dungeons;
  private static File DUNG_DATA_FOLDER;
  private static DungeonStorage storage;
  
  public static Main getInstance()
  {
    return instance;
  }
  
  public void onEnable()
  {
    System.out.println("[SkyHash] Loading Dungeons...");
    instance = this;
    checkVersions();
    config();
    dungeonMap = new HashMap();
    dungeons = new ArrayList();
    DUNG_DATA_FOLDER = new File(getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator);
    storage = new DungeonStorage();
    runLoad();
    Utils.logo = getInstance().getConfig().getString("Messages.Logo");
    System.out.println("[SkyHash] Succesfully loaded Dungeons!");
  }
  
  public void onDisable()
  {
    try
    {
      storage.saveDungeons();
    }
    catch (IOException localIOException)
    {
      System.out.println("There was an error saving dungeons!");
    }
    instance = null;
  }
  
  public static Map<String, Dungeon> getMap()
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
  public void runLoad()
  {
    if (!DUNG_DATA_FOLDER.exists())
    {
      if (DUNG_DATA_FOLDER.mkdirs()) {
        getLogger().info("[SkyHash]Created dungeons data folder.");
      }
    }
    else {
      try
      {
        storage.loadHolo();
      }
      catch (IOException localIOException) {}
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
  
  public void config()
  {
    getConfig().addDefault("Messages.Logo", "§7[§bDungeons§7]");
    getConfig().options().copyDefaults(true);
    saveConfig();
  }
  
  public void checkVersions()
  {
	 boolean is = false;
	 for(Plugin p : getServer().getPluginManager().getPlugins())
	 {
		 if(p.getDescription().getMain().equalsIgnoreCase("com.massivecraft.factions.P"))
		 {
			 System.out.println("[SkyHash] FactionsUUID Detected!");
			 getCommand("dg").setExecutor(new DungeonCMDD());
			 registerEvent(new ListenerUUID());
			 is = true;
			 
		 }
		 else if(p.getDescription().getMain().equalsIgnoreCase("com.massivecraft.factions.Factions"))
		 {
			 System.out.println("[SkyHash] Factions Detected!");
			 getCommand("dg").setExecutor(new DungeonCMD());
			 registerEvent(new ListenerGUI());
			 is = true;
		 }
		 //MassiveFaction : com.massivecraft.factions.Factions
		 //FactionsUUID : com.massivecraft.factions.P
	 }
	 if(!is)
	 {
		System.out.println("[SkyHash] No factions plugin was detected!"); 
	 }
  }
  
  
  
}
