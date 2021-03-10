package ru.bruhabruh.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import ru.bruhabruh.Main;
import ru.bruhabruh.managers.PeeManager;

import java.util.HashMap;
import java.util.Map;

public class PeeListener implements Listener {
    private static Map<Player, Integer> uses_bottle_of_water = new HashMap<>();

    @EventHandler
    public void onPlayerOverdrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (!event.getItem().getType().equals(Material.POTION)) { return; }
        PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
        if (!meta.getBasePotionData().getType().equals(PotionType.WATER)) { return; }
        // TODO Проверка на уровень воды
        // Ниже проверка на 2+ бутылки за 5 минут
        if (uses_bottle_of_water.containsKey(player)) { // Если больше 2 бутылок за 5 минут то чел ссыт в штаны
            if (uses_bottle_of_water.get(player) == 2) {
                PeeManager.addPlayerToUrgeToPee(player);
                uses_bottle_of_water.remove(player);
            } else {
                uses_bottle_of_water.put(player, uses_bottle_of_water.get(player)+1);
            }
        } else {
            uses_bottle_of_water.put(player, 1);
        }
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
            @Override
            public void run() {
                decBottle(player);
            }
        }, 20*60*5);
    }

    private static void decBottle(Player player) {
        if (!uses_bottle_of_water.containsKey(player)) { return; }
        uses_bottle_of_water.put(player, uses_bottle_of_water.put(player, uses_bottle_of_water.get(player)-1));
        if (uses_bottle_of_water.get(player) == 0) {
            uses_bottle_of_water.remove(player);
        }
    }
}
