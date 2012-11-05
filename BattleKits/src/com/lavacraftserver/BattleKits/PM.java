package com.lavacraftserver.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PM {

	BattleKits plugin;
	
	/**
	* Constructor method used when creating instance of this class
	* Used so we have access to the main plugin & config etc
	* @param instance - Instance of BattleKits.java
	*/
	public PM(BattleKits instance) {
		plugin = instance;
	}

	/**
	 * Method to keep messages consistent
	 * Sends a red coloured warning to the supplied player
	 * @param player - The player to send the warning to
	 * @param message - The message to send them
	 */
	public void warn(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "] " + ChatColor.RED + message);
	}
	
	/**
	 * Method to keep messages consistent
	 * Sends a red coloured warning to the supplied sender
	 * Supports console & uncast player (CommandSender)
	 * @param sender - The CommandSender to send the warning to
	 * @param message - The message to send them
	 */
	public void warn(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "] " + ChatColor.RED + message);
	}
	
	/**
	 * Method to keep messages consistent
	 * Sends a yellow notification to the supplied player
	 * @param player - The player to send the message to
	 * @param message - The message to send them
	 */
	public void notify(CommandSender player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "]" + ChatColor.YELLOW + message);
	}
	
	/**
	 * Method to keep messages consistent
	 * Sends a yellow notification to the supplied sender
	 * @param player - The player to send the message to
	 * @param message - The message to send them
	 */
	public void notify(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "]" + ChatColor.YELLOW + message);
	}

}
