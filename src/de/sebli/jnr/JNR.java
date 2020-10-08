package de.sebli.jnr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.sebli.jnr.commands.JNRCommand;
import de.sebli.jnr.listeners.DamageListener;
import de.sebli.jnr.listeners.ItemListener;
import de.sebli.jnr.listeners.StartListener;
import de.sebli.jnr.listeners.WinListener;

public class JNR extends JavaPlugin {

//	public static Economy eco = null;

	private static JNR jnr;

	public static JNR getInstance() {
		return jnr;
	}

	public static String prefix = "§7[§6JumpAndRun§7] ";

	public static File ordner = new File("plugins//JumpAndRun");

	public static File file = new File("plugins//JumpAndRun//jnr.yml");
	public static YamlConfiguration data = null;

	public static File file2 = new File("plugins//JumpAndRun//playerData.yml");
	public static YamlConfiguration playerData = null;

	public static File file3 = new File("plugins//JumpAndRun//stats.yml");
	public static YamlConfiguration stats = null;

	public static File file4 = new File("plugins//JumpAndRun//messages.yml");
	public static YamlConfiguration messages = null;

	@Override
	public void onDisable() {
		System.out.println("JumpAndRun: Plugin disabled!");
	}

	@Override
	public void onEnable() {
		jnr = this;

		loadCommands();
		loadListeners();
		loadFiles();

		checkPlayers();

		StartListener.startTimer();

		System.out.println("JumpAndRun: Plugin enabled!");

		checkVersion();
	}

	private void checkPlayers() {
		// TODO
	}

