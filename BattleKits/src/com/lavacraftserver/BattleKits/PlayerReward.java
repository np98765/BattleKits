package com.lavacraftserver.BattleKits;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Class to handle command based rewards when killing another player
 * 
 * @author lol768
 * 
 */
public class PlayerReward implements Listener {
	private BattleKits plugin;
	public PlayerReward(BattleKits p) {
		plugin = p;
	} 

	/**
	 * Method that automatically fills in and does magic stuff
	 * @param l The list of actions
	 * @param attacker The attacker!
	 * @param val The number of kills
	 */
	public void processActions(List<String> l, Player attacker, int val) {
		for (String s : l) {
			s = s.replace("<player>", attacker.getName());
			s = s.replace("<kills>", Integer.toString(val));
			if (s.startsWith("/")) {
				s = s.substring(1);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);

			}

			if (s.startsWith("#")) {
				s = s.substring(1);
				s = ChatColor.translateAlternateColorCodes('&', s);
				attacker.sendMessage(s);

			}

			if (s.startsWith("!")) {
				s = s.substring(1);
				s = ChatColor.translateAlternateColorCodes('&', s);
				Bukkit.broadcastMessage(s);

			}
		}
	}

	/**
	 * This event handles PvP rewards + kill streaks
	 * @param event
	 */
	@EventHandler
	public void ede(PlayerDeathEvent event) {
		Player dead = event.getEntity();
		if ((boolean) plugin.checkSetting("killStreaks.enabled", dead, false)) {
			this.plugin.kitHistory.getConfig().set("killStreaks." + dead.getName(), 0);

		}

		if (dead.getKiller() != null && dead.getKiller() instanceof Player) {
			Player attacker = dead.getKiller();
			attacker = (Player) dead.getKiller();
			String name = attacker.getName();
			if ((boolean) plugin.checkSetting("killStreaks.enabled", attacker, false)) {
				int val = 1;
				val = val + this.plugin.kitHistory.getConfig().getInt("killStreaks." + attacker.getName());
				this.plugin.kitHistory.getConfig().set("killStreaks." + attacker.getName(), val);
				if (plugin.checkList("killStreaks.action" + val, attacker) != null) {
					this.processActions(plugin.checkList("killStreaks.action" + val, attacker), attacker, val);

				} else {
					if (plugin.checkList("killStreaks.actionDefault", attacker) != null) {
						this.processActions(plugin.checkList("killStreaks.actionDefault", attacker), attacker, val);

					}
				}
			} else {
				
			}

			if (plugin.global.getConfig().contains((dead.getWorld().getName() + ".rewards.killCommands")) && !(dead.getKiller().getName().equals(dead.getName()))) {
				List<String> commands = this.plugin.global.getConfig().getStringList(dead.getWorld().getName() + ".rewards.killCommands");

				for (String s : commands) {
					s = s.replace("<player>", name);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
				}

			} else {
				if (plugin.global.getConfig().contains(("rewards.killCommands"))  && !(dead.getKiller().getName().equals(dead.getName()))) {
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
