package it.skyhash.git.dungeons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DungeonStorage {
	
	private final List<Dungeon> PROTECTED_DUNGEONS = new ArrayList();
	  
	  public void add(Dungeon s)
	  {
	    if (!this.PROTECTED_DUNGEONS.contains(s)) {
	      this.PROTECTED_DUNGEONS.add(s);
	    }
	  }
	  
	  public void remove(Dungeon s)
	  {
	    this.PROTECTED_DUNGEONS.remove(s);
	  }
	  
	  public void saveDungeons()
	    throws IOException
	  {
	    File file = new File(Main.getSignDataFolder() + File.separator + "dungeons.flat");
	    if (this.PROTECTED_DUNGEONS.isEmpty()) {
	      return;
	    }
	    FileWriter fstream = new FileWriter(file);
	    BufferedWriter out = new BufferedWriter(fstream);
	    for (Dungeon dungeon : this.PROTECTED_DUNGEONS)
	    {
	      out.write("[Dungeon Data]");
	      out.newLine();
	      String s = dungeon.getName();
	      out.write("Name:" + s);
	      out.newLine();
	      out.write("WorldLoc:" + dungeon.getLocation().getWorld().getName());
	      out.newLine();
	      out.write("XLoc:" + dungeon.getLocation().getBlockX());
	      out.newLine();
	      out.write("YLoc:" + dungeon.getLocation().getBlockY());
	      out.newLine();
	      out.write("ZLoc:" + dungeon.getLocation().getBlockZ());
	      out.newLine();
	      out.write("Cost:" + dungeon.getCost());
	      out.newLine();
	      out.write("IconType:" + dungeon.getIcon().getType());
	      out.newLine();
	      out.write("Time:" + dungeon.getTime());
	      out.newLine();
	      out.write("MoneyPrize:" + dungeon.getMoneyPrize());
	      out.newLine();
	      out.write("MobsNumber:" + dungeon.getMobs().size());
	      out.newLine();
	      for(String str : dungeon.getMobs())
	      {
	    	  out.write("Mob:" + str);
		      out.newLine(); 
	      }
	      out.write("ItemsPrizeNumber:" + dungeon.getPrizes().size());
	      out.newLine();
	      for(ItemStack i : dungeon.getPrizes())
	      {
	    	  out.write("Item:" + i.getType());
		      out.newLine(); 
	      }
	    }
	    out.close();
	  }
	  
	  public void loadHolo()
	    throws IOException
	  {
	    File file = new File(Main.getSignDataFolder() + File.separator + "dungeons.flat");
	    if (!file.exists()) {
	      return;
	    }
	    FileReader fstream = new FileReader(file);
	    BufferedReader reader = new BufferedReader(fstream);
	    String line = "";
	    while ((line = reader.readLine()) != null) {
	      if (line.startsWith("[Dungeon Data]"))
	      {
	        String Name = reader.readLine().split(":")[1];
	        String world = reader.readLine().split(":")[1];
	        int x = Integer.parseInt(reader.readLine().split(":")[1]);
	        int y = Integer.parseInt(reader.readLine().split(":")[1]);
	        int z = Integer.parseInt(reader.readLine().split(":")[1]);
	        int cost = Integer.parseInt(reader.readLine().split(":")[1]);
	        String icon = reader.readLine().split(":")[1];
	        ItemStack iconI = new ItemStack(Material.matchMaterial(icon));
	        int time = Integer.parseInt(reader.readLine().split(":")[1]);
	        int moneyPrize = Integer.parseInt(reader.readLine().split(":")[1]);
	        int mobsC = Integer.parseInt(reader.readLine().split(":")[1]);
	        ArrayList<String> linesText = new ArrayList();
	        for(int i = 0;i<mobsC;i++)
	        {
	        	String mob = reader.readLine().split(":")[1];
	        	linesText.add(mob);
	        }
	        int itemS = Integer.parseInt(reader.readLine().split(":")[1]);
	        Bukkit.broadcastMessage("Item counter: " + itemS);
	        ArrayList<ItemStack> items = new ArrayList();
	        for(int i = 0;i<itemS;i++)
	        {
	        	String item = reader.readLine().split(":")[1];
	        	Material material = Material.matchMaterial(item);
	        	ItemStack it = new ItemStack(material);
	        	items.add(it);
	        }
	        Location loc = new Location(Bukkit.getWorld(world),x,y,z);
	        Dungeon d = new Dungeon(loc, Name, cost, iconI, Bukkit.getWorld(world), time,moneyPrize,linesText,items);
	        add(d);
			Main.getMap().put(Name, d);
			Main.getDungeons().add(d);
			Main.getDungeonStorage().add(d);
	      }
	    }
	    reader.close();
	  }
	  
	  
	  public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		    for (Entry<T, E> entry : map.entrySet()) {
		        if (Objects.equals(value, entry.getValue())) {
		            return entry.getKey();
		        }
		    }
		    return null;
		}

}
