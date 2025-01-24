package com.gmdsoft.jnsia.fat32;

import com.gmdsoft.jnsia.type.NodeType;
import com.gmdsoft.jnsia.utils.StringUtil;
import tech.favware.result.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Node {
    int actualSize;
    int allocSize;
    String name;
    NodeType type;
    List<Node> children;
    ScatteredStream stream;

    Node(ScatteredStream stream) {
        this.stream = stream;
        this.children = new ArrayList<>();
    }

    // TODO: Add exception other cases.
    public Result<String> exportTo(String path) {
        try {
            File parentDir = new File(StringUtil.trimLastPath(path));

            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(path);
            ByteBuffer buf = this.stream.read(actualSize);
            fos.write(buf.array());
        } catch (IOException ex) {
            return Result.err(new IOException("Fail to File Export"));
        }

        return Result.ok("Success");
    }

    public boolean isExpandable() {
        if (StringUtil.isDotOrDoubleDot(this.name))
            return false;

        return this.isDir();
    }

    public boolean isDir() {
        return this.type == NodeType.Directory;
    }

    public boolean isFile() {
        return this.type == NodeType.File;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public void setActualSize(int actualSize) {
        this.actualSize = actualSize;
    }

    public void setAllocSize(int allocSize) {
        this.allocSize = allocSize;
    }

    @Override
    public String toString() {
        String s0 = String.format("%s", this.name);

        return String.join(" ", s0);
    }
}