package io.kloon.gameserver.creative.storage.defs;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.creative.storage.deletion.WorldDeletion;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.creative.storage.owner.WorldOwnerStorage;
import io.kloon.infra.facts.KloonDataCenter;
import io.kloon.infra.mongo.storage.BufferedDocument;
import net.minestom.server.item.Material;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WorldDef {
    private final ObjectId id;
    private final KloonDataCenter datacenter;
    private final WorldOwner owner;

    private String name;
    private Material menuIcon;
    private final Map<ObjectId, BuildPermit> permitsByAccountId = new HashMap<>();

    private final BufferedDocument document;

    public WorldDef(ObjectId id, String name, Material menuIcon, WorldOwner owner, KloonDataCenter datacenter) {
        this.document = new BufferedDocument(new Document());
        this.id = id;
        this.name = name;
        this.datacenter = datacenter;
        this.owner = owner;
        this.menuIcon = menuIcon;

        document.putObjectId(ID, id);
        document.putString(NAME, name);
        document.putString(MENU_ICON, menuIcon.name());
        document.putObject(OWNERSHIP, owner.asOwnership(), WorldOwnerStorage.BSON_CODEC);
        document.putString(DATACENTER, datacenter.getDbKey());
        document.putLong(CREATION_TIMESTAMP, System.currentTimeMillis());
    }

    public WorldDef(Document doc) {
        this.document = new BufferedDocument(doc);
        this.id = document.getObjectId(ID);
        this.name = document.getString(NAME, "Unknown");
        this.datacenter = KloonDataCenter.parse(document.getString(DATACENTER));
        this.owner = document.getObject(OWNERSHIP, d -> WorldOwnerStorage.BSON_CODEC.decode(d).toOwner());
        reconstructPermitsCache();
    }

    public BufferedDocument getDocument() {
        return document;
    }

    public ObjectId _id() {
        return id;
    }

    public String idHex() {
        return id.toHexString();
    }

    public void broadcastInvalidate() {
        Kgs.INSTANCE.getWorldListsCache().invalidate(owner);
        Kgs.getCreative().broadcastWorldInvalidate(id, owner.asOwnership().playerId()); // eek
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        document.putString(NAME, name);
    }

    public KloonDataCenter datacenter() {
        return datacenter;
    }

    public WorldOwner owner() {
        return owner;
    }

    public WorldOwnerStorage ownership() {
        return owner.asOwnership();
    }

    public long creationTimestamp() {
        return document.getLong(CREATION_TIMESTAMP);
    }

    @Nullable
    public WorldDeletion deletion() {
        return document.getObject(DELETION, WorldDeletion.BSON_CODEC);
    }

    public void setDeletion(@Nullable WorldDeletion deletion) {
        document.putObject(DELETION, deletion, WorldDeletion.BSON_CODEC);
    }

    public Material menuIcon() {
        String iconStr = document.getString(MENU_ICON);
        Material material = iconStr == null ? null : Material.fromKey(iconStr);
        return material == null ? Material.DIRT : material;
    }

    public void setMenuIcon(Material icon) {
        this.menuIcon = icon;
        document.putString(MENU_ICON, icon.name());
    }

    public boolean deleted() {
        return deletion() != null;
    }

    public List<WorldCopyInfo> copyChain() {
        return document.getArray(COPY_CHAIN, WorldCopyInfo.BSON_CODEC).toList();
    }

    public WorldDef withCopyChain(List<WorldCopyInfo> copyChain) {
        document.putArray(COPY_CHAIN, copyChain, WorldCopyInfo.BSON_CODEC);
        return this;
    }

    @Nullable
    public BuildPermit getPermitForPlayer(ObjectId accountId) {
        return permitsByAccountId.get(accountId);
    }

    public List<BuildPermit> buildPermits() {
        return new ArrayList<>(permitsByAccountId.values());
    }

    private List<BuildPermit> loadBuildPermits() {
        return document.getArray(BUILD_PERMITS, BuildPermit.BSON_CODEC).toList();
    }

    public void setPermits(List<BuildPermit> permits) {
        document.putArray(BUILD_PERMITS, permits, BuildPermit.BSON_CODEC);
        reconstructPermitsCache();
    }

    private void reconstructPermitsCache() {
        permitsByAccountId.clear();
        loadBuildPermits().forEach(permit -> permitsByAccountId.put(permit.accountId(), permit));
    }

    public void setHasEverIssuedPermit(boolean everIssuedPermit) {
        document.putBoolean(EVER_ISSUED_PERMIT, everIssuedPermit);
    }

    public boolean hasEverIssuedPermit() {
        return document.getBoolean(EVER_ISSUED_PERMIT);
    }

    @Override
    public String toString() {
        return "(" + idHex() + ", " + name() + ")";
    }

    public static final String ID = "_id";
    private static final String NAME = "name";
    private static final String MENU_ICON = "menuIcon";
    public static final String CREATION_TIMESTAMP = "creation_timestamp";
    private static final String OWNERSHIP = "ownership";
    public static final String DELETION = "deletion";
    private static final String DATACENTER = "datacenter";
    private static final String COPY_CHAIN = "copyChain";
    public static final String BUILD_PERMITS = "buildPermits";
    public static final String PERMITS_IGNORED_BY = "permitsIgnoredBy";
    public static final String EVER_ISSUED_PERMIT = "everIssuedPermit";
}
