package io.kloon.gameserver.creative.menu.manage.oldsaves.bruh;

import com.spotify.futures.CompletableFutures;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncPlayerButton;
import io.kloon.gameserver.chestmenus.states.ButtonState;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.menu.commands.CreateWorldCommand;
import io.kloon.gameserver.creative.menu.create.CopyingWorld;
import io.kloon.gameserver.creative.menu.create.CreateWorldMenu;
import io.kloon.gameserver.creative.menu.create.WorldCreationState;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.defs.WorldDefRepo;
import io.kloon.gameserver.creative.storage.owner.player.PlayerWorldOwner;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.creative.storage.saves.WorldSaveRepo;
import io.kloon.gameserver.creative.storage.saves.WorldSaveWithData;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import io.kloon.infra.facts.KloonDataCenter;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CopySaveToWorldButton extends AsyncPlayerButton<CopySaveToWorldButton.Data> {
    private final WorldDef worldToCopy;
    private final WorldSave save;

    private KloonDataCenter copyDataCenter;

    private static final PlayerTickCooldownMap clickCd = new PlayerTickCooldownMap(50);

    public CopySaveToWorldButton(ChestMenu parent, int slot, WorldDef worldToCopy, WorldSave save) {
        super(parent, slot);
        this.worldToCopy = worldToCopy;
        this.copyDataCenter = worldToCopy.datacenter();
        this.save = save;
    }

    @Override
    public CompletableFuture<Data> fetchData(Player player) {
        if (!(player instanceof KloonPlayer kp)) {
            return CompletableFutures.exceptionallyCompletedFuture(new RuntimeException("Wrong player type"));
        }

        WorldDefRepo defRepo = Kgs.getCreativeRepos().defs();
        WorldSaveRepo savesRepo = new WorldSaveRepo(Kgs.getInfra(), copyDataCenter.getCreativeWorldsBucket());

        PlayerWorldOwner owner = new PlayerWorldOwner(kp);
        CompletableFuture<WorldSaveWithData> getSaveData = savesRepo.getSaveData(save);
        CompletableFuture<Long> countWorlds = defRepo.countLiveWorldsByOwner(owner);

        return CompletableFuture.allOf(getSaveData, countWorlds)
                .thenApply(_ -> new Data(kp, getSaveData.join(), countWorlds.join()));
    }

    @Override
    public void handleClickWithData(Player p, ButtonClick click, Data data) {
        KloonPlayer player = (KloonPlayer) p;
        if (!(clickCd.get(player).cooldownIfPossible())) {
            player.sendMessage(MM."<red>This functionality is on cooldown!");
            return;
        }

        ButtonState state = getState(data);
        if (state != CAN_CREATE) {
            state.sendChatMessage(player);
            return;
        }

        CopyingWorld copying = new CopyingWorld(worldToCopy, data.saveWithData);
        WorldCreationState creationState = new WorldCreationState(copying);

        if (click.isRightClick()) {
            new CreateWorldMenu(parent, creationState).display(player);
            return;
        }

        player.closeInventory();

        WorldDef worldDef = creationState.createWorldDef(player);
        CreateWorldCommand.createWorldWithCopy(player, worldDef, data.saveWithData);
    }

    @Override
    public ItemStack renderWithData(Player player, Data data) {
        Component name = MM."<title>Create World from Save";

        List<Component> lore = MM_WRAP."<gray>Copies this save and creates a new creative world with it.";
        lore.add(Component.empty());

        ButtonState state = getState(data);
        if (state == CAN_CREATE) {
            lore.add(MM."<rcta>Click for advanced copy!");
        }
        lore.add(state.getCallToAction(player));

        return MenuStack.of(getLoadingIcon()).name(name).lore(lore).build();
    }

    @Override
    protected Material getLoadingIcon() {
        return Material.NETHERITE_SCRAP;
    }

    private ButtonState getState(Data data) {
        if (data.worldsByOwner + 1 > CreateWorldCommand.getWorldsLimit(data.player)) {
            return NO_WORLD_SLOTS;
        }
        return CAN_CREATE;
    }

    private static final ButtonState CAN_CREATE = new ButtonState("<lcta>Click to create world copy!");
    private static final ButtonState NO_WORLD_SLOTS = new ButtonState("<!cta>No available world slot!");

    public record Data(KloonPlayer player, WorldSaveWithData saveWithData, long worldsByOwner) {}
}
