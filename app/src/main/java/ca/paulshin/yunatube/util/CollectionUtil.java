package ca.paulshin.yunatube.util;

import java.util.Collection;

/**
 * Created by paulshin on 16-02-16.
 */
public class CollectionUtil {
	public static <E> boolean isEmpty(Collection<E> collection) {
		if (collection == null || collection.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
