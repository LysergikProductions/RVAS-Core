package core.commands.restricted;

/* *
 *
 *  About: Allow ops to read and modify Donor objects stored in memory
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
import core.data.objects.Donor;
import core.annotations.Critical;

import java.util.UUID;
import java.util.Arrays;
import java.util.Objects;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Critical
@SuppressWarnings("SpellCheckingInspection")
public class DonorCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            System.out.println("TODO: sending donor info to console"); return false; }

        if (args.length >= 3) {

            if (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("tag") &&
                    !args[1].equalsIgnoreCase("motd") && !args[1].equalsIgnoreCase("set") &&
                    !args[1].equalsIgnoreCase("key") && !args[1].equalsIgnoreCase("ign") &&
                    !args[1].equalsIgnoreCase("refresh")) {

                sender.sendMessage(ChatPrint.fail +
                        "Invalid syntax. Syntax: /donor [name] [add/tag/ign/motd/key/set/refresh]");
                return false;
            }

            // Concatenate args int message
            final String[] msg = {""};
            int[] i = {0};

            Arrays.stream(args.clone()).forEach(str ->  {
                i[0]++;

                if (i[0] > 2) msg[0] += str + " ";
            });
            msg[0] = msg[0].trim();

            Donor thisDonor = Objects.requireNonNull(DonationManager.getDonorByName(args[0]));
            switch (args[1].toLowerCase()) {

                case "add":

                    try { Double.parseDouble(args[2]);
                    } catch (Exception ignore) {
                        sender.sendMessage(ChatPrint.fail + "Invalid number."); return false; }

                    thisDonor.addToSum(Double.parseDouble(args[2])); // <- updates validity automatically
                    thisDonor.setRecentDonationDate();

                    sender.sendMessage(ChatPrint.primary + "Successfully added $" + args[2] + " to " +
                            args[0] + "'s profile for a total of $" + thisDonor.getSumDonated());
                    return true;

                case "set":

                    double thisAmount;

                    try { thisAmount = Double.parseDouble(args[2]);
                    } catch (Exception ignore) {
                        sender.sendMessage(ChatPrint.fail + "Invalid number."); return false; }

                    thisDonor.setSumDonated(thisAmount);
                    sender.sendMessage(ChatPrint.primary +
                            "Successfully set sum donated to $" + thisDonor.getSumDonated());

                    return true;

                case "key":

                    String newKey = args[2].trim();

                    if (DonationManager.isInvalidKey(newKey)) {
                        sender.sendMessage(ChatPrint.fail + "Invalid key."); return false; }

                    if (DonationManager.DonorCodes.contains(newKey) &&
                            !DonationManager.UsedDonorCodes.contains(newKey)) {

                        thisDonor = new Donor(thisDonor.getUserID(), newKey, thisDonor.getSumDonated());
                        DonationManager.UsedDonorCodes.add(newKey);

                    } else { sender.sendMessage(ChatPrint.fail + "Invalid key."); return false; }

                    sender.sendMessage(ChatPrint.primary +
                            "Successfully set donation key to " + thisDonor.getDonationKey());

                    thisDonor.updateValidity(); return true;

                case "tag":

                    if (msg[0].length() < 1 || msg[0].length() > 54) {
                        sender.sendMessage(ChatPrint.fail + "Invalid tag. Use 1 to 54 characters."); return false; }

                    try { Objects.requireNonNull(DonationManager
                                .getDonorByName(args[0])).setTagLine(msg[0]);

                    } catch (Exception ignore) {
                        sender.sendMessage(ChatPrint.fail + "Internal error. Failed to set tag to:");
                        sender.sendMessage(ChatPrint.secondary + msg[0]); return false;
                    }

                    sender.sendMessage(ChatPrint.primary + "Successfully set donor tagline to:");
                    sender.sendMessage(ChatPrint.secondary + msg[0]); return true;

                case "motd":

                    if (msg[0].length() < 2 || msg[0].length() > 48) {
                        sender.sendMessage(ChatPrint.fail + "Invalid MOTD. Use 3 to 48 characters."); return false; }

                    try { Objects.requireNonNull(DonationManager
                                .getDonorByName(args[0])).setMsgOtd(msg[0]);

                    } catch (Exception ignore) {
                        sender.sendMessage(ChatPrint.fail + "Internal error. Failed to set MOTD to:");
                        sender.sendMessage(ChatPrint.secondary + msg[0]); return false;
                    }

                    sender.sendMessage(ChatPrint.primary + "Successfully set donor MOTD to:");
                    sender.sendMessage(ChatPrint.secondary + msg[0]); return true;

                case "ign":

                    if (args.length != 3) {
                        sender.sendMessage(ChatPrint.fail + "Invalid syntax. Syntax: /donor [name] ign [one_word_ign]");
                        return false;
                    }

                    if (msg[0].length() < 3 || msg[0].length() > 20) {
                        sender.sendMessage(ChatPrint.fail + "Invalid IGN. Use 3 to 20 characters."); return false;

                    } else if (DonationManager.isRestrictedIGN(msg[0])) {
                        sender.sendMessage(ChatPrint.fail + "Invalid IGN. Use another name."); return false;

                    } else if (DonationManager.isExistingCustomIGN(msg[0])) {
                        sender.sendMessage(ChatPrint.fail + "IGN is alrady taken. Try another name."); return false; }

                    try { Objects.requireNonNull(DonationManager
                                .getDonorByName(args[0])).setCustomIGN(msg[0]);

                    } catch (Exception ignore) {
                        sender.sendMessage(ChatPrint.fail + "Internal error. Failed to set IGN to:");
                        sender.sendMessage(ChatPrint.secondary + msg[0]); return false;
                    }

                    sender.sendMessage(ChatPrint.primary + "Successfully set custom IGN to:");
                    sender.sendMessage(ChatPrint.secondary + msg[0]); return true;

                case "refresh":

                    try { Objects.requireNonNull(DonationManager.getDonorByName(args[0])).updateDonorIGN();
                    } catch (Exception ignore) {
                        sender.sendMessage(ChatPrint.fail + "Internal error. Failed to update IGN."); return false; }

                default: return false;
            }

        } else if (args.length != 1) {
            sender.sendMessage(ChatPrint.fail + "Invalid syntax. Syntax: /donor [name]"); return false;

        } else if (!sender.isOp()) {
            sender.sendMessage(ChatPrint.fail + "yeah no"); return false; }

        String thisSearch = args[0].trim();
        UUID thisID = null; Donor thisDonor;
        TextComponent resultMsg; boolean isID = false;

        try { thisID = UUID.fromString(thisSearch); isID = true;
        } catch (IllegalArgumentException ignore) { }

        if (isID) thisDonor = DonationManager.getDonorByUUID(thisID);
        else thisDonor = DonationManager.getDonorByName(thisSearch);

        if (thisDonor == null) {
            resultMsg = new TextComponent(ChatPrint.primary +
                    "Click here to set " + thisSearch + " as a donor!");

            if (isID) thisSearch = Objects.requireNonNull(
                    sender.getServer().getPlayer(thisID)).getName();

            resultMsg.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND, "/setdonator " + thisSearch));

            sender.sendMessage(resultMsg); return true;
        }

        String donorRealIGN;

        try { donorRealIGN = Objects.requireNonNull(sender.getServer()
                    .getPlayer(thisDonor.getUserID())).getName();

        } catch (Exception ignore) {
            sender.sendMessage(ChatPrint.fail + "This player is not online"); return false; }

        sender.sendMessage("");

        sender.sendMessage(ChatPrint.primary + "Real IGN: " + ChatPrint.clear + donorRealIGN);
        sender.sendMessage(ChatPrint.primary + "Custom IGN: " + ChatPrint.clear + thisDonor.getCustomIGN());
        sender.sendMessage(ChatPrint.primary + "UUID: " + ChatPrint.clear + thisDonor.getUserID());
        sender.sendMessage(ChatPrint.primary + "Donation Key: " + ChatPrint.clear + thisDonor.getDonationKey());
        sender.sendMessage(ChatPrint.primary + "First Donation Date: " + ChatPrint.clear + thisDonor.getFirstDonationDate());
        sender.sendMessage(ChatPrint.primary + "Recent Donation Date: " + ChatPrint.clear + thisDonor.getRecentDonationDate());
        sender.sendMessage(ChatPrint.primary + "Total donated on record: " + ChatPrint.clear + "$" + thisDonor.getSumDonated());
        sender.sendMessage(ChatPrint.primary + "Tagline: " + ChatPrint.clear + thisDonor.getTagLine());
        sender.sendMessage(ChatPrint.primary + "MOTD: " + ChatPrint.clear + thisDonor.getMsgOtd());

        return true;
    }
}
