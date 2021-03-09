package ru.bruhabruh;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.bruhabruh.commands.PeeCommand;
import ru.bruhabruh.commands.PoopCommand;
import ru.bruhabruh.listeners.PeeListener;
import ru.bruhabruh.listeners.PoopListener;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {


    @Override
    public void onEnable() {
        try {
            Bukkit.getPluginManager().registerEvents(new PoopListener(), this); // Регистрация "слушателя" какашек
            Bukkit.getPluginManager().registerEvents(new PeeListener(), this); // Регистрация "слушателя" пиписек
            this.getCommand("poop").setExecutor(new PoopCommand());
            this.getCommand("pee").setExecutor(new PeeCommand());
            Logger logger = Bukkit.getLogger();
            logger.info("Plugin is enabled!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        Logger logger = Bukkit.getLogger();
        logger.info("Plugin is disabled!");
    }

}
