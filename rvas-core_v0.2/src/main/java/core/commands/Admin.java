package core.commands;

import core.Main;
import core.backend.*;

import core.events.SpeedLimiter;
import core.tasks.Analytics;

import java.io.IOException;
import java.util.*;

import io.ipgeolocation.api.IPGeolocationAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Admin implements CommandExecutor {

	public static List<UUID> Spies = new ArrayList<>();
	public static List<UUID> MsgToggle = new ArrayList<>();
	public static List<UUID> UseRedName = new ArrayList<>();
	public static Map<String, Location> LogOutSpots = new HashMap<>();
	public static boolean disableWarnings = false;

	@Override
	@SuppressWarnings("deprecation")
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player player = (Player) sender;
		
		if (!player.isOp()) Analytics.admin_cmd++;
		
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
						Main.GeoIP_api = new IPGeolocationAPI(Config.getValue("IPGeolocationAPI.key"));

						sender.spigot().sendMessage(new TextComponent("§aSuccessfully reloaded."));

					} catch (IOException e) {
						sender.spigot().sendMessage(new TextComponent("§4Failed to reload."));
						Utilities.restart();
					}
					return true;
					
				case "SPEED":
					player.spigot().sendMessage(new TextComponent("§6Player speeds:"));
					List< Pair<Double, String> > speeds = SpeedLimiter.getSpeeds();
					
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

				case "CRYSTAL":
					player.chat(Aliases.invulCrystal);
					return true;

				case "ILLEGALS":
				case "ILLEGAL":
					player.chat(Aliases.illegals_kit);
					return true;
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("spot")) {
				
				Location loc = LogOutSpots.get(args[1]);
				
				if (loc == null) {
					sender.sendMessage(new TextComponent("§6No logout spot logged for " + args[1]));
				} else {
					
					String dimension = Utilities.getDimensionName(loc);
					String location = (int)loc.getX() + " " + (int)loc.getY() + " " + (int)loc.getZ();

					TextComponent logSpot = new TextComponent("§6"+args[1] + " logged out at " + location);
					
					logSpot.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/execute in " + dimension + " run tp @s " + location));
					
					sender.sendMessage(logSpot);
				}
				return true;
			}
		}
		
		TextComponent ops_a = new TextComponent("OP Accounts: ");
		TextComponent ops_b = new TextComponent("" + Bukkit.getOperators().size());
		
		ops_a.setColor(ChatColor.RED); ops_b.setColor(ChatColor.GRAY);
		TextComponent ops = new TextComponent(ops_a, ops_b);
		
		player.spigot().sendMessage(new TextComponent(""));
		player.spigot().sendMessage(new TextComponent("§csinse420: §7Server Admin, Developer, Founder"));
		player.spigot().sendMessage(ops);
		return true;
	}
}
