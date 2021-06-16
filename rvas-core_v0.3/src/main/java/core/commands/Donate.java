package core.commands;

import core.backend.Config;
import core.data.PlayerMeta;
import core.frontend.ChatPrint;
import core.tasks.Analytics;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Donate implements CommandExecutor {

    final static String default_link = "https://commerce.coinbase.com/checkout/f3a218cd-d7f5-4248-bcbc-8c230af05178";

    static String link1_url = Config.getValue("link.donation.1").trim();
    static String link2_url = Config.getValue("link.donation.2").trim();

    static String link1_id = Config.getValue("id.donation.1").trim();
    static String link2_id = Config.getValue("id.donation.2").trim();

    static {
        if (link1_url.equals("false") || link1_url.isEmpty()) link1_url = default_link;
        if (link2_url.equals("false") || link2_url.isEmpty()) link2_url = default_link;
        if (link1_id.equals("false") || link1_id.isEmpty()) link1_id = "crypto";
        if (link2_id.equals("false") || link2_id.isEmpty()) link2_id = "crypto";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        if (args.length != 0) { sender.sendMessage(
                ChatPrint.fail + "Invalid syntax. Syntax: /donate"); return false; }

        Player player = (Player)sender;
        String id = player.getUniqueId().toString();

        if (!PlayerMeta.isAdmin(player)) Analytics.donate_cmd++;

        TextComponent msg1 = new TextComponent(ChatPrint.primary + "Click here to copy your UUID to your clipboard");
        TextComponent msg2 = new TextComponent(ChatPrint.controls + "Click here to donate with " + link1_id + "!");
        TextComponent msg3 = new TextComponent(ChatPrint.secondary + "Click here to donate with " + link2_id + "!");

        msg1.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id));
        msg2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link1_url));
        msg3.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link2_url));

        msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatPrint.faded + id)));
        msg2.setBold(true); msg2.setUnderlined(true);
        msg3.setBold(true); msg3.setItalic(true); msg3.setUnderlined(true);

        // Print messages
        sender.sendMessage(ChatPrint.warn + "Please DM " + Config.getValue("admin") +
                " your IGN and UUID before donating to get your perks!");

        sender.sendMessage(msg1); sender.sendMessage(msg2);
        if (!link1_url.equals(link2_url)) sender.sendMessage(msg3);

        return true;
    }
}
