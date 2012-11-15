package com.lavacraftserver.BattleKits;

import java.util.List;
import java.util.Set;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kitteh.tag.TagAPI;

public class CommandBattleKits implements CommandExecutor {
	
	public BattleKits plugin;
	
	/**
	* Constructor method used when creating instance of this class
	* Used so we have access to the main plugin & config etc
	* @param instance - Instance of BattleKits.java
	*/
	public CommandBattleKits(BattleKits p) {
		this.plugin = p;
	}
	
	/**
	 * Method that is executed every time a BattleKit registered command is executed
	 * Takes some info on the command, such as arguments
	 * This determines whether to reload config, apply a kit, list kits etc
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("bk") || commandLabel.equalsIgnoreCase("battlekits")  || commandLabel.equalsIgnoreCase("kit")) {
			
			/**
			 * No args = list all available kits with costs
			 */
			if (args.length != 1) {
				String kit_ref = "";
				for (String s: plugin.getConfig().getConfigurationSection("kits").getKeys(false)) {
					if (plugin.getConfig().contains("kits." + s + ".cost")) {
						s = s + " (" + plugin.getConfig().getDouble("kits." + s + ".cost") + ") "; //Builds list of kits incl. cost of each
					}
					kit_ref = kit_ref + s + ", "; //Add new kit info to String
				}
				kit_ref = kit_ref.substring(0, kit_ref.length() - 1); //remove last comma
				plugin.PM.notify((Player)sender,"Available kits (cost): " ); //Header for info
				sender.sendMessage(kit_ref); //Send the kit list
				return true;
			}
			
			/**
			 * This command reloads the BattleKit config
			 */
			if (args[0].equals("reload")) {
				if (!sender.hasPermission("BattleKits.config.reload")) {
					plugin.PM.warn(sender, "You don't have permission to use this command.");
					return true;
				}
				plugin.reloadConfig();
				plugin.PM.notify(sender, "Config reloaded");
				return true;
			}
			
			/**
			 * Restores the config back to the original defaults and saves it
			 */
			if (args[0].equals("restoreconfig")) {
				if (!sender.hasPermission("BattleKits.config.restore")) {
					plugin.PM.warn(sender, "You don't have permission to use this command."); 
					return true;
				}
				this.plugin.saveDefaultConfig();
				this.plugin.saveConfig();
				plugin.PM.notify(sender, "Config restored to defaults");
				return true;
			}
			
