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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.sebli.jnr.JNR;

public class ItemListener implements Listener {

	HashMap<String, Long> wait = new HashMap<>();
	HashMap<String, Long> wait2 = new HashMap<>();
	HashMap<String, Long> wait3 = new HashMap<>();

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (StartListener.playing.containsKey(p.getName())) {
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				String itemCooldownMsg = JNR.messages.getString("Messages.ItemCooldown").replaceAll("&", "§");

				if (p.getItemInHand().getType().equals(Material.SLIME_BALL)
						&& p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§cVerlassen")) {
					String jnr = StartListener.playing.get(p.getName());

					StartListener.playing.remove(p.getName());
					StartListener.checkpoint.remove(p.getName());
					WinListener.resetInventory(p);
					StartListener.time.remove(p.getName());
					StartListener.timer.remove(p.getName());

					World world = Bukkit.getWorld(JNR.data.getString(jnr + ".Leave.World"));
					double x = JNR.data.getDouble(jnr + ".Leave.X");
					double y = JNR.data.getDouble(jnr + ".Leave.Y");
					double z = JNR.data.getDouble(jnr + ".Leave.Z");
					float yaw = (float) JNR.data.getDouble(jnr + ".Leave.Yaw");
					float pitch = (float) JNR.data.getDouble(jnr + ".Leave.Pitch");

					Location loc = new Location(world, x, y, z, yaw, pitch);

					p.teleport(loc);

					for (Player all : Bukkit.getOnlinePlayers()) {
						p.showPlayer(all);
					}
				} else if (p.getItemInHand().getType().equals(Material.GLOWSTONE_DUST)
						&& p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§cSpieler verstecken")) {
					Long millis = System.currentTimeMillis();

					if (!wait2.containsKey(p.getName())) {
						hidePlayer(p);
						wait2.put(p.getName(), millis);
					} else if (wait2.containsKey(p.getName())) {

						Long last = wait2.get(p.getName());

						if (last + 500 > millis) {
							p.sendMessage(JNR.prefix + itemCooldownMsg);
							return;
						}
						wait2.put(p.getName(), millis);

						hidePlayer(p);
					}
				} else if (p.getItemInHand().getType().equals(Material.SULPHUR)
						&& p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§aSpieler anzeigen")) {
					Long millis = System.currentTimeMillis();

					if (!wait3.containsKey(p.getName())) {
						showPlayer(p);
						wait3.put(p.getName(), millis);
					} else if (wait3.containsKey(p.getName())) {

						Long last = wait3.get(p.getName());

						if (last + 500 > millis) {
							p.sendMessage(JNR.prefix + itemCooldownMsg);
							return;
						}
						wait3.put(p.getName(), millis);

						showPlayer(p);
					}
				} else if (p.getItemInHand().getType().equals(Material.REDSTONE) && p.getItemInHand().getItemMeta()
						.getDisplayName().equalsIgnoreCase("§cZum letzten Checkpoint")) {
					Long millis = System.currentTimeMillis();

					if (!wait.containsKey(p.getName())) {
						toLastCP(p);

						wait.put(p.getName(), millis);
					} else if (wait.containsKey(p.getName())) {

						Long last = wait.get(p.getName());

						if (last + 500 > millis) {
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

	private void toLastCP(Player p) {
		String jnr = StartListener.playing.get(p.getName());
		int cp = StartListener.checkpoint.get(p.getName());
		int fails = StartListener.fails.get(p.getName());

		StartListener.fails.put(p.getName(), fails + 1);
		JNR.stats.set(p.getName() + "." + jnr + ".fails", JNR.stats.getInt(p.getName() + "." + jnr + ".fails") + 1);

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
		ItemStack hide = new ItemStack(Material.SULPHUR);
		ItemMeta hide1 = hide.getItemMeta();
		hide1.setDisplayName("§aSpieler anzeigen");
		hide.setItemMeta(hide1);

		p.getInventory().setItem(1, hide);
	}

	private void showPlayer(Player p) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			p.showPlayer(all);
		}
		ItemStack show = new ItemStack(Material.GLOWSTONE_DUST);
		ItemMeta show1 = show.getItemMeta();
		show1.setDisplayName("§cSpieler verstecken");
		show.setItemMeta(show1);

		p.getInventory().setItem(1, show);
	}

}
