package core.commands.op;

import core.Main;
import core.frontend.ChatPrint;
import core.frontend.GUI.SpeedList;

import java.util.logging.Level;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Speeds implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player)) {
            Main.console.log(Level.INFO, "This command cannot be run from the console"); return false; }

        Player sender = (Player) commandSender;
        if (!sender.isOp()) { sender.sendMessage(ChatPrint.fail + "no"); return false; }

        Admin.doNotDisturb.remove(sender.getUniqueId());

        SpeedList.updateGUI();
        sender.openInventory(SpeedList.speedGUI);
        return true;
    }
}
