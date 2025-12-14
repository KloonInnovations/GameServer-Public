package io.kloon.gameserver.modes.creative.tools.impl.teleport;

import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.TickingTool;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.commands.TeleportCommand;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.commands.TeleportItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.menu.TeleportToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;

import java.util.List;
import java.util.stream.Stream;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportTool.*;

public class TeleportTool extends CreativeTool<Settings, Preferences> implements TickingTool {
    public static final double DEFAULT_RANGE = 8;
    public static final double LEFT_CLICK_MIN_RANGE = 1;
    public static final double LEFT_CLICK_MAX_RANGE = 50;

    public static final int RIGHT_CLICK_MAX_RANGE = 50;

    public TeleportTool() {
        super(CreativeToolType.TELEPORTER, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected boolean canUseWithoutEditPerm(CreativePlayer player, ToolClick click) {
        return true;
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        if (player.isSneaking()) {
            if (click.side() == ToolClickSide.LEFT) {
                handleSneakLeftClick(player);
            } else {
                handleSneakRightClick(player);
            }
        } else {
            if (click.side() == ToolClickSide.LEFT) {
                handleLeftClick(player, click.getItem());
            } else {
                handleRightClick(player);
            }
        }
    }

    private void handleLeftClick(CreativePlayer player, ItemStack item) {
        Pos posBefore = player.getPosition();
        boolean flyingBefore = player.isFlying();

        Settings settings = getItemBound(item);
        double distance = settings.leftClickRange;
        Pos teleportPos = computeLeftClickPosition(player, item);
        player.teleport(teleportPos);

        Component text = (int) distance == 1
                ? MM."<gray>Teleported a block forward to \{PointFmt.fmt10k(posBefore)}!"
                : MM."<gray>Teleported forward \{NumberFmt.NO_DECIMAL.format(distance)} blocks to \{PointFmt.fmt10k(posBefore)}!";
        SentMessage msg = player.msg().send(MsgCat.TOOL, NamedTextColor.LIGHT_PURPLE, "ZIIIP!", text,
                SoundEvent.ENTITY_ENDERMAN_TELEPORT, Pitch.base(1.1).addRand(0.2));

        player.addToHistory(toolType, "<light_purple>Ziiip!",
                msg, new TeleportChange(posBefore, flyingBefore, player));
    }

    public Pos computeLeftClickPosition(CreativePlayer player, ItemStack item) {
        Pos position = player.getPosition();
        Settings settings = getItemBound(item);
        double distance = settings.leftClickRange;
        return position.add(position.direction().mul(distance));
    }

    private void handleRightClick(CreativePlayer player) {
        Pos posBefore = player.getPosition();
        boolean flyingBefore = player.isFlying();

        RightClickPos rightClickPos = computeRightClickPosition(player);
        if (rightClickPos.shouldFly) {
            player.setFlying(true);
        }
        player.teleport(rightClickPos.pos);

        double distance = rightClickPos.pos.distance(posBefore);

        Component text;
        if (distance + 2 >= RIGHT_CLICK_MAX_RANGE) {
            text = MM."<gray>Warped the max of \{RIGHT_CLICK_MAX_RANGE} blocks to \{PointFmt.fmt10k(posBefore)}!";
        } else {
            text = MM."<gray>Warped forward \{NumberFmt.ONE_DECIMAL.format(distance)} blocks to \{PointFmt.fmt10k(posBefore)}!";
        }

        SentMessage msg = player.msg().send(MsgCat.TOOL,
                NamedTextColor.DARK_PURPLE, "ZOOP!", text,
                SoundEvent.ENTITY_ENDERMAN_TELEPORT, Pitch.base(1.4).addRand(0.2));

        player.addToHistory(toolType, "<dark_purple>Zoop",
                msg, new TeleportChange(posBefore, flyingBefore, player));
    }

    public Point getTargetBlockNotNull(CreativePlayer player) {
        Point targetBlock = player.getTargetBlockPosition(RIGHT_CLICK_MAX_RANGE);
        if (targetBlock == null) {
            Pos eyePos = player.getEyePosition();
            targetBlock = new BlockVec(eyePos.add(eyePos.direction().mul(RIGHT_CLICK_MAX_RANGE)));
        }
        return targetBlock;
    }

    public RightClickPos computeRightClickPosition(CreativePlayer player) {
        CreativeInstance instance = player.getInstance();
        Pos playerPos = player.getPosition();

        Point targetBlock = getTargetBlockNotNull(player);

        Point blockCenter = targetBlock.add(0.5, 0.5, 0.5);
        Vec teleportOffset = Vec.ZERO;

        boolean shouldFly = false;
        if (!instance.getBlock(targetBlock).isAir()) {
            BoundingBox boundingBox = BoundingBoxUtils.fromBlock(blockCenter);
            Vec faceNormal = Collisions.raycastBoxGetFaceNormal(player.getEyeRay(), boundingBox);
            CardinalDirection normalDir = CardinalDirection.fromVec(faceNormal);
            teleportOffset = switch (normalDir) {
                case UP -> new Vec(0, 0.5, 0);
                case DOWN -> new Vec(0, -3, 0);
                default -> normalDir.vec();
            };
            if (normalDir != CardinalDirection.UP) {
                shouldFly = true;
            }
            if (normalDir.horizontal()) {
                Pos teleportPos = playerPos.withCoord(blockCenter.add(teleportOffset));
                Vec dontBumpHead = offsetDontBumpHead(player, teleportPos);
                teleportOffset = teleportOffset.add(dontBumpHead);
            }
        }

        Pos teleportPos = playerPos.withCoord(blockCenter.add(teleportOffset));
        return new RightClickPos(teleportPos, shouldFly);
    }

    public record RightClickPos(Pos pos, boolean shouldFly) {}

    @Override
    public void tickHolding(CreativePlayer player, ItemStack item) {
        player.attachIfAbsent("teleport_preview_left", () -> {
            TeleportPreview leftPreview = new TeleportPreview(player, this);
            leftPreview.setInstance(player.getInstance(), leftPreview.computePosition().pos());
            return leftPreview;
        });
    }

    private Vec offsetDontBumpHead(Player player, Pos pos) {
        Instance instance = player.getInstance();
        return Stream.of(0, -1, -2)
                .map(Vec.ZERO::withY)
                .filter(offset -> {
                    Pos visionPos = pos.add(offset).add(0, player.getEyeHeight(), 0);
                    Pos bodyPos = visionPos.withY(y -> y - 1);
                    boolean bumpingHead = instance.getBlock(visionPos).isSolid()
                            || instance.getBlock(bodyPos).isSolid();
                    return !bumpingHead;
                })
                .findFirst().orElse(Vec.ZERO);
    }

    private void handleSneakLeftClick(CreativePlayer player) {
        Pos posBefore = player.getPosition();
        boolean flyingBefore = player.isFlying();

        BlockVec floorOfBasement = PointTopUtils.findFloorOfBasement(player.getInstance(), posBefore);
        if (floorOfBasement == null) {
            player.playSound(SoundEvent.ENTITY_ENDERMAN_HURT, Pitch.base(0.8).addRand(0.2));
            player.sendPit(NamedTextColor.RED, "NO BASEMENT!", MM."<gray>Couldn't find space below the floor below you!");
            player.sendPit(NamedTextColor.YELLOW, "TIP!", MM."<gray>To teleport to the floor, just right-click it!");
            return;
        }

        Pos teleportPos = posBefore.withY(floorOfBasement.y() + 1.0);
        player.teleport(teleportPos);

        double distance = teleportPos.distance(posBefore);

        Component text = MM."<gray>Teleported down \{NumberFmt.ONE_DECIMAL.format(distance)} blocks to y = \{NumberFmt.NO_DECIMAL.format(teleportPos.y())}!";
        SentMessage msg = player.msg().send(MsgCat.TOOL,
                NamedTextColor.DARK_PURPLE, "DOWN!", text,
                SoundEvent.ENTITY_ENDERMAN_TELEPORT, Pitch.base(0.5).addRand(0.1));

        player.addToHistory(toolType, "<dark_purple>Down!",
                msg, new TeleportChange(posBefore, flyingBefore, player));
    }

    private void handleSneakRightClick(CreativePlayer player) {
        Pos posBefore = player.getPosition();
        boolean flyingBefore = player.isFlying();

        BlockVec floorOfRoof = PointTopUtils.findFloorOfRoof(player.getInstance(), posBefore);
        if (floorOfRoof == null) {
            player.playSound(SoundEvent.ENTITY_ENDERMAN_HURT, Pitch.base(0.8).addRand(0.2));
            player.sendPit(NamedTextColor.RED, "NO ROOF!", MM."<gray>Couldn't find an empty space on the roof above you!");
            return;
        }

        Pos teleportPos = posBefore.withY(floorOfRoof.y() + 1.0);
        player.teleport(teleportPos);

        double distance = teleportPos.distance(posBefore);

        Component text = MM."<gray>Teleported up \{NumberFmt.ONE_DECIMAL.format(distance)} blocks to y = \{NumberFmt.NO_DECIMAL.format(teleportPos.y())}!";
        SentMessage msg = player.msg().send(MsgCat.TOOL,
                NamedTextColor.LIGHT_PURPLE, "UP!", text,
                SoundEvent.ENTITY_ENDERMAN_TELEPORT, Pitch.base(1.05).addRand(0.2));

        player.addToHistory(toolType, "<light_purple>Up!",
                msg, new TeleportChange(posBefore, flyingBefore, player));
    }

    @Override
    public List<Command> createCommands() {
        return List.of(
                new TeleportCommand(this),
                new TeleportItemCommand(this)
        );
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new TeleportToolMenu(this, itemRef).display(player);
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>TELEPORT");
        lore.addAll(MM_WRAP."<gray>Forward <green>\{NumberFmt.ONE_DECIMAL.format(settings.leftClickRange)} <gray>blocks.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>WARP");

        String previewColor = TextColor.color(TeleportPreview.COLOR).asHexString();
        lore.addAll(MM_WRAP."<gray>To <\{previewColor}>(previewed) <gray>target.");

        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.SNEAK_LCLICK_GREEN} <#FF266E><b>UNDER");
        lore.addAll(MM_WRAP."<gray>Teleports to the floor of the first empty space under you.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.SNEAK_RCLICK_GREEN} <#FF266E><b>TOP");
        lore.addAll(MM_WRAP."<gray>Teleports to the floor over the ceiling above you.");
    }

    public static class Settings {
        public double leftClickRange = DEFAULT_RANGE;
    }

    public record Preferences() {}
}
