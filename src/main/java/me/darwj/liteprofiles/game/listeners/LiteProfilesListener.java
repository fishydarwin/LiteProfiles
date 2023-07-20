package me.darwj.liteprofiles.game.listeners;

import com.destroystokyo.paper.event.profile.LookupProfileEvent;
import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import me.darwj.liteprofiles.LiteProfiles;
import me.darwj.liteprofiles.domain.LiteProfile;
import me.darwj.liteprofiles.repository.LiteProfileRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LiteProfilesListener implements Listener {

    @EventHandler
    public void onPreLookupProfileEvent(@NotNull PreLookupProfileEvent event) {
        LiteProfiles.getInstance().getLogger().info("PreLookup");
        PlayerProfile checkProfile = Bukkit.createProfile(event.getName());
        checkProfile.complete();
        UUID ownerUUID = checkProfile.getId();
        if (ownerUUID == null) {
            return;
        }
        LiteProfiles.getInstance().getLogger().info("Not Null");
        if (LiteProfileRepository.hasProfile(ownerUUID)) {
            LiteProfiles.getInstance().getLogger().info("Load");
            LiteProfile profile = LiteProfileRepository.getActiveProfile(ownerUUID);
            event.setUUID(profile.getUUID());
            event.setProfileProperties(profile.getProfileProperties());
        }
    }

    @EventHandler
    public void onLookupProfileEvent(@NotNull LookupProfileEvent event) {
        LiteProfiles.getInstance().getLogger().info("Lookup");
        UUID ownerUUID = event.getPlayerProfile().getId();
        assert ownerUUID != null;
        LiteProfiles.getInstance().getLogger().info(ownerUUID.toString());
        if (!LiteProfileRepository.hasProfile(ownerUUID)) {
            LiteProfile profile = new LiteProfile(ownerUUID, event.getPlayerProfile().getProperties());
            LiteProfileRepository.registerProfile(ownerUUID, profile);
            LiteProfileRepository.setActiveProfile(ownerUUID, ownerUUID);
        }
    }

}
