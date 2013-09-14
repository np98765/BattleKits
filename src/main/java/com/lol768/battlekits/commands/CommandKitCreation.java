package com.lol768.battlekits.commands;

import com.lol768.battlekits.BattleKits;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandKitCreation implements CommandExecutor {

    public BattleKits plugin;

    public CommandKitCreation(BattleKits battleKits) {
        plugin = battleKits;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("This command is not yet implemented.");
        return true;
    }
}
