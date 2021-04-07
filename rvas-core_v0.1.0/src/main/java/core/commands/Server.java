package protocol3.commands;

import java.text.DecimalFormat;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.LagProcessor;
import protocol3.backend.PlayerMeta;
import protocol3.backend.ServerMeta;
import protocol3.backend.Utilities;
import protocol3.events.LagPrevention;
import protocol3.events.SpeedLimit;
import protocol3.tasks.ProcessPlaytime;

public class Server implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Arrays.asList(
				"§c========== GENERAL ==========", "§cServer Uptime:§7 " + Utilities.calculateTime(ServerMeta.getUptime()),
				"§cCurrent Population:§7 " + Bukkit.getOnlinePlayers().size(),
				"§cCurrent TPS:§7 " + new DecimalFormat("#.##").format(LagProcessor.getTPS()),
				"§cCurrent Speed Limit:§7 " + (LagProcessor.getTPS() <= 15 ? "36" : "48") + " blocks per second",
				"§cSpeed Limit Kicks:§7 " + SpeedLimit.totalKicks,
				"§cAnti-Cheat Enabled: §7" + (LagProcessor.getTPS() <= 10 ? "True" : "False"),
				"§c========== PLAYER ==========", "§cUnique Joins (§eSince Map Creation§c):§7 " + Bukkit.getOfflinePlayers().length,
				"§cUnique Joins (§eSince Stats Update§c):§7 " + PlayerMeta.Playtimes.keySet().size(),
				"§cDonators:§7 " + PlayerMeta._donatorList.size(),
				"§cLagfags:§7 " + PlayerMeta._lagfagList.size(),
				"§cPermanent Mutes:§7 " + PlayerMeta._permanentMutes.size(),
				"§cOP Accounts:§7 " + Bukkit.getOperators().size(),
				"§c=========== DEBUG ===========", "§cServer Restarting: §7" + (Utilities.restarting ? "True" : "False"),
				"§cTime below acceptable TPS:§7 " + ProcessPlaytime.lowTpsCounter + "ms (600000ms required to restart)"//,
				//"§cWither Count:§7 " + LagPrevention.currentWithers
				)
				.forEach(s -> sender.spigot().sendMessage(new TextComponent(s)));
		return true;
	}
}