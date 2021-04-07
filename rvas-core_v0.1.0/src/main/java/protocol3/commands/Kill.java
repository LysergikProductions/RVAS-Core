package protocol3.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Kill implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender) && args.length == 0) {
			((Player) sender).setHealth(0);
			return true;
		} else {
			if (args.length == 1 && Bukkit.getPlayer(args[0]) != null) {
				if (sender instanceof ConsoleCommandSender) {
					Objects.requireNonNull(Bukkit.getPlayer(args[0])).setHealth(0);
				} else if (sender instanceof Player) {
					if ((sender).isOp()) {
						Objects.requireNonNull(Bukkit.getPlayer(args[0])).setHealth(0);
					}
				}
			}
		}
		return true;
	}
}
