package core.commands.restricted;

import core.backend.Config;
import core.frontend.ChatPrint;
import core.data.DonationManager;
import core.backend.anno.Critical;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

@Critical
public class SetDonorCmd implements CommandExecutor {

	@SuppressWarnings("SpellCheckingInspection")
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
			sender.sendMessage(ChatPrint.fail + "You can't run this"); return false; }

		int argCount = args.length;
		Player donator = sender.getServer().getPlayer(args[0].trim());

		if (donator == null) {
			sender.sendMessage(ChatPrint.fail + "Player is not online");
			return true;
		}

		if (argCount != 3) {
			sender.sendMessage(ChatPrint.fail + "Invalid syntax. Syntax: /setdonator [name] [key] [$amount]");
			return true;
		}

		String key = args[1].trim();

		try {
			if (!DonationManager.setDonor(donator, key, Double.parseDouble(args[2]))
			) return false; // < - return false when setDonor returns false

		} catch (Exception e) {

			sender.sendMessage(ChatPrint.fail +
					"Failed to set donator. Please report this to " + Config.getValue("admin"));

			sender.sendMessage(ChatPrint.faded + e.getMessage());
			e.printStackTrace(); return false;
		}

		if (DonationManager.isValidDonor(DonationManager.getDonorByUUID(donator.getUniqueId()))) {
			Bukkit.getServer().spigot().broadcast(
					new TextComponent("\u00A76" + donator.getName() + " just donated to the server!"));

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
