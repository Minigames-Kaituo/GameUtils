package fun.kaituo;

import fun.kaituo.event.PlayerChangeGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
        } else {
            return false;
        }
    }
}
