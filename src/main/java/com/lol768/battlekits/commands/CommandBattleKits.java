package com.lol768.battlekits.commands;

import com.lol768.battlekits.BattleKits;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandBattleKits implements CommandExecutor {

    public BattleKits plugin;

    public CommandBattleKits(BattleKits p) {
        this.plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("battlekits")) {

            if (args.length == 0) {
                if (!sender.hasPermission("battlekits.listkits")) {
                    plugin.PM.warn(sender, "You do not have permission to use this command.");
                    return true;
                }
                String kit_ref = "";
                for (String s : plugin.kits.getConfig().getConfigurationSection("kits").getKeys(false)) {

                    if (sender.hasPermission("battlekits.use." + s)) {
                        s = ChatColor.GREEN + s + ChatColor.RESET;
                    } else {
                        s = ChatColor.RED + s + ChatColor.RESET;
                    }
                    if (plugin.kits.getConfig().contains("kits." + ChatColor.stripColor(s) + ".cost")) {
                        s = s + " (" + plugin.kits.getConfig().getDouble("kits." + ChatColor.stripColor(s) + ".cost") + ")"; //Builds list of kits incl. cost of each
                    }

                    kit_ref = kit_ref + s + ", "; //Add new kit info to String
                }
                kit_ref = kit_ref.substring(0, kit_ref.length() - 2); //remove last comma and space
                plugin.PM.notify(sender, "Available kits (cost): "); //Header for info
                plugin.PM.notify(sender, "Accessible kits are marked green.");
                sender.sendMessage(kit_ref); //Send the kit list
                return true;
            }
            if (args.length != 1 && args.length != 2) {
                plugin.PM.warn(sender, "Need at least one argument");
                return true;
            }

            /**
             * This command reloads the BattleKit config
             */
            if (args[0].equals("reload")) {
                if (!sender.hasPermission("battlekits.config.reload")) {
                    plugin.PM.warn(sender, "You don't have permission to use this command.");
                    return true;
                }
                plugin.kits.reloadConfig();
                plugin.global.reloadConfig();
                plugin.PM.notify(sender, "Kit and global configs reloaded!");

                return true;
            }


            /**
             * This handles checking the sender to ensure they are a player and
             * giving them the requested kit.
             */
            if (!(sender instanceof Player)) {
                if (args.length != 2) {
                    plugin.PM.warn(sender, "Usage for console: /<command> <kit> <player>");
                    return true;
                }
                Player p = Bukkit.getPlayer(args[1]);

                if (p == null) {
                    plugin.PM.warn(sender, "Couldn't locate specified player.");
                    return true;
                }

                supplyKit(p, args[0], (boolean) plugin.checkSetting("indirect.ignore-permissions", p, false), (boolean) plugin.checkSetting("indirect.ignore-costs", p, false), (boolean) plugin.checkSetting("indirect.ignore-lives-restriction", p, false), (boolean) plugin.checkSetting("indirect.ignore-world-restriction", p, false));
                return true;

            } else {
                if (args.length == 1) {
                    Player p = (Player) sender;
                    supplyKit(p, args[0], false, false, false, false); //Obey all restrictions (still adheres to config)
                    return true;
                } else {
                    Player p = Bukkit.getPlayer(args[1]);

                    if (p == null) {
                        plugin.PM.warn(sender, "Couldn't locate specified player.");
                        return true;
                    }

                    if (!sender.hasPermission("BattleKits.kit.other")) {
                        plugin.PM.warn(sender, "You don't have permission for indirect kit distribution.");
                        return true;

                    }

                    supplyKit(p, args[0], (boolean) plugin.checkSetting("indirect.ignore-permissions", p, false), (boolean) plugin.checkSetting("indirect.ignore-costs", p, false), (boolean) plugin.checkSetting("indirect.ignore-lives-restriction", p, false), (boolean) plugin.checkSetting("indirect.ignore-world-restriction", p, false));
                    return true;
                }

            }

        }
        return false;
    }

    /**
     * Method that tells us how much xp is required to reach a level
     *
     * @param level The level you want the player at
     * @return
     */
    public int getExpTolevel(int level) {
        if (level <= 16) {
            return (level * 17);
        } else {
            int r = level - 16;
            r = r * 3;
            r = (level * 17) + r;
            return r;
        }
    }

    /**
     * Big method that gives the player a specified kit
     *
     * @param p - The player to give the kit
     * @param className - The name of the kit to give them
     * @param ignorePerms - Whether or not to ignore the
     * Battlekits.getConfig().use permission requirements
     * @param ignoreCost - Whether or not to ignore the economy & cost settings
     * @param ignoreLives - Whether or not to ignore the 1 per life rule
     * @param ignoreWorldRestriction - Whether or not to ignore the world
     * restrictions
     * @return
     */
    public Boolean supplyKit(final Player p, String className, Boolean ignorePerms, Boolean ignoreCost, Boolean ignoreLives, Boolean ignoreWorldRestriction) {

        if (p.hasPermission("Battlekits.use." + className) || ignorePerms) { //Ensure they have permission to use the kit
        } else {
            plugin.PM.warn(p, "You do not have permission to use this kit!");
            return true;
        }

        /**
         * This if statement checks if the once-per-life rule is active, and
         * whether the user has already used a kit
         */
        if (!ignoreLives && plugin.global.getConfig().getBoolean("settings.once-per-life")) {
            if (!plugin.kitHistory.getConfig().contains("dead." + p.getName())) {
                Set<String> keys = plugin.kits.getConfig().getConfigurationSection("kits").getKeys(false); //Current kits in config

                /**
                 * Ensures that there are no restrictions for the user's current
                 * world
                 */
                if (keys.contains(className)) {
                    if (plugin.kits.getConfig().contains("kits." + className + ".active-in")) {
                        if (!(plugin.kits.getConfig().getString("kits." + className + ".active-in").contains("'" + p.getWorld().getName() + "'") || plugin.kits.getConfig().getString("kits." + className + ".active-in").equals("all"))) {
                            if (!ignoreWorldRestriction) {
                                plugin.PM.warn(p, "This kit is disabled in your current world (" + p.getWorld().getName() + ")");
                                return true;
                            }
                        }
                    }
                } else {
                    plugin.PM.warn(p, "Please choose a valid kit!");
                }
            } else {
                plugin.PM.warn(p, "You may only use one kit per life!");

                return true;
            }

        }

        /**
         * One-off purchases
         */
        if (BattleKits.economy != null && plugin.kits.getConfig().contains("kits." + className + ".cost") && plugin.kits.getConfig().getDouble("kits." + className + ".cost") != 0 && !ignoreCost && plugin.kits.getConfig().getBoolean("kits." + className + ".one-off-purchase")) {
            double cost = plugin.kits.getConfig().getDouble("kits." + className + ".cost");
            if (!plugin.kitHistory.getConfig().getBoolean(p.getName() + ".unlocked." + className)) {
                if (!plugin.buy(cost, p.getName())) {
                    return true;
                } else {
                    plugin.kitHistory.getConfig().set(p.getName() + ".unlocked." + className, true);
                    p.sendMessage(ChatColor.GREEN + "You have now permanently unlocked this kit!");

                }

            }

        }


        /**
         * Uses vault to charge user as specified in config
         */
        if (BattleKits.economy != null && plugin.kits.getConfig().contains("kits." + className + ".cost") && plugin.kits.getConfig().getDouble("kits." + className + ".cost") != 0 && !ignoreCost && !plugin.kits.getConfig().getBoolean("kits." + className + ".one-off-purchase")) {
            double cost = plugin.kits.getConfig().getDouble("kits." + className + ".cost");

            if (!plugin.buy(cost, p.getName())) {
                return true;
            }
        }

        p.getInventory().clear();
        p.getInventory().setArmorContents(null); //Gets rid of current kit + items to avoid duplication

        /**
         * Handles kit give message
         */
        if (plugin.kits.getConfig().contains("kits." + className + ".on-give-message")) {

            if (!plugin.kits.getConfig().getString("kits." + className + ".on-give-message").contains("&h")) { //User may wish to hide BattleKits prefix
                plugin.PM.notify(p, ChatColor.translateAlternateColorCodes('&', plugin.kits.getConfig().getString("kits." + className + ".on-give-message")));

            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.kits.getConfig().getString("kits." + className + ".on-give-message").replace("&h", "")));

            }

        }

        /**
         * XP 'showering'
         */
        if (plugin.kits.getConfig().contains("kits." + className + ".xpLevels")) {
            int amount = plugin.kits.getConfig().getInt("kits." + className + ".xpLevels");
            int required = getExpTolevel(amount);
            plugin.getLogger().info(Integer.toString(required));
            required = required - (int) p.getExp();
            int divisor = 5;
            int quotient = required / divisor;
            plugin.getLogger().info(Integer.toString(quotient) + " " + Integer.toString(required) + " " + Integer.toString(amount));
            int counter = 0;
            while (counter < quotient) {
                ExperienceOrb orb = (ExperienceOrb) p.getWorld().spawnEntity(p.getEyeLocation().add(0, 3, 0), EntityType.EXPERIENCE_ORB);
                orb.setExperience(divisor);
                counter++;
            }
            int remaining = required % divisor;
            ExperienceOrb orbRemaining = (ExperienceOrb) p.getWorld().spawnEntity(p.getEyeLocation().add(0, 3, 0), EntityType.EXPERIENCE_ORB);
            orbRemaining.setExperience(remaining);
        }

        /**
         * Handles TagAPI stuff
         */
        if (plugin.kits.getConfig().contains("kits." + className + ".tagPrefix") && plugin.useTags) {
            plugin.getLogger().info("TagAPI support removed pending native 1.5 API");


        }

        /**
         * Clears inventory when switching kit
         */
        if ((boolean) plugin.checkSetting("settings.clear-default", p, false)) {
            p.getInventory().clear();
        }

        int slot;
        this.plugin.kitHistory.getConfig().set("kitHistory." + p.getName(), className); //Stores last kit for respawn

        for (slot = 0; slot <= 35; slot++) {
            ItemStack i = new ItemStack(0);
            String getSlot = plugin.kits.getConfig().getString("kits." + className + ".items." + slot);

            if (plugin.kits.getConfig().contains("kits." + className + ".items." + slot) && !(plugin.kits.getConfig().getString("kits." + className + ".items." + slot).equals("0")) && !(plugin.kits.getConfig().getString("kits." + className + ".items." + slot).equals(""))) {
                String[] s = getSlot.split(" ");
                String[] item = s[0].split(":");

                //Sets the block/item
                i.setTypeId(Integer.parseInt(item[0]));

                //Sets the amount and durability
                if (item.length > 1) {
                    i.setAmount(Integer.parseInt(item[1]));

                    if (item.length > 2) {
                        i.setDurability((short) Integer.parseInt(item[2]));
                    }

                } else {
                    i.setAmount(1); //Default amount is 1
                }

                if (plugin.kits.getConfig().contains("kits." + className + ".items" + ".names." + slot)) {
                    //get item name
                    String name = ChatColor.translateAlternateColorCodes('&', plugin.kits.getConfig().getString("kits." + className + ".items" + ".names." + slot));
                    ItemMeta im = i.getItemMeta();
                    if (name.equals(ChatColor.RESET + "" + ChatColor.RESET)) {
                        im.setDisplayName(name + im.getDisplayName());
                    } else {
                        im.setDisplayName(name);
                    }


                    i.setItemMeta(im);

                }

                // Sets the enchantments and level
                Boolean first = true;

                if (s.length > 1) {
                    for (String a : s) {
                        if (!first) {
                            String[] enchant = a.split(":");
                            Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
                            int levelInt = Integer.parseInt(enchant[1]);
                            i.addUnsafeEnchantment(enchantmentInt, levelInt);
                        }
                        first = false;
                    }
                }
                p.getInventory().setItem(slot, i);
            }
        }

        //Sets the armor contents
        String getHelmet = plugin.kits.getConfig().getString("kits." + className + ".items" + ".helmet");
        String getChestplate = plugin.kits.getConfig().getString("kits." + className + ".items" + ".chestplate");
        String getLeggings = plugin.kits.getConfig().getString("kits." + className + ".items" + ".leggings");
        String getBoots = plugin.kits.getConfig().getString("kits." + className + ".items" + ".boots");



        //These hold the chosen colours for dying
        int helmColor = 0;
        int chestColor = 0;
        int legColor = 0;
        int bootColor = 0;

        /**
         * Main item stacks for various armour types. They will not necessarily
         * all be used, only those that the user wishes to use and has defined
         * in the config.
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
        if (plugin.kits.getConfig().contains("kits." + className + ".items" + ".helmetColor")) {
            helmColor = Integer.parseInt(plugin.kits.getConfig().getString("kits." + className + ".items.helmetColor").replace("#", ""), 16);
            lhelmet = this.plugin.setColor(lhelmet, helmColor);
        }

        if (plugin.kits.getConfig().contains("kits." + className + ".items" + ".chestplateColor")) {
            chestColor = Integer.parseInt(plugin.kits.getConfig().getString("kits." + className + ".items.chestplateColor").replace("#", ""), 16);
            lchestplate = this.plugin.setColor(lchestplate, chestColor);
        }

        if (plugin.kits.getConfig().contains("kits." + className + ".items" + ".leggingColor")) {
            legColor = Integer.parseInt(plugin.kits.getConfig().getString("kits." + className + ".items.leggingColor").replace("#", ""), 16);
            lleggings = this.plugin.setColor(lleggings, legColor);
        }

        if (plugin.kits.getConfig().contains("kits." + className + ".items" + ".bootColor")) {
            bootColor = Integer.parseInt(plugin.kits.getConfig().getString("kits." + className + ".items.bootColor").replace("#", ""), 16);
            lboots = this.plugin.setColor(lboots, bootColor);
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

        short s1 = (short) plugin.kits.getConfig().getInt("kits." + className + ".items.helmetDurability", -2);
        short s2 = (short) plugin.kits.getConfig().getInt("kits." + className + ".items.chestplateDurability", -2);
        short s3 = (short) plugin.kits.getConfig().getInt("kits." + className + ".items.leggingsDurability", -2);
        short s4 = (short) plugin.kits.getConfig().getInt("kits." + className + ".items.bootsDurability", -2);

        if (s1 == -1) {
            s1 = finalHelmet.getType().getMaxDurability();
        }
        if (s2 == -1) {
            s2 = finalChestplate.getType().getMaxDurability();
        }
        if (s3 == -1) {
            s3 = finalLeggings.getType().getMaxDurability();
        }
        if (s4 == -1) {
            s4 = finalBoots.getType().getMaxDurability();
        }

        if (plugin.kits.getConfig().getString("kits." + className + ".items.helmetName") != null) {
            ItemMeta im = finalHelmet.getItemMeta();
            String name = plugin.kits.getConfig().getString("kits." + className + ".items.helmetName");
            name = ChatColor.translateAlternateColorCodes('&', name);
            im.setDisplayName(name);
            finalHelmet.setItemMeta(im);
        }
        if (plugin.kits.getConfig().getString("kits." + className + ".items.chestplateName") != null) {
            ItemMeta im = finalChestplate.getItemMeta();
            String name = plugin.kits.getConfig().getString("kits." + className + ".items.chestplateName");
            name = ChatColor.translateAlternateColorCodes('&', name);
            im.setDisplayName(name);
            finalChestplate.setItemMeta(im);
        }
        if (plugin.kits.getConfig().getString("kits." + className + ".items.leggingsName") != null) {
            ItemMeta im = finalLeggings.getItemMeta();
            String name = plugin.kits.getConfig().getString("kits." + className + ".items.leggingsName");
            name = ChatColor.translateAlternateColorCodes('&', name);
            im.setDisplayName(name);
            finalLeggings.setItemMeta(im);
        }
        if (plugin.kits.getConfig().getString("kits." + className + ".items.bootsName") != null) {
            ItemMeta im = finalBoots.getItemMeta();
            String name = plugin.kits.getConfig().getString("kits." + className + ".items.bootsName");
            name = ChatColor.translateAlternateColorCodes('&', name);
            im.setDisplayName(name);
            finalBoots.setItemMeta(im);
        }

        if (s2 == -3) {
            s2 = finalChestplate.getType().getMaxDurability();
        }
        if (s3 == -3) {
            s3 = finalLeggings.getType().getMaxDurability();
        }
        if (s4 == -3) {
            s4 = finalBoots.getType().getMaxDurability();
        }

        if (finalHelmet != null && s1 != -2 && s1 != -3) {
            finalHelmet.setDurability(s1);
        }
        plugin.getLogger().info("Setting durability to " + s1);
        if (finalChestplate != null && s2 != -2 && s1 != -3) {
            finalChestplate.setDurability(s2);
        }
        if (finalLeggings != null && s3 != -2 && s1 != -3) {
            finalLeggings.setDurability(s3);
        }
        if (finalBoots != null && s4 != -2 && s1 != -3) {
            finalBoots.setDurability(s4);
        }




        if (plugin.kits.getConfig().contains("kits." + className + ".items.helmetEnchant") && finalHelmet != null) {
            for (String a : plugin.kits.getConfig().getString("kits." + className + ".items.helmetEnchant").split(" ")) {
                String[] enchant = a.split(":");
                Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
                int levelInt = Integer.parseInt(enchant[1]);
                finalHelmet.addUnsafeEnchantment(enchantmentInt, levelInt);
            }
        }



        if (plugin.kits.getConfig().contains("kits." + className + ".items.chestplateEnchant") && finalChestplate != null) {
            for (String a : plugin.kits.getConfig().getString("kits." + className + ".items.chestplateEnchant").split(" ")) {
                String[] enchant = a.split(":");
                Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
                int levelInt = Integer.parseInt(enchant[1]);
                finalChestplate.addUnsafeEnchantment(enchantmentInt, levelInt);
            }
        }

        if (plugin.kits.getConfig().contains("kits." + className + ".items.leggingsEnchant") && finalLeggings != null) {
            for (String a : plugin.kits.getConfig().getString("kits." + className + ".items.leggingsEnchant").split(" ")) {
                String[] enchant = a.split(":");
                Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
                int levelInt = Integer.parseInt(enchant[1]);
                finalLeggings.addUnsafeEnchantment(enchantmentInt, levelInt);
            }
        }

        if (plugin.kits.getConfig().contains("kits." + className + ".items.bootsEnchant") && finalBoots != null) {
            for (String a : plugin.kits.getConfig().getString("kits." + className + ".items.bootsEnchant").split(" ")) {
                String[] enchant = a.split(":");
                Enchantment enchantmentInt = new EnchantmentWrapper(Integer.parseInt(enchant[0]));
                int levelInt = Integer.parseInt(enchant[1]);
                finalBoots.addUnsafeEnchantment(enchantmentInt, levelInt);
            }
        }

        if (finalHelmet != null) {
            p.getInventory().setHelmet(finalHelmet);
        }
        if (finalChestplate != null) {
            p.getInventory().setChestplate(finalChestplate);
        }
        if (finalLeggings != null) {
            p.getInventory().setLeggings(finalLeggings);
        }
        if (finalBoots != null) {
            p.getInventory().setBoots(finalBoots);
        }
        p.updateInventory();
        if (plugin.global.getConfig().getBoolean("settings.once-per-life")) {
            plugin.kitHistory.getConfig().set("dead." + p.getName(), true);
        }

        if (plugin.kits.getConfig().contains(("kits." + className + ".commands"))) {
            List<String> commands = this.plugin.kits.getConfig().getStringList("kits." + className + ".commands");

            for (String s : commands) {
                s = s.replace("<player>", p.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
            }

        }




        return true;

    }
}
