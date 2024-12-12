package com.gmdsodt.jskim.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class StreamReader {
    FileChannel file;

    public StreamReader() {}

    public boolean loadFileChannel(String path) throws IOException {
        try {
            this.file = new RandomAccessFile(path, "r").getChannel();
        } catch (IOException ex) {
            throw new IOException("File Not Found");
//            return false;
        }

        return true;
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
