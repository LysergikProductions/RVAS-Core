package core.backend;

import core.events.*;
import core.tasks.Analytics;
import core.tasks.AutoAnnouncer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Config {

	public final static int version = 42;
	private final static HashMap<String, String> _values = new HashMap<>();

	public static boolean debug = Boolean.parseBoolean(getValue("debug"));
	public static boolean verbose = Boolean.parseBoolean(getValue("verbose"));

	public static String getValue(String key) { return _values.getOrDefault(key, "false"); }

	public static boolean exists(String thisConfig) { return _values.containsKey(thisConfig); }

	public static void modify(@NotNull String thisConfig, @NotNull String thisValue) {
		if (thisConfig.trim().isEmpty() || thisValue.trim().isEmpty()) return;

		_values.remove(thisConfig); _values.put(thisConfig, thisValue);

		if (debug) System.out.println(
				"[core.backend.config.modify] Result: " + _values.get(thisConfig));
	}

	public static void load() throws IOException {
		Files.readAllLines(Paths.get("plugins/core/configs/config.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val -> {

			try { _values.put(val.split("=")[0].trim(), val.split("=")[1].trim());
			} catch (Exception e) {
				System.out.println("Failed to store value for " + val.split("=")[0].trim());
				System.out.println(e.getMessage());
			}
		});

		Files.readAllLines(Paths.get("plugins/core/configs/restrictions.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val -> {

			try { _values.put(val.split("=")[0].trim(), val.split("=")[1].trim());
			} catch (Exception e) {
				System.out.println("Failed to store value for " + val.split("=")[0].trim());
				System.out.println(e.getMessage());
			}
		});

		Files.readAllLines(Paths.get("plugins/core/configs/spawn_controller.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val -> {

			try { _values.put(val.split("=")[0].trim(), val.split("=")[1].trim());
			} catch (Exception e) {
				System.out.println("Failed to store value for " + val.split("=")[0].trim());
				System.out.println(e.getMessage());
			}
		});

		debug = Boolean.parseBoolean(getValue("debug"));
		verbose = Boolean.parseBoolean(getValue("verbose"));
		OpListener.isSauceInitialized = false;

		if (BlockListener.init() && debug) System.out.println("BlockListener sConfigs Updated!");
		if (Analytics.init() && debug) System.out.println("Analytics sConfigs Updated!");
		if (SpawnController.init() && debug) System.out.println("SpawnController sConfigs Updated!");
		if (ItemCheck.init() && debug) System.out.println("Banned Block sConfigs Updated!");
		if (ConnectionController.init() && debug) System.out.println("MOTDs Updated!");
		if (AutoAnnouncer.init() && debug) System.out.println("Announcements Updated!");

		System.out.println("Configs updated!");
	}
}
