package net.obnoxint.mcdev.consolename;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BroadcastEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final String message;
    private final String prefix;
    private final CommandSender sender;

    BroadcastEvent(String prefix, String message, CommandSender sender) {
        this.prefix = prefix;
        this.message = message;
        this.sender = sender;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getMessage() {
        return message;
    }

    public String getPrefix() {
        return prefix;
    }

    public CommandSender getSender() {
        return sender;
    }

}
