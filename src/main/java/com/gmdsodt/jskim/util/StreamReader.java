package com.gmdsodt.jskim.util;

import tech.favware.result.Result;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class StreamReader {
    FileChannel file;

    public StreamReader() {}

    public Result<Boolean> loadFileChannel(String path) throws IOException {
        try {
            this.file = new RandomAccessFile(path, "r").getChannel();
        } catch (IOException ex) {
            return Result.err(new IOException("File Not Found"));
        }

        return Result.ok(true);
    }

    public FileChannel getFileChannel() {
        return this.file;
    }

    public ByteBuffer readBuffer(long offset, int size) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        this.file.position(offset).read(buf);

        return buf.flip();
    };
}
