package ru.bruhabruh.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import ru.bruhabruh.managers.PoopManager;

public class PoopListener implements Listener {

    @EventHandler
    public void onPlayerOvereats(FoodLevelChangeEvent event) {
        if (event.getItem() == null) { return; } // Если игрок СЪЕЛ еду, а не проголодался
        Player player = (Player) event.getEntity();
        if (event.getFoodLevel() > 20 && Math.random() < 0.2) { // Если ножек АБСТРАКТНО больше 10 и 20% что захочешь какать. Переедание
            player.sendMessage("Переедание!");
            PoopManager.addPlayerToUrgeToPoop(player);
        } else {
            player.sendMessage("NO");
        }
    }
}
