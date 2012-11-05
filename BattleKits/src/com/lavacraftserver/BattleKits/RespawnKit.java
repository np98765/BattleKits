package com.lavacraftserver.BattleKits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnKit implements Listener {

	private BattleKits plugin;

	public RespawnKit(BattleKits plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {

		// Set<String> keys = plugin.getConfig().getConfigurationSection("kits").getKeys(false);
		final Player p = event.getPlayer();
		final String kit = plugin.getConfig().getString("kitHistory." + p.getName());

		if (kit != null && p.hasPermission("BattleKits.auto." + kit) && (!plugin.getConfig().getBoolean("settings.override-disable-respawn-kits"))) {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

						public void run() {
							p.performCommand("pvp " + kit);
						}
					}, 20L);

		}
	}
}
