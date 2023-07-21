package me.darwj.liteprofiles.game.commands;

import me.darwj.liteprofiles.domain.LiteProfile;
import me.darwj.liteprofiles.repository.LiteProfileRepository;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileCommand implements CommandExecutor, TabCompleter {

    private static int findProfileLimit(Player player) {
        for (int i = 64; i > 1; i--) {
            if (player.hasPermission("liteprofiles.limit." + i)) {
                return i;
            }
        }
        return 1;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendRichMessage("<red>Only players can use this.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendRichMessage("<red>Invalid use. Please try /" + label + " add/remove/use");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {

            int limit = findProfileLimit(player);
            if (LiteProfileRepository.getProfileCount(player.getUniqueId()) >= limit) {
                player.sendRichMessage("<red>You cannot create a new profile. You can only have "+limit+" profiles.");
                return true;
            }

            UUID ownerUUID = LiteProfileRepository.findOwnerOfUUID(player.getUniqueId());
            UUID randomUUID = UUID.randomUUID();

            assert(ownerUUID) != null;
            LiteProfile currentProfile = LiteProfileRepository.getActiveProfile(ownerUUID);
            LiteProfile newProfile = new LiteProfile(randomUUID, currentProfile.getProfileProperties());
            LiteProfileRepository.registerProfile(ownerUUID, newProfile);

            player.sendRichMessage("<green>Added new profile to " + player.getName() + ".");

        }
        else if (args[0].equalsIgnoreCase("remove")) {

            if (args.length != 2) {
                player.sendRichMessage("<red>Invalid use. Please try /" + label + " remove (uuid)");
                return true;
            }

            UUID ownerUUID = LiteProfileRepository.findOwnerOfUUID(player.getUniqueId());
            UUID slaveUUID;
            try {
                slaveUUID = UUID.fromString(args[1]);
            } catch (IllegalArgumentException ex) {
                player.sendRichMessage("<red>No profile with this UUID exists.");
                return true;
            }

            assert ownerUUID != null;
            if (ownerUUID.equals(slaveUUID)) {
                player.sendRichMessage("<red>You cannot remove the profile associated to your original account.");
                return true;
            }

            if (!LiteProfileRepository.isValidSlaveUUID(ownerUUID, slaveUUID)) {
                player.sendRichMessage("<red>No profile with this UUID exists.");
                return true;
            }

            LiteProfile profile = LiteProfileRepository.getActiveProfile(ownerUUID);
            if (slaveUUID.equals(profile.getUUID())) {
                player.sendRichMessage("<red>You cannot remove your active profile. Change to another profile first.");
                return true;
            }

            LiteProfileRepository.unregisterProfile(ownerUUID, slaveUUID);

            player.sendRichMessage("<green>Removed profile " + args[1] + " from " + player.getName() + ".");

        }
        else if (args[0].equalsIgnoreCase("use")) {

            if (args.length != 2) {
                player.sendRichMessage("<red>Invalid use. Please try /" + label + " use (uuid)");
                return true;
            }

            UUID ownerUUID = LiteProfileRepository.findOwnerOfUUID(player.getUniqueId());
            UUID slaveUUID;
            try {
                slaveUUID = UUID.fromString(args[1]);
            } catch (IllegalArgumentException ex) {
                player.sendRichMessage("<red>No profile with this UUID exists.");
                return true;
            }

            assert ownerUUID != null;
            if (!LiteProfileRepository.isValidSlaveUUID(ownerUUID, slaveUUID)) {
                player.sendRichMessage("<red>No profile with this UUID exists.");
                return true;
            }

            LiteProfile profile = LiteProfileRepository.getActiveProfile(ownerUUID);
            if (slaveUUID.equals(profile.getUUID())) {
                player.sendRichMessage("<red>You are already using this profile.");
                return true;
            }

            LiteProfileRepository.setActiveProfile(ownerUUID, slaveUUID);
            player.kick(MiniMessage.miniMessage().deserialize("Profile changed! Please join the server again."));

        }
        else {
            player.sendRichMessage("<red>Invalid use. Please try /" + label + " add/remove/use");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        if (args.length <= 1) {
            List<String> list = new ArrayList<>();
            list.add("add");
            list.add("remove");
            list.add("use");
            return list;
        }
        else if (args.length == 2) {
            List<String> list = new ArrayList<>();
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("use")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    UUID ownerUUID = LiteProfileRepository.findOwnerOfUUID(player.getUniqueId());
                    assert ownerUUID != null;
                    for (UUID uuid : LiteProfileRepository.getAllSlaveUUIDs(ownerUUID))
                        list.add(uuid.toString());
                }
            }
            return list;
        }
        return new ArrayList<>();
    }
}
