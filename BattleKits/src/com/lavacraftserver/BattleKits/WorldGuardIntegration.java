package com.lavacraftserver.BattleKits;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardIntegration implements Listener {
	
	private BattleKits plugin;
	private RegionManager rm;
	private WorldGuardPlugin wgp;
	/**
	* Constructor method used when creating instance of this class
	* Used so we have access to the main plugin & config etc
	* @param instance - Instance of Battlekits.getConfig().java
	*/
	public WorldGuardIntegration(BattleKits plugin) {
		this.plugin = plugin;
		wgp =null;
		

	}
	
	@EventHandler
	public void MoveEvent(PlayerMoveEvent e) { //Hog
		if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
			Location loc = e.getTo();
			rm = wgp.getRegionManager(loc.getWorld());
			Iterator<ProtectedRegion> regions = rm.getApplicableRegions(loc).iterator();
			String lastRegion = "none";
			String s = "";
			while(regions.hasNext()){
			    lastRegion = regions.next().getId();
			}
			plugin.getLogger().info("Player in region " + lastRegion);
			if (lastRegion != "") {
				if (!((String)plugin.checkSetting("experimental.region-commands." + lastRegion, e.getPlayer(), "nope")).equals("nope")) {
					s = (String) plugin.checkSetting("experimental.region-commands." + lastRegion, e.getPlayer(), "nope");
	
					
				} else {
					if (!((String)plugin.checkSetting("experimental.region-commands.others", e.getPlayer(), "nope")).equals("nope")) {
						s = (String) plugin.checkSetting("experimental.region-commands.others", e.getPlayer(), "nope");
					}
	
				}
			} else {
				if (!((String)plugin.checkSetting("experimental.region-commands.none", e.getPlayer(), "nope")).equals("nope")) {
					s = (String) plugin.checkSetting("experimental.region-commands.none", e.getPlayer(), "nope");
				}
			}
			s = s.replace("<player>", e.getPlayer().getName());
			
			if (s != "") {
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), s);
			}

			
		}
	}

}
