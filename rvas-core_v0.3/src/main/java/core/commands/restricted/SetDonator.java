package core.commands.restricted;

import core.backend.Config;
import core.frontend.ChatPrint;
import core.data.DonationManager;
import core.annotations.Critical;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

@Critical
@SuppressWarnings("SpellCheckingInspection")
public class SetDonator implements CommandExecutor {

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
			sender.sendMessage(new TextComponent(ChatPrint.fail +
					"You can't run this").toLegacyText()); return false;
		}

		int argCount = args.length;
		Player donator = sender.getServer().getPlayer(args[0].trim());

		if (donator == null) {
			sender.sendMessage(new TextComponent(
					ChatPrint.fail + "Player is not online").toLegacyText());
			return true;
		}

		if (argCount == 1) {
			try {
				if (!DonationManager.setDonor(donator, "INVALID", 0.00)) return false;
				else {
					if (DonationManager.isValidDonor(donator)) donator.sendMessage(new TextComponent(
							ChatPrint.primary + "Added donator!").toLegacyText());
					else donator.sendMessage(new TextComponent(
							ChatPrint.primary + "Removed donator!").toLegacyText());
					return true;
				}
			} catch (IOException e) { e.printStackTrace(); }

		} else if (argCount != 3) {
			sender.sendMessage(new TextComponent(ChatPrint.fail +
					"Invalid syntax. Syntax: /setdonator [name] [key] [$amount]").toLegacyText());
			return true;
		}

		String key = args[1].trim();

		try {
			if (!DonationManager.setDonor(donator, key, Double.parseDouble(args[2]))
			) return false; // < - return false when setDonor returns false

		} catch (Exception e) {

			sender.sendMessage(new TextComponent(ChatPrint.fail +
					"Failed to set donator. Please report this to " + Config.getValue("admin")).toLegacyText());

			sender.sendMessage(new TextComponent(ChatPrint.faded + e.getMessage()).toLegacyText());
			e.printStackTrace(); return false;
		}

		if (DonationManager.isValidDonor(donator)) {
			Bukkit.getServer().spigot()
					.broadcast(new TextComponent("\u00A76" + donator.getName() + " just donated to the server!"));

			try {
				Objects.requireNonNull(DonationManager.getDonorByUUID(donator.getUniqueId()))
						.sendMessage(new TextComponent(ChatPrint.controls +
								"/w an op about setting your custom IGN, tag, and motd!"));

			} catch (Exception ignore) { }
			return true;
		}
		return false;
	}
}
