package fun.kaituo;

import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class GameUtilsTabCompleter implements TabCompleter {
    List<String> biomeNames;
    List<String> modes;

    public GameUtilsTabCompleter() {
        biomeNames = new ArrayList<>();
        modes = new ArrayList<>();
        biomeNames.add("auto");
        for (Biome b : Biome.values()) {
            biomeNames.add(b.name().toLowerCase());
        }
        modes.add("circular");
        modes.add("square");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("changebiome")) {
            if (args.length == 1) {
                return biomeNames;
            } else if (args.length == 3) {
                return modes;
            }
        } else if (command.getName().equalsIgnoreCase("changegame")) {
            if (args.length == 1) {
                return (List<String>) GameUtils.getRegisteredGames(true);
            }
        }
        return null;
    }
}
