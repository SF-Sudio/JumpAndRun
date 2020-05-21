package de.sebli.jnr;

import org.bukkit.entity.Player;

public class Language {

	public static String getLanguage() {
		String language = "none";

		String lang = JNR.getInstance().getConfig().getString("Language");

		if (lang.equalsIgnoreCase("en") || lang.equalsIgnoreCase("english") || lang.equalsIgnoreCase("englisch")
				|| lang.equalsIgnoreCase("eng") || lang.equalsIgnoreCase("us")) {
			language = "english";
		}

		if (lang.equalsIgnoreCase("de") || lang.equalsIgnoreCase("german") || lang.equalsIgnoreCase("deutsch")
				|| lang.equalsIgnoreCase("ger")) {
			language = "german";
		}

		return language;
	}

	public static void sendMessage(Player p, String msgEN, String msgDE) {
		if (getLanguage().equalsIgnoreCase("german")) {
			p.sendMessage(msgDE);
		} else {
			p.sendMessage(msgEN);
		}
	}

}
