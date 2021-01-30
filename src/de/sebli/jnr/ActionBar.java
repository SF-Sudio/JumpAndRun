package de.sebli.jnr;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBar {

	public static void sendActionbar(Player player, String msg) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
//		try {
//			Constructor<?> constructor = getNMSClass("PacketPlayOutChat")
//					.getConstructor(getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType"));
//
//			Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
//					.invoke(null, "{\"text\":\"" + msg + "\"}");
//			Object packet = constructor.newInstance(icbc, getNMSClass("ChatMessageType").getEnumConstants()[2]);
//			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
//			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
//
//			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
//		} catch (Exception e) {
//			e.printStackTrace();
//
//			ActionBar18.sendToPlayer(player, msg);
//		}
	}

//	public static Class<?> getNMSClass(String name) {
//		try {
//			return Class.forName("net.minecraft.server."
//					+ Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
//		} catch (ClassNotFoundException e) {
//			return null;
//		}
//	}

}
