package com.lavacraftserver.BattleKits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BattleKits extends JavaPlugin {

	public HashSet<String> death = new HashSet<String>();
	public static net.milkbowl.vault.economy.Economy economy = null;
	
	@Override
	public void onEnable() {
		getLogger().info("BattleKits has been enabled!");
		getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new RespawnKit(this), this);
		getServer().getPluginManager().registerEvents(new RestrictionEvents(this), this);
		getConfig().options().copyDefaults(true);
		getConfig().options().copyHeader(true);
		if (getConfig().getBoolean("settings.auto-update")) {
			Updater updater = new Updater(this, "battlekits", this.getFile(), Updater.UpdateType.DEFAULT, true); //New slug
		}
		if (Bukkit.getPluginManager().getPlugin("Vault") != null && setupEconomy()) {
			getLogger().info("Found vault successfully!");
		} else {
			getLogger().info("Couldn't find vault. Economy disabled for now.");
		}
		
		saveConfig();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Saved config! Use /pvp reload if you wish to modify it"); 
		this.saveConfig();
		getLogger().info("BattleKits has been disabled."); 
		
	}
	
	private boolean setupEconomy() {

        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
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
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {	
		if (commandLabel.equalsIgnoreCase("pvp")) {
			if (args.length != 1) {
				String kit_ref = "";
				for (String s: getConfig().getConfigurationSection("kits").getKeys(false))
				{
					kit_ref = kit_ref + s + ",";
				}
				kit_ref = kit_ref.substring(0, kit_ref.length() - 1); //remove last comma
				sender.sendMessage(ChatColor.BLUE + "Available kits: " + ChatColor.RESET + kit_ref);
				return true;
			}
			if (args[0].equals("reload")) {
				this.reloadConfig();
				sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
				return true;
			}
			
			if (args[0].equals("restoreconfig")) {
				
				this.saveDefaultConfig();
				this.saveConfig();
				sender.sendMessage(ChatColor.GREEN + "Config restored to defaults!");
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("[BattleKits] This command can only be executed by a player!");
				return true;
			 } else {
				 Player p = (Player)sender;
				 
				 String className = args[0];
				 if (p.hasPermission("BattleKits.kit." + className) || p.isOp()) {
					 if ((getConfig().getBoolean("settings.once-per-life") && !getConfig().contains("dead." + p.getName())) || (getConfig().getBoolean("settings.once-per-life") == false)) {
						 
						 if (args.length == 1) {
							 Set<String> keys = getConfig().getConfigurationSection("kits").getKeys(false);
							 if (keys.contains(args[0])) {
								 if (getConfig().contains("kits." + args[0] + ".active-in")) {
									 if (!(getConfig().getString("kits." + args[0] + ".active-in").contains("'" + p.getWorld().getName() + "'") || getConfig().getString("kits." + args[0] + ".active-in").equals("all"))) {
										 sender.sendMessage(ChatColor.RED + "You can't use that kit in this world!");
										 return true;
									 }
								 }
								 if (economy != null && getConfig().contains("kits." + args[0] + ".cost")) {
									 double cost = getConfig().getDouble("kits." + args[0] + ".cost");
									 net.milkbowl.vault.economy.EconomyResponse r = economy.withdrawPlayer(sender.getName(), cost);
									 if (!r.transactionSuccess()) {
									 sender.sendMessage(ChatColor.RED + "You don't have enough money!");
									 return true;
									 } else {
										 sender.sendMessage(ChatColor.GREEN + "You spent " + cost + ". You now have: " + r.balance + " remaining."); 
									 }
								 }
								 p.getInventory().clear();
								 p.getInventory().setArmorContents(null);
								 if (getConfig().contains("kits." + args[0] + ".on-give-message"))
								 {
									 p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("kits." + args[0] + ".on-give-message")));
								 }
								 int slot;
								 
								 this.getConfig().set("kitHistory." + p.getName(), args[0]);
								 for (slot = 0; slot<=35; slot++) {
									 ItemStack i = new ItemStack(0);
									 String getSlot = getConfig().getString("kits." + className + ".items." + slot);
									 if (!(getConfig().getString("kits." + className + ".items." + slot).equals(null)) || !(getConfig().getString("kits." + className + ".items." + slot).equals("0"))) {
										 String[] s = getSlot.split(" ");
										 String[] item = s[0].split(":");

										 //Sets the block/item
										 i.setTypeId(Integer.parseInt(item[0]));
										 
										 //Sets the amount and durability
										 if (item.length > 1) {
											 i.setAmount(Integer.parseInt(item[1]));
											 if (item.length > 2) {
												 i.setDurability((short)Integer.parseInt(item[2]));
											 }
										 } else {
											 i.setAmount(1);
										 }
										 
										 if (getConfig().contains("kits." + className + ".items" + ".names." + slot) ) {

											 //get item name
											 String name = ChatColor.translateAlternateColorCodes('&', getConfig().getString("kits." + className + ".items" + ".names." + slot));
											 CraftItemStack c = new CraftItemStack(i);
											 NBTTagCompound tag = new NBTTagCompound();
									            NBTTagCompound display = new NBTTagCompound();
									            tag.setCompound("display", display);
									            display.setString("Name", name);
									            c.getHandle().tag = tag;
									            i = c;
											 
										 }
										 
										// Sets the enchantments and level
										Boolean first = true;
										if (s.length > 1) {
											for (String a : s) {
												if (!first) {

													String[] enchant = a.split(":");
													Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
													int levelInt = Integer.parseInt(enchant[1]);
													i.addUnsafeEnchantment(enchantmentInt,levelInt);
												}
												first = false;
											}
										}
										 
										
										 
										 p.getInventory().setItem(slot, i);
									 }
								 }
								 
								//Sets the armor contents
								 String getHelmet = getConfig().getString("kits." + className + ".items" + ".helmet");
								 String getChestplate = getConfig().getString("kits." + className + ".items" + ".chestplate");
								 String getLeggings = getConfig().getString("kits." + className + ".items" + ".leggings");
								 String getBoots = getConfig().getString("kits." + className + ".items" + ".boots");
								 int helmColor = 0;
								 int chestColor = 0;
								 int legColor = 0;
								 int bootColor = 0;
								 
								 ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET);
								 ItemStack lchestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
								 ItemStack lleggings = new ItemStack(Material.LEATHER_LEGGINGS);
								 ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS);
								 
								 ItemStack ihelmet = new ItemStack(Material.IRON_HELMET, 1);
								 ItemStack ichestplate = new ItemStack(Material.IRON_CHESTPLATE, 1);
								 ItemStack ileggings = new ItemStack(Material.IRON_LEGGINGS, 1);
								 ItemStack iboots = new ItemStack(Material.IRON_BOOTS, 1);
								 
								 ItemStack ghelmet = new ItemStack(Material.GOLD_HELMET, 1);
								 ItemStack gchestplate = new ItemStack(Material.GOLD_CHESTPLATE, 1);
								 ItemStack gleggings = new ItemStack(Material.GOLD_LEGGINGS, 1);
								 ItemStack gboots = new ItemStack(Material.GOLD_BOOTS, 1);
								 
								 ItemStack dhelmet = new ItemStack(Material.DIAMOND_HELMET, 1);
								 ItemStack dchestplate = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
								 ItemStack dleggings = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
								 ItemStack dboots = new ItemStack(Material.DIAMOND_BOOTS, 1);
								 
								 ItemStack chelmet = new ItemStack(Material.CHAINMAIL_HELMET);
								 ItemStack cchestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
								 ItemStack cleggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
								 ItemStack cboots = new ItemStack(Material.CHAINMAIL_BOOTS);
								 
								 ItemStack finalHelmet = null;
								 ItemStack finalChestplate = null;
								 ItemStack finalLeggings = null;
								 ItemStack finalBoots = null;
								 if (getConfig().contains("kits." + className + ".items" + ".helmetColor"))
								 {
									  helmColor = Integer.parseInt(getConfig().getString("kits." + className + ".items.helmetColor").replace("#", ""), 16); 
									  lhelmet = this.setColor(lhelmet, helmColor);
									  
									  
								 }
								 if (getConfig().contains("kits." + className + ".items" + ".chestplateColor"))
								 {
									  chestColor = Integer.parseInt(getConfig().getString("kits." + className + ".items.chestplateColor").replace("#", ""), 16);  
									  lchestplate = this.setColor(lchestplate, chestColor);
								 }
								 if (getConfig().contains("kits." + className + ".items" + ".leggingColor"))
								 {
									  legColor = Integer.parseInt(getConfig().getString("kits." + className + ".items.leggingColor").replace("#", ""), 16); 
									  lleggings = this.setColor(lleggings, legColor);
								 }
								 if (getConfig().contains("kits." + className + ".items" + ".bootColor"))
								 {
									  bootColor = Integer.parseInt(getConfig().getString("kits." + className + ".items.bootColor").replace("#", ""), 16);   
									  lboots= this.setColor(lboots, bootColor);
								 }
								 
						
							  
								 if (getHelmet != null) {
									 if (getHelmet.equals("leather")) {
										 finalHelmet = lhelmet;
									 }
									 if (getHelmet.equals("iron")) {
										 finalHelmet = ihelmet;
									 }
									 if (getHelmet.equals("gold")) {
										 finalHelmet = ghelmet;
									 }
									 if (getHelmet.equals("diamond")) {
										 finalHelmet = dhelmet;
									 }
									 if (getHelmet.equals("chainmail")) {
										 finalHelmet = chelmet;
									 }
									 
								 }
								
								 if (getChestplate != null) {
									 if (getChestplate.equals("leather")) {
										 finalChestplate = lchestplate;
									 }
									 if (getChestplate.equals("iron")) {
										 finalChestplate = ichestplate;
									 }
									 if (getChestplate.equals("gold")) {
										 finalChestplate = gchestplate;
									 }
									 if (getChestplate.equals("diamond")) {
										 finalChestplate = dchestplate;
									 }
									 if (getChestplate.equals("chainmail")) {
										 finalChestplate = cchestplate;
									 }
									 
								 }
								 if (getLeggings != null) {
									 if (getLeggings.equals("leather")) {
										 finalLeggings = lleggings;
									 }
									 if (getLeggings.equals("iron")) {
										 finalLeggings = ileggings;
									 }
									 if (getLeggings.equals("gold")) {
										 finalLeggings = gleggings;
									 }
									 if (getLeggings.equals("diamond")) {
										 finalLeggings = dleggings;
									 }
									 if (getLeggings.equals("chainmail")) {
										 finalLeggings = cleggings;
									 }
								 }
								 if (getBoots != null) {
									 if (getBoots.equals("leather")) {
										 finalBoots = lboots;
									 }
									 if (getBoots.equals("iron")) {
										 finalBoots = iboots;
									 }
									 if (getBoots.equals("gold")) {
										 finalBoots = gboots;
									 }
									 if (getBoots.equals("diamond")) {
										 finalBoots = dboots;
									 }
									 if (getBoots.equals("chainmail")) {
										 finalBoots = cboots;
									 }
									 
								 }
								 
								 if (getConfig().contains("kits." + className + ".items.helmetEnchant") && finalHelmet != null) {
									  for (String a : getConfig().getString("kits." + className + ".items.helmetEnchant").split(" ")) {

												String[] enchant = a.split(":");
												Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
												int levelInt = Integer.parseInt(enchant[1]);
												finalHelmet.addUnsafeEnchantment(enchantmentInt,levelInt);
										}
								  }
								 
								
								 
								 if (getConfig().contains("kits." + className + ".items.chestplateEnchant") && finalChestplate != null) {
									  for (String a : getConfig().getString("kits." + className + ".items.chestplateEnchant").split(" ")) {

												String[] enchant = a.split(":");
												Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
												int levelInt = Integer.parseInt(enchant[1]);
												finalChestplate.addUnsafeEnchantment(enchantmentInt,levelInt);
										}
								  }
								 
								 if (getConfig().contains("kits." + className + ".items.leggingsEnchant") && finalLeggings != null) {
									  for (String a : getConfig().getString("kits." + className + ".items.leggingsEnchant").split(" ")) {

												String[] enchant = a.split(":");
												Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
												int levelInt = Integer.parseInt(enchant[1]);
												finalLeggings.addUnsafeEnchantment(enchantmentInt,levelInt);
										}
								  }
								 
								 if (getConfig().contains("kits." + className + ".items.bootsEnchant") && finalBoots != null) {
									  for (String a : getConfig().getString("kits." + className + ".items.bootsEnchant").split(" ")) {
												String[] enchant = a.split(":");
												Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
												int levelInt = Integer.parseInt(enchant[1]);
												finalBoots.addUnsafeEnchantment(enchantmentInt,levelInt);
										}
								  }
								 
								 p.getInventory().setHelmet(finalHelmet);
								 p.getInventory().setChestplate(finalChestplate);
								 p.getInventory().setLeggings(finalLeggings);
								 p.getInventory().setBoots(finalBoots);
								 
								 if (getConfig().getBoolean("settings.once-per-life")) {
									 getConfig().set("dead." + p.getName(), true);
								 }
								 
								 if (getConfig().contains(("kits." + className + ".commands"))) {
									 List<String> commands = this.getConfig().getStringList("kits." + className + ".commands");
									 for (String s : commands) {
										 s = s.replace("<player>", p.getName());
										 Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
									 }
								 }
								 
							 } else {
								 p.sendMessage(ChatColor.RED + "Please choose a valid kit!");
							 }
						 }
					 } else {
						 p.sendMessage(ChatColor.RED + "You may only use one kit per life!");
					 }
				 } else {
					 p.sendMessage(ChatColor.RED + "You do not have permission to use this kit!");
				 }
			}
		}
		return true;
	}
}