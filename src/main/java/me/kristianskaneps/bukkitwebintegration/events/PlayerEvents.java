package me.kristianskaneps.bukkitwebintegration.events;

import me.kristianskaneps.bukkitwebintegration.WebIntegration;
import me.kristianskaneps.bukkitwebintegration.database.Database;
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
		Database.playersTable().onLogin(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		Database.playersTable().onLogout(event.getPlayer());
	}
}
