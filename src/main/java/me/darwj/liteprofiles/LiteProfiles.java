package me.darwj.liteprofiles;

import me.darwj.liteprofiles.domain.LiteProfile;
import me.darwj.liteprofiles.game.listeners.LiteProfilesListener;
import me.darwj.liteprofiles.repository.LiteProfileRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class LiteProfiles extends JavaPlugin {

    private static LiteProfiles instance;
    public static LiteProfiles getInstance() { return instance; }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        File dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                if (dataFile.createNewFile()) {
                    getLogger().info("Created new data.yml file.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        LiteProfileRepository.init(dataFile);

        getServer().getPluginManager().registerEvents(new LiteProfilesListener(), this);

    }

    @Override
    public void onDisable() {
        LiteProfileRepository.saveFile();
    }
}
