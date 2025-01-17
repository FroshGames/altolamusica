package am.FroshGames.altolamusica.listener;

import am.FroshGames.altolamusica.Altolamusica;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameListener implements Listener, CommandExecutor {

    private final Altolamusica pluguin;
    private boolean musicPlaying = true;
    private final Set<Player> movingPlayers = new HashSet<>();
    private final Set<Player> activePlayers = new HashSet<>();
    private final Set<Location> chairs = new HashSet<>();
    private final int round = 1;
    private final int chairsToRemovePerRound;
    private final int totalRounds;
    private final String musicStart;
    private final String musicStop;

    public GameListener(Altolamusica plugin) {
        this.pluguin = plugin;
        this.chairsToRemovePerRound = plugin.getConfig().getInt("chairsToRemovePerRound", 1);
        this.totalRounds = plugin.getConfig().getInt("totalRounds", 5);
        this.musicStart = plugin.getConfig().getString("musicStart", "minecraft:music_disc.cat");
        this.musicStop = plugin.getConfig().getString("musicStop", "minecraft:music_disc.11");
        setupChairs();
        startMusicCycle();
    }

    private void setupChairs() {
        List<?> chairlist = pluguin.getConfig().getList("chairs");
        if (chairlist != null) {
            for (Object chairObj : chairlist) {
                if (chairObj instanceof ConfigurationSection) {
                    ConfigurationSection chairSection = (ConfigurationSection) chairObj;
                    String world = chairSection.getString("world");
                    double x = chairSection.getDouble("x");
                    double y = chairSection.getDouble("y");
                    double z = chairSection.getDouble("z");
                    chairs.add(new Location(Bukkit.getWorld(world), x, y, z));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!musicPlaying && !movingPlayers.contains(player)) {
            Bukkit.getScheduler().runTask(pluguin, () -> {
                player.sendMessage(ChatColor.RED + "¡Te has movido mientras la música estaba detenida!");
                //player.setHealth(0); // Elimina al jugador
            });
            //bug ALM:00001 pluguin asesino de espectadores
        }       //[(Intento de  solución de bug)] fecha 11/01/2025
    }

    private void startMusicCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                musicPlaying = !musicPlaying;
                if (musicPlaying) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "¡La música ha comenzado! Puedes moverte.");
                    movingPlayers.clear();
                    //playMusic(musicStart);
                } else {
                    removeChairs();
                }
            }
        }.runTaskLater(pluguin, 100L);
    }

    private boolean isPlayerOnChair(Player player) {
        for (Location chair : chairs) {
            Location playerLocation = player.getLocation();
            if (playerLocation.getWorld().equals(chair.getWorld()) &&
                    Math.abs(playerLocation.getX() - chair.getX()) < 0.5 &&
                    Math.abs(playerLocation.getY() - (chair.getY() + 0.5)) < 0.5 &&
                    Math.abs(playerLocation.getZ() - chair.getZ()) < 0.5) {
                return true;
            }
        }
        return false;
    }

    private void removeChairs() {
        Random random = new Random();
        int chairsToRemove = Math.min(chairsToRemovePerRound, chairs.size());
        for (int i = 0; i < chairsToRemove; i++) {
            int index = random.nextInt(chairs.size());
            Location chairToRemove = (Location) chairs.toArray()[index];
            chairs.remove(chairToRemove);
        }
    }

    private void playMusic(String music) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.valueOf(music), 1.0f, 1.0f);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("stopgame")) {
            stopGame();
            return true;
        }
        return false;
    }

    private void stopGame() {
        Bukkit.broadcastMessage(ChatColor.RED + "¡El juego ha sido detenido!");
        for (Player player : activePlayers) {
            player.setHealth(0); // Elimina al jugador
        }
        activePlayers.clear();
    }
}