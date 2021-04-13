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
		// String line looks like: f6c6e3a1-a1ec-4fee-9d1d-f5e495c3e9d7=f6c6e3a1-a1ec-4fee-9d1d-f5e495c3e9d7:4:7:null!
		System.out.println("Converting string to PVPstats object: " + line);
		
		String[] sep = line.split("=");
		String[] stats = sep[1].split(":");
		
		UUID playerid = UUID.fromString(stats[0]);
		System.out.println("Parsed: " + playerid);
		
		int killTotal = Integer.parseInt(stats[1]);
		System.out.println("Parsed: " + killTotal);
		
		int deathTotal = Integer.parseInt(stats[2]);
		System.out.println("Parsed: " + deathTotal);
		
		String kd = stats[3];
		System.out.println("Parsed: " + kd);
		
		PVPstats out = new PVPstats(playerid, killTotal, deathTotal, kd);
		System.out.println("PVPstats out: " + out);
		
		return out;
	}
}
