package com.rhcloud.igorbotian.rsskit.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class IOUtils {

    private static final int BUFFER_SIZE = 16 * 1024;

    private IOUtils() {
        //
    }

    public static void copy(InputStream src, OutputStream dest) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);

        copy(Channels.newChannel(src), Channels.newChannel(dest));
    }

    public static void copy(ReadableByteChannel src, WritableByteChannel dest) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);

        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }

        buffer.flip();

        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
}