			/**
			 * This handles checking the sender to ensure they are
			 * a player and giving them the requested kit.
			 */
			if (!(sender instanceof Player)) {
				plugin.PM.warn(sender, "Players are only supported by this command"); 
				return true;
				
			 } else {
				 Player p = (Player)sender;
				 supplyKit(p, args[0], false, false, false, false); //Obey all restrictions
				 return true;
			
			 }
		
		}
		return false;
	}
	
	/**
	 * Big method that gives the player a specified kit
	 * @param p - The player to give the kit
	 * @param className - The name of the kit to give them
	 * @param ignorePerms - Whether or not to ignore the BattleKits.use permission requirements
	 * @param ignoreCost - Whether or not to ignore the economy & cost settings
	 * @param ignoreLives - Whether or not to ignore the 1 per life rule
	 * @param ignoreWorldRestriction - Whether or not to ignore the world restrictions
	 * @return
	 */
	public Boolean supplyKit(Player p, String className, Boolean ignorePerms, Boolean ignoreCost, Boolean ignoreLives, Boolean ignoreWorldRestriction) {
		
		if (p.hasPermission("BattleKits.use." + className) || ignorePerms) { //Ensure they have permission to use the kit
			 
		 }
		else{
			plugin.PM.warn(p, "You do not have permission to use this kit!");
			return true;
		}
		
		/**
		 * This if statement checks if the once-per-life rule is active, and whether the user has already used a kit
		 */
		if (ignoreLives || (plugin.getConfig().getBoolean("settings.once-per-life") && !plugin.getConfig().contains("dead." + p.getName())) || (plugin.getConfig().getBoolean("settings.once-per-life") == false)) {
				 Set<String> keys = plugin.getConfig().getConfigurationSection("kits").getKeys(false); //Current kits in config
				 
				 /**
				  * Ensures that there are no restrictions for the user's current world
				  */
				 if (keys.contains(className)) {
					 if (plugin.getConfig().contains("kits." + className + ".active-in")) {
						 if (ignoreWorldRestriction || !(plugin.getConfig().getString("kits." + className + ".active-in").contains("'" + p.getWorld().getName() + "'") || plugin.getConfig().getString("kits." + className + ".active-in").equals("all"))) {
							 plugin.PM.warn(p, "This kit is disabled in your current world (" + p.getWorld().getName() + ")");
							 return true;
						 }
					 }
					 
					 /**
					  * Uses vault to charge user as specified in config
					  */
					 if (BattleKits.economy != null && plugin.getConfig().contains("kits." + className + ".cost") && plugin.getConfig().getDouble("kits." + className + ".cost") != 0 && !ignoreCost) {
						 double cost = plugin.getConfig().getDouble("kits." + className + ".cost");
						 
						 if (!plugin.buy(cost, p.getName())) {
							return true;
						}
					 }
					 
					 p.getInventory().clear();
					 p.getInventory().setArmorContents(null); //Gets rid of current kit + items to avoid duplication
					 
					 /**
					  * Handles kit give message
					  */
					 if (plugin.getConfig().contains("kits." + className + ".on-give-message") ) {
						 
						 if (!plugin.getConfig().getString("kits." + className + ".on-give-message").contains("&h")) { //User may wish to hide BattleKits prefix
							 plugin.PM.notify(p, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("kits." + className + ".on-give-message")));
							 
						 } else {
							 p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("kits." + className + ".on-give-message").replace("&h", "")));

						 }
							 
					 }
					 
					 /**
					  * Handles TagAPI stuff
					  */
					 if (plugin.getConfig().contains("kits." + className + ".tagPrefix") && plugin.useTags) {
						 String tagPrefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("kits." + className + ".tagPrefix"));
						 plugin.tags.put(p.getName(), tagPrefix);
						 TagAPI.refreshPlayer(p);
					 }

					 int slot;
					 
					 this.plugin.getConfig().set("kitHistory." + p.getName(), className); //Stores last kit for respawn
					 
					 for (slot = 0; slot<=35; slot++) {
						 ItemStack i = new ItemStack(0);
						 String getSlot = plugin.getConfig().getString("kits." + className + ".items." + slot);
						 
						 if (!(plugin.getConfig().getString("kits." + className + ".items." + slot).equals(null)) || !(plugin.getConfig().getString("kits." + className + ".items." + slot).equals("0"))) {
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
								 i.setAmount(1); //Default amount is 1
							 }
							 
							 if (plugin.getConfig().contains("kits." + className + ".items" + ".names." + slot) ) {
								 //get item name
								 String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("kits." + className + ".items" + ".names." + slot));
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
					 String getHelmet = plugin.getConfig().getString("kits." + className + ".items" + ".helmet");
					 String getChestplate = plugin.getConfig().getString("kits." + className + ".items" + ".chestplate");
					 String getLeggings = plugin.getConfig().getString("kits." + className + ".items" + ".leggings");
					 String getBoots = plugin.getConfig().getString("kits." + className + ".items" + ".boots");
					 
					 //These hold the chosen colours for dying
					 int helmColor = 0;
					 int chestColor = 0;
					 int legColor = 0;
					 int bootColor = 0;
					 
					 /**
					  * Main item stacks for various armour types.
					  * They will not necessarily all be used, only those
					  * that the user wishes to use and has defined in the config.
					  */
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
					 
					 //ItemStack for final armour items
					 ItemStack finalHelmet = null;
					 ItemStack finalChestplate = null;
					 ItemStack finalLeggings = null;
					 ItemStack finalBoots = null;
					 
					 //Dying leather armour
					 if (plugin.getConfig().contains("kits." + className + ".items" + ".helmetColor")) {
						  helmColor = Integer.parseInt(plugin.getConfig().getString("kits." + className + ".items.helmetColor").replace("#", ""), 16); 
						  lhelmet = this.plugin.setColor(lhelmet, helmColor);	  
					 }
					 
					 if (plugin.getConfig().contains("kits." + className + ".items" + ".chestplateColor")) {
						  chestColor = Integer.parseInt(plugin.getConfig().getString("kits." + className + ".items.chestplateColor").replace("#", ""), 16);  
						  lchestplate = this.plugin.setColor(lchestplate, chestColor);
					 }
					 
					 if (plugin.getConfig().contains("kits." + className + ".items" + ".leggingColor")) {
						  legColor = Integer.parseInt(plugin.getConfig().getString("kits." + className + ".items.leggingColor").replace("#", ""), 16); 
						  lleggings = this.plugin.setColor(lleggings, legColor);
					 }
					 
					 if (plugin.getConfig().contains("kits." + className + ".items" + ".bootColor")) {
						  bootColor = Integer.parseInt(plugin.getConfig().getString("kits." + className + ".items.bootColor").replace("#", ""), 16);   
						  lboots= this.plugin.setColor(lboots, bootColor);
					 }

					 //Determine which type of armour they want to use
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
					 
					 if (plugin.getConfig().contains("kits." + className + ".items.helmetEnchant") && finalHelmet != null) {
						  for (String a : plugin.getConfig().getString("kits." + className + ".items.helmetEnchant").split(" ")) {
									String[] enchant = a.split(":");
									Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
									int levelInt = Integer.parseInt(enchant[1]);
									finalHelmet.addUnsafeEnchantment(enchantmentInt,levelInt);
							}
					  }
					 
					
					 
					 if (plugin.getConfig().contains("kits." + className + ".items.chestplateEnchant") && finalChestplate != null) {
						  for (String a : plugin.getConfig().getString("kits." + className + ".items.chestplateEnchant").split(" ")) {
									String[] enchant = a.split(":");
									Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
									int levelInt = Integer.parseInt(enchant[1]);
									finalChestplate.addUnsafeEnchantment(enchantmentInt,levelInt);
							}
					  }
					 
					 if (plugin.getConfig().contains("kits." + className + ".items.leggingsEnchant") && finalLeggings != null) {
						  for (String a : plugin.getConfig().getString("kits." + className + ".items.leggingsEnchant").split(" ")) {
									String[] enchant = a.split(":");
									Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
									int levelInt = Integer.parseInt(enchant[1]);
									finalLeggings.addUnsafeEnchantment(enchantmentInt,levelInt);
							}
					  }
					 
					 if (plugin.getConfig().contains("kits." + className + ".items.bootsEnchant") && finalBoots != null) {
						  for (String a : plugin.getConfig().getString("kits." + className + ".items.bootsEnchant").split(" ")) {
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
					 
					 if (plugin.getConfig().getBoolean("settings.once-per-life")) {
						 plugin.getConfig().set("dead." + p.getName(), true);
					 }
					 
					 if (plugin.getConfig().contains(("kits." + className + ".commands"))) {
						 List<String> commands = this.plugin.getConfig().getStringList("kits." + className + ".commands");
						 
						 for (String s : commands) {
							 s = s.replace("<player>", p.getName());
							 Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
						 }
					 
					 }
					 return true;
					 
				 } else {
					 plugin.PM.warn(p, "Please choose a valid kit!");
				 }
				 
			 
			 
		 } else {
			 plugin.PM.warn(p, "You may only use one kit per life!");
		 }
		return true;
		
	}

}
