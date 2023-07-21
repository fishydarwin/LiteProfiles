package me.darwj.liteprofiles.repository;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.darwj.liteprofiles.domain.LiteProfile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LiteProfileRepository {

    private static File storageFile;
    private static YamlConfiguration storageConfig;

    private static BiMap<UUID, UUID> ownerSlaveMap;

    public static void init(File storageFile) {
        LiteProfileRepository.storageFile = storageFile;
        loadFile();
        ownerSlaveMap = HashBiMap.create();
    }

    public static void loadFile() {
        storageConfig = YamlConfiguration.loadConfiguration(storageFile);
    }

    public static void saveFile() {
        try {
            storageConfig.save(storageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable UUID findOwnerOfUUID(UUID slaveUUID) {
        return ownerSlaveMap.inverse().get(slaveUUID);
    }

    public static boolean hasProfile(@NotNull UUID ownerUUID) {
        return storageConfig.isSet(ownerUUID.toString());
    }

    public static boolean isValidSlaveUUID(@NotNull UUID ownerUUID, @NotNull UUID slaveUUID) {
        return storageConfig.isSet(ownerUUID + "." + slaveUUID);
    }

    public static void registerProfile(@NotNull UUID ownerUUID, @NotNull LiteProfile profile) {
        for (ProfileProperty property : profile.getProfileProperties()) {
            storageConfig.set(ownerUUID + "." + profile.getUUID().toString() + "." + property.getName() + ".value",
                    property.getValue());
            storageConfig.set(ownerUUID + "." + profile.getUUID().toString() + "." + property.getName() + ".signature",
                    property.getSignature());
        }
        saveFile();
    }

    public static void unregisterProfile(@NotNull UUID ownerUUID, @NotNull UUID slaveUUID) {
        storageConfig.set(ownerUUID + "." + slaveUUID, null);
        saveFile();
    }

    public static void setActiveProfile(@NotNull UUID ownerUUID, @NotNull UUID profileUUID) {
        storageConfig.set(ownerUUID + ".active", profileUUID.toString());
        saveFile();
        ownerSlaveMap.put(ownerUUID, profileUUID);
    }

    public static @NotNull LiteProfile getActiveProfile(@NotNull UUID ownerUUID) {
        UUID activeUUID =
                UUID.fromString(storageConfig.getString(ownerUUID + ".active", ownerUUID.toString()));

        Set<ProfileProperty> profileProperties = new HashSet<>();
        for (String key : storageConfig.getConfigurationSection(ownerUUID + "." + activeUUID).getKeys(false)) {
            String value =
                    storageConfig.getString(ownerUUID + "." + activeUUID + "." + key + ".value");
            String signature =
                    storageConfig.getString(ownerUUID + "." + activeUUID + "." + key  + ".signature", null);
            assert value != null;
            profileProperties.add(new ProfileProperty(key, value, signature));
        }
        return new LiteProfile(activeUUID, profileProperties);
    }

    public static void updateProfileProperty(@NotNull UUID ownerUUID,
                                             @NotNull UUID slaveUUID,
                                             @NotNull String name,
                                             @NotNull String newValue,
                                             @Nullable String newSignature) {
        UUID activeUUID =
                UUID.fromString(storageConfig.getString(ownerUUID + ".active", ownerUUID.toString()));

        if (activeUUID.equals(slaveUUID)) {
            Player player = Bukkit.getPlayer(ownerUUID);
            if (player != null) {
                LiteProfile profile = getActiveProfile(ownerUUID);
                profile.updateProfileProperty(name, newValue, newSignature);
                player.getPlayerProfile().setProperties(profile.getProfileProperties());
            }
        }

        storageConfig.set(ownerUUID + "." + slaveUUID + "." + name + ".value", newValue);
        storageConfig.set(ownerUUID + "." + slaveUUID + "." + name + ".signature", newSignature);
        saveFile();
    }

    public static int getProfileCount(@NotNull UUID ownerUUID) {
        ConfigurationSection section = storageConfig.getConfigurationSection(ownerUUID.toString());
        if (section != null)
            return section.getKeys(false).size();
        return 0;
    }

    public static @NotNull List<UUID> getAllSlaveUUIDs(@NotNull UUID ownerUUID) {
        List<UUID> result = new ArrayList<>();
        ConfigurationSection section = storageConfig.getConfigurationSection(ownerUUID.toString());
        if (section != null)
            for (String uuidString : section.getKeys(false))
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    result.add(uuid);
                } catch (IllegalArgumentException ignored) {} // ok
        return result;
    }

}
