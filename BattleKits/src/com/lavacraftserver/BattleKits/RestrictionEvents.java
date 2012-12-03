package com.lavacraftserver.BattleKits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class RestrictionEvents implements Listener {
	private BattleKits plugin;

	/**
	 * Constructor method used when creating instance of this class Used so we
	 * have access to the main plugin & config etc
	 * 
	 * @param instance
	 *            - Instance of Battlekits.getConfig().java
	 */
	public RestrictionEvents(BattleKits plugin) {
		this.plugin = plugin;
	}
	

	@EventHandler
	public void itemDrop(PlayerDropItemEvent e) {
			e.setCancelled((boolean) plugin.checkSetting("settings.disable-dropping-items", e.getPlayer(), false));
	}

	@EventHandler
	public void itemDrop(CraftItemEvent e) {
		
		if ((boolean) plugin.checkSetting("settings.disable-crafting", (Player) e.getWhoClicked(), false)) {
		e.setCancelled(true);
		}

	}

	@EventHandler
	public void pickup(PlayerPickupItemEvent e) {
		e.setCancelled((boolean) plugin.checkSetting("settings.disable-pickup-items",  e.getPlayer(), false));

	}

	@EventHandler
	public void bpe(BlockPlaceEvent e) {
		e.setCancelled((boolean) plugin.checkSetting("settings.disable-block-place", e.getPlayer(), false));

	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		if ((boolean) plugin.checkSetting("settings.disable-player-xp-drop", e.getEntity(), false))
			e.setDroppedExp(0);

		if ((boolean) plugin.checkSetting("settings.disable-player-drops-on-death", e.getEntity(), false))
			e.getDrops().clear();

		if ((boolean) plugin.checkSetting("settings.hide-death-messages", e.getEntity(), false))
			e.setDeathMessage(null);

	}

	@EventHandler
	public void mobDeath(EntityDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			if ((boolean) plugin.checkSetting("settings.disable-mob-xp", e.getEntity().getWorld().getName(), false)) {
				
				e.setDroppedExp(0);
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {

		if ((boolean) plugin.checkSetting("settings.disable-block-xp", e.getPlayer(), false))
			e.setExpToDrop(0);

		if ((boolean) plugin.checkSetting("settings.disable-block-break", e.getPlayer(), false))
			e.setCancelled(true);

	}

	@EventHandler
	public void invInteract(InventoryClickEvent e) {
			e.setCancelled((boolean) plugin.checkSetting("settings.disable-inventory-interaction", (Player) e.getWhoClicked(), false));
	}
	
	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player && (boolean) plugin.checkSetting("settings.disable-hunger", (Player) e.getEntity(), false))
			e.setCancelled(true);
	}

}
