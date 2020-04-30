package de.sebli.jnr.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.sebli.jnr.JNR;

public class JNRCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Du musst ein Spieler sein!");
		} else {
			Player p = (Player) sender;
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("stats")) {
					String map = args[1];
					getStats(p, p.getName(), map);
				} else {
					if (p.hasPermission("jnr.admin")) {
						if (args[0].equalsIgnoreCase("setextrawin")) {
							try {
								int eWin = Integer.valueOf(args[1]);

								JNR.data.set("NewRecordWin", eWin);
								saveFile(p, "§7Gewinn für einen neuen Rekord auf §a" + eWin + " §7gesetzt.");
							} catch (Exception ex) {
								p.sendMessage(JNR.prefix
										+ "§cEs ist ein Fehler aufgetreten. Bitte gebe einen gültigen Wert ein!");
							}
						} else if (args[0].equalsIgnoreCase("setmoney")) {
							String mn = args[1];
							JNR.data.set("MoneyName", mn);
							saveFile(p, "§7Währungsname -> " + mn);
						} else if (args[0].equalsIgnoreCase("setblock")) {
							int block = p.getItemInHand().getType().getId();
							if (args[1].equalsIgnoreCase("win")) {
								JNR.data.set("WinBlock", block);
								saveFile(p, "§7Win-Block");
							} else if (args[1].equalsIgnoreCase("checkpoint")) {
								JNR.data.set("CheckpointBlock", block);
								saveFile(p, "§7Checkpoint-Block");
							}
						} else if (args[0].equalsIgnoreCase("create")) {
							String name = args[1];
							if (!JNR.data.contains(name)) {
								JNR.data.set(name, "true");
								saveFile(p, "§7JumpAndRun §a" + name + " §7erstellt.");
								p.sendMessage("§4§lBitte vervollständige nun das Setup für dieses JumpAndRun.");
							} else {
								p.sendMessage(JNR.prefix + "§cEs gibt bereits ein JumpAndRun mit dem Namen '§e" + name
										+ "§c'");
							}
						} else if (args[0].equalsIgnoreCase("setspawn")) {
							String name = args[1];

							if (JNR.data.contains(name)) {
								Location loc = p.getLocation();

								String world = loc.getWorld().getName();
								double x = loc.getX();
								double y = loc.getY();
								double z = loc.getZ();
								float yaw = loc.getYaw();
								float pitch = loc.getPitch();

								JNR.data.set(name + ".World", world);
								JNR.data.set(name + ".X", x);
								JNR.data.set(name + ".Y", y);
								JNR.data.set(name + ".Z", z);
								JNR.data.set(name + ".Yaw", yaw);
								JNR.data.set(name + ".Pitch", pitch);

								saveFile(p, "§7Spawn-Punkt gesetzt.");
							} else {
								p.sendMessage(JNR.prefix + "§cEin JumpAndRun mit dem Namen '§e" + name
										+ "§c' existiert nicht!");
								p.sendMessage(JNR.prefix + "§cBenutze: /jnr create " + name
										+ " §cum das JumpAndRun zu erstellen!");
							}
						} else if (args[0].equalsIgnoreCase("setleave")) {
							String name = args[1];

							if (JNR.data.contains(name)) {
								Location loc = p.getLocation();

								String world = loc.getWorld().getName();
								double x = loc.getX();
								double y = loc.getY();
								double z = loc.getZ();
								float yaw = loc.getYaw();
								float pitch = loc.getPitch();

								JNR.data.set(name + ".Leave.World", world);
								JNR.data.set(name + ".Leave.X", x);
								JNR.data.set(name + ".Leave.Y", y);
								JNR.data.set(name + ".Leave.Z", z);
								JNR.data.set(name + ".Leave.Yaw", yaw);
								JNR.data.set(name + ".Leave.Pitch", pitch);

								saveFile(p, "§7Leave-Punkt gesetzt.");
							} else {
								p.sendMessage(JNR.prefix + "§cEin JumpAndRun mit dem Namen '§e" + name
										+ "§c' existiert nicht!");
								p.sendMessage(JNR.prefix + "§cBenutze: /jnr create " + name
										+ " §cum das JumpAndRun zu erstellen!");
							}
						} else if (args[0].equalsIgnoreCase("resetall")) {
							String pName = args[1];

							JNR.stats.set(pName, null);
							try {
								JNR.stats.save(JNR.file3);
							} catch (IOException e) {
								e.printStackTrace();
							}
							p.sendMessage(
									"§7Du hast die gesamten JumpAndRun Stats von §e" + pName + " §czurückgesetzt§7.");
						} else if (args[0].equalsIgnoreCase("allcpstowin")) {
							if (args[1].equalsIgnoreCase("true")) {
								JNR.data.set("NeedAllCheckpointsToWin", true);
								saveFile(p, "§7NeedAllCheckpointsToWin -> true");
							} else if (args[1].equalsIgnoreCase("false")) {
								JNR.data.set("NeedAllCheckpointsToWin", false);
								saveFile(p, "§7NeedAllCheckpointsToWin -> false");
							} else {
								sendHelp(p);
							}
						} else {
							sendHelp(p);
						}
					}
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("stats")) {
					String pName = args[1];
					String map = args[2];
					getStats(p, pName, map);
				} else {
					if (p.hasPermission("jnr.admin")) {
						if (args[0].equalsIgnoreCase("setwin")) {
							try {
								String name = args[1];
								int amount = Integer.valueOf(args[2]);

								if (JNR.data.contains(name)) {
									JNR.data.set(name + ".Win", amount);
									saveFile(p, "§7Gewinn für §a" + args[1] + " auf §6" + amount + JNR.getMoneyName()
											+ " §7gesetzt.");
								} else {
									p.sendMessage(JNR.prefix + "§cEin JumpAndRun mit dem Namen '§e" + name
											+ "§c' existiert nicht!");
									p.sendMessage(JNR.prefix + "§cBenutze: /jnr create " + name
											+ " §cum das JumpAndRun zu erstellen!");
								}
							} catch (Exception e) {
								p.sendMessage(JNR.prefix + "§cEin Fehler ist aufgetreten!");
							}
						} else if (args[0].equalsIgnoreCase("setcp")) {
							String name = args[1];

							if (JNR.data.contains(name)) {
								try {
									Location loc = p.getLocation();

									String world = loc.getWorld().getName();
									double x = loc.getX();
									double y = loc.getY();
									double z = loc.getZ();
									float yaw = loc.getYaw();
									float pitch = loc.getPitch();

									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".World", world);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".X", x);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".Y", y);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".Z", z);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".Yaw", yaw);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".Pitch", pitch);

									JNR.data.set(name + ".Checkpoints", Integer.valueOf(args[2]));

									saveFile(p, "§a" + Integer.valueOf(args[2]) + ". §7Checkpoint wurde gesetzt.");
								} catch (Exception e) {
									p.sendMessage(JNR.prefix + "§cEin Fehler ist aufgetreten!");
								}
							} else {
								p.sendMessage(JNR.prefix + "§cEin JumpAndRun mit dem Namen '§e" + name
										+ "§c' existiert nicht!");
								p.sendMessage(JNR.prefix + "§cBenutze: /jnr create " + name
										+ " §cum das JumpAndRun zu erstellen!");
							}
						} else if (args[0].equalsIgnoreCase("reset")) {
							String pName = args[1];
							String map = args[2];

							if (containsMap(map)) {
								for (String key : JNR.stats.getKeys(false)) {
									if (key.equalsIgnoreCase(map)) {
										map = key;
										break;
									}
								}

								JNR.stats.set(pName + "." + map, null);

								try {
									JNR.stats.save(JNR.file3);
								} catch (IOException e) {
									e.printStackTrace();
								}

								p.sendMessage("§7Du hast die JumpAndRun Stats von §e" + pName
										+ " §czurückgesetzt§7. §8[Map: " + map + "]");
							} else {
								p.sendMessage("§cDie Map §e" + map + " §cexistiert nicht.");
							}
						}
					} else {
						sendHelp(p);
					}
				}
			} else {
				sendHelp(p);
			}
		}

		return false;
	}

	private void sendHelp(Player p) {
		if (p.hasPermission("jnr.admin")) {
			p.sendMessage("§8======§6JumpAndRun§8======");
			p.sendMessage("");
			p.sendMessage("§6/jnr create <Name> §7- §aErstellt ein neues JumpAndRun");
			p.sendMessage("§6/jnr setspawn <Name> §7- §aSetzt den Spawn-Punkt für ein JumpAndRun");
			p.sendMessage("§6/jnr setcp <Name> <Checkpoint> §7- §aSetzt einen Checkpoint für ein JumpAndRun");
			p.sendMessage("§6/jnr setleave <Name> §7- §aSetzt den Leave-Punkt für ein JumpAndRun");
			p.sendMessage("§6/jnr setmoney <Währung> §7- §aSetzt den Namen der Währung fest");
			p.sendMessage("§6/jnr setwin <Name> <Money> §7- §aSetzt den Gewinn für das JumpAndRun fest");
			p.sendMessage(
					"§6/jnr setextrawin <Money> §7- §aSetzt den Gewinn für das Aufstellen eines neuen Rekords fest");
			p.sendMessage("§6/jnr setblock <Checkpoint/Win> §7- §aSetzt den Gewinn/Checkpoint-Block");
			p.sendMessage(
					"§6/jnr allcpstowin <true/false> §7- §aLegt fest ob man alle Checkpoints benötigt, um das JumpAndRun abzuschließen");
			p.sendMessage("§6/jnr resetall <Spieler> §7- §aSetzt alle Stats von einem Spieler zurück");
			p.sendMessage(
					"§6/jnr reset <Spieler> <Map> §7- §aSetzt die Stats für eine bestimmte Map von einem Spieler zurück");
			p.sendMessage("");
			p.sendMessage("§8=====§9Plugin by Seblii§8=====");
		} else {
			p.sendMessage("§cNutze: /jnr stats <Spieler> <Map>");
		}
	}

	public static void saveFile(Player p, String msg) {
		try {
			JNR.data.save(JNR.file);
			p.sendMessage(JNR.prefix + "§aGespeichert - " + "§8{§7" + msg + "§8}");
		} catch (IOException e) {
			p.sendMessage(JNR.prefix + "§cBeim Speichern ist ein Fehler aufgetreten!");
			e.printStackTrace();
		}
	}

	private void getStats(Player executor, String name, String map) {
		if (containsMap(map)) {
			for (String key : JNR.stats.getKeys(false)) {
				if (key.equalsIgnoreCase(map)) {
					map = key;
					break;
				}
			}

			if (containsPlayer(name)) {
				for (String key : JNR.stats.getKeys(false)) {
					if (key.equalsIgnoreCase(name)) {
						name = key;
						break;
					}
				}

				int finishedTimes = JNR.stats.getInt(name + "." + map + ".finishedTimes");
				int playedTimes = JNR.stats.getInt(name + "." + map + ".playedTimes");
				int fails = JNR.stats.getInt(name + "." + map + ".fails");
				double recordTime = JNR.stats.getDouble(name + "." + map + ".bestTime");
				double globalRecordTime = JNR.stats.getDouble(map + ".globalBestTime");
				String bt = calculateTime(recordTime);
				String gbt = calculateTime(globalRecordTime);

				try {
					executor.sendMessage("§8======§6JumpAndRun§8======");
					executor.sendMessage("");
					executor.sendMessage("§7JumpAndRun: §6" + map);
					executor.sendMessage("§7Angefangene Runden: §6" + playedTimes);
					executor.sendMessage("§7Abgeschlossene Runden: §6" + finishedTimes);
					executor.sendMessage("§7Fails: §6" + fails);
					if (JNR.stats.getInt(name + "." + map + ".bestTime") == 0.0) {
						executor.sendMessage("§7Rekordzeit: §6---");
					} else {
						executor.sendMessage("§7Rekordzeit: §6" + bt);
					}
					if (!JNR.stats.contains(map + ".globalBestTime")
							|| JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
						executor.sendMessage(
								"§7Globale Rekordzeit: §6Es wurde auf dieser Map noch kein Rekord aufgestellt.");
					} else {
						executor.sendMessage("§7Globale Rekordzeit: §6" + gbt);
					}
					executor.sendMessage("");
					executor.sendMessage("§8=====§9Stats von §c" + name + "§8=====");
				} catch (Exception e) {
					executor.sendMessage("§8======§6JumpAndRun§8======");
					executor.sendMessage("");
					executor.sendMessage("§7JumpAndRun: §6" + map);
					executor.sendMessage("§7Angefangene Runden: §6" + 0);
					executor.sendMessage("§7Abgeschlossene Runden: §6" + 0);
					executor.sendMessage("§7Fails: §6" + 0);
					executor.sendMessage("§7Rekordzeit: §6---");
					if (!JNR.stats.contains(map + ".globalBestTime")
							|| JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
						executor.sendMessage(
								"§7Globale Rekordzeit: §6Es wurde auf dieser Map noch kein Rekord aufgestellt.");
					} else {
						executor.sendMessage("§7Globale Rekordzeit: §6" + gbt);
					}
					executor.sendMessage("");
					executor.sendMessage("§8=====§9Stats von §c" + name + "§8=====");
				}
			} else {
				executor.sendMessage("§8======§6JumpAndRun§8======");
				executor.sendMessage("");
				executor.sendMessage("§7JumpAndRun: §6" + map);
				executor.sendMessage("§7Angefangene Runden: §6" + 0);
				executor.sendMessage("§7Abgeschlossene Runden: §6" + 0);
				executor.sendMessage("§7Fails: §6" + 0);
				executor.sendMessage("§7Rekordzeit: §6---");
				if (!JNR.stats.contains(map + ".globalBestTime") || JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
					executor.sendMessage(
							"§7Globale Rekordzeit: §6Es wurde auf dieser Map noch kein Rekord aufgestellt.");
				} else {
					executor.sendMessage(
							"§7Globale Rekordzeit: §6" + calculateTime(JNR.stats.getDouble(map + ".globalBestTime")));
				}
				executor.sendMessage("");
				executor.sendMessage("§8=====§9Stats von §c" + name + "§8=====");
			}
		} else {
			executor.sendMessage("§cDie Map §e" + map + " §cexistiert nicht.");
		}
	}

	private boolean containsMap(String map) {
		boolean cont = false;

		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(JNR.stats.getKeys(false));

		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i).equalsIgnoreCase(map)) {
				cont = true;
			}
		}

		return cont;
	}

	private boolean containsPlayer(String name) {
		boolean cont = false;

		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(JNR.stats.getKeys(false));

		for (int i = 0; i < keys.size(); i++) {
			if (keys.get(i).equalsIgnoreCase(name)) {
				cont = true;
			}
		}

		return cont;
	}

	public static String calculateTime(double time) {
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;

		seconds = seconds - minutes * 60;
		time = time - seconds * 1000 - minutes * 60 * 1000;

		String sec = String.valueOf(seconds);
		String min = String.valueOf(minutes);
		String ti = String.valueOf((int) time);

		if (minutes < 10) {
			min = "0" + String.valueOf(minutes);
		}

		if (seconds < 10) {
			sec = "0" + String.valueOf(seconds);
		}

		if (time < 100 && time >= 10) {
			ti = "0" + ti;
		} else if (time < 10) {
			ti = "00" + ti;
		}

		return "§6§l" + min + "§7:§6§l" + sec + "§7,§6" + ti;
	}

}
