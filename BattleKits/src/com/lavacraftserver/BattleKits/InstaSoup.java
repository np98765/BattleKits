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
	
	/**
	* Constructor method used when creating instance of this class
	* Used so we have access to the main plugin & config etc
	* @param p - Instance of BattleKits.java
	*/
	public InstaSoup(BattleKits p) {
		this.plugin = p;
	}

	/**
	 * Event that checks if user is right clicking with soup
	 * Then automatically refills the bowl and adds health/hunger
	 * @param event - PlayerInteractEvent
	 */
	@EventHandler
	public void onInteractEvent(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (p.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
				if (plugin.global.getBoolean("settings.instant-soup-drink")) {
					if (plugin.global.getString("instant-soup-drink.replenish-type").equals("hunger")) {
						ItemStack bowl = new ItemStack(Material.BOWL, 1);

						if (p.getFoodLevel() + 6 <= 20) { //Only add some hunger back on
							event.setCancelled(true);
							p.getInventory().setItemInHand(bowl);
							p.setFoodLevel(p.getFoodLevel() + 6);
						}
						
						if (p.getFoodLevel() + 6 > 20) { //Hunger close to max, so refill it
							event.setCancelled(true);
							p.getInventory().setItemInHand(bowl);
							p.setFoodLevel(20);
						}
					}
					
					if (plugin.global.getString("instant-soup-drink.replenish-type").equals("health")) {
						ItemStack bowl = new ItemStack(Material.BOWL, 1);

						if (p.getHealth() + 6 <= 20) { //Only add some health back on
							event.setCancelled(true);
							p.getInventory().setItemInHand(bowl);
							p.setHealth(p.getHealth() + 6);
						}
						
						if (p.getHealth() + 6 > 20) { //Health close to max, so refill it
							event.setCancelled(true);
							p.getInventory().setItemInHand(bowl);
							p.setHealth(20);
						}
					}
				}
			}
		}
	}
}
