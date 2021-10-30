package me.kristianskaneps.bukkitwebintegration.database.table;

import me.kristianskaneps.bukkitwebintegration.database.Database;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.SQLDataType;
import org.jooq.types.ULong;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.jooq.impl.DSL.*;

public final class PlayersTable
{
	private final Table<? extends Record> table;

	private final Field<ULong> id;
	private final Field<UUID> uuid;
	private final Field<String> name;
	private final Field<Boolean> online;
	private final Field<LocalDateTime> last_login;
	private final Field<LocalDateTime> last_logout;
	private final Field<String> ip;
	private final Field<LocalDateTime> created_at;

	public PlayersTable(String tableName)
	{
		table = table(name(tableName));
		id = field(name("id"), SQLDataType.BIGINTUNSIGNED);
		uuid = field(name("uuid"), SQLDataType.UUID);
		name = field(name("name"), SQLDataType.VARCHAR);
		online = field(name("online"), SQLDataType.BOOLEAN);
		last_login = field(name("last_login"), SQLDataType.LOCALDATETIME);
		last_logout = field(name("last_logout"), SQLDataType.LOCALDATETIME);
		ip = field(name("ip"), SQLDataType.VARCHAR);
		created_at = field(name("created_at"), SQLDataType.LOCALDATETIME);
	}

	private String getPlayerIp(Player player)
	{
		String playerIp = null;
		final InetSocketAddress playerSocketAddress = player.getAddress();
		if(playerSocketAddress != null) playerIp = playerSocketAddress.getAddress().getHostAddress();
		return playerIp;
	}

	public boolean exists(UUID playerUuid)
	{
		final DSLContext query = Database.query();
		return query.fetchExists(query.selectFrom(table).where(uuid.eq(playerUuid)));
	}

	private void insert(Player player)
	{
		final LocalDateTime now = LocalDateTime.now();
		Database.query()
				.insertInto(table, uuid, name, last_login, ip, created_at)
				.values(player.getUniqueId(), player.getName(), now, getPlayerIp(player), now)
				.execute();
	}

	public void onLogin(Player player)
	{
		if(exists(player.getUniqueId()))
		{
			Database.query()
					.update(table)
					.set(name, player.getName())
					.set(online, true)
					.set(last_login, LocalDateTime.now())
					.set(ip, getPlayerIp(player))
					.where(uuid.eq(player.getUniqueId()))
					.execute();
		}
		else
		{
			insert(player);
		}
	}

	public void onLogout(Player player)
	{
		if(exists(player.getUniqueId()))
		{
			Database.query()
					.update(table)
					.set(online, false)
					.set(last_logout, LocalDateTime.now())
					.where(uuid.eq(player.getUniqueId()))
					.execute();
		}
	}
}
