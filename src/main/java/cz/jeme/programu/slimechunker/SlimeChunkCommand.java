package cz.jeme.programu.slimechunker;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SlimeChunkCommand extends Command {
    protected SlimeChunkCommand() {
        super("slimechunk", "Checks for a slime chunk at your location", "false", Collections.emptyList());
        setPermission("slimechunker.slimechunk");
        register();
    }

    private void register() {
        Bukkit.getCommandMap().register("slimechunker", this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.prefix("<red>This command is only runnable as a player!</red>"));
            return true;
        }
        Chunk chunk = player.getLocation().getChunk();

        World world = player.getWorld();

        String message;
        if (world.getEnvironment() == World.Environment.NORMAL) {
            if (chunk.isSlimeChunk()) {
                message = SlimeChunker.config.getString("messages.is-a-slime-chunk");
            } else {
                message = SlimeChunker.config.getString("messages.not-a-slime-chunk");
            }
        } else {
            message = SlimeChunker.config.getString("messages.wrong-dimension");
        }
        if (message == null) throw new NullPointerException("Message is null!");
        player.sendMessage(Messages.from(message));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
