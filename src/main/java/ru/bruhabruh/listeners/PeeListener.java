package ru.bruhabruh.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import ru.bruhabruh.managers.PeeManager;

public class PeeListener implements Listener {

    @EventHandler
    public void onPlayerOvereats(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (!event.getItem().getType().equals(Material.POTION)) { return; }
        PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
        if (!meta.getBasePotionData().getType().equals(PotionType.WATER)) { return; }
        // TODO Проверка на уровень воды
        PeeManager.addPlayerToUrgeToPee(player);
    }
}
