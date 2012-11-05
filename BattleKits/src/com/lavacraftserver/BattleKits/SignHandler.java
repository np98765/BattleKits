package com.lavacraftserver.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignHandler implements Listener {
	private BattleKits plugin;

	public SignHandler(BattleKits plugin) {
		this.plugin = plugin;

	}
	
	@EventHandler
	public void signClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST) {
			Sign s = (Sign) e.getClickedBlock();
			String[] lines = s.getLines();
			if (lines.length > 1 && lines[0].equals(ChatColor.DARK_RED + "[BattleKits]")) {
				
				if (p.hasPermission("BattleKits.sign.use")) {
					
					if (!plugin.getConfig().contains("kits." + lines[1])) {
						plugin.PM.warn(p, "That kit does not exist!");

					} else {
						//TODO: Give kit
						if (plugin.getConfig().getBoolean("signs.ignore-permissions")) {}
						if (plugin.getConfig().getBoolean("signs.ignore-costs")) {}

					}
					
				} else {
					plugin.PM.warn(p, "You don't have permission to use kit signs");

				}
			}
		}
	}

	@EventHandler
	public void signEdit(SignChangeEvent e) {
		String[] lines = e.getLines();
		Player p = e.getPlayer();

		if (lines.length > 1 && lines[0].equalsIgnoreCase("[BattleKits]")) {
			
			if (p.hasPermission("BattleKits.sign.create")) {
				
				if (plugin.getConfig().contains("kits." + lines[1])) {
					e.setLine(0, ChatColor.DARK_RED + "[BattleKits]");
					plugin.PM.notify(p, "Kit sign created successfully!");
				
				} else {
					e.getBlock().breakNaturally();
					plugin.PM.warn(p, "That kit does not exist!");
				}
			} else {
				plugin.PM.warn(p, "You don't have permission to create kit signs");

			}
		}
	}
}
