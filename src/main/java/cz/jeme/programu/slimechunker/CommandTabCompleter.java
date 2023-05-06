package cz.jeme.programu.slimechunker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		String commandName = command.getName();
		if (commandName.equalsIgnoreCase("slimechunker")) {
			return slimechunkerCommand(args);
		}
		return new ArrayList<String>();
	}

	private List<String> slimechunkerCommand(String[] args) {
		if (args.length == 1) {
			List<String> array = new ArrayList<String>();
			array.add("reload");
			return array;
		}
		return new ArrayList<String>();
	}

}
