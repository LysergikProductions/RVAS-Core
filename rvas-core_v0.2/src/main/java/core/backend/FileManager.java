package core.backend;

import core.Main;
import core.objects.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import java.util.Date;
import java.text.SimpleDateFormat;

public class FileManager {
	
	public static final String plugin_work_path = "plugins/core/";
	
	public static File pvpstats_user_database;
	public static File playtime_user_database;
	public static File settings_user_database;
	public static File muted_user_database;
	
	public static File donor_list;
	public static File all_donor_codes;
	public static File used_donor_codes;
	
	public static File server_statistics_list;
	public static File core_server_config;
	public static File motd_message_list;
	public static File prison_user_database;
	
	public static void backupData(File thisFile, String thisFileName, String ext) throws IOException {
	    
	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		Date date = new Date();
		
		File copied = new File("plugins/core/backup/" + thisFileName + formatter.format(date) + ext);
	    if (!copied.exists()) copied.createNewFile();
	    
	    try (	    	
	    	
	    	InputStream in = new BufferedInputStream(
	        new FileInputStream(thisFile));
	    	
	    	OutputStream out = new BufferedOutputStream(
	        new FileOutputStream(copied))) {
	 
	        byte[] buffer = new byte[1024];
	        int lengthRead;
	        
	        while ((lengthRead = in.read(buffer)) > 0) {
	        	
	            out.write(buffer, 0, lengthRead);
	            out.flush();
	        }
	        
	    } catch (IOException e) {System.out.println(e);}
	}
	
	public static void setup() throws IOException {
		
		File plugin_work_directory = new File(plugin_work_path);
		File backup_directory = new File(plugin_work_path + "backup");
		File analytics_directory = new File(plugin_work_path + "analytics/");
		File donor_code_directory = new File(plugin_work_path + "codes");
		
		donor_list = new File(plugin_work_path + "donator.db");
		all_donor_codes = new File(plugin_work_path + "codes/all.db");
		used_donor_codes = new File(plugin_work_path + "codes/used.db");
		
		server_statistics_list = new File(plugin_work_path + "analytics.csv");
		core_server_config = new File(plugin_work_path + "config.txt");
		motd_message_list = new File(plugin_work_path + "motds.txt");
		
		playtime_user_database = new File(plugin_work_path + "playtime.db");
		pvpstats_user_database = new File(plugin_work_path + "pvpstats.txt");
		settings_user_database = new File(plugin_work_path + "player_settings.txt");
		muted_user_database = new File(plugin_work_path + "muted.db");
		prison_user_database = new File(plugin_work_path + "prisoners.db");	

		if (!plugin_work_directory.exists()) plugin_work_directory.mkdir();
		if (!backup_directory.exists()) backup_directory.mkdir();
		if (!analytics_directory.exists()) analytics_directory.mkdir();
		if (!donor_code_directory.exists()) donor_code_directory.mkdir();
		
		if (!donor_list.exists()) donor_list.createNewFile();
		if (!all_donor_codes.exists()) all_donor_codes.createNewFile();
		if (!used_donor_codes.exists()) used_donor_codes.createNewFile();
		if (!muted_user_database.exists()) muted_user_database.createNewFile();
		if (!motd_message_list.exists()) motd_message_list.createNewFile();

		if (!server_statistics_list.exists()) {
			
			server_statistics_list.createNewFile();
			Files.write(Paths.get(server_statistics_list.getAbsolutePath()),
					"\"Average Playtime\",\"New Joins\", \"Unique Joins\"\n".getBytes());
		}

		if (!core_server_config.exists()) {
			InputStream core_server_config_template = (Main.class.getResourceAsStream("/config.txt"));
			Files.copy(core_server_config_template, Paths.get(plugin_work_path + "config.txt"));
		}

		Config.load();

		if (Integer.parseInt(Config.getValue("config.version")) < Config.version) {
			core_server_config.delete();
			InputStream core_server_config_template = (Main.class.getResourceAsStream("/config.txt"));
			Files.copy(core_server_config_template, Paths.get(plugin_work_path + "config.txt"));
		}
		
		if (!prison_user_database.exists()) prison_user_database.createNewFile();
		if (!playtime_user_database.exists()) playtime_user_database.createNewFile();
		if (!pvpstats_user_database.exists()) pvpstats_user_database.createNewFile();
		if (!settings_user_database.exists()) settings_user_database.createNewFile();

		Config.load();
		
		// Store Donor Codes in RAM \\
		try {
			Files.readAllLines(all_donor_codes.toPath()).forEach(val ->
				PlayerMeta.DonorCodes.add(val.replace("\"", "").trim()));
		} catch (Exception e) {
			System.out.println("Exception while reading all.db : " + e);
		}

		try {
			PlayerMeta.UsedDonorCodes.addAll(Files.readAllLines(used_donor_codes.toPath()));
		} catch (Exception e) {
			System.out.println("Exception while reading used.db : " + e);
		}	
		
		// Store Playtimes in RAM \\
		try {
			Files.readAllLines(playtime_user_database.toPath()).forEach(val ->
				PlayerMeta.Playtimes.put(
						UUID.fromString(val.split(":")[0]), Double.parseDouble(val.split(":")[1])));
		} catch (Exception e) {
			System.out.println("Exception while reading playtimes.db : " + e);
		}		
		
		// Store PVPstats in RAM \\
		try {
			Files.readAllLines(pvpstats_user_database.toPath()).forEach(line -> {
				System.out.println("Reading pvpstats.txt, line = " + line);
				
				PVPstats stats = PVPstats.fromString(line);
				PVPdata.sPVPStats.put(stats.playerid, stats);
			});
		} catch (Exception e) {
			System.out.println("Exception while reading pvpstats.txt : " + e);
		}
		
		System.out.println("---------------------------------------------------------------------");
		
		// Store PlayerSettings in RAM \\
		try {
			Files.readAllLines(settings_user_database.toPath()).forEach(line -> {
				System.out.println("Reading player_settings.txt, line = " + line);
				
				PlayerSettings settings = PlayerSettings.fromString(line);
				PlayerMeta.sPlayerSettings.put(settings.playerid, settings);
			});
		} catch (Exception e) {
			System.out.println("Exception while reading player_settings.txt : " + e);
		}
	}
}
