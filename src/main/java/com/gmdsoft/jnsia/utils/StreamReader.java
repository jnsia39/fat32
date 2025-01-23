package com.gmdsoft.jnsia.utils;

import tech.favware.result.Result;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class StreamReader {
    public StreamReader() {}

    public static Result<ByteBuffer> readFileChannel(FileChannel fileChannel, long offset, int size) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            fileChannel.position(offset).read(byteBuffer);

            return Result.ok(byteBuffer.flip());
        } catch ( IOException ex) {
            return Result.err(ex);
        }
    };
}
