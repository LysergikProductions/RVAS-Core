package core.commands;

import core.frontend.ChatPrint;
import core.data.DonationManager;
import core.backend.anno.Critical;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Critical
@SuppressWarnings("SpellCheckingInspection")
public class Redeem implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
		Player player = (Player) sender;

		if (args.length != 1) {
			player.sendMessage(ChatPrint.fail + "Syntax: /redeem [code]"); return true; }

		if (DonationManager._validDonors.contains(player.getUniqueId())) {
			player.sendMessage(ChatPrint.secondary + "You are already a donator. You keep it for life!"); return true; }

		if (DonationManager.DonorCodes.contains(args[0]) && !DonationManager.UsedDonorCodes.contains(args[0])) {

			DonationManager.UsedDonorCodes.add(args[0]);

			try { if (!DonationManager.setDonor(
						player, args[0], 0.00)) sender.sendMessage(ChatPrint.fail + "Invalid key");
			} catch (IOException e) { e.printStackTrace(); }

			Bukkit.getServer().spigot()
					.broadcast(new TextComponent("\u00A76" + player.getName() + " just donated to the server!"));

		} else player.sendMessage(ChatPrint.fail + "This code is not valid.");
		return true;
	}
}
