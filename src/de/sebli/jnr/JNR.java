package de.sebli.jnr;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.sebli.jnr.commands.JNRCommand;
import de.sebli.jnr.listeners.ItemListener;
import de.sebli.jnr.listeners.StartListener;
import de.sebli.jnr.listeners.WinListener;
import net.milkbowl.vault.economy.Economy;

public class JNR extends JavaPlugin {

	public static Economy eco = null;

	private static JNR jnr;

	public static JNR getInstance() {
		return jnr;
	}

	public static String prefix = "§7[§6JumpAndRun§7] ";

	public static File ordner = new File("plugins//JumpAndRun");
	public static File file = new File("plugins//JumpAndRun//jnr.yml");
	public static YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

	public static File file2 = new File("plugins//JumpAndRun//playerInvs.yml");
	public static YamlConfiguration invs = YamlConfiguration.loadConfiguration(file2);

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

		if (data.getBoolean("EnableVault")) {
			try {
				setupEconomy();
			} catch (Exception e) {
				System.err.println("JumpAndRun: Vault konnte nicht geladen werden!");
			}
		}

		System.out.println("JumpAndRun: Plugin enabled!");
	}

	private void loadListeners() {
		Bukkit.getPluginManager().registerEvents(new StartListener(), this);
		Bukkit.getPluginManager().registerEvents(new WinListener(), this);
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
	}

	private void loadCommands() {
		getCommand("jnr").setExecutor(new JNRCommand());
	}

	private void loadFiles() {
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
				"Achtung! Wenn du 'EnableReloadWhilePlayerInGame' auf 'true' setzt (NICHT empfohlen!) und den Reload-Befehl nutzt während Spieler in einem JumpAndRun sind,"
						+ "\nkönnen diese die JumpAndRun Items nicht mehr benutzen und das Inventar des Spielers kann nicht mehr wiederhergestellt werden."
						+ "\n" + "\nBeachte, dass wenn du 'EnableCommandsWhileInGame' auf 'true' setzt,"
						+ "\ndie Spieler sich mit z.B. einem (wenn vorhanden) Warp- oder Spawn-Befehl aus dem JumpAndRun teleportieren können und die JumpAndRun Items somit behalten können.");

		if (!data.contains("EnableVault")) {
			data.set("EnableVault", false);
		}

		if (!data.contains("EnableReloadWhilePlayerInGame")) {
			data.set("EnableReloadWhilePlayerInGame", false);
		}

		if (!data.contains("EnableCommandsWhileInGame")) {
			data.set("EnableCommandsWhileInGame", false);
		}

		if (!data.contains("SendBroadcastAtNewRecord")) {
			data.set("SendBroadcastAtNewRecord", true);
		}

		if (!data.contains("NeedAllCheckpointsToWin")) {
			data.set("NeedAllCheckpointsToWin", true);
		}

		if (!data.contains("NewRecordWin")) {
			data.set("NewRecordWin", 0);
		}

		try {
			data.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Setting up the messages.yml file
		if (!messages.contains("Prefix")) {
			messages.set("Prefix", "&7[&6JumpAndRun&7] ");
		}

		if (!messages.contains("Messages.JoinSign.1")) {
			messages.set("Messages.JoinSign.1", "&0- &6JumpAndRun &0-");
		}

		if (!messages.contains("Messages.AlreadyInAJumpAndRun")) {
			messages.set("Messages.AlreadyInAJumpAndRun", "&cDu bist bereits in einem JumpAndRun! (%map%)");
		}

		if (!messages.contains("Messages.NoCommandsWhileInGame")) {
			messages.set("Messages.NoCommandsWhileInGame",
					"&cDu darfst während des JumpAndRun's keinen Befehl ausführen.");
		}

		if (!messages.contains("Messages.NoRealoadsWhileGameRunning")) {
			messages.set("Messages.NoRealoadsWhileGameRunning",
					"&cBitte nicht reloaden während Spieler ein JumpAndRun spielen!");
		}

		if (!messages.contains("Messages.LeftServerWhileInGame")) {
			messages.set("Messages.LeftServerWhileInGame",
					"&cDu hast den Server während eines JumpAndRun's verlassen. Dein altes Inventar wird jetzt wiederhergestellt...");
		}

		if (!messages.contains("Messages.InventoryRestored")) {
			messages.set("Messages.InventoryRestored", "&aDein Inventar wurde wiederhergestellt.");
		}

		if (!messages.contains("Messages.ItemCooldown")) {
			messages.set("Messages.ItemCooldown", "&cBitte warte kurz bis du das Item wieder benutzen kannst.");
		}

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

		if (!messages.contains("Messages.Win.Title.1")) {
			messages.set("Messages.Win.Title.1", "&aHerzlichen Glückwunsch!");
		}

		if (!messages.contains("Messages.Win.Title.2")) {
			messages.set("Messages.Win.Title.2", "&6%map% abgeschlossen.");
		}

		if (!messages.contains("Messages.Win.Title.Vault.1")) {
			messages.set("Messages.Win.Title.Vault.1", "&aHerzlichen Glückwunsch!");
		}

		if (!messages.contains("Messages.Win.Title.Vault.2")) {
			messages.set("Messages.Win.Title.Vault.2", "&6Dein Preis: %win%%moneyName%");
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

		if (!messages.contains("Messages.BonusForNewRecord.Vault")) {
			messages.set("Messages.BonusForNewRecord.Vault",
					"&aDu hast zusätzliche &6%bonus%%moneyName% &afür den neuen Rekord erhalten.");
		}

		try {
			messages.save(file4);
		} catch (IOException e) {
			e.printStackTrace();
		}

		prefix = messages.getString("Prefix").replaceAll("&", "§");
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			eco = economyProvider.getProvider();
		}

		return (eco != null);
	}

	public static String getMoneyName() {
		String mn = "$";
		if (data.contains("MoneyName")) {
			mn = data.getString("MoneyName");
		}
		return mn;
	}

}
