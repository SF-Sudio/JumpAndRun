package de.sebli.jnr;

import org.bukkit.entity.Player;

public class ActionBar18 {

	public static void sendToPlayer(Player p, String text) {
		net.minecraft.server.v1_8_R3.PacketPlayOutChat packet = new net.minecraft.server.v1_8_R3.PacketPlayOutChat(
				net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"),
				(byte) 2);

		((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	
}
