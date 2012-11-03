package com.lavacraftserver.BattleKits;

import java.util.Set;

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
	public void onRespawn (PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		 Set<String> keys = plugin.getConfig().getConfigurationSection("kits").getKeys(false);
		 String kit = "a";
		if (p.hasPermission("BattleKits.auto." + kit)) {
			p.performCommand("pvp " + kit);
		}
	}

}
