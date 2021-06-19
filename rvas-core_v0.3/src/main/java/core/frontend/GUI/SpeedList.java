package core.frontend.GUI;

/* *
 *  About: Inventory-type GUI for ops to see the current live speed of all
 *      players who are moving faster than 40% of the current speed limit
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

import core.Main;
import core.data.objects.Pair;
import core.events.SpeedLimiter;
import core.backend.utils.Util;
import core.frontend.ChatPrint;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpeedList {

    public static Map<Player, Double> sortedSpeedsList = new HashMap<>();

    public static Inventory speedGUI; static {
        SpeedList.speedGUI = Bukkit.createInventory(null, 54, ChatPrint.fail + "Speeds List"); }

    public static void updateGUI() throws IllegalArgumentException {
        Bukkit.getScheduler().runTask(Main.instance, () -> {
            speedGUI.clear(); sortedSpeedsList.clear();
            List<Pair<Double, String>> speeds = SpeedLimiter.getSpeeds();

            for (Pair<Double, String> speedEntry : speeds) {
                double speed = speedEntry.getLeft();
                if(speed == 0) continue;

                String thisName = speedEntry.getRight();
                if (speed >= SpeedLimiter.currentSpeedLimit * 0.4) {
                    sortedSpeedsList.putIfAbsent(Bukkit.getPlayer(thisName), speedEntry.getLeft());
                }
            }

            sortedSpeedsList = Util.sortSpeedMap(sortedSpeedsList);
            for (Player thisPlayer: sortedSpeedsList.keySet()) {
                ItemStack newHead = new ItemStack(Material.PLAYER_HEAD, 1);
                ItemMeta thisHeadMeta = newHead.getItemMeta();
                thisHeadMeta.setDisplayName(thisPlayer.getName());

                String color = "\u00A7";
                color += "c"; // red
                List<String> lore = Collections
                        .singletonList(color + String.format("%4.1f", sortedSpeedsList.get(thisPlayer)));

                thisHeadMeta.setLore(lore);
                newHead.setItemMeta(thisHeadMeta);
                speedGUI.addItem(newHead);
            }
        });
    }
}
