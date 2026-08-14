package eu.bqkitcat.liteprofiles.impl.denizen;

import com.denizenscript.denizen.objects.PlayerTag;
import eu.bqkitcat.liteprofiles.impl.ProfileAndUsernameUpdateService;

import java.util.UUID;

public class DenizenProfileAndUsernameUpdateService implements ProfileAndUsernameUpdateService {

    @Override
    public void update(UUID uuid, String username) {
        PlayerTag.notePlayer(username, uuid);
    }

}
