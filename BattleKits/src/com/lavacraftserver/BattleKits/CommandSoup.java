package com.lavacraftserver.BattleKits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class CommandSoup implements CommandExecutor {

	public BattleKits plugin;

	public CommandSoup(BattleKits p) {
		this.plugin = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		//Assigned to np98765, so I've only made the command class
		if (cmd.getName().equals("soup") || cmd.getName().equals("refill") || cmd.getName().equals("stew")) {
			if (sender.hasPermission("BattleKits.soup")) {
				Player p = (Player) sender;
				ItemStack i = p.getItemInHand();
				if (i.getType() != Material.BOWL) {
					sender.sendMessage(ChatColor.RED + "You must have an empty bowl in your hand");
					return true;
				} else {
					i.setType(Material.MUSHROOM_SOUP);
			}
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission for this command.");

			}

		}
		return false;
	}

}