package eu.bqkitcat.liteprofiles.impl.essentials;

import com.earth2me.essentials.Essentials;
import eu.bqkitcat.liteprofiles.LiteProfiles;
import eu.bqkitcat.liteprofiles.impl.ProfileAndUsernameUpdateService;

import java.util.UUID;

public class EssentialsProfileAndUsernameUpdateService implements ProfileAndUsernameUpdateService {

    private static Essentials instance;

    @Override
    public void update(UUID uuid, String username) {
        if (instance == null) {
            instance = (Essentials) LiteProfiles.getInstance().getServer()
                .getPluginManager().getPlugin("Essentials");
        }
        assert instance != null;
        instance.getUsers().addCachedNpcName(uuid, username);
    }

}
