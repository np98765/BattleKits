package com.lavacraftserver.BattleKits;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Class to handle command based rewards when killing another player
 * @author lol768
 *
 */
public class PlayerReward implements Listener {
	private BattleKits plugin;
	public PlayerReward(BattleKits p) {
		this.plugin = p;
	}
	
	@EventHandler
	public void ede(PlayerDeathEvent event) {
		Player dead = event.getEntity();
		Player attacker;
		if (dead.getLastDamageCause().getEntity() instanceof Player) {
			attacker = (Player) dead.getKiller();
			String name = attacker.getName();
			if (plugin.global.getConfig().contains((dead.getWorld().getName() + ".rewards.killCommands"))) {
					 List<String> commands = this.plugin.global.getConfig().getStringList(dead.getWorld().getName() + ".rewards.killCommands");
					 
					 for (String s : commands) {
						 s = s.replace("<player>", name);
						 Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
					 }
				 
			} else {
				 if (plugin.global.getConfig().contains(("rewards.killCommands"))) {
					 List<String> commands = this.plugin.global.getConfig().getStringList("rewards.killCommands");
					 
					 for (String s : commands) {
						 s = s.replace("<player>", name);
						 Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
					 }
				 
				 }
			}
				
		}
	}

}
