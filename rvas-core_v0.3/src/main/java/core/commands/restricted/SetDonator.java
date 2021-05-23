package core.commands.restricted;

import core.data.PlayerMeta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class SetDonator implements CommandExecutor {

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
			sender.sendMessage("\u00A7cYou can't run this.");
			return true;
		}
		if (args.length != 1) {
			sender.sendMessage("\u00A7cInvalid syntax. Syntax: /setdonator [name]");
			return true;
		}
		Player donator = Bukkit.getPlayer(args[0]);
		if (donator == null) {
			sender.sendMessage("\u00A7cPlayer is not online.");
			return true;
		}
		PlayerMeta.setDonator(donator, !PlayerMeta.isDonator(donator));
		if (PlayerMeta.isDonator(donator)) {
			Bukkit.getServer().spigot()
					.broadcast(new TextComponent("\u00A76" + donator.getName() + " just donated to the server!"));
		}
		return true;
	}
}
