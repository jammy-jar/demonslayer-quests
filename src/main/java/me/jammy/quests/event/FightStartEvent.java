package me.jammy.quests.event;

import lombok.Getter;
import me.jammy.quests.model.boss.Boss;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class FightStartEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled;
    @Getter private final Boss boss;

    public FightStartEvent(@NotNull final Player player, @NotNull final Boss boss) {
        super(player);
        this.isCancelled = false;
        this.boss = boss;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
