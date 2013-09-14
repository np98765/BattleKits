package com.lol768.battlekits.listeners;

import com.lol768.battlekits.BattleKits;
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
	 * Handles automatically adding kit back to user on respawn if enabled
	 * @param event
	 */
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		// Set<String> keys = plugin.getConfig().getConfigurationSection("kits").getKeys(false);
		final Player p = event.getPlayer();
		final String kit = plugin.kitHistory.getConfig().getString("kitHistory." + p.getName());

		if (kit != null && p.hasPermission("Battlekits.auto." + kit) && !(boolean) plugin.checkSetting("settings.override-disable-respawn-kits", p, false)) {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.cbk.supplyKit(p, kit, false, false, false, false);
				}
			}, 20L);
		}
	}
}
