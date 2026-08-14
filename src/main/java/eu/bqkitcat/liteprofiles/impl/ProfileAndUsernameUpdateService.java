package eu.bqkitcat.liteprofiles.impl;

import java.util.UUID;

public interface ProfileAndUsernameUpdateService {
	void update(UUID uuid, String username);
}
