package me.kristianskaneps.bukkitwebintegration.database.table;

import me.kristianskaneps.bukkitwebintegration.database.Database;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.SQLDataType;
import org.jooq.types.ULong;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.jooq.impl.DSL.*;

public class PlayerCredentialsTable
{
	private final Table<? extends Record> table;

	private final Field<ULong> id;
	private final Field<UUID> uuid;
	private final Field<String> password;
	private final Field<LocalDateTime> updated_at;
	private final Field<LocalDateTime> created_at;

	public PlayerCredentialsTable(String tableName)
	{
		table = table(name(tableName));
		id = field(name("id"), SQLDataType.BIGINTUNSIGNED);
		uuid = field(name("uuid"), SQLDataType.UUID);
		password = field(name("password"), SQLDataType.VARCHAR);
		updated_at = field(name("updated_at"), SQLDataType.LOCALDATETIME);
		created_at = field(name("created_at"), SQLDataType.LOCALDATETIME);
	}

	public boolean exists(UUID playerUuid)
	{
		final DSLContext query = Database.query();
		return query.fetchExists(query.selectFrom(table).where(uuid.eq(playerUuid)));
	}

	private void insert(UUID playerUuid, String password)
	{
		final LocalDateTime now = LocalDateTime.now();
		Database.query()
				.insertInto(
						table,
						uuid,
						this.password,
						updated_at,
						created_at
				).values(
						playerUuid,
						password,
						now,
						now
				).execute();
	}

	public void onRegister(Player player, String password)
	{
		if(exists(player.getUniqueId())) return;
		insert(player.getUniqueId(), password);
	}

	public void onChangePassword(Player player, String password)
	{
		if(exists(player.getUniqueId()))
		{
			Database.query()
					.update(table)
					.set(this.password, password)
					.where(uuid.eq(player.getUniqueId()))
					.execute();
		}
	}

	public String getPassword(Player player)
	{
		if(exists(player.getUniqueId()))
		{
			return Database.query()
					.selectFrom(table)
					.where(uuid.eq(player.getUniqueId()))
					.fetchSingle(password);
		}

		return null;
	}
}
