package me.kristianskaneps.bukkitwebintegration.events;

import me.kristianskaneps.bukkitwebintegration.Notification;
import me.kristianskaneps.bukkitwebintegration.WebIntegration;
import me.kristianskaneps.bukkitwebintegration.database.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener
{
	protected final WebIntegration instance;

	public PlayerEvents(WebIntegration instance)
	{
		this.instance = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		Database.playersTable().onJoin(player);
		instance.pushNotification(new Notification(Notification.Type.PLAYER_JOIN, player));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		final Player player = event.getPlayer();
		Database.playersTable().onQuit(player);
		instance.pushNotification(new Notification(Notification.Type.PLAYER_QUIT, player));
	}
}
