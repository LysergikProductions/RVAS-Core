package core.frontend.GUI;

/* *
 *  About: Inventory-type GUI for ops to see all the players
 *      that are very close to a potential lag machine chunk
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

import core.data.objects.Pair;
import core.frontend.ChatPrint;
import core.backend.utils.Chunks;
import core.backend.utils.Util;

import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LagList {

    public static Map<UUID, Pair<Integer, Location>> lagList = new HashMap<>();
    public static Map<Player, Integer> sortedLagList = new HashMap<>();

    public static Inventory lagCheckGUI; static {
        LagList.lagCheckGUI = Bukkit.createInventory(null, 54, ChatPrint.fail + "Occupied Laggy Chunks"); }

    public static void updateGUI() {
        Bukkit.getScheduler().runTask(core.Main.instance, () -> {
            lagCheckGUI.clear(); // do not also clear sorted list, to keep results persistent

            for (Player thisPlayer: Bukkit.getServer().getOnlinePlayers()) {
                int thisCount;

                if (!thisPlayer.isOp()) {

                    lagList.remove(thisPlayer.getUniqueId());
                    thisCount = Chunks.countChunkLagBlocks(thisPlayer);

                    if (thisCount > 255) {
                        lagList.putIfAbsent(thisPlayer.getUniqueId(),
                                new Pair<>(thisCount, thisPlayer.getLocation()));
                        sortedLagList.putIfAbsent(thisPlayer, thisCount);
                    }
                }
            }

            sortedLagList = Util.sortLagMap(sortedLagList);
            for (Player thisPlayer: sortedLagList.keySet()) {

                String thisName = thisPlayer.getName();
                ItemStack newHead = new ItemStack(Material.PLAYER_HEAD, 1);

                ItemMeta thisHeadMeta = newHead.getItemMeta();
                thisHeadMeta.setDisplayName(thisName);

                List<String> lore = Collections.singletonList("\u00A7c" + sortedLagList.get(thisPlayer));
                thisHeadMeta.setLore(lore);
                newHead.setItemMeta(thisHeadMeta);
                lagCheckGUI.addItem(newHead);
            }
        });
    }
}
