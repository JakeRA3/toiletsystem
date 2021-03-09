package ru.bruhabruh.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.bruhabruh.Main;
import ru.bruhabruh.managers.PoopManager;

import java.util.List;

public class PeeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Вы должны быть игроком!"); return true; }
        if (!PoopManager.hasPlayerInMap((Player) sender)) { sender.sendMessage("Вы не хотите писать!"); return true; }
        Player player = (Player) sender;
        PoopManager.removePlayerFromUrgeToPoop(player);
        player.sendMessage(ChatColor.GOLD + "Пописал"); // TODO Можно поменять на /try
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 10));
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
            @Override
            public void run() {
                if (Math.random() < 0.1) {
                    List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            player.sendMessage(ChatColor.GOLD
                                    + String.format("%s, пока писал и попал себе на ногу", player.getName()));
                        }
                    }
                } else {
                    List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            player.sendMessage(ChatColor.GOLD + String.format("%s пописал!", player.getName()));
                        }
                    }
                    player.sendMessage(ChatColor.GOLD + "");
                }
            }
        }, 3*20);

        return true;
    }
}