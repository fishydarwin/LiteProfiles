package me.darwj.liteprofiles.repository;

import com.destroystokyo.paper.profile.ProfileProperty;
import me.darwj.liteprofiles.domain.LiteProfile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LiteProfileRepository {

    private static File storageFile;
    private static YamlConfiguration storageConfig;

    public static void init(File storageFile) {
        LiteProfileRepository.storageFile = storageFile;
        loadFile();
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

    public static boolean hasProfile(@NotNull UUID ownerUUID) {
        return storageConfig.isSet(ownerUUID.toString());
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

    public static void unregisterProfile(@NotNull UUID ownerUUID, @NotNull LiteProfile profile) {
        storageConfig.set(ownerUUID + "." + profile.getUUID().toString(), null);
        saveFile();
    }

    public static void setActiveProfile(@NotNull UUID ownerUUID, @NotNull UUID profileUUID) {
        storageConfig.set(ownerUUID + ".active", profileUUID.toString());
        saveFile();
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

}
