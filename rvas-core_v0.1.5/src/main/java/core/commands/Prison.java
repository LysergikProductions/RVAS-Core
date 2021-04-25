package core.commands;

import core.backend.PlayerMeta;
import core.events.SpawnController;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

// Toggle players being prisoners
public class Prison implements CommandExecutor {

	//HashMap<UUID, Boolean> threadIndicators = new HashMap<UUID, Boolean>();
	//HashMap<UUID, Boolean> threadProgression = new HashMap<UUID, Boolean>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		
		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't run this."));
			return true;
		}
		if (args.length != 1) {
			sender.spigot().sendMessage(new TextComponent("§cInvalid syntax. Syntax: /prison [name]"));
			return true;
		}

		/*if (sender.isOp()) {
			Player op = (Player) sender;
			switch (args[0]) {
				case "cam":
					Bukkit.getScheduler().runTaskAsynchronously(core.Main.instance, () -> {
						while (true) {

							Player finalOp  = (Player) sender;
							Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
							players.forEach(p -> {
								if (p.isOp())
									return;
								Bukkit.getScheduler().runTask(core.Main.instance, () -> {
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
		}*/

		Player thisPlayer = Bukkit.getPlayer(args[0]);
		
		if (thisPlayer == null) {
			sender.spigot().sendMessage(new TextComponent("§cPlayer is not online."));
			return true;
			
		} else if (PlayerMeta.isAdmin(thisPlayer) || thisPlayer.isOp()) return false;
		
		PlayerMeta.togglePrisoner(thisPlayer);
		
		if (PlayerMeta.isPrisoner(thisPlayer)) {
			
			Arrays.asList("§6" + thisPlayer.getName() + " was caught lagging the server!", "§6IP: " + thisPlayer.getAddress().toString().split(":")[0].replace("/", ""),
					"§6COORDS: " + Math.round(thisPlayer.getLocation().getX()) + ", "
					+ Math.round(thisPlayer.getLocation().getY()) + ", "
					+ Math.round(thisPlayer.getLocation().getZ())).forEach(s -> Bukkit.getServer().spigot().broadcast(new TextComponent(s)));

			thisPlayer.getEnderChest().clear();
			thisPlayer.setHealth(0);
		}
		return true;
	}
}
