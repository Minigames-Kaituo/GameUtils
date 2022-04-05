package fun.kaituo;

import fun.kaituo.event.PlayerChangeGameEvent;
import fun.kaituo.event.PlayerEndGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.List;

import static fun.kaituo.GameUtils.getGamePlayerIsIn;

public class GameUtilsListener implements Listener {
    GameUtils plugin;
    ItemStack menu;
    FileConfiguration c;

    public GameUtilsListener(GameUtils plugin) {
        this.plugin = plugin;
        this.menu = new ItemStack(Material.CLOCK, 1);
        ItemMeta itemMeta = menu.getItemMeta();
        itemMeta.setDisplayName("§e● §b§l菜单 §e●");
        itemMeta.setLore(List.of("§f请右键打开!"));
        menu.setItemMeta(itemMeta);
        c=plugin.getConfig();
    }
    //Chat is now handled by trchat
    /*
    @EventHandler
    public void AsyncPlayerChat(AsyncPlayerChatEvent apce) {
        String msg = apce.getMessage();
        Player from = apce.getPlayer();
        for (Player to : Bukkit.getServer().getOnlinePlayers()) {
            if (msg.equals(to.getName())) {
                if (from != to) {
                    to.playSound(to.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    from.playSound(from.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    apce.setMessage(msg.replaceAll(to.getName(), ChatColor.GREEN + "@" + to.getName()));
                }
            }
        }
        String gamePrefix = "";
        if (getGamePlayerIsIn(from) != null) {
            gamePrefix = "§7[" + getGamePlayerIsIn(from).getFullName() + "§7] §r";
        } else {
            gamePrefix = "§7[§3空闲§7] §r";
        }
        apce.setFormat(gamePrefix + "%1$s： %2$s");
    }

     */


    private void resetPlayer(Player p) {
        if (!p.getGameMode().equals(GameMode.CREATIVE)) {
            p.setGameMode(GameMode.ADVENTURE);
            p.resetMaxHealth();
            p.setHealth(20);
            p.setLevel(0);
            p.getInventory().clear();
            for (PotionEffect effect : p.getActivePotionEffects())
                p.removePotionEffect(effect.getType());
            p.getInventory().setItem(0, menu);
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                team.removePlayer(p);
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1, 0, false, false));
            p.setInvisible(true);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                p.setInvisible(false);
            }, 1);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent pje) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!pje.getPlayer().isOnline()) {
                return;
            }
            pje.getPlayer().sendMessage("§6欢迎， §e" + pje.getPlayer().getName() + "§6！");
        }, 20);
    }

    @EventHandler
    public void onPlayerEndGame(PlayerEndGameEvent pege) {
        resetPlayer(pege.getPlayer());
    }

    @EventHandler
    public void onPlayerChangeGame(PlayerChangeGameEvent pcge) {
        resetPlayer(pcge.getPlayer());
    }

    @EventHandler
    public void fixRespawn(PlayerJoinEvent pje) {
        Player p = pje.getPlayer();
        resetPlayer(p);
        p.setBedSpawnLocation(new Location(Bukkit.getWorld("world"), 0.5, 89, 0.5, 0, 0), true);
        p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 89, 0.5, 0, 0));
    }

    @EventHandler
    public void preventFireworkDamage(EntityDamageByEntityEvent edbee) {
        if (!c.getBoolean("no-firework-damage")) {
            return;
        }
        if (edbee.getDamager() instanceof Firework && edbee.getEntity() instanceof Player) {
            if (edbee.getDamager().getScoreboardTags().contains("gameFirework")) {
                edbee.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void preventDroppingMenu(PlayerDropItemEvent pdie) {
        if (!c.getBoolean("no-drop-menu")) {
            return;
        }
        if (pdie.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals("§e● §b§l菜单 §e●")) {
            pdie.setCancelled(true);
        }
    }

    @EventHandler
    public void preventDestroyingPainting(HangingBreakByEntityEvent hbbee) {
        if (!c.getBoolean("no-destroy-painting")) {
            return;
        }
        if (!(hbbee.getRemover() instanceof Player)) {
            return;
        }
        if (((Player) hbbee.getRemover()).getGameMode().equals(GameMode.ADVENTURE)) {
            hbbee.setCancelled(true);
        }
    }

    @EventHandler
    public void preventManipulatingArmorStand(PlayerArmorStandManipulateEvent pasme) {
        if (!c.getBoolean("no-armourstand-manipulation")) {
            return;
        }
        if (pasme.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            pasme.setCancelled(true);
        }
    }

    @EventHandler
    public void preventBlockInteraction(PlayerInteractEvent pie) {
        if (!c.getBoolean("no-block-interaction")) {
            return;
        }
        if (pie.getClickedBlock() == null) {
            return;
        }
        if (!pie.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }
        Block block = pie.getClickedBlock();
        if (block.getType().name().startsWith("POTTED_") || block.getType() == Material.FLOWER_POT) {
            pie.setCancelled(true);
            return;
        }
        if (block.getType().equals(Material.COMPOSTER)) {
            pie.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void setStandInvulnerable(EntitySpawnEvent ese) {
        if (!c.getBoolean("invulnerable-armourstand-on-spawn")) {
            return;
        }
        if (ese.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
            ese.getEntity().setInvulnerable(true);
        }
    }

    @EventHandler
    public void setPaintingInvulnerable(HangingPlaceEvent hpe) {
        if (!c.getBoolean("invulnerable-painting-on-spawn")) {
            return;
        }
        hpe.getEntity().setInvulnerable(true);
    }

    @EventHandler
    public void cancelSpawn(CreatureSpawnEvent cse) { //防止鸡蛋生成鸡
        if (!c.getBoolean("no-chicken-from-egg")) {
            return;
        }
        if (cse.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.EGG)) {
            cse.setCancelled(true);
        }
    }

    @EventHandler
    public void preventSnowFormation(BlockFormEvent bfe) {
        if (!c.getBoolean("no-snow-and-ice-formation")) {
            return;
        }
        if (bfe.getNewState().getType().equals(Material.SNOW) || bfe.getNewState().getType().equals(Material.ICE)) {
            bfe.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent pqe) throws IOException {
        Game game = getGamePlayerIsIn(pqe.getPlayer());
        if (game != null) {
            game.savePlayerQuitData(pqe.getPlayer());
        }
    }
}
