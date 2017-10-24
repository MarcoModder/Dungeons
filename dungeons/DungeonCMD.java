package it.skyhash.git.dungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.util.IdUtil;

import net.milkbowl.vault.economy.Economy;

public class DungeonCMD implements CommandExecutor {
	
	public static Inventory inv;
	public static Economy economy = null;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
		{
			return true;
		}
		Player p = (Player) sender;
		MPlayer mplayer;
		//Get the f player
		String uuidString = p.getUniqueId().toString();
		UUID uuid = p.getUniqueId();
		CommandSender commandSender = Bukkit.getConsoleSender();
		
		mplayer = MPlayer.get(uuidString);
		mplayer = MPlayer.get(uuid);
		mplayer = MPlayer.get(commandSender);
		mplayer = MPlayer.get(p);
		
		mplayer = MPlayer.get(IdUtil.getId(p.getName()));
		
		//Get faction object
		Faction f = mplayer.getFaction();
		
		if(args.length == 0)
		{
			try
			{
				if(!f.getLeader().equals(mplayer))
				{
					p.sendMessage(Utils.logo + "§cYou must be the leader in order to do this command!");
					return true;
				}
				// GUI
				inv = Bukkit.createInventory(p, 27, "§bDungeons List / " + p.getWorld().getName());
				ItemStack buffer = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 15);
				ItemMeta bufferMeta = buffer.getItemMeta();
				bufferMeta.setDisplayName("§8Buffer");
				buffer.setItemMeta(bufferMeta);
				ItemStack em = new ItemStack(Material.EMERALD);
				ItemMeta emeta = em.getItemMeta();
				if(setupEconomy())
				{
			        double balance = economy.getBalance(p);
			        emeta.setDisplayName("§aBalance: " + balance);
			        em.setItemMeta(emeta);
				}
				inv.setItem(22, em);
				ItemStack arrow = new ItemStack(Material.ARROW);
				inv.setItem(26, arrow);
				inv.setItem(18, arrow);
				inv.setItem(25, buffer);
				inv.setItem(24, buffer);
				inv.setItem(23, buffer);
				inv.setItem(21, buffer);
				inv.setItem(20, buffer);
				inv.setItem(19, buffer);
				ItemStack red = new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 14);
				ItemMeta redMeta = red.getItemMeta();
				redMeta.setDisplayName("§cWork in Progress!");
				red.setItemMeta(redMeta);
				int count = 0;
				for(int i=0;i<Main.getDungeons().size();i++)
				{
					ItemStack is = Main.getDungeons().get(i).getGuiItem();
					if(Main.getDungeons().get(i).getLocation().getWorld().equals(p.getWorld()))
					{
						is = Main.getDungeons().get(i).getGuiItem();
						List<String> lores = new ArrayList<>();
						String ingame = "§fStatus: §cIn-Game";
						String free = "§7Status: §aFree";
						String cost = "§7Cost: §6" + Main.getDungeons().get(i).getCost();
						if(Main.getDungeons().get(i).getGameStatus())
						{
							lores.add(ingame);
						}
						else
						{
							lores.add(free);
						}
						lores.add(cost);
						ItemMeta im = is.getItemMeta();
						im.setLore(lores);
						is.setItemMeta(im);
						is = Utils.addGlow(is);
						inv.setItem(count, is);
						count++;
					}
				}
				for(int i=0;i<27;i++)
				{
					if(inv.getItem(i) == null)
					{
						inv.setItem(i, red);
					}
				}
				p.openInventory(inv);
				return true;
			}catch(java.lang.NullPointerException ex)
			{
				p.sendMessage(Utils.logo + "§cYou must be in a faction in order to do this command!");
				return true;
			}
		}
		
		if(args[0].equalsIgnoreCase("create"))
		{
			if(!p.hasPermission("dungeon.create"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			if(args.length == 4)
			{
				String name = args[1];
				if(Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo + "§cThis dungeon already exists");
					return true;
				}
				int cost = 0;
				try
				{
					cost = Integer.parseInt(args[2]);
					
				}catch(java.lang.NumberFormatException x)
				{
					p.sendMessage(Utils.logo + "§cCost must be a number!");
					return true;
				}
				ItemStack item = p.getItemInHand();
				if(item.getType() == Material.AIR)
				{
					p.sendMessage(Utils.logo + "§cYou have no item in your hand! Icon can't be air!");
					return true;
				}
				int costz = 0;
				try
				{
					costz = Integer.parseInt(args[3]);
					
				}catch(java.lang.NumberFormatException x)
				{
					p.sendMessage(Utils.logo + "§cTime must be a number!");
					return true;
				}
				Dungeon d = new Dungeon(p.getLocation(), name, cost, item, p.getLocation().getWorld(),costz);
				Main.getMap().put(name, d);
				Main.getDungeons().add(d);
				Main.getDungeonStorage().add(d);
				p.sendMessage(Utils.logo+"§aSuccesfully created " + name + " dungeon!");
			}
			else
			{
				p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon create <NAME> <COST> <TIME>");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("delete"))
		{
			if(!p.hasPermission("dungeon.delete"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			if(args.length==2)
			{
				String name = args[1];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				Main.getDungeons().remove(d);
				Main.getMap().remove(name);
				p.sendMessage(Utils.logo+"§aSuccesfully deleted " + name + " dungeon!");
				return true;
			}
			else
			{
				p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon delete <NAME>");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("add"))
		{
			if(!p.hasPermission("dungeon.add"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			if(args.length == 3)
			{
				String name = args[1];
				String mob = args[2];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				d.getMobs().add(mob);
				p.sendMessage(Utils.logo+"§aSuccesfully added " + mob + " mob to your dungeon!");
				return true;
			}
			else
			{
				p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon add <ARENA NAME> <MOB NAME>");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("prizeadd"))
		{
			if(!p.hasPermission("dungeon.prizeadd"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			if(args.length == 3)
			{
				String name = args[1];
				String option = args[2];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				if(option.equalsIgnoreCase("item"))
				{
					ItemStack i = p.getItemInHand();
					if(i.getType() == Material.AIR)
					{
						p.sendMessage(Utils.logo+"§cYou can't set air item as prize!");
						return true;
					}
					d.getPrizes().add(i);
					p.sendMessage(Utils.logo+"§aSuccesfully set §b" + i.getType().toString() + "§a as item prize in §b" + d.getName() + "§a!");
					return true;
				}
				else
				{
					p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon prizeadd <ARENA NAME> <item/money>");
					return true;
				}
			}
			else if(args.length == 4)
			{
				String name = args[1];
				String option = args[2];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				if(option.equalsIgnoreCase("money"))
				{
					try
					{
						int money = Integer.parseInt(args[3]);
						d.setMoneyPrize(d.getMoneyPrize() + money);
						p.sendMessage(Utils.logo+"§aSuccesfully added §b" + money + " as money prize of §b" + d.getName() + "§a!");
						return true;
					}catch(java.lang.NumberFormatException x)
					{
						p.sendMessage(Utils.logo+"§cMoney Prize must be a number!");
						return true;
					}
				}
				else
				{
					p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon prizeadd <ARENA NAME> <item/money> <Number>");
					return true;
				}
			}
			else
			{
				p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon prizeadd <ARENA NAME> <item/money> <number or item>");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("remove"))
		{
			if(!p.hasPermission("dungeon.remove"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			if(args.length == 3)
			{
				String name = args[1];
				String mob = args[2];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				if(!d.getMobs().contains(mob))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not contain this mob!");
					return true;
				}
				d.getMobs().remove(mob);
				p.sendMessage(Utils.logo+"§aSuccesfully removed " + mob + " mob to your dungeon!");
				return true;
			}
			else
			{
				p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon remove <ARENA NAME> <MOB NAME>");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("help"))
		{
			if(!p.hasPermission("dungeon.help"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			else
			{
				p.sendMessage(Utils.logo+"§bDungeon Commands:");
				p.sendMessage("§7§m-------------------------------");
				p.sendMessage("§9/dungeon create §7<§3NAME§7> §7<§3COST§7> §7<§3TIME§7>"); //DONE
				p.sendMessage("§9/dungeon delete §7<§3NAME§7>"); // DONE
				p.sendMessage("§9/dungeon add §7<§3ARENA NAME§7> §7<§3MOB NAME§7>"); // DONE
				p.sendMessage("§9/dungeon remove §7<§3ARENA NAME§7§7> §7<§3MOB NAME§7>"); // DONE
				p.sendMessage("§9/dungeon list §7<§3ARENA NAME§7>"); // DONE
				p.sendMessage("§9/dungeon prizeadd §3§7<§3ARENA NAME§7> §3§7<§3ITEM/MONEY§7>"); // DONE
				p.sendMessage("§9/dungeon prizeremove §3§7<§3ARENA NAME§7> §3§7<§3PRIZE/ITEM§7>"); // TO FINISH
				p.sendMessage("§9/dungeon prizelist §3§7<§3ARENA NAME§7>"); // DONE
				p.sendMessage("§7§m-------------------------------");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("prizeremove"))
		{
			if(!p.hasPermission("dungeon.prizeremove"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			if(args.length == 3)
			{
				String name = args[1];
				String option = args[2];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				if(option.equalsIgnoreCase("item"))
				{
					ItemStack i = p.getItemInHand();
					if(i.getType() == Material.AIR)
					{
						p.sendMessage(Utils.logo+"§cYou can't remove air item as prize!");
						return true;
					}
					d.getPrizes().remove(i);
					p.sendMessage(Utils.logo+"§aSuccesfully remove §b" + i.getType().toString() + " §aas item prize in §b" + d.getName() + "§a!");
					return true;
				}
				else
				{
					p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon prizeremove <ARENA NAME> <item/money>");
					return true;
				}
			}
			else if(args.length == 4)
			{
				String name = args[1];
				String option = args[2];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				if(option.equalsIgnoreCase("money"))
				{
					try
					{
						int money = Integer.parseInt(args[3]);
						d.setMoneyPrize(d.getMoneyPrize() - money);
						p.sendMessage(Utils.logo+"§aSuccesfully remove §b" + money + " as money prize of §b" + d.getName() + "§a!");
						return true;
					}catch(java.lang.NumberFormatException x)
					{
						p.sendMessage(Utils.logo+"§cMoney Prize must be a number!");
						return true;
					}
				}
				else
				{
					p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon prizeremove <ARENA NAME> <item/money> <Number>");
					return true;
				}
			}
			else
			{
				p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon prizeremove <ARENA NAME> <item/money> <number or item>");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("prizelist"))
		{
			if(!p.hasPermission("dungeon.prizelist"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			if(args.length == 2)
			{
				String name = args[1];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				p.sendMessage(Utils.logo+"§bCurrent prizes in " + d.getName() +":");
				p.sendMessage("§7§m-------------------------------");
				int count = 0;
				for(ItemStack s : d.getPrizes())
				{
					count++;
					p.sendMessage("§3" + count + "§7: §b" + s.getType().toString());
				}
				if(count == 0)
				{
					p.sendMessage("§cThere are no item prizes!");
				}
				p.sendMessage("");
				p.sendMessage("§6Current money prize: §a" + d.getMoneyPrize());
				p.sendMessage("");
				p.sendMessage("§7§m-------------------------------");
				return true;
			}
			else
			{
				p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon prizelist <ARENA NAME>");
				return true;
			}		
		}
		else if(args[0].equalsIgnoreCase("list"))
		{
			if(!p.hasPermission("dungeon.list"))
			{
				p.sendMessage(Utils.logo + "§cNo permissions!");
				return true;
			}
			if(args.length == 2)
			{
				String name = args[1];
				if(!Main.getMap().containsKey(name))
				{
					p.sendMessage(Utils.logo+"§cThis arena does not exist!");
					return true;
				}
				Dungeon d = Main.getMap().get(name);
				p.sendMessage(Utils.logo+"§bCurrent mobs in " + d.getName() +":");
				p.sendMessage("§7§m-------------------------------");
				int count = 0;
				for(String s : d.getMobs())
				{
					count++;
					p.sendMessage("§3" + count + "§7: §b" + s);
				}
				if(count == 0)
				{
					p.sendMessage("§cThere are no mobs!");
				}
				p.sendMessage("§7§m-------------------------------");
				return true;
			}
			else
			{
				p.sendMessage(Utils.logo+"§cCorrect usage: /dungeon list <ARENA NAME>");
				return true;
			}
		}
		else
		{
			p.sendMessage(Utils.logo + "§cWrong syntax!");
			return true;
		}
		return true;
	}
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
 
        return (economy != null);
	}

}
