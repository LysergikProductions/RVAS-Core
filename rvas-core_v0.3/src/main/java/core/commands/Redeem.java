package core.commands;

/* *
 *  About: A command for non-ops to register themselves as
 * 		Donor objects when they have a valid donation key
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

import core.frontend.ChatPrint;
import core.data.DonationManager;
import core.backend.ex.Critical;

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
