package me.kristianskaneps.bukkitwebintegration.database.table;

import me.kristianskaneps.bukkitwebintegration.database.Database;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.SQLDataType;
import org.jooq.types.ULong;

import java.time.LocalDateTime;
import java.util.UUID;

import static me.kristianskaneps.bukkitwebintegration.util.PlayerUtils.getPlayerIP;
import static org.jooq.impl.DSL.*;

public final class PlayersTable
{
	private final Table<? extends Record> table;

	private final Field<ULong> id;
	private final Field<UUID> uuid;
	private final Field<String> name;
	private final Field<Boolean> online;
	private final Field<LocalDateTime> last_login, last_logout;
	private final Field<String> login_world;
	private final Field<Double> login_x, login_y, login_z;
	private final Field<String> logout_world;
	private final Field<Double> logout_x, logout_y, logout_z;
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
		login_world = field(name("login_world"), SQLDataType.VARCHAR);
		login_x = field(name("login_x"), SQLDataType.DOUBLE);
		login_y = field(name("login_y"), SQLDataType.DOUBLE);
		login_z = field(name("login_z"), SQLDataType.DOUBLE);
		logout_world = field(name("logout_world"), SQLDataType.VARCHAR);
		logout_x = field(name("logout_x"), SQLDataType.DOUBLE);
		logout_y = field(name("logout_y"), SQLDataType.DOUBLE);
		logout_z = field(name("logout_z"), SQLDataType.DOUBLE);
		ip = field(name("ip"), SQLDataType.VARCHAR);
		created_at = field(name("created_at"), SQLDataType.LOCALDATETIME);
	}

	public boolean exists(UUID playerUuid)
	{
		final DSLContext query = Database.query();
		return query.fetchExists(query.selectFrom(table).where(uuid.eq(playerUuid)));
	}

	private void insert(Player player)
	{
		final LocalDateTime now = LocalDateTime.now();
		final Location loc = player.getLocation();
		final World world = loc.getWorld();
		Database.query()
				.insertInto(
						table,
						uuid,
						name,
						last_login,
						login_world,
						login_x,
						login_y,
						login_z,
						ip,
						created_at
				).values(
						player.getUniqueId(),
						player.getName(),
						now,
						world == null ? null : world.getName(),
						loc.getX(),
						loc.getY(),
						loc.getZ(),
						getPlayerIP(player),
						now
				).execute();
	}

	public void onLogin(Player player)
	{
		if(exists(player.getUniqueId()))
		{
			final Location loc = player.getLocation();
			final World world = loc.getWorld();
			Database.query()
					.update(table)
					.set(name, player.getName())
					.set(online, true)
					.set(last_login, LocalDateTime.now())
					.set(login_world, world == null ? null : world.getName())
					.set(login_x, loc.getX())
					.set(login_y, loc.getY())
					.set(login_z, loc.getZ())
					.set(ip, getPlayerIP(player))
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
			final Location loc = player.getLocation();
			final World world = loc.getWorld();
			Database.query()
					.update(table)
					.set(online, false)
					.set(last_logout, LocalDateTime.now())
					.set(logout_world, world == null ? null : world.getName())
					.set(logout_x, loc.getX())
					.set(logout_y, loc.getY())
					.set(logout_z, loc.getZ())
					.where(uuid.eq(player.getUniqueId()))
					.execute();
		}
	}
}
