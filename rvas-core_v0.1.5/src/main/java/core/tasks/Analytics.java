package core.tasks;

import core.backend.Config;
import core.backend.PlayerMeta;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.StringBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
//import org.bukkit.OfflinePlayer;

public class Analytics extends TimerTask {
	
	// TODO: track new players over time
	// TODO: track daily players (plays 4-7 days a week for at least 15min per day)
	// TODO: track second-try players over time (players joining after a 90+ day hiatus)
	// TODO: track weekly players (not daily player and plays at least one-day a week for 15min that day)
	
	private boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	private final static String analytics_work_path = "plugins/core/analytics/";
	
	@Override
	public void run() {	
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		
		Date date = new Date();
		String current_date = formatter.format(date);
		
		for (Player thisPlayer: Bukkit.getServer().getOnlinePlayers()) {
			
			Date firstPlayDate = new Date(thisPlayer.getFirstPlayed());	
			String firstPlayed = formatter.format(firstPlayDate);
			
			try {
				UUID playerid = thisPlayer.getUniqueId();
				
				File thisPlayerFile = new File(analytics_work_path + playerid.toString() + ".csv");				
				if (!thisPlayerFile.exists()) thisPlayerFile.createNewFile();
				
				String line = valuesToCSV(
					current_date, firstPlayed, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					(int)Math.rint(PlayerMeta.getPlaytime(thisPlayer))
				);
				
				writeNewData(thisPlayerFile, line, playerid);
				
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		if (debug) System.out.println("[core.tasks.analytics] Analytics updated!");
	}
	
	public String valuesToCSV(
			String date, String join_date, int use_stats, int use_stats_help, int use_stats_info,
			int use_kit, int use_help, int use_server, int use_admin, int use_w, int use_r,
			int use_msg, int use_discord, int use_about, int use_tps, int use_kill, int use_vm,
			int use_vote, int use_sign, int playtime_sec
			) {
		
		StringBuilder sb = new StringBuilder(100);
		
		sb.append('"' + date + "\",");
		sb.append('"' + join_date + "\",");
		sb.append(String.valueOf(use_stats) + ',');
		sb.append(String.valueOf(use_stats_help) + ',');
		sb.append(String.valueOf(use_stats_info) + ',');
		sb.append(String.valueOf(use_kit) + ',');
		sb.append(String.valueOf(use_help) + ',');
		sb.append(String.valueOf(use_server) + ',');
		sb.append(String.valueOf(use_admin) + ',');
		sb.append(String.valueOf(use_w) + ',');
		sb.append(String.valueOf(use_r) + ',');
		sb.append(String.valueOf(use_msg) + ',');
		sb.append(String.valueOf(use_discord) + ',');
		sb.append(String.valueOf(use_about) + ',');
		sb.append(String.valueOf(use_tps) + ',');
		sb.append(String.valueOf(use_kill) + ',');
		sb.append(String.valueOf(use_vm) + ',');
		sb.append(String.valueOf(use_vote) + ',');
		sb.append(String.valueOf(use_sign) + ',');
		sb.append(String.valueOf(playtime_sec));
		
		String out = sb.toString();
		return out;
	}
	
	public boolean writeNewData(File thisFile, String thisLine, UUID playerid) {
		
		// append == true
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(analytics_work_path + playerid.toString() + ".csv", true));
			
			w.write(thisLine + "\n");
			w.close();
			
		  } catch (IOException e) {
			  throw new UncheckedIOException(e);
		  }
		return true;
	}
}
