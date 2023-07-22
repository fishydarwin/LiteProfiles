package me.darwj.liteprofiles.game.listeners;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.darwj.liteprofiles.LiteProfiles;
import me.darwj.liteprofiles.domain.LiteProfile;
import me.darwj.liteprofiles.repository.LiteProfileRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        LiteProfiles.getGameProfileCacheService().update(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

}
