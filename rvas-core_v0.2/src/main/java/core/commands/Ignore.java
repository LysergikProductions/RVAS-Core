package core.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class Ignore implements CommandExecutor {

    public static HashMap<UUID, List<UUID>> Ignores = new HashMap<>();
    private Random r = new Random();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player)sender;
            UUID playerID = player.getUniqueId();

            if (args.length != 1) {
                player.sendMessage(new TextComponent("§cIncorrect syntax. Syntax: /ignore [player]"));
                return true;
            }

            Player toIgnore = Bukkit.getServer().getPlayer(args[0]);

            if (toIgnore == null) {
                player.sendMessage(new TextComponent("§cPlayer is not online."));
                return true;
            }

            UUID toIgnoreID = toIgnore.getUniqueId();

            if (toIgnore.isOp()) {
                player.sendMessage(new TextComponent("§cYou can't ignore this person."));
                return true;
            }

            if (Ignores.containsKey(playerID)) {
                List<UUID> existing = Ignores.get(playerID);

                if(existing.contains(toIgnoreID)) {

                    existing.remove(toIgnoreID);
                    player.sendMessage(new TextComponent("§6No longer ignoring " + toIgnore.getName() +"."));

                } else {
                    existing.add(toIgnoreID);
                    player.sendMessage(new TextComponent("§6Now ignoring "+ toIgnore.getName() +
                            ". This will persist until the server restarts."));

                    int rnd = r.nextInt(10);
                    if(rnd == 5) {
                        player.sendMessage(new TextComponent("§6§oTip: You can vote to mute people server-wide. Try §n/vm " +
                                toIgnore.getName() +"§r§6§o."));
                    }
                }
                Ignores.put(playerID, existing);
                return true;

            } else {
                List<UUID> ignores = new ArrayList<>();
                ignores.add(toIgnoreID);
                Ignores.put(playerID, ignores);
                player.sendMessage(new TextComponent("§6Now ignoring "+ toIgnore.getName() +
                        ". This will persist until the server restarts."));

                int rnd = r.nextInt(10);
                if (rnd == 5) {
                    player.sendMessage(new TextComponent("§6§oTip: You can vote to mute people server-wide. Try §n/vm "+
                            toIgnore.getName() +"§r§6§o."));
                }
                return true;
            }
        } else {
            sender.sendMessage(new TextComponent("§cConsole can't run this command."));
            return true;
        }
    }
}
