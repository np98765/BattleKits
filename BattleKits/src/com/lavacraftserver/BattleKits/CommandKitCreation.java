package com.lavacraftserver.BattleKits;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandKitCreation implements CommandExecutor {
	
	public BattleKits plugin;
	public CommandKitCreation(BattleKits battleKits) {
		plugin = battleKits;
	}
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		arg0.sendMessage("This command is not yet implemented.");
		return true;
	}

}
