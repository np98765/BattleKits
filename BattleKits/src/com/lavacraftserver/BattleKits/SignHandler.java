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
import org.bukkit.inventory.ItemStack;

public class SignHandler implements Listener {
	private BattleKits plugin;

	/**
	 * Constructor method used when creating instance of this class
	 * Used so we have access to the main plugin & config etc
	 * @param plugin - Instance of Battlekits.getConfig().java
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
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
		
			if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST) {
				Sign s = (Sign) e.getClickedBlock().getState();
				String[] lines = s.getLines();
				if (lines.length > 1 && lines[0].equals(ChatColor.DARK_RED + "[" + ChatColor.stripColor(plugin.global.getConfig().getString("brand")) + "]")) {
					
					if (p.hasPermission("Battlekits.sign.use")) {
						
						if (!plugin.kits.getConfig().contains("kits." + lines[1])) {
							if (lines[1].equals("soupFill")) {
								if (!p.hasPermission("BattleKits.soupFill")) {
									plugin.PM.warn(p, "You don't have permission for this!");
									return;
								}

								Boolean rez = true;
								if ((Double) plugin.checkSetting("signs.soupFillCost", p, null) != null && BattleKits.economy != null) {
									rez = plugin.buyNeutral((Double) plugin.checkSetting("signs.soupFillCost", p, null), p.getName());
									
								} else {
									plugin.getLogger().info(BattleKits.economy + " " + plugin.checkSetting("signs.soupFillCost", p, null));
								}
								if (rez) {
									for(ItemStack i : p.getInventory().getContents()) {
										if(i == null) {
											p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
										}
									}
								}
							} else {
								plugin.PM.warn(p, "That kit does not exist!");
							}
	
						} else {
							plugin.getLogger().info("Supplying kit - " + lines[1]);
							plugin.cbk.supplyKit(p, lines[1], (boolean) plugin.checkSetting("signs.ignore-permissions", p, false), (boolean) plugin.checkSetting("signs.ignore-costs", p, false), (boolean) plugin.checkSetting("signs.ignore-lives-restriction", p, false), (boolean) plugin.checkSetting("signs.ignore-world-restriction", p, false));
	
						}
						
					} else {
						plugin.PM.warn(p, "You don't have permission to use kit signs");
	
					}
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

		if (lines.length > 1 && lines[0].equalsIgnoreCase("[" + ChatColor.stripColor(plugin.global.getConfig().getString("brand")) + "]")) {
			
			if (p.hasPermission("Battlekits.sign.create")) {
				
				if (plugin.kits.getConfig().contains("kits." + lines[1]) || lines[1].equals("soupFill")) {
					e.setLine(0, ChatColor.DARK_RED + "[" + ChatColor.stripColor(plugin.global.getConfig().getString("brand")) + "]");
					plugin.PM.notify(p, "Kit sign created successfully!");
				
				} else {
					e.getBlock().breakNaturally();
					plugin.PM.warn(p, "That kit does not exist!");
				}
			} else {
				plugin.PM.warn(p, "You don't have permission to create kit signs");
				e.getBlock().breakNaturally();

			}
		}
	}
}
