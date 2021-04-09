package core.backend;

import java.util.UUID;

import org.bukkit.entity.Player;

public class PVPstats {
	UUID playerid;
	int killTotal;
	int deathTotal;
	double kd;
	int killWcrystal;
	int logEscape;
	
	public PVPstats(UUID playerid, int killTotal) {
		this.playerid = playerid; this.killTotal = 0;
	}
	
	public static PVPstats fromString(String line) {
		String[] stats = line.split(":");
		
		UUID playerid = UUID.fromString(stats[0]);
		int killTotal = Integer.parseInt(stats[1]);
		
		PVPstats out = new PVPstats(playerid, killTotal);
		
		return out;
	}
}
