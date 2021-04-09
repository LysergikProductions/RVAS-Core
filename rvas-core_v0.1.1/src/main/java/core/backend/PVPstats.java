package core.backend;

import java.util.UUID;

public class PVPstats {
	UUID playerid;
	int killTotal;
	int deathTotal;
	double kd;
	int killWcrystal;
	int logEscape;
	
	public PVPstats(UUID playerid) {
		this.playerid = playerid; this.killTotal = 0;
	}
}