	private void checkVersion() {
		System.out.println("JumpAndRun: Searching for updates...");

		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			int resourceID = 78123;
			try (InputStream inputStream = (new URL(
					"https://api.spigotmc.org/legacy/update.php?resource=" + resourceID)).openStream();
					Scanner scanner = new Scanner(inputStream)) {
				if (scanner.hasNext()) {
					String latest = scanner.next();
					String current = getDescription().getVersion();

					int late = Integer.parseInt(latest.replaceAll("\\.", ""));
					int curr = Integer.parseInt(current.replaceAll("\\.", ""));

					if (curr >= late) {
						System.out.println("JumpAndRun: No updates found. The server is running the latest version.");
					} else {
						System.out.println("");
						System.out.println("JumpAndRun: There is a newer version available - " + latest
								+ ", you are on - " + current);
						System.out.println(
								"JumpAndRun: Please download the latest version - https://www.spigotmc.org/resources/"
										+ resourceID);
						System.out.println("");
					}
				}
			} catch (IOException exception) {
				System.err.println("JumpAndRun: Cannot search for updates - " + exception.getMessage());
			}
		});

	}

	private void loadListeners() {
		Bukkit.getPluginManager().registerEvents(new StartListener(), this);
		Bukkit.getPluginManager().registerEvents(new WinListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
	}

	private void loadCommands() {
		getCommand("jnr").setExecutor(new JNRCommand());
	}

	public void loadFiles() {
		if (!ordner.exists()) {
			ordner.mkdir();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!file2.exists()) {
			try {
				file2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!file3.exists()) {
			try {
				file3.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!file4.exists()) {
			try {
				file4.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		data = YamlConfiguration.loadConfiguration(file);
		playerData = YamlConfiguration.loadConfiguration(file2);
		stats = YamlConfiguration.loadConfiguration(file3);
		messages = YamlConfiguration.loadConfiguration(file4);

		getConfig().options().copyDefaults(true);

		getConfig().addDefault("Language", "english");
		getConfig().addDefault("EnableReloadWhilePlayerInGame", false);
		getConfig().addDefault("EnableCommandsWhileInGame", false);
		getConfig().addDefault("EnableFallDamage", false);
		getConfig().addDefault("EnableJoinCommand", true);
		getConfig().addDefault("EnableStartCountdown", true);
		getConfig().addDefault("EnableItemCooldown", true);
		getConfig().addDefault("ResetHeight", -1);
		getConfig().addDefault("NeedAllCheckpointsToWin", true);

		// setting up the config files
		if (Language.getLanguage().equalsIgnoreCase("german")) {
			data.options().header(
					"Die Einstellungen wurden in die config.yml verschoben! Hier werden nur noch die JumpAndRun-/JoinSchilder-Locations gespeichert.");

			getConfig().options().header(
					"Achtung! Wenn du 'EnableReloadWhilePlayerInGame' auf 'true' setzt (NICHT empfohlen!) und den Reload-Befehl nutzt während Spieler in einem JumpAndRun sind,"
							+ "\nkönnen diese die JumpAndRun Items nicht mehr benutzen und müssen neujoinen, um ihr altes Inventar wieder zu bekommen."
							+ "\n" + "\nBeachte, dass wenn du 'EnableCommandsWhileInGame' auf 'true' setzt,"
							+ "\ndie Spieler sich mit z.B. einem (wenn vorhanden) Warp- oder Spawn-Befehl aus dem JumpAndRun teleportieren können und die JumpAndRun Items somit behalten können.");
		} else {
			data.options().header(
					"The settings were moved to the config.yml! Only the JumpAndRun-/JoinSign-Locations will be saved in this file.");

			getConfig().options().header(
					"Attention! If you set 'EnableReloadWhilePlayerInGame' to 'true' (NOT recommended!) and you use the reload-command while players are in a JumpAndRun,"
							+ "\nthe players can no longer use the JumpAndRun-Items and must rejoin to get their old inventory contents back."
							+ "\n" + "\nNotice that if you set 'EnableCommandsWhileInGame' to 'true',"
							+ "\nthe players can teleport theirself, with a warp- or spawn-command (if available), out of the JumpAndRun although they are still in the JumpAndRun-Mode with the JumpAndRun-Items.");
		}

//		if (!data.contains("EnableVault")) {
//			data.set("EnableVault", false);
//		}

		// INGAME ITEMS
		if (!data.contains("Item.BackToLastCheckpoint")) {
			data.set("Item.BackToLastCheckpoint", "REDSTONE");
		}
		if (!data.contains("Item.HidePlayers")) {
			data.set("Item.HidePlayers", "INK_SACK:10");
		}
		if (!data.contains("Item.ShowPlayers")) {
			data.set("Item.ShowPlayers", "INK_SACK:8");
		}
		if (!data.contains("Item.Quit")) {
			data.set("Item.Quit", "SLIME_BALL");
		}

		// MOVING SETTINGS FROM THE DATA.YML TO THE CONFIG.YML
		if (data.contains("EnableReloadWhilePlayerInGame")) {
			getConfig().set("EnableReloadWhilePlayerInGame", data.getBoolean("EnableReloadWhilePlayerInGame"));

			data.set("EnableReloadWhilePlayerInGame", null);
		}
		if (data.contains("EnableCommandsWhileInGame")) {
			getConfig().set("EnableCommandsWhileInGame", data.getBoolean("EnableCommandsWhileInGame"));

			data.set("EnableCommandsWhileInGame", null);
		}
		if (data.contains("EnableJoinCommand")) {
			getConfig().set("EnableJoinCommand", data.getBoolean("EnableJoinCommand"));

			data.set("EnableJoinCommand", null);
		}
		if (data.contains("SendBroadcastAtNewRecord")) {
			data.set("SendBroadcastAtNewRecord", null);
		}
		if (data.contains("NeedAllCheckpointsToWin")) {
			getConfig().set("NeedAllCheckpointsToWin", data.getBoolean("NeedAllCheckpointsToWin"));

			data.set("NeedAllCheckpointsToWin", null);
		}

//		if (!data.contains("NewRecordWin")) {
//			data.set("NewRecordWin", 0);
//		}

		saveConfig();

		try {
			data.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Setting up the messages.yml file
		if (Language.getLanguage().equalsIgnoreCase("german")) {
			messages.options().header(
					"Schreibe ein 'x' statt einer Nachricht, um die Nachricht zu deaktivieren. (Der Spieler bekommt dann, wenn die Nachricht eigentlich erscheinen sollte, keine Nachricht mehr.)"
							+ "\nWenn du einen Titel ('Title') deaktivieren möchtest, musst du beide Felder ('1' und '2') frei lassen, dann wird dem Spieler der Titel nicht mehr angezeigt."
							+ "\nBei 'Messages.Win.Stats' kannst du beliebig viele Nachrichten hinzufügen (einfach das Format beibehalten, d.h. für jede Nachricht eine extra Zeile mit '-' + Nachricht).");
		} else {
			messages.options().header("Write an 'x' instead of an message to disable the message."
					+ "\nIf you want to disable a Title message, just leave the fields ('1' and '2') blank, so the title will not be displayed to the player anymore."
					+ "\nAt 'Messages.Win.Stats' you can write as many messages as you want (you must only keep the format, so for every message you need a new line which starts with '-').");
		}

		// PREFIX
		if (!messages.contains("Prefix")) {
			messages.set("Prefix", "&7[&6JumpAndRun&7] ");
		}

		if (Language.getLanguage().equalsIgnoreCase("german")) {
			// ITEM NAMES
			if (!messages.contains("Item.BackToLastCheckpoint.Name")) {
				messages.set("Item.BackToLastCheckpoint.Name", "&cZum letzten Checkpoint");
			}
			if (!messages.contains("Item.HidePlayers.Name")) {
				messages.set("Item.HidePlayers.Name", "&cSpieler verstecken");
			}
			if (!messages.contains("Item.ShowPlayers.Name")) {
				messages.set("Item.ShowPlayers.Name", "&aSpieler anzeigen");
			}
			if (!messages.contains("Item.Quit.Name")) {
				messages.set("Item.Quit.Name", "&cVerlassen");
			}

			// SIGN
			if (!messages.contains("Messages.JoinSign.1")) {
				messages.set("Messages.JoinSign.1", "&0- &6JumpAndRun &0-");
			}

			// MESSAGES AND TITLES
			if (!messages.contains("Messages.JoinTitle.1")) {
				messages.set("Messages.JoinTitle.1", "%map%");
			}
			if (!messages.contains("Messages.JoinTitle.2")) {
				messages.set("Messages.JoinTitle.2", "&aStartet in: §6%timer%");
			}
			if (!messages.contains("Messages.AlreadyInAJumpAndRun")) {
				messages.set("Messages.AlreadyInAJumpAndRun", "&cDu bist bereits in einem JumpAndRun! (%map%)");
			}
			if (!messages.contains("Messages.JoinCooldown")) {
				messages.set("Messages.JoinCooldown", "&cBitte warte kurz, bevor du wieder in ein JumpAndRun kannst.");
			}

			// ACTIONBAR
			if (!messages.contains("Messages.ActionBar")) {
				messages.set("Messages.ActionBar", "&a%map%: §6%time% &r&7| &aDeine Fails: &c&l%fails%");
			}

			// ERROR MESSAGES
			if (!messages.contains("Messages.NoCommandsWhileInGame")) {
				messages.set("Messages.NoCommandsWhileInGame",
						"&cDu darfst während des JumpAndRun's keinen Befehl ausführen.");
			}
			if (!messages.contains("Messages.NoRealoadsWhileGameRunning")) {
				messages.set("Messages.NoRealoadsWhileGameRunning",
						"&cBitte nicht reloaden während Spieler ein JumpAndRun spielen!");
			}
//		if (!messages.contains("Messages.LeftServerWhileInGame")) {
//			messages.set("Messages.LeftServerWhileInGame",
//					"&cDu hast den Server während eines JumpAndRun's verlassen. Dein altes Inventar wird jetzt wiederhergestellt...");
//		}
//		if (!messages.contains("Messages.InventoryRestored")) {
//			messages.set("Messages.InventoryRestored", "&aDein Inventar wurde wiederhergestellt.");
//		}
			if (!messages.contains("Messages.ItemCooldown")) {
				messages.set("Messages.ItemCooldown", "&cBitte warte kurz bis du das Item wieder benutzen kannst.");
			}

			// CHECKPOINT MESSAGES
			if (!messages.contains("Messages.CheckpointReached.Time")) {
				messages.set("Messages.CheckpointReached.Time", "&7Deine Zwischenzeit: &6&l%time%");
			}
			if (!messages.contains("Messages.CheckpointReached.Title.1")) {
				messages.set("Messages.CheckpointReached.Title.1", "");
			}
			if (!messages.contains("Messages.CheckpointReached.Title.2")) {
				messages.set("Messages.CheckpointReached.Title.2", "&aCheckpoint #%checkpoint% erreicht!");
			}
			if (!messages.contains("Messages.NotAllCheckpoints")) {
				messages.set("Messages.NotAllCheckpoints",
						"&cDu hast nicht alle Checkpoints erreicht! Kehre zum letzten Checkpoint zurück.");
			}

			// WIN MESSAGES
			if (!messages.contains("Messages.Win.Title.1")) {
				messages.set("Messages.Win.Title.1", "&aHerzlichen Glückwunsch!");
			}
			if (!messages.contains("Messages.Win.Title.2")) {
				messages.set("Messages.Win.Title.2", "&6%map% abgeschlossen.");
			}
			if (!messages.contains("Messages.Win.Stats")) {
				List<String> list = new ArrayList<>();
				list.add("&8===============");
				list.add("");
				list.add("&aMap&7: &6%map%");
				list.add("&aZeit&7: %time%");
				list.add("&aFails&7: &6%fails%");
				list.add("");
				list.add("&8===============");

				messages.set("Messages.Win.Stats", list);
			}

			if (!messages.contains("Messages.NewPersonalRecord.Title.1")) {
				messages.set("Messages.NewPersonalRecord.Title.1", "&a&lNeuer persönlicher Rekord!");
			}
			if (!messages.contains("Messages.NewPersonalRecord.Title.2")) {
				messages.set("Messages.NewPersonalRecord.Title.2", "Map: %map%");
			}
			if (!messages.contains("Messages.NewGlobalRecord.Title.1")) {
				messages.set("Messages.NewGlobalRecord.Title.1", "&a&lNeuer globaler Rekord!");
			}
			if (!messages.contains("Messages.NewGlobalRecord.Title.2")) {
				messages.set("Messages.NewGlobalRecord.Title.2", "Map: %map%");
			}
			if (!messages.contains("Messages.NewGlobalRecord.Broadcast")) {
				messages.set("Messages.NewGlobalRecord.Broadcast",
						"&a%player% &6hat einen neuen Rekord &7(&a%time%&7) &6auf der Map &a%map% &6aufgestellt!");
			}

			// COMMAND MESSAGES
			if (!messages.contains("Messages.Command.Stats.Player")) {
				List<String> list = new ArrayList<>();
				list.add("&8======&6JumpAndRun&8======");
				list.add("");
				list.add("&7JumpAndRun: &6%map%");
				list.add("&7Angefangene Runden: &6%playedTimes%");
				list.add("&7Abgeschlossene Runden: &6%finishedTimes%");
				list.add("&7Fails: &6%fails%");
				list.add("&7Rekordzeit: &6%recordTime%");
				list.add("&7Globale Rekordzeit: &6%globalRecordTime%");
				list.add("");
				list.add("&8=====&9Stats von &c%player%&8=====");

				messages.set("Messages.Command.Stats.Player", list);
			}
			if (!messages.contains("Messages.Command.Stats.NoRecord")) {
				messages.set("Messages.Command.Stats.NoRecord",
						"&6Du hast noch keinen Rekord auf dieser Map aufgestellt.");
			}
			if (!messages.contains("Messages.Command.Stats.NoGlobalRecord")) {
				messages.set("Messages.Command.Stats.NoGlobalRecord",
						"&6Es wurde auf dieser Map noch kein Rekord aufgestellt.");
			}
		} else {
			// ITEM NAMES
			if (!messages.contains("Item.BackToLastCheckpoint.Name")) {
				messages.set("Item.BackToLastCheckpoint.Name", "&cBack to last checkpoint");
			}
			if (!messages.contains("Item.HidePlayers.Name")) {
				messages.set("Item.HidePlayers.Name", "&cHide players");
			}
			if (!messages.contains("Item.ShowPlayers.Name")) {
				messages.set("Item.ShowPlayers.Name", "&aShow players");
			}
			if (!messages.contains("Item.Quit.Name")) {
				messages.set("Item.Quit.Name", "&cQuit");
			}

			// SIGN
			if (!messages.contains("Messages.JoinSign.1")) {
				messages.set("Messages.JoinSign.1", "&0- &6JumpAndRun &0-");
			}

			// MESSAGES AND TITLES
			if (!messages.contains("Messages.JoinTitle.1")) {
				messages.set("Messages.JoinTitle.1", "%map%");
			}
			if (!messages.contains("Messages.JoinTitle.2")) {
				messages.set("Messages.JoinTitle.2", "&aStarting in: &6%timer%");
			}
			if (!messages.contains("Messages.AlreadyInAJumpAndRun")) {
				messages.set("Messages.AlreadyInAJumpAndRun", "&cYou are already in a JumpAndRun! (%map%)");
			}
			if (!messages.contains("Messages.JoinCooldown")) {
				messages.set("Messages.JoinCooldown", "&cPlease wait before you can play the next JumpAndRun.");
			}

			// ACTIONBAR
			if (!messages.contains("Messages.ActionBar")) {
				messages.set("Messages.ActionBar", "&a%map%: §6%time% &r&7| &aFails: &c&l%fails%");
			}

			// ERROR MESSAGES
			if (!messages.contains("Messages.NoCommandsWhileInGame")) {
				messages.set("Messages.NoCommandsWhileInGame", "&cYou can not execute commands during JumpAndRun.");
			}
			if (!messages.contains("Messages.NoRealoadsWhileGameRunning")) {
				messages.set("Messages.NoRealoadsWhileGameRunning",
						"&cPlease do not reload while players are in a JumpAndRun!");
			}
			if (!messages.contains("Messages.ItemCooldown")) {
				messages.set("Messages.ItemCooldown", "&cPlease wait before you can use that item again.");
			}

			// CHECKPOINT MESSAGES
			if (!messages.contains("Messages.CheckpointReached.Time")) {
				messages.set("Messages.CheckpointReached.Time", "&7Your time: &6&l%time%");
			}
			if (!messages.contains("Messages.CheckpointReached.Title.1")) {
				messages.set("Messages.CheckpointReached.Title.1", "");
			}
			if (!messages.contains("Messages.CheckpointReached.Title.2")) {
				messages.set("Messages.CheckpointReached.Title.2", "&aCheckpoint #%checkpoint% reached!");
			}
			if (!messages.contains("Messages.NotAllCheckpoints")) {
				messages.set("Messages.NotAllCheckpoints",
						"&cYou need all checkpoint to finish this JumpAndRun! Go back to the last checkpoint.");
			}

			// WIN MESSAGES
			if (!messages.contains("Messages.Win.Title.1")) {
				messages.set("Messages.Win.Title.1", "&aCongratulations!");
			}
			if (!messages.contains("Messages.Win.Title.2")) {
				messages.set("Messages.Win.Title.2", "&6%map% completed.");
			}
			if (!messages.contains("Messages.Win.Stats")) {
				List<String> list = new ArrayList<>();
				list.add("&8===============");
				list.add("");
				list.add("&aMap&7: &6%map%");
				list.add("&aTime&7: %time%");
				list.add("&aFails&7: &6%fails%");
				list.add("");
				list.add("&8===============");

				messages.set("Messages.Win.Stats", list);
			}

			if (!messages.contains("Messages.NewPersonalRecord.Title.1")) {
				messages.set("Messages.NewPersonalRecord.Title.1", "&a&lNew personal record!");
			}
			if (!messages.contains("Messages.NewPersonalRecord.Title.2")) {
				messages.set("Messages.NewPersonalRecord.Title.2", "Map: %map%");
			}
			if (!messages.contains("Messages.NewGlobalRecord.Title.1")) {
				messages.set("Messages.NewGlobalRecord.Title.1", "&a&lNew global record!");
			}
			if (!messages.contains("Messages.NewGlobalRecord.Title.2")) {
				messages.set("Messages.NewGlobalRecord.Title.2", "Map: %map%");
			}
			if (!messages.contains("Messages.NewGlobalRecord.Broadcast")) {
				messages.set("Messages.NewGlobalRecord.Broadcast",
						"&a%player% &6sets a new record &7(&a%time%&7) &6on the map &a%map%&6!");
			}

			// COMMAND MESSAGES
			if (!messages.contains("Messages.Command.Stats.Player")) {
				List<String> list = new ArrayList<>();
				list.add("&8======&6JumpAndRun&8======");
				list.add("");
				list.add("&7JumpAndRun: &6%map%");
				list.add("&7Started JumpAndRuns: &6%playedTimes%");
				list.add("&7Completed JumpAndRuns: &6%finishedTimes%");
				list.add("&7Fails: &6%fails%");
				list.add("&7Record time: &6%recordTime%");
				list.add("&7Global record time: &6%globalRecordTime%");
				list.add("");
				list.add("&8=====&9Stats of &c%player%&8=====");

				messages.set("Messages.Command.Stats.Player", list);
			}
			if (!messages.contains("Messages.Command.Stats.NoRecord")) {
				messages.set("Messages.Command.Stats.NoRecord", "&6You have no record on this map yet.");
			}
			if (!messages.contains("Messages.Command.Stats.NoGlobalRecord")) {
				messages.set("Messages.Command.Stats.NoGlobalRecord", "&6There is no record for this map.");
			}
		}

		try {
			messages.save(file4);
		} catch (IOException e) {
			e.printStackTrace();
		}

		prefix = messages.getString("Prefix").replaceAll("&", "§");
	}

	public void changeLanguage() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (StartListener.playing.containsKey(all.getName())) {
				WinListener.reset(all);

				Language.sendMessage(all,
						prefix + "§cYou have been kicked out of the JumpAndRun, because the language was changed.",
						prefix + "§cDu wurdest aus dem JumpAndRun gekickt, weil die Sprache geändert wurde.");
			}
		}

		File fileBackup = new File("plugins//JumpAndRun//messagesOLD.yml");
		try {
			fileBackup.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		YamlConfiguration backup = YamlConfiguration.loadConfiguration(file4);

		try {
			backup.save(fileBackup);
		} catch (IOException e) {
			e.printStackTrace();
		}

		data = null;
		messages = null;
		playerData = null;
		stats = null;
		reloadConfig();

		file4.delete();

		loadFiles();
	}

}
