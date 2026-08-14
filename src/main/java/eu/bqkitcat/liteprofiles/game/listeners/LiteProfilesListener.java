package eu.bqkitcat.liteprofiles.game.listeners;

import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import eu.bqkitcat.liteprofiles.LiteProfiles;
import eu.bqkitcat.liteprofiles.domain.LiteProfile;
import eu.bqkitcat.liteprofiles.repository.LiteProfileRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class LiteProfilesListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLoginEvent(@NotNull AsyncPlayerPreLoginEvent event) {

        UUID ownerUUID = event.getUniqueId();
        if (LiteProfileRepository.hasProfile(ownerUUID)) {

            LiteProfile profile = LiteProfileRepository.getActiveProfile(ownerUUID);
            if (ownerUUID.equals(profile.getUUID())) {
                LiteProfileRepository.syncMainProfile(ownerUUID, event.getPlayerProfile().getProperties());
                LiteProfileRepository.setActiveProfile(ownerUUID, ownerUUID);
                LiteProfiles.getProfileAndUsernameUpdateService().update(ownerUUID, event.getName());
                return;
            }

            LiteProfileRepository.syncMainProfile(ownerUUID, event.getPlayerProfile().getProperties());
            LiteProfileRepository.updateActiveProfileProperties(ownerUUID, event.getPlayerProfile().getProperties());

            PlayerProfile newPlayerProfile = Bukkit.createProfile(profile.getUUID(), event.getName());
            newPlayerProfile.setProperties(profile.getProfileProperties());
            event.setPlayerProfile(newPlayerProfile);

            LiteProfileRepository.setActiveProfile(ownerUUID, profile.getUUID());

        } else {
            LiteProfile profile = new LiteProfile(ownerUUID, event.getPlayerProfile().getProperties());
            LiteProfileRepository.registerProfile(ownerUUID, profile);

            LiteProfileRepository.setActiveProfile(ownerUUID, ownerUUID);
        }
        LiteProfiles.getProfileAndUsernameUpdateService().update(ownerUUID, event.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        UUID masterUuid = LiteProfileRepository.findOwnerOfUUID(event.getPlayer().getUniqueId());
        LiteProfiles.getProfileAndUsernameUpdateService().update(masterUuid, event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLookupProfileEvent(@NotNull PreLookupProfileEvent event) {
        if (event.getName().contains(".")) {
            String[] nameSplit = event.getName().split("\\.");
            String actualName = nameSplit[0];
            int index;
            try {
                index = Integer.parseInt(nameSplit[1]);
            } catch (Exception ex) {
                return;
            }
            if (index < 0 || index > 64) {
                return;
            }
            // Note: should generally be fast, could sometimes be slow because it introduces an extra lookup for
            // every lookup at least once
            OfflinePlayer masterOfflinePlayer = LiteProfiles.getInstance().getServer().getOfflinePlayer(actualName);
            //
            UUID masterUUID = masterOfflinePlayer.getUniqueId();
            if (LiteProfileRepository.hasProfile(masterUUID)) {
                if (index == 0) {
                    event.setUUID(masterUUID);
                    return;
                }
                List<UUID> slaveUUIDs = LiteProfileRepository.getAllSlaveUUIDs(masterUUID);
                if (index > slaveUUIDs.size()) {
                    return;
                }
                event.setUUID(slaveUUIDs.get(index));
            }
        }
    }

}
