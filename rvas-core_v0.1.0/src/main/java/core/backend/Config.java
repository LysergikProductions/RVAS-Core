package protocol3.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Config {

	private static HashMap<String, String> _values = new HashMap<String, String>();

	public static int version = 22;

	public static String getValue(String key)
	{
		return _values.getOrDefault(key, "false");
	}

	public static void load() throws IOException {
		Files.readAllLines(Paths.get("plugins/protocol3/config.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val ->
					_values.put(val.split("=")[0].trim(), val.split("=")[1].trim()));
	}
}
