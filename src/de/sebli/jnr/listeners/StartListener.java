package de.sebli.jnr.listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.sebli.jnr.ActionBar;
import de.sebli.jnr.JNR;
import de.sebli.jnr.commands.JNRCommand;

public class StartListener implements Listener {

	public static HashMap<String, String> playing = new HashMap<>();
	public static HashMap<String, ItemStack[]> inventory = new HashMap<>();
	public static HashMap<String, Integer> checkpoint = new HashMap<>();
	public static HashMap<String, Integer> timer = new HashMap<>();
	public static HashMap<String, Long> time = new HashMap<>();
	public static HashMap<String, Integer> fails = new HashMap<>();

	public static List<String> cooldown = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getPlayer().hasPermission("jnr.admin")) {
			if (e.getLine(0).equalsIgnoreCase("[jnr]") && !e.getLine(1).isEmpty()) {
				String line1 = JNR.messages.getString("Messages.JoinSign.1").replaceAll("&", "§");
				e.setLine(0, line1);

				List<Location> signLocs = new ArrayList<>();

				if (JNR.data.contains(e.getLine(1) + ".JoinSign")) {
					signLocs = (List<Location>) JNR.data.getList(e.getLine(1) + ".JoinSign");
				}

				signLocs.add(e.getBlock().getLocation());

				JNR.data.set(e.getLine(1) + ".JoinSign", signLocs);

				try {
					JNR.data.save(JNR.file);

					e.getPlayer().sendMessage(JNR.prefix + "§aJoin-Schild wurde erstellt.");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onStart(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) e.getClickedBlock().getState();

				if (sign.getLine(0).isEmpty())
					return;

				List<Location> signLocs = new ArrayList<>();

				if (JNR.data.contains(sign.getLine(1) + ".JoinSign")) {
					signLocs = (List<Location>) JNR.data.getList(sign.getLine(1) + ".JoinSign");
				}

				if (signLocs.contains(sign.getLocation())) {
					String jnr = sign.getLine(1);

					p.performCommand("jnr join " + jnr);
				} else {
					if (p.hasPermission("jnr.admin")) {
						p.sendMessage(JNR.prefix
								+ "§cDurch das neuste Plugin-Update musst du alle Join-Schilder neu setzen, damit sie wieder funktionieren."
								+ "\n§cHier erfährst du die Gründe dafür: §e§lhttps://www.spigotmc.org/resources/jumpandrun-1-8-1-12-x.78123/updates");
					}
				}

//				String line1 = JNR.messages.getString("Messages.JoinSign.1");
//				String sLine1 = sign.getLine(0);
//
//				char[] c = { 'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'k', 'l',
//						'm', 'n', 'o', 'r' };
//
//				for (char ch : c) {
//					line1 = line1.replaceAll("&" + ch, "");
//					sLine1 = sLine1.replaceAll("§" + ch, "");
//				}
//
//				if (sLine1.equalsIgnoreCase(line1)) {
//					String jnr = sign.getLine(1);
//
//					p.performCommand("jnr join " + jnr);
//				}
			}
		} else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) e.getClickedBlock().getState();

				if (sign.getLine(0).isEmpty())
					return;

				if (JNR.stats.contains(sign.getLine(1))) {
					List<Location> signLocs = new ArrayList<>();

					if (JNR.data.contains(sign.getLine(1) + ".JoinSign")) {
						signLocs = (List<Location>) JNR.data.getList(sign.getLine(1) + ".JoinSign");
					}

					if (signLocs.contains(sign.getLocation())) {
						String map = sign.getLine(1);

						p.performCommand("jnr stats " + map);
					} else {
						if (p.hasPermission("jnr.admin")) {
							p.sendMessage(JNR.prefix
									+ "§cDurch das neuste Plugin-Update musst du alle Join-Schilder neu setzen, damit sie wieder funktionieren."
									+ "\n§cHier erfährst du die Gründe dafür: §e§lhttps://www.spigotmc.org/resources/jumpandrun-1-8-1-12-x.78123/updates");
						}
					}
				}
			}
		}

	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();

		if (playing.containsKey(p.getName())) {
			if (!JNR.data.getBoolean("EnableCommandsWhileInGame")) {
				if (!p.hasPermission("jnr.admin")) {
					if (!e.getMessage().startsWith("/jnr")) {
						e.setCancelled(true);

						String errorMsg = JNR.messages.getString("Messages.NoCommandsWhileInGame").replaceAll("&", "§");
						if (!errorMsg.equalsIgnoreCase("x"))
							p.sendMessage(JNR.prefix + errorMsg);
					}
				} else {
					e.setCancelled(false);
				}
			} else {
				e.setCancelled(false);
			}
		} else {
			e.setCancelled(false);
		}

		if (playing.size() != 0) {
			if (!JNR.data.getBoolean("EnableReloadWhilePlayerInGame")) {
				if (e.getMessage().equalsIgnoreCase("/rl") || e.getMessage().equalsIgnoreCase("/reload")
						|| e.getMessage().equalsIgnoreCase("/bukkit:rl")
						|| e.getMessage().equalsIgnoreCase("/bukkit:reload")) {
					e.setCancelled(true);

					if (p.hasPermission("jnr.admin")) {
						String errorMsg = JNR.messages.getString("Messages.NoRealoadsWhileGameRunning").replaceAll("&",
								"§");
						if (!errorMsg.equalsIgnoreCase("x"))
							p.sendMessage(JNR.prefix + errorMsg);
					}
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		if (JNR.playerData.getBoolean(p.getName() + ".isPlaying")) {
			WinListener.reset(p);
		}
	}

	@SuppressWarnings("deprecation")
	public static void setInventory(Player p) {
		int id = 0;
		int subID = 0;

		int id2 = 0;
		int subID2 = 0;

		int id3 = 0;
		int subID3 = 0;

		if (JNR.data.getString("Item.BackToLastCheckpoint").contains(":")) {
			String[] array = JNR.data.getString("Item.BackToLastCheckpoint").split(":");
			id = Integer.valueOf(array[0]);
			subID = Integer.valueOf(array[1]);
		} else {
			id = Integer.valueOf(JNR.data.getString("Item.BackToLastCheckpoint"));
		}

		if (JNR.data.getString("Item.HidePlayers").contains(":")) {
			String[] array = JNR.data.getString("Item.HidePlayers").split(":");
			id2 = Integer.valueOf(array[0]);
			subID2 = Integer.valueOf(array[1]);
		} else {
			id2 = Integer.valueOf(JNR.data.getString("Item.HidePlayers"));
		}

		if (JNR.data.getString("Item.Quit").contains(":")) {
			String[] array = JNR.data.getString("Item.Quit").split(":");
			id3 = Integer.valueOf(array[0]);
			subID3 = Integer.valueOf(array[1]);
		} else {
			id3 = Integer.valueOf(JNR.data.getString("Item.Quit"));
		}

		String checkName = JNR.messages.getString("Item.BackToLastCheckpoint.Name").replaceAll("&", "§");
		String showName = JNR.messages.getString("Item.HidePlayers.Name").replaceAll("&", "§");
		String quitName = JNR.messages.getString("Item.Quit.Name").replaceAll("&", "§");

		ItemStack check = new ItemStack(id, 1, (byte) subID);
		ItemMeta check1 = check.getItemMeta();
		check1.setDisplayName(checkName);
		check.setItemMeta(check1);

		ItemStack show = new ItemStack(id2, 1, (byte) subID2);
		ItemMeta show1 = show.getItemMeta();
		show1.setDisplayName(showName);
		show.setItemMeta(show1);

		ItemStack leave = new ItemStack(id3, 1, (byte) subID3);
		ItemMeta leave1 = leave.getItemMeta();
		leave1.setDisplayName(quitName);
		leave.setItemMeta(leave1);

		new org.bukkit.scheduler.BukkitRunnable() {
			public void run() {
				p.getInventory().setItem(0, check);
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
			}
		}.runTaskLater(JNR.getInstance(), 5L);

		new org.bukkit.scheduler.BukkitRunnable() {
			public void run() {
				p.getInventory().setItem(1, show);
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
			}
		}.runTaskLater(JNR.getInstance(), 10L);

		new org.bukkit.scheduler.BukkitRunnable() {
			public void run() {
				p.getInventory().setItem(8, leave);
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
			}
		}.runTaskLater(JNR.getInstance(), 15L);
	}

	@SuppressWarnings("deprecation")
	public static void savePlayerData(Player p) {
		String jnr = StartListener.playing.get(p.getName());

		JNR.playerData.set(p.getName() + ".Gamemode", p.getGameMode().getValue());
		JNR.playerData.set(p.getName() + ".Health", p.getHealth());
		JNR.playerData.set(p.getName() + ".FoodLevel", p.getFoodLevel());
		JNR.playerData.set(p.getName() + ".Map", jnr);
		JNR.playerData.set(p.getName() + ".isPlaying", true);
		JNR.playerData.set(p.getName() + ".Inv", p.getInventory().getContents());
		
		if (!JNR.data.contains(jnr + ".Leave")) {
			JNR.playerData.set(p.getName() + ".Location.World", p.getLocation().getWorld().getName());
			JNR.playerData.set(p.getName() + ".Location.X", p.getLocation().getX());
			JNR.playerData.set(p.getName() + ".Location.Y", p.getLocation().getY());
			JNR.playerData.set(p.getName() + ".Location.Z", p.getLocation().getZ());
			JNR.playerData.set(p.getName() + ".Location.Yaw", p.getLocation().getYaw());
			JNR.playerData.set(p.getName() + ".Location.Pitch", p.getLocation().getPitch());
		}

		try {
			JNR.playerData.save(JNR.file2);
		} catch (Exception e1) {
			p.sendMessage(JNR.prefix + "§cDein Inventar kann nicht gespeichert werden."
					+ "\n§cBitte entferne unzulässige Items aus deinem Inventar (z.B. Custom Player Heads), um diesen Bug zu vermeiden und einem JumpAndRun beitreten zu können.");
			ItemListener.quit(p);
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void displayTimer(Player p) {
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(JNR.getInstance(), new Runnable() {

			@Override
			public void run() {
				if (playing.containsKey(p.getName())) {
					long time = (System.nanoTime() - StartListener.time.get(p.getName())) / 1000000;

					String abText = JNR.messages.getString("Messages.ActionBar").replaceAll("&", "§")
							.replaceAll("%map%", StartListener.playing.get(p.getName()))
							.replaceAll("%time%", JNRCommand.calculateTimeInSeconds(time))
							.replaceAll("%fails%", StartListener.fails.get(p.getName()).toString());

					ActionBar actionBar = new ActionBar(abText);
					actionBar.sendToPlayer(p);
				} else {
					return;
				}
			}

		}, 0, 20);
	}

}
