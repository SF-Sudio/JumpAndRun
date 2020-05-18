package de.sebli.jnr;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
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
	public static YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

	public static File file2 = new File("plugins//JumpAndRun//playerData.yml");
	public static YamlConfiguration playerData = YamlConfiguration.loadConfiguration(file2);

	public static File file3 = new File("plugins//JumpAndRun//stats.yml");
	public static YamlConfiguration stats = YamlConfiguration.loadConfiguration(file3);

	public static File file4 = new File("plugins//JumpAndRun//messages.yml");
	public static YamlConfiguration messages = YamlConfiguration.loadConfiguration(file4);

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

//		if (data.getBoolean("EnableVault")) {
//			try {
//				setupEconomy();
//			} catch (Exception e) {
//				System.err.println("JumpAndRun: Vault konnte nicht geladen werden!");
//			}
//		}

		checkPlayers();

		System.out.println("JumpAndRun: Plugin enabled!");
	}

	private void checkPlayers() {
		// TODO
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

		// Setting up the jnr.yml file
		data.options().header(
				"Die Einstellungen wurden in die config.yml verschoben! Hier werden nur noch die JumpAndRun-/JoinSchilder-Locations gespeichert.");

		getConfig().options().header(
				"Achtung! Wenn du 'EnableReloadWhilePlayerInGame' auf 'true' setzt (NICHT empfohlen!) und den Reload-Befehl nutzt während Spieler in einem JumpAndRun sind,"
						+ "\nkönnen diese die JumpAndRun Items nicht mehr benutzen und müssen neujoinen, um ihr altes Inventar wieder zu bekommen."
						+ "\n" + "\nBeachte, dass wenn du 'EnableCommandsWhileInGame' auf 'true' setzt,"
						+ "\ndie Spieler sich mit z.B. einem (wenn vorhanden) Warp- oder Spawn-Befehl aus dem JumpAndRun teleportieren können und die JumpAndRun Items somit behalten können.");

		getConfig().options().copyDefaults(true);

		getConfig().addDefault("EnableReloadWhilePlayerInGame", false);
		getConfig().addDefault("EnableCommandsWhileInGame", false);
		getConfig().addDefault("EnableFallDamage", false);
		getConfig().addDefault("EnableJoinCommand", true);
		getConfig().addDefault("NeedAllCheckpointsToWin", true);

//		if (!data.contains("EnableVault")) {
//			data.set("EnableVault", false);
//		}

		// INGAME ITEMS
		if (!data.contains("Item.BackToLastCheckpoint")) {
			data.set("Item.BackToLastCheckpoint", "331");
		}
		if (!data.contains("Item.HidePlayers")) {
			data.set("Item.HidePlayers", "348");
		}
		if (!data.contains("Item.ShowPlayers")) {
			data.set("Item.ShowPlayers", "289");
		}
		if (!data.contains("Item.Quit")) {
			data.set("Item.Quit", "341");
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
		messages.options().header(
				"Vault wird nicht mehr unterstützt. Nutze eine ältere Version, wenn du das Plugin mit Vault nutzen möchtest."
						+ "\nSchreibe ein 'x' statt einer Nachricht, um die Nachricht zu deaktivieren. (Der Spieler bekommt dann, wenn die Nachricht eigentlich erscheinen sollte, keine Nachricht mehr.)"
						+ "\nWenn du einen Titel ('Title') deaktivieren möchtest, musst du beide Felder ('1' und '2') frei lassen, dann wird dem Spieler der Titel nicht mehr angezeigt.");

		// PREFIX
		if (!messages.contains("Prefix")) {
			messages.set("Prefix", "&7[&6JumpAndRun&7] ");
		}

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
			messages.set("Messages.JoinTitle.2", "");
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

//		if (!messages.contains("Messages.Win.Title.Vault.1")) {
//			messages.set("Messages.Win.Title.Vault.1", "&aHerzlichen Glückwunsch!");
//		}
//
//		if (!messages.contains("Messages.Win.Title.Vault.2")) {
//			messages.set("Messages.Win.Title.Vault.2", "&6Dein Preis: %win%%moneyName%");
//		}

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

//		if (!messages.contains("Messages.BonusForNewRecord.Vault")) {
//			messages.set("Messages.BonusForNewRecord.Vault",
//					"&aDu hast zusätzliche &6%bonus%%moneyName% &afür den neuen Rekord erhalten.");
//		}

		try {
			messages.save(file4);
		} catch (IOException e) {
			e.printStackTrace();
		}

		prefix = messages.getString("Prefix").replaceAll("&", "§");
	}

//	public boolean setupEconomy() {
//		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
//				.getRegistration(net.milkbowl.vault.economy.Economy.class);
//		if (economyProvider != null) {
//			eco = economyProvider.getProvider();
//		}
//
//		return (eco != null);
//	}

//	public static String getMoneyName() {
//		String mn = "$";
//		if (data.contains("MoneyName")) {
//			mn = data.getString("MoneyName");
//		}
//		return mn;
//	}

}
