package protocol3.commands;

import java.text.DecimalFormat;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.LagProcessor;

// TPS check

public class Tps implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		double tps = LagProcessor.getTPS();
		if (tps > 20)
			tps = 20;
		if (!new IntRange(1, 20).containsInteger(tps)) {
			TextComponent component = new TextComponent(
					"TPS is either extremely low or still processing. Try again later.");
			sender.spigot().sendMessage(component);
		} else {
			String message_formatted_ticks_per_second = new DecimalFormat("#.##").format(tps);
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
