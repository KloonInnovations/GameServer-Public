package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.sign;

import io.kloon.gameserver.chestmenus.ChestMenuPlayer;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.minestom.InventoryExtras;
import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.handlers.signs.StandingSignBlock;
import io.kloon.gameserver.minestom.blocks.handlers.signs.WallSignBlock;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.nbt.NBT;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SignHandler implements BlockHandler {
    public static final Key ID = Key.key("sign");

    public static final Tag<Boolean> IS_WAXED = Tag.Boolean("is_waxed");
    public static final Tag<BinaryTag> FRONT_TEXT = Tag.NBT("front_text");
    public static final Tag<BinaryTag> BACK_TEXT = Tag.NBT("back_text");
    private static final List<Tag<?>> CLIENT_TAGS = Arrays.asList(IS_WAXED, FRONT_TEXT, BACK_TEXT);

    @Override
    public boolean onInteract(@NotNull BlockHandler.Interaction interaction) {
        if (interaction.getHand() != PlayerHand.MAIN) return true;

        Player player = interaction.getPlayer();
        if (! (player instanceof ChestMenuPlayer chestMenuPlayer)) {
            player.sendMessage(MM."<red>Sign editing unsupported!");
            return false;
        }

        Instance instance = interaction.getInstance();
        Point blockPos = interaction.getBlockPosition();
        Block block = interaction.getBlock();

        SignSide side = getClickedSide(player, block, interaction.getBlockFace());

        CompoundBinaryTag nbtBefore = block.nbt();
        SignTile signTile = SignTile.NBT_CODEC.decode(nbtBefore);

        ItemStack inHand = player.getItemInMainHand();
        if (inHand.material() == Material.GLOW_INK_SAC) {
            InventoryExtras.consumeItemInMainHand(player);
            signTile.side(side).withGlowing(true);
            instance.playSound(Sound.sound(SoundEvent.ITEM_GLOW_INK_SAC_USE, Sound.Source.BLOCK, 1f, 1f), blockPos);
            instance.setBlock(blockPos, block.withNbt(NBT.compound(signTile, SignTile.NBT_CODEC)));
            return false;
        }
        if (inHand.material() == Material.INK_SAC) {
            InventoryExtras.consumeItemInMainHand(player);
            signTile.side(side).withGlowing(false);
            instance.playSound(Sound.sound(SoundEvent.ITEM_DYE_USE, Sound.Source.BLOCK, 1f, 1f), blockPos);
            instance.setBlock(blockPos, block.withNbt(NBT.compound(signTile, SignTile.NBT_CODEC)));
            return false;
        }

        Component[] lines = signTile.side(side).lines();

        SignUX.display(chestMenuPlayer, block, lines, input -> {
            Block blockNow = instance.getBlock(blockPos);
            if (blockNow != block) {
                player.sendMessage(MM."<red>The block you were editing changed!");
                return;
            }

            signTile.side(side).withLines(input);
            CompoundBinaryTag nbtAfter = NBT.compound(signTile, SignTile.NBT_CODEC);
            instance.setBlock(blockPos, block.withNbt(nbtAfter));
        });

        return false;
    }

    private SignSide getClickedSide(Player player, Block block, BlockFace clickedFace) {
        if (BlockFamily.Variant.SIGN.contains(block) || BlockFamily.Variant.HANGING_SIGN.contains(block)) {
            int rotation = StandingSignBlock.ROTATION.get(block);
            double signRad = (Math.PI * 2 / 16) * rotation - Math.PI / 2;
            Vec signVec = new Vec(Math.cos(signRad), 0, Math.sin(signRad));
            Vec lookDir = player.getPosition().direction();
            return lookDir.dot(signVec) >= 0.0 ? SignSide.FRONT : SignSide.BACK;
        } else if (BlockFamily.Variant.WALL_SIGN.contains(block)) {
            FacingXZ facing = WallSignBlock.FACING.get(block);
            if (facing.toBlockFace().getOppositeFace() == clickedFace) {
                return SignSide.BACK;
            }
            return SignSide.FRONT;
        } else if (BlockFamily.Variant.WALL_HANGING_SIGN.contains(block)) {
            FacingXZ facing = BlockProp.FACING_XZ.get(block);
            Vec facingNormal = facing.toDirection().vec();
            Vec lookDir = player.getPosition().direction();
            return facingNormal.dot(lookDir) <= 0 ? SignSide.FRONT : SignSide.BACK;
        }

        return SignSide.FRONT;
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return CLIENT_TAGS;
    }

    @Override
    public @NotNull Key getKey() {
        return ID;
    }
}
