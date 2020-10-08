package de.sebli.jnr.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.sebli.jnr.JNR;

public class DamageListener implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();

			if (StartListener.playing.containsKey(p.getName())) {
				if (e.getCause().equals(DamageCause.FALL)) {
					if (!JNR.getInstance().getConfig().getBoolean("EnableFallDamage")) {
						e.setCancelled(true);
					} else {
						e.setCancelled(false);
					}
				}

				if (p.getHealth() - e.getDamage() <= 0) {
					e.setCancelled(true);
					p.setHealth(p.getMaxHealth());
					ItemListener.toLastCP(p);
				}
			}

			if (StartListener.cooldown.contains(p.getName())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDmgByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (StartListener.playing.containsKey(e.getDamager().getName())) {
				e.setCancelled(true);
			}
		}
	}

}
