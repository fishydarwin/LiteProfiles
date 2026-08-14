package eu.bqkitcat.liteprofiles.game.commands;

import eu.bqkitcat.liteprofiles.LiteProfiles;
import eu.bqkitcat.liteprofiles.repository.LiteProfileRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class LiteProfilesCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String @NonNull [] args) {

        if (args.length == 0) {
            sender.sendRichMessage("<red>Invalid use. Please try /" + label + " version/reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("version")) {
            sender.sendRichMessage("LiteProfiles version " +
                            LiteProfiles.getInstance().getDescription().getVersion() + " by bqkitcat");
            sender.sendRichMessage("https://www.github.com/fishydarwin/LiteProfiles");
        }
        else if (args[0].equalsIgnoreCase("reload")) {
            sender.sendRichMessage("<yellow>Started reload...");

            LiteProfileRepository.loadFile();
            LiteProfiles.getProfileAndUsernameUpdateService().updateAll();

            sender.sendRichMessage("<green>All files reloaded and username service refreshed!");
        }
        else {
            sender.sendRichMessage("<red>Invalid use. Please try /" + label + " version/reload");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String @NonNull [] args) {
        if (args.length <= 1) {
            List<String> list = new ArrayList<>();
            list.add("version");
            list.add("reload");
            return list;
        }
        return new ArrayList<>();
    }
}
