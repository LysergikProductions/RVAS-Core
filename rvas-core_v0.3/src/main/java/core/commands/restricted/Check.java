package core.commands.restricted;

/* *
 *
 *  About: Check a 3x3 chunk grid around each online player for lag block count
 *          and display the results in an auto-updating GUI inventory
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
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * */

import core.backend.ChatPrint;
import core.data.PlayerMeta;
import core.data.objects.Pair;
import core.backend.utils.Chunks;

import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class Check implements CommandExecutor, Listener {

    public static Map<UUID, Pair<Integer, Location>> lagList = new HashMap<>();
    public static Map<Player, Integer> sortedLagList = new HashMap<>();

    private static Player checker = null;
    public static Inventory lagCheckGUI;

    static {
        lagCheckGUI = Bukkit.createInventory(checker, 54, ChatPrint.fail + "Occupied Laggy Chunks");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(commandSender instanceof Player)) return false;

        try { checker = (Player)commandSender;
        } catch (Exception ignore) { return false; }

        if (!checker.isOp()) { checker.sendMessage("You can't use this!"); return false; }
        Admin.doNotDisturb.remove(checker.getUniqueId());

        updateGUI();
        checker.openInventory(lagCheckGUI);
        return true;
    }

    public static void updateGUI() {
        lagCheckGUI.clear();

        for (Player thisPlayer: Bukkit.getServer().getOnlinePlayers()) {
            int thisCount;

            if (!thisPlayer.isOp()) {

                lagList.remove(thisPlayer.getUniqueId());
                thisCount = Chunks.countChunkLagBlocks(thisPlayer);

                if (thisCount > 192) {
                    lagList.putIfAbsent(thisPlayer.getUniqueId(), new Pair<>(thisCount, thisPlayer.getLocation()));
                    sortedLagList.putIfAbsent(thisPlayer, thisCount);
                }
            }
        }

        sortedLagList = PlayerMeta.sortLagMap(sortedLagList);
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
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == Check.lagCheckGUI) {

            event.setCancelled(true); String thatPlayerName = null;

            try { thatPlayerName = Objects.requireNonNull(event.getCurrentItem()).getItemMeta().getDisplayName();
            } catch (Exception ignore) {}

            if (event.getWhoClicked().getServer().getPlayer(thatPlayerName) == null) checker
                    .chat("/admin spot " + thatPlayerName);
            else checker.chat("/ninjatp " + thatPlayerName);
        }
    }
}
