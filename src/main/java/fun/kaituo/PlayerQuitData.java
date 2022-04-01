package fun.kaituo;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerQuitData {
    private final HashMap<String, Object> data = new HashMap<>();
    private final Set<PotionEffect> potionEffects = new HashSet<>();
    private UUID gameUUID;
    private Game game;
    private Location loc;
    private GameMode gameMode;
    private double health;
    private double maxHealth;
    private Team team;

    public PlayerQuitData(Player p, Game game, UUID gameUUID) {
        p.saveData();
        try {
            Files.copy(new File("world/playerdata/" + p.getUniqueId() + ".dat").toPath(), new File("plugins/GameUtils/" + p.getUniqueId() + ".dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.game = game;
        this.gameUUID = gameUUID;
        loc = p.getLocation();
        gameMode = p.getGameMode();
        potionEffects.addAll(p.getActivePotionEffects());
        health = p.getHealth();
        maxHealth = p.getMaxHealth();
        for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if (t.hasPlayer(p)) {
                team = t;
            }
        }
    }

    public void restoreBasicData(Player p) {
        try {
            Files.copy(new File("plugins/GameUtils/" + p.getUniqueId() + ".dat").toPath(), new File("world/playerdata/" + p.getUniqueId() + ".dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.loadData();
        p.teleport(loc);
        p.setGameMode(gameMode);
        p.addPotionEffects(potionEffects);
        p.setHealth(health);
        p.setMaxHealth(maxHealth);
        if (team != null) {
            team.addPlayer(p);
        }
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public UUID getGameUUID() {
        return gameUUID;
    }

    public void setGameUUID(UUID gameUUID) {
        this.gameUUID = gameUUID;
    }

    public Set<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}
