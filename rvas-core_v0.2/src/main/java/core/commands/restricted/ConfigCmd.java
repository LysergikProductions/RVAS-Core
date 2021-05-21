package core.commands.restricted;

import core.backend.Config;
import core.backend.utils.Do;

import java.io.IOException;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ConfigCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("You can't use this!");
            return false;

        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            try {
                Config.load();
                sender.sendMessage("\u00A7aSuccessfully reloaded.");

            } catch (IOException e) {
                sender.sendMessage("\u00A74Failed to reload.");
                Do.restart();
            }
            return true;

        } else if (args.length != 2) {
            player.sendMessage("Correct syntax: /fig [key] [value]");
            return false;
        }

        String thisKey = args[0]; String thisValue = args[1];

        if (!Config.isRealConfig(thisKey)) {
            player.sendMessage("This is not a recognized config key!");
            return false;
        }

        Config.modifyConfig(thisKey, thisValue);
        player.sendMessage(thisKey + " is now set to " + Config.getValue(thisKey));
        return true;
    }
}
