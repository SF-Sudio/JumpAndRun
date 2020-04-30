package de.sebli.jnr.listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
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

import de.sebli.jnr.JNR;

public class StartListener implements Listener {

	public static HashMap<String, String> playing = new HashMap<>();
	public static HashMap<String, ItemStack[]> inventory = new HashMap<>();
	public static HashMap<String, Integer> checkpoint = new HashMap<>();
	public static HashMap<String, Integer> timer = new HashMap<>();
	public static HashMap<String, Long> time = new HashMap<>();
	public static HashMap<String, Integer> fails = new HashMap<>();

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getPlayer().hasPermission("jnr.admin")) {
			if (e.getLine(0).equalsIgnoreCase("[jnr]")) {
				String line1 = JNR.messages.getString("Messages.JoinSign.1").replaceAll("&", "§");
				e.setLine(0, line1);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onStart(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				String line1 = JNR.messages.getString("Messages.JoinSign.1").replaceAll("&", "§");
				if (line1.contains(sign.getLine(0))) {
					if (!playing.containsKey(p.getName())) {
						if (JNR.data.contains(sign.getLine(1))) {
							String jnr = sign.getLine(1);
							World world = Bukkit.getWorld(JNR.data.getString(jnr + ".World"));
							double x = JNR.data.getDouble(jnr + ".X");
							double y = JNR.data.getDouble(jnr + ".Y");
							double z = JNR.data.getDouble(jnr + ".Z");
							float yaw = (float) JNR.data.getDouble(jnr + ".Yaw");
							float pitch = (float) JNR.data.getDouble(jnr + ".Pitch");

							Location loc = new Location(world, x, y, z, yaw, pitch);

							if (playing.containsKey(p.getName())) {
								playing.remove(p.getName());
							}
							if (checkpoint.containsKey(p.getName())) {
								checkpoint.remove(p.getName());
							}
							playing.put(p.getName(), jnr);
							checkpoint.put(p.getName(), 1);

							saveInventory(p);

							p.getInventory().clear();
							setInventory(p);

							p.teleport(loc);
							p.sendTitle(jnr, "");

							time.put(p.getName(), System.nanoTime());
							fails.put(p.getName(), 0);

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
						}
					} else {
						String errorMsg = JNR.messages.getString("Messages.AlreadyInAJumpAndRun").replaceAll("&", "§")
								.replaceAll("%map%", StartListener.playing.get(p.getName()));
						p.sendMessage(JNR.prefix + errorMsg);
					}
				}
			}
		} else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				String map = sign.getLine(1);

				String line1 = JNR.messages.getString("Messages.JoinSign.1").replaceAll("&", "§");
				if (line1.contains(sign.getLine(0))) {
					if (JNR.stats.contains(sign.getLine(1))) {
						p.performCommand("jnr stats " + map);
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
						p.sendMessage(JNR.prefix + errorMsg);
					}
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		if (JNR.invs.getBoolean(p.getName() + ".isPlaying")) {
			String jnr = (String) JNR.invs.get(p.getName() + ".Map");

			World world = Bukkit.getWorld(JNR.data.getString(jnr + ".Leave.World"));
			double x = JNR.data.getDouble(jnr + ".Leave.X");
			double y = JNR.data.getDouble(jnr + ".Leave.Y");
			double z = JNR.data.getDouble(jnr + ".Leave.Z");
			float yaw = (float) JNR.data.getDouble(jnr + ".Leave.Yaw");
			float pitch = (float) JNR.data.getDouble(jnr + ".Leave.Pitch");

			Location loc = new Location(world, x, y, z, yaw, pitch);

			p.teleport(loc);

			new org.bukkit.scheduler.BukkitRunnable() {
				public void run() {
					String leftServerMsg = JNR.messages.getString("Messages.LeftServerWhileInGame").replaceAll("&",
							"§");

					p.sendMessage(JNR.prefix + leftServerMsg);
				}
			}.runTaskLater(JNR.getInstance(), 10L);

			new org.bukkit.scheduler.BukkitRunnable() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public void run() {

					ItemStack[] content;

					try {
						ArrayList list = (ArrayList) JNR.invs.get(p.getName() + ".Inv");

						content = (ItemStack[]) list.toArray(new ItemStack[list.size()]);
					} catch (Exception ex) {
						content = (ItemStack[]) JNR.invs.get(p.getName() + ".Inv");
					}

					p.getInventory().setContents(content);

					String invRestoredMsg = JNR.messages.getString("Messages.InventoryRestored").replaceAll("&", "§");
					p.sendMessage(JNR.prefix + invRestoredMsg);

					JNR.invs.set(p.getName(), null);

					try {
						JNR.invs.save(JNR.file2);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}.runTaskLater(JNR.getInstance(), 40L);

			playing.remove(p.getName());

			JNR.invs.set(p.getName() + ".isPlaying", false);

			try {
				JNR.invs.save(JNR.file2);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void setInventory(Player p) {
		ItemStack show = new ItemStack(Material.GLOWSTONE_DUST);
		ItemMeta show1 = show.getItemMeta();
		show1.setDisplayName("§cSpieler verstecken");
		show.setItemMeta(show1);

		ItemStack leave = new ItemStack(Material.SLIME_BALL);
		ItemMeta leave1 = leave.getItemMeta();
		leave1.setDisplayName("§cVerlassen");
		leave.setItemMeta(leave1);

		ItemStack check = new ItemStack(Material.REDSTONE);
		ItemMeta check1 = check.getItemMeta();
		check1.setDisplayName("§cZum letzten Checkpoint");
		check.setItemMeta(check1);

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

	private void saveInventory(Player p) {
		inventory.put(p.getName(), p.getInventory().getContents());
	}

}
