package de.sebli.jnr.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.sebli.jnr.JNR;
import de.sebli.jnr.listeners.StartListener;

public class JNRCommand implements CommandExecutor {

	@SuppressWarnings({ "deprecation" })
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Du musst ein Spieler sein!");
		} else {
			Player p = (Player) sender;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					JNR.messages = null;
					JNR.data = null;

					JNR.messages = YamlConfiguration.loadConfiguration(JNR.file4);
					JNR.data = YamlConfiguration.loadConfiguration(JNR.file);

					JNR.getInstance().reloadConfig();

//					if (JNR.data.getBoolean("EnableVault")) {
//						try {
//							JNR.getInstance().setupEconomy();
//						} catch (Exception e) {
//							System.err.println("JumpAndRun: Vault konnte nicht geladen werden!");
//						}
//					}

					p.sendMessage(JNR.prefix + "§aPlugin erfolgreich neu geladen.");
				} else {
					sendHelp(p, args[0]);
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("stats")) {
					String map = args[1];
					getStats(p, p.getName(), map);
				} else if (args[0].equalsIgnoreCase("join")) {
					if (JNR.getInstance().getConfig().getBoolean("EnableJoinCommand")) {
						String map = args[1];
						joinMap(p, map);
					} else {
						sendHelp(p, args[0]);
					}
				} else {
					if (p.hasPermission("jnr.admin")) {
//						if (args[0].equalsIgnoreCase("setextrawin")) {
//							try {
//								int eWin = Integer.valueOf(args[1]);
//
//								JNR.data.set("NewRecordWin", eWin);
//								saveFile(p, "§7Gewinn für einen neuen Rekord auf §a" + eWin + " §7gesetzt.");
//							} catch (Exception ex) {
//								p.sendMessage(JNR.prefix
//										+ "§cEs ist ein Fehler aufgetreten. Bitte gebe einen gültigen Wert ein!");
//							}
//						} else if (args[0].equalsIgnoreCase("setmoney")) {
//							String mn = args[1];
//							JNR.data.set("MoneyName", mn);
//							saveFile(p, "§7Währungsname -> " + mn);
//						}
						if (args[0].equalsIgnoreCase("setblock")) {
							String block = p.getInventory().getItemInHand().getType().toString();
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
								sendHelp(p, args[0]);
							}
						} else if (args[0].equalsIgnoreCase("item")) {
							if (p.getItemInHand() != null && p.getItemInHand().getType().getId() != 0) {
								if (args[1].equalsIgnoreCase("checkpoint")) {
									JNR.data.set("Item.BackToLastCheckpoint", p.getItemInHand().getType().toString() + ":"
											+ p.getItemInHand().getData().getData());
									saveFile(p, "§7BackToLastCheckpoint Item -> " + p.getItemInHand().getType().toString()
											+ ":" + p.getItemInHand().getData().getData());
								} else if (args[1].equalsIgnoreCase("hide")) {
									JNR.data.set("Item.HidePlayers", p.getItemInHand().getType().toString() + ":"
											+ p.getItemInHand().getData().getData());
									saveFile(p, "§7HidePlayers Item -> " + p.getItemInHand().getType().toString() + ":"
											+ p.getItemInHand().getData().getData());
								} else if (args[1].equalsIgnoreCase("unhide")) {
									JNR.data.set("Item.ShowPlayers", p.getItemInHand().getType().toString() + ":"
											+ p.getItemInHand().getData().getData());
									saveFile(p, "§7ShowPlayers Item -> " + p.getItemInHand().getType().toString() + ":"
											+ p.getItemInHand().getData().getData());
								} else if (args[1].equalsIgnoreCase("quit")) {
									JNR.data.set("Item.Quit", p.getItemInHand().getType().toString() + ":"
											+ p.getItemInHand().getData().getData());
									saveFile(p, "§7Quit Item -> " + p.getItemInHand().getType().toString() + ":"
											+ p.getItemInHand().getData().getData());
								} else {
									sendHelp(p, args[0]);
								}
							} else {
								p.sendMessage(JNR.prefix + "§cDu musst ein Item in der Hand haben.");
							}
						} else {
							sendHelp(p, args[0]);
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
//						if (args[0].equalsIgnoreCase("setwin")) {
//							try {
//								String name = args[1];
//								int amount = Integer.valueOf(args[2]);
//
//								if (JNR.data.contains(name)) {
//									JNR.data.set(name + ".Win", amount);
//									saveFile(p, "§7Gewinn für §a" + args[1] + " auf §6" + amount + JNR.getMoneyName()
//											+ " §7gesetzt.");
//								} else {
//									p.sendMessage(JNR.prefix + "§cEin JumpAndRun mit dem Namen '§e" + name
//											+ "§c' existiert nicht!");
//									p.sendMessage(JNR.prefix + "§cBenutze: /jnr create " + name
//											+ " §cum das JumpAndRun zu erstellen!");
//								}
//							} catch (Exception e) {
//								p.sendMessage(JNR.prefix + "§cEin Fehler ist aufgetreten!");
//							}
//						}
						if (args[0].equalsIgnoreCase("setcp")) {
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
						sendHelp(p, args[0]);
					}
				}
			} else {
				sendHelp(p, "");
			}
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public static void joinMap(Player p, String jnr) {
		if (!StartListener.playing.containsKey(p.getName())) {
			if (!StartListener.cooldown.contains(p.getName())) {
				if (containsMap(jnr)) {
					for (String key : JNR.stats.getKeys(false)) {
						if (key.equalsIgnoreCase(jnr)) {
							jnr = key;
							break;
						}
					}
				} else {
					p.sendMessage("§cDie Map §e" + jnr + " §cexistiert nicht.");

					return;
				}

				StartListener.savePlayerData(p);

				p.setGameMode(GameMode.ADVENTURE);
				p.setHealth(p.getMaxHealth());
				p.setFoodLevel(20);

				StartListener.displayTimer(p);

				if (StartListener.playing.containsKey(p.getName())) {
					StartListener.playing.remove(p.getName());
				}
				if (StartListener.checkpoint.containsKey(p.getName())) {
					StartListener.checkpoint.remove(p.getName());
				}
				StartListener.playing.put(p.getName(), jnr);
				StartListener.checkpoint.put(p.getName(), 1);

				World world = Bukkit.getWorld(JNR.data.getString(jnr + ".World"));
				double x = JNR.data.getDouble(jnr + ".X");
				double y = JNR.data.getDouble(jnr + ".Y");
				double z = JNR.data.getDouble(jnr + ".Z");
				float yaw = (float) JNR.data.getDouble(jnr + ".Yaw");
				float pitch = (float) JNR.data.getDouble(jnr + ".Pitch");

				Location loc = new Location(world, x, y, z, yaw, pitch);

				p.teleport(loc);

				String joinTitle1 = JNR.messages.getString("Messages.JoinTitle.1").replaceAll("&", "§")
						.replaceAll("%map%", StartListener.playing.get(p.getName()));
				String joinTitle2 = JNR.messages.getString("Messages.JoinTitle.2").replaceAll("&", "§")
						.replaceAll("%map%", StartListener.playing.get(p.getName()));

				p.sendTitle(joinTitle1, joinTitle2);

				StartListener.time.put(p.getName(), System.nanoTime());
				StartListener.fails.put(p.getName(), 0);

				if (!JNR.stats.contains(jnr + ".globalBestTime")) {
					JNR.stats.set(jnr + ".globalBestTime", 0.0);
				}

				if (JNR.stats.contains(p.getName() + "." + jnr + ".playedTimes")) {
					int pt = JNR.stats.getInt(p.getName() + "." + jnr + ".playedTimes") + 1;
					JNR.stats.set(p.getName() + "." + jnr + ".playedTimes", pt);
				} else {
					JNR.stats.set(p.getName() + "." + jnr + ".playedTimes", 1);
				}

				try {
					JNR.stats.save(JNR.file3);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				p.getInventory().clear();
				StartListener.setInventory(p);
			} else {
				String errorMsg = JNR.messages.getString("Messages.JoinCooldown").replaceAll("&", "§");
				if (!errorMsg.equalsIgnoreCase("x"))
					p.sendMessage(JNR.prefix + errorMsg);
			}
		} else {
			String errorMsg = JNR.messages.getString("Messages.AlreadyInAJumpAndRun").replaceAll("&", "§")
					.replaceAll("%map%", StartListener.playing.get(p.getName()));
			if (!errorMsg.equalsIgnoreCase("x"))
				p.sendMessage(JNR.prefix + errorMsg);
		}
	}

	private void sendHelp(Player p, String cmd) {
		if (p.hasPermission("jnr.admin")) {
			p.sendMessage("§8======§6JumpAndRun§8======");
			p.sendMessage("");
			p.sendMessage("§6/jnr create <Name> §7- §aErstellt ein neues JumpAndRun");
			p.sendMessage("§6/jnr setspawn <Name> §7- §aSetzt den Spawn-Punkt für ein JumpAndRun");
			p.sendMessage("§6/jnr setcp <Name> <Checkpoint> §7- §aSetzt einen Checkpoint für ein JumpAndRun");
			p.sendMessage("§6/jnr setleave <Name> §7- §aSetzt den Leave-Punkt für ein JumpAndRun");
//			p.sendMessage("§6/jnr setmoney <Währung> §7- §aSetzt den Namen der Währung fest");
//			p.sendMessage("§6/jnr setwin <Name> <Money> §7- §aSetzt den Gewinn für das JumpAndRun fest");
//			p.sendMessage("§6/jnr setextrawin <Money> §7- §aSetzt den Gewinn für das Aufstellen eines neuen Rekords fest");
			p.sendMessage("§6/jnr setblock <Checkpoint/Win> §7- §aSetzt den Gewinn/Checkpoint-Block");
			p.sendMessage(
					"§6/jnr item <checkpoint/hide/unhide/quit> §7- §aLegt die JumpAndRun Items fest (Zurück zum letzten Checkpoint, Spieler verstecken, Spieler anzeigen, Verlassen)");
			p.sendMessage(
					"§6/jnr allcpstowin <true/false> §7- §aLegt fest ob man alle Checkpoints benötigt, um das JumpAndRun abzuschließen");
			p.sendMessage("§6/jnr reload §7- §aLädt das Plugin neu");
			p.sendMessage("§6/jnr resetall <Spieler> §7- §aSetzt alle Stats von einem Spieler zurück");
			p.sendMessage(
					"§6/jnr reset <Spieler> <Map> §7- §aSetzt die Stats für eine bestimmte Map von einem Spieler zurück");
			p.sendMessage(
					"§6/jnr stats <Spieler> <Map> §7- §aZeigt die Stats für einen Spieler auf einer bestimmten Map an");
			p.sendMessage("§6/jnr join <Map> §7- §aTrete einem JumpAndRun bei");
			p.sendMessage("");
			p.sendMessage("§8=====§9Plugin by Seblii§8=====");
		} else {
			if (cmd.equalsIgnoreCase("stats")) {
				p.sendMessage("§cNutze: /jnr stats <Spieler> <Map>");
			} else if (cmd.equalsIgnoreCase("join")) {
				if (JNR.getInstance().getConfig().getBoolean("EnableJoinCommand")) {
					p.sendMessage("§cNutze: /jnr join <Map>");
				} else {
					p.sendMessage("§cNutze: /jnr stats <Spieler> <Map>");
				}
			} else {
				if (JNR.getInstance().getConfig().getBoolean("EnableJoinCommand")) {
					p.sendMessage(
							"§6/jnr stats <Spieler> <Map> §7- §aZeigt die Stats für einen Spieler auf einer bestimmten Map an");
					p.sendMessage("§6/jnr join <Map> §7- §aTrete einem JumpAndRun bei");
				} else {
					p.sendMessage("§cNutze: /jnr stats <Spieler> <Map>");
				}
			}
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
					List<String> statsList = JNR.messages.getStringList("Messages.Command.Stats.Player");

					for (String message : statsList) {
						message = message.replaceAll("&", "§")
								.replaceAll("%finishedTimes%", String.valueOf(finishedTimes))
								.replaceAll("%playedTimes%", String.valueOf(playedTimes))
								.replaceAll("%fails%", String.valueOf(fails))
								.replaceAll("%recordTime%", String.valueOf(bt)).replaceAll("%map%", map)
								.replaceAll("%player%", name);

						if (JNR.stats.getInt(name + "." + map + ".bestTime") == 0.0) {
							message = message.replaceAll("%recordTime%",
									JNR.messages.getString("Messages.Command.Stats.NoRecord").replaceAll("&", "§"));
						} else {
							message = message.replaceAll("%recordTime%", bt);
						}

						if (!JNR.stats.contains(map + ".globalBestTime")
								|| JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
							message = message.replaceAll("%globalRecordTime%", JNR.messages
									.getString("Messages.Command.Stats.NoGlobalRecord").replaceAll("&", "§"));
						} else {
							message = message.replaceAll("%globalRecordTime%", gbt);
						}

						executor.sendMessage(message);
					}

//					executor.sendMessage("§8======§6JumpAndRun§8======");
//					executor.sendMessage("");
//					executor.sendMessage("§7JumpAndRun: §6" + map);
//					executor.sendMessage("§7Angefangene Runden: §6" + playedTimes);
//					executor.sendMessage("§7Abgeschlossene Runden: §6" + finishedTimes);
//					executor.sendMessage("§7Fails: §6" + fails);
//					if (JNR.stats.getInt(name + "." + map + ".bestTime") == 0.0) {
//						executor.sendMessage("§7Rekordzeit: §6---");
//					} else {
//						executor.sendMessage("§7Rekordzeit: §6" + bt);
//					}
//					if (!JNR.stats.contains(map + ".globalBestTime")
//							|| JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
//						executor.sendMessage(
//								"§7Globale Rekordzeit: §6Es wurde auf dieser Map noch kein Rekord aufgestellt.");
//					} else {
//						executor.sendMessage("§7Globale Rekordzeit: §6" + gbt);
//					}
//					executor.sendMessage("");
//					executor.sendMessage("§8=====§9Stats von §c" + name + "§8=====");
				} catch (Exception e) {
					List<String> statsList = JNR.messages.getStringList("Messages.Command.Stats.Player");

					for (String message : statsList) {
						message = message.replaceAll("&", "§").replaceAll("%finishedTimes%", String.valueOf(0))
								.replaceAll("%playedTimes%", String.valueOf(0)).replaceAll("%fails%", String.valueOf(0))
								.replaceAll("%recordTime%",
										JNR.messages.getString("Messages.Command.Stats.NoRecord").replaceAll("&", "§"))
								.replaceAll("%player%", name).replaceAll("%map%", map);

						if (!JNR.stats.contains(map + ".globalBestTime")
								|| JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
							message = message.replaceAll("%globalRecordTime%", JNR.messages
									.getString("Messages.Command.Stats.NoGlobalRecord").replaceAll("&", "§"));
						} else {
							message = message.replaceAll("%globalRecordTime%", gbt);
						}

						executor.sendMessage(message);
					}

//					executor.sendMessage("§8======§6JumpAndRun§8======");
//					executor.sendMessage("");
//					executor.sendMessage("§7JumpAndRun: §6" + map);
//					executor.sendMessage("§7Angefangene Runden: §6" + 0);
//					executor.sendMessage("§7Abgeschlossene Runden: §6" + 0);
//					executor.sendMessage("§7Fails: §6" + 0);
//					executor.sendMessage("§7Rekordzeit: §6---");
//					if (!JNR.stats.contains(map + ".globalBestTime")
//							|| JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
//						executor.sendMessage(
//								"§7Globale Rekordzeit: §6Es wurde auf dieser Map noch kein Rekord aufgestellt.");
//					} else {
//						executor.sendMessage("§7Globale Rekordzeit: §6" + gbt);
//					}
//					executor.sendMessage("");
//					executor.sendMessage("§8=====§9Stats von §c" + name + "§8=====");
				}
			} else {
				List<String> statsList = JNR.messages.getStringList("Messages.Command.Stats.Player");

				for (String message : statsList) {
					message = message.replaceAll("&", "§").replaceAll("%finishedTimes%", String.valueOf(0))
							.replaceAll("%playedTimes%", String.valueOf(0)).replaceAll("%fails%", String.valueOf(0))
							.replaceAll("%recordTime%", "---").replaceAll("%player%", name).replaceAll("%map%", map);

					if (!JNR.stats.contains(map + ".globalBestTime")
							|| JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
						message = message.replaceAll("%globalRecordTime%",
								JNR.messages.getString("Messages.Command.Stats.NoGlobalRecord").replaceAll("&", "§"));
					} else {
						message = message.replaceAll("%globalRecordTime%",
								calculateTime(JNR.stats.getDouble(map + ".globalBestTime")));
					}

					executor.sendMessage(message);
				}

//				executor.sendMessage("§8======§6JumpAndRun§8======");
//				executor.sendMessage("");
//				executor.sendMessage("§7JumpAndRun: §6" + map);
//				executor.sendMessage("§7Angefangene Runden: §6" + 0);
//				executor.sendMessage("§7Abgeschlossene Runden: §6" + 0);
//				executor.sendMessage("§7Fails: §6" + 0);
//				executor.sendMessage("§7Rekordzeit: §6---");
//				if (!JNR.stats.contains(map + ".globalBestTime") || JNR.stats.getInt(map + ".globalBestTime") == 0.0) {
//					executor.sendMessage(
//							"§7Globale Rekordzeit: §6Es wurde auf dieser Map noch kein Rekord aufgestellt.");
//				} else {
//					executor.sendMessage(
//							"§7Globale Rekordzeit: §6" + calculateTime(JNR.stats.getDouble(map + ".globalBestTime")));
//				}
//				executor.sendMessage("");
//				executor.sendMessage("§8=====§9Stats von §c" + name + "§8=====");
			}
		} else {
			executor.sendMessage("§cDie Map §e" + map + " §cexistiert nicht.");

			return;
		}
	}

	public static boolean containsMap(String map) {
		boolean cont = false;

		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(JNR.data.getKeys(false));

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

	public static String calculateTimeInSeconds(double time) {
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;

		seconds = seconds - minutes * 60;
		time = time - seconds * 1000 - minutes * 60 * 1000;

		String sec = String.valueOf(seconds);
		String min = String.valueOf(minutes);

		if (minutes < 10) {
			min = "0" + String.valueOf(minutes);
		}

		if (seconds < 10) {
			sec = "0" + String.valueOf(seconds);
		}

		return "§6§l" + min + "§7:§6§l" + sec;
	}

}
