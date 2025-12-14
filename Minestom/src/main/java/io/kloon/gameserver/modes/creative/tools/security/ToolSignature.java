package io.kloon.gameserver.modes.creative.tools.security;

import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.tag.Tag;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

public final class ToolSignature {
    private static final Logger LOG = LoggerFactory.getLogger(ToolSignature.class);

    public static final Tag<String> KLOON_SIG = Tag.String("kloon:sig");

    private static final HmacUtils HMAC;
    static {
        try {
            SecureRandom random = new SecureRandom();
            byte[] secret = new byte[32];
            random.nextBytes(secret);
            HMAC = new HmacUtils("HmacSHA256", secret);
        } catch (Throwable t) {
            t.printStackTrace();
            Runtime.getRuntime().exit(1);
            throw new RuntimeException("Error with ToolSignature", t);
        }
    }

    public static ItemStack signed(ItemStack item) {
        if (!requiresSignature(item)) {
            return item;
        }

        String signature = computeHmac(item);
        return item.withTag(KLOON_SIG, signature);
    }

    public static String computeHmac(ItemStack item) {
        try {
            byte[] itemBytes;
            if (item.hasTag(MaskItem.TAG)) {
                itemBytes = MaskItem.encodeSigContents(item);
            } else {
                ItemStack itemWithoutSig = item.withAmount(1).withTag(KLOON_SIG, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                CustomData customData = itemWithoutSig.get(DataComponents.CUSTOM_DATA);

                CompoundBinaryTag tagToWrite = customData == null ? itemWithoutSig.toItemNBT() : customData.nbt();
                tagToWrite.type().write(tagToWrite, new DataOutputStream(bos));

                itemBytes = bos.toByteArray();
            }
            byte[] hmac = HMAC.hmac(itemBytes);
            return Base64.getEncoder().encodeToString(hmac);
        } catch (Throwable t) {
            LOG.error("Error in computeSignature", t);
            throw new RuntimeException("Error in computeSignature", t);
        }
    }

    public static Validity isValid(ItemStack item) {
        try {
            if (!requiresSignature(item)) {
                return Validity.VALID_NOT_NEEDED;
            }

            if (!item.hasTag(KLOON_SIG)) {
                return Validity.INVALID_MISSING_SIG;
            }

            String provided = item.getTag(KLOON_SIG);
            String computed = computeHmac(item);
            return computed.equals(provided)
                    ? Validity.VALID_CONFIRMED
                    : Validity.INVALID_MISMATCH;
        } catch (Throwable t) {
            LOG.error("Error verifying tool signature", t);
            return Validity.INVALID_ERROR;
        }
    }

    public enum Validity {
        VALID_NOT_NEEDED(true),
        VALID_CONFIRMED(true),
        INVALID_MISSING_SIG(false),
        INVALID_MISMATCH(false),
        INVALID_ERROR(false),
        ;

        private final boolean valid;

        Validity(boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }
    }

    private static boolean requiresSignature(ItemStack item) {
        return item.hasTag(CreativeTool.TOOL_TYPE_TAG)
               || item.hasTag(CreativeTool.TOOL_DATA)
               || item.hasTag(PatternBlock.TAG)
               || item.hasTag(MaskItem.TAG);
    }
}
