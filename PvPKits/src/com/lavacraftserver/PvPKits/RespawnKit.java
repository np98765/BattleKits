package com.lavacraftserver.PvPKits;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnKit implements Listener {
	
	private PvPKits plugin;
	
	public RespawnKit(PvPKits plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onRespawn (PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		 Set<String> keys = plugin.getConfig().getConfigurationSection("kits").getKeys(false);
		 String kit = "a";
		if (p.hasPermission("pvpkits.auto." + kit)) {
			p.performCommand("pvp " + kit);
		}
	}

}
