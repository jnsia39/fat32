package com.gmdsodt.jskim;

import com.gmdsodt.jskim.util.ByteUtil;
import com.gmdsodt.jskim.util.StreamReader;
import tech.favware.result.Result;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FAT32 {
    FileSystem fs;
    StreamReader reader;
    BootRecord br;
    FATArea fatArea;

    private FAT32() {};

    public static Result<FAT32> create() {
        try {
            return Result.ok(new FAT32());
        } catch (Exception ex) {
            return Result.err(ex);
        }
    }

    public Result<FileSystem> buildFileSystem(String path) {
        this.reader = new StreamReader();
        this.br = new BootRecord();
        this.fatArea = new FATArea();
        this.fs = new FileSystem();

        Result<Boolean> res = this.reader.loadFileChannel(path)
                .flatMap(b -> this.br.analyze(reader.getFileChannel()))
                .flatMap(b -> this.fatArea.makeFatChain(reader.readBuffer(this.br.reservedAreaSize, this.br.fatSize)))
                .flatMap(b -> expandAll(makeRoot()));

        return res.isOk() ? Result.ok(this.fs) : Result.err(new Exception("Exception"));
    }

    private Node makeRoot() {
        ScatteredStream stream = makeStream(this.br.rootClusterNo);

        return new Node(stream);
    }

    private Node makeNode(DirEntry dirEntry) {
        ScatteredStream stream = makeStream(dirEntry.clusterNo);
        int allocSize = this.br.clusterSize * stream.extents.size();

        return new Node(dirEntry.name, dirEntry.type, dirEntry.actualSize, allocSize, stream);
    }

    private ScatteredStream makeStream(int clusterNo) {
        List<Integer> clusters = this.fatArea.getClusters(clusterNo);
        List<Extent> extents = new ArrayList<>();

        for (int cluster: clusters) {
            long offset = toPhysicalAddr(cluster);
            extents.add(new Extent(offset, this.br.clusterSize));
        }

        return new ScatteredStream(extents, reader.getFileChannel());
    }

    private Result<Boolean> expandAll(Node node) throws IOException {
        Node rootNode = expand(node);
        if (!fs.unfold(rootNode, ""))
            return Result.err(new Exception("Exception from Unfold Process"));

        return Result.ok(true);
    };

    private Node expand(Node node) throws IOException {
        ByteBuffer buf = node.stream.readAll();
        byte[] bytes = new byte[0x20];
        Stack<String> stack = new Stack<>();

        while (buf.hasRemaining()) {
            buf.get(bytes);

            if (ByteUtil.onlyZeroesIn(bytes))
                break;

            ByteBuffer wrapBuf = ByteBuffer.wrap(bytes);

            DirEntry dirEntry = new DirEntry();
            if (!dirEntry.analyze(wrapBuf).isOk())
                continue;

            if (dirEntry.isLfn()) {
                Result<String> lfn = dirEntry.makeLfnFrom(wrapBuf);
                if (lfn.isOk())
                    lfn.map(stack::add);

                continue;
            }

            StringBuilder sb = new StringBuilder();

            while (!stack.isEmpty())
                sb.append(stack.pop());

            dirEntry.name = sb.isEmpty() ? dirEntry.name : sb.toString();

            Node childNode = makeNode(dirEntry);

            if (dirEntry.isExpandable())
                childNode = expand(childNode);

            node.children.add(childNode);
        }

        return node;
    }

    private long toPhysicalAddr(int clusterNo) {
        return this.br.dataAreaAddr + (long)(clusterNo - this.br.rootClusterNo) * this.br.clusterSize;
    }

    @Override
    public String toString() {
        return "FAT32 FileSystem";
    }
}
