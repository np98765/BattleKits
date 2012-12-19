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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class RestrictionEvents implements Listener {
	@SuppressWarnings("unused")
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
			e.setCancelled(!e.getPlayer().hasPermission("BattleKits.bypassRestriction.disable-dropping-items") && !e.isCancelled());
	}

	@EventHandler
	public void craftItemEvent(CraftItemEvent e) {
		
		if (!e.getWhoClicked().hasPermission("BattleKits.bypassRestriction.disable-crafting") && !e.isCancelled()) {
		e.setCancelled(true);
		}

	}

	@EventHandler
	public void pickup(PlayerPickupItemEvent e) {
		
		if(!e.getPlayer().hasPermission("BattleKits.bypassRestriction.disable-pickup-items") && !e.isCancelled()) {
		e.setCancelled(true);
		}

	}

	@EventHandler
	public void bpe(BlockPlaceEvent e) {
		if (!e.getPlayer().hasPermission("BattleKits.bypassRestriction.disable-block-place") && !e.isCancelled()) {
			e.setCancelled(true);
		}

	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		if (!e.getEntity().hasPermission("BattleKits.bypassRestriction.disable-player-xp-drop"))
			e.setDroppedExp(0);

		if (!e.getEntity().hasPermission("BattleKits.bypassRestriction.disable-player-drops-on-death"))
			e.getDrops().clear();

		if (!e.getEntity().hasPermission("BattleKits.bypassRestriction.hide-death-messages"))
			e.setDeathMessage(null);

	}

	@EventHandler
	public void mobDeath(EntityDeathEvent e) {
		if (e.getEntity().getKiller() != null) {
		if (!(e.getEntity() instanceof Player) && e.getEntity().getKiller() instanceof Player) {
			Player p = e.getEntity().getKiller();
			if (!p.hasPermission("BattleKits.bypassRestriction.disable-mob-xp")) {
				
				e.setDroppedExp(0);
			}
		}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {

		if (!e.getPlayer().hasPermission("BattleKits.bypassRestriction.disable-block-xp") && !e.isCancelled())
			e.setExpToDrop(0);

		if (!e.getPlayer().hasPermission("BattleKits.bypassRestriction.disable-block-break") && !e.isCancelled())
			e.setCancelled(true);

	}

	@EventHandler
	public void invInteract(InventoryClickEvent e) {
		if (!e.getWhoClicked().hasPermission("BattleKits.bypassRestriction.disable-inventory-click") && !e.isCancelled()) {

			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
		if (!e.getEntity().hasPermission("BattleKits.bypassRestriction.disable-food-change") && !e.isCancelled()) {

			e.setCancelled(true);
		}
	}
	
	/**
	 * DEBUG EVENT
	 */
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		plugin.getLogger().info(Boolean.toString(p.hasPermission("Battlekits.auto." + "archer")));
	}

}
