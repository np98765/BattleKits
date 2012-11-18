package com.lavacraftserver.BattleKits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

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
	/**
	 * TagAPI event which adds support for coloured tags
	 * @param event - PlayerReceiveNameTagEvent
	 */
    @EventHandler
    public void onNameTag(PlayerReceiveNameTagEvent event) {
	    if (plugin.tags.containsKey(event.getNamedPlayer().getName())) { //See if a tag is defined
	    	event.setTag(plugin.tags.get(event.getNamedPlayer().getName()) + event.getNamedPlayer().getName()); //Set new tag
	    }
    }

}
