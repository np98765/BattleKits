package com.lavacraftserver.BattleKits;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandSoup implements CommandExecutor {
	
	public BattleKits plugin;
	
	public CommandSoup(BattleKits p) {
		this.plugin = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		//Assigned to np98765, so I've only made the command class
		if (cmd.getName().equals("soup")) {
			// TODO Write soup command
			
		}
		return false;
	}

}
