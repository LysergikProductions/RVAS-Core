package core.data;

import core.Main;
import core.backend.ChatPrint;
import core.data.objects.*;
import core.backend.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import java.util.Date;
import java.text.SimpleDateFormat;

@SuppressWarnings("SpellCheckingInspection")
public class FileManager {
	
	public static final String plugin_work_path = "plugins/core/";
	
	public static File pvpstats_user_database;
	public static File playtime_user_database;
	public static File settings_user_database;

	public static File muted_user_database;
	public static File prison_user_database;

	public static File core_server_config;
	public static File core_restrictions_config;
	public static File core_spawn_config;
	public static File defaultThemeFile;

	public static File donor_list;
	public static File all_donor_codes;
	public static File used_donor_codes;
	
	public static File server_statistics_list;
	public static File motd_message_list;
	public static File auto_announce_list;
	
	public static void backupData(File thisFile, String thisFileName, String ext) throws IOException {
	    
	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		Date date = new Date(); boolean backed_up;
		
		File copied = new File("plugins/core/backup/" + thisFileName + formatter.format(date) + ext);
	    if (!copied.exists()) backed_up = copied.createNewFile();
	    else backed_up = false;

	    if (backed_up) {
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

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else System.out.println("[WARN] FAILED TO COPY ONE OR MORE FILES");
	}
	
	public static void setup() throws IOException {

		// Instantiate File objects \\
		File plugin_work_directory = new File(plugin_work_path);
		File configs_directory = new File(plugin_work_path + "configs");
		File themes_directory = new File(plugin_work_path + "themes/");

		File analytics_directory = new File(plugin_work_path + "analytics/");
		File backup_directory = new File(plugin_work_path + "backup");
		File donor_code_directory = new File(plugin_work_path + "codes");

		core_server_config = new File(plugin_work_path + "configs/config.txt");
		core_restrictions_config = new File(plugin_work_path + "configs/restrictions.txt");
		core_spawn_config = new File(plugin_work_path + "configs/spawn_controller.txt");
		defaultThemeFile = new File(plugin_work_path + "themes/default.json");

		donor_list = new File(plugin_work_path + "donator.db");
		all_donor_codes = new File(plugin_work_path + "codes/all.db");
		used_donor_codes = new File(plugin_work_path + "codes/used.db");
		
		server_statistics_list = new File(plugin_work_path + "analytics.csv");
		motd_message_list = new File(plugin_work_path + "motds.txt");
		auto_announce_list = new File(plugin_work_path + "announcements.txt");
		
		playtime_user_database = new File(plugin_work_path + "playtime.db");
		pvpstats_user_database = new File(plugin_work_path + "pvpstats.txt");
		settings_user_database = new File(plugin_work_path + "player_settings.txt");
		muted_user_database = new File(plugin_work_path + "muted.db");
		prison_user_database = new File(plugin_work_path + "prisoners.db");

		// Create directories and files \\
		if (!plugin_work_directory.exists() && plugin_work_directory.mkdir()) {
			System.out.println("[INFO] Succesfully created plugin_work_directory");
		}

		if (!configs_directory.exists() && configs_directory.mkdir()) {
			if (!core_server_config.exists()) {
				InputStream core_server_config_template = (Main.class.getResourceAsStream("/configs/config.txt"));
				if (core_server_config_template != null) {
					Files.copy(core_server_config_template, Paths.get(plugin_work_path + "/configs/config.txt"));
				}
			}

			if (!core_restrictions_config.exists()) {
				InputStream core_restrictions_config_template = (Main.class.getResourceAsStream("/configs/restrictions.txt"));
				if (core_restrictions_config_template != null) {
					Files.copy(core_restrictions_config_template, Paths.get(plugin_work_path + "/configs/restrictions.txt"));
				}
			}

			if (!core_spawn_config.exists()) {
				InputStream core_spawn_config_template = (Main.class.getResourceAsStream("/configs/spawn_controller.txt"));
				if (core_spawn_config_template != null) {
					Files.copy(core_spawn_config_template, Paths.get(plugin_work_path + "configs/spawn_controller.txt"));
				}
			}
		} else if (!configs_directory.exists()) System.out.println("[WARN] FAILED TO CREATE CONFIGS_DIRECTORY");

		if (!themes_directory.exists() && themes_directory.mkdir()) {
			if (!defaultThemeFile.exists()) {
				InputStream default_template = Main.class.getResourceAsStream("/themes/default.json");
				if (default_template != null) {
					Files.copy(default_template, Paths.get("plugins/core/themes/default.json")); }
			}
		}

		if (!analytics_directory.exists() && analytics_directory.mkdir()) {
			System.out.println("[INFO] Succesfully created analytics_directory");
		}

		if (!backup_directory.exists() && backup_directory.mkdir()) {
			System.out.println("[INFO] Succesfully created backup_directory");
		}

		if (!donor_code_directory.exists() && donor_code_directory.mkdir()) {
			if (!all_donor_codes.exists()) all_donor_codes.createNewFile();
			if (!used_donor_codes.exists()) used_donor_codes.createNewFile();
		} else if (!donor_code_directory.exists()) System.out.println("[WARN] FAILED TO CREATE DONOR_CODE_DIRECTORY");

		if (!donor_list.exists()) donor_list.createNewFile();
		if (!auto_announce_list.exists()) auto_announce_list.createNewFile();
		if (!muted_user_database.exists()) muted_user_database.createNewFile();
		if (!motd_message_list.exists()) motd_message_list.createNewFile();

		if (!server_statistics_list.exists()) {
			server_statistics_list.createNewFile();

			Files.write(Paths.get(server_statistics_list.getAbsolutePath()),
					"\"Average Playtime\",\"New Joins\", \"Unique Joins\"\n".getBytes());
		}
		
		if (!prison_user_database.exists()) prison_user_database.createNewFile();
		if (!playtime_user_database.exists()) playtime_user_database.createNewFile();
		if (!pvpstats_user_database.exists()) pvpstats_user_database.createNewFile();
		if (!settings_user_database.exists()) settings_user_database.createNewFile();

		// Load then check the main config version \\
		try { Config.load();
		} catch (Exception e) { e.printStackTrace(); }

		if (Integer.parseInt(Config.getValue("config.version")) < Config.version) {
			InputStream core_server_config_template;

			if (core_server_config.delete()) {
				core_server_config_template = (Main.class.getResourceAsStream("/configs/config.txt"));
			} else core_server_config_template = null;

			if (core_server_config_template != null) {
				Files.copy(core_server_config_template, Paths.get(plugin_work_path + "/configs/config.txt"));
			}
		}

		try { Config.load();
		} catch (Exception e) { e.printStackTrace(); }
		
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
				
				StatsContainer stats = StatsContainer.fromString(line);
				StatsManager.sPVPStats.put(stats.playerid, stats);
			});
		} catch (Exception e) {
			System.out.println("Exception while reading pvpstats.txt : " + e);
		}
		
		System.out.println("---------------------------------------------------------------------");
		
		// Store PlayerSettings in RAM \\
		try {
			Files.readAllLines(settings_user_database.toPath()).forEach(line -> {
				System.out.println("Reading player_settings.txt, line = " + line);
				
				SettingsContainer settings = SettingsContainer.fromString(line);
				PlayerMeta.sPlayerSettings.put(settings.playerid, settings);
			});
		} catch (Exception e) {
			System.out.println("Exception while reading player_settings.txt : " + e);
		}
	}
}
