package me.kristianskaneps.bukkitwebintegration.commands;

import at.favre.lib.crypto.bcrypt.BCrypt;
import me.kristianskaneps.bukkitwebintegration.Notification;
import me.kristianskaneps.bukkitwebintegration.WebIntegration;
import me.kristianskaneps.bukkitwebintegration.database.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AuthCommand extends Executor<WebIntegration>
{
	private final WebIntegration instance;

	public AuthCommand(WebIntegration instance)
	{
		this.instance = instance;
	}

	@Override
	protected WebIntegration getPluginInstance()
	{
		return instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		final Player player = (Player) sender;

		if("register".equalsIgnoreCase(label)) return onRegisterCommand(player, command, args);
		if("login".equalsIgnoreCase(label)) return onLoginCommand(player, command, args);
		if("logout".equalsIgnoreCase(label)) return onLogoutCommand(player, command, args);

		return false;
	}

	private boolean onRegisterCommand(Player player, Command command, String[] args)
	{
		if(Database.playerCredentialsTable().exists(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.GREEN + "You are already registered.");
			return true;
		}

		if(args.length != 2)
			player.sendMessage(ChatColor.RED + "Usage: /register <password> <password>");
		else if(!args[0].equals(args[1]))
			player.sendMessage(ChatColor.RED + "Passwords do not match!");
		else
			register(player, args[0]);

		return true;
	}

	private boolean onLoginCommand(Player player, Command command, String[] args)
	{
		if(!Database.playersTable().exists(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "You must first register.");
			return true;
		}

		if(Database.playersTable().isAuthenticated(player))
		{
			player.sendMessage(ChatColor.GREEN + "You are already authenticated.");
			return true;
		}

		if(args.length != 1)
			player.sendMessage(ChatColor.RED + "Usage: /login <password>");
		else
			login(player, args[0]);

		return true;
	}

	private boolean onLogoutCommand(Player player, Command command, String[] args)
	{
		if(!Database.playersTable().isAuthenticated(player))
		{
			player.sendMessage(ChatColor.GREEN + "You are already logged out.");
			return true;
		}

		if(args.length != 0)
			player.sendMessage(ChatColor.RED + "Usage: /logout");
		else
			logout(player);
		return true;
	}

	private void register(Player player, String password)
	{
		final byte[] hash = BCrypt
				.with(new SecureRandom())
				.hash(10, password.getBytes(StandardCharsets.UTF_8));

		Database.playerCredentialsTable().onRegister(player, new String(hash, StandardCharsets.UTF_8));
		Database.playersTable().setAuthenticated(player, true);

		instance.pushNotification(new Notification(Notification.Type.PLAYER_REGISTER, player));

		player.sendMessage(ChatColor.GREEN + "Successfully registered!");
	}

	private void login(Player player, String password)
	{
		final String hashedPassword = Database.playerCredentialsTable().getPassword(player);

		if(hashedPassword == null)
		{
			player.sendMessage(ChatColor.RED + "You aren't registered.");
			return;
		}

		BCrypt.Result result = BCrypt.verifyer().verify(
				password.getBytes(StandardCharsets.UTF_8),
				hashedPassword.getBytes(StandardCharsets.UTF_8)
		);

		if(!result.verified)
		{
			player.sendMessage(ChatColor.RED + "Incorrect password.");
			instance.pushNotification(new Notification(Notification.Type.PLAYER_FAILED_LOGIN, player));
			return;
		}

		Database.playersTable().setAuthenticated(player, true);

		instance.pushNotification(new Notification(Notification.Type.PLAYER_LOGIN, player));

		player.sendMessage(ChatColor.GREEN + "Logged in!");
	}

	private void logout(Player player)
	{
		Database.playersTable().setAuthenticated(player, false);

		instance.pushNotification(new Notification(Notification.Type.PLAYER_LOGOUT, player));

		player.sendMessage(ChatColor.GREEN + "Logged out!");
	}
}
