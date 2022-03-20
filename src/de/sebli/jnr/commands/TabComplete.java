package de.sebli.jnr.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import de.sebli.jnr.JNR;

public class TabComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		List<String> commands = new ArrayList<>();

		if (args.length == 1) {
			commands.add("stats");
			if (JNR.getInstance().getConfig().getBoolean("EnableJoinCommand")) {
				commands.add("join");
			}

			StringUtil.copyPartialMatches(args[0], commands, completions);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("join")) {
				for (String map : JNR.data.getKeys(false)) {
					if (JNRCommand.containsMap(map)) {
						commands.add(map);
					}
				}
			} else if (args[0].equalsIgnoreCase("stats")) {
				for (Player players : Bukkit.getOnlinePlayers()) {
					commands.add(players.getName());
				}
			}

			StringUtil.copyPartialMatches(args[1], commands, completions);
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("stats")) {
				for (String map : JNR.stats.getKeys(false)) {
					if (JNRCommand.containsMap(map)) {
						commands.add(map);
					}
				}

				StringUtil.copyPartialMatches(args[2], commands, completions);
			}
		}

		if (sender.hasPermission("jnr.admin")) {
			if (args.length == 1) {
				commands.add("create");
				commands.add("setspawn");
				commands.add("setcp");
				commands.add("setleave");
				commands.add("setblock");
//				commands.add("item");
				commands.add("allcpstowin");
				commands.add("language");
				commands.add("reload");
				commands.add("reset");
				commands.add("resetall");
				commands.add("stats");
				if (JNR.getInstance().getConfig().getBoolean("EnableJoinCommand")) {
					commands.add("join");
				}

				StringUtil.copyPartialMatches(args[0], commands, completions);
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setcp")
						|| args[0].equalsIgnoreCase("setleave")) {
					if (JNRCommand.mapCreationCache.containsKey(sender.getName())) {
						commands.add(JNRCommand.mapCreationCache.get(sender.getName()));
					}
				} else if (args[0].equalsIgnoreCase("setblock")) {
					commands.add("checkpoint");
					commands.add("win");
				} else if (args[0].equalsIgnoreCase("item")) {
//					commands.add("checkpoint");
//					commands.add("hide");
//					commands.add("unhide");
//					commands.add("quit");
				} else if (args[0].equalsIgnoreCase("allcpstowin")) {
					commands.add("true");
					commands.add("false");
				} else if (args[0].equalsIgnoreCase("language")) {
					commands.add("english");
					commands.add("german");
				} else if (args[0].equalsIgnoreCase("resetall") || args[0].equalsIgnoreCase("reset")) {
					for (String player : JNR.stats.getKeys(false)) {
						boolean isMap = false;

						for (String map : JNR.data.getKeys(false)) {
							if (player.equalsIgnoreCase(map)) {
								isMap = true;

								break;
							}
						}

						if (isMap)
							continue;

						commands.add(player);
					}
				}

				StringUtil.copyPartialMatches(args[1], commands, completions);
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("setcp")) {
					commands.add(String.valueOf(JNR.data.getInt(args[1] + ".Checkpoints") + 1));
				} else if (args[0].equalsIgnoreCase("reset")) {
					for (String map : JNR.stats.getKeys(false)) {
						if (JNRCommand.containsMap(map)) {
							commands.add(map);
						}
					}
				}

				StringUtil.copyPartialMatches(args[2], commands, completions);
			}
		}

		Collections.sort(completions);

		return completions;
	}

}
