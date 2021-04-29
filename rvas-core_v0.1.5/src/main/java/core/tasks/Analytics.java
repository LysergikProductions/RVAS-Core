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
import core.backend.FileManager;
import core.backend.LagProcessor;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

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
	public static int loaded_chunks = 0;
	public static int speed_warns = 0;
	public static int speed_kicks = 0;
	public static int wither_spawns = 0;
	public static int failed_wither_spawns = 0;
	public static int removed_skulls = 0;
	
	// commands trackers
	public static int about_cmd = 0; public static int admin_cmd = 0;
	public static int discord_cmd = 0; public static int help_cmd = 0;
	public static int kill_cmd = 0; public static int kit_cmd = 0;
	public static int w_cmd = 0; public static int r_cmd = 0;
	public static int msg_cmd = 0; public static int server_cmd = 0;
	public static int sign_cmd = 0;
	
	public static int stats_total = 0; public static int tjm_cmd = 0;
	public static int stats_help = 0; public static int tps_cmd = 0;
	public static int stats_info = 0; public static int vm_cmd = 0;
	
	public static String performance_work_path = FileManager.plugin_work_path
			+ "analytics/RVAS_Analytics-performance.csv";
	public static String commands_work_path = FileManager.plugin_work_path
			+ "analytics/RVAS_Analytics-commands.csv";
	
	public static String CSV_perfHeader; {
		
		StringBuilder sb1 = new StringBuilder(256);
		
		sb1.append("\"Date\","); sb1.append("\"TPS\","); sb1.append("\"Online Players\",");
		sb1.append("\"New UUIDs\","); sb1.append("\"Joins Events\",");
		sb1.append("\"New Chunks\","); sb1.append("\"Loaded Chunks\",");
		sb1.append("\"Speed Warnings\","); sb1.append("\"Speed Kicks\",");
		sb1.append("\"Wither Spawns\","); sb1.append("\"Failed Withers\",");
		sb1.append("\"Removed Wither Skulls\","); sb1.append("\"Loaded Withers\"");
		
		CSV_perfHeader = sb1.toString();
	}
	
	public static String CSV_cmdHeader; {
		
		StringBuilder sb2 = new StringBuilder(128);
		
		sb2.append("\"Date\",");
		sb2.append("\"/about\","); sb2.append("\"/admin\",");
		sb2.append("\"/discord\","); sb2.append("\"/help\",");
		sb2.append("\"/kill\","); sb2.append("\"/kit\",");
		sb2.append("\"/w\","); sb2.append("\"/r\",");
		sb2.append("\"/msg\","); sb2.append("\"/server\",");
		sb2.append("\"/sign\","); sb2.append("\"Overall /stats\",");
		sb2.append("\"/stats help\","); sb2.append("\"/stats info\",");
		sb2.append("\"/tjm\","); sb2.append("\"/tps\","); sb2.append("\"/vm\""); 
		
		CSV_cmdHeader = sb2.toString();
	}
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	
	@Override // write and reset analytics on-schedule
	public void run() {
		
		// if (!Config.getValue("analytics.enabled").equals("true")) return;
		// TODO: why doesn't ^this^ line return false and move to capture()?
		Analytics.capture();
	}
	
	// write and reset analytics on-demand
	public static boolean capture() {
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");		
		String current_date = formatter.format(date);
		
		File performanceFile; File commandsFile;
		
		try {			
			performanceFile = new File(performance_work_path);
			commandsFile = new File(commands_work_path);
			
			if (!performanceFile.exists()) {
				
				performanceFile.createNewFile();
				Analytics.writeNewData(performanceFile, CSV_perfHeader);
			}
			
			if (!commandsFile.exists()) {
				
				commandsFile.createNewFile();
				Analytics.writeNewData(commandsFile, CSV_cmdHeader);
			}
			
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
		int conPlayers = Bukkit.getOnlinePlayers().size();
		
		String performanceLine = toPerformanceCSV(
				current_date, LagProcessor.getTPS(),
				conPlayers, new_players, total_joins,
				new_chunks, loaded_chunks, speed_warns, speed_kicks,
				wither_spawns, failed_wither_spawns, removed_skulls,
				LagManager.getWithers()
			);
		
		String commandsLine = toCommandsCSV(
				current_date,
				about_cmd, admin_cmd, discord_cmd, help_cmd, kill_cmd,
				kit_cmd, w_cmd, r_cmd, msg_cmd, server_cmd, sign_cmd,
				stats_total, stats_help, stats_info, tjm_cmd, tps_cmd, vm_cmd
			);
		
		// append data to file
		try {Analytics.writeNewData(performanceFile, performanceLine);}
		catch (Exception e) {System.out.println(e);}
		
		try {Analytics.writeNewData(commandsFile, commandsLine);}
		catch (Exception e) {System.out.println(e);}
		
		// reset data
		conPlayers = 0; new_players = 0; total_joins = 0;
		new_chunks = 0; loaded_chunks = 0; speed_warns = 0; speed_kicks = 0;
		wither_spawns = 0; failed_wither_spawns = 0; removed_skulls = 0;
		
		about_cmd = 0; admin_cmd = 0; discord_cmd = 0; help_cmd = 0;
		kill_cmd = 0; kit_cmd = 0; w_cmd = 0; r_cmd = 0; msg_cmd = 0;
		server_cmd = 0; sign_cmd = 0; stats_total = 0; stats_help = 0;
		stats_info = 0; tjm_cmd = 0; tps_cmd = 0; vm_cmd = 0;
		
		if (debug) System.out.println("[core.tasks.analytics] Analytics updated!");
		return true;
	}
	
	// convert data to a single performance CSV-file line
	public static String toPerformanceCSV(
			String date, double tps, int con_players, int new_players,
			int total_joins, int new_chunks, int loaded_chunks,
			int speed_warns, int speed_kicks, int wither_spawns,
			int failed_wither_spawns, int removed_skulls, int loaded_withers
			) {
		
		StringBuilder sb = new StringBuilder(128);
		
		sb.append('"' + date + "\",");
		sb.append(String.valueOf(tps) + ',');
		sb.append(String.valueOf(con_players) + ',');
		sb.append(String.valueOf(new_players) + ',');
		sb.append(String.valueOf(total_joins) + ',');
		sb.append(String.valueOf(new_chunks) + ',');
		sb.append(String.valueOf(loaded_chunks) + ',');
		sb.append(String.valueOf(speed_warns) + ',');
		sb.append(String.valueOf(speed_kicks) + ',');
		sb.append(String.valueOf(wither_spawns) + ',');
		sb.append(String.valueOf(failed_wither_spawns) + ',');
		sb.append(String.valueOf(removed_skulls) + ',');
		sb.append(String.valueOf(loaded_withers));
		
		return sb.toString();
	}
	
	// convert data in RAM to a single commands CSV-file line
	public static String toCommandsCSV(
			String date,
			int about_cmd, int admin_cmd, int discord_cmd, int help_cmd, int kill_cmd,
			int kit_cmd, int w_cmd, int r_cmd, int msg_cmd, int server_cmd, int sign_cmd,
			int stats_total, int stats_help, int stats_info, int tjm_cmd, int tps_cmd, int vm_cmd
			) {
		
		StringBuilder sb = new StringBuilder(128);
		
		sb.append('"' + date + "\",");
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
		
		return sb.toString();
	}
	
	// append single CSV Strings to file
	public static boolean writeNewData(File thisFile, String thisLine) {
		
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(thisFile, true));
			
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
}