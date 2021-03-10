package ru.bruhabruh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.bruhabruh.managers.PeeManager;

public class PeeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Вы должны быть игроком!"); return true; }
        PeeManager.tryToPee(((Player) sender).getPlayer());

        return true;
    }
}