package de.sebli.jnr.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.sebli.jnr.JNR;
import de.sebli.jnr.Language;
import de.sebli.jnr.listeners.StartListener;

public class JNRCommand implements CommandExecutor {

	public static HashMap<String, String> mapCreationCache = new HashMap<>();

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (Language.getLanguage().equalsIgnoreCase("german")) {
				sender.sendMessage("Du musst ein Spieler sein!");
			} else {
				sender.sendMessage("You have to be a player!");
			}
		} else {
			Player p = (Player) sender;
			if (args.length == 1) {
				if (p.hasPermission("jnr.admin")) {
					if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
						JNR.messages = null;
						JNR.data = null;

						JNR.messages = YamlConfiguration.loadConfiguration(JNR.file4);
						JNR.data = YamlConfiguration.loadConfiguration(JNR.file);

						JNR.getInstance().reloadConfig();

						Language.sendMessage(p, JNR.prefix + "§aConfigs reloaded.",
								JNR.prefix + "§aConfigs erfolgreich neu geladen.");
					} else {
						sendHelp(p, args[0]);
					}
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
						Language.sendMessage(p, JNR.prefix + "§cYou can only join a JumpAndRun through a Join-Sign.",
								JNR.prefix + "§cDu kannst einem JumpAndRun nur über ein Join-Schild betreten.");
					}
				} else {
					if (p.hasPermission("jnr.admin")) {
						if (args[0].equalsIgnoreCase("setblock")) {
							if (p.getInventory().getItemInMainHand() == null
									|| p.getInventory().getItemInMainHand().getType() == Material.AIR) {
								Language.sendMessage(p, JNR.prefix + "§cYou must hold a block in your hand.",
										JNR.prefix + "§cDu musst einen Block in der Hand halten.");

								return false;
							}

							String block = p.getInventory().getItemInMainHand().getType().toString();
							if (args[1].equalsIgnoreCase("win")) {
								JNR.data.set("WinBlock", block);
								saveFile(p, "§7Win-Block");
							} else if (args[1].equalsIgnoreCase("checkpoint")) {
								JNR.data.set("CheckpointBlock", block);
								saveFile(p, "§7Checkpoint-Block");
							}
						} else if (args[0].equalsIgnoreCase("create")) {
							String name = args[1];
							if (name.equalsIgnoreCase("Item") || name.equalsIgnoreCase("JoinSign")) {
								Language.sendMessage(p,
										JNR.prefix + "§c'§e" + name
												+ "§c' is a invalid name. Please use a different name.",
										JNR.prefix + "§c'§e" + name
												+ "§c' ist ein ungültiger Name. Bitte benutze einen anderen Namen.");

								return false;
							}

							if (!JNR.data.contains(name)) {
								JNR.data.set(name, "true");
								if (Language.getLanguage().equalsIgnoreCase("german")) {
									saveFile(p, "§7JumpAndRun §a" + name + " §7erstellt.");
								} else {
									saveFile(p, "§7JumpAndRun §a" + name + " §7created.");
								}
								Language.sendMessage(p, "§4§lPlease complete the setup for this JumpAndRun now.",
										"§4§lBitte vervollständige nun das Setup für dieses JumpAndRun.");

								if (mapCreationCache.containsKey(p.getName()))
									mapCreationCache.remove(p.getName());

								mapCreationCache.put(p.getName(), name);
							} else {
								Language.sendMessage(p,
										JNR.prefix + "§cThere is already a JumpAndRun named '§e" + name + "§c'",
										JNR.prefix + "§cEs gibt bereits ein JumpAndRun mit dem Namen '§e" + name
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

								if (Language.getLanguage().equalsIgnoreCase("german")) {
									saveFile(p, "§7Spawn-Punkt gesetzt.");
								} else {
									saveFile(p, "§7Spawn-Point set.");
								}
							} else {
								Language.sendMessage(p,
										JNR.prefix + "§cThere is no JumpAndRun named '§e" + name + "§c'!", JNR.prefix
												+ "§cEin JumpAndRun mit dem Namen '§e" + name + "§c' existiert nicht!");
								Language.sendMessage(p,
										JNR.prefix + "§cUse: /jnr create " + name + " §cto create this JumpAndRun!",
										JNR.prefix + "§cBenutze: /jnr create " + name
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

								if (Language.getLanguage().equalsIgnoreCase("german")) {
									saveFile(p, "§7Leave-Punkt gesetzt.");
								} else {
									saveFile(p, "§7Leave-Point set.");
								}
							} else {
								Language.sendMessage(p,
										JNR.prefix + "§cThere is no JumpAndRun named '§e" + name + "§c'!", JNR.prefix
												+ "§cEin JumpAndRun mit dem Namen '§e" + name + "§c' existiert nicht!");
								Language.sendMessage(p,
										JNR.prefix + "§cUse: /jnr create " + name + " §cto create this JumpAndRun!",
										JNR.prefix + "§cBenutze: /jnr create " + name
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
							Language.sendMessage(p, "§7You have §creset §7the JumpAndRun stats of §e" + pName + " §7.",
									"§7Du hast die JumpAndRun Stats von §e" + pName + " §czurückgesetzt§7.");
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
							if (p.getInventory().getItemInMainHand() != null
									&& p.getInventory().getItemInMainHand().getType().getId() != 0) {
								if (args[1].equalsIgnoreCase("checkpoint")) {
									JNR.data.set("Item.BackToLastCheckpoint",
											p.getInventory().getItemInMainHand().getType().toString() + ":"
													+ p.getInventory().getItemInMainHand().getData().getData());
									saveFile(p,
											"§7BackToLastCheckpoint Item -> "
													+ p.getInventory().getItemInMainHand().getType().toString() + ":"
													+ p.getInventory().getItemInMainHand().getData().getData());
								} else if (args[1].equalsIgnoreCase("hide")) {
									JNR.data.set("Item.HidePlayers",
											p.getInventory().getItemInMainHand().getType().toString() + ":"
													+ p.getInventory().getItemInMainHand().getData().getData());
									saveFile(p,
											"§7HidePlayers Item -> "
													+ p.getInventory().getItemInMainHand().getType().toString() + ":"
													+ p.getInventory().getItemInMainHand().getData().getData());
								} else if (args[1].equalsIgnoreCase("unhide")) {
									JNR.data.set("Item.ShowPlayers",
											p.getInventory().getItemInMainHand().getType().toString() + ":"
													+ p.getInventory().getItemInMainHand().getData().getData());
									saveFile(p,
											"§7ShowPlayers Item -> "
													+ p.getInventory().getItemInMainHand().getType().toString() + ":"
													+ p.getInventory().getItemInMainHand().getData().getData());
								} else if (args[1].equalsIgnoreCase("quit")) {
									JNR.data.set("Item.Quit", p.getInventory().getItemInMainHand().getType().toString()
											+ ":" + p.getInventory().getItemInMainHand().getData().getData());
									saveFile(p,
											"§7Quit Item -> "
													+ p.getInventory().getItemInMainHand().getType().toString() + ":"
													+ p.getInventory().getItemInMainHand().getData().getData());
								} else {
									sendHelp(p, args[0]);
								}
							} else {
								Language.sendMessage(p, JNR.prefix + "§cYou must hold an item in your hand.",
										JNR.prefix + "§cDu musst ein Item in der Hand haben.");
							}
						} else if (args[0].equalsIgnoreCase("language") || args[0].equalsIgnoreCase("lang")
								|| args[0].equalsIgnoreCase("sprache")) {
							JNR.getInstance().getConfig().set("Language", args[1]);
							JNR.getInstance().saveConfig();

							Language.sendMessage(p, JNR.prefix + "§7Language set to §6" + args[1] + "§7.",
									JNR.prefix + "§7Sprache zu §6" + args[1] + " §7geändert.");

							JNR.getInstance().changeLanguage();

							Language.sendMessage(p, JNR.prefix
									+ "§cGenerated new 'messages.yml'. Content of old 'messages.yml' moved to 'messagesOLD.yml'.",
									JNR.prefix
											+ "§c'messages.yml' neu generiert. Inhalt der alten 'messages.yml' in 'messagesOLD.yml' verschoben.");
						} else {
							sendHelp(p, args[0]);
						}
					} else {
						sendHelp(p, args[0]);
					}
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("stats")) {
					String pName = args[1];
					String map = args[2];
					getStats(p, pName, map);
				} else {
					if (p.hasPermission("jnr.admin")) {
						if (args[0].equalsIgnoreCase("setcp")) {
							String name = args[1];

							if (JNR.data.contains(name)) {
								try {
									if (Integer.valueOf(args[2]) == 0) {
										Language.sendMessage(p, JNR.prefix + "§cAn error has occurred!",
												JNR.prefix + "§cEin Fehler ist aufgetreten!");

										return false;
									}

									Location loc = p.getLocation();

									String world = loc.getWorld().getName();
									double x = loc.getX();
									double y = loc.getY();
									double z = loc.getZ();
									float yaw = loc.getYaw();
									float pitch = loc.getPitch();

									JNR.data.set(name + ".Checkpoints", Integer.valueOf(args[2]));

									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".World", world);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".X", x);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".Y", y);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".Z", z);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".Yaw", yaw);
									JNR.data.set(name + "." + Integer.valueOf(args[2]) + ".Pitch", pitch);

									if (Language.getLanguage().equalsIgnoreCase("german")) {
										saveFile(p, "§a" + Integer.valueOf(args[2]) + ". §7Checkpoint wurde gesetzt.");
									} else {
										saveFile(p, "§a" + Integer.valueOf(args[2]) + ". §7checkpoint set.");
									}
								} catch (Exception e) {
									Language.sendMessage(p, JNR.prefix + "§cAn error has occurred!",
											JNR.prefix + "§cEin Fehler ist aufgetreten!");
								}
							} else {
								Language.sendMessage(p,
										JNR.prefix + "§cThere is no JumpAndRun named '§e" + name + "§c'!", JNR.prefix
												+ "§cEin JumpAndRun mit dem Namen '§e" + name + "§c' existiert nicht!");
								Language.sendMessage(p,
										JNR.prefix + "§cUse: /jnr create " + name + " §cto create this JumpAndRun!",
										JNR.prefix + "§cBenutze: /jnr create " + name
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

								Language.sendMessage(p,
										"§7You have §creset §7the map-stats of §e" + pName + "§7. §8[Map: " + map + "]",
										"§7Du hast die Map-Stats von §e" + pName + " §czurückgesetzt§7. §8[Map: " + map
												+ "]");
							} else {
								Language.sendMessage(p, "§cThere is no map named §e" + map + "§c.",
										"§cDie Map §e" + map + " §cexistiert nicht.");
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

	public static void joinMap(Player p, String jnr) {
		if (!StartListener.playing.containsKey(p.getName())) {
			if (!StartListener.cooldown.contains(p.getName())) {
				if (containsMap(jnr)) {
					for (String key : JNR.data.getKeys(false)) {
						if (key.equalsIgnoreCase(jnr)) {
							jnr = key;
							break;
						}
					}
				} else {
					Language.sendMessage(p, JNR.prefix + "§cThe map §e" + jnr + " §cdoes not exist.",
							JNR.prefix + "§cDie Map §e" + jnr + " §cexistiert nicht.");

					return;
				}

				StartListener.savePlayerData(p);

				p.setGameMode(GameMode.ADVENTURE);
				p.setHealth(p.getMaxHealth());
				p.setFoodLevel(20);

				if (StartListener.playing.containsKey(p.getName())) {
					StartListener.playing.remove(p.getName());
				}
				if (StartListener.checkpoint.containsKey(p.getName())) {
					StartListener.checkpoint.remove(p.getName());
				}
				StartListener.playing.put(p.getName(), jnr);
				StartListener.checkpoint.put(p.getName(), 1);
				StartListener.fails.put(p.getName(), 0);

				if (JNR.getInstance().getConfig().getBoolean("EnableStartCountdown")) {
					StartListener.startCountdown.put(p.getName(), 3);
				} else {
					StartListener.time.put(p.getName(), System.nanoTime());
				}

				World world = Bukkit.getWorld(JNR.data.getString(jnr + ".World"));
				double x = JNR.data.getDouble(jnr + ".X");
				double y = JNR.data.getDouble(jnr + ".Y");
				double z = JNR.data.getDouble(jnr + ".Z");
				float yaw = (float) JNR.data.getDouble(jnr + ".Yaw");
				float pitch = (float) JNR.data.getDouble(jnr + ".Pitch");

				Location loc = new Location(world, x, y, z, yaw, pitch);

				p.teleport(loc);

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
			if (cmd.equalsIgnoreCase("create")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr create <Name>",
						JNR.prefix + "§cBenutze: /jnr create <Name>");
			} else if (cmd.equalsIgnoreCase("setspawn")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr setspawn <Name>",
						JNR.prefix + "§cBenutze: /jnr setspawn <Name>");
			} else if (cmd.equalsIgnoreCase("setcp")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr setcp <Name> <Checkpoint>",
						JNR.prefix + "§cBenutze: /jnr setcp <Name> <Checkpoint>");
			} else if (cmd.equalsIgnoreCase("setleave")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr setleave <Name>",
						JNR.prefix + "§cBenutze: /jnr setleave <Name>");
			} else if (cmd.equalsIgnoreCase("setblock")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr setblock <Checkpoint/Win>",
						JNR.prefix + "§cBenutze: /jnr setblock <Checkpoint/Win>");
			} else if (cmd.equalsIgnoreCase("item")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr item <checkpoint/hide/unhide/quit>",
						JNR.prefix + "§cBenutze: /jnr item <checkpoint/hide/unhide/quit>");
			} else if (cmd.equalsIgnoreCase("allcpstowin")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr allcpstowin <true/false>",
						JNR.prefix + "§cBenutze: /jnr allcpstowin <true/false>");
			} else if (cmd.equalsIgnoreCase("language")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr language <english/german>",
						JNR.prefix + "§cBenutze: /jnr language <english/german>");
			} else if (cmd.equalsIgnoreCase("reload")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr reload", JNR.prefix + "§cBenutze: /jnr reload");
			} else if (cmd.equalsIgnoreCase("resetall")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr resetall <Player>",
						JNR.prefix + "§cBenutze: /jnr resetall <Spieler>");
			} else if (cmd.equalsIgnoreCase("reset")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr reset <Player> <Map>",
						JNR.prefix + "§cBenutze: /jnr reset <Spieler> <Map>");
			} else if (cmd.equalsIgnoreCase("stats")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr stats <Player> <Map>",
						JNR.prefix + "§cBenutze: /jnr stats <Spieler> <Map>");
			} else if (cmd.equalsIgnoreCase("join")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr join <Map>",
						JNR.prefix + "§cBenutze: /jnr join <Map>");
			} else {
				p.sendMessage("§8======§6JumpAndRun§8======");
				p.sendMessage("");
				Language.sendMessage(p, "§6/jnr create <Name> §7- §aCreates a new JumpAndRun",
						"§6/jnr create <Name> §7- §aErstellt ein neues JumpAndRun");
				Language.sendMessage(p, "§6/jnr setspawn <Name> §7- §aSets the spawn-point for a JumpAndRun",
						"§6/jnr setspawn <Name> §7- §aSetzt den Spawn-Punkt für ein JumpAndRun");
				Language.sendMessage(p, "§6/jnr setcp <Name> <Checkpoint> §7- §aSets a checkpoint for a JumpAndRun",
						"§6/jnr setcp <Name> <Checkpoint> §7- §aSetzt einen Checkpoint für ein JumpAndRun");
				Language.sendMessage(p, "§6/jnr setleave <Name> §7- §aSets the leave-point for a JumpAndRun",
						"§6/jnr setleave <Name> §7- §aSetzt den Leave-Punkt für ein JumpAndRun");
				Language.sendMessage(p, "§6/jnr setblock <Checkpoint/Win> §7- §aSets the Win/Checkpoint-Block",
						"§6/jnr setblock <Checkpoint/Win> §7- §aSetzt den Gewinn/Checkpoint-Block");
				Language.sendMessage(p,
						"§6/jnr item <checkpoint/hide/unhide/quit> §7- §aSets the JumpAndRun-Items (Back to last checkpoint, Hide players, Show players, Quit)",
						"§6/jnr item <checkpoint/hide/unhide/quit> §7- §aLegt die JumpAndRun-Items fest (Zurück zum letzten Checkpoint, Spieler verstecken, Spieler anzeigen, Verlassen)");
				Language.sendMessage(p,
						"§6/jnr allcpstowin <true/false> §7- §aDetermines if you need all checkpoints to complete a JumpAndRun or not",
						"§6/jnr allcpstowin <true/false> §7- §aLegt fest ob man alle Checkpoints benötigt, um das JumpAndRun abzuschließen");
				Language.sendMessage(p,
						"§6/jnr language <english/german> §7- §aSets the language to english/german (if you execute this command, all players will be kicked out of the JumpAndRun)",
						"§6/jnr language <english/german> §7- §aLegt die Sprache fest (Englisch/Deutsch) (wenn du diesen Befehl nutzt, werden alle Spieler aus dem JumpAndRun gekickt)");
				Language.sendMessage(p, "§6/jnr reload §7- §aReloads the configs of the plugin",
						"§6/jnr reload §7- §aLädt die Plugin-Configs neu");
				Language.sendMessage(p, "§6/jnr resetall <Player> §7- §aResets all stats of a player",
						"§6/jnr resetall <Spieler> §7- §aSetzt alle Stats von einem Spieler zurück");
				Language.sendMessage(p, "§6/jnr reset <Player> <Map> §7- §aResets the map-stats of a player",
						"§6/jnr reset <Spieler> <Map> §7- §aSetzt die Stats für eine bestimmte Map von einem Spieler zurück");
				Language.sendMessage(p, "§6/jnr stats <Player> <Map> §7- §aShows the stats of a player",
						"§6/jnr stats <Spieler> <Map> §7- §aZeigt die Stats für einen Spieler auf einer bestimmten Map an");
				Language.sendMessage(p, "§6/jnr join <Map> §7- §aJoin a JumpAndRun",
						"§6/jnr join <Map> §7- §aTrete einem JumpAndRun bei");
				p.sendMessage("");
				p.sendMessage("§8=====§9Plugin by Seblii§8=====");
			}
		} else {
			if (cmd.equalsIgnoreCase("stats")) {
				Language.sendMessage(p, JNR.prefix + "§cUse: /jnr stats <Player> <Map>",
						JNR.prefix + "§cBenutze: /jnr stats <Spieler> <Map>");
			} else if (cmd.equalsIgnoreCase("join")) {
				if (JNR.getInstance().getConfig().getBoolean("EnableJoinCommand")) {
					Language.sendMessage(p, JNR.prefix + "§cUse: /jnr join <Map>",
							JNR.prefix + "§cBenutze: /jnr join <Map>");
				} else {
					Language.sendMessage(p, JNR.prefix + "§cUse: /jnr stats <Player> <Map>",
							JNR.prefix + "§cBenutze: /jnr stats <Spieler> <Map>");
				}
			} else {
				if (JNR.getInstance().getConfig().getBoolean("EnableJoinCommand")) {
					Language.sendMessage(p, "§8========§6JumpAndRun Help§8========",
							"§8========§6JumpAndRun Hilfe§8========");
					p.sendMessage("");
					Language.sendMessage(p, "§6/jnr stats <Spieler> <Map> §7- §aShows the stats of a player",
							"§6/jnr stats <Spieler> <Map> §7- §aZeigt die Stats für einen Spieler auf einer bestimmten Map an");
					Language.sendMessage(p, "§6/jnr join <Map> §7- §aJoin a JumpAndRun",
							"§6/jnr join <Map> §7- §aTrete einem JumpAndRun bei");
					p.sendMessage("");
					Language.sendMessage(p, "§8======§6JumpAndRun Commands§8======",
							"§8=======§6JumpAndRun Befehle§8=======");
				} else {
					Language.sendMessage(p, "§cUse: /jnr stats <Player> <Map>",
							"§cBenutze: /jnr stats <Spieler> <Map>");
				}
			}
		}
	}

	public static void saveFile(Player p, String msg) {
		try {
			JNR.data.save(JNR.file);
			Language.sendMessage(p, JNR.prefix + "§aSaved - " + "§8{§7" + msg + "§8}",
					JNR.prefix + "§aGespeichert - " + "§8{§7" + msg + "§8}");
		} catch (IOException e) {
			Language.sendMessage(p, JNR.prefix + "§cAn error has occured!",
					JNR.prefix + "§cBeim Speichern ist ein Fehler aufgetreten!");
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
			}
		} else {
			Language.sendMessage(executor, JNR.prefix + "§cThe map §e" + map + " §cdoes not exist.",
					JNR.prefix + "§cDie Map §e" + map + " §cexistiert nicht.");

			sendHelp(executor, "stats");

			return;
		}
	}

	public static boolean containsMap(String map) {
		boolean cont = false;

		if (map.equalsIgnoreCase("Item") || map.equalsIgnoreCase("JoinSign") || map.equalsIgnoreCase("CheckpointBlock")
				|| map.equalsIgnoreCase("WinBlock"))
			return false;

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

		if (Language.getLanguage().equalsIgnoreCase("german")) {
			return "§6§l" + min + "§7:§6§l" + sec + "§7,§6" + ti;
		} else {
			return "§6§l" + min + "§7:§6§l" + sec + "§7.§6" + ti;
		}
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
