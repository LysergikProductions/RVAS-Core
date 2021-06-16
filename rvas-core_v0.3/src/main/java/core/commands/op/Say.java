package core.commands.op;

import core.frontend.ChatPrint;
import core.backend.ex.Critical;

import java.util.Arrays;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

@Critical
public class Say implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatPrint.fail + "Unknown command.");
			return true;
		}

		final String[] data = {""};
		Arrays.stream(args).forEach(arg -> data[0] += arg + " ");
		data[0] = data[0].trim();
		data[0] = data[0].replace("\u00A7", "");

		if (data[0].isEmpty()) {
			sender.sendMessage(ChatPrint.fail + "No message specified.");
			return true;
		}

		Bukkit.spigot().broadcast(new TextComponent("\u00A7d[Server] " + data[0]));
		System.out.println("\u00A7d[Server] " + data[0]);
		return true;
	}
}
