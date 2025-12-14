package io.kloon.gameserver.modes.creative;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.minestom.events.AnnotationEventsRegisterer;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.RaycastUtils;
import io.kloon.gameserver.modes.creative.commands.NightVisionCommand;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeRecord;
import io.kloon.gameserver.modes.creative.history.History;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskWorkCache;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.selection.CuboidSelection;
import io.kloon.gameserver.modes.creative.selection.NoCuboidSelection;
import io.kloon.gameserver.modes.creative.storage.datainworld.PlayerWorldStorage;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestStorage;
import io.kloon.gameserver.modes.creative.storage.playerdata.CreativePlayerStorage;
import io.kloon.gameserver.modes.creative.storage.playerdata.InventoryStorage;
import io.kloon.gameserver.modes.creative.storage.playerdata.ToolsStorage;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.PlayerClipboard;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.hand.PlayerChangesByHand;
import io.kloon.gameserver.modes.creative.tools.impl.MainMenuTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection.NoPasteSelection;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection.PasteSelection;
import io.kloon.gameserver.modes.creative.tools.impl.selection.regular.SelectionTool;
import io.kloon.gameserver.modes.creative.tools.snipe.PlayerSnipeHandler;
import io.kloon.gameserver.modes.creative.ux.CreativeHeaderFooter;
import io.kloon.gameserver.modes.creative.ux.CreativeTablist;
import io.kloon.gameserver.modes.creative.ux.messaging.CreativeBroadcaster;
import io.kloon.gameserver.modes.creative.ux.messaging.CreativeBroadcasterWithInitiator;
import io.kloon.gameserver.modes.creative.ux.messaging.CreativeMessager;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.modes.creative.ux.sidebar.CreativeSidebar;
import io.kloon.gameserver.modes.creative.vanilla.BlockPickingListener;
import io.kloon.gameserver.modes.creative.vanilla.WaterBucketListener;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.tablist.KloonTablist;
import io.kloon.gameserver.util.CachedForTick;
import io.kloon.gameserver.util.physics.Ray;
import io.kloon.gameserver.ux.headerfooter.KloonHeaderFooter;
import io.kloon.gameserver.ux.sidebar.KloonSidebar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativePlayer extends KloonPlayer {
    private static final Logger LOG = LoggerFactory.getLogger(CreativePlayer.class);

    private CreativePlayerStorage storage;
    private EnderChestStorage enderChest;
    private boolean spawnedFully;

    private final Map<String, Entity> attachedEntities = new HashMap<>();

    private final PlayerChangesByHand changesByHand;
    private final CreativeMessager messager;

    private History history = new History(new ArrayList<>(), new ArrayList<>());
    private CuboidSelection selection = new NoCuboidSelection(this);
    private PasteSelection pasteSelection = new NoPasteSelection(this);
    private final PlayerSnipeHandler snipeHandler = new PlayerSnipeHandler(this);
    private int joinTick = GlobalMinestomTicker.getTick();

    private final CachedForTick<Boolean> canEditWorldCached = new CachedForTick<>(this::computeCanEditWorld);

    public CreativePlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        this.messager = new CreativeMessager(this);

        eventNode().addListener(RemoveEntityFromInstanceEvent.class, event -> onDespawn((CreativeInstance) event.getInstance()));

        AnnotationEventsRegisterer registerer = new AnnotationEventsRegisterer(eventNode());
        registerer.register(changesByHand = new PlayerChangesByHand(this));
        registerer.register(new WaterBucketListener());
        registerer.register(new BlockPickingListener());
    }

    @Override
    protected KloonSidebar createSidebar() {
        return new CreativeSidebar(this);
    }

    @Override
    protected KloonHeaderFooter createHeaderFooter() {
        return new CreativeHeaderFooter(this);
    }

    @Override
    protected KloonTablist createTabList() {
        return new CreativeTablist(this);
    }

    public CreativeMainMenu createMainMenu() {
        return new CreativeMainMenu(this);
    }

    @Override
    public @UnknownNullability CreativeInstance getInstance() {
        return (CreativeInstance) super.getInstance();
    }

    public CreativePlayerStorage getCreativeStorage() {
        return storage;
    }

    public ToolsStorage getToolsStorage() {
        return storage.getTools();
    }

    public PlayerClipboard getClipboard() {
        return storage.getClipboard();
    }

    public double getPastingRange() {
        return storage.getPastingRange();
    }

    public EnderChestStorage getEnderChest() {
        return enderChest;
    }

    public void setCreativeStorage(CreativePlayerStorage storage) {
        this.storage = storage;
    }

    public void setEnderChest(EnderChestStorage enderChest) {
        this.enderChest = enderChest;
    }

    public CuboidSelection getSelection() {
        return selection;
    }

    public void setSelection(CuboidSelection selection) {
        this.selection = selection;
    }

    public PasteSelection getPasteSelection() {
        return pasteSelection;
    }

    public void setPasteSelection(PasteSelection selection) {
        this.pasteSelection = selection;
    }

    public PlayerChangesByHand getChangesByHand() {
        return changesByHand;
    }

    public PlayerSnipeHandler getSnipe() {
        return snipeHandler;
    }

    public History getHistory() {
        return history;
    }

    public void addToHistory(ChangeMeta meta, Change change) {
        ChangeRecord changeRecord = ChangeRecord.instant(this, meta, change);
        history.add(changeRecord);

        AuditRecord auditRecord = new AuditRecord(
                System.currentTimeMillis(),
                getAccountId(),
                change.getType(),
                meta);
        getInstance().addToAuditHistory(auditRecord);
    }

    public void addToHistory(CreativeToolType tool, String changeTitleMM, Component text, CoolSound sound, Change change) {
        ChangeMeta meta = new ChangeMeta(tool, changeTitleMM, text, sound.soundEvent(), sound.pitch().getBasePitch());
        addToHistory(meta, change);
    }

    public void addToHistory(CreativeToolType tool, String changeTitleMM, SentMessage msg, Change change) {
        addToHistory(tool, changeTitleMM, msg.details(), msg.sound(), change);
    }

    public boolean isHoldingTool(CreativeToolType type) {
        ItemStack inHand = getItemInMainHand();
        return getCreative().isTool(inHand, type);
    }

    public CreativeMode getCreative() {
        return (CreativeMode) Kgs.INSTANCE.getMode();
    }

    public CreativeMessager msg() {
        return messager;
    }

    public CreativeBroadcasterWithInitiator broadcast() {
        CreativeBroadcaster broadcast = getInstance().broadcast();
        return new CreativeBroadcasterWithInitiator(this, broadcast);
    }

    public void saveCreativeStorage() throws Exception {
        writeStuffToPlayerStorage(storage);
        getCreative().getPlayersRepo().save(getAccount(),  storage).exceptionally(t -> {
            LOG.error(STR."Error saving creative storage for \{getAccountId()} \{this}", t);
            return null;
        });
    }

    private void writeStuffToPlayerStorage(CreativePlayerStorage storage) throws Exception {
        InventoryStorage invStorage = storage.getInventory(this);
        invStorage.setContents(Arrays.asList(getInventory().getItemStacks()));
        invStorage.setHeldSlot(getHeldSlot());

        storage.setFlySpeed(getFlyingSpeed());
    }

    public void writeStuffToWorldStorage(PlayerWorldStorage storage) {
        storage.setPosition(getPosition());
        storage.setSelectionStorage(selection.toStorage());
        storage.setFlying(isFlying());

        try {
            storage.setHistory(history);
        } catch (Throwable t) {
            storage.clearHistory();
            LOG.error("Error setting history in storage for " + getUsername() + " " + getUuid(), t);
            sendPit(NamedTextColor.DARK_RED, "ERROR", MM."<white>Failed writing undo history!");
        }
    }

    public void onSpawn(CreativeInstance instance) {
        try {
            spawnedFully = false;

            PlayerInventory playerInv = getInventory();

            PlayerWorldStorage worldStorage = instance.getStorage(this);
            selection = worldStorage.getSelectionStorage().createSelection(this);
            setFlying(worldStorage.isFlying());

            if (!isFlying()) {
                AttributeInstance gravity = getAttribute(Attribute.GRAVITY);
                double base = gravity.getBaseValue();
                gravity.setBaseValue(0.0);
                scheduleTicks(() -> gravity.setBaseValue(base), 10); // fix spawning in the ground
            }

            long lastVisit = worldStorage.getLastVisit();
            if (lastVisit == 0) {
                giveStarterItems();
            }
            worldStorage.setLastVisit(System.currentTimeMillis());

            history = worldStorage.getHistory(this);

            CreativePlayerStorage playerStorage = getCreativeStorage();
            InventoryStorage invStorage = playerStorage.getInventory(this);
            if (invStorage.hasProperCodecVersion()) {
                List<ItemStack> contents = invStorage.getContents();
                if (contents != null) {
                    playerInv.copyContents(contents.toArray(ItemStack[]::new));
                }
            } else {
                giveStarterItems();
                scheduleTicks(() -> sendPit(NamedTextColor.RED, "EEK!", MM."<gray>Your inventory was reset because our storage systems changed!"), 15);
            }

            int heldSlot = invStorage.getHeldSlot();
            setHeldItemSlot((byte) heldSlot);

            setFlyingSpeed(storage.getFlySpeed());
            applyWalkSpeedFromStorage();

            changesByHand.clear();
            changesByHand.setBufferingTicks(playerStorage.getHandBufferingTicks());

            if (storage.hasNightVision()) {
                NightVisionCommand.applyNightVision(this);
            }

            if (storage.isRenderingWorldBorder()) {
                sendWorldBorder(instance.getChunkLoader().getBoundsAsBorder());
            }

            joinTick = GlobalMinestomTicker.getTick();

            this.spawnedFully = true;
        } catch (Throwable t) {
            LOG.error(STR."Error during onSpawn for \{this}", t);
            sendMessage(MM."<dark_red>Something went wrong while you spawned, please report this!");
            kick(MM."<red>Critical error while spawning!");
        }
    }

    private void giveStarterItems() {
        PlayerInventory inv = getInventory();
        inv.addItemStack(new SelectionTool().renderNewItem(this));
        inv.setItemStack(8, new MainMenuTool().renderNewItem(this));
    }

    public boolean canEditWorld() {
        return canEditWorld(false);
    }

    public boolean canEditWorld(boolean sendMsg) {
        try {
            boolean canEdit = canEditWorldCached.get();
            if (sendMsg && !canEdit) {
                sendCantEditWorldMessage();
            }
            return canEdit;
        } catch (Throwable t) {
            LOG.error("Error checking build permission", t);
            return false;
        }
    }

    private boolean computeCanEditWorld() {
        ObjectId accountId = getAccountId();

        CreativeInstance creativeInstance = getInstance();
        WorldDef worldDef = creativeInstance.getWorldDef();
        if (accountId.equals(worldDef.ownership().playerId())) {
            return true;
        }

        BuildPermit permit = creativeInstance.getPermitForPlayer(this);
        return permit != null && !permit.isExpired(creativeInstance);
    }

    public void sendCantEditWorldMessage() {
        playSound(SoundEvent.BLOCK_NOTE_BLOCK_BIT, Pitch.base(0.5).addRand(0.45));
        sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>Don't have permission to edit this world!");
    }

    public void sendOutOfBoundsMessage() {
        playSound(SoundEvent.BLOCK_NOTE_BLOCK_BIT, Pitch.base(0.5).addRand(0.45));
        sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>That's outside the world's bounds!");
    }

    public MaskLookup computeMaskLookup() {
        List<MaskItem> maskItems = new ArrayList<>(4);
        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack item = slot.get(this);
            MaskItem maskItem = MaskItem.get(item);
            if (maskItem == null || maskItem.getMasks().isEmpty()) continue;
            maskItems.add(maskItem);
        }

        MaskWorkCache maskWorkCache = MaskWorkCache.create(this);

        return new MaskLookup(maskItems, maskWorkCache);
    }

    public void onDespawn(CreativeInstance instance) {
        if (!spawnedFully) {
            return;
        }
        spawnedFully = false;

        try {
            changesByHand.flush();

            PlayerWorldStorage storage = instance.getStorage(this);
            writeStuffToWorldStorage(storage);
            saveCreativeStorage();
        } catch (Throwable t) {
            LOG.error("Error writing stuff to storage on despawn", t);
        }

        try {
            selection.remove();
            pasteSelection.remove();
            snipeHandler.remove();
            despawnAttachedEntities();
        } catch (Throwable t) {
            LOG.error("Error dealing with stuff on despawn", t);
        }
    }

    private void despawnAttachedEntities() {
        attachedEntities.values().forEach(entity -> {
            try {
                entity.remove();
            } catch (Throwable t) {
                LOG.error(STR."Error removing attached entity \{entity.getClass()}", t);
            }
        });
        attachedEntities.clear();
    }

    public void applyWalkSpeedFromStorage() {
        float baseSpeed = storage.getWalkSpeed();

        int speedAmp = storage.getSpeedEnchant();
        if (speedAmp >= 0) {
            baseSpeed *= 1 + (0.2f * (speedAmp + 1));
        }

        if (speedAmp >= 0) {
            addEffect(new Potion(PotionEffect.SPEED, (byte) speedAmp, -1));
        } else {
            removeEffect(PotionEffect.SPEED);
        }

        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(baseSpeed);
    }

    @Override
    public @Nullable Point getTargetBlockPosition(int maxDistance) {
        return RaycastUtils.getTargetBlock(this, maxDistance);
    }

    public BlockVec getTargetBlockPositionOrMax(int maxRange) {
        Point target = getTargetBlockPosition(maxRange);
        if (target != null) {
            return new BlockVec(target);
        }

        Vec midAir = getPointInFront(maxRange);
        return new BlockVec(midAir);
    }

    public Vec getPointInFront(double range) {
        Ray ray = getEyeRay();
        return ray.origin().add(ray.dir().mul(range));
    }

    public int getTicksOnInstance() {
        return GlobalMinestomTicker.getTick() - joinTick;
    }

    @Override
    protected void customTick(long time) {
        super.customTick(time);

        if (canEditWorld()) {
            selection.tick();
        } else {
            selection.remove();
            if (!(selection instanceof NoCuboidSelection)) {
                setSelection(new NoCuboidSelection(this));
            }
        }

        getCreative().getToolsListener().tickForPlayer(this);
        changesByHand.tick();
        snipeHandler.tick();
    }

    public void attachIfAbsent(String key, Supplier<Entity> spawnedEntitySupplier) {
        Entity existing = attachedEntities.computeIfAbsent(key, k -> spawnedEntitySupplier.get());
        if (existing != null && existing.isRemoved()) {
            attachedEntities.put(key, spawnedEntitySupplier.get());
        }
    }
}
