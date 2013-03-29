package com.lol768.BattleKits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TagHandler implements Listener {
	private BattleKits plugin;

	/**
	 * Constructor method used when creating instance of this class
	 * Used so we have access to the main plugin & config etc
	 * @param plugin - Instance of Battlekits.getConfig().java
	 */
	public TagHandler(BattleKits plugin) {
		this.plugin = plugin;

	}
	

}
