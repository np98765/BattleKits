package com.lavacraftserver.BattleKits;

import java.util.HashSet;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class BattleKits extends JavaPlugin {
	public static net.milkbowl.vault.economy.Economy economy = null;

	public HashSet<String> death = new HashSet<String>();
	;
	

	public boolean setupEconomy() {

        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        
		if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public boolean buy(double amount, String name)
	{
		Player p = Bukkit.getPlayer(name);
		 net.milkbowl.vault.economy.EconomyResponse r = economy.withdrawPlayer(name, amount);
		 if (r.transactionSuccess())
		 {
			 p.sendMessage(ChatColor.GREEN + "Purchase successful! You spent " + amount + " and now have " + r.balance);
			 return true;
			 
		 }
		 else
		 {
			 p.sendMessage(ChatColor.RED + "You don't have enough money! Kit costs " + amount + " and you have " + r.balance);

		 }
		return false;
		
	}
	
	@Override
	public void onEnable() {
		getLogger().info("BattleKits has been enabled!");
		getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new SignHandler(this), this);
		getServer().getPluginManager().registerEvents(new RespawnKit(this), this);
		getServer().getPluginManager().registerEvents(new RestrictionEvents(this), this);
		getServer().getPluginManager().registerEvents(new InstaSoup(this), this);
		
		getConfig().options().copyDefaults(true);
		getConfig().options().copyHeader(true);
		
		getCommand("soup").setExecutor(new CommandSoup(this));
		
		
		if (getConfig().getBoolean("settings.auto-update")) {
			Updater updater = new Updater(this, "battlekits", this.getFile(), Updater.UpdateType.DEFAULT, true); //New slug
		}
		
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			Bukkit.getLogger().info("Found vault successfully!");
			setupEconomy();
		} else {
			Bukkit.getLogger().info("Couldn't find vault. Economy disabled for now.");
		}
		CommandBattleKits cbk = new CommandBattleKits(this);
		getCommand("battlekits").setExecutor(cbk);
		saveConfig();
		//this.buy(5, "lol768");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Saved config! Use /pvp reload if you wish to modify it"); 
		this.saveConfig();
		getLogger().info("BattleKits has been disabled."); 
		
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
