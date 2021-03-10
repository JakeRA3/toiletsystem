package ru.bruhabruh.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.bruhabruh.Main;

import java.util.*;

public class PoopManager {
    private static final Map<Player, Integer> UrgeToPoopPlayers = new HashMap<>(); // Желаюние покакать от 1 до 10

    public static String GetPhraseByValue(Player player, int value) { // TODO Вместо player.getName() надо Имя Фамилия или Незнакомец
        switch (value) {
            case 2: {
                return String.format("У %s урчит живот", player.getName());
            }
            case 4: {
                return String.format("У %s громко урчит живот", player.getName());
            }
            case 6: {
                return String.format("%s тихо пукнул", player.getName());
            }
            case 8: {
                return String.format("%s громко пернул", player.getName());
            }
            default: {
                return null;
            }
        }
    }

    public static void tryToPoop(Player player, ItemStack item) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 10));
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
            @Override
            public void run() {
                removePlayerFromUrgeToPoop(player);
                List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                entities.add(player); // Чтобы игроку тоже выводило
                for (Entity entity : entities) {
                    if (entity instanceof Player) {
                        entity.sendMessage(ChatColor.GOLD
                                + String.format("%s вытер попу бумажкой", player.getName())); // TODO Можно поменять на /try
                    }
                }
                if (Math.random() < 0.1) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            entity.sendMessage(ChatColor.GOLD
                                    + String.format("Во время вытерания попы бумажка у %s порвалась!", player.getName()));
                        }
                    }
                }
                item.setAmount(item.getAmount()-1);
            }
        }, 3*20);
    }

    public static void setEffectByValue(Player player, int value) {
        if (value > 6) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10*20, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10*20, 2));
        } else if (value > 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10*20, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5*20, 1));
        } else if (value > 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
        }
    }

    public static void removePlayerFromUrgeToPoop(Player player) {
        UrgeToPoopPlayers.remove(player);
    }

    public static boolean hasPlayerInMap(Player player) {
        return UrgeToPoopPlayers.containsKey(player);
    }

    public static void addPlayerToUrgeToPoop(Player player) { // Добавить/Увеличить желание покакать
        if (UrgeToPoopPlayers.containsKey(player)) { // При наличии игрока добавляет ему ещё 1
            UrgeToPoopPlayers.put(player, UrgeToPoopPlayers.get(player)+1);
        } else {
            UrgeToPoopPlayers.put(player, 1);
        }
        startAddUrgeToPoopPercentToPlayer(player);
    }

    private static void startAddUrgeToPoopPercentToPlayer(Player player) {  // РАЗ В минуту увеличивает на 1
        int minutes = 1;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!UrgeToPoopPlayers.containsKey(player)) { this.cancel(); return; }
                int newValue = UrgeToPoopPlayers.get(player)+1;
                if (newValue > 10) { newValue = 10; }
                UrgeToPoopPlayers.put(player, newValue);
                if (newValue > 5) {
                    double chance = 0.1;
                    if (newValue == 10) { chance = 0.2; }
                    if (Math.random() < chance) { //
                        List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                        for (Entity entity : entities) {
                            if (entity instanceof Player) {
                                entity.sendMessage(ChatColor.GOLD + String.format("%s обделался", player.getName())); // TODO Возможно заменить
                            }
                        }
                        player.sendMessage(ChatColor.GOLD + String.format("%s обделался", player.getName())); // TODO Возможно заменить
                        /*player.spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0,0.5, 0), 20, Какашечки
                                0, 0,0, Material.BROWN_TERRACOTTA.createBlockData());*/
                        if (player.getFoodLevel() - 10 < 2) {
                            player.setFoodLevel(2);
                        } else { player.setFoodLevel(player.getFoodLevel() - 10); }
                        removePlayerFromUrgeToPoop(player);
                        return;
                    }
                }
                if (Math.random() < 0.1) {
                    setEffectByValue(player, newValue);
                }
                String message = GetPhraseByValue(player, newValue); // Фразы для предупреждения уровня обсера
                if (message != null) {
                    List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            entity.sendMessage(ChatColor.GOLD + message); // TODO Возможно заменить
                        }
                    }
                }
                player.sendMessage("DEBUG: " + newValue);
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 20*60*minutes, 20*60*minutes);
    }
}
