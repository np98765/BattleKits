package com.lavacraftserver.BattleKits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnKit implements Listener {

	private BattleKits plugin;
	
	/**
	* Constructor method used when creating instance of this class
	* Used so we have access to the main plugin & config etc
	* @param instance - Instance of Battlekits.getConfig().java
	*/
	public RespawnKit(BattleKits plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Multi-world config accessor
	 * @param String path - The setting path to look for (e.g. settings.disable-xp)
	 * @param Player p - Player to get world from
	 * @param Object defaultValue - If empty, use this
	 * @return Object - result
	 */
	public Object checkSetting(String path, Player p, Object defaultValue) {
		if (plugin.global.getConfig().contains(p.getWorld().getName() + "." + path)) {
			//We have an override
			return plugin.getConfig().get(p.getWorld().getName() + "." + path);
		} else {
			if (plugin.global.getConfig().contains(path)) {
				return plugin.global.getConfig().get(path);
			} else {
				return defaultValue;
			}
		}
		
	}

	/**
	 * Handles automatically adding kit back to user on respawn if enabled
	 * @param event
	 */
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		// Set<String> keys = plugin.getConfig().getConfigurationSection("kits").getKeys(false);
		final Player p = event.getPlayer();
		final String kit = plugin.kitHistory.getConfig().getString("kitHistory." + p.getName());

		if (kit != null && p.hasPermission("Battlekits.auto." + kit) && !(boolean) checkSetting("settings.override-disable-respawn-kits", p, false)) {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.cbk.supplyKit(p, kit, false, false, false, false);
				}
			}, 20L);
		}
	}
}
