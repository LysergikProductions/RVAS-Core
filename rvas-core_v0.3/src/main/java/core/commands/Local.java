package core.commands;

import core.data.PlayerMeta;
import core.commands.restricted.Admin;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Local implements CommandExecutor {

    static int serverRenderDistance; static {
        serverRenderDistance = Bukkit.getServer().getViewDistance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player sender; String sendName;

        if (commandSender instanceof Player) {
            sender = (Player)commandSender;
            sendName = sender.getName();
        }
        else return false;

        if (args.length < 1) {
            sender.sendMessage("\u00A7cIncorrect syntax. Syntax: /l [message]");
            return false;
        }

        // If either player is muted, refuse message.
        if (PlayerMeta.isMuted(sender)) {
            sender.sendMessage("\u00A7cYou can't send messages.");
            return true;
        }

        Location senderLoc = sender.getLocation();
        senderLoc.setY(0);

        // Get recipients in-range
        ArrayList<Player> receivers = new ArrayList<>();
        for (Player thisPlayer: sender.getServer().getOnlinePlayers()) {
            if (thisPlayer.getWorld() != sender.getWorld()) continue;

            Location thisLoc = thisPlayer.getLocation();
            thisLoc.setY(0);

            if (PlayerMeta.isMuted(thisPlayer) ||
                    (Admin.MsgToggle.contains(thisPlayer.getUniqueId()) &&
                            !sender.isOp())) continue;

            if(PlayerMeta.isIgnoring(sender.getUniqueId(), thisPlayer.getUniqueId())) continue;
            if(PlayerMeta.isIgnoring(thisPlayer.getUniqueId(), sender.getUniqueId())) continue;

            Vector distance = thisLoc.subtract(senderLoc).toVector();
            if (distance.length() < serverRenderDistance * 16) receivers.add(thisPlayer);
        }

        // Concatenate args int message
        final String[] msg = {""};
        final int[] x = {1};

        Arrays.stream(args).forEach(s ->  {
            x[0]++;
            msg[0] += s + " ";
        });
        msg[0] = msg[0].trim();

        // send message to admin spies
        Bukkit.getOnlinePlayers().forEach(thisPlayer -> { if (Admin.Spies.contains(thisPlayer.getUniqueId())) {
            thisPlayer.sendMessage("\u00A73" + sendName + " to local:" + msg[0]);
        }});

        // send the local message
        for (Player receiver: receivers) receiver.sendMessage("\u00A73" + sendName + ": " + msg[0]);
        return true;
    }
}
