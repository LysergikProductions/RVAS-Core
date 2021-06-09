package core.commands;

import core.backend.ChatPrint;

import core.backend.Config;
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

        TextComponent msg = new TextComponent(ChatPrint.primary + "Click here to donate with crypto!");
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                "https://commerce.coinbase.com/checkout/f3a218cd-d7f5-4248-bcbc-8c230af05178"));

        sender.sendMessage(msg); // TODO: send cash donation link as well
        sender.sendMessage(new TextComponent("WARN Please DM " + Config.getValue("admin") +
                " your IGN and UUID before donating to get your perks!").toLegacyText());
        return true;
    }
}
