package core.objects;

import java.util.UUID;
import java.io.Serializable;

public class PVPstats implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public UUID playerid;
	public int killTotal;
	public int deathTotal;
	public String kd;
	
	//int killWcrystal;
	//int logEscape;
	
	public PVPstats(UUID playerid, int killTotal, int deathTotal, String kd) {
		this.playerid = playerid; this.killTotal = killTotal; this.deathTotal = deathTotal; this.kd = kd;
	}
	
	@Override
    public String toString() {
        return playerid + "=" + playerid + ":" + killTotal + ":" + deathTotal + ":" + kd;
    }
	
	public static PVPstats fromString(String line) {
		String[] stats = line.split(":");
		
		UUID playerid = UUID.fromString(stats[0]);
		int killTotal = Integer.parseInt(stats[1]);
		int deathTotal = Integer.parseInt(stats[2]);
		String kd = stats[3];
		
		PVPstats out = new PVPstats(playerid, killTotal, deathTotal, kd);
		
		return out;
	}
}
