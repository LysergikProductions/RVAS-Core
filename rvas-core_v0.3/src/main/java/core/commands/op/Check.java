package core.commands.op;

/* *
 *  About: Check a 3x3 chunk grid around each online player for lag-block
 *      count and display the results in an auto-updating GUI inventory
 *
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021 Lysergik Productions (https://github.com/LysergikProductions)
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
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * */

import core.frontend.GUI.LagList;
import core.backend.ex.Critical;
import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Critical
public class Check implements CommandExecutor, Listener {

    private static Player thisSender;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(commandSender instanceof Player)) return false;
        thisSender = (Player) commandSender;

        if (!thisSender.isOp()) { thisSender.sendMessage("You can't use this!"); return false; }
        Admin.doNotDisturb.remove(thisSender.getUniqueId());

        LagList.updateGUI();
        thisSender.openInventory(LagList.lagCheckGUI);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == LagList.lagCheckGUI) {

            event.setCancelled(true); String thatPlayerName = null;

            try { thatPlayerName = Objects.requireNonNull(event.getCurrentItem()).getItemMeta().getDisplayName();
            } catch (Exception ignore) {}

            if (thatPlayerName != null ) {
                if (event.getWhoClicked().getServer().getPlayer(thatPlayerName) == null) {
                    thisSender.chat("/admin spot " + thatPlayerName);

                } else thisSender.chat("/ninjatp " + thatPlayerName);
            }
        }
    }
}
