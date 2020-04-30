package de.sebli.jnr.listeners;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import de.sebli.jnr.JNR;
import de.sebli.jnr.commands.JNRCommand;

public class WinListener implements Listener {

	HashMap<String, Long> wait = new HashMap<>();
	HashMap<String, Long> wait2 = new HashMap<>();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onWin(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		if (StartListener.playing.containsKey(p.getName())) {
			int id = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().getId();
			if (id == JNR.data.getInt("WinBlock")
					|| p.getLocation().getBlock().getType().getId() == JNR.data.getInt("WinBlock")) {
				if (StartListener.checkpoint.get(p.getName()) > JNR.data
						.getInt(StartListener.playing.get(p.getName()) + ".Checkpoints")
						|| !JNR.data.getBoolean("NeedAllCheckpointsToWin")) {
					int win = JNR.data.getInt(StartListener.playing.get(p.getName()) + ".Win");
					int eWin = JNR.data.getInt("NewRecordWin");

					if (JNR.data.getBoolean("EnableVault")) {
						String winTitle1 = JNR.messages.getString("Messages.Win.Title.Vault.1").replaceAll("&", "§")
								.replaceAll("%map%", StartListener.playing.get(p.getName()))
								.replaceAll("%win%", String.valueOf(win)).replaceAll("%moneyName%", JNR.getMoneyName());

						String winTitle2 = JNR.messages.getString("Messages.Win.Title.Vault.2").replaceAll("&", "§")
								.replaceAll("%map%", StartListener.playing.get(p.getName()))
								.replaceAll("%win%", String.valueOf(win)).replaceAll("%moneyName%", JNR.getMoneyName());

						p.sendTitle(winTitle1, winTitle2);

						JNR.eco.depositPlayer(p.getName(), win);
					} else {
						String winTitle1 = JNR.messages.getString("Messages.Win.Title.1").replaceAll("&", "§")
								.replaceAll("%map%", StartListener.playing.get(p.getName()));

						String winTitle2 = JNR.messages.getString("Messages.Win.Title.2").replaceAll("&", "§")
								.replaceAll("%map%", StartListener.playing.get(p.getName()));

						p.sendTitle(winTitle1, winTitle2);
					}

					String jnr = StartListener.playing.get(p.getName());

					StartListener.playing.remove(p.getName());
					StartListener.checkpoint.remove(p.getName());

					if (JNR.stats.contains(p.getName() + "." + jnr + ".finishedTimes")) {
						int ft = JNR.stats.getInt(p.getName() + "." + jnr + ".finishedTimes");
						JNR.stats.set(p.getName() + "." + jnr + ".finishedTimes", ft + 1);
					} else {
						JNR.stats.set(p.getName() + "." + jnr + ".finishedTimes", 1);
					}

					try {
						JNR.stats.save(JNR.file3);
					} catch (IOException e2) {
						e2.printStackTrace();
					}

					World world = Bukkit.getWorld(JNR.data.getString(jnr + ".Leave.World"));
					double x = JNR.data.getDouble(jnr + ".Leave.X");
					double y = JNR.data.getDouble(jnr + ".Leave.Y");
					double z = JNR.data.getDouble(jnr + ".Leave.Z");
					float yaw = (float) JNR.data.getDouble(jnr + ".Leave.Yaw");
					float pitch = (float) JNR.data.getDouble(jnr + ".Leave.Pitch");

					Location loc = new Location(world, x, y, z, yaw, pitch);

					for (Player all : Bukkit.getOnlinePlayers()) {
						p.showPlayer(all);
					}

					p.teleport(loc);
					WinListener.resetInventory(p);

					long time = (System.nanoTime() - StartListener.time.get(p.getName())) / 1000000;

					Bukkit.getScheduler().scheduleSyncDelayedTask(JNR.getInstance(), new Runnable() {

						@Override
						public void run() {
							if (!JNR.stats.contains(p.getName() + "." + jnr + ".bestTime")
									|| JNR.stats.getDouble(p.getName() + "." + jnr + ".bestTime") > time) {
								JNR.stats.set(p.getName() + "." + jnr + ".bestTime", time);

								String newPRecord1 = JNR.messages.getString("Messages.NewPersonalRecord.Title.1")
										.replaceAll("&", "§").replaceAll("%map%", jnr);
								String newPRecord2 = JNR.messages.getString("Messages.NewPersonalRecord.Title.2")
										.replaceAll("&", "§").replaceAll("%map%", jnr);

								p.sendTitle(newPRecord1, newPRecord2);

								try {
									JNR.stats.save(JNR.file3);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}

							if (!JNR.stats.contains(jnr + ".globalBestTime")
									|| JNR.stats.getDouble(jnr + ".globalBestTime") > time
									|| JNR.stats.getDouble(jnr + ".globalBestTime") == 0.0) {
								JNR.stats.set(jnr + ".globalBestTime", time);

								String newGRecord1 = JNR.messages.getString("Messages.NewGlobalRecord.Title.1")
										.replaceAll("&", "§").replaceAll("%map%", jnr);
								String newGRecord2 = JNR.messages.getString("Messages.NewGlobalRecord.Title.2")
										.replaceAll("&", "§").replaceAll("%map%", jnr);

								p.sendTitle(newGRecord1, newGRecord2);

								try {
									JNR.stats.save(JNR.file3);
								} catch (IOException e1) {
									e1.printStackTrace();
								}

								if (JNR.data.getBoolean("SendBroadcastAtNewRecord")) {
									String broadcastMsg = JNR.messages.getString("Messages.NewGlobalRecord.Broadcast")
											.replaceAll("&", "§").replaceAll("%map%", jnr)
											.replaceAll("%player%", p.getDisplayName())
											.replaceAll("%time%", JNRCommand.calculateTime(time).toString());

									Bukkit.broadcastMessage(JNR.prefix + broadcastMsg);
								}

								if (JNR.data.getBoolean("EnableVault")) {
									JNR.eco.depositPlayer(p.getName(), eWin);

									String bonusReceivedMsg = JNR.messages.getString("Messages.BonusForNewRecord.Vault")
											.replaceAll("&", "§").replaceAll("%bonus%", String.valueOf(eWin))
											.replaceAll("%moneyName%", JNR.getMoneyName());

									p.sendMessage(JNR.prefix + bonusReceivedMsg);
								}
							}
						}

					}, 50L);

					int fails = StartListener.fails.get(p.getName());

					p.sendMessage("§8===============");
					p.sendMessage("");
					p.sendMessage("§aMap§7: §6" + jnr);
					p.sendMessage("§aZeit§7: " + JNRCommand.calculateTime(time));
					p.sendMessage("§aFails§7: §6" + fails);
					p.sendMessage("");
					p.sendMessage("§8===============");

					JNR.stats.set(p.getName() + "." + jnr + ".fails", fails);

					try {
						JNR.stats.save(JNR.file3);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					StartListener.fails.remove(p.getName());
					StartListener.timer.remove(p.getName());
					StartListener.time.remove(p.getName());
				} else {
					Long millis = System.currentTimeMillis();

					String errorMsg = JNR.messages.getString("Messages.NotAllCheckpoints").replaceAll("&", "§");

					if (!wait.containsKey(p.getName())) {
						p.sendMessage(JNR.prefix + errorMsg);
						wait.put(p.getName(), millis);
					} else if (wait.containsKey(p.getName())) {

						Long last = wait.get(p.getName());

						if (last + 5000 > millis) {
							return;
						}
						wait.put(p.getName(), millis);
						p.sendMessage(JNR.prefix + errorMsg);
					}
				}
			} else if (p.getLocation().getBlock().getType().getId() == JNR.data.getInt("CheckpointBlock")
					|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().getId() == JNR.data
							.getInt("CheckpointBlock")) {

				String jnr = StartListener.playing.get(p.getName());

				if (StartListener.checkpoint.get(p.getName()) != 0) {
					World world = p.getWorld();

					int x = (int) JNR.data.getDouble(jnr + "." + StartListener.checkpoint.get(p.getName()) + ".X");
					int y = (int) JNR.data.getDouble(jnr + "." + StartListener.checkpoint.get(p.getName()) + ".Y");
					int z = (int) JNR.data.getDouble(jnr + "." + StartListener.checkpoint.get(p.getName()) + ".Z");

					Location loc = new Location(world, x, y, z);

					int x1 = (int) p.getLocation().getBlock().getLocation().getX();
					int y1 = (int) p.getLocation().getBlock().getLocation().getY();
					int z1 = (int) p.getLocation().getBlock().getLocation().getZ();

					Location loc1 = new Location(world, x1, y1, z1);

					if (loc.equals(loc1) || !(loc.distance(loc1) > 2)) {
						String cpReached1 = JNR.messages.getString("Messages.CheckpointReached.Title.1")
								.replaceAll("&", "§")
								.replaceAll("%checkpoint%", StartListener.checkpoint.get(p.getName()).toString());
						String cpReached2 = JNR.messages.getString("Messages.CheckpointReached.Title.2")
								.replaceAll("&", "§")
								.replaceAll("%checkpoint%", StartListener.checkpoint.get(p.getName()).toString());
						p.sendTitle(cpReached1, cpReached2);
						StartListener.checkpoint.put(p.getName(), StartListener.checkpoint.get(p.getName()) + 1);
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

						getCPTime(p);
					}
				} else {
					if (wait2.containsKey(p.getName())) {
						Long millis = System.currentTimeMillis();
						Long last = wait2.get(p.getName());

						if (last + 1000 > millis) {
							return;
						}
						wait2.put(p.getName(), millis);
					} else {
						StartListener.checkpoint.put(p.getName(), StartListener.checkpoint.get(p.getName()) + 1);
						String cpReached1 = JNR.messages.getString("Messages.CheckpointReached.Title.1")
								.replaceAll("&", "§")
								.replaceAll("%checkpoint%", StartListener.checkpoint.get(p.getName()).toString());
						String cpReached2 = JNR.messages.getString("Messages.CheckpointReached.Title.2")
								.replaceAll("&", "§")
								.replaceAll("%checkpoint%", StartListener.checkpoint.get(p.getName()).toString());
						p.sendTitle(cpReached1, cpReached2);
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					}

					getCPTime(p);
				}
			}
		}

	}

	public void getCPTime(Player p) {
		long time = (System.nanoTime() - StartListener.time.get(p.getName())) / 1000000;

		String cpTimeMsg = JNR.messages.getString("Messages.CheckpointReached.Time").replaceAll("&", "§")
				.replaceAll("%time%", JNRCommand.calculateTime(time));
		p.sendMessage(cpTimeMsg);
	}

	public static void resetInventory(Player p) {
		ItemStack[] content = StartListener.inventory.get(p.getName());
		p.getInventory().setContents(content);
		p.updateInventory();
	}

	@EventHandler
	public void onFallDMG(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (StartListener.playing.containsKey(p.getName())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (StartListener.playing.containsKey(p.getName())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if (StartListener.playing.containsKey(e.getWhoClicked().getName())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		if (StartListener.playing.containsKey(p.getName())) {
			String jnr = StartListener.playing.get(p.getName());

			JNR.invs.set(p.getName() + ".Map", jnr);
			JNR.invs.set(p.getName() + ".isPlaying", true);
			JNR.invs.set(p.getName() + ".Inv", StartListener.inventory.get(p.getName()));

			try {
				JNR.invs.save(JNR.file2);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			StartListener.checkpoint.remove(p.getName());

			for (Player all : Bukkit.getOnlinePlayers()) {
				p.showPlayer(all);
			}
		}
	}

	@EventHandler
	public void onBuild(BlockPlaceEvent e) {
		if (StartListener.playing.containsKey(e.getPlayer().getName())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player) {
			if (StartListener.playing.containsKey(e.getEntity().getName())) {
				e.setCancelled(true);
			}
		}
	}

}