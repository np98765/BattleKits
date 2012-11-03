package com.lavacraftserver.BattleKits;

import java.util.HashSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathEvent implements Listener {
	
	public static HashSet<Player> death;
	
	private BattleKits plugin;
	
	public DeathEvent(BattleKits plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerDeath (EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			if (plugin.getConfig().getBoolean("settings.once-per-life") == true) {
				Player p = (Player) event.getEntity();
				if (death.contains(p)) {
					p.sendMessage("removed from dlist");
					death.remove(p);
				}
			}
		}
	}
}
