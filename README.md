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
not to offer you support if you are using this system.**

The current moment, this plugin is **very experimental**.<br>
Use this at your own risk - make FULL backups whenever possible.

### Building

Run `gradle build` in the source directory.<br>
The result of this can be found as `build/libs/LiteProfiles_x.x.x.jar`.

### Stability

The plugin seems to be relatively stable, not causing issues with other
important plugins such as banning or permission plugins.

It is very likely that you will receive warnings from plugins such as
**LuckPerms** which see this system as a badly configured BungeeCord network.
I cannot really blame it for thinking that, since this entire setup is pretty
bizarre and unsupported.

NMS is used extremely lightly. So, unless Minecraft makes a huge change to 
its systems, this plugin will continue working on PaperMC.

### Offline Player Support

The plugin attempts to correct `Server.getOfflinePlayer(String name)` 
behaviour by changing the offline cache when a player logs off. This 
ensures that, in general, attempting to find a player that is offline 
by name will always return the master UUID (which is a player's original, 
"normal" UUID).

The behaviour described above, where an offline player will have a name
such that it always points to their official UUID is what plugins assume
to be normal behaviour, hence why this is maintained.

This may not be 100% stable, but it seems to correctly identify most cases.<br>
Thank you @martijn-heil for figuring out a solution to this.

### Caveats

There are two main caveats with this plugin:
1. Skins from **minecraft.net** may not update gracefully every time.
Sometimes, the player may have to relog to see the correct view.
2. Changing a profile requires that the player **relogs**.

### Rate limiting

The plugin should not have rate limiting issues. This is because the plugin
uses a copy of the main profile at all times. That is to say, no extra call
to the Mojang API is made at any point.