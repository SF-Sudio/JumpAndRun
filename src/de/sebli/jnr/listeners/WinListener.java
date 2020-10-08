package de.sebli.jnr.listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material
					.getMaterial(JNR.data.getString("WinBlock"))
					|| p.getLocation().getBlock().getType() == Material.getMaterial(JNR.data.getString("WinBlock"))) {
				if (StartListener.checkpoint.get(p.getName()) > JNR.data
						.getInt(StartListener.playing.get(p.getName()) + ".Checkpoints")
						|| !JNR.getInstance().getConfig().getBoolean("NeedAllCheckpointsToWin")) {

					String winTitle1 = JNR.messages.getString("Messages.Win.Title.1").replaceAll("&", "§")
							.replaceAll("%map%", StartListener.playing.get(p.getName()));

					String winTitle2 = JNR.messages.getString("Messages.Win.Title.2").replaceAll("&", "§")
							.replaceAll("%map%", StartListener.playing.get(p.getName()));

					p.sendTitle(winTitle1, winTitle2);

					String jnr = StartListener.playing.get(p.getName());

					if (JNR.stats.contains(p.getName() + "." + jnr + ".finishedTimes")) {
						int ft = JNR.stats.getInt(p.getName() + "." + jnr + ".finishedTimes");
						JNR.stats.set(p.getName() + "." + jnr + ".finishedTimes", ft + 1);
					} else {
						JNR.stats.set(p.getName() + "." + jnr + ".finishedTimes", 1);
					}

					int fails = StartListener.fails.get(p.getName());
					long time = (System.nanoTime() - StartListener.time.get(p.getName())) / 1000000;

					List<String> finishedStats = JNR.messages.getStringList("Messages.Win.Stats");

					for (String message : finishedStats) {
						p.sendMessage(message.replaceAll("&", "§").replaceAll("%map%", jnr)
								.replaceAll("%time%", JNRCommand.calculateTime(time))
								.replaceAll("%fails%", String.valueOf(fails)));
					}

					JNR.stats.set(p.getName() + "." + jnr + ".fails", fails);

					try {
						JNR.stats.save(JNR.file3);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

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

								if (JNR.getInstance().getConfig().getBoolean("SendBroadcastAtNewRecord")) {
									String broadcastMsg = JNR.messages.getString("Messages.NewGlobalRecord.Broadcast")
											.replaceAll("&", "§").replaceAll("%map%", jnr)
											.replaceAll("%player%", p.getDisplayName())
											.replaceAll("%time%", JNRCommand.calculateTime(time).toString());

									if (!broadcastMsg.equalsIgnoreCase("x"))
										Bukkit.broadcastMessage(JNR.prefix + broadcastMsg);
								}
							}
						}

					}, 60L);

					WinListener.reset(p);
				} else {
					Long millis = System.currentTimeMillis();

					String errorMsg = JNR.messages.getString("Messages.NotAllCheckpoints").replaceAll("&", "§");

					if (!wait.containsKey(p.getName())) {
						if (!errorMsg.equalsIgnoreCase("x"))
							p.sendMessage(JNR.prefix + errorMsg);
						wait.put(p.getName(), millis);
					} else if (wait.containsKey(p.getName())) {

						Long last = wait.get(p.getName());

						if (last + 5000 > millis) {
							return;
						}
						wait.put(p.getName(), millis);
						if (!errorMsg.equalsIgnoreCase("x"))
							p.sendMessage(JNR.prefix + errorMsg);
					}
				}
			} else if (p.getLocation().getBlock().getType() == Material
					.getMaterial(JNR.data.getString("CheckpointBlock"))
					|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material
							.getMaterial(JNR.data.getString("CheckpointBlock"))) {

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
						try {
							p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
						} catch (NoSuchFieldError ex) {
							p.playSound(p.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1.0F, 1.0F);
						}

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
						try {
							p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
						} catch (NoSuchFieldError ex) {
							p.playSound(p.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1.0F, 1.0F);
						}
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
		if (!cpTimeMsg.equalsIgnoreCase("x"))
			p.sendMessage(cpTimeMsg);
	}

	public static void reset(Player p) {
		if (StartListener.playing.containsKey(p.getName())) {
			StartListener.startCountdown.remove(p.getName());

			StartListener.cooldown.add(p.getName());

			String jnr = (String) JNR.playerData.get(p.getName() + ".Map");

			if (!JNR.data.contains(jnr + ".Leave")) {
				World world = Bukkit.getWorld(JNR.playerData.getString(p.getName() + ".Location.World"));
				double x = JNR.playerData.getDouble(p.getName() + ".Location.X");
				double y = JNR.playerData.getDouble(p.getName() + ".Location.Y");
				double z = JNR.playerData.getDouble(p.getName() + ".Location.Z");
				float yaw = (float) JNR.playerData.getDouble(p.getName() + ".Location.Yaw");
				float pitch = (float) JNR.playerData.getDouble(p.getName() + ".Location.Pitch");

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

			StartListener.playing.remove(p.getName());
			StartListener.checkpoint.remove(p.getName());
			StartListener.time.remove(p.getName());
			StartListener.timer.remove(p.getName());

			for (Player all : Bukkit.getOnlinePlayers()) {
				p.showPlayer(all);
			}

			Bukkit.getScheduler().runTask(JNR.getInstance(), new Runnable() {

				@SuppressWarnings({ "unchecked", "deprecation" })
				@Override
				public void run() {
					p.setGameMode(GameMode.getByValue(JNR.playerData.getInt(p.getName() + ".Gamemode")));
					p.setHealth(JNR.playerData.getDouble(p.getName() + ".Health"));
					p.setFoodLevel(JNR.playerData.getInt(p.getName() + ".FoodLevel"));

					try {
						p.getInventory().setContents((ItemStack[]) JNR.playerData.get(p.getName() + ".Inv"));
					} catch (Exception e) {
						List<ItemStack> items = (List<ItemStack>) JNR.playerData.get(p.getName() + ".Inv");
						ItemStack[] content = new ItemStack[items.size()];

						int i = 0;
						for (ItemStack item : items) {
							content[i] = item;

							i++;
						}

						p.getInventory().setContents(content);
					}

					p.updateInventory();

					JNR.playerData.set(p.getName(), null);

					StartListener.cooldown.remove(p.getName());

					try {
						JNR.playerData.save(JNR.file2);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
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

			JNR.playerData.set(p.getName() + ".Map", jnr);
			JNR.playerData.set(p.getName() + ".isPlaying", true);

			try {
				JNR.playerData.save(JNR.file2);
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
	public void onBreak(BlockBreakEvent e) {
		if (StartListener.playing.containsKey(e.getPlayer().getName())) {
			e.setCancelled(true);
		}

		if (e.getBlock().getState() instanceof Sign) {
			Sign sign = (Sign) e.getBlock().getState();

			if (sign.getLine(0).isEmpty())
				return;

			List<String> signLocs = new ArrayList<>();

			if (JNR.data.contains("JoinSign." + sign.getLine(1))) {
				signLocs = JNR.data.getStringList("JoinSign." + sign.getLine(1));
			}

			String signLocStr = sign.getLocation().getWorld().getName() + " : " + sign.getLocation().getX() + ", "
					+ sign.getLocation().getY() + ", " + sign.getLocation().getZ();

			if (signLocs.contains(signLocStr)) {
				signLocs.remove(signLocStr);

				JNR.data.set("JoinSign." + sign.getLine(1), signLocs);

				try {
					JNR.data.save(JNR.file);

					e.getPlayer().sendMessage(JNR.prefix + "§cJoin-Schild wurde entfernt.");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
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