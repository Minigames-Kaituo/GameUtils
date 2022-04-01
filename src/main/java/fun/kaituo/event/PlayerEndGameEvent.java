package fun.kaituo.event;

import fun.kaituo.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEndGameEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    Player player;
    Game game;


    public PlayerEndGameEvent(Player p, Game game) {
        this.player = p;
        this.game = game;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }
}
