package ru.bruhabruh.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.bruhabruh.managers.PoopManager;

import java.util.Objects;

public class PoopListener implements Listener {

    @EventHandler
    public void onPlayerOvereats(FoodLevelChangeEvent event) {
        if (event.getItem() == null) { return; } // Если игрок СЪЕЛ еду, а не проголодался
        double chance = 0.2;
        Player player = (Player) event.getEntity();
        chance += 0.1 * (event.getFoodLevel() - 20); // +10% если ножек больше на 1 чем видно игроку
        if (event.getFoodLevel() > 20 && Math.random() < chance) { // Если ножек АБСТРАКТНО больше 10 и 20% что захочешь какать. Переедание
            PoopManager.addPlayerToUrgeToPoop(player);
        }
    }

    @EventHandler
    public void onPlayerUsePaper(InventoryClickEvent event) {
        if (!Objects.requireNonNull(event.getClickedInventory()).getType().equals(InventoryType.PLAYER)) { return; }
        if (!event.getClick().isRightClick()) { return; } // В креативе не работает!
        if (!Objects.requireNonNull(event.getCurrentItem()).getType().equals(Material.PAPER)) { return; }
        Player player = (Player) event.getWhoClicked();
        if (!PoopManager.hasPlayerInMap(player)) { return; }
        if (event.getSlot() == 40) {// вторая рука
            if (Objects.requireNonNull(player.getEquipment()).getItemInOffHand().equals(event.getCurrentItem())) {
                PoopManager.tryToPoop(player, event.getCurrentItem());
                event.setCancelled(true);
                player.closeInventory();
            }
        } else {
            if (Objects.requireNonNull(player.getEquipment()).getItemInMainHand().equals(event.getCurrentItem())) {
                PoopManager.tryToPoop(player, event.getCurrentItem());
                event.setCancelled(true);
                player.closeInventory();
            }
        }
    }
}
