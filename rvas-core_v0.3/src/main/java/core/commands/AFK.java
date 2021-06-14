package core.commands;

import core.frontend.ChatPrint;

import java.util.UUID;
import java.util.ArrayList;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AFK implements CommandExecutor {

    public static ArrayList<UUID> _AFKs = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        String result;
        UUID playerid = player.getUniqueId();

        if (!_AFKs.contains(playerid)) {
            _AFKs.add(playerid);
            result = ChatPrint.controls + "Your whisperers will now see that you are AFK!";

        } else {
            _AFKs.remove(playerid); Message.AFK_warned.remove(playerid);
            result = ChatPrint.succeed + "You are no longer AFK!";
        }
        player.sendMessage(result); return true;
    }
}
