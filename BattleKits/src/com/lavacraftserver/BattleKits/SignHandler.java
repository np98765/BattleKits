package com.lavacraftserver.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignHandler implements Listener {
	private BattleKits plugin;

	public SignHandler(BattleKits plugin) {
		this.plugin = plugin;

	}

	@EventHandler
	public void signEdit(SignChangeEvent e) {
		String[] lines = e.getLines();
		Player p = e.getPlayer();

		if (lines.length > 1 && lines[0].equalsIgnoreCase("[BattleKits]")) {
			if (plugin.getConfig().contains("kits." + lines[1])) {
				e.setLine(0, ChatColor.DARK_RED + "[BattleKits]");
				plugin.PM.notify(p, "Kit sign created successfully!");
			
			} else {
				e.getBlock().breakNaturally();
				plugin.PM.notify(p, "That kit does not exist!");
			}
		}
	}
}
