package protocol3.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;

// OP-only say command.

public class Say implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
			sender.spigot().sendMessage(new TextComponent("§cUnknown command."));
			return true;
		}
		final String[] data = {""};
		Arrays.stream(args).forEach(arg -> data[0] += arg + " ");
		data[0] = data[0].trim();
		data[0] = data[0].replace("§", "");
		if (data[0].isEmpty()) {
			sender.spigot().sendMessage(new TextComponent("§cNo message specified."));
			return true;
		}
		Bukkit.spigot().broadcast(new TextComponent("§d[Server] " + data[0]));
		System.out.println("§d[Server] " + data[0]);
		return true;
	}

}
