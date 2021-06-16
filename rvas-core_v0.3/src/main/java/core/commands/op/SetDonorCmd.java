package core.commands.op;

/* *
 *  About: A command for ops to register brand new Donor
 * 		objects when they have a valid donation key
 *
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * */

import core.backend.Config;
import core.frontend.ChatPrint;
import core.data.DonationManager;
import core.backend.ex.Critical;

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
		if (argCount == 2 && args[1].equals("DELETE")) {

			try { DonationManager._donorList.remove(DonationManager.getDonorByName(args[0]));
			} catch (Exception ignore) { sender.sendMessage(ChatPrint.fail + "Failed to remove this donor."); }
			return true;

		} else if (argCount != 3) {
			sender.sendMessage(ChatPrint.fail + "Invalid syntax. Syntax: /setdonator [name] [key] [$amount]");
			return true;
		}

		Player donator = sender.getServer().getPlayer(args[0].trim());
		if (donator == null) {
			sender.sendMessage(ChatPrint.fail + "Player is not online");
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
