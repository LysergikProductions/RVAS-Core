package protocol3.backend;

import java.util.LinkedHashMap;
import java.util.Map;

/* least recently used cache */
public class LruCache<K, V> extends LinkedHashMap<K, V> {
	private int cacheSize;

	public LruCache(int cacheSize)
	{
		super(cacheSize, 0.75f, true);
		this.cacheSize = cacheSize;
	}

	protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
	{
		return size() >= cacheSize;
	}
}
