package core.commands;

import core.frontend.GUI.SL;
import core.frontend.ChatPrint;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SLCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (SL.isSlViewer(player)) { SL.rmSlViewer(player); player.sendMessage(ChatPrint.primary + "Disabled SL-Viewer"); }
        else { SL.addSlViewer(player); player.sendMessage(ChatPrint.primary + "Enabled SL-Viewer"); }

        return true;
    }
}
