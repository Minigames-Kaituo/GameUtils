package fun.kaituo.event;

import fun.kaituo.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangeGameEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    Player player;
    Game fromGame;
    Game toGame;


    public PlayerChangeGameEvent(Player p, Game fromGame, Game toGame) {
        this.player = p;
        this.fromGame = fromGame;
        this.toGame = toGame;
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

    public Game getFromGame() {
        return fromGame;
    }

    public Game getToGame() {
        return toGame;
    }
}
