package me.darwj.liteprofiles;

import me.darwj.liteprofiles.craftserver.GameProfileCacheService;
import me.darwj.liteprofiles.game.commands.LiteProfilesCommand;
import me.darwj.liteprofiles.game.commands.ProfileCommand;
import me.darwj.liteprofiles.game.listeners.LiteProfilesListener;
import me.darwj.liteprofiles.placeholder.LiteProfilesExpansion;
import me.darwj.liteprofiles.repository.LiteProfileRepository;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class LiteProfiles extends JavaPlugin {

    private static LiteProfiles instance;
    public static LiteProfiles getInstance() { return instance; }

    private static GameProfileCacheService gameProfileCacheService;
    public static GameProfileCacheService getGameProfileCacheService() { return gameProfileCacheService; }

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

        gameProfileCacheService = new GameProfileCacheService((CraftServer) getServer());

        new LiteProfilesExpansion().register();
    }

    @Override
    public void onDisable() {
        LiteProfileRepository.saveFile();
    }

}
