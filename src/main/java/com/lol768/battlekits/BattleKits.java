package com.lol768.battlekits;

import com.lol768.battlekits.listeners.RespawnKit;
import com.lol768.battlekits.listeners.SignHandler;
import com.lol768.battlekits.utilities.BukkitMetrics;
import com.lol768.battlekits.utilities.Updater;
import com.lol768.battlekits.utilities.WebHandler;
import com.lol768.battlekits.utilities.ConfigAccessor;
import com.lol768.battlekits.utilities.PM;
import com.lol768.battlekits.listeners.RestrictionEvents;
import com.lol768.battlekits.listeners.PlayerReward;
import com.lol768.battlekits.listeners.TagHandler;
import com.lol768.battlekits.listeners.InstaSoup;
import com.lol768.battlekits.listeners.DeathEvent;
import com.lol768.battlekits.commands.CommandRefillAll;
import com.lol768.battlekits.commands.CommandKitCreation;
import com.lol768.battlekits.commands.CommandBattleKits;
import com.lol768.battlekits.commands.CommandSoup;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class BattleKits extends JavaPlugin {
	
	public static net.milkbowl.vault.economy.Economy economy = null;
	public String html = "Error";
	public HashSet<String> death = new HashSet<String>();
	public HashMap<String, String> tags = new HashMap<String, String>(); //Name, prefix (colour codes)
	public CommandBattleKits cbk = new CommandBattleKits(this);
	public boolean useTags = false;
	public PM PM = new PM(this);
	//Configuration stuff
    public ConfigAccessor global;
    public ConfigAccessor kits;
    public ConfigAccessor kitHistory;
    public WebHandler wh;
    Connection connection = null;

	public boolean setupEconomy() {
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null); 
	}
	
	/**
	 * Multi-world config accessor
	 * @param String path - The setting path to look for (e.g. settings.disable-xp)
	 * @param Player p - Player to get world from
	 * @param Object defaultValue - If empty, use this value
	 * @return Object - result
	 */
	public Object checkSetting(String path, Player p, Object defaultValue) {
		if (global.getConfig().contains(p.getWorld().getName() + "." + path)) {
			return global.getConfig().get(p.getWorld().getName() + "." + path);
		} else {
			if (global.getConfig().contains(path)) {
				return global.getConfig().get(path);
			} else {
				return defaultValue;
			}
		}
		
	}
	
	/**
	 * Multi-world config accessor
	 * @param String path - The setting path to look for (e.g. settings.disable-xp)
	 * @param Player p - Player to get world from
	 * @param Object defaultValue - If empty, use this
	 * @return Object - resultant list
	 */
	public List<String> checkList(String path, Player p) {
		if (global.getConfig().contains(p.getWorld().getName() + "." + path)) {
			return global.getConfig().getStringList(p.getWorld().getName() + "." + path);
		} else {
			if (global.getConfig().contains(path)) {
				return global.getConfig().getStringList(path);
			} else {
				return null;
			}
		}
		
	}
	
	/**
	 * Multi-world config accessor -- accepts world name instead of Player
	 * @param String path - The setting path to look for (e.g. settings.disable-xp)
	 * @param String world - World to check
	 * @param Object defaultValue - If empty, use this
	 * @return Object - result
	 */
	public Object checkSetting(String path, String world, Object defaultValue) {
		if (global.getConfig().contains(world + "." + path)) {
			//We have an override
			
			return global.getConfig().get(world + "." + path);
		} else {
			if (global.getConfig().contains(path)) {
				return global.getConfig().get(path);
			} else {
				return defaultValue;
			}
		}
		
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	public boolean buy(double amount, String name) {
		Player p = Bukkit.getPlayer(name);
		net.milkbowl.vault.economy.EconomyResponse r = economy.withdrawPlayer(name, amount);
		
		if (r.transactionSuccess()) {
			this.PM.notify(p, "Purchase successful! You spent " + amount + " and now have " + r.balance);
			return true;
			
		} else {
			this.PM.warn(p, "You don't have enough money! The kit costs " + amount + " and you have " + r.balance);
		}
		return false;
	}
	
	public boolean buyNeutral(double amount, String name) {
		Player p = Bukkit.getPlayer(name);
		net.milkbowl.vault.economy.EconomyResponse r = economy.withdrawPlayer(name, amount);
		
		if (r.transactionSuccess()) {
			this.PM.notify(p, "Purchase successful! You spent " + amount + " and now have " + r.balance);
			return true;
			
		} else {
			this.PM.warn(p, "You don't have enough money! This costs " + amount + " and you have " + r.balance);
		}
		return false;
	}
	
	private boolean createDataDirectory() {
	    File file = this.getDataFolder();
	    if (!file.isDirectory()){
	        if (!file.mkdirs()) {
	            // failed to create the non existent directory, so failed
	            return false;
	        }
	    }
	    return true;
	}
	
    
	@Override
	public void onEnable() {
		if (!createDataDirectory()) {
			this.getLogger().severe("Couldn't create BattleKits data folder. Shutting down...");
			this.setEnabled(false);
		}
		this.getLogger().info("Initializing configs...");
		InputStream page = getResource("page.txt");
		html = convertStreamToString(page);
		makeConfigs();
		
	}

	@Override
	public void onDisable() {
		
		this.getLogger().info("BattleKits has been disabled.");
		kitHistory.saveConfig();
		if (wh != null) { wh.saveAll(); }
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void makeConfigs() {
		
		kits = new ConfigAccessor(this, "kits.yml");
		global = new ConfigAccessor(this, "global.yml");
		kitHistory = new ConfigAccessor(this, "kitHistory.yml");
		kits.reloadConfig();
		global.reloadConfig();
		kitHistory.reloadConfig();
		postStartup();
	}
	
	
	public void postStartup() {
		getServer().getPluginManager().registerEvents(new DeathEvent(this),
				this);
		if (global.getConfig().getBoolean("signs.enabled")) {
			getServer().getPluginManager().registerEvents(new SignHandler(this), this);
		}
		

		getServer().getPluginManager().registerEvents(new RespawnKit(this), this);
		getServer().getPluginManager().registerEvents(new PlayerReward(this),this);
		
		if (global.getConfig().getBoolean("settings.enable-restrictions")) {
			getServer().getPluginManager().registerEvents(new RestrictionEvents(this), this);
			getLogger().info("Restrictions enabled. Use permissions to setup");


		} else {
			getLogger().info("Not enabling restrictions due to config setting");
		}
		getServer().getPluginManager()
				.registerEvents(new InstaSoup(this), this);

		getCommand("soup").setExecutor(new CommandSoup(this));
		getCommand("toolkit").setExecutor(new CommandKitCreation(this));
		getCommand("fillall").setExecutor(new CommandRefillAll(this));
		
		/*
		 * Web
		 */
		if (global.getConfig().getBoolean("server.enabled")) {
			int port = global.getConfig().getInt("server.port");
			try {
				
			  wh = new WebHandler(this);
			  
			  wh.html = html;
			  Container container = wh;
			  
		      Server server = new ContainerServer(container);
		      connection = new SocketConnection(server);
		      SocketAddress address = new InetSocketAddress(port);

		      connection.connect(address);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (global.getConfig().getBoolean("settings.auto-update") == true) {
			@SuppressWarnings("unused")
			Updater updater = new Updater(this, "battlekits", this.getFile(),
					Updater.UpdateType.DEFAULT, true); // New slug
		}

		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			this.getLogger().info("Vault found.");
			setupEconomy();

		} else {
			this.getLogger().info(
					"Couldn't find Vault. Economy disabled for now.");
		}

		if (Bukkit.getPluginManager().getPlugin("TagAPI") != null) {
			this.getLogger().info("TagAPI found.");
			getServer().getPluginManager().registerEvents(new TagHandler(this),
					this);

			useTags = true;
		} else {
			this.getLogger().info(
					"Disabling tag functionality as TagAPI is not installed.");
		}

		getCommand("battlekits").setExecutor(cbk);
		
		try {
		    BukkitMetrics metrics = new BukkitMetrics(this);
		    metrics.addCustomData(new BukkitMetrics.Plotter("Number of kits") {

		        @Override
		        public int getValue() {
		            return kits.getConfig().getConfigurationSection("kits").getKeys(false).size();
		        }

		    });
		    
		    metrics.addCustomData(new BukkitMetrics.Plotter("Restrictions enabled") {

		        @Override
		        public int getValue() {
		            if (global.getConfig().getBoolean("settings.enable-restrictions")) { return 1; }
		            return 0;
		        }

		    });
		    metrics.start();
		} catch (IOException e) {
			this.getLogger().severe("Metrics failed.");

		}
		
		String ver = getDescription().getVersion();
		this.getLogger().info("BattleKits (" + ver + ") has been enabled!");
	}

	public ItemStack setColor(ItemStack item, int color) {
		/*CraftItemStack craftStack = null;
		net.minecraft.server.v1_4_5.ItemStack itemStack = null;
		
		if (item instanceof CraftItemStack) {
			craftStack = (CraftItemStack) item;
			itemStack = craftStack.getHandle();
		} else if (item instanceof ItemStack) {
			craftStack = new CraftItemStack(item);
			itemStack = craftStack.getHandle();
		}
		NBTTagCompound tag = itemStack.tag;
		
		if (tag == null) {
			tag = new NBTTagCompound();
			tag.setCompound("display", new NBTTagCompound());
			itemStack.tag = tag;
		}
		tag = itemStack.tag.getCompound("display");
		tag.setInt("color", color);
		itemStack.tag.setCompound("display", tag);*/
		LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
		im.setColor(Color.fromRGB(color));
		item.setItemMeta(im);
		return item;
	}

}
