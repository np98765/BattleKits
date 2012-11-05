package com.lavacraftserver.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PM {

	public void warn(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "] " + ChatColor.RED + message);
	}
	
	public void notify(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "BattleKits" + ChatColor.GRAY + "]" + ChatColor.YELLOW + message);
	}
	
}
