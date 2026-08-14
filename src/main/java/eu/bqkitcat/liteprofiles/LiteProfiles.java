package eu.bqkitcat.liteprofiles;

import eu.bqkitcat.liteprofiles.game.commands.LiteProfilesCommand;
import eu.bqkitcat.liteprofiles.game.commands.ProfileCommand;
import eu.bqkitcat.liteprofiles.game.listeners.LiteProfilesListener;
import eu.bqkitcat.liteprofiles.impl.DefaultProfileAndUsernameUpdateService;
import eu.bqkitcat.liteprofiles.impl.denizen.DenizenProfileAndUsernameUpdateService;
import eu.bqkitcat.liteprofiles.impl.essentials.EssentialsProfileAndUsernameUpdateService;
import eu.bqkitcat.liteprofiles.placeholder.LiteProfilesExpansion;
import eu.bqkitcat.liteprofiles.repository.LiteProfileRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class LiteProfiles extends JavaPlugin {

    private static LiteProfiles instance;
    public static LiteProfiles getInstance() { return instance; }

    private static DefaultProfileAndUsernameUpdateService profileAndUsernameUpdateService;
    public static DefaultProfileAndUsernameUpdateService getProfileAndUsernameUpdateService() {
        return profileAndUsernameUpdateService;
    }

    private boolean hasPlaceholderAPISupport = false;
    public boolean hasPlaceholderAPISupport() {
        return hasPlaceholderAPISupport;
    }

    private boolean hasDenizenSupport = false;
    public boolean hasDenizenSupport() {
        return hasDenizenSupport;
    }

    private boolean hasEssentialsSupport = false;
    public boolean hasEssentialsSupport() {
        return hasEssentialsSupport;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        File dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            if (!getDataFolder().exists()) {
                if (getDataFolder().mkdir()) {
                    try {
                        if (dataFile.createNewFile()) {
                            getLogger().info("Created new data.yml file.");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        LiteProfileRepository.init(dataFile);

        getServer().getPluginManager().registerEvents(new LiteProfilesListener(), this);

        LiteProfilesCommand liteProfilesCommand = new LiteProfilesCommand();
        Objects.requireNonNull(getCommand("liteprofiles")).setExecutor(liteProfilesCommand);
        Objects.requireNonNull(getCommand("liteprofiles")).setTabCompleter(liteProfilesCommand);

        ProfileCommand profileCommand = new ProfileCommand();
        Objects.requireNonNull(getCommand("profile")).setExecutor(profileCommand);
        Objects.requireNonNull(getCommand("profile")).setTabCompleter(profileCommand);

        profileAndUsernameUpdateService = new DefaultProfileAndUsernameUpdateService();
        profileAndUsernameUpdateService.updateAll();

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            hasPlaceholderAPISupport = true;
            new LiteProfilesExpansion().register();
        }

        if (getServer().getPluginManager().isPluginEnabled("Denizen")) {
            hasDenizenSupport = true;
            profileAndUsernameUpdateService.addAdditionalService(new DenizenProfileAndUsernameUpdateService());
        }

        if (getServer().getPluginManager().isPluginEnabled("Essentials")) {
            hasEssentialsSupport = true;
            profileAndUsernameUpdateService.addAdditionalService(new EssentialsProfileAndUsernameUpdateService());
        }

    }

    @Override
    public void onDisable() {
        LiteProfileRepository.saveFile();
    }

}
