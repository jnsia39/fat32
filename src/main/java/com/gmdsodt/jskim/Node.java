package com.gmdsodt.jskim;

import com.gmdsodt.jskim.type.NodeType;
import com.gmdsodt.jskim.util.StringUtil;
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
        this(".", NodeType.Directory, 0, 0, stream);
    }

    Node(String name, NodeType type, int actualSize, int allocSize, ScatteredStream stream) {
        this.name = name;
        this.type = type;
        this.actualSize = actualSize;
        this.allocSize = allocSize;
        this.stream = stream;
        this.children = new ArrayList<>();
    }

    // TODO: Add exception other cases.
    public Result<Boolean> exportTo(String path) {
        try {
            if (!new File(StringUtil.trimLastPath(path)).mkdirs())
                return Result.err(new Exception("Cannot Create Folders"));

            FileOutputStream fos = new FileOutputStream(path);
            ByteBuffer buf = this.stream.read(actualSize);
            fos.write(buf.array());
        } catch (IOException ex) {
            return Result.err(new IOException("Fail to File Export"));
        }

        return Result.ok(true);
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

    @Override
    public String toString() {
        String s0 = String.format("%s", this.name);

        return String.join(" ", s0);
    }
}