package com.lavacraftserver.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PM {

	public BattleKits plugin;
	
	/**
	* Constructor method used when creating instance of this class
	* Used so we have access to the main plugin & config etc
	* @param instance - Instance of Battlekits.getConfig().java
	*/
	public PM(BattleKits instance) {
		plugin = instance;
	}
	
	/**
	 * Logger method which supports localisation
	 * 
	 * @param message
	 */
	public void trLogger(String message, String type) {
		String ld = plugin.global.getConfig().getString("language"); //User's selected language
		
		if (plugin.global.getConfig().contains("messages." + ld + "." + message)) {
			message = plugin.global.getConfig().getString("messages." + ld + "." + message);
		} else {
			try {
				throw new Exception("Gimme class name");
			} catch( Exception e) {
				plugin.getLogger().severe("Not given a valid language in config at " + e.getStackTrace()[1].getClassName() + "." + e.getStackTrace()[1].getMethodName() + "!" );
			}
			return;
		}
		if (type.equals("info")) {
			plugin.getLogger().info(message);
			return;

		}

		if (type.equals("warn") || type.equals("warning")) {
			plugin.getLogger().warning(message);

			return;

		}

		if (type.equals("error") || type.equals("severe")) {
			plugin.getLogger().severe(message);

			return;

		}
		try {
			throw new Exception("Gimme class name");
		} catch( Exception e) {
			plugin.getLogger().severe("Not given a valid type in " + e.getStackTrace()[1].getClassName() + "." + e.getStackTrace()[1].getMethodName() + "!" );
		}


	}

	/**
	 * Method to keep messages consistent
	 * Sends a red coloured warning to the supplied player
	 * @param player - The player to send the warning to
	 * @param message - The message to send them
	 */
	public void warn(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + plugin.global.getConfig().getString("brand") + ChatColor.GRAY + "]" + ChatColor.RED + " " + message);
	}
	
	/**
	 * Method to keep messages consistent
	 * Sends a red coloured warning to the supplied sender
	 * Supports console & uncast player (CommandSender)
	 * @param sender - The CommandSender to send the warning to
	 * @param message - The message to send them
	 */
	public void warn(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + plugin.global.getConfig().getString("brand") + ChatColor.GRAY + "]" + ChatColor.RED +" " +  message);
	}
	
	/**
	 * Method to keep messages consistent
	 * Sends a yellow notification to the supplied player
	 * @param player - The player to send the message to
	 * @param message - The message to send them
	 */
	public void notify(CommandSender player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + plugin.global.getConfig().getString("brand") + ChatColor.GRAY + "]" + ChatColor.YELLOW + " " + message);
	}
	
	/**
	 * Method to keep messages consistent
	 * Sends a yellow notification to the supplied sender
	 * @param player - The player to send the message to
	 * @param message - The message to send them
	 */
	public void notify(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + plugin.global.getConfig().getString("brand") + ChatColor.GRAY + "]" + ChatColor.YELLOW + " " + message);
	}

}
