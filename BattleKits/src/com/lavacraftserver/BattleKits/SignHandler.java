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

	/**
	 * Constructor method used when creating instance of this class
	 * Used so we have access to the main plugin & config etc
	 * @param plugin - Instance of BattleKits.java
	 */
	public SignHandler(BattleKits plugin) {
		this.plugin = plugin;

	}
	
	/**
	 * Event used to handle when a player clicks a sign
	 * @param e PlayerInteractEvent
	 */
	@EventHandler
	public void signClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST) {
			Sign s = (Sign) e.getClickedBlock().getState();
			String[] lines = s.getLines();
			if (lines.length > 1 && lines[0].equals(ChatColor.DARK_RED + "[BattleKits]")) {
				
				if (p.hasPermission("BattleKits.sign.use")) {
					
					if (!plugin.getConfig().contains("kits." + lines[1])) {
						plugin.PM.warn(p, "That kit does not exist!");

					} else {
						plugin.cbk.supplyKit(p, lines[1], plugin.getConfig().getBoolean("ignore-permissions"), plugin.getConfig().getBoolean("ignore-costs"), plugin.getConfig().getBoolean("ignore-world-restriction"), plugin.getConfig().getBoolean("ignore-lives-restriction"));

					}
					
				} else {
					plugin.PM.warn(p, "You don't have permission to use kit signs");

				}
			}
		}
	}

	/**
	 * Event when player clicks done after writing text on a sign
	 * Used to check for Kit signs
	 * @param e SignChangeEvent
	 */
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
