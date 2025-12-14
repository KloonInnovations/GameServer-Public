package io.kloon.gameserver.modes.creative.tools.impl.waypoint;

import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointColor;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.util.ChangeTracker;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;

import java.util.stream.Stream;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WaypointEntity extends Entity {
    private final WaypointStorage storage;

    private final Entity nameDisplay;

    private final ChangeTracker<String> nameTracker = new ChangeTracker<>();

    private static final int INVISIBLE_AFTER_USE_TICKS = 30;
    private final PlayerTickCooldownMap recentlyUsed = new PlayerTickCooldownMap(INVISIBLE_AFTER_USE_TICKS);

    private WaypointEntity(Instance instance, Pos displayPos, WaypointStorage storage) {
        super(EntityType.ITEM_DISPLAY, storage.getUuid());
        this.storage = storage;

        WaypointColor color = storage.getColor();
        {
            ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
            meta.setItemStack(ItemStack.of(color.getMaterial()));
            meta.setHasNoGravity(true);
            meta.setOnFire(true);
            meta.setGlowColorOverride(color.getColor().asRGB());
            meta.setHasGlowingEffect(true);
        }

        setInstance(instance, displayPos);

        nameDisplay = new Entity(EntityType.TEXT_DISPLAY);
        {
            TextDisplayMeta meta = (TextDisplayMeta) nameDisplay.getEntityMeta();
            meta.setHasNoGravity(true);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
            meta.setText(Component.text(storage.getName()).color(color.getTextColor()));
            meta.setSeeThrough(true);
            meta.setBackgroundColor(0x1B000000);
            meta.setOnFire(true);
            nameDisplay.setInstance(instance, displayPos.add(0, 1.6, 0));
        }

        Stream.of(this, nameDisplay).forEach(entity -> {
            entity.updateViewableRule(p -> {
                CreativePlayer player = (CreativePlayer) p;
                if (recentlyUsed.get(player).isOnCooldown() && entity == this) {
                    return false;
                }

                WaypointsTool.Preferences preferences = player.getCreative().getWaypointTool().getPlayerBound(player);
                if (preferences.isAlwaysShowingWaypoints()) {
                    return true;
                }
                return player.isHoldingTool(CreativeToolType.WAYPOINTS);
            });
        });
    }

    public WaypointStorage getStorage() {
        return storage;
    }

    public void markUsed(CreativePlayer player) {
        recentlyUsed.get(player).cooldown();
        updateViewableRule();
        player.scheduler().scheduleTask(() -> {
            updateViewableRule();
            Sound sound = Sound.sound(SoundEvent.ENTITY_BEE_STING, Sound.Source.PLAYER, 0.3f, 1.6f);
            player.playSound(sound, getPosition());
        }, TaskSchedule.tick(INVISIBLE_AFTER_USE_TICKS + 1), TaskSchedule.stop());
    }

    @Override
    public void update(long time) {
        super.update(time);

        if (GlobalMinestomTicker.every(15, this)) {
            updateViewableRule();
            nameDisplay.updateViewableRule();
        }

        String nameMM = storage.getNameMM();
        nameTracker.acceptIfChanged(nameMM, n -> {
            TextDisplayMeta nameMeta = (TextDisplayMeta) nameDisplay.getEntityMeta();
            nameMeta.setText(MM."\{n}");
        });
    }

    @Override
    public void remove() {
        super.remove();
        nameDisplay.remove();
    }

    @Override
    public boolean preventBlockPlacement() {
        return false;
    }

    public BoundingBox getBoundingBoxForTool() {
        double width = 0.55;
        Point min = position.add(-width, -0.3, -width);
        Point max = position.add(width, 1.9, width);
        return BoundingBox.fromPoints(min, max);
    }

    public static WaypointEntity spawn(Instance instance, WaypointStorage storage) {
        Pos displayPos = storage.getPosition().add(0, 0.5, 0)
                .withYaw(yaw -> yaw + 180)
                .withPitch(0);

        return new WaypointEntity(instance, displayPos, storage);
    }
}
