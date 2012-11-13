package com.lavacraftserver.BattleKits;

import java.util.HashSet;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BattleKits extends JavaPlugin {
	
	public static net.milkbowl.vault.economy.Economy economy = null;
	
	public HashSet<String> death = new HashSet<String>();
	CommandBattleKits cbk = new CommandBattleKits(this);
	public PM PM = new PM(this);
	
	public boolean setupEconomy() {
		RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
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

	
	@Override
	public void onEnable() {
		this.getLogger().info("BattleKits has been enabled!");
		getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		
		if (getConfig().getBoolean("signs.enabled"))
			getServer().getPluginManager().registerEvents(new SignHandler(this), this);
		
		getServer().getPluginManager().registerEvents(new RespawnKit(this), this);
		getServer().getPluginManager().registerEvents(new RestrictionEvents(this), this);
		getServer().getPluginManager().registerEvents(new InstaSoup(this), this);

		getConfig().options().copyDefaults(true);
		getConfig().options().copyHeader(true);
		//Check TagAPI prescense
		getCommand("soup").setExecutor(new CommandSoup(this));
		getCommand("fillall").setExecutor(new CommandRefillAll(this));
		if (getConfig().getBoolean("settings.auto-update") == true) {
			@SuppressWarnings("unused")
			Updater updater = new Updater(this, "battlekits", this.getFile(), Updater.UpdateType.DEFAULT, true); // New slug
		}

		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			this.getLogger().info("Vault found.");
			setupEconomy();
			
		} else {
			this.getLogger().info("Couldn't find Vault. Economy disabled for now.");
		}

		
		getCommand("battlekits").setExecutor(cbk);
		// this.buy(5, "lol768");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("Saved config! Use /pvp reload if you wish to modify it");
		this.saveConfig();
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
