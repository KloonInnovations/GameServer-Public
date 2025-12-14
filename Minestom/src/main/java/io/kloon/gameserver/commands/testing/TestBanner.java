package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.color.DyeColor;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.component.DataComponents;
import net.minestom.server.instance.block.banner.BannerPattern;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.BannerPatterns;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TestBanner extends AdminCommand {
    private static final Logger LOG = LoggerFactory.getLogger(TestBanner.class);

    public TestBanner() {
        super("testbanner");
        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                ItemStack banner = ItemStack.builder(Material.BLUE_BANNER)
                        .set(DataComponents.BANNER_PATTERNS, new BannerPatterns(new BannerPatterns.Layer(BannerPattern.BORDER, DyeColor.RED)))
                        .build();

                try {
                    CompoundBinaryTag nbt = banner.toItemNBT();
                    ItemStack itsBack = ItemStack.fromItemNBT(nbt);

                    player.getInventory().addItemStack(itsBack);
                    player.sendMessage("Banner you have!");
                } catch (Throwable t) {
                    LOG.error("Banner command error", t);
                    player.sendMessage(MM."<red>Error with NBT!");
                }
            }
        });
    }
}
