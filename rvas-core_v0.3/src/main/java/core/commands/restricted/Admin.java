package core.commands.restricted;

import core.backend.*;
import core.backend.utils.Util;
import core.backend.utils.Restart;
import core.frontend.ChatPrint;

import core.data.DonationManager;
import core.data.objects.Aliases;
import core.data.ThemeManager;
import core.data.objects.Pair;
import core.data.PlayerMeta;

import core.tasks.Analytics;
import core.events.SpeedLimiter;
import core.backend.anno.Critical;

import java.util.*;
import java.io.IOException;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Critical
public class Admin implements CommandExecutor {

	public static List<UUID> Spies = new ArrayList<>();
	public static List<UUID> MsgToggle = new ArrayList<>();
	public static List<UUID> UseRedName = new ArrayList<>();
	public static List<UUID> doNotDisturb = new ArrayList<>();

	public static Map<String, Location> LogOutSpots = new HashMap<>();
	public static boolean disableWarnings = false;

	@Override
	@SuppressWarnings("SpellCheckingInspection")
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
		Player player = (Player) sender;
		
		if (player.isOp()) Analytics.admin_cmd++;
		
		if (args.length == 1) {
			if (!PlayerMeta.isOp(sender)) {
				sender.sendMessage(ChatPrint.fail + "You can't use this.");
				return true;
			}

			UUID senderID = player.getUniqueId();
			
			switch (args[0].toUpperCase()) {

				case "QUIET":
					if (doNotDisturb.contains(senderID)) {
						player.sendMessage(ChatPrint.fail + "Disabled warnings");
						doNotDisturb.remove(senderID);
					} else {
						player.sendMessage(ChatPrint.succeed + "Enabled warnings!");
						doNotDisturb.add(senderID);
					}
					return true;

				case "COLOR":
					if (UseRedName.contains(senderID)) {
						player.sendMessage(ChatPrint.fail + "Disabled red name");
						UseRedName.remove(senderID);
					} else {
						player.sendMessage(ChatPrint.succeed + "Enabled red name!");
						UseRedName.add(senderID);
					}
					return true;
					
				case "SPY":
					if (Spies.contains(senderID)) {
						player.sendMessage(ChatPrint.fail + "Disabled spying on player messages");
						Spies.remove(senderID);
					} else {
						player.sendMessage(ChatPrint.succeed + "Enabled spying on player messages!");
						Spies.add(player.getUniqueId());
					}
					return true;
					
				case "MSGTOGGLE":
					if (MsgToggle.contains(senderID)) {
						player.sendMessage(ChatPrint.succeed + "Enabled receiving player messages!");
						MsgToggle.remove(senderID);
					} else {
						player.sendMessage(ChatPrint.fail + "Disabled receiving player messages!");
						MsgToggle.add(senderID);
					}
					return true;

				case "RELOAD":

					try { ChatPrint.init();
					} catch (Exception ignore) {
						sender.sendMessage(ChatPrint.fail + "Failed to reload colors, setting internal theme..");
						ThemeManager.currentTheme.setToInternalDefaults();
						ChatPrint.init();
					}

					try { Config.load();
					} catch (IOException e) {
						sender.sendMessage(ChatPrint.fail + "Failed to reload configs, restarting..");
						Restart.restart();
					}

					try { DonationManager.loadDonors();
					} catch (Exception ignore) {
						sender.sendMessage(ChatPrint.fail + "Failed to reload donors..");
					}

					player.sendMessage(ChatPrint.succeed + "Successfully reloaded!");
					return true;

				//deprecated
				case "SPEED":
					player.sendMessage(ChatPrint.primary + "Player speeds:");
					List<Pair<Double, String>> speeds = SpeedLimiter.getSpeeds();
					
					for (Pair<Double, String> speedEntry : speeds) {
						double speed = speedEntry.getLeft();
						if(speed == 0) continue;
						String playerName = speedEntry.getRight();
						String color = "\u00A7";
						if (speed >= 64.0)
							color += "c"; // red
						else if (speed >= 48.0)
							color += "e"; // yellow
						else
							color += "a"; // green
						player.sendMessage(color + String.format("%4.1f: %s", speed, playerName));
					}
					player.sendMessage("\u00A76End of speed list.");
					return true;
					
				case "AGRO":
					disableWarnings = !disableWarnings;

					if (disableWarnings) sender.sendMessage(ChatPrint.succeed + "Enabled aggressive speed limit!");
					else sender.sendMessage(ChatPrint.fail + "Disabled aggressive speed limit");

					return true;

				case "CRYSTAL": player.chat(Aliases.invulCrystal); return true;
				case "ILLEGALS": case "ILLEGAL": player.chat(Aliases.illegals_kit); return true;
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("spot")) {
				
				Location loc = LogOutSpots.get(args[1]);
				
				if (loc == null) sender.sendMessage(ChatPrint.fail + "No logout spot logged for " + args[1]);
				else {
					
					String dimension = Util.getDimensionName(loc);
					String location = (int)loc.getX() + " " + (int)loc.getY() + " " + (int)loc.getZ();

					TextComponent logSpot = new TextComponent("\u00A76"+args[1] + " logged out at " + location);

					logSpot.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/ninjatp " + dimension + " " + location));

					sender.spigot().sendMessage(logSpot);
				}
				return true;
			} else if (args[0].equalsIgnoreCase("debug")) {
				if (args[1].equalsIgnoreCase("normal")) {
					Config.debug = true;
					Config.verbose = false;

					player.sendMessage("Config.debug is now true");
					player.sendMessage("Config.verbose is now false");

				} else if (args[1].equalsIgnoreCase("verbose")) {
					Config.debug = true;
					Config.verbose = true;

					player.sendMessage("Config.debug is now true");
					player.sendMessage("Config.verbose is now true");

				} else if (args[1].equalsIgnoreCase("off")) {
					Config.debug = false;
					Config.verbose = false;

					player.sendMessage("Config.debug is now false");
					player.sendMessage("Config.verbose is now false");

				} else {
					Config.debug = !Config.debug;
					player.sendMessage("Config.debug is now " + Config.debug);
				}
			}
		}
		
		TextComponent ops_a = new TextComponent("OP Accounts: ");
		TextComponent ops_b = new TextComponent("" + Bukkit.getOperators().size());
		
		ops_a.setColor(ChatPrint.fail); ops_b.setColor(ChatPrint.desc);
		TextComponent ops = new TextComponent(ops_a, ops_b);
		
		player.sendMessage("");
		player.sendMessage("\u00A7csinse420: \u00A77Server Admin, Developer, Founder");
		player.sendMessage(ops.toLegacyText());
		return true;
	}
}
