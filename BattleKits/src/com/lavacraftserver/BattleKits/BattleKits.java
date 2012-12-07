package com.lavacraftserver.BattleKits;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BattleKits extends JavaPlugin {
	
	public static net.milkbowl.vault.economy.Economy economy = null;
	
	public HashSet<String> death = new HashSet<String>();
	public HashMap<String, String> tags = new HashMap<String, String>(); //Name, prefix (colour codes)
	CommandBattleKits cbk = new CommandBattleKits(this);
	public boolean useTags = false;
	public PM PM = new PM(this);
	//Configuration stuff
    public ConfigAccessor global;
    public ConfigAccessor kits;
    public ConfigAccessor kitHistory;


	public boolean setupEconomy() {
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null); //t/f
	}
	
	/**
	 * Multi-world config accessor
	 * @param String path - The setting path to look for (e.g. settings.disable-xp)
	 * @param Player p - Player to get world from
	 * @param Object defaultValue - If empty, use this
	 * @return Object - result
	 */
	public Object checkSetting(String path, Player p, Object defaultValue) {
		if (global.getConfig().contains(p.getWorld().getName() + "." + path)) {
			return global.getConfig().getBoolean(p.getWorld().getName() + "." + path);
		} else {
			if (global.getConfig().contains(path)) {
				return global.getConfig().get(path);
			} else {
				return defaultValue;
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
		
		makeConfigs();
		
		
	}

	@Override
	public void onDisable() {
		
		this.getLogger().info("BattleKits has been disabled.");
		kitHistory.saveConfig();
		
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
		
		if (global.getConfig().getBoolean("experimental.enable-wg")) {
	        getLogger().severe("You're not a beta tester.");

		   /* // WorldGuard may not be loaded
		    if (WGP == null || !(WGP instanceof WorldGuardPlugin)) {
		        getLogger().severe("Only enable worldguard integration if you have WorldGuard installed.");
		        
		    } else {
				getServer().getPluginManager().registerEvents(new WorldGuardIntegration(this), this);
		    } */
		}

		getServer().getPluginManager().registerEvents(new RespawnKit(this),
				this);
		getServer().getPluginManager().registerEvents(new PlayerReward(this),
				this);
		getServer().getPluginManager().registerEvents(
				new RestrictionEvents(this), this);
		getServer().getPluginManager()
				.registerEvents(new InstaSoup(this), this);

		getCommand("soup").setExecutor(new CommandSoup(this));
		getCommand("fillall").setExecutor(new CommandRefillAll(this));
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
		this.getLogger().info("BattleKits has been enabled!");
	}

	public ItemStack setColor(ItemStack item, int color) {
		CraftItemStack craftStack = null;
		net.minecraft.server.ItemStack itemStack = null;
		
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
		itemStack.tag.setCompound("display", tag);
		return craftStack;
	}

}
