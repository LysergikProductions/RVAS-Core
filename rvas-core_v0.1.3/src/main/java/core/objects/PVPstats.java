package core.objects;

/* *
 * 
 *  About: A class object that is used as the data container
 *  	for all PVP related stats while they are in memory 
 * 
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * */

import java.util.UUID;
import java.io.Serializable;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

public class PVPstats implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public UUID playerid; public int killTotal;
	public int deathTotal; public String kd;
	public int spawnKills;
	
	//int killWcrystal;
	//int logEscape;
	
	public PVPstats(UUID playerid, int killTotal, int deathTotal, String kd, int spawnKills) {
		this.playerid = playerid; this.killTotal = killTotal; this.deathTotal = deathTotal;
		this.kd = kd; this.spawnKills = spawnKills;
	}
	
	@Override
    public String toString() {
		// ** try remove the "UUID=" at the beginning of the strings
		String out = playerid + "=" + playerid + ":" + killTotal + ":" + deathTotal + ":" + kd + ":" + spawnKills;		
		return out;
    }
	
	public static PVPstats fromString(String line) {
		// String line looks like: f6c6e3a1-a1ec-4fee-9d1d-f5e495c3e9d7=f6c6e3a1-a1ec-4fee-9d1d-f5e495c3e9d7:4:7:null!
		
		// ** try remove the "UUID=" at the beginning of the strings
		
		String[] sep = line.split("=");
		String[] stats = sep[1].split(":");
		
		UUID playerid = UUID.fromString(stats[0]);
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerid);
		String player_name = player.getName();
		
		System.out.println("Parsed ign: " + player_name);
		System.out.println("Parsed id: " + playerid);
		
		int killTotal;
		try {
			killTotal = Integer.parseInt(stats[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			killTotal = 0;
		}
		
		System.out.println("Parsed kills: " + killTotal);
		
		int deathTotal;
		try {
			deathTotal = Integer.parseInt(stats[2]);
		} catch (ArrayIndexOutOfBoundsException e) {
			deathTotal = 0;
		}
		
		System.out.println("Parsed deaths: " + deathTotal);
		
		String kd;
		try {
			kd = stats[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			kd = "";
		}
		
		System.out.println("Parsed k/d: " + kd);
		
		int spawnKills;
		try {
			spawnKills = Integer.parseInt(stats[4]);
		} catch (ArrayIndexOutOfBoundsException e) {
			spawnKills = 0;
		}
		
		System.out.println("Parsed spawn kills: " + spawnKills);
		
		PVPstats out = new PVPstats(playerid, killTotal, deathTotal, kd, spawnKills);
		
		return out;
	}
}