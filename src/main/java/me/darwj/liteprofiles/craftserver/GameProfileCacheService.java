package me.darwj.liteprofiles.craftserver;

import com.mojang.authlib.GameProfile;
import me.darwj.liteprofiles.repository.LiteProfileRepository;
import net.minecraft.server.players.GameProfileCache;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;

import java.util.List;
import java.util.UUID;

public final class GameProfileCacheService {
	private final GameProfileCache gameProfileCache;

	public GameProfileCacheService(CraftServer server) {
		this.gameProfileCache = server.getHandle().getServer().getProfileCache();
	}

	public void Update(UUID uuid, String userName) {
		List<UUID> slaves = LiteProfileRepository.getAllSlaveUUIDs(uuid);
		for (int i = 0; i < slaves.size(); i++)
		{
			String name = userName + "." + i;
			if (gameProfileCache.get(name).isEmpty()) {
				gameProfileCache.add(new GameProfile(slaves.get(i), name));
			}
		}
	}
}
