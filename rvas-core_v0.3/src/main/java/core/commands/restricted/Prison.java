package core.commands.restricted;

import core.backend.Config;
import core.frontend.ChatPrint;
import core.data.PlayerMeta;
import core.data.PrisonerManager;

import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Prison implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
		Player player = (Player) sender;
		
		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
			sender.sendMessage(ChatPrint.fail + "You can't run this"); return true; }

		if (args.length != 1) {
			sender.sendMessage(ChatPrint.fail + "Invalid syntax. Syntax: /prison [name]"); return true; }

		Player thisPlayer = Bukkit.getPlayer(args[0]);
		
		if (thisPlayer == null) {
			sender.sendMessage(ChatPrint.fail + "Player is not online");
			return true;
			
		} else if (PlayerMeta.isAdmin(thisPlayer) || thisPlayer.isOp()) return false;

		try { PrisonerManager.togglePrisoner(thisPlayer);
		} catch (Exception e) {
			if (Config.debug) e.printStackTrace();
			player.sendMessage(ChatPrint.fail + "Failed to toggle LagPrisoner :/");
			return false;
		}

		if (PrisonerManager.isPrisoner(thisPlayer) && !thisPlayer.isOp()) {
			
			Arrays.asList(

					"\u00A76" + thisPlayer.getName() + " was caught lagging the server!", "\u00A76IP: " +
							Objects.requireNonNull(thisPlayer.getAddress()).toString().split(":")[0].replace("/", ""),
					"\u00A76COORDS: " + Math.round(thisPlayer.getLocation().getX()) + ", "
					+ Math.round(thisPlayer.getLocation().getY()) + ", "
					+ Math.round(thisPlayer.getLocation().getZ())

			).forEach(ln -> Bukkit.getServer().spigot().broadcast(new TextComponent(ln)));

			thisPlayer.getEnderChest().clear();
			thisPlayer.setHealth(0);
		}
		return true;
	}
}
