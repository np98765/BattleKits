package com.lavacraftserver.BattleKits;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

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
import org.bukkit.plugin.java.JavaPlugin;

public class BattleKits extends JavaPlugin {

	public static HashSet<Player> death = new HashSet<Player>();
	
	@Override
	public void onEnable() {
		getLogger().info("BattleKits has been enabled!");
		getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new RespawnKit(this), this);
		getConfig().options().copyDefaults(true);
		getConfig().options().copyHeader(true);
		if (!getConfig().contains("settings.no-auto-update")) {
			Updater updater = new Updater(this, "battlekits", this.getFile(), Updater.UpdateType.DEFAULT, true); //New slug
		}
		
		saveConfig();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("BattleKits has been disabled."); 
	}
	
	private static ItemStack toCraftBukkit(ItemStack stack) {
		if (!(stack instanceof CraftItemStack))
		return new CraftItemStack(stack);
		else
		return stack;
		}
	
	public ItemStack renameItem(CraftItemStack cis, String name) {
		
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound display = new NBTTagCompound();
        tag.setCompound("display", display);
        display.setString("Name", name);
        cis.getHandle().tag = tag;
        return cis;
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
			tag = new NBTTagCompound(); // TEST
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
				sender.sendMessage(ChatColor.RED + "Bad number of arguments");
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("[BattleKits] This command can only be executed by a player!");
				return true;
			 } else {
				 Player p = (Player)sender;
				 String className = args[0];
				 if (p.hasPermission("BattleKits.kit." + className) || p.isOp()) {
					 if (((getConfig().getBoolean("settings.once-per-life") == true) && !(death.contains(p))) || (getConfig().getBoolean("settings.once-per-life") == false)) {
						 
						 if (args.length == 1) {
							 Set<String> keys = getConfig().getConfigurationSection("kits").getKeys(false);
							 if (keys.contains(args[0])) {
								 if (getConfig().contains("kits." + args[0] + ".active-in")) {
									 if (!(getConfig().getString("kits." + args[0] + ".active-in").contains("'" + p.getWorld().getName() + "'") || getConfig().getString("kits." + args[0] + ".active-in").equals("all"))) {
										 sender.sendMessage(ChatColor.RED + "You can't use that kit in this world!");
										 return true;
									 }
								 }
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