package core.backend;

import java.util.UUID;
import java.io.Serializable;

import org.bukkit.entity.Player;

public class PVPstats implements Serializable {
	private static final long serialVersionUID = 1L;
	
	UUID playerid;
	int killTotal;
	int deathTotal;
	
	//int killWcrystal;
	//int logEscape;
	
	/*PVPstats() {
		
	};*/
	
	PVPstats(UUID playerid, int killTotal, int deathTotal) {
		this.playerid = playerid; this.killTotal = killTotal; this.deathTotal = deathTotal;
	}
	
	@Override
    public String toString() {
        return playerid + ":" + killTotal + ":" + deathTotal;
    }
	
	public static PVPstats fromString(String line) {
		String[] stats = line.split(":");
		
		UUID playerid = UUID.fromString(stats[0]);
		int killTotal = Integer.parseInt(stats[1]);
		int deathTotal = Integer.parseInt(stats[2]);
		
		PVPstats out = new PVPstats(playerid, killTotal, deathTotal);
		
		return out;
	}
}
