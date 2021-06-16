package core.frontend.GUI;

/* *
 *  About: Inventory-type GUI for ops to see all the players
 *      that have donated to the server and details about their donation/s
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

import core.Main;
import core.data.DonationManager;
import core.data.objects.Donor;
import core.frontend.ChatPrint;

import java.util.List;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class DonorList {

    public static Inventory _donorGUI; static {
        _donorGUI = Bukkit.createInventory(null, 54, ChatPrint.primary + "Donor List");
        _donorGUI.setMaxStackSize(1);
    }

    public static boolean updateDonorGUI() {
        if (DonationManager._donorList.isEmpty()) return false;

        Bukkit.getScheduler().runTask(Main.instance, () -> {
            _donorGUI.clear();

            for (Donor thisDonor: DonationManager._donorList) {
                thisDonor.updateAboveThreshold();

                ItemStack newHead = new ItemStack(Material.PLAYER_HEAD, 1);
                ItemMeta thisHeadMeta = newHead.getItemMeta();
                thisHeadMeta.setDisplayName(thisDonor.getRealIGN());

                boolean valid = thisDonor.isAboveThreshold() &&
                        !DonationManager.isInvalidKey(thisDonor.getDonationKey());

                String validityStr = valid ? "Valid" : "Invalid";
                ChatColor validityClr = valid ? ChatPrint.succeed : ChatPrint.fail;

                String custom_ign = thisDonor.getCustomIGN();
                ChatColor custom_ign_clr = DonationManager
                        .isValidString(custom_ign) ? ChatPrint.clear : ChatPrint.faded;

                String tag = thisDonor.getTagLine();
                ChatColor tag_clr = DonationManager.isValidString(tag) ? ChatPrint.clear : ChatPrint.faded;

                String motd = thisDonor.getMsgOtd();
                ChatColor motd_clr = DonationManager.isValidString(motd) ? ChatPrint.clear : ChatPrint.faded;

                @SuppressWarnings("SpellCheckingInspection")
                List<String> lore = Arrays.asList(
                        ChatPrint.primary + thisDonor.getOfflinePlayer().getName(),
                        ChatPrint.controls + "$" + thisDonor.getSumDonated(),
                        thisDonor.getDonationKey(),
                        validityClr + validityStr + " key!",
                        ChatPrint.desc + "--------------------",
                        "UUID: " + thisDonor.getUserID(),
                        "Custom IGN: " + custom_ign_clr + custom_ign,
                        "First Dono: " + thisDonor.getFirstDonationDate(),
                        "Last Dono: " + thisDonor.getRecentDonationDate(),
                        "Tagline: " + tag_clr + tag,
                        "MOTD: " + motd_clr + motd);

                thisHeadMeta.setLore(lore);
                newHead.setItemMeta(thisHeadMeta);
                _donorGUI.addItem(newHead);
            }
        });
        return true;
    }
}
