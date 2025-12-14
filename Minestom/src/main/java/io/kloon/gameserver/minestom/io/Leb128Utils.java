package io.kloon.gameserver.minestom.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// https://github.com/facebook/buck/blob/main/third-party/java/dx/src/com/android/dex/Leb128.java
public final class Leb128Utils {
    private static final int CONTINUATION = 0b1000_0000;
    private static final int DATA = 0b0111_1111;

    private Leb128Utils() {
    }

    public static int readIntSigned(InputStream in) throws IOException {
        int result = 0;
        int cur;
        int count = 0;
        int signBits = -1;

        do {
            cur = in.read() & 0xff;
            result |= (cur & 0x7f) << (count * 7);
            signBits <<= 7;
            count++;
        } while (((cur & 0x80) == 0x80) && count < 5);

        if ((cur & 0x80) == 0x80) {
            throw new EOFException("invalid LEB128 sequence");
        }

        // Sign extend if appropriate
        if (((signBits >> 1) & result) != 0 ) {
            result |= signBits;
        }

        return result;
    }

    public static void writeIntSigned(int value, OutputStream out) throws IOException {
        int remaining = value >> 7;
        boolean hasMore = true;
        int end = ((value & Integer.MIN_VALUE) == 0) ? 0 : -1;

        while (hasMore) {
            hasMore = (remaining != end)
                      || ((remaining & 1) != ((value >> 6) & 1));

            out.write((byte) ((value & 0x7f) | (hasMore ? 0x80 : 0)));
            value = remaining;
            remaining >>= 7;
        }
    }

    public static int readIntUnsigned(InputStream in) throws IOException {
        int result = 0;
        int shift = 0;
        int byteValue;
        do {
            byteValue = in.read();
            if (byteValue == -1) {
                throw new IOException("Unexpected end of stream");
            }
            result |= (byteValue & 0x7F) << shift;
            shift += 7;
        } while ((byteValue & 0x80) != 0);
        return result;
    }

    public static void writeIntUnsigned(OutputStream out, int value) throws IOException {
        do {
            int byteValue = value & 0x7F;
            value >>>= 7;
            if (value != 0) {
                byteValue |= 0x80;
            }
            out.write(byteValue);
        } while (value != 0);
    }

    public static int getVarIntSize(int value) {
        return (value & 0xFFFFFF80) == 0
                ? 1 : (value & 0xFFFFC000) == 0
                ? 2 : (value & 0xFFE00000) == 0
                ? 3 : (value & 0xF0000000) == 0
                ? 4 : 5;
    }

    public static long readLongSigned(InputStream in) throws IOException {
        long value = 0;
        byte bytes = 0;
        boolean keepGoing = true;
        while (keepGoing) {
            long current = in.read();
            if (current < 0) {
                throw new EOFException();
            }
            keepGoing = (current & CONTINUATION) == CONTINUATION;
            value |= current << bytes++ * 7;
            if (bytes > 10 && keepGoing) {
                throw new RuntimeException("Leb128 signed long is too large");
            }
        }
        return value;
    }

    public static void writeLongSigned(long value, OutputStream out) throws IOException {
        do {
            long data = value & DATA;
            value >>= 7;
            if (value != 0) {
                data |= CONTINUATION;
            }
            out.write((int) data);
        } while (value != 0);
    }
}