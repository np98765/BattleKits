package com.lavacraftserver.BattleKits;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
	public FileConfiguration global = null;
    private File globalFile = null;
    
    public FileConfiguration kits = null;
    private File kitsFile = null;
    
    public FileConfiguration kitHistory = null;
    private File kitHistoryFile= null;
    
    
	public boolean setupEconomy() {
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null); //t/f
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
	
	public void loadHistory() {
        if (kitHistoryFile == null) {
        	kitHistoryFile = new File(getDataFolder(), "kitHistory.yml");
        }
        kitHistory = YamlConfiguration.loadConfiguration(kitHistoryFile);
        
        // Look for defaults in the jar
        InputStream defConfigStream = this.getResource("kitHistory.yml");
        if (defConfigStream != null && !kitHistoryFile.exists()) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            kitHistory.setDefaults(defConfig);
            try {
				kitHistory.save(kitHistoryFile);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.getLogger().severe("Couldn't save kit history defaults...");
			}
            
            
        } else {
		this.getLogger().info("Loaded kitHistory config");
        }

    }
	
	public void reloadKits() {
        if (kitsFile == null) {
        	kitsFile = new File(getDataFolder(), "kits.yml");
        }
        kits = YamlConfiguration.loadConfiguration(kitsFile);
        
        // Look for defaults in the jar
        InputStream defConfigStream = this.getResource("kits.yml");
        if (defConfigStream != null && !kitsFile.exists()) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            kits.setDefaults(defConfig);
            try {
				kits.save(kitsFile);
				this.getLogger().info("Saved kits config defaults");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.getLogger().severe("Couldn't save configuration defaults...");
			}
            
            
        } else {
		this.getLogger().info("Loaded kits config");
        }

    }

    public void reloadGlobals() {
        if (globalFile == null) {
        	globalFile = new File(getDataFolder(), "global.yml");
        }
        global = YamlConfiguration.loadConfiguration(globalFile);
        
        // Look for defaults in the jar
        InputStream defConfigStream = this.getResource("global.yml");
        if (defConfigStream != null && !globalFile.exists()) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            global.setDefaults(defConfig);
            try {
				global.save(globalFile);
				this.getLogger().info("Saved global config defaults");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.getLogger().severe("Couldn't save configuration defaults...");
			}
            
            
        } else {
		this.getLogger().info("Loaded global config");
        }

    }
    
	@Override
	public void onEnable() {
		
		if (!createDataDirectory()) {
			this.getLogger().severe("Couldn't create BattleKits data folder. Shutting down...");
			this.setEnabled(false);
		}
		
		reloadGlobals(); //Load global config
		reloadKits(); //load kit data
		
		getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		
		if (global.getBoolean("signs.enabled"))
			getServer().getPluginManager().registerEvents(new SignHandler(this), this);
		
		getServer().getPluginManager().registerEvents(new RespawnKit(this), this);
		getServer().getPluginManager().registerEvents(new RestrictionEvents(this), this);
		getServer().getPluginManager().registerEvents(new InstaSoup(this), this);
	

		getCommand("soup").setExecutor(new CommandSoup(this));
		getCommand("fillall").setExecutor(new CommandRefillAll(this));
		if (global.getBoolean("settings.auto-update") == true) {
			@SuppressWarnings("unused")
			Updater updater = new Updater(this, "battlekits", this.getFile(), Updater.UpdateType.DEFAULT, true); // New slug
		}

		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			this.getLogger().info("Vault found.");
			setupEconomy();
			
		} else {
			this.getLogger().info("Couldn't find Vault. Economy disabled for now.");
		}
		
		
		
		
		
		
		if (Bukkit.getPluginManager().getPlugin("TagAPI") != null) {
			this.getLogger().info("TagAPI found.");
			getServer().getPluginManager().registerEvents(new TagHandler(this), this);

			useTags = true;
		} else {
			this.getLogger().info("Disabling tag functionality as TagAPI is not installed.");
		}
		
		getCommand("battlekits").setExecutor(cbk);
		// this.buy(5, "lol768");
		this.getLogger().info("BattleKits has been enabled!");
	}

	@Override
	public void onDisable() {

		this.getLogger().info("BattleKits has been disabled.");
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
