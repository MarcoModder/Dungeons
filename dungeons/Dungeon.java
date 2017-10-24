package it.skyhash.git.dungeons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class Dungeon {
	
	
	private Location location;
	private String name;
	private int cost;
	private ItemStack icon;
	private World world;
	private boolean inGame;
	private ArrayList<Player> playerInDungeon;
	private Map<Player,Location> playerLastLocation;
	private List<String> mobs;
	private ArrayList<ItemStack> prizes;
	private int duration;
	private int cont;
	private int moneyPrize;
	private int mobsAlive;
	private int taskId;
	private Dungeon dung;
	
	
	public Dungeon(Location loc,String s,int money,ItemStack ic,World w,int sec)
	{
		this.location = loc;
		this.name = s;
		this.cost = money;
		this.icon = ic;
		this.world = w;
		this.inGame = false;
		this.playerInDungeon = new ArrayList();
		this.mobs = new ArrayList();
		this.prizes = new ArrayList();
		this.playerLastLocation = new HashMap();
		this.duration = sec;
		this.cont = sec;
		this.moneyPrize = 0;
		this.mobsAlive = 0;
		this.dung = this;
	}
	
	public Dungeon(Location loc,String s,int money,ItemStack ic,World w,int sec,int moneyPrizez,ArrayList<String> mobsz,ArrayList<ItemStack> is)
	{
		this.location = loc;
		this.name = s;
		this.cost = money;
		this.icon = ic;
		this.world = w;
		this.inGame = false;
		this.playerInDungeon = new ArrayList();
		this.mobs = new ArrayList();
		this.prizes = new ArrayList();
		this.mobs = mobsz;
		this.prizes = is;
		this.playerLastLocation = new HashMap();
		this.duration = sec;
		this.cont = sec;
		this.moneyPrize = moneyPrizez;
		this.mobsAlive = 0;
		this.dung = this;
	}
	
	public int getMobsAlive()
	{
		return this.mobsAlive;
	}
	
	public void decreaseMobsAlive()
	{
		this.mobsAlive--;
	}
	
	public void increaseMobsAlive()
	{
		this.mobsAlive++;
	}
	
	public int getMoneyPrize()
	{
		return this.moneyPrize;
	}
	
	public void setMoneyPrize(int prize)
	{
		this.moneyPrize = prize;
	}
	public Map<Player,Location> getLastLoc()
	{
		return this.playerLastLocation;
	}
	
	public ArrayList<Player> getPlayers()
	{
		return this.playerInDungeon;
	}
	
	public int getTime()
	{
		return this.duration;
	}
	
	public List<String> getMobs()
	{
		return this.mobs;
	}
	
	public ArrayList<ItemStack> getPrizes()
	{
		return this.prizes;
	}
	
	public void addPlayerToList(Player p)
	{
		this.playerInDungeon.add(p);
	}
	
	
	public void setGameStatus(boolean b)
	{
		this.inGame = b;
	}
	
	public boolean getGameStatus()
	{
		return this.inGame;
	}
	
	public int getCost()
	{
		return this.cost;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Location getLocation()
	{
		return this.location;
	}
	
	public ItemStack getIcon()
	{
		return this.icon;
	}
	
	public World getWorld()
	{
		return this.world;
	}
	
	public ItemStack getGuiItem()
	{
		ItemMeta itemMeta = this.icon.getItemMeta();
		itemMeta.setDisplayName("§a" + this.name);
		this.icon.setItemMeta(itemMeta);
		return this.icon;
	}
	
	public void startTimer()
	{
		this.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			public void run() {
				if(duration == 0)
				{
					Bukkit.getScheduler().cancelAllTasks();
					Utils.dungFinish(dung);
					duration = cont;
					inGame = false;
				}
				duration--;
			}
		}, 0L, 20L); //Repeating it every second
	}
	
	public void stopTimer()
	{
		Bukkit.getScheduler().cancelTask(this.taskId);
		this.duration = this.cont;
		this.inGame = false;
	}
	
	public void removePlayers()
	{
		playerInDungeon.clear();
	}

}
