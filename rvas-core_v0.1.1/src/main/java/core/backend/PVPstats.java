package core.backend;

import java.util.UUID;
import java.io.Serializable;

import org.bukkit.entity.Player;

public class PVPstats implements Serializable {
	private static final long serialVersionUID = 1L;
	
	UUID playerid;
	int killTotal;
	int deathTotal;
	
	int killWcrystal;
	int logEscape;
	
	PVPstats() {
		
	};
	
	PVPstats(UUID playerid, int killTotal) {
		this.playerid = playerid; this.killTotal = killTotal;
	}
	
	@Override
    public String toString() {
        return playerid + ":" + killTotal;
    }
	
	public static PVPstats fromString(String line) {
		String[] stats = line.split(":");
		
		UUID playerid = UUID.fromString(stats[0]);
		int killTotal = Integer.parseInt(stats[1]);
		
		PVPstats out = new PVPstats(playerid, killTotal);
		
		return out;
	}
}
