package com.lol768.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
	
	@EventHandler
	public void onUse(PlayerInteractEvent e) {
	    if (e.getPlayer().getItemInHand() != null) {
	        if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().startsWith(ChatColor.RESET + "" + ChatColor.RESET)) {
	            e.getPlayer().getItemInHand().setDurability(e.getPlayer().getItemInHand().getType().getMaxDurability());
	        }
	    }
	}
	

	/**
	 * Death event that resets lives so that Player can get kits again
	 * @param event - EntityDamageEvent
	 */
	 @EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) { 
			final Player p = (Player) event.getEntity();
			Integer i = 0;
			if (plugin.wh != null) {
				if (plugin.wh.deaths.get(p.getName()) != null) {
					i = plugin.wh.deaths.get(p.getName());
				}
				i++;
				plugin.wh.deaths.put(p.getName(), i);
				if (p.getKiller() != null) {
					i = 0;
					if (plugin.wh.kills.get(p.getKiller().getName()) != null) {
						i = plugin.wh.kills.get(p.getKiller().getName());
					}
					i++;
					plugin.wh.kills.put(p.getKiller().getName(), i);
				}
			}
			if ( plugin.kitHistory.getConfig().contains("dead." + p.getName())) { 

				if ((boolean) plugin.checkSetting("settings.once-per-life", p, false)) {
					plugin.kitHistory.getConfig().set("dead." + p.getName(), null);
				}
				
				if ((boolean) plugin.checkSetting("settings.show-kit-info-on-respawn", p, false)) {
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						  public void run() {
							  plugin.PM.notify(p, "You may now use a kit");
						  }
						}, 60L);
					
				}
			
			}
		}
	}
}
