package core.backend;

import core.events.Chat;
import core.backend.Config;

import core.objects.PVPstats;
import core.objects.PlayerSettings;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class PlayerMeta {

	public static List<String> _ipMutes = new ArrayList<String>();
	public static List<UUID> _permanentMutes = new ArrayList<UUID>();
	public static HashMap<UUID, Double> _temporaryMutes = new HashMap<UUID, Double>();
	
	public static List<UUID> _donatorList = new ArrayList<UUID>();
	public static List<String> DonorCodes = new ArrayList<String>();
	public static List<String> UsedDonorCodes = new ArrayList<String>();
	
	public static HashMap<UUID, String> _lagfagList = new HashMap<UUID, String>();
	public static HashMap<UUID, Double> Playtimes = new HashMap<UUID, Double>();
	public static Map <UUID, PlayerSettings> sPlayerSettings = new HashMap<UUID, PlayerSettings>();

	public static boolean MuteAll = false;

	// GET/SET DONATOR STATUS \\
	public static boolean isDonator(Player p)
	{
		return _donatorList.contains(p.getUniqueId());
	}

	public static void setDonator(Player p, boolean status)
	{
		if (status)
		{
			if (!_donatorList.contains(p.getUniqueId())) {
				_donatorList.add(p.getUniqueId());
			}
		} else {
			_donatorList.remove(p.getUniqueId());
		}

		try {
			saveDonators();
		} catch (IOException e) {
			System.out.println("[core.backend.playermeta] Failed to save donators.");
		}
	}

	// --- MUTES --- \\
	public static boolean isMuted(Player p) {
		
		if(_temporaryMutes.containsKey(p.getUniqueId())) { return true; }		
		if(_permanentMutes.contains(p.getUniqueId())) { return true; }
		
		if(_ipMutes.contains(getIp(p))) {
			if(!_permanentMutes.contains(p.getUniqueId())) {
				
				setMuteType(p, MuteType.PERMANENT);
			}
			return true; 
		}
		return false;
	}

	public static MuteType getMuteType(Player p){
		if (isMuted(p)) {
			if (_temporaryMutes.containsKey(p.getUniqueId())) {
				
				return MuteType.TEMPORARY;
				
			} else if(_permanentMutes.contains(p.getUniqueId())) {
				
				return MuteType.PERMANENT;
				
			} else if(_ipMutes.contains(getIp(p))) {
				
				return MuteType.IP;
			}
		}
		return MuteType.NONE;
	}

	public static void setMuteType(Player p, MuteType type)
	{
		UUID uuid = p.getUniqueId();
		String muteType = "";
		
		if (type.equals(MuteType.NONE)) {
			
			muteType = "un";
			if (_temporaryMutes.containsKey(uuid)) {
				_temporaryMutes.remove(uuid);
			}
			
			if (_permanentMutes.contains(uuid)) {
				_permanentMutes.remove(uuid);
				saveMuted();
			}
			
			if(_ipMutes.contains(getIp(p))) {
				_ipMutes.remove(getIp(p));
				saveMuted();
			}
			Chat.violationLevels.remove(uuid);
			
		} else if (type.equals(MuteType.TEMPORARY)) {
			
			muteType = "temporarily ";
			_permanentMutes.remove(uuid);
			
			if (!_temporaryMutes.containsKey(uuid))
				_temporaryMutes.put(uuid, 0.0);
			
		} else if (type.equals(MuteType.PERMANENT)) {
			
			muteType = "permanently ";
			
			if (!_permanentMutes.contains(uuid)) _permanentMutes.add(uuid);
			if (_temporaryMutes.containsKey(uuid)) _temporaryMutes.remove(uuid);
			saveMuted();
			
		} else if (type.equals(MuteType.IP)) {
			
			muteType = "permanently ";
			
			setMuteType(p, MuteType.PERMANENT);
			_ipMutes.add(getIp(p));
		}
		p.spigot().sendMessage(new TextComponent("§7§oYou are now " + muteType + "muted."));
	}

	public static void tickTempMutes(double msToAdd) {
		
		_temporaryMutes.keySet().forEach(u -> {
			double oldValue = _temporaryMutes.get(u);
			_temporaryMutes.put(u, oldValue + (msToAdd / 1000));
			if (oldValue + (msToAdd / 1000) >= 3600) _temporaryMutes.remove(u);
		});
	}

	// -- LAG PRISONERS -- \\
	public static void togglePrisoner(Player p) {

		if (!_lagfagList.containsKey(p.getUniqueId())) {
			_lagfagList.put(p.getUniqueId(), p.getAddress().toString().split(":")[0]);
		} else {
			try {
				_lagfagList.remove(p.getUniqueId());
			} catch (Exception e) {
				System.out.println(
						"[core.backend.playermeta] WARNING: Tried and failed to remove this uuid from prisoner list..");
				System.out.println(e);
			}
		}	
		
		try {
			saveLagfags();
		} catch (IOException e) {
			System.out.println("[core.backend.playermeta] Failed to save lag priosners.");
		}
	}

	public static boolean isPrisoner(Player p) {
		
		if (
			_lagfagList.containsKey(p.getUniqueId()) ||
			_lagfagList.containsValue(p.getAddress().toString().split(":")[0])
		){			
			return true;
			
		} else return false;
	}

	public static void saveLagfags() throws IOException {
		List<String> list = _lagfagList.keySet().stream().map(u -> u.toString() + ":" + _lagfagList.get(u)).collect(Collectors.toList());
		Files.write(Paths.get("plugins/core/prisoners.db"), String.join("\n", list).getBytes());
	}

	public static void loadLagfags() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("plugins/core/prisoners.db"));
		lines.forEach(val -> _lagfagList.put(UUID.fromString(val.split(":")[0]), val.split(":")[1]));
	}

	// --- SAVE/LOAD DONATORS --- \\
	public static void loadDonators() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("plugins/core/donator.db"));
		lines.forEach(val -> _donatorList.add(UUID.fromString(val)));
	}

	public static void saveDonators() throws IOException {
		List<String> list = _donatorList.stream().map(UUID::toString).collect(Collectors.toList());
		Files.write(Paths.get("plugins/core/donator.db"), String.join("\n", list).getBytes());
		Files.write(Paths.get("plugins/core/codes/used.db"), String.join("\n", UsedDonorCodes).getBytes());
	}

	// --- SAVE/LOAD MUTED --- \\
	public static void loadMuted() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("plugins/protocol3/muted.db"));
		for(String line : lines) {
			try {
				_permanentMutes.add(UUID.fromString(line));
			}
			catch(IllegalArgumentException e) {
				_ipMutes.add(line);
			}
		}
	}

	public static void saveMuted() {
		try {
		List<String> lines = new ArrayList<String>();
		for(UUID key : _permanentMutes) {
			lines.add(key.toString());
		}
		for(String ip : _ipMutes) {
			lines.add(ip);
		}
		Files.write(Paths.get("plugins/protocol3/muted.db"), String.join("\n", lines).getBytes());
		}
		catch (IOException e)
		{
			System.out.println("[protocol3] Failed to save mutes.");
		}
	}

	// --- PLAYTIME --- \\
	public static void tickPlaytime(Player p, double msToAdd) {
		
		if (Playtimes.containsKey(p.getUniqueId())) {
			double value = Playtimes.get(p.getUniqueId());
			value += msToAdd / 1000;
			Playtimes.put(p.getUniqueId(), value);
		} else {
			Playtimes.put(p.getUniqueId(), msToAdd / 1000);
		}
	}

	public static double getPlaytime(OfflinePlayer p) {
		return (Playtimes.containsKey(p.getUniqueId())) ? Playtimes.get(p.getUniqueId()) : 0;
	}

	public static int getRank(OfflinePlayer p) {
		if (getPlaytime(p) == 0) return 0;
		return Playtimes.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).map(Map.Entry::getKey).collect(Collectors.toList()).lastIndexOf(p.getUniqueId()) + 1;
	}

	public static HashMap<UUID, Double> getTopFivePlayers() {
		return Playtimes.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(5).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
				LinkedHashMap::new));
	}

	private static HashMap<UUID, Double> sortByValue(HashMap<UUID, Double> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<UUID, Double>> list = new LinkedList(hm.entrySet());

		// Sort the list
		Collections.sort(list, (o1, o2) -> -(o1.getValue()).compareTo(o2.getValue()));

		// put data from sorted list to hashmap
		HashMap<UUID, Double> temp = new LinkedHashMap();
		list.forEach(aa -> temp.put(aa.getKey(), aa.getValue()));

		return temp;
	}

	public static void writePlaytime() throws IOException {
		List<String> list = new ArrayList();

		Playtimes.keySet().forEach(user -> list.add(user.toString() + ":" + Math.rint(Playtimes.get(user))));

		Files.write(Paths.get("plugins/core/playtime.db"), String.join("\n", list).getBytes());
	}
	
	public static PlayerSettings getNewSettings(OfflinePlayer p) {
		PlayerSettings out = new PlayerSettings(p.getUniqueId(), true, true, true, true, true, true);
		return out;
	}
	
	public static PlayerSettings getSettings(OfflinePlayer p) {
		
		PlayerSettings settings = sPlayerSettings.get(p.getUniqueId());
		
		if (settings != null && sPlayerSettings.containsKey(p.getUniqueId())) {
			
			return settings;
			
		} else {
			
			PlayerSettings new_settings = getNewSettings(p);
			return new_settings;
		}
	}
	
	public static void writePlayerSettings() throws IOException {
		
		BufferedWriter w = new BufferedWriter(new FileWriter("plugins/core/player_settings.txt"));
		
		for (PlayerSettings object: sPlayerSettings.values()) {
			try {
				w.write(object.toString() + "\n");
				w.flush();
				
			  } catch (IOException e) {
				  throw new UncheckedIOException(e);
			  }
		};
		w.close();
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
		
		try {
			admin_id = UUID.fromString(Config.getValue("adminid"));
		} catch (Exception e) {return false;}
		
		if (admin_name.equals(target_name) && admin_id.equals(target_id)) {
			return true;
		} else return false;
	}
	
	public static String getIp(Player p) {
		return p.getAddress().toString().split(":")[0].replace("/", "");
	}
}
