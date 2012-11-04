package com.lavacraftserver.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathEvent implements Listener {
	
	private BattleKits plugin;
	
	public DeathEvent(BattleKits plugin) {
		this.plugin = plugin;
		
	}
	
	@EventHandler
	public void onPlayerDeath (EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			if (plugin.getConfig().getBoolean("settings.once-per-life") == true) {
				Player p = (Player) event.getEntity();
				plugin.getConfig().set("dead." + p.getName(), null);
				if (plugin.getConfig().getBoolean("settings.show-kit-info-on-respawn")) {
					p.sendMessage(ChatColor.GREEN + "You may now use a kit");
				}
			}
		}
	}
}
