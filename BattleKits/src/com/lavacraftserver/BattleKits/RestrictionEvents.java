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
	
	/**
	 * Multi-world config accessor
	 * @param String path - The setting path to look for (e.g. settings.disable-xp)
	 * @param Player p - Player to get world from
	 * @param Object defaultValue - If empty, use this
	 * @return Object - result
	 */
	public Object checkSetting(String path, Player p, Object defaultValue) {
		if (plugin.global.getConfig().contains(p.getWorld().getName() + "." + path)) {
			//We have an override
			return plugin.getConfig().get(p.getWorld().getName() + "." + path);
		} else {
			if (plugin.global.getConfig().contains(path)) {
				return plugin.global.getConfig().get(path);
			} else {
				return defaultValue;
			}
		}
		
	}
	
	/**
	 * Multi-world config accessor -- accepts world name instead of Player
	 * @param String path - The setting path to look for (e.g. settings.disable-xp)
	 * @param String world - World to check
	 * @param Object defaultValue - If empty, use this
	 * @return Object - result
	 */
	public Object checkSetting(String path, String world, Object defaultValue) {
		if (plugin.global.getConfig().contains(world + "." + path)) {
			//We have an override
			return plugin.getConfig().get(world + "." + path);
		} else {
			if (plugin.global.getConfig().contains(path)) {
				return plugin.global.getConfig().get(path);
			} else {
				return defaultValue;
			}
		}
		
	}

	@EventHandler
	public void itemDrop(PlayerDropItemEvent e) {
			e.setCancelled((boolean) checkSetting("settings.disable-dropping-items", e.getPlayer(), false));
	}

	@EventHandler
	public void itemDrop(CraftItemEvent e) {
		e.setCancelled((boolean) checkSetting("settings.disable-crafting", (Player) e.getWhoClicked(), false));

	}

	@EventHandler
	public void pickup(PlayerPickupItemEvent e) {
		e.setCancelled((boolean) checkSetting("settings.disable-pickup-items",  e.getPlayer(), false));

	}

	@EventHandler
	public void bpe(BlockPlaceEvent e) {
		e.setCancelled((boolean) checkSetting("settings.disable-block-place", e.getPlayer(), false));

	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		if ((boolean) checkSetting("settings.disable-player-xp-drop", e.getEntity(), false))
			e.setDroppedExp(0);

		if ((boolean) checkSetting("settings.disable-player-drops-on-death", e.getEntity(), false))
			e.getDrops().clear();

		if ((boolean) checkSetting("settings.hide-death-messages", e.getEntity(), false))
			e.setDeathMessage(null);

	}

	@EventHandler
	public void mobDeath(EntityDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			if ((boolean) checkSetting("settings.disable-mob-xp", e.getEntity().getWorld().getName(), false)) {
				
				e.setDroppedExp(0);
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {

		if ((boolean) checkSetting("settings.disable-block-xp", e.getPlayer(), false))
			e.setExpToDrop(0);

		if ((boolean) checkSetting("settings.disable-block-break", e.getPlayer(), false))
			e.setCancelled(true);

	}

	@EventHandler
	public void invInteract(InventoryClickEvent e) {
		if ((boolean) checkSetting("settings.disable-inventory-interaction", (Player) e.getWhoClicked(), false))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player && (boolean) checkSetting("settings.disable-hunger", (Player) e.getEntity(), false))
			e.setCancelled(true);
	}

}
