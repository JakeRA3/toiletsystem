package ru.bruhabruh.managers;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
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
        if (!hasPlayerInMap(player)) { player.sendMessage("Вы не хотите писать!"); return; }
        removePlayerFromUrgeToPee(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 10));
        @NotNull BukkitTask taskid = new BukkitRunnable() {
            @Override
            public void run() {
                spawnParticlesBefore(player);
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 20);
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
            @Override
            public void run() {
                List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                entities.add(player); // Чтобы игрок тоже видел сообщение
                taskid.cancel();
                if (Math.random() < 0.1) {
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            entity.sendMessage(ChatColor.GOLD
                                    + String.format("%s, пока писал, попал себе на ногу", player.getName()));
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

    public static void addPlayerToUrgeToPee(Player player) { // Добавить/Увеличить желание пописать
        if (UrgeToPeePlayers.containsKey(player)) { // При наличии игрока добавляет ему ещё 1
            UrgeToPeePlayers.put(player, UrgeToPeePlayers.get(player)+1);
        } else {
            UrgeToPeePlayers.put(player, 1);
        }
        startAddUrgeToPoopPercentToPlayer(player);
    }

    public static void spawnParticlesBefore(Player player) { // Чисто поссать

        float yaw = player.getLocation().getYaw() + 90;
        double cos = Math.cos(yaw*Math.PI/180);
        double sin = Math.sin(yaw*Math.PI/180);
        double[] y = new double[] {0.65, 0.6, 0.2};
        for (int i=1; i<=3; i++) {
            double add = 4 / i;
            double c = cos / add;
            double s = sin / add;
            Location loc = player.getLocation().add(c, y[i-1], s);
            player.spawnParticle(Particle.DRIP_LAVA, loc, 1, 0,0,0,
                    0);
        }
    }

    public static void spawnParticlesOnOnePoint(Player player) { // Когда обоссался
        float yaw = player.getLocation().getYaw() + 90;
        double cos = Math.cos(yaw*Math.PI/180) / 4;
        double sin = Math.sin(yaw*Math.PI/180) / 4;

        Location loc = player.getLocation().add(cos, 0.65, sin);
        player.spawnParticle(Particle.BLOCK_CRACK, loc, 3,
                0, 0,0, Material.YELLOW_STAINED_GLASS.createBlockData());
    }

    private static void startAddUrgeToPoopPercentToPlayer(Player player) {  // РАЗ В минуту увеличивает на 1
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!UrgeToPeePlayers.containsKey(player)) { this.cancel(); return; }
                int newValue = UrgeToPeePlayers.get(player)+1;
                if (newValue > 5) { newValue = 5; }
                UrgeToPeePlayers.put(player, newValue);
                double chance = 0.2;
                if (newValue == 2) { chance = 0; }
                if (newValue == 5) { chance = 0.5; }
                if (Math.random() < chance) { //
                    List<Entity> entities = player.getNearbyEntities(10, 10, 10);
                    entities.add(player);
                    spawnParticlesOnOnePoint(player);
                    for (Entity entity : entities) {
                        if (entity instanceof Player) {
                            entity.sendMessage(ChatColor.GOLD + String.format("%s обоссался", player.getName())); // TODO заменить
                        }
                    }
                    removePlayerFromUrgeToPee(player);
                    return;
                }
                if (Math.random() < 0.4) {
                    setEffectByValue(player, newValue);
                }
                if (newValue % 2 == 1) { // Чтобы меньше писало и не надоедало !!! ПРИ МАЛОМ ИНТЕРВАЛЕ
                    if (newValue == 5 && Math.random() < 0.25) {
                        player.sendMessage(ChatColor.GOLD + "Вы хотите пописать");
                    }
                }
                //player.sendMessage("DEBUG: " + newValue);
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 20*60, 20*60);
    }
}
