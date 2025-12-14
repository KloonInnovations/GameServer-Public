package io.kloon.gameserver.modes.hub;

import com.google.common.base.Stopwatch;
import io.kloon.bigbackend.client.games.HubsClient;
import io.kloon.bigbackend.games.hub.HubsList;
import io.kloon.gameserver.GsEnv;
import io.kloon.gameserver.KloonGameServer;
import io.kloon.gameserver.chestmenus.autoupdate.NatsSubCache;
import io.kloon.gameserver.commands.player.SettingsCommand;
import io.kloon.gameserver.commands.testing.TimeSetCommand;
import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.nbt.NBT;
import io.kloon.gameserver.minestom.scheduler.Repeat;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.GameServerMode;
import io.kloon.gameserver.modes.ModeType;
import io.kloon.gameserver.modes.hub.commands.HubMenuCommand;
import io.kloon.gameserver.modes.hub.menu.HubMainMenu;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.player.settings.menu.GeneralSettingsMenu;
import io.kloon.gameserver.util.RandUtil;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.serviceframework.subscriptions.PredicatedSubscriber;
import net.hollowcube.polar.minestom.FilePolarChunkLoader;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.*;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biome.Biome;
import net.minestom.server.world.biome.BiomeEffects;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class HubMode extends GameServerMode {
    public static final ItemStack MAIN_MENU_STACK = new ItemBuilder2(Material.NETHER_STAR).name(MM."<title>Main Menu").build();

    public HubMode(KloonNetworkInfra infra) {
        super(infra);
    }

    @Override
    public void onMinestomInitialize() {
        super.onMinestomInitialize();

        InstanceManager instanceMan = MinecraftServer.getInstanceManager();

        //IChunkLoader chunkLoader = loadStaticWorld("hub.polar");
        IChunkLoader chunkLoader = loadStaticWorld("hub_stuffy.polar");
        //IChunkLoader chunkLoader = loadStaticWorld("mia_hub.polar");

        Pos spawnPos = new Pos(46.7, 103, 39.5, 132, 5);
        BoundingBox bounds = BoundingBox.fromPoints(new Vec(-110, 0, 100), new Vec(100, 256, -74));

        KloonInstance instance = new KloonInstance(UUID.randomUUID(), DimensionType.OVERWORLD, chunkLoader);
        instance.setChunkSupplier(LightingChunk::new);
        instanceMan.registerInstance(instance);

        DynamicRegistry<Biome> biomeRegistry = MinecraftServer.getBiomeRegistry();
        Biome stuffyBiome = Biome.builder()
                .effects(BiomeEffects.builder()
                        .skyColor(TextColor.color(255, 255, 255))
                        .fogColor(TextColor.color(255, 242, 242))
                        .biomeParticle(new BiomeEffects.Particle(0.002f, Particle.FALLING_OBSIDIAN_TEAR))
                        .waterColor(new Color(134, 194, 204))
                        .waterFogColor(new Color(134, 194, 204))
                        .build())
                .build();
        RegistryKey<Biome> stuffyBiomeKey = biomeRegistry.register("kloon:hub_stuffy", stuffyBiome);
        int stuffyBiomeId = biomeRegistry.getId(stuffyBiomeKey);
        instance.eventNode().addListener(InstanceChunkLoadEvent.class, event -> {
            Chunk chunk = event.getChunk();
            chunk.getSections().forEach(section -> {
                section.biomePalette().fill(stuffyBiomeId);
            });
            if (chunk instanceof LightingChunk lighting) {
                LightingChunk.relight(instance, Collections.singletonList(lighting));
            }
        });

        instance.setTime(2000);
        instance.setTimeRate(0);

        GlobalEventHandler eventsHandler = MinecraftServer.getGlobalEventHandler();
        eventsHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();

            event.setSpawningInstance(instance);
            //player.setRespawnPoint(new Pos(-158.5, 89.5, 193.5, 270, 0));
            player.setRespawnPoint(spawnPos);
            //player.setRespawnPoint(new Pos(10_000.5, 1, 10_000.5, 270, 0));
        });

        eventsHandler.addListener(PlayerSpawnEvent.class, event -> {
            KloonPlayer player = (KloonPlayer) event.getPlayer();

            player.addEffect(new Potion(PotionEffect.SPEED, (byte) 0, Integer.MAX_VALUE));
            player.sendMessage(MM."<gray>You are on \{instance.getCuteName()}!");

            player.getInventory().setItemStack(8, MAIN_MENU_STACK);

            if (!player.getRanks().isNone()) {
                player.setAllowFlying(true);
            }
        });

        eventsHandler.addListener(ItemDropEvent.class, event -> {
            KloonPlayer player = (KloonPlayer) event.getPlayer();
            if (MAIN_MENU_STACK.equals(event.getItemStack())) {
                List<String> menuLines = Arrays.asList(
                        "Did you drop the menu?",
                        "Lost the menu?",
                        "Accidentally yeeted your menu?",
                        "Awkward, you dropped the menu!",
                        "Why did you do that?");
                player.sendPit(NamedTextColor.LIGHT_PURPLE, "OH?", MM."<gray>\{RandUtil.getRandom(menuLines)} <gray>Try <green>/\{HubMenuCommand.LABEL}<gray>!"
                        .hoverEvent(MM."<yellow>Click to run the command!")
                        .clickEvent(ClickEvent.runCommand("/" + HubMenuCommand.LABEL)));
            }
        });

        HubsClient hubsClient = new HubsClient(infra.nats());
        PredicatedSubscriber<HubsList> hubsListSub = hubsClient.subscribeHubsList(instance.scheduler());
        NatsSubCache<HubsList> hubsCache = new NatsSubCache<>(hubsListSub, hubsClient::getHubsList);

        eventsHandler.addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            if (event.getItemStack().material() == Material.NETHER_STAR) {
                event.setCancelled(true);

                new HubMainMenu(hubsCache).display(player);
            }
        });

        CommandManager commandMan = MinecraftServer.getCommandManager();
        commandMan.register(new SettingsCommand() {
            @Override
            public void openMenu(KloonPlayer player) {
                HubMainMenu mainMenu = new HubMainMenu(hubsCache);
                new GeneralSettingsMenu(mainMenu).display(player);
            }
        });
        commandMan.register(new HubMenuCommand());

        eventsHandler.addListener(PlayerBlockBreakEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        });

        eventsHandler.addListener(PlayerBlockPlaceEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        });

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.scheduleTask(() -> {
            instance.streamPlayers().forEach(player -> {
                if (player.getPosition().y() < 20) {
                    player.teleport(spawnPos);
                    player.sendPit(NamedTextColor.LIGHT_PURPLE, "OOPS!", MM."<gray>You fell! Up you go!");
                    double base = ThreadLocalRandom.current().nextDouble(0.5, 0.8);
                    Repeat.n(player.scheduler(), 5, 2, t -> {
                        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BANJO, Pitch.base(base + t * 0.25));
                    });
                }
            });
        }, TaskSchedule.nextTick(), TaskSchedule.tick(20));

        scheduler.scheduleTask(() -> {
            instance.streamPlayers().forEach(player -> {
                if (!Collisions.contains(bounds, player.getPosition())) {
                    player.teleport(spawnPos);
                    List<Component> messages = Arrays.asList(
                            MM."<gray>Can't go that far! That's crazy dangerous!",
                            MM."<gray>Can't go that far! That's where the fun is!",
                            MM."<gray>Can't go that far! There's nothing that way!",
                            MM."<gray>Can't go that far! It's not allowed!",
                            MM."<gray>Can't go that far! What if you got lost?"
                    );
                    player.sendPit(NamedTextColor.LIGHT_PURPLE, "WOAH THERE!", RandUtil.getRandom(messages));
                    double base = ThreadLocalRandom.current().nextDouble(0.3);
                    Repeat.n(player.scheduler(), 5, 2, t -> {
                        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BANJO, Pitch.base(2.0 - base - t * 0.25));
                    });
                }
            });
        }, TaskSchedule.nextTick(), TaskSchedule.tick(1));
    }

    @Override
    public void onStart(KloonGameServer kgs) {
        MinecraftServer.getConnectionManager().setPlayerProvider(HubPlayer::new);
    }

    private IChunkLoader loadStaticWorld(String filepath) {
        Path staticWorldsPath = Paths.get(GsEnv.STATICWORLDS_PATH.get("/kloon/static_worlds"));
        Path worldPath = staticWorldsPath.resolve(filepath);
        FilePolarChunkLoader chunkLoader = new FilePolarChunkLoader(worldPath);

        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            chunkLoader.loadWorld().get(5, TimeUnit.SECONDS);
            LOG.info("Loaded world from " + worldPath + " in " + stopwatch);
        } catch (Throwable t) {
            LOG.error(STR."Error loading polar world at \{worldPath}", t);
            Runtime.getRuntime().exit(1);
            return null;
        }
        return chunkLoader;
    }

    @Override
    public ModeType getType() {
        return ModeType.HUB;
    }
}
