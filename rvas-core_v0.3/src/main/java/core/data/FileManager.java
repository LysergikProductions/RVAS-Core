package core.data;

import core.Main;
import core.backend.ex.CoreException;
import core.data.objects.*;
import core.backend.Config;
import core.backend.ex.Critical;

import java.io.*;
import java.util.Date;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.text.SimpleDateFormat;

@Critical
public class FileManager {
	
	public static final String plugin_work_path = "plugins/core/";
	
	public static File pvpstats_user_database, playtime_user_database, server_statistics_list,
			settings_user_database, muted_user_database, prison_user_database, core_server_config,
			core_restrictions_config, core_spawn_config, motd_message_list, auto_announce_list,
			donor_database, all_donor_codes, used_donor_codes, defaultThemeFile, halloweenThemeFile, customThemeFile;

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

			} catch (IOException e) { e.printStackTrace(); }

		} else Main.console.log(Level.WARNING, "FAILED TO COPY ONE OR MORE FILES");
	}

	@SuppressWarnings("SpellCheckingInspection")
	public static void setup() throws CoreException {

		// Instantiate File objects \\
		try {
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
			halloweenThemeFile = new File(plugin_work_path + "themes/halloween.json");
			customThemeFile = new File(plugin_work_path + "themes/custom.json");

			donor_database = new File(plugin_work_path + "donators.json");
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
				Main.console.log(Level.INFO, "Succesfully created plugin_work_directory"); }

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
			} else if (!configs_directory.exists()) Main.console.log(Level.WARNING, "Failed to create configs directory!");

			// - THEMES - \\
			if (!themes_directory.exists() &&
					themes_directory.mkdir()) Main.console.log(Level.INFO, "Created themes directory!");

			if (!defaultThemeFile.exists()) {
				InputStream defaultTemplate = Main.class.getResourceAsStream("/themes/default.json");

				if (defaultTemplate != null) {
					Files.copy(defaultTemplate, Paths.get("plugins/core/themes/default.json"));
					Main.console.log(Level.INFO, "Successfully copied data from resource default.json"); }
			}

			if (!halloweenThemeFile.exists()) {
				InputStream halloweenTemplate = Main.class.getResourceAsStream("/themes/halloween.json");

				if (halloweenTemplate != null) {
					Files.copy(halloweenTemplate, Paths.get("plugins/core/themes/halloween.json"));
					Main.console.log(Level.INFO, "Successfully copied data from resource halloween.json"); }
			}

			if (!customThemeFile.exists()) {
				InputStream customTemplate = Main.class.getResourceAsStream("/themes/custom.json");

				if (customTemplate != null) {
					Files.copy(customTemplate, Paths.get("plugins/core/themes/custom.json"));
					Main.console.log(Level.INFO, "Successfully copied data from resource custom.json"); }
			}

			if (!analytics_directory.exists() && analytics_directory.mkdir()) {
				Main.console.log(Level.INFO, "Succesfully created analytics_directory"); }

			if (!backup_directory.exists() && backup_directory.mkdir()) {
				Main.console.log(Level.INFO, "Succesfully created backup_directory"); }

			if (!donor_code_directory.exists() && donor_code_directory.mkdir()) {
				if (!all_donor_codes.exists()) all_donor_codes.createNewFile();
				if (!used_donor_codes.exists()) used_donor_codes.createNewFile();

			} else if (!donor_code_directory.exists()) Main.console.log(Level.WARNING, "Failed to create donor code files");

			if (!donor_database.exists()) donor_database.createNewFile();
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
			} catch (Exception e) { System.out.println(e.getMessage()); }

			if (Integer.parseInt(Config.getValue("config.version")) < Config.version) {
				InputStream core_server_config_template;

				if (core_server_config.delete()) { core_server_config_template = (
						Main.class.getResourceAsStream("/configs/config.txt"));
				} else core_server_config_template = null;

				if (core_server_config_template != null) Files.copy(
						core_server_config_template, Paths.get(plugin_work_path + "/configs/config.txt"));
			}
							// THROW FATAL EXCEPTION \\
		} catch (Exception e) { throw new CoreException(FileManager.class, e); }

									// Safe \\
		try { Config.load();
		} catch (Exception e) { System.out.println(e.getMessage()); }

		// Store Donors and Codes in RAM \\
		try { Files.readAllLines(all_donor_codes.toPath())
				.forEach(val -> DonationManager.DonorCodes.add(val.replace("\"", "").trim()));
		} catch (Exception e) { Main.console.log(Level.WARNING, "WARN Exception while reading all.db : " + e); }

		try { DonationManager.UsedDonorCodes.addAll(Files.readAllLines(used_donor_codes.toPath()));
		} catch (Exception e) { Main.console.log(Level.WARNING, "WARN Exception while reading used.db : " + e); }

		// Store Playtimes in RAM \\
		try { Files.readAllLines(playtime_user_database.toPath()).forEach(val ->
					PlayerMeta.Playtimes.put(UUID.fromString(val.split(":")[0]), Double.parseDouble(val.split(":")[1])));
		} catch (Exception e) { Main.console.log(Level.WARNING, "Exception while reading playtimes.db : " + e); }

		// Store PVPstats in RAM \\
		try {
			Files.readAllLines(pvpstats_user_database.toPath()).forEach(line -> {
				if (Config.debug && Config.verbose) Main.console.log(Level.INFO,
						"Reading pvpstats.txt, line = " + line);

				StatsContainer stats = StatsContainer.fromString(line);
				StatsManager.sPVPStats.put(stats.playerid, stats);
			});
		} catch (Exception e) { Main.console.log(Level.WARNING, "Exception while reading pvpstats.txt : " + e); }

		System.out.println("---------------------------------------------------------------------");

		// Store PlayerSettings in RAM \\
		try {
			Files.readAllLines(settings_user_database.toPath()).forEach(line -> {
				if (Config.debug && Config.verbose) Main.console.log(Level.INFO,
						"Reading player_settings.txt, line = " + line);

				SettingsContainer settings = SettingsContainer.fromString(line);
				PlayerMeta.sPlayerSettings.put(settings.playerid, settings);
			});
		} catch (Exception e) { Main.console.log(Level.WARNING, "Exception while reading player_settings.txt : " + e); }
	}

	public static File getConfiguredThemeFile() {
		String thisString = Config.getValue("theme").trim();

		switch (thisString) {
			case "default": return defaultThemeFile;
			case "halloween": return halloweenThemeFile;
			case "custom": return customThemeFile;
		}
		return defaultThemeFile;
	}
}
