package de.sebli.jnr.listeners;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.sebli.jnr.ActionBar;
import de.sebli.jnr.JNR;
import de.sebli.jnr.commands.JNRCommand;

public class ItemListener implements Listener {

	HashMap<String, Long> wait = new HashMap<>();
	HashMap<String, Long> wait2 = new HashMap<>();
	HashMap<String, Long> wait3 = new HashMap<>();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (StartListener.playing.containsKey(p.getName())) {
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				String itemCooldownMsg = JNR.messages.getString("Messages.ItemCooldown").replaceAll("&", "§");

				int id = 0;
				int subID = 0;

				int id2 = 0;
				int subID2 = 0;

				int id3 = 0;
				int subID3 = 0;

				int id4 = 0;
				int subID4 = 0;

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

				if (JNR.data.getString("Item.ShowPlayers").contains(":")) {
					String[] array = JNR.data.getString("Item.ShowPlayers").split(":");
					id3 = Integer.valueOf(array[0]);
					subID3 = Integer.valueOf(array[1]);
				} else {
					id3 = Integer.valueOf(JNR.data.getString("Item.ShowPlayers"));
				}

				if (JNR.data.getString("Item.Quit").contains(":")) {
					String[] array = JNR.data.getString("Item.Quit").split(":");
					id4 = Integer.valueOf(array[0]);
					subID4 = Integer.valueOf(array[1]);
				} else {
					id4 = Integer.valueOf(JNR.data.getString("Item.Quit"));
				}

				String checkName = JNR.messages.getString("Item.BackToLastCheckpoint.Name").replaceAll("&", "§");
				String showName = JNR.messages.getString("Item.HidePlayers.Name").replaceAll("&", "§");
				String hideName = JNR.messages.getString("Item.ShowPlayers.Name").replaceAll("&", "§");
				String quitName = JNR.messages.getString("Item.Quit.Name").replaceAll("&", "§");

				ItemStack check = new ItemStack(id, 1, (byte) subID);
				ItemMeta check1 = check.getItemMeta();
				check1.setDisplayName(checkName);
				check.setItemMeta(check1);

				ItemStack show = new ItemStack(id2, 1, (byte) subID2);
				ItemMeta show1 = show.getItemMeta();
				show1.setDisplayName(showName);
				show.setItemMeta(show1);

				ItemStack hide = new ItemStack(id3, 1, (byte) subID3);
				ItemMeta hide1 = hide.getItemMeta();
				hide1.setDisplayName(hideName);
				hide.setItemMeta(hide1);

				ItemStack leave = new ItemStack(id4, 1, (byte) subID4);
				ItemMeta leave1 = leave.getItemMeta();
				leave1.setDisplayName(quitName);
				leave.setItemMeta(leave1);

				if (p.getInventory().getItemInHand().equals(leave)) {
					quit(p);
				} else if (p.getInventory().getItemInHand().equals(show)) {
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
				} else if (p.getInventory().getItemInHand().equals(hide)) {
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
				} else if (p.getInventory().getItemInHand().equals(check)) {
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

		ActionBar actionBar = new ActionBar(abText);
		actionBar.sendToPlayer(p);

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

	@SuppressWarnings("deprecation")
	private void hidePlayer(Player p) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			p.hidePlayer(all);
		}
		int id3 = 0;
		int subID3 = 0;

		if (JNR.data.getString("Item.ShowPlayers").contains(":")) {
			String[] array = JNR.data.getString("Item.ShowPlayers").split(":");
			id3 = Integer.valueOf(array[0]);
			subID3 = Integer.valueOf(array[1]);
		} else {
			id3 = Integer.valueOf(JNR.data.getString("Item.ShowPlayers"));
		}

		String hideName = JNR.messages.getString("Item.ShowPlayers.Name").replaceAll("&", "§");

		ItemStack hide = new ItemStack(id3, 1, (byte) subID3);
		ItemMeta hide1 = hide.getItemMeta();
		hide1.setDisplayName(hideName);
		hide.setItemMeta(hide1);

		p.getInventory().setItem(1, hide);
	}

	@SuppressWarnings("deprecation")
	private void showPlayer(Player p) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			p.hidePlayer(all);
		}
		int id2 = 0;
		int subID2 = 0;

		if (JNR.data.getString("Item.HidePlayers").contains(":")) {
			String[] array = JNR.data.getString("Item.HidePlayers").split(":");
			id2 = Integer.valueOf(array[0]);
			subID2 = Integer.valueOf(array[1]);
		} else {
			id2 = Integer.valueOf(JNR.data.getString("Item.HidePlayers"));
		}

		String showName = JNR.messages.getString("Item.HidePlayers.Name").replaceAll("&", "§");

		ItemStack show = new ItemStack(id2, 1, (byte) subID2);
		ItemMeta show1 = show.getItemMeta();
		show1.setDisplayName(showName);
		show.setItemMeta(show1);

		p.getInventory().setItem(1, show);
	}

	public static void quit(Player p) {
		WinListener.reset(p);
	}

	@SuppressWarnings("deprecation")
	public static void quickQuit(Player p) {
		String jnr = StartListener.playing.get(p.getName());

		p.setGameMode(GameMode.getByValue(JNR.playerData.getInt(p.getName() + ".Gamemode")));
		p.setHealth(JNR.playerData.getDouble(p.getName() + ".Health"));
		p.setFoodLevel(JNR.playerData.getInt(p.getName() + ".FoodLevel"));

		p.getInventory().setContents((ItemStack[]) JNR.playerData.get(p.getName() + ".Inv"));
		p.updateInventory();

		JNR.playerData.set(p.getName(), null);

		StartListener.cooldown.remove(p.getName());

		try {
			JNR.playerData.save(JNR.file2);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (!JNR.data.contains(jnr + ".Leave")) {
			World world = Bukkit.getWorld(JNR.playerData.getString("Location." + p.getName() + ".World"));
			double x = JNR.playerData.getDouble("Location." + p.getName() + ".X");
			double y = JNR.playerData.getDouble("Location." + p.getName() + ".Y");
			double z = JNR.playerData.getDouble("Location." + p.getName() + ".Z");
			float yaw = (float) JNR.playerData.getDouble("Location." + p.getName() + ".Yaw");
			float pitch = (float) JNR.playerData.getDouble("Location." + p.getName() + ".Pitch");

			Location loc = new Location(world, x, y, z, yaw, pitch);

			p.teleport(loc);

			JNR.playerData.set("Location." + p.getName(), null);

			try {
				JNR.playerData.save(JNR.file2);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			World world = Bukkit.getWorld(JNR.data.getString(jnr + ".Leave.World"));
			double x = JNR.data.getDouble(jnr + ".Leave.X");
			double y = JNR.data.getDouble(jnr + ".Leave.Y");
			double z = JNR.data.getDouble(jnr + ".Leave.Z");
			float yaw = (float) JNR.data.getDouble(jnr + ".Leave.Yaw");
			float pitch = (float) JNR.data.getDouble(jnr + ".Leave.Pitch");

			Location loc = new Location(world, x, y, z, yaw, pitch);

			p.teleport(loc);
		}

		for (Player all : Bukkit.getOnlinePlayers()) {
			p.showPlayer(all);
		}
	}

}
