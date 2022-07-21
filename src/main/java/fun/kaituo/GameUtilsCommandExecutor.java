package fun.kaituo;

import fun.kaituo.event.PlayerChangeGameEvent;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.RayTraceResult;

import java.util.List;

import static fun.kaituo.GameUtils.world;

public class GameUtilsCommandExecutor implements CommandExecutor {
    GameUtils plugin;

    public GameUtilsCommandExecutor(GameUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("changebiome")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage("§c此指令必须由玩家执行！");
                return true;
            }
            if (!sender.isOp()) {
                sender.sendMessage("§c你没有权限执行这个指令！");
                return true;
            }

            if (args.length != 3) {
                sender.sendMessage("§c指令参数错误！使用方法为/changebiome <biome>/auto radius <circular/square>");
                return true;
            }
            World world = Bukkit.getWorld("world");
            Location l = ((Player) sender).getLocation();
            int x = (int) Math.floor(l.getX());
            int y = (int) Math.floor(l.getY());
            int z = (int) Math.floor(l.getZ());
            int radius = Integer.parseInt(args[1]);
            int r = radius - 1;
            if (!args[2].equalsIgnoreCase("circular") && !args[2].equalsIgnoreCase("square")) {
                sender.sendMessage("§c指令参数错误！使用方法为/changebiome <biome>/auto radius <circular/square> ！");
                return true;
            }
            boolean isCircular = args[2].equalsIgnoreCase("circular");
            try {
                if (!args[0].equalsIgnoreCase("auto")) {
                    try {
                        for (int xOffset = -r; xOffset <= r; xOffset++) {
                            if (xOffset == Math.round(-r / 2D)) {
                                Bukkit.broadcastMessage("§a[changebiome]服务器主线程冻结中，生物群系设置操作进度为 25%");
                            } else if (xOffset == 0) {
                                Bukkit.broadcastMessage("§a[changebiome]服务器主线程冻结中，生物群系设置操作进度为 50%");
                            } else if (xOffset == Math.round(r / 2D)) {
                                Bukkit.broadcastMessage("§a[changebiome]服务器主线程冻结中，生物群系设置操作进度为 75%");
                            }
                            for (int zOffset = -r; zOffset <= r; zOffset++) {
                                if (isCircular) {
                                    if (Math.sqrt(Math.pow(xOffset, 2) + Math.pow(zOffset, 2)) > r) {
                                        continue;
                                    }
                                }
                                world.setBiome((x + xOffset), (z + zOffset), Biome.valueOf(args[0].toUpperCase()));
                            }
                        }
                        Bukkit.broadcastMessage("§a[changebiome]生物群系设置操作完成！");
                    } catch (Exception e) {
                        sender.sendMessage("§c生物群系ID错误！");
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    try {
                        for (int xOffset = -r; xOffset <= r; xOffset++) {
                            if (xOffset == Math.round(-r / 2D)) {
                                Bukkit.broadcastMessage("§a[changebiome]服务器主线程冻结中，生物群系设置操作进度为 25%");
                            } else if (xOffset == 0) {
                                Bukkit.broadcastMessage("§a[changebiome]服务器主线程冻结中，生物群系设置操作进度为 50%");
                            } else if (xOffset == Math.round(r / 2D)) {
                                Bukkit.broadcastMessage("§a[changebiome]服务器主线程冻结中，生物群系设置操作进度为 75%");
                            }
                            for (int zOffset = -r; zOffset <= r; zOffset++) {
                                if (isCircular) {
                                    if (Math.sqrt(Math.pow(xOffset, 2) + Math.pow(zOffset, 2)) > r) {
                                        continue;
                                    }
                                }
                                int k = 319;
                                boolean isBlockGotten = false;
                                while (!isBlockGotten) {
                                    if (world.getBlockAt((x + xOffset), k, (z + zOffset)).getType().equals(Material.WATER)) {
                                        isBlockGotten = true;
                                        sender.sendMessage((x + xOffset) + " " + (z + zOffset) + " " + "river");
                                        world.setBiome((x + xOffset), (z + zOffset), Biome.RIVER);
                                    } else if (world.getBlockAt((x + xOffset), k, (z + zOffset)).getType().equals(Material.LAVA)) {
                                        isBlockGotten = true;
                                        sender.sendMessage((x + xOffset) + " " + (z + zOffset) + " " + "badlands");
                                        world.setBiome((x + xOffset), (z + zOffset), Biome.BADLANDS);
                                    } else if (world.getBlockAt((x + xOffset), k, (z + zOffset)).getType().isSolid()) {
                                        isBlockGotten = true;
                                        FileConfiguration config = plugin.getConfig();
                                        Material material = world.getBlockAt((x + xOffset), k, (z + zOffset)).getType();
                                        if (config.contains("change-biome-settings." + material.toString().toLowerCase())) {
                                            world.setBiome((x + xOffset), (z + zOffset), Biome.valueOf(config.getString("change-biome-settings." + material.toString().toLowerCase()).toUpperCase()));
                                        } else {
                                            world.setBiome((x + xOffset), (z + zOffset), Biome.valueOf(config.getString("change-biome-settings.default").toUpperCase()));
                                        }
                                    }
                                    k--;
                                    if (k == -65) {
                                        break;
                                    }
                                }
                            }
                        }
                        Bukkit.broadcastMessage("§a[changebiome]生物群系自动设置操作完成！");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.broadcastMessage("§c[changebiome]发生内部错误！");
                    }

                }
                return true;
            } catch (Exception e) {
                sender.sendMessage("§c指令执行错误！使用方法为/changebiome <biome>/auto radius <circular/square> ！请检查生物群系名称是否正确！");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("changegame")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c此指令必须由玩家执行！");
                return true;
            }
            if (!sender.isOp()) {
                sender.sendMessage("§c你没有权限执行这个指令！");
                return true;
            }
            if (args.length == 0) {
                Bukkit.getPluginManager().callEvent(new PlayerChangeGameEvent((Player) sender, GameUtils.getGamePlayerIsIn((Player) sender), null));
            } else if (args.length == 1) {
                Game game = GameUtils.getGameByName(args[0]);
                if (game != null) {
                    Bukkit.getPluginManager().callEvent(new PlayerChangeGameEvent((Player) sender, GameUtils.getGamePlayerIsIn((Player) sender), game));
                } else {
                    sender.sendMessage("§c该游戏不存在！");
                }
            } else {
                sender.sendMessage("§c指令格式错误！");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("rejoin")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c此指令必须由玩家执行！");
            }
            Player p = (Player) sender;
            PlayerQuitData pqd = GameUtils.getPlayerQuitData(p.getUniqueId());
            if (pqd != null) {
                pqd.getGame().rejoin(p);
            } else {
                sender.sendMessage("§c无法重新加入，游戏不存在或者不支持重新加入");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("placestand")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c此指令必须由玩家执行！");
                return true;
            }
            if (!sender.isOp()) {
                sender.sendMessage("§c你没有权限执行这个指令！");
                return true;
            }
            RayTraceResult result = ((Player)sender).rayTraceBlocks(5, FluidCollisionMode.NEVER);
            if (result != null) {
                Block b = result.getHitBlock();
                if (b != null) {
                    if (b.getType().equals(Material.OAK_BUTTON)) {
                        Location l = b.getLocation().clone();
                        l.setX(l.getX() + 0.5);
                        l.setY(l.getY() - 1);
                        l.setZ(l.getZ() + 0.5);
                        switch (((Directional)b.getBlockData()).getFacing()) {
                            case NORTH -> l.setZ(l.getZ() + 0.2);
                            case SOUTH -> l.setZ(l.getZ() - 0.2);
                            case EAST -> l.setX(l.getX() - 0.2);
                            case WEST -> l.setX(l.getX() + 0.2);
                            case UP, DOWN -> {
                                sender.sendMessage("§c按钮方向不可为向上或向下！");
                                return true;
                            }
                        }
                        ArmorStand stand = (ArmorStand)Bukkit.getWorld("world").spawnEntity(l, EntityType.ARMOR_STAND);
                        stand.setInvisible(true);
                        stand.setCustomName("开始游戏");
                        stand.setGravity(false);
                        stand.setInvulnerable(true);
                    } else {
                        sender.sendMessage("§c请面对橡木按钮！");
                    }
                } else {
                    sender.sendMessage("§c请面对橡木按钮！");
                }
            } else {
                sender.sendMessage("§c请面对橡木按钮！");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("rotatable")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c此指令必须由玩家执行！");
                return true;
            }
            if (!((Player)sender).getGameMode().equals(GameMode.CREATIVE)) {
                sender.sendMessage("§c此指令必须在创造模式下执行！");
                return true;
            }
            switch (args.length) {
                case 3:
                    for (ItemFrame itemFrame: world.getEntitiesByClass(ItemFrame.class)) {
                        if (itemFrame.getLocation().getBlockX() == Integer.parseInt(args[0]) && itemFrame.getLocation().getBlockY() == Integer.parseInt(args[1]) && itemFrame.getLocation().getBlockZ() == Integer.parseInt(args[2])) {
                            List<MetadataValue> values = itemFrame.getMetadata("rotatable");
                            if (values.size() == 0 || !values.get(0).asBoolean()) {
                                sender.sendMessage("该物品展示框不可旋转！");
                            } else {
                                sender.sendMessage("该物品展示框可旋转！");
                            }
                            return true;
                        }
                    }
                    sender.sendMessage("§c该物品展示框不存在！");
                    return true;
                case 4:
                    for (ItemFrame itemFrame: world.getEntitiesByClass(ItemFrame.class)) {
                        if (itemFrame.getLocation().getBlockX() == Integer.parseInt(args[0]) && itemFrame.getLocation().getBlockY() == Integer.parseInt(args[1]) && itemFrame.getLocation().getBlockZ() == Integer.parseInt(args[2])) {
                            if (args[3].equals("true")) {
                                itemFrame.setMetadata("rotatable", new FixedMetadataValue(plugin, true));
                                sender.sendMessage("该物品展示框可旋转！");
                            } else if (args[3].equals("false")) {
                                itemFrame.setMetadata("rotatable", new FixedMetadataValue(plugin, false));
                                sender.sendMessage("该物品展示框不可旋转！");
                            } else {
                                sender.sendMessage("§c指令格式错误！");
                            }
                            return true;
                        }
                    }
                    sender.sendMessage("§c该物品展示框不存在！");
                    return true;
                default :
                    sender.sendMessage("§c指令格式错误！");
                    return true;
            }
        } else {
            return false;
        }
    }
}
