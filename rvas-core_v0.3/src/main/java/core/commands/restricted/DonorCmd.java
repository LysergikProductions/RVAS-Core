package core.commands.restricted;

import core.frontend.ChatPrint;
import core.data.DonationManager;
import core.data.objects.Donor;

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

public class DonorCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ((sender instanceof Player)) {
            if (args.length >= 3) {

                if (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("tag") &&
                        !args[1].equalsIgnoreCase("motd") && !args[1].equalsIgnoreCase("set") &&
                        !args[1].equalsIgnoreCase("key") && !args[1].equalsIgnoreCase("ign")) {

                    sender.sendMessage(new TextComponent(ChatPrint.fail +
                            "Invalid syntax. Syntax: /donor [name] [add/tag/ign/motd/key/set]").toLegacyText());
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
                        thisDonor.addToSum(Double.parseDouble(args[2]));
                        thisDonor.setRecentDonationDate();

                        sender.sendMessage(new TextComponent(
                                ChatPrint.primary + "Successfully added $" + args[2] +
                                " to that donor for a total of $" + thisDonor.getSumDonated()).toLegacyText());
                        return true;

                    case "set":
                        thisDonor.setSumDonated(Double.parseDouble(args[2]));

                        sender.sendMessage(new TextComponent(
                                ChatPrint.primary + "Successfully set sum donated to $" +
                                        thisDonor.getSumDonated()).toLegacyText());
                        return true;

                    case "key":
                        String newKey = args[2];

                        if (DonationManager.DonorCodes.contains(args[0]) && !DonationManager.UsedDonorCodes.contains(args[0])) {
                            thisDonor.setDonationKey(newKey);
                            DonationManager.UsedDonorCodes.add(args[0]);

                        } else {
                            sender.sendMessage(new TextComponent(ChatPrint.fail + "Invalid key.").toLegacyText());
                            return false;
                        }

                        sender.sendMessage(new TextComponent(
                                ChatPrint.primary + "Successfully set donation key to " +
                                        thisDonor.getDonationKey()).toLegacyText());
                        return true;

                    case "tag":
                        Objects.requireNonNull(DonationManager
                                .getDonorByName(args[0])).setTagLine(msg[0]);

                        sender.sendMessage(new TextComponent(ChatPrint.primary +
                                "Successfully set donor tagline to:").toLegacyText());
                        sender.sendMessage(new TextComponent(ChatPrint.secondary + msg[0]).toLegacyText());
                        return true;

                    case "motd":
                        Objects.requireNonNull(DonationManager
                                .getDonorByName(args[0])).setMsgOtd(msg[0]);

                        sender.sendMessage(new TextComponent(ChatPrint.primary +
                                "Successfully set donor MOTD to:").toLegacyText());
                        sender.sendMessage(new TextComponent(ChatPrint.secondary + msg[0]).toLegacyText());
                        return true;

                    case "ign":
                        if (args.length != 3) {
                            sender.sendMessage(new TextComponent(ChatPrint.fail +
                                    "Invalid syntax. Syntax: /donor [name] ign [one_word_ign]").toLegacyText());
                            return false;
                        }

                        Objects.requireNonNull(DonationManager
                                .getDonorByName(args[0])).setCustomIGN(msg[0]);

                        sender.sendMessage(new TextComponent(ChatPrint.primary +
                                "Successfully set custom IGN to:").toLegacyText());
                        sender.sendMessage(new TextComponent(ChatPrint.secondary + msg[0]).toLegacyText());
                        return true;

                    default: return false;
                }

            } else if (args.length != 1) {
                sender.sendMessage(new TextComponent(ChatPrint.fail +
                        "Invalid syntax. Syntax: /donor [name]").toLegacyText());
                return false;

            } else if (!sender.isOp()) {
                sender.sendMessage(new TextComponent(ChatPrint.fail + "yeah no").toLegacyText());
                return false;
            }

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

            String donorRealIGN = Objects.requireNonNull(sender.getServer()
                    .getPlayer(thisDonor.getUserID())).getName();

            sender.sendMessage("");

            sender.sendMessage(new TextComponent(ChatPrint.primary + "Current Real IGN: " +
                    ChatPrint.clear + donorRealIGN).toLegacyText());

            sender.sendMessage(new TextComponent(ChatPrint.primary + "UUID: " +
                    ChatPrint.clear + thisDonor.getUserID()).toLegacyText());

            sender.sendMessage(new TextComponent(ChatPrint.primary + "Custom IGN: " +
                    ChatPrint.clear + thisDonor.getCustomIGN()).toLegacyText());

            sender.sendMessage(new TextComponent(ChatPrint.primary + "Donation Key: " +
                    ChatPrint.clear + thisDonor.getDonationKey()).toLegacyText());

            sender.sendMessage(new TextComponent(ChatPrint.primary + "First Donation Date: " +
                    ChatPrint.clear + thisDonor.getFirstDonationDate()).toLegacyText());

            sender.sendMessage(new TextComponent(ChatPrint.primary + "Recent Donation Date: " +
                    ChatPrint.clear + thisDonor.getRecentDonationDate()).toLegacyText());

            sender.sendMessage(new TextComponent(ChatPrint.primary + "Total donated on record: " +
                    ChatPrint.clear + "$" + thisDonor.getSumDonated()).toLegacyText());

            sender.sendMessage(new TextComponent(ChatPrint.primary + "Tagline: " +
                    ChatPrint.clear + thisDonor.getTagLine()).toLegacyText());

            sender.sendMessage(new TextComponent(ChatPrint.primary + "MOTD: " +
                    ChatPrint.clear + thisDonor.getMsgOtd()).toLegacyText());


        } else { System.out.println("TODO: sending donor info to console"); }
        return true;
    }
}
