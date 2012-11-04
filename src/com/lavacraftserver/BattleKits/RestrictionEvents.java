package com.lavacraftserver.BattleKits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class RestrictionEvents implements Listener
{
private BattleKits plugin;
	
	public RestrictionEvents(BattleKits plugin) 
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void itemDrop(PlayerDropItemEvent e)
	{
		if (plugin.getConfig().getBoolean("settings.disable-dropping-items"))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void itemDrop(CraftItemEvent e)
	{
		if (plugin.getConfig().getBoolean("settings.disable-crafting"))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void pickup(PlayerPickupItemEvent e)
	{
		if (plugin.getConfig().getBoolean("settings.disable-pickup-items"))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void pickup(BlockPlaceEvent e)
	{
		if (plugin.getConfig().getBoolean("settings.disable-block-place"))
			e.setCancelled(true);
	}

	
	@EventHandler
	public void death(PlayerDeathEvent e)
	{
		if (plugin.getConfig().getBoolean("settings.disable-player-xp-drop"))
			e.setDroppedExp(0);
		
		if (plugin.getConfig().getBoolean("settings.disable-player-drops-on-death"))
			e.getDrops().clear();
		
		if (plugin.getConfig().getBoolean("settings.hide-death-messages"))
			e.setDeathMessage(null);

	}
	
	@EventHandler
	public void mobDeath (EntityDeathEvent e)
	{
		if (!(e.getEntity() instanceof Player))
		{
			if (plugin.getConfig().getBoolean("settings.disable-mob-xp-drop"))
				e.setDroppedExp(0);
		}
	}
	
	@EventHandler
	public void blockBreak (BlockBreakEvent e)
	{

			if (plugin.getConfig().getBoolean("settings.disable-block-xp"))
				e.setExpToDrop(0);
			
			if (plugin.getConfig().getBoolean("settings.disable-block-break"))
				e.setCancelled(true);
		
	}
	
	@EventHandler
	public void invInteract(InventoryClickEvent e)
	{
		if (plugin.getConfig().getBoolean("settings.disable-inventory-interaction"))
			e.setCancelled(true);
	}
	
	

}
