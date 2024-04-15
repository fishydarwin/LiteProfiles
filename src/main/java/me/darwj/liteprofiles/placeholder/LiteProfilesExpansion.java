package me.darwj.liteprofiles.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.darwj.liteprofiles.game.commands.ProfileCommand;
import me.darwj.liteprofiles.repository.LiteProfileRepository;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LiteProfilesExpansion extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getAuthor() {
        return "darwj";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "liteprofiles";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("masteruuid")) {
            UUID uuid = LiteProfileRepository.findOwnerOfUUID(player.getUniqueId());
            if (uuid == null) return player.getUniqueId().toString();
            else return uuid.toString();
        }

        if (params.equalsIgnoreCase("limit")) {
            if (player instanceof Player) {
                return Integer.toString(ProfileCommand.findProfileLimit((Player) player));
            }
            return "";
        }

        if (params.equalsIgnoreCase("count")) {
            UUID uuid = LiteProfileRepository.findOwnerOfUUID(player.getUniqueId());
            if (uuid == null) uuid = player.getUniqueId();
            return Integer.toString(LiteProfileRepository.getProfileCount(uuid));
        }

        return null;
    }
}
