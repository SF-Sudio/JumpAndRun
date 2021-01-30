package de.sebli.jnr.listeners;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.sebli.jnr.ActionBar;
import de.sebli.jnr.JNR;
import de.sebli.jnr.commands.JNRCommand;

public class ItemListener implements Listener {

	HashMap<String, Long> wait = new HashMap<>();
	HashMap<String, Long> wait2 = new HashMap<>();
	HashMap<String, Long> wait3 = new HashMap<>();

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (StartListener.playing.containsKey(p.getName())) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
				String itemCooldownMsg = JNR.messages.getString("Messages.ItemCooldown").replaceAll("&", "§");

				String id = "AIR";
				int subID = 0;

				String id2 = "AIR";
				int subID2 = 0;

				String id3 = "AIR";
				int subID3 = 0;

				String id4 = "AIR";
				int subID4 = 0;

				if (JNR.data.getString("Item.BackToLastCheckpoint").contains(":")) {
					String[] array = JNR.data.getString("Item.BackToLastCheckpoint").split(":");
					id = array[0];
					subID = Integer.valueOf(array[1]);
				} else {
					id = JNR.data.getString("Item.BackToLastCheckpoint");
				}

				if (JNR.data.getString("Item.HidePlayers").contains(":")) {
					String[] array = JNR.data.getString("Item.HidePlayers").split(":");
					id2 = array[0];
					subID2 = Integer.valueOf(array[1]);
				} else {
					id2 = JNR.data.getString("Item.HidePlayers");
				}

				if (JNR.data.getString("Item.ShowPlayers").contains(":")) {
					String[] array = JNR.data.getString("Item.ShowPlayers").split(":");
					id3 = array[0];
					subID3 = Integer.valueOf(array[1]);
				} else {
					id3 = JNR.data.getString("Item.ShowPlayers");
				}

				if (JNR.data.getString("Item.Quit").contains(":")) {
					String[] array = JNR.data.getString("Item.Quit").split(":");
					id4 = array[0];
					subID4 = Integer.valueOf(array[1]);
				} else {
					id4 = JNR.data.getString("Item.Quit");
				}

				String checkName = JNR.messages.getString("Item.BackToLastCheckpoint.Name").replaceAll("&", "§");
				String showName = JNR.messages.getString("Item.HidePlayers.Name").replaceAll("&", "§");
				String hideName = JNR.messages.getString("Item.ShowPlayers.Name").replaceAll("&", "§");
				String quitName = JNR.messages.getString("Item.Quit.Name").replaceAll("&", "§");

				ItemStack check = new ItemStack(Material.getMaterial(id), 1, (byte) subID);
				ItemMeta check1 = check.getItemMeta();
				check1.setDisplayName(checkName);
				check.setItemMeta(check1);

				ItemStack show = new ItemStack(Material.getMaterial(id2), 1, (byte) subID2);
				ItemMeta show1 = show.getItemMeta();
				show1.setDisplayName(showName);
				show.setItemMeta(show1);

				ItemStack hide = new ItemStack(Material.getMaterial(id3), 1, (byte) subID3);
				ItemMeta hide1 = hide.getItemMeta();
				hide1.setDisplayName(hideName);
				hide.setItemMeta(hide1);

				ItemStack leave = new ItemStack(Material.getMaterial(id4), 1, (byte) subID4);
				ItemMeta leave1 = leave.getItemMeta();
				leave1.setDisplayName(quitName);
				leave.setItemMeta(leave1);

