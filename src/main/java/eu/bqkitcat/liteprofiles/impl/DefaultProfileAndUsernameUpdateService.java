package eu.bqkitcat.liteprofiles.impl;

import com.mojang.authlib.GameProfile;
import eu.bqkitcat.liteprofiles.LiteProfiles;
import eu.bqkitcat.liteprofiles.repository.LiteProfileRepository;
import io.papermc.paper.profile.PaperFilledProfileCache;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserNameToIdResolver;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class DefaultProfileAndUsernameUpdateService implements ProfileAndUsernameUpdateService {

	private final Set<ProfileAndUsernameUpdateService> additionalImplementations = new HashSet<>();

	public void addAdditionalService(ProfileAndUsernameUpdateService service) {
		additionalImplementations.add(service);
	}

	public void updateAll() {
		OfflinePlayer[] offlinePlayers = LiteProfiles.getInstance().getServer().getOfflinePlayers();
		for (OfflinePlayer offlinePlayer : offlinePlayers) {
			assert offlinePlayer != null;
			assert offlinePlayer.getName() != null;
			if (!offlinePlayer.getName().contains(".")) {
				update(offlinePlayer.getUniqueId(), offlinePlayer.getName());
			}
		}
	}

	public void update(UUID uuid, String username) {
		MinecraftServer server = MinecraftServer.getServer();
		PaperFilledProfileCache profileCache = server.services().paper().filledProfileCache();
		UserNameToIdResolver userNameToIdResolver = server.services().nameToIdCache();

		List<UUID> slaves = LiteProfileRepository.getAllSlaveUUIDs(uuid);
		for (int i = 0; i < slaves.size(); i++) {
			UUID slaveUuid = slaves.get(i);
			String slaveUsername;
			if (slaveUuid.equals(uuid)) {
				slaveUsername = username + ".0";
				updateInternally(username, slaveUuid, profileCache, userNameToIdResolver);
			} else {
				slaveUsername = username + "." + (i + 1);
			}
			updateInternally(slaveUsername, slaveUuid, profileCache, userNameToIdResolver);
		}
	}

	private void updateInternally(String username, UUID uuid,
										 PaperFilledProfileCache profileCache,
										 UserNameToIdResolver userNameToIdResolver) {
		GameProfile profile = new GameProfile(uuid, username);
		profileCache.add(profile);
		userNameToIdResolver.add(new NameAndId(profile));
		for (ProfileAndUsernameUpdateService service : additionalImplementations) {
			service.update(uuid, username);
		}
	}

}
