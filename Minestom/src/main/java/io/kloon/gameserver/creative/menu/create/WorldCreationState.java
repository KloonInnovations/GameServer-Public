package io.kloon.gameserver.creative.menu.create;

import io.kloon.gameserver.creative.CreativeWorldsMenuProxy;
import io.kloon.gameserver.creative.menu.create.dimensions.ChooseDimensionMenu;
import io.kloon.gameserver.creative.menu.create.dimensions.DimensionChoice;
import io.kloon.gameserver.creative.storage.defs.WorldCopyInfo;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.creative.storage.owner.player.PlayerWorldOwner;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.RandUtil;
import io.kloon.infra.facts.KloonDataCenter;
import net.minestom.server.item.Material;
import net.minestom.server.world.DimensionType;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldCreationState {
    private String name;
    private KloonDataCenter datacenter = null;
    private DimensionChoice dimension = ChooseDimensionMenu.OVERWORLD;
    private CopyingWorld copyingWorld = null;

    public static final int MAX_COPY_CHAIN_RECORDS = 10;

    public WorldCreationState() {

    }

    public WorldCreationState(CopyingWorld copying) {
        this.name = copying.save().cuteName();
        this.copyingWorld = copying;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KloonDataCenter getDatacenter(KloonPlayer player) {
        KloonDataCenter prefDatacenter = player.getAccount().getPreferredDatacenter();
        return getDatacenter(prefDatacenter);
    }

    public KloonDataCenter getDatacenter(KloonDataCenter def) {
        return datacenter == null ? def : datacenter;
    }

    public void setDatacenter(KloonDataCenter datacenter) {
        this.datacenter = datacenter;
    }

    public DimensionChoice getDimension() {
        return dimension;
    }

    public void setDimension(DimensionChoice dimension) {
        this.dimension = dimension;
    }

    public CopyingWorld getCopyingWorld() {
        return copyingWorld;
    }

    public WorldDef createWorldDef(KloonPlayer player) {
        KloonDataCenter datacenter = getDatacenter(player);

        WorldOwner owner = new PlayerWorldOwner(player);
        Material icon = copyingWorld == null
                ? RandUtil.getRandom(CreativeWorldsMenuProxy.GLAZED)
                : Material.NETHERITE_SCRAP;

        WorldDef worldDef = new WorldDef(new ObjectId(), name, icon, owner, datacenter);

        if (copyingWorld != null) {
            WorldDef worldToCopy = copyingWorld.worldDef();
            WorldSave saveToCopy = copyingWorld.save();
            List<WorldCopyInfo> copyChain = new ArrayList<>(worldToCopy.copyChain());
            WorldCopyInfo copyInfo = new WorldCopyInfo(
                    System.currentTimeMillis(),
                    worldToCopy._id(),
                    worldToCopy.name(),
                    worldToCopy.ownership(),
                    saveToCopy._id());
            copyChain.addFirst(copyInfo);
            if (copyChain.size() > MAX_COPY_CHAIN_RECORDS) {
                copyChain = copyChain.subList(0, MAX_COPY_CHAIN_RECORDS);
            }
            worldDef.withCopyChain(copyChain);
        }

        return worldDef;
    }
}
