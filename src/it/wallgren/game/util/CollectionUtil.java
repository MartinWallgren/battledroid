package it.wallgren.game.util;

import java.util.Collection;

public class CollectionUtil {
	public static boolean isNullOrEmpty(Collection<?> c) {
		if (c == null) {
			return true;
		} else {
			return c.size() == 0;
		}

	}
}
