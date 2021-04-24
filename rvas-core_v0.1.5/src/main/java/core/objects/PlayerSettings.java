package core.objects;

/* *
 * 
 *  About: A class object that is used as the data container
 *  	for toggleable player settings and other information
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

import core.backend.Config;

import java.util.UUID;
import java.io.Serializable;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

public class PlayerSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public UUID playerid;
	public boolean show_PVPstats;
	
	public boolean show_kills;
	public boolean show_deaths;
	public boolean show_kd;
	
	public boolean show_player_join_messages;
	public boolean show_player_death_messages;
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	
	public PlayerSettings(
			UUID playerid, boolean show_PVPstats, boolean show_kills,
			boolean show_deaths, boolean show_kd, boolean show_player_join_messages,
			boolean show_player_death_messages) {
		
		this.playerid = playerid; this.show_PVPstats = show_PVPstats;
		this.show_kills = show_kills; this.show_deaths = show_deaths;
		this.show_kd = show_kd; this.show_player_join_messages = show_player_join_messages;
		this.show_player_death_messages = show_player_death_messages;
	}
	
	@Override
    public String toString() {

		String out = playerid + ":" + show_PVPstats + ":" + show_kills + ":" + show_deaths + ":" + show_kd + ":" + show_player_join_messages + ":" + show_player_death_messages;
		
		return out;
    }
	
	public static PlayerSettings fromString(String line) {
		// Example of intended given line: f6c6e3a1-a1ec-4fee-9d1d-f5e495c3e9d7:true:true:true:true:false:true
		
		String[] settings = line.split(":");
		
		UUID playerid = UUID.fromString(settings[0]);
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerid);
		String player_name = player.getName();
		
		boolean show_PVPstats;		
		try {show_PVPstats = Boolean.parseBoolean(settings[1]);} catch (Exception e) {show_PVPstats = false;}
		
		boolean show_kills;		
		try {show_kills = Boolean.parseBoolean(settings[2]);} catch (Exception e) {show_kills = true;}
		
		boolean show_deaths;		
		try {show_deaths = Boolean.parseBoolean(settings[3]);} catch (Exception e) {show_deaths = true;}
		
		boolean show_kd;		
		try {show_kd = Boolean.parseBoolean(settings[4]);} catch (Exception e) {show_kd = true;}
		
		boolean show_player_join_messages;
		
		try {show_player_join_messages = Boolean.parseBoolean(settings[4]);}
		catch (Exception e) {show_player_join_messages = true;}
		
		boolean show_player_death_messages;
		
		try {show_player_death_messages = Boolean.parseBoolean(settings[4]);}
		catch (Exception e) {show_player_death_messages = true;}
		
		PlayerSettings out = new PlayerSettings(playerid, show_PVPstats, show_kills, show_deaths, show_kd,
				show_player_join_messages, show_player_death_messages);
		return out;
	}
}
