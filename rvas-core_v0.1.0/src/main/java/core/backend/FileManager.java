package protocol3.backend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import protocol3.Main;

public class FileManager {

	public static void setup() throws IOException {
		final String plugin_work_path = "plugins/protocol3/";

		// Create initial directory
		File plugin_work_directory = new File(plugin_work_path);
		File donor_code_directory = new File(plugin_work_path + "codes");
		File donor_list = new File(plugin_work_path + "donator.db");
		File all_donor_codes = new File(plugin_work_path + "codes/all.db");
		File used_donor_codes = new File(plugin_work_path + "codes/used.db");
		File muted_user_database = new File(plugin_work_path + "muted.db");
		File server_statistics_list = new File(plugin_work_path + "analytics.csv");
		File protocol3_server_config = new File(plugin_work_path + "config.txt");
		File lagfag_user_database = new File(plugin_work_path + "lagfag.db");
		File playtime_user_database = new File(plugin_work_path + "playtime.db");

		//
		if (!plugin_work_directory.exists()) plugin_work_directory.mkdir();
		if (!donor_code_directory.exists()) donor_code_directory.mkdir();
		if (!donor_list.exists()) donor_list.createNewFile();
		if (!all_donor_codes.exists()) all_donor_codes.createNewFile();
		if (!used_donor_codes.exists()) used_donor_codes.createNewFile();
		if (!muted_user_database.exists()) muted_user_database.createNewFile();

		if (!server_statistics_list.exists()) {
			server_statistics_list.createNewFile();
			Files.write(Paths.get(server_statistics_list.getAbsolutePath()),
					"\"Average Playtime\",\"New Joins\", \"Unique Joins\"\n".getBytes());
		}

		if (!protocol3_server_config.exists()) {
			InputStream protocol3_server_config_template = (Main.class.getResourceAsStream("/config.txt"));
			Files.copy(protocol3_server_config_template, Paths.get(plugin_work_path + "config.txt"));
		}

		Config.load();

		if (Integer.parseInt(Config.getValue("config.version")) < Config.version) {
			protocol3_server_config.delete();
			InputStream protocol3_server_config_template = (Main.class.getResourceAsStream("/config.txt"));
			Files.copy(protocol3_server_config_template, Paths.get(plugin_work_path + "config.txt"));
		}

		if (!lagfag_user_database.exists()) lagfag_user_database.createNewFile();


		if (!playtime_user_database.exists()) playtime_user_database.createNewFile();

		Config.load();

		Files.readAllLines(all_donor_codes.toPath()).forEach( val ->
				PlayerMeta.DonorCodes.add(val.replace("\"", "").trim())
		);

		Files.readAllLines(used_donor_codes.toPath()).forEach( val ->
				PlayerMeta.UsedDonorCodes.add(val)
		);

		Files.readAllLines(playtime_user_database.toPath()).forEach(val ->
				PlayerMeta.Playtimes.put(UUID.fromString(val.split(":")[0]), Double.parseDouble(val.split(":")[1]))
		);

	}
}
