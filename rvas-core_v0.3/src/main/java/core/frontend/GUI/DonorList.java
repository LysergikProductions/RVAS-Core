package core.frontend.GUI;

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

    public static void updateDonorGUI() {
        Bukkit.getScheduler().runTask(Main.instance, () -> {
            _donorGUI.clear();

            for (Donor thisDonor: DonationManager._donorList) {
                thisDonor.updateAboveThreshold();

                ItemStack newHead = new ItemStack(Material.PLAYER_HEAD, 1);
                ItemMeta thisHeadMeta = newHead.getItemMeta();
                thisHeadMeta.setDisplayName(thisDonor.getRealIGN());

                String validityStr = thisDonor.isAboveThreshold() ? "Valid" : "Invalid";
                ChatColor validityClr = thisDonor.isAboveThreshold() ? ChatPrint.succeed : ChatPrint.fail;

                @SuppressWarnings("SpellCheckingInspection")
                List<String> lore = Arrays.asList(
                        ChatPrint.primary + thisDonor.getPlayer().getName(),
                        ChatPrint.controls + "$" + thisDonor.getSumDonated(),
                        thisDonor.getDonationKey(),
                        validityClr + validityStr + " key!",
                        "UUID: " + thisDonor.getUserID(),
                        "Custom IGN: " + thisDonor.getCustomIGN(),
                        "First Dono: " + thisDonor.getFirstDonationDate(),
                        "Last Dono: " + thisDonor.getRecentDonationDate(),
                        "Tagline: " + thisDonor.getTagLine(),
                        "MOTD: " + thisDonor.getMsgOtd());

                thisHeadMeta.setLore(lore);
                newHead.setItemMeta(thisHeadMeta);
                _donorGUI.addItem(newHead);
            }
        });
    }
}
