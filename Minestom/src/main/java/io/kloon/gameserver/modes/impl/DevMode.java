package io.kloon.gameserver.modes.impl;

import io.kloon.gameserver.KloonGameServer;
import io.kloon.gameserver.commands.testing.SaveWorldCommand;
import io.kloon.gameserver.commands.testing.ConvertPolar;
import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.modes.GameServerMode;
import io.kloon.gameserver.modes.ModeType;
import io.kloon.infra.KloonNetworkInfra;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.world.DimensionType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DevMode extends GameServerMode {
    private InstanceContainer instance;

    public DevMode(KloonNetworkInfra infra) {
        super(infra);
    }

    @Override
    public void onMinestomInitialize() {
        super.onMinestomInitialize();

        InstanceManager instanceMan = MinecraftServer.getInstanceManager();

        //Path worldPath = Paths.get("hub_world.polar");
        //InstanceContainer instance = instanceMan.createInstanceContainer(new FilePolarChunkLoader(worldPath));

        Path worldPath = Paths.get("Kloon");
        AnvilLoader chunkLoader = new AnvilLoader(worldPath);
        this.instance = new KloonInstance(UUID.randomUUID(), DimensionType.OVERWORLD, chunkLoader);
        instance.setChunkSupplier(LightingChunk::new);
        instanceMan.registerInstance(instance);

        //instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        CommandManager commandMan = MinecraftServer.getCommandManager();
        commandMan.register(new SaveWorldCommand());
        commandMan.register(new ConvertPolar());

        GlobalEventHandler eventsHandler = MinecraftServer.getGlobalEventHandler();
        eventsHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();

            player.setRespawnPoint(new Pos(46.7, 103, 39.5, 132, 5));
            player.setGameMode(GameMode.CREATIVE);
        });

        ItemStack stick = ItemStack.of(Material.STICK);

        eventsHandler.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            //player.addEffect(new Potion(PotionEffect.NIGHT_VISION, (byte) 0, Integer.MAX_VALUE));
            player.setItemInMainHand(stick);

            player.addEffect(new Potion(PotionEffect.SPEED, (byte) 0, Integer.MAX_VALUE));

            player.sendMessage(MM."<gray>You joined the network on \{infra.serverName()}");
        });

        eventsHandler.addListener(PlayerBlockBreakEvent.class, event -> {
            Player player = event.getPlayer();
            LOG.info(STR."\{player.getUsername()} broke a block!");
            player.getInstance().sendMessage(MM."<rainbow>\{player.getUsername()} broke a block!</rainbow>");
        });

        eventsHandler.addListener(ItemDropEvent.class, event -> {
            final Player player = event.getPlayer();
            ItemStack droppedItem = event.getItemStack();

            Pos playerPos = player.getPosition();
            ItemEntity itemEntity = new ItemEntity(droppedItem);
            itemEntity.setPickupDelay(Duration.of(500, TimeUnit.MILLISECOND));
            itemEntity.setInstance(player.getInstance(), playerPos.withY(y -> y + 1.5));
            Vec velocity = playerPos.direction().mul(6);
            itemEntity.setVelocity(velocity);
        });

        eventsHandler.addListener(PickupItemEvent.class, event -> {
            final Entity entity = event.getLivingEntity();
            if (entity instanceof Player) {
                // Cancel event if player does not have enough inventory space
                final ItemStack itemStack = event.getItemEntity().getItemStack();
                event.setCancelled(!((Player) entity).getInventory().addItemStack(itemStack));
            }
        });

        instance.scheduler().submitTask(() -> {
            Set<Player> players = instance.getPlayers();
            if (players.isEmpty()) {
                LOG.info("There are no players online!");
            } else {
                LOG.info("There are " + players.size() + " player(s) online!");
            }
            return TaskSchedule.tick(40);
        });
    }

    public InstanceContainer getInstance() {
        return instance;
    }

    @Override
    public void onStart(KloonGameServer kgs) {

    }

    @Override
    public ModeType getType() {
        return ModeType.DEV;
    }
}