				if (p.getInventory().getItemInMainHand().equals(leave)) {
					quit(p);
				} else if (p.getInventory().getItemInMainHand().equals(show)) {
					if (JNR.getInstance().getConfig().getBoolean("EnableItemCooldown")) {
						Long millis = System.currentTimeMillis();

						if (!wait2.containsKey(p.getName())) {
							hidePlayer(p);
							wait2.put(p.getName(), millis);
						} else if (wait2.containsKey(p.getName())) {
							Long last = wait2.get(p.getName());

							if (last + 500 > millis) {
								if (!itemCooldownMsg.equalsIgnoreCase("x"))
									p.sendMessage(JNR.prefix + itemCooldownMsg);
								return;
							}
							wait2.put(p.getName(), millis);

							hidePlayer(p);
						}
					} else {
						hidePlayer(p);
					}
				} else if (p.getInventory().getItemInMainHand().equals(hide)) {
					if (JNR.getInstance().getConfig().getBoolean("EnableItemCooldown")) {
						Long millis = System.currentTimeMillis();

						if (!wait3.containsKey(p.getName())) {
							showPlayer(p);
							wait3.put(p.getName(), millis);
						} else if (wait3.containsKey(p.getName())) {
							Long last = wait3.get(p.getName());

							if (last + 500 > millis) {
								if (!itemCooldownMsg.equalsIgnoreCase("x"))
									p.sendMessage(JNR.prefix + itemCooldownMsg);
								return;
							}
							wait3.put(p.getName(), millis);

							showPlayer(p);
						}
					} else {
						showPlayer(p);
					}
				} else if (p.getInventory().getItemInMainHand().equals(check)) {
					if (StartListener.startCountdown.containsKey(p.getName())) {
						return;
					}

					if (JNR.getInstance().getConfig().getBoolean("EnableItemCooldown")) {
					Long millis = System.currentTimeMillis();

					if (!wait.containsKey(p.getName())) {
						toLastCP(p);

						wait.put(p.getName(), millis);
					} else if (wait.containsKey(p.getName())) {

						Long last = wait.get(p.getName());

						if (last + 500 > millis) {
							if (!itemCooldownMsg.equalsIgnoreCase("x"))
								p.sendMessage(JNR.prefix + itemCooldownMsg);
							return;
						}
						wait.put(p.getName(), millis);

						toLastCP(p);
					}
					} else {
						toLastCP(p);
					}
				}
			}
		}
	}

	public static void toLastCP(Player p) {
		String jnr = StartListener.playing.get(p.getName());
		int cp = StartListener.checkpoint.get(p.getName());
		int fails = StartListener.fails.get(p.getName());

		StartListener.fails.put(p.getName(), fails + 1);
		JNR.stats.set(p.getName() + "." + jnr + ".fails", JNR.stats.getInt(p.getName() + "." + jnr + ".fails") + 1);

		long time = (System.nanoTime() - StartListener.time.get(p.getName())) / 1000000;

		String abText = JNR.messages.getString("Messages.ActionBar").replaceAll("&", "§")
				.replaceAll("%map%", StartListener.playing.get(p.getName()))
				.replaceAll("%time%", JNRCommand.calculateTimeInSeconds(time))
				.replaceAll("%fails%", StartListener.fails.get(p.getName()).toString());

		ActionBar.sendActionbar(p, abText);

		try {
			JNR.stats.save(JNR.file3);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (cp != 1) {
			int cps = cp - 1;
			World world = Bukkit.getWorld(JNR.data.getString(jnr + "." + cps + ".World"));
			double x = JNR.data.getDouble(jnr + "." + cps + ".X");
			double y = JNR.data.getDouble(jnr + "." + cps + ".Y");
			double z = JNR.data.getDouble(jnr + "." + cps + ".Z");
			float yaw = (float) JNR.data.getDouble(jnr + "." + cps + ".Yaw");
			float pitch = (float) JNR.data.getDouble(jnr + "." + cps + ".Pitch");

			Location loc = new Location(world, x + 0.25, y, z + 0.25, yaw, pitch);

			p.teleport(loc);
		} else {
			World world = Bukkit.getWorld(JNR.data.getString(jnr + ".World"));
			double x = JNR.data.getDouble(jnr + ".X");
			double y = JNR.data.getDouble(jnr + ".Y");
			double z = JNR.data.getDouble(jnr + ".Z");
			float yaw = (float) JNR.data.getDouble(jnr + ".Yaw");
			float pitch = (float) JNR.data.getDouble(jnr + ".Pitch");

			Location loc = new Location(world, x, y, z, yaw, pitch);

			p.teleport(loc);
		}
	}

	private void hidePlayer(Player p) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			p.hidePlayer(all);
		}
		String id3 = "AIR";
		int subID3 = 0;

		if (JNR.data.getString("Item.ShowPlayers").contains(":")) {
			String[] array = JNR.data.getString("Item.ShowPlayers").split(":");
			id3 = array[0];
			subID3 = Integer.valueOf(array[1]);
		} else {
			id3 = JNR.data.getString("Item.ShowPlayers");
		}

		String hideName = JNR.messages.getString("Item.ShowPlayers.Name").replaceAll("&", "§");

		ItemStack hide = new ItemStack(Material.getMaterial(id3), 1, (byte) subID3);
		ItemMeta hide1 = hide.getItemMeta();
		hide1.setDisplayName(hideName);
		hide.setItemMeta(hide1);

		p.getInventory().setItem(1, hide);
	}

	private void showPlayer(Player p) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			p.showPlayer(all);
		}
		String id2 = "AIR";
		int subID2 = 0;

		if (JNR.data.getString("Item.HidePlayers").contains(":")) {
			String[] array = JNR.data.getString("Item.HidePlayers").split(":");
			id2 = array[0];
			subID2 = Integer.valueOf(array[1]);
		} else {
			id2 = JNR.data.getString("Item.HidePlayers");
		}

		String showName = JNR.messages.getString("Item.HidePlayers.Name").replaceAll("&", "§");

		ItemStack show = new ItemStack(Material.getMaterial(id2), 1, (byte) subID2);
		ItemMeta show1 = show.getItemMeta();
		show1.setDisplayName(showName);
		show.setItemMeta(show1);

		p.getInventory().setItem(1, show);
	}

	public static void quit(Player p) {
		WinListener.reset(p);
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if (StartListener.playing.containsKey(e.getPlayer().getName())) {
			e.setCancelled(true);
		}
	}

}
