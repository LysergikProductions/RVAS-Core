package core.commands;

import core.backend.LagProcessor;
import core.backend.PlayerMeta;
import core.tasks.Analytics;

import java.text.DecimalFormat;
import org.apache.commons.lang.math.IntRange;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class Tps implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		double tps = LagProcessor.getTPS();
		
		if (!PlayerMeta.isAdmin((Player)sender)) Analytics.tps_cmd++;
		
		if (tps > 20)
			tps = 20;
		if (!new IntRange(1, 20).containsInteger(tps)) {
			TextComponent component = new TextComponent(
					"TPS is either extremely low or still processing. Try again later.");
			sender.spigot().sendMessage(component);
		} else {
			String message_formatted_ticks_per_second = new DecimalFormat("0.000").format(tps);
			double ticks_per_second_percentage = Math.round(100 - ((tps / 20.0D) * 100.0D));
			String message_formatted_percentage = new DecimalFormat("###.##").format(ticks_per_second_percentage);
			TextComponent component = new TextComponent(
					"TPS is " + message_formatted_ticks_per_second + ", which is " + message_formatted_percentage + "% slower than normal.");

			switch (((int) ticks_per_second_percentage)/10) {
				case 0:
				case 1:
					component.setColor(ChatColor.GREEN);
					break;
				case 2:
				case 3:
					component.setColor(ChatColor.YELLOW);
					break;
				case 4:
				case 5:
				case 6:
					component.setColor(ChatColor.GOLD);
					break;
				case 7:
				case 8:
				case 9:
				case 10:
					component.setColor(ChatColor.RED);
					break;
				default:
					component.setColor(ChatColor.LIGHT_PURPLE);
					break;
			}

			sender.spigot().sendMessage(component);
		}
		return true;
	}
}
