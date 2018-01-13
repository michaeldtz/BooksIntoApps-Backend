package de.dietzm.booksintoapps.utils;

import java.util.UUID;

public class UUIDUtil {

	public static String generateUUID() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");
		return uuid;
	}

}
