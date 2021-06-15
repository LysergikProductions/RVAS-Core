package core.data;

import core.backend.anno.Critical;
import core.backend.Config;
import core.commands.Ignore;
import core.events.ChatListener;
import core.data.objects.SettingsContainer;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@Critical
@SuppressWarnings("SpellCheckingInspection")
public class PlayerMeta {

	public static List<String> _ipMutes = new ArrayList<>();
	public static List<UUID> _permanentMutes = new ArrayList<>();
	public static HashMap<UUID, Double> _temporaryMutes = new HashMap<>();

	public static HashMap<UUID, Double> Playtimes = new HashMap<>();
	public static Map <UUID, SettingsContainer> sPlayerSettings = new HashMap<>();

	public static boolean MuteAll = false;

	// --- MUTES --- \\
	public static boolean isMuted(Player p) {
		
		if(_temporaryMutes.containsKey(p.getUniqueId())) { return true; }		
		if(_permanentMutes.contains(p.getUniqueId())) { return true; }
		
		if(_ipMutes.contains(getIp(p))) {
			if(!_permanentMutes.contains(p.getUniqueId())) setMuteType(p, MuteType.PERMANENT);
			return true; 
		}
		return false;
	}

	public static boolean isIgnoring(UUID ignorer, UUID ignored) {
		if (Ignore.Ignores.containsKey(ignorer)) {
			return Ignore.Ignores.get(ignorer).contains(ignored); }

		return false;
	}

	@SuppressWarnings("deprecation")
	public static void setMuteType(Player p, MuteType type)
	{
		UUID uuid = p.getUniqueId();
		String muteType = "";
		
		if (type.equals(MuteType.NONE)) {
			
			muteType = "un";
			_temporaryMutes.remove(uuid);
			
			if (_permanentMutes.contains(uuid)) {
				_permanentMutes.remove(uuid);
				saveMuted();
			}
			
			if(_ipMutes.contains(getIp(p))) {
				_ipMutes.remove(getIp(p));
				saveMuted();
			}
			ChatListener.violationLevels.remove(uuid);
			
		} else if (type.equals(MuteType.TEMPORARY)) {
			
			muteType = "temporarily ";
			_permanentMutes.remove(uuid);
			
			if (!_temporaryMutes.containsKey(uuid))
				_temporaryMutes.put(uuid, 0.0);
			
		} else if (type.equals(MuteType.PERMANENT)) {
			
			muteType = "permanently ";
			_temporaryMutes.remove(uuid);

			if (!_permanentMutes.contains(uuid)) _permanentMutes.add(uuid);
			saveMuted();
			
		} else if (type.equals(MuteType.IP)) {
			
			muteType = "permanently ";
			
			setMuteType(p, MuteType.PERMANENT);
			_ipMutes.add(getIp(p));
		}
		p.spigot().sendMessage(new TextComponent("\u00A77\u00A7oYou are now " + muteType + "muted."));
	}

	public static void tickTempMutes(double msToAdd) {
		
		_temporaryMutes.keySet().forEach(u -> {
			double oldValue = _temporaryMutes.get(u);
			_temporaryMutes.put(u, oldValue + (msToAdd / 1000));
			if (oldValue + (msToAdd / 1000) >= 3600) _temporaryMutes.remove(u);
		});
	}

	// --- SAVE/LOAD MUTED --- \\
	public static void loadMuted() throws IOException {
		
		List<String> lines = Files.readAllLines(Paths.get("plugins/core/muted.db"));
		
		for (String line : lines) {
			try { _permanentMutes.add(UUID.fromString(line));
			} catch(IllegalArgumentException e) { _ipMutes.add(line); }
		}
	}

	public static void saveMuted() {
		
		try {
			List<String> lines = new ArrayList<>();

			for(UUID key : _permanentMutes) lines.add(key.toString());
			lines.addAll(_ipMutes);
			
			Files.write(Paths.get("plugins/core/muted.db"), String.join("\n", lines).getBytes());
		}
		catch (IOException e) {
			System.out.println("[core.backend.playermeta] Failed to save mutes.");
		}
	}

	// --- PLAYTIME --- \\
	public static void tickPlaytime(Player p, double msToAdd) {
		// tick and store playtime in seconds
		if (Playtimes.containsKey(p.getUniqueId())) {

			double value = Playtimes.get(p.getUniqueId());
			value += msToAdd / 1000;
			Playtimes.replace(p.getUniqueId(), value);

		} else Playtimes.put(p.getUniqueId(), msToAdd / 1000);
	}

	public static double getPlaytime(OfflinePlayer p) {
		return (Playtimes.containsKey(p.getUniqueId())) ? Playtimes.get(p.getUniqueId()) : 0;
	}

	public static int getRank(OfflinePlayer p) {
		String admin = Config.getValue("admin");
		if (getPlaytime(p) == 0) return 0;


		int out = Playtimes.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).map(Map.Entry::getKey)
			.collect(Collectors.toList()).lastIndexOf(p.getUniqueId()) + 1;

		if (!admin.equalsIgnoreCase("myAccountInGameName") &&
				!admin.equals("")) return out-1;
		else return out;
	}

	public static HashMap<UUID, Double> getTopFifteenPlayers() {
		int limit = 15;
		if (!Config.getValue("admin").equals("")) limit = 16;

		HashMap<UUID, Double> out;
		out = Playtimes.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(limit)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

		try { out.remove(UUID.fromString(Config.getValue("adminid")));
		} catch (Exception ignore) {}
		return out;
	}

	public static void writePlaytime() throws IOException {
		List<String> list = new ArrayList<>();

		Playtimes.keySet().forEach(user -> list.add(user.toString() + ":" + Math.rint(Playtimes.get(user))));
		Files.write(Paths.get("plugins/core/playtime.db"), String.join("\n", list).getBytes());
	}

	// --- OTHER -- \\
	public enum MuteType {
		TEMPORARY, PERMANENT, IP, NONE
	}

	public static boolean isOp(CommandSender sender) {
		return (sender instanceof Player) ? sender.isOp() : sender instanceof ConsoleCommandSender;
	}
	
	public static boolean isAdmin(Player target) {
		
		String target_name = target.getName();
		UUID target_id = target.getUniqueId();
		String admin_name = Config.getValue("admin");
		UUID admin_id;
		
		try { admin_id = UUID.fromString(Config.getValue("adminid"));
		} catch (Exception e) { return false; }

		return admin_name.equals(target_name) && admin_id.equals(target_id);
	}
	
	public static String getIp(Player p) {
		return Objects.requireNonNull(p.getAddress()).toString().split(":")[0].replace("/", "");
	}

	@Deprecated
	public static MuteType getMuteType(Player p){
		if (isMuted(p)) {
			if (_temporaryMutes.containsKey(p.getUniqueId())) {

				return MuteType.TEMPORARY;

			} else if(_permanentMutes.contains(p.getUniqueId())) {

				return MuteType.PERMANENT;

			} else if(_ipMutes.contains(getIp(p))) {

				return MuteType.IP;
			}
		} return MuteType.NONE;
	}
}
