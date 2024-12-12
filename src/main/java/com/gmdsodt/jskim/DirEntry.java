package com.gmdsodt.jskim;

import com.gmdsodt.jskim.type.NodeType;
import com.gmdsodt.jskim.util.StringUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DirEntry {
    String name;
    int attribute;
    int clusterNo;
    int actualSize;
    NodeType type;

    public boolean analyze(ByteBuffer bb) {
        try {
            byte[] nameBytes = new byte[11];
            bb.get(nameBytes);
            this.name = StringUtil.trimRight(new String(nameBytes, 0, 8)).trim();
            String ext = new String(nameBytes, 8, 3).trim();

            this.attribute = bb.get() & 0xff;
            this.type = (this.attribute & 0x10) > 0 ? NodeType.Directory : NodeType.File;

            if (this.type == NodeType.File && !ext.isEmpty())
                this.name += "." + ext;

            bb.order(ByteOrder.LITTLE_ENDIAN);
            int clusterHi = bb.position(20).getShort() & 0xffff;
            int clusterLo = bb.position(26).getShort() & 0xffff;
            this.clusterNo = (clusterHi << 16) + clusterLo;
            this.actualSize = bb.getInt();
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    public boolean isExpandable() {
        if (StringUtil.isDotOrDoubleDot(name))
            return false;

        return this.isDir();
    }

    public boolean isDir() {
        return this.type == NodeType.Directory;
    }

    public boolean isFile() {
        return this.type == NodeType.File;
    }

    public boolean isLfn() {
        return this.attribute == 0x0F;
    }

    @Override
    public String toString() {
        String s0 = String.format("Name: %s", this.name);
        String s1 = String.format("Cluster Number: %d", this.clusterNo);
        String s2 = String.format("Node Type: %s", this.type);
        String s3 = String.format("Actual Size: %d", this.actualSize);

        return String.join("\n", s0, s1, s2, s3);
    }
}
