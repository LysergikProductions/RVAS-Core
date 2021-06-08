package core.commands.restricted;

import core.backend.ChatPrint;
import core.data.DonationManager;
import core.data.objects.Donor;

import java.util.UUID;
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
            if (args.length != 1) {
                sender.sendMessage(new TextComponent(ChatPrint.fail +
                        "Invalid syntax. Syntax: /donor [name]").toLegacyText());
                return false;

            } else if (!sender.isOp()) {
                sender.sendMessage(new TextComponent(ChatPrint.fail + "yeah no").toLegacyText());
                return false;
            }

            String thisSearch = args[0].trim();
            UUID thisID = null; Donor thisDonor;
            TextComponent resultMsg;
            boolean isID = false;

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
