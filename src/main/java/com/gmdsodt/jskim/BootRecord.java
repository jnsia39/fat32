package com.gmdsodt.jskim;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class BootRecord {
    int sectorSize;
    int clusterSize;
    int fatSize;
    int reservedAreaSize;
    int dataAreaAddr;
    int rootClusterNo;

    public boolean analyze(FileChannel file) {
        try {
            ByteBuffer buf = ByteBuffer.allocate(0x200);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            file.read(buf);

            this.sectorSize = buf.position(11).getShort();
            int sectorsPerCluster = buf.get() & 0xff;
            int sectorsOfReservedArea = buf.getShort();
            int fatCount = buf.get() & 0xff;
            int sectorsPerFATSection = buf.position(36).getInt();
            this.rootClusterNo = buf.position(44).getInt();
            int magicNo = buf.position(510).getShort() & 0xffff;
            if (magicNo != 0xAA55)
                return false;

            this.clusterSize = this.sectorSize * sectorsPerCluster;
            this.fatSize = this.sectorSize * sectorsPerFATSection;
            this.reservedAreaSize = sectorsOfReservedArea * this.sectorSize;
            this.dataAreaAddr = this.reservedAreaSize + this.fatSize * fatCount;
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        String s0 = String.format("Sector Size: 0x%s", Integer.toHexString(this.sectorSize));
        String s1 = String.format("Cluster Size: 0x%s", Integer.toHexString(this.clusterSize));
        String s2 = String.format("FAT Size: 0x%s", Integer.toHexString(this.fatSize));
        String s3 = String.format("Address of Data Area: 0x%s", Integer.toHexString(this.dataAreaAddr));
        String s4 = String.format("Root Cluster Number: 0x%s", Integer.toHexString(this.rootClusterNo));

        return String.join("\n", s0, s1, s2, s3, s4);
    }
}
