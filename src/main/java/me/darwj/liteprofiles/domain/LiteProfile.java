package me.darwj.liteprofiles.domain;

import com.destroystokyo.paper.profile.ProfileProperty;

import java.util.Set;
import java.util.UUID;

public class LiteProfile {

    private final UUID uuid;
    private final Set<ProfileProperty> profileProperties;

    public LiteProfile(UUID uuid, Set<ProfileProperty> profileProperties) {
        this.uuid = uuid;
        this.profileProperties = profileProperties;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Set<ProfileProperty> getProfileProperties() {
        return profileProperties;
    }

}
