package com.gmdsodt.jskim;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.List;

public class ScatteredStream {
    FileChannel file;
    List<Extent> extents;
    long offset;

    ScatteredStream(List<Extent> extents, FileChannel file) {
        this.extents = extents;
        this.offset = 0L;
        this.file = file;
    }

    public ByteBuffer readAll() throws IOException {
        long length = 0;
        for (Extent extent: extents)
            length += extent.size;

        return read(length).flip();
    }

    // Buffer.allocate 되는 size 너무 큼 => 자바 힙 메모리 초과될 수 있음
    public ByteBuffer read(long length) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate((int) length);
        ByteBuffer readBuf;
        long totalMove = 0;

        for (Extent extent: extents) {
            long extentOffset = extent.offset;
            long extentSize = extent.size;

            if (this.offset >= extentSize + extentOffset)
                continue;

            int move = (int)Math.min(extentSize - this.offset, length - totalMove);
            readBuf = ByteBuffer.allocate(move);

            this.offset += extentOffset;
            this.file.position(this.offset).read(readBuf);
            this.offset = 0;

            buf.put(readBuf.flip());
            totalMove += move;

            if (move >= length)
                break;
        }

        return buf;
    }

    public void position(long position) {
        this.offset = position;
    }

    @Override
    public String toString() {
        String s0 = String.format("Current Offset: %d", this.offset);
        String s1 = String.format("Extents: %s", this.extents.toString());

        return String.join("\n", s0, s1);
    }
}
