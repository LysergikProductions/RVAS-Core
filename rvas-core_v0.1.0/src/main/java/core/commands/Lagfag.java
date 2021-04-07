package protocol3.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;

// funny command haha

public class Lagfag implements CommandExecutor {

	HashMap<UUID, Boolean> threadIndicators = new HashMap<UUID, Boolean>();
	HashMap<UUID, Boolean> threadProgression = new HashMap<UUID, Boolean>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't run this."));
			return true;
		}
		if (args.length != 1) {
			sender.spigot().sendMessage(new TextComponent("§cInvalid syntax. Syntax: /lagfag [name]"));
			return true;
		}

		if (sender.isOp()) {
			Player op = (Player) sender;
			switch (args[0]) {
				case "cam":
					Bukkit.getScheduler().runTaskAsynchronously(protocol3.Main.instance, () -> {
						while (true) {

							Player finalOp  = (Player) sender;
							Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
							players.forEach(p -> {
								if (p.isOp())
									return;
								Bukkit.getScheduler().runTask(protocol3.Main.instance, () -> {
									finalOp.setGameMode(GameMode.SPECTATOR);
									finalOp.teleport(p.getLocation());
								});
								finalOp.sendMessage("§6Player: " + p.getName());

								while (!threadProgression.get(finalOp.getUniqueId()) && !threadIndicators.get(finalOp.getUniqueId())) {
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
									}
								}

								if (threadIndicators.get(finalOp.getUniqueId())) {
									threadIndicators.remove(finalOp.getUniqueId());
									threadProgression.remove(finalOp.getUniqueId());
									return;
								}

								if (threadProgression.get(finalOp.getUniqueId())) {
									threadProgression.put(finalOp.getUniqueId(), false);
								}

							});
							if (threadIndicators.get(finalOp.getUniqueId())) {
								threadIndicators.remove(finalOp.getUniqueId());
								threadProgression.remove(finalOp.getUniqueId());
								break;
							}
						}
						return;
					});
					op = (Player) sender;
					threadIndicators.put(op.getUniqueId(), false);
					threadProgression.put(op.getUniqueId(), false);
					return true;
				case "cancel":
					op = (Player) sender;
					if (threadIndicators.containsKey(op.getUniqueId())) {
						threadIndicators.put(op.getUniqueId(), true);
					}
					return true;
				case "next":
					op = (Player) sender;
					if (threadProgression.containsKey(op.getUniqueId())) {
						threadProgression.put(op.getUniqueId(), true);
					}
					return true;
			}
		}

		Player lagfag = Bukkit.getPlayer(args[0]);
		if (lagfag == null) {
			sender.spigot().sendMessage(new TextComponent("§cPlayer is not online."));
			return true;
		}
		PlayerMeta.setLagfag(lagfag, !PlayerMeta.isLagfag(lagfag));
		if (PlayerMeta.isLagfag(lagfag)) {
			Arrays.asList("§6" + lagfag.getName() + " is a lagfag!", "§6IP: " + lagfag.getAddress().toString().split(":")[0].replace("/", ""),
					"§6COORDS: " + Math.round(lagfag.getLocation().getX()) + ", "
					+ Math.round(lagfag.getLocation().getY()) + ", "
					+ Math.round(lagfag.getLocation().getZ())).forEach(s -> Bukkit.getServer().spigot().broadcast(new TextComponent(s)));

			lagfag.getEnderChest().clear();
			lagfag.setBedSpawnLocation(Bukkit.getWorld("world").getSpawnLocation(), true);
			lagfag.setHealth(0);
		}
		return true;
	}

}
