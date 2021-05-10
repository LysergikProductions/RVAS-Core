package core.backend;

import core.commands.Prison;
import core.events.BlockListener;
import core.events.SpawnController;
import core.tasks.Analytics;
import core.tasks.LagManager;
import core.tasks.OnTick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Config {

	private static HashMap<String, String> _values = new HashMap<>();

	public static int version = 22;

	public static String getValue(String key)
	{
		return _values.getOrDefault(key, "false");
	}

	public static void load() throws IOException {
		boolean verbose = Boolean.parseBoolean(Config.getValue("verbose"));

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
		
		if (BlockListener.updateConfigs() && verbose) System.out.println("BlockListener sConfigs Updated!");
		if (BlockListener.updateConfigs() && verbose) System.out.println("ChunkListener sConfigs Updated!");
		if (Analytics.updateConfigs() && verbose) System.out.println("Analytics sConfigs Updated!");
		if (LagManager.updateConfigs() && verbose) System.out.println("LagManager sConfigs Updated!");
		if (Prison.updateConfigs() && verbose) System.out.println("Prison sConfigs Updated!");
		if (NoGhost.updateConfigs() && verbose) System.out.println("NoGhost sConfigs Updated!");
		if (OnTick.updateConfigs() && verbose) System.out.println("OnTick sConfigs Updated!");
		if (SpawnController.updateConfigs() && verbose) System.out.println("SpawnController sConfigs Updated!");

		System.out.println("Configs updated!");
	}
}
