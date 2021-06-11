package core.commands;

import core.backend.Config;
import core.frontend.ChatPrint;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Donate implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        if (args.length != 0) { sender.sendMessage(new TextComponent(ChatPrint.fail +
                    "Invalid syntax. Syntax: /donate").toLegacyText()); return false; }

        TextComponent msg1 = new TextComponent(ChatPrint.secondary + "Click here to copy your UUID to your clipboard");
        TextComponent msg2 = new TextComponent(ChatPrint.primary + "Click here to donate with crypto!");

        msg1.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ((Player) sender).getUniqueId().toString()));
        msg2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                "https://commerce.coinbase.com/checkout/f3a218cd-d7f5-4248-bcbc-8c230af05178"));

        msg2.setBold(true);
        sender.sendMessage(new TextComponent(ChatPrint.warn, new TextComponent("Please DM " + Config.getValue("admin") +
                " your IGN and UUID before donating to get your perks!")).toLegacyText());

        // TODO: send cash donation link as well
        sender.sendMessage(msg1);
        sender.sendMessage(msg2);
        sender.sendMessage(new TextComponent(ChatPrint.controls + "UUID copied to clipboard!").toLegacyText());
        return true;
    }
}
