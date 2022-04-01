package fun.kaituo;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.io.IOException;
import java.util.*;

import static fun.kaituo.GameUtils.world;

public abstract class Game {
    protected List<Player> players = new ArrayList<>();
    protected List<Integer> taskIds = new ArrayList<>();
    protected Random random = new Random();
    protected JavaPlugin plugin;

    protected String fullName;
    protected String name;
    protected Location hubLocation;
    protected BoundingBox gameBoundingBox;
    protected UUID gameUUID;
    protected Runnable gameRunnable;

    protected Location startButtonLocation;
    protected BlockFace startButtonDirection;
    protected Location spectateButtonLocation;
    protected BlockFace spectateButtonDirection;

    protected Color[] fireworkColors = {Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GREEN, Color.LIME, Color.MAROON,
            Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW};


    //Initialize game
    protected void initializeGame(JavaPlugin plugin, String name, String fullName, Location hubLocation, BoundingBox gameBoundingBox) {
        this.plugin = plugin;
        this.name = name;
        this.fullName = fullName;
        this.hubLocation = hubLocation;
        this.gameBoundingBox = gameBoundingBox;
        this.gameUUID = UUID.randomUUID();
        initializeGameRunnable();
    }


    protected void initializeButtons(Location startButtonLocation, BlockFace startButtonDirection, Location spectateButtonLocation, BlockFace spectateButtonDirection) {
        this.startButtonLocation = startButtonLocation;
        this.startButtonDirection = startButtonDirection;
        this.spectateButtonLocation = spectateButtonLocation;
        this.spectateButtonDirection = spectateButtonDirection;
    }


    //For game utilities
    public void startGame() {
        Bukkit.getScheduler().runTask(plugin, gameRunnable);
    }

    protected void cancelGameTasks() {
        List<Integer> taskIdsCopy = new ArrayList<>(taskIds);
        taskIds.clear();
        for (int i : taskIdsCopy) {
            Bukkit.getScheduler().cancelTask(i);
        }
    }

    protected long getTime(World world) {
        return (world.getGameTime());
    }

    protected void spawnFireworks(Player p) {
        for (int i = 0; i < 5; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> spawnFirework(p), 8 * i + 1);
        }
    }

    protected void spawnFirework(Player p) {
        Location loc = p.getLocation();
        loc.setY(loc.getY() + 0.9);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        fw.addScoreboardTag("gameFirework");
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(fireworkColors[random.nextInt(fireworkColors.length)]).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
    }

    //For game logic
    protected Collection<Player> getPlayersNearHub(double xRadius, double yRadius, double zRadius) {
        Collection<Player> result = new ArrayList<>();
        for (Entity e : world.getNearbyEntities(hubLocation, xRadius, yRadius, zRadius, (e) -> e instanceof Player)) {
            result.add((Player) e);
        }
        return result;
    }


    protected void placeStartButton() {
        Block block = world.getBlockAt(startButtonLocation);
        block.setType(Material.OAK_BUTTON);
        BlockData data = block.getBlockData().clone();
        ((Directional) data).setFacing(startButtonDirection);
        block.setBlockData(data);
    }

    protected void removeStartButton() {
        world.getBlockAt(startButtonLocation).setType(Material.AIR);
    }

    protected void placeSpectateButton() {
        Block block = world.getBlockAt(spectateButtonLocation);
        block.setType(Material.OAK_BUTTON);
        BlockData data = block.getBlockData().clone();
        ((Directional) data).setFacing(spectateButtonDirection);
        block.setBlockData(data);
    }

    protected void removeSpectateButton() {
        world.getBlockAt(spectateButtonLocation).setType(Material.AIR);
    }

    protected void startCountdown(int countDownSeconds) {
        if (countDownSeconds > 5) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (Player p : players) {
                    p.sendTitle("§a游戏还有 " + countDownSeconds + " 秒开始", null, 2, 16, 2);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                    p.getInventory().clear();
                }
            });
        }
        for (int i = 5; i > 0; i--) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player p : players) {
                    p.sendTitle("§a游戏还有 " + finalI + " 秒开始", null, 2, 16, 2);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                    p.getInventory().clear();
                }
            }, 20L * countDownSeconds - 20L * i);
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : players) {
                p.sendTitle("§e游戏开始！", null, 2, 16, 2);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 2f);
            }
        }, 20L * countDownSeconds);
    }

    //For outside usage
    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    //For per-game override usage
    protected abstract void initializeGameRunnable();

    protected abstract void savePlayerQuitData(Player p) throws IOException;

    protected abstract void rejoin(Player p);
}