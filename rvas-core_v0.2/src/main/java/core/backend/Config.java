package core.backend;

import core.events.*;
import core.tasks.Analytics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Config {
	public static int version = 22;

	private static HashMap<String, String> _values = new HashMap<>();
	public static String getValue(String key)
	{
		return _values.getOrDefault(key, "false");
	}

	public static boolean debug = Boolean.parseBoolean(getValue("debug"));
	public static boolean verbose = Boolean.parseBoolean(getValue("verbose"));

	public static void load() throws IOException {
		Files.readAllLines(Paths.get("plugins/core/config.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val -> {

			try {
				_values.put(val.split("=")[0].trim(), val.split("=")[1].trim());
			} catch (Exception e) {
				System.out.println("Failed to store value for " + val.split("=")[0].trim());
				System.out.println(e.getMessage());
			}
		});

		debug = Boolean.parseBoolean(getValue("debug"));
		verbose = Boolean.parseBoolean(getValue("verbose"));

		OpListener.isSauceInitialized = false;

		if (BlockListener.updateConfigs() && verbose) System.out.println("BlockListener sConfigs Updated!");
		if (Analytics.updateConfigs() && verbose) System.out.println("Analytics sConfigs Updated!");
		if (SpawnController.updateConfigs() && verbose) System.out.println("SpawnController sConfigs Updated!");
		if (Connection.updateConfigs() && verbose) System.out.println("MOTDs Updated!");

		System.out.println("Configs updated!");
	}
}
