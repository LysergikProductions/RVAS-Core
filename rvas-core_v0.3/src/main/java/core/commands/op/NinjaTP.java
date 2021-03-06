package core.commands.op;

/* *
 *  About: Allow ops to vanish and teleport at the same time.
 *          Teleporting to other dimensions also requires simpler
 *          syntax. Developed primarily for internal use.
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
import core.backend.ex.Critical;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Critical
public class NinjaTP implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

        if (!(sender instanceof Player)) return false;
        Player player = (Player)sender;

        if (args.length == 4) {
            String dimension = args[0].trim();

            String loc =  args[1].trim() + " " + args[2].trim() + " " + args[3].trim();
            String tp_cmd = "/execute in " + dimension + " run tp @p[name=" + player.getName() + "] " + loc;

            player.chat("/sv on"); // <- requires SuperVanish plugin
            scheduleSyncedTP(player, tp_cmd);

        } else if (args.length == 1) {
            String tp_cmd = "/tp " + args[0].trim();

            player.chat("/sv on"); // <- requires SuperVanish plugin
            scheduleSyncedTP(player, tp_cmd);
        }
        return true;
    }

    final static TextComponent t3 = new TextComponent(ChatPrint.faded + "teleporting in 3 seconds..");
    final static TextComponent t2 = new TextComponent(ChatPrint.faded + "teleporting in 2 seconds..");
    final static TextComponent t1 = new TextComponent(ChatPrint.faded + "teleporting now..");

    static void scheduleSyncedTP(Player thisPlayer, String thisCmd) {
        thisPlayer.sendMessage(t3.toLegacyText());

        Bukkit.getScheduler().scheduleSyncDelayedTask(core.Main.instance, () ->
                thisPlayer.sendMessage(t2.toLegacyText()), 20L);

        Bukkit.getScheduler().scheduleSyncDelayedTask(core.Main.instance, () ->
                thisPlayer.sendMessage(t1.toLegacyText()), 48L);

        Bukkit.getScheduler().scheduleSyncDelayedTask(core.Main.instance, () ->
                thisPlayer.chat(thisCmd), 60L);
    }
}
