package core.tasks;

/* *
 * 
 *  About: Track overall use of various commands by the player-base,
 *  as well as newly generated chunks, new players, and total joins
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
import core.backend.PlayerMeta;
import core.commands.Stats;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.StringBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

public class Analytics extends TimerTask {
	
	// connection trackers
	public static int total_joins = 0;
	public static int new_players = 0;
	
	// performance trackers
	public static int new_chunks = 0;
	
	// commands trackers
	public static int about_cmd = 0;
	public static int admin_cmd = 0;
	public static int discord_cmd = 0;
	public static int help_cmd = 0;
	public static int kill_cmd = 0;
	public static int kit_cmd = 0;
	public static int w_cmd = 0;
	public static int r_cmd = 0;
	public static int msg_cmd = 0;
	public static int server_cmd = 0;
	public static int sign_cmd = 0;
	
	public static int stats_total = 0;
	public static int stats_help = 0;
	public static int stats_info = 0;
	
	public static int tjm_cmd = 0;
	public static int tps_cmd = 0;
	public static int vm_cmd = 0;
	
	public static String CSV_header; {
		
		StringBuilder sb = new StringBuilder(100);
		
		sb.append("\"Date\","); sb.append("\"Joins\",");
		sb.append("\"New Players\","); sb.append("\"New Chunks\",");
		sb.append("\"/about\","); sb.append("\"/admin\",");
		sb.append("\"/discord\","); sb.append("\"/help\",");
		sb.append("\"/kill\","); sb.append("\"/kit\",");
		sb.append("\"/w\","); sb.append("\"/r\",");
		sb.append("\"/msg\","); sb.append("\"/server\",");
		sb.append("\"/sign\","); sb.append("\"Overall /stats\",");
		sb.append("\"/stats help\","); sb.append("\"/stats info\",");
		sb.append("\"/tjm\","); sb.append("\"/tps\","); sb.append("\"/vm\"");
		
		CSV_header = sb.toString();
	}
	
	// TODO: get and track difference between current and previous total server playtime
	
	// TODO: track daily players (plays 4-7 days a week for at least 15min per day)
	// TODO: track second-try players over time (players joining after a 90+ day hiatus)
	// TODO: track weekly players (not daily player and plays at least one-day a week for 15min that day)
	
	private boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	public final static String analytics_work_path = "plugins/core/analytics/";
	
	@Override
	public void run() {	
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		Date date = new Date();
		String current_date = formatter.format(date);
		
		try {
			
			File thisFile = new File(analytics_work_path + "RVAS_Analytics.csv");				
			if (!thisFile.exists()) {
				
				thisFile.createNewFile();
				Analytics.writeNewData(thisFile, CSV_header);
			}
			
			String line = valuesToCSV(
					current_date, total_joins, new_players, new_chunks,
					about_cmd, admin_cmd, discord_cmd, help_cmd, kill_cmd,
					kit_cmd, w_cmd, r_cmd, msg_cmd, server_cmd, sign_cmd,
					stats_total, stats_help, stats_info, tjm_cmd, tps_cmd, vm_cmd
				);
			
			writeNewData(thisFile, line);
			
			// TODO: add remaining resets (see ln 206)
			total_joins = 0;
			stats_total = 0; stats_help = 0; stats_info = 0;
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		if (debug) System.out.println("[core.tasks.analytics] Analytics updated!");
	}
	
	public static String valuesToCSV(
			String date, int total_joins, int new_players, int new_chunks,
			int about_cmd, int admin_cmd, int discord_cmd, int help_cmd, int kill_cmd,
			int kit_cmd, int w_cmd, int r_cmd, int msg_cmd, int server_cmd, int sign_cmd,
			int stats_total, int stats_help, int stats_info, int tjm_cmd, int tps_cmd, int vm_cmd
			) {
		
		StringBuilder sb = new StringBuilder(100);
		
		sb.append('"' + date + "\",");
		sb.append(String.valueOf(total_joins) + ',');
		sb.append(String.valueOf(new_players) + ',');
		sb.append(String.valueOf(new_chunks) + ',');
		sb.append(String.valueOf(about_cmd) + ',');
		sb.append(String.valueOf(admin_cmd) + ',');
		sb.append(String.valueOf(discord_cmd) + ',');
		sb.append(String.valueOf(help_cmd) + ',');
		sb.append(String.valueOf(kill_cmd) + ',');
		sb.append(String.valueOf(kit_cmd) + ',');
		sb.append(String.valueOf(w_cmd) + ',');
		sb.append(String.valueOf(r_cmd) + ',');
		sb.append(String.valueOf(msg_cmd) + ',');
		sb.append(String.valueOf(server_cmd) + ',');
		sb.append(String.valueOf(sign_cmd) + ',');
		sb.append(String.valueOf(stats_total) + ',');
		sb.append(String.valueOf(stats_help) + ',');
		sb.append(String.valueOf(stats_info) + ',');
		sb.append(String.valueOf(tjm_cmd) + ',');
		sb.append(String.valueOf(tps_cmd) + ',');
		sb.append(String.valueOf(vm_cmd));
		
		String out = sb.toString();
		return out;
	}
	
	public static boolean writeNewData(File thisFile, String thisLine) {
		
		// append == true
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(analytics_work_path + thisFile.getName(), true));
			
			w.write(thisLine + "\n");
			w.close();
			
		  } catch (IOException e) {
			  throw new UncheckedIOException(e);
		  }
		return true;
	}
	
	// TODO: see if calling this method in a thread is necessary for performance
	// currently unused
	public static double sumPlaytimes() {
		
		double sum = 0;
		
		for (OfflinePlayer thisPlayer: Bukkit.getServer().getOfflinePlayers()) {
			sum += PlayerMeta.getPlaytime(thisPlayer);
		}
		
		return sum;
	}
	
	public static boolean incAnalytics() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");		
		Date date = new Date();
		
		File thisFile;
		String current_date = formatter.format(date);
		
		try {
			thisFile = new File(Analytics.analytics_work_path + "RVAS_Analytics.csv");
			if (!thisFile.exists()) {
				
				thisFile.createNewFile();
				Analytics.writeNewData(thisFile, CSV_header);
			}
			
		} catch (IOException e) {
			System.out.println(e);
			return false;
		}
		
		String line = valuesToCSV(
				current_date, total_joins, new_players, new_chunks,
				about_cmd, admin_cmd, discord_cmd, help_cmd, kill_cmd,
				kit_cmd, w_cmd, r_cmd, msg_cmd, server_cmd, sign_cmd,
				stats_total, stats_help, stats_info, tjm_cmd, tps_cmd, vm_cmd
			);
		
		try {
			Analytics.writeNewData(thisFile, line);
			
			// TODO: add remaining resets
			total_joins = 0;
			stats_total = 0; stats_help = 0; stats_info = 0;
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return true;
	}
}
