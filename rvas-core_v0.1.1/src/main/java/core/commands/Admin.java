package core.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import core.backend.Config;
import core.backend.Pair;
import core.backend.PlayerMeta;
import core.backend.Utilities;
import core.events.SpeedLimit;

import java.io.IOException;
import java.util.*;

public class Admin implements CommandExecutor {

	public static List<UUID> Spies = new ArrayList<UUID>();
	public static List<UUID> MsgToggle = new ArrayList<UUID>();
	public static List<UUID> UseRedName = new ArrayList<UUID>();
	public static Map<String, Location> LogOutSpots = new HashMap<>();
	public static boolean disableWarnings = false;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if (args.length == 1) {
			if (!PlayerMeta.isOp(sender)) {
				sender.sendMessage(new TextComponent("§cYou can't use this."));
				return true;
			}
			switch (args[0].toUpperCase()) {
				case "COLOR":
					if (UseRedName.contains(player.getUniqueId())) {
						player.spigot().sendMessage(new TextComponent("§6Disabled red name."));
						UseRedName.remove(player.getUniqueId());
					} else {
						player.spigot().sendMessage(new TextComponent("§6Enabled red name."));
						UseRedName.add(player.getUniqueId());
					}
					return true;
				case "SPY":
					if (Spies.contains(player.getUniqueId())) {
						player.spigot().sendMessage(new TextComponent("§6Disabled spying on player messages."));
						Spies.remove(player.getUniqueId());
					} else {
						player.spigot().sendMessage(new TextComponent("§6Enabled spying on player messages."));
						Spies.add(player.getUniqueId());
					}
					return true;
				case "MSGTOGGLE":
					if (MsgToggle.contains(player.getUniqueId())) {
						player.spigot().sendMessage(new TextComponent("§6Enabled recieving player messages."));
						MsgToggle.remove(player.getUniqueId());
					} else {
						player.spigot().sendMessage(new TextComponent("§6Disabled recieving player messages."));
						MsgToggle.add(player.getUniqueId());
					}
					return true;
				case "RELOAD":
					try {
						Config.load();
						sender.spigot().sendMessage(new TextComponent("§aSuccessfully reloaded."));

					} catch (IOException e) {
						sender.spigot().sendMessage(new TextComponent("§4Failed to reload."));
						Utilities.restart();
					}
					return true;
				case "SPEED":
					player.spigot().sendMessage(new TextComponent("§6Player speeds:"));
					List< Pair<Double, String> > speeds = SpeedLimit.getSpeeds();
					for (Pair<Double, String> speedEntry : speeds) {
						double speed = speedEntry.getLeft();
						if(speed == 0) continue;
						String playerName = speedEntry.getRight();
						String color = "§";
						if (speed >= 64.0)
							color += "c"; // red
						else if (speed >= 48.0)
							color += "e"; // yellow
						else
							color += "a"; // green
						player.spigot().sendMessage(new TextComponent(color
								+ String.format("%4.1f: %s", speed, playerName)));
					}
					player.spigot().sendMessage(new TextComponent("§6End of speed list."));
					return true;
				case "AGRO":
					disableWarnings = !disableWarnings;
					if(disableWarnings) {
						sender.spigot().sendMessage(new TextComponent("§6Enabled aggressive speed limit."));
					}
					else {
						sender.spigot().sendMessage(new TextComponent("§6Disabled aggressive speed limit."));
					}
					return true;
					
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("spot")) {
				Location l = LogOutSpots.get(args[1]);
				if (l == null) {
					sender.sendMessage(new TextComponent("§6No logout spot logged for " + args[1]));
				} else {
					sender.sendMessage(new TextComponent("§6"+args[1] + " logged out at " + l.getX() + " " + l.getY() + " " + l.getZ()));
				}
				return true;
			}
		}
		player.spigot().sendMessage(new TextComponent("§csinse420: §7Systems Administrator, Developer, Founder"));
		return true;
	}
}