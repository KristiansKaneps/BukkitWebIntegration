package me.kristianskaneps.bukkitwebintegration.util;

import org.bukkit.entity.Player;

import java.net.InetSocketAddress;

public final class PlayerUtils
{
	private PlayerUtils() {}

	public static String getPlayerIP(Player player)
	{
		String playerIp = null;
		final InetSocketAddress playerSocketAddress = player.getAddress();
		if(playerSocketAddress != null) playerIp = playerSocketAddress.getAddress().getHostAddress();
		return playerIp;
	}
}
