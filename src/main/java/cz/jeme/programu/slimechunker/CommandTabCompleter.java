package cz.jeme.programu.slimechunker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        String commandName = command.getName();
        if (commandName.equalsIgnoreCase("slimechunker")) {
            return slimechunkerCommand(args);
        }
        return new ArrayList<>();
    }

    private List<String> slimechunkerCommand(String[] args) {
        if (args.length == 1) {
            List<String> array = new ArrayList<>();
            array.add("reload");
            return array;
        }
        return new ArrayList<>();
    }

}
