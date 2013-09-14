package com.lol768.battlekits.commands;


import com.lol768.battlekits.BattleKits;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandRefillAll implements CommandExecutor {

	public BattleKits plugin;
	
	/**
	 * Thanks lol, I should start making my own code, I hope you liked this though -Maple
	 */
	public CommandRefillAll(BattleKits p) {
		this.plugin = p;
	}

	/**
	 * Executed when /fillall or /fullsoup is used
	 * Permissions is Battlekits.getConfig().use.fillall
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equals("fillall") || cmd.getName().equals("fullsoup")) {
			Player p = (Player) sender;
		    ItemStack[] inv = p.getInventory().getContents();
		    boolean gotBowl = false;
			if (sender.hasPermission("Battlekits.use.fillall")) {
				//Get array of itemstack
				for (ItemStack slot : inv) {
				    if (slot != null && slot.getType() == Material.BOWL) { //Check for NPE 
				    	gotBowl = true;
				    	slot.setType(Material.MUSHROOM_SOUP);
				    }
				}
				
				if (!gotBowl) {
					plugin.PM.warn(p, "You have no empty bowls!");
					return true; 
				}
				return true;
				
			} else {
				plugin.PM.warn(sender, "You don't have permission for this command.");
				return true;
			}
		}
		
		return false;
	}
	
}