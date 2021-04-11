package core.backend;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import core.events.Chat;
import core.backend.Config;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import core.backend.PVPstats;

public class PlayerMeta {

	public static List<UUID> _donatorList = new ArrayList<UUID>();
	
	public static HashMap<UUID, String> _permanentMutes = new HashMap<UUID, String>();
	public static HashMap<UUID, Double> _temporaryMutes = new HashMap<UUID, Double>();

	public static HashMap<UUID, Double> Playtimes = new HashMap<UUID, Double>();
	public static Map <UUID, PVPstats> sKillStats = new HashMap<>();

	public static HashMap<UUID, String> _lagfagList = new HashMap<UUID, String>();

	public static List<String> DonorCodes = new ArrayList<String>();
	public static List<String> UsedDonorCodes = new ArrayList<String>();

	public static boolean MuteAll = false;

	// --- GET/SET DONATOR STATUS --- //

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

	// --- MUTES --- //

	public static boolean isMuted(Player p)
	{
		String ip = p.getAddress().toString().split(":")[0].replace("/", "");
		
		boolean muted = (_temporaryMutes.containsKey(p.getUniqueId()) || _permanentMutes.containsKey(p.getUniqueId()) || _permanentMutes.containsValue(ip));
		
		// make sure this combo of uuid and ip are in db
		if(muted && !(_permanentMutes.containsKey(p.getUniqueId()) && _permanentMutes.containsValue(ip))) {
			_permanentMutes.put(p.getUniqueId(), ip);
			try {
				saveMuted();
			} catch (IOException e) {
				System.out.println("[core.backend.playermeta] Failed to save mutes.");
			}
		}
		
		
		return muted;
	}

	public static MuteType getMuteType(Player p)
	{
		if (isMuted(p))
		{
			String ip = p.getAddress().toString().split(":")[0].replace("/", "");
			if (_temporaryMutes.containsKey(p.getUniqueId()) && !_permanentMutes.containsKey(p.getUniqueId()))
			{
				return MuteType.TEMPORARY;
			} else
			{
				return MuteType.PERMANENT;
			}
		} else
		{
			return MuteType.NONE;
		}
	}

	public static void setMuteType(Player p, MuteType type)
	{
		UUID uuid = p.getUniqueId();
		String muteType = "";
		if (type.equals(MuteType.NONE))
		{
			muteType = "un";
			if (_temporaryMutes.containsKey(uuid))
				_temporaryMutes.remove(uuid);
			if (_permanentMutes.containsKey(uuid))
			{
				String ip = _permanentMutes.get(uuid);
				HashMap<UUID, String> modified = new HashMap<UUID, String>();
				modified = _permanentMutes;
				for(UUID val : modified.keySet()) {
					if(_permanentMutes.get(val).equals(ip)) {
						_permanentMutes.remove(val);
					}
					if(val.equals(uuid)) {
						_permanentMutes.remove(val);
					}
				}
				try
				{
					saveMuted();
				} catch (IOException e)
				{
					System.out.println("[protocol3] Failed to save mutes.");
				}
			}
			Chat.violationLevels.remove(uuid);
		} else if (type.equals(MuteType.TEMPORARY)) {
			muteType = "temporarily ";
			_permanentMutes.remove(uuid);
			if (!_temporaryMutes.containsKey(uuid))
				_temporaryMutes.put(uuid, 0.0);
		} else if (type.equals(MuteType.PERMANENT)) {
			muteType = "permanently ";
			String ip = p.getAddress().toString().split(":")[0].replace("/", "");
			if (!_permanentMutes.containsKey(uuid)) _permanentMutes.put(uuid, ip);
			if (_temporaryMutes.containsKey(uuid)) _temporaryMutes.remove(uuid);
			try {
				saveMuted();
			} catch (IOException e) {
				System.out.println("[core.backend.playermeta] Failed to save mutes.");
			}
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

	// -- LAGFAGS -- //
	public static void setLagfag(Player p, boolean status) {
		if (status) {
			if (!_lagfagList.containsKey(p.getUniqueId())) {
				_lagfagList.put(p.getUniqueId(), p.getAddress().toString().split(":")[0]);
			}
		} else {
			_lagfagList.remove(p.getUniqueId());
		}
		try {
			saveLagfags();
		} catch (IOException e) {
			System.out.println("[core.backend.playermeta] Failed to save laggers.");
		}
	}

	public static boolean isLagfag(Player p) {
		return _lagfagList.containsKey(p.getUniqueId())
				|| _lagfagList.containsValue(p.getAddress().toString().split(":")[0]);
	}

	public static void saveLagfags() throws IOException {
		List<String> list = _lagfagList.keySet().stream().map(u -> u.toString() + ":" + _lagfagList.get(u)).collect(Collectors.toList());
		Files.write(Paths.get("plugins/core/lagfag.db"), String.join("\n", list).getBytes());
	}

	public static void loadLagfags() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("plugins/core/lagfag.db"));
		lines.forEach(val -> _lagfagList.put(UUID.fromString(val.split(":")[0]), val.split(":")[1]));
	}

	// --- SAVE/LOAD DONATORS --- //

	public static void loadDonators() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("plugins/core/donator.db"));
		lines.forEach(val -> _donatorList.add(UUID.fromString(val)));
	}

	public static void saveDonators() throws IOException {
		List<String> list = _donatorList.stream().map(UUID::toString).collect(Collectors.toList());
		Files.write(Paths.get("plugins/core/donator.db"), String.join("\n", list).getBytes());
		Files.write(Paths.get("plugins/core/codes/used.db"), String.join("\n", UsedDonorCodes).getBytes());
	}

	// --- SAVE/LOAD MUTED --- //

	public static void loadMuted() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("plugins/core/muted.db"));
		lines.forEach(val -> _permanentMutes.put(UUID.fromString(val.split(":")[0]), val.split(":")[1]));
	}

	public static void saveMuted() throws IOException {
		List<String> lines = new ArrayList<String>();
		for(UUID key : _permanentMutes.keySet()) {
			lines.add(key.toString() + ":" + _permanentMutes.get(key));
		}
		Files.write(Paths.get("plugins/core/muted.db"), String.join("\n", lines).getBytes());
	}

	// --- PLAYTIME --- //
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

	// --- PVP -- //
	
	public static void incKillTotal(Player p, int inc) {
		if (sKillStats.containsKey(p.getUniqueId())) {
			
			if (Config.getValue("debug").equals("true")) {
				
				PVPstats id = sKillStats.get(p.getUniqueId());
				Bukkit.spigot().broadcast(new TextComponent(p.getName() + "'s Kills: " + id.killTotal));
				
				id.killTotal += inc;
				Bukkit.spigot().broadcast(new TextComponent(p.getName() + "'s Kills: " + id.killTotal));
			} else {
				
				PVPstats id = sKillStats.get(p.getUniqueId());
				id.killTotal += inc;
			}
		} else {
			if (Config.getValue("debug").equals("true")) {
				
				PVPstats id = new PVPstats(p.getUniqueId(), 1);
				Bukkit.spigot().broadcast(new TextComponent(p.getName() + "'s Kills: " + id.killTotal));
				
				sKillStats.put(p.getUniqueId(), id);
				Bukkit.spigot().broadcast(new TextComponent(p.getName() + "'s Kills: " + id.killTotal));
			} else {
				
				PVPstats id = new PVPstats(p.getUniqueId(), 1);
				sKillStats.put(p.getUniqueId(), id);
			}
		}
	}
	
	public static PVPstats constructStats(OfflinePlayer p) {
		PVPstats out = new PVPstats(p.getUniqueId(), 0);
		return out;
	}
	
	public static int getKills(OfflinePlayer p) {
		PVPstats player = sKillStats.get(p.getUniqueId());
		if (player != null) {
			if (Config.getValue("debug").equals("true")) {
				System.out.println("[core.backend.playermeta] killTotal for "+p+" is "+player.killTotal);
			}
			return player.killTotal;
		} else {
			System.out.println("[core.backend.playermeta] killTotal for "+p+" is null. Constructing new PVPstats object.");
			PVPstats newPlayer = constructStats(p);
			return newPlayer.killTotal;
		}
	}

//	public static void writeKills() throws IOException {
//		List<String> list = new ArrayList();

//		sKillStats.keySet().forEach(user -> list.add(user.toString() + ":" + sKillStats.get(user)));
//		Files.write(Paths.get("plugins/core/killstats.db"), String.join("\n", list).getBytes());
//	}
	
	// --- OTHER -- //

	public enum MuteType {
		TEMPORARY, PERMANENT, NONE
	}

	public static boolean isOp(CommandSender sender) {
		return (sender instanceof Player) ? sender.isOp() : sender instanceof ConsoleCommandSender;
	}
}
