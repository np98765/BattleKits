package com.lavacraftserver.PvPKits;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PvPKits extends JavaPlugin {

	public static HashSet<Player> death = new HashSet<Player>();
	
	@Override
	public void onEnable() {
		getLogger().info("PvPKits has been enabled!");
		getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new RespawnKit(this), this);
		getConfig().options().copyDefaults(true);
		getConfig().options().copyHeader(true);
		if (!getConfig().contains("settings.no-auto-update"))
		{
			Updater updater = new Updater(this, "pvpkits", this.getFile(), Updater.UpdateType.DEFAULT, false);
		}
		
		saveConfig();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("PvPKits has been disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {	
		if (commandLabel.equalsIgnoreCase("pvp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("[PvPKits] This command can only be executed by a player!");
				return true;
			 } else {
				 Player p = (Player)sender;
				 String className = args[0];
				 if (p.hasPermission("pvpkits.kit." + className) || p.isOp()) {
					 if (((getConfig().getBoolean("settings.once-per-life") == true) && !(death.contains(p))) || (getConfig().getBoolean("settings.once-per-life") == false)) {
						 if (args.length > 1) {
							 sender.sendMessage(ChatColor.RED + "Too many arguments!");
							 return true;
						 }
						 if (args.length < 1) {
							 sender.sendMessage(ChatColor.RED + "Please specify a kit!");
							 return true;
						 } 
						 if (args.length == 1) {
							 Set<String> keys = getConfig().getConfigurationSection("kits").getKeys(false);
							 if (keys.contains(args[0])) {
								 p.getInventory().clear();
								 p.getInventory().setArmorContents(null);
								 int slot;
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
										 
										 //Sets the enchantment and level
										 if (s.length == 2 ) {
											 String[] enchant = s[1].split(":");
											 Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
											 int levelInt = Integer.parseInt(enchant[1]);
											 i.addUnsafeEnchantment(enchantmentInt, levelInt);
										 }
										 
										 //Sets the armor contents
										 String getHelmet = getConfig().getString("kits." + className + ".items" + ".helmet");
										 String getChestplate = getConfig().getString("kits." + className + ".items" + ".chestplate");
										 String getLeggings = getConfig().getString("kits." + className + ".items" + ".leggings");
										 String getBoots = getConfig().getString("kits." + className + ".items" + ".boots");
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
									  
										 if (getHelmet != null) {
											 if (getHelmet.equals("leather")) {
												 p.getInventory().setHelmet(lhelmet);
											 }
											 if (getHelmet.equals("iron")) {
												 p.getInventory().setHelmet(ihelmet);
											 }
											 if (getHelmet.equals("gold")) {
												 p.getInventory().setHelmet(ghelmet);
											 }
											 if (getHelmet.equals("diamond")) {
												 p.getInventory().setHelmet(dhelmet);
											 }
											 if (getHelmet.equals("chainmail")) {
												 p.getInventory().setHelmet(chelmet);
											 }
											 if (getHelmet.equals("none")) {
												 p.getInventory().setHelmet(null);
											 }
										 }
										 if (getChestplate != null) {
											 if (getChestplate.equals("leather")) {
												 p.getInventory().setChestplate(lchestplate);
											 }
											 if (getChestplate.equals("iron")) {
												 p.getInventory().setChestplate(ichestplate);
											 }
											 if (getChestplate.equals("gold")) {
												 p.getInventory().setChestplate(gchestplate);
											 }
											 if (getChestplate.equals("diamond")) {
												 p.getInventory().setChestplate(dchestplate);
											 }
											 if (getChestplate.equals("chainmail")) {
												 p.getInventory().setChestplate(cchestplate);
											 }
											 if (getChestplate.equals("none")) {
												 p.getInventory().setChestplate(null);
											 }
										 }
										 if (getLeggings != null) {
											 if (getLeggings.equals("leather")) {
												 p.getInventory().setLeggings(lleggings);
											 }
											 if (getLeggings.equals("iron")) {
												 p.getInventory().setLeggings(ileggings);
											 }
											 if (getLeggings.equals("gold")) {
												 p.getInventory().setLeggings(gleggings);
											 }
											 if (getLeggings.equals("diamond")) {
												 p.getInventory().setLeggings(dleggings);
											 }
											 if (getLeggings.equals("chainmail")) {
												 p.getInventory().setLeggings(cleggings);
											 }
											 if (getLeggings.equals("none")) {
												 p.getInventory().setLeggings(null);
											 }
										 }
										 if (getBoots != null) {
											 if (getBoots.equals("leather")) {
												 p.getInventory().setBoots(lboots);
											 }
											 if (getBoots.equals("iron")) {
												 p.getInventory().setBoots(iboots);
											 }
											 if (getBoots.equals("gold")) {
												 p.getInventory().setBoots(gboots);
											 }
											 if (getBoots.equals("diamond")) {
												 p.getInventory().setBoots(dboots);
											 }
											 if (getBoots.equals("chainmail")) {
												 p.getInventory().setBoots(cboots);
											 }
											 if (getBoots.equals("none")) {
												 p.getInventory().setBoots(null);
											 }
										 }
										 p.getInventory().setItem(slot, i);
									 }
								 }
								 
								 if (getConfig().getBoolean("settings.once-per-life") == true) {
									 death.add(p);
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