### What's this & Why
This is the code running on the game servers on https://kloon.io/.
It's a creative gamemode / network built with [Minestom](https://minestom.net/).

The project was set up in the Summer of 2024 and a year later as I'm moving on to [Hytale](https://hytale.com/), I felt like offering the code could have some value to Minestom or block game enthusiasts.
### It doesn't run!
There is a [Main.java](https://github.com/KloonInnovations/GameServer-Public/blob/main/Minestom/src/main/java/io/kloon/gameserver/Main.java), but you'll be missing the dependencies.
Only the GameServer code is open source at this point. It actually contains both the Hub and Creative mode.

Missing packages:
* `io.kloon.infra` from KloonInfra, which contains nats, database, redis, object storage APIs. KloonInfra can be thought of as a code representation of the services available on the network.
  [Here](https://i.imgur.com/FDQnD0l.png)'s how the services look in [Nomad](https://developer.hashicorp.com/nomad), Mongo and Object Storage are using DigitalOcean.
* `io.kloon.bigbackend.client` from BigBackend.
  BigBackend is the silly name for the monolith backend app of the network. It keeps track of who is where, proxies, transferring players between servers, syncing the chat and tablist..
* `io.kloon.discord`, the integration with the network's Discord bot.
* `io.kloon.velocity`, integration with the network's [Velocity](https://papermc.io/software/velocity/) plugin. The client is at the 'network' level while MinecraftIntegration is for Minecraft's [plugin channel](https://minecraft.wiki/w/Java_Edition_protocol/Plugin_channels).

The custom [polar](https://github.com/Minikloon/polar) code used for saving worlds *is* available, although its jar isn't deployed anywhere public.

### So what do I do with this?
There are some alright parts to salvage:
* `io.k.gs.chestmenus` is how I'm used to doing Minecraft chest UIs. There's a bazillion usage examples in this repo.
* `io.k.gs.chestmenus.signui` can take user input from the Minecraft Sign UI. Not the prettiest code but it fits what I'm used to.
* `io.k.gs.minestom.blocks` is not *pretty* but has Minestom-compatible block placement rules for a lot of blocks. It was designed to be taken out into its own library.
* `io.k.gs.minestom.blockchange` lets you place a lot of blocks in one go.
* `io.k.gs.minestom.events` has annotation-based event registration if you're a big Bukkit fan.
* `io.k.gs.modes.creative.jobs` may be hard to take out, but it's like AsyncWorldEdit and throttles jobs per user. Integrated with the undo/redo system by implementing `Change`.
* `io.k.gs.modes.creative.tools.impl` and `io.k.gs.modes.creative.masks.impl` have some content.
* `io.k.gs.modes.creative.storage.blockvolume` has something like [Schematics](https://minecraft.fandom.com/wiki/Schematic_file_format).
* I'm using the vanilla Minestom command system. Lots of usage examples!
* I personally liked the string templates ([MM](https://gist.github.com/Minikloon/e0ec3a6ada42c127a2d4caf7cd8f0686) and MM_WRAP) but they were [removed](https://mail.openjdk.org/pipermail/amber-spec-experts/2024-April/004106.html) past Java 21. i18n wasn't a goal for the server but could've been cute with string templates.

This code is released under MIT license just like Minestom.
