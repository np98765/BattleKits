package com.lavacraftserver.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PM {

	BattleKits plugin;
	
	public PM(BattleKits instance) {
		plugin = instance;
	}

	public void warn(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "] " + ChatColor.RED + message);
	}
	
	public void warn(CommandSender player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "] " + ChatColor.RED + message);
	}
	
	public void notify(CommandSender player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "]" + ChatColor.YELLOW + message);
	}
	
	public void notify(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "]" + ChatColor.YELLOW + message);
	}

}
