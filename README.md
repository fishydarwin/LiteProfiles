# LiteProfiles

LiteProfiles is a PaperMC plugin which allows players with one account to use 
multiple UUIDs alongside their original UUID.

This works by dynamically changing your PlayerProfile when logging onto the
server, making it and all plugins believe that your account is different. This
is thanks to the fact that an underlying assumption is that the UUID of some
player will never change.

**Please note that this is probably not the smartest idea, and that you should
almost certainly not be using this to manage multiple accounts.**<br>

**Changing UUIDs at runtime is inherently risky. Expect other plugin devs
not to  offer you support if you are using this system.**

The current moment, this plugin is **very experimental**.<br>
Use this at your own risk - make FULL backups whenever possible.

### Building

Run `mvn clean install` in the source directory.<br>
The result of this can be found in the `target` folder.

### Stability

The plugin seems to be relatively stable, not causing issues with other
important plugins such as banning or permission plugins.

It is very likely that you will receive warnings from plugins such as
**LuckPerms** which see this system as a badly configured BungeeCord network.
I cannot really blame it for thinking that, since this entire setup is pretty
bizarre and unsupported.

### Caveats

There are two main caveats with this plugin:
1. Skins from **minecraft.net** will not update automatically, so you will 
need some sort of skin system to ensure things work as expected.
2. Changing a profile requires that the player **relogs**.