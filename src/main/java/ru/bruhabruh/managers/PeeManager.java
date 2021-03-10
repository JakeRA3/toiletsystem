package ru.bruhabruh.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.bruhabruh.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeeManager  {
    private static Map<Player, Integer> UrgeToPeePlayers = new HashMap<>(); // Желающие пописать от 1 до 5

    public static void setEffectByValue(Player player, int value) {
        switch (value) {
            case 1: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 1));
            }
            case 3: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 3*20, 1));
            }
            case 5: {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5*20, 2));
            }
        }
    }

    public static void tryToPee(Player player) {
        player.sendMessage(UrgeToPeePlayers.toString());
        if (!hasPlayerInMap(player)) { player.sendMessage("Вы не хотите писать!"); return; }
        removePlayerFromUrgeToPee(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 10));
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
            @Override
            public void run() {
                List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                entities.add(player); // Чтобы игрок тоже видел сообщение
                if (Math.random() < 0.1) {
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            entity.sendMessage(ChatColor.GOLD
                                    + String.format("%s, пока писал и попал себе на ногу", player.getName()));
                        }
                    }
                } else {
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            entity.sendMessage(ChatColor.GOLD + String.format("%s пописал!", player.getName()));
                        }
                    }
                }
            }
        }, 3*20);
    }

    public static void removePlayerFromUrgeToPee(Player player) {
        UrgeToPeePlayers.remove(player);
    }

    public static boolean hasPlayerInMap(Player player) {
        return UrgeToPeePlayers.containsKey(player);
    }

    public static void addPlayerToUrgeToPee(Player player) { // Добавить/Увеличить желание покакать
        if (UrgeToPeePlayers.containsKey(player)) { // При наличии игрока добавляет ему ещё 1
            UrgeToPeePlayers.put(player, UrgeToPeePlayers.get(player)+1);
        } else {
            UrgeToPeePlayers.put(player, 1);
        }
        startAddUrgeToPoopPercentToPlayer(player);
        player.sendMessage(UrgeToPeePlayers.toString());
    }

    private static void startAddUrgeToPoopPercentToPlayer(Player player) {  // РАЗ В минуту увеличивает на 1
        int minutes = 1;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!UrgeToPeePlayers.containsKey(player)) { this.cancel(); return; }
                int newValue = UrgeToPeePlayers.get(player)+1;
                if (newValue > 5) { newValue = 5; }
                UrgeToPeePlayers.put(player, newValue);
                double chance = 0.1;
                if (newValue == 5) { chance = 0.3; }
                if (Math.random() < chance) { //
                    List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            entity.sendMessage(ChatColor.GOLD + String.format("%s обоссался", player.getName())); // TODO заменить
                        }
                    }
                    player.sendMessage(ChatColor.GOLD + String.format("%s обоссался", player.getName())); // TODO сменить
                    removePlayerFromUrgeToPee(player);
                    return;
                }
                if (Math.random() < 0.1) {
                    setEffectByValue(player, newValue);
                }
                if (newValue % 2 == 1) { // Чтобы меньше писало и не надоедало !!! ПРИ МАЛОМ ИНТЕРВАЛЕ
                    player.sendMessage(ChatColor.GOLD + "Вы хотите пописать"); // TODO сменить
                }
                player.sendMessage("DEBUG: " + newValue);
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 20*60*minutes, 20*60*minutes);
    }
}
