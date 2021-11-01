package me.kristianskaneps.bukkitwebintegration.events;

import me.kristianskaneps.bukkitwebintegration.Notification;
import me.kristianskaneps.bukkitwebintegration.NotificationListener;
import me.kristianskaneps.bukkitwebintegration.WebIntegration;
import me.kristianskaneps.bukkitwebintegration.config.AuthConfig;
import me.kristianskaneps.bukkitwebintegration.config.ConfigManager;
import me.kristianskaneps.bukkitwebintegration.database.Database;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UnauthenticatedPlayerEvents implements Listener, NotificationListener
{
	private static final Notification.Type[] SUPPORTED_NOTIFICATIONS = {
			Notification.Type.PLAYER_LOGIN,
			Notification.Type.PLAYER_REGISTER,
			Notification.Type.PLAYER_QUIT,
			Notification.Type.PLAYER_LOGOUT
	};

	protected final WebIntegration instance;

	private final Set<UUID> authenticated = new HashSet<>();
	private final AuthConfig auth;

	public UnauthenticatedPlayerEvents(WebIntegration instance)
	{
		this.instance = instance;
		auth = ConfigManager.auth.get();
	}

	@Override
	public void onNotification(Notification notification)
	{
		final Player player = (Player) notification.value;
		switch (notification.type)
		{
			case PLAYER_LOGIN:
			case PLAYER_REGISTER: authenticated.add(player.getUniqueId()); break;
			case PLAYER_QUIT:
			case PLAYER_LOGOUT: authenticated.remove(player.getUniqueId()); break;
		}
	}

	@Override
	public Notification.Type[] getSupportedNotifications()
	{
		return SUPPORTED_NOTIFICATIONS;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		if(auth.isOptional()) return;
		if(!event.isAsynchronous()) return; // Player was compelled to send a message.

		final Player player = event.getPlayer();
		final UUID playerUuid = player.getUniqueId();

		if(auth.mandatory_register && !Database.playerCredentialsTable().exists(playerUuid))
		{
			mustRegister(player);
		}
		else
		{
			if(authenticated.contains(playerUuid)) return; // Player is authenticated.
			if(Database.playersTable().isAuthenticated(player)) // Player is authenticated.
			{
				authenticated.add(playerUuid);
				return;
			}

			mustLogin(player);
		}

		event.setCancelled(true);
	}

	private void mustRegister(Player player)
	{
		player.sendMessage(ChatColor.RED + "You must register!");
	}

	private void mustLogin(Player player)
	{
		player.sendMessage(ChatColor.RED + "You must log in!");
	}
}
