package me.darwj.liteprofiles.craftserver;

import com.mojang.authlib.GameProfile;
import io.papermc.paper.profile.PaperFilledProfileCache;
import me.darwj.liteprofiles.repository.LiteProfileRepository;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.UUID;

public final class GameProfileCacheService {
	private final PaperFilledProfileCache gameProfileCache;

	public GameProfileCacheService() {
        MinecraftServer server = MinecraftServer.getServer();
		this.gameProfileCache = server.services().paper().filledProfileCache();
	}

	public void update(UUID uuid, String userName) {
		List<UUID> slaves = LiteProfileRepository.getAllSlaveUUIDs(uuid);
		for (int i = 0; i < slaves.size(); i++)
		{
			String name = userName + "." + i;
			if (gameProfileCache.getIfCached(name) == null) {
				gameProfileCache.add(new GameProfile(slaves.get(i), name));
			}
		}
	}
}
