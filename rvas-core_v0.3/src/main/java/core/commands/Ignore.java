package core.commands;

import core.frontend.ChatPrint;
import core.data.PlayerMeta;
import core.tasks.Analytics;

import java.util.List;
import java.util.UUID;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Ignore implements CommandExecutor {

    public static HashMap<UUID, List<UUID>> Ignores = new HashMap<>();
    private final Random r = new Random();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player)sender;
        UUID playerID = player.getUniqueId();

        if (!PlayerMeta.isAdmin(player)) Analytics.ignore_cmd++;

        if (args.length != 1) {
            player.sendMessage(ChatPrint.fail + "Incorrect syntax. Syntax: /ignore [player]");
            return true;
        }

        Player toIgnore = Bukkit.getServer().getPlayer(args[0]);

        if (toIgnore == null) {
            player.sendMessage(ChatPrint.fail + "Player is not online");
            return true;
        }

        UUID toIgnoreID = toIgnore.getUniqueId();

        if (toIgnore.isOp() || PlayerMeta.isAdmin(toIgnore)) {
            player.sendMessage(ChatPrint.fail + "You can't ignore this person");
            return true;
        }

        if (Ignores.containsKey(playerID)) {
            List<UUID> existing = Ignores.get(playerID);

            if(existing.contains(toIgnoreID)) {

                existing.remove(toIgnoreID);
                player.sendMessage("\u00A76No longer ignoring " + toIgnore.getName() +".");

            } else {
                existing.add(toIgnoreID);
                player.sendMessage("\u00A76Now ignoring "+ toIgnore.getName() +
                        ". This will persist until the server restarts.");

                int rnd = r.nextInt(10);
                if(rnd == 5) {
                    player.sendMessage("\u00A76\u00A7oTip: You can vote to mute people server-wide. Try \u00A7n/vm " +
                            toIgnore.getName() +"\u00A7r\u00A76\u00A7o.");
                }
            }
            Ignores.put(playerID, existing);
            return true;

        } else {
            List<UUID> ignores = new ArrayList<>();
            ignores.add(toIgnoreID);
            Ignores.put(playerID, ignores);
            player.sendMessage("\u00A76Now ignoring "+ toIgnore.getName() +
                    ". This will persist until the server restarts.");

            int rnd = r.nextInt(10);
            if (rnd == 5) {
                player.sendMessage("\u00A76\u00A7oTip: You can vote to mute people server-wide. Try \u00A7n/vm "+
                        toIgnore.getName() +"\u00A7r\u00A76\u00A7o.");
            }
            return true;
        }
    }
}
