package ru.bruhabruh.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.bruhabruh.Main;
import ru.bruhabruh.managers.PoopManager;

import java.util.List;

public class PoopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Вы должны быть игроком!"); return true; }
        if (!PoopManager.hasPlayerInMap((Player) sender)) { sender.sendMessage("Вы не хотите какать!"); return true; }
        // TODO Возможно, проверить сидит ли человек
        Player player = (Player) sender;
        if (player.getEquipment() == null) { player.sendMessage("У вас нет бумажки"); return true; }
        if (player.getEquipment().getItemInMainHand().getType().equals(Material.PAPER) ||
            player.getEquipment().getItemInOffHand().getType().equals(Material.PAPER)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 10));
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
                @Override
                public void run() {
                    PoopManager.removePlayerFromUrgeToPoop(player);
                    List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            player.sendMessage(ChatColor.GOLD
                                    + String.format("%s вытер попу бумажкой", player.getName())); // TODO Можно поменять на /try
                        }
                    }
                    if (Math.random() < 0.1) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
                        for (Entity entity : entities) {
                            if (entity instanceof Player) {
                                player.sendMessage(ChatColor.GOLD
                                        + String.format("Бумажка у %s порвалась!", player.getName()));
                            }
                        }
                    }
                    ItemStack item;
                    if (player.getEquipment().getItemInMainHand().getType().equals(Material.PAPER)) {
                        item = player.getEquipment().getItemInMainHand();
                    } else {
                        item = player.getEquipment().getItemInOffHand();
                    }
                    item.setAmount(item.getAmount()-1);
                }
            }, 3*20);
        } else {
            player.sendMessage("У вас нет бумажки");
        }
        return true;
    }
}
