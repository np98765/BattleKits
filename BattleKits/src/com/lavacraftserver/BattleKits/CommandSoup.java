package com.lavacraftserver.BattleKits;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSoup implements CommandExecutor {

	public BattleKits plugin;
	
	/**
	* Constructor method used when creating instance of this class
	* Used so we have access to the main plugin & config etc
	* @param plugin - Instance of Battlekits.getConfig().java
	*/
	public CommandSoup(BattleKits p) {
		this.plugin = p;
	}

	/**
	 * Method that is executed every time a soup command or alias is used
	 * Takes some info on the command, such as arguments
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equals("soup") || cmd.getName().equals("refill") || cmd.getName().equals("stew")) {
			Player p = (Player) sender;
			ItemStack i = p.getItemInHand();

			if (sender.hasPermission("Battlekits.use.soup")) {
				//TODO: @MapleFighter -- implement whole inventory
				if (i.getType() != Material.BOWL) {
					plugin.PM.warn(p, "You must have an empty bowl in your hand");
					return true;
					
				} else {
					i.setType(Material.MUSHROOM_SOUP);
					return true;
				}
				
			} else {
				plugin.PM.warn(sender, "You don't have permission for this command.");
				return true;
			}
		
		}
		return false;
	}
	
}