package com.lavacraftserver.BattleKits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InstaSoup implements Listener {
	
	public BattleKits plugin;
	
	public InstaSoup(BattleKits p) {
		this.plugin = p;
	}
	
	@EventHandler
	public void onInteractEvent (PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (p.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
				
				if (plugin.getConfig().getBoolean("settings.instant-soup-drink")) {
					
					ItemStack bowl = new ItemStack(Material.BOWL, 1);
					
					if (p.getFoodLevel() + 6 <= 20) {						
						event.setCancelled(true);
						p.getInventory().setItemInHand(bowl);
						p.setFoodLevel(p.getFoodLevel() + 6);
					
					} else {
						
						if (p.getFoodLevel() +6 > 20) {
							event.setCancelled(true);
							p.getInventory().setItemInHand(bowl);
							p.setFoodLevel(20);
						}
					}
				}
			}
		}
	}
}
