package com.lavacraftserver.BattleKits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathEvent implements Listener {

	private BattleKits plugin;

	/**
	* Constructor method used when creating instance of this class
	* Used so we have access to the main plugin & config etc
	* @param instance - Instance of Battlekits.getConfig().java
	*/
	public DeathEvent(BattleKits plugin) {
		this.plugin = plugin;

	}
	

	/**
	 * Death event that resets lives so that Player can get kits again
	 * @param event - EntityDamageEvent
	 */
	 @EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) { 
			Player p = (Player) event.getEntity();
			if (event.getDamage() > p.getHealth()) { //Make sure they'll die :)

				if ((boolean) plugin.checkSetting("settings.once-per-life", p, false)) {
					plugin.kitHistory.getConfig().set("dead." + p.getName(), null);
				}
	
				if ((boolean) plugin.checkSetting("settings.show-kit-info-on-respawn", p, false)) {
					plugin.PM.notify(p, "You may now use a kit");
				}
			
			}
		}
	}
}
