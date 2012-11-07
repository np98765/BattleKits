package com.lavacraftserver.BattleKits;


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
	 * Permissions is BattleKits.use.fillall
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equals("fillall") || cmd.getName().equals("fullsoup")) {
			Player p = (Player) sender;
			ItemStack i = p.getItemInHand();
			ItemStack[] inv = p.getInventory().getContents();

			if (sender.hasPermission("BattleKits.use.fillall")) {
				//Get array of itemstack
				for (ItemStack slot : inv) {
				    if (slot.getType() != Material.BOWL) {
				    	plugin.PM.warn(p, "You have no empty bowls!");
						return true;
				   
				    }
				}
					
				} else {
					i.setType(Material.MUSHROOM_SOUP);
					return true;
				}
				
			} else {
				plugin.PM.warn(sender, "You don't have permission for this command.");
				return true;
			}
		
		return false;
	}
	
}