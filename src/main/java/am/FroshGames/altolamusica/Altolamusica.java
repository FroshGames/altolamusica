package am.FroshGames.altolamusica;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Altolamusica extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("AltoLaMusica has been enable");
            saveDefaultConfig();

            getCommand("altolamusica").setExecutor(new GameCommand(this));
    };

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("AltoLaMusica pluguin has been disable");
    }
}

// Pluguin echo por Froshy alias Amir chiquito para Mialu