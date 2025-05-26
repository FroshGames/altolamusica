package am.FroshGames.altolamusica;

import am.FroshGames.altolamusica.listener.GameListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand implements CommandExecutor {
    private final Altolamusica pluguin;
    private boolean gameInProgress = false;

    public GameCommand(Altolamusica pluguin) {
        this.pluguin = pluguin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("altolamusica")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("altolamusica.start")) {
                    if (!gameInProgress) {
                        startGame();
                        player.sendMessage(ChatColor.GREEN + "¡El juego Alto la Música ha comenzado!");
                    } else {
                        player.sendMessage(ChatColor.RED + "El juego ya está en curso.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "No tienes permiso para iniciar el minijuego.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            }
            return true;
        }
        return false;
    }
// Pluguin echo por Froshy
    private void startGame() {
        gameInProgress = true;
        Bukkit.broadcastMessage(ChatColor.GOLD + "¡El minijuego Alto la Música ha comenzado!");
        pluguin.getServer().getPluginManager().registerEvents(new GameListener(pluguin), pluguin);
    }

    public void endGame() {
        gameInProgress = false;
        Bukkit.broadcastMessage(ChatColor.GOLD + "¡El minijuego Alto la Música ha terminado!");
    }
}
