package com.gmdsoft.jnsia;

import com.gmdsoft.jnsia.utils.ByteUtil;
import com.gmdsoft.jnsia.utils.StreamReader;
import tech.favware.result.Result;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FAT32 {
    FileSystem fs;
    StreamReader reader;
    BootRecord bootRecord;
    FATArea fatArea;

    // 임의로 넣은 property
    String filePath;

    private FAT32() {};

    public static Result<FAT32> create() {
        try {
            return Result.ok(new FAT32());
        } catch (Exception ex) {
            return Result.err(ex);
        }
    }

    public Result<FileSystem> buildFileSystem(String[] paths) {
        this.bootRecord = new BootRecord();
        this.fatArea = new FATArea();
        this.fs = new FileSystem();

        // 임의로 넣은 property
        this.filePath = paths[0];

        Result<Boolean> res = FileChannelPool.addFileChannel(this.filePath)
                .flatMap(fileChannel -> this.bootRecord.analyze(fileChannel))
                .flatMap(fileChannel -> readFatArea(fileChannel))
                .flatMap(FatArea -> this.fatArea.makeFatChain(FatArea))
                .flatMap(fatChain -> expandAll(makeRoot()));
        System.out.println(res);

        return res.isOk() ? Result.ok(this.fs) : Result.err(new Exception());
    }

    private Result<ByteBuffer> readFatArea(FileChannel fileChannel) {
       return StreamReader.readFileChannel(fileChannel, this.bootRecord.reservedAreaSize, this.bootRecord.fatSize);
    }

    private Node makeRoot() {
        ScatteredStream stream = makeStream(this.bootRecord.rootClusterNo);

        return new Node(stream);
    }

    private Node makeNode(DirEntry dirEntry) {
        ScatteredStream stream = makeStream(dirEntry.clusterNo);
        int allocSize = this.bootRecord.clusterSize * stream.extents.size();

        return new Node(dirEntry.name, dirEntry.type, dirEntry.actualSize, allocSize, stream);
    }

    private ScatteredStream makeStream(int clusterNo) {
        List<Integer> clusters = this.fatArea.getClusters(clusterNo);
        List<Extent> extents = new ArrayList<>();

        for (int cluster: clusters) {
            long offset = toPhysicalAddr(cluster);
            extents.add(new Extent(offset, this.bootRecord.clusterSize));
        }

        return new ScatteredStream(extents, FileChannelPool.getFileChannel(this.filePath).orElse(null));
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

            if (dirEntry.isExpandable()) {
                childNode = expand(childNode);
            }

            node.children.add(childNode);
        }

        return node;
    }

    private long toPhysicalAddr(int clusterNo) {
        return this.bootRecord.dataAreaAddr + (long)(clusterNo - this.bootRecord.rootClusterNo) * this.bootRecord.clusterSize;
    }

    @Override
    public String toString() {
        return "FAT32 FileSystem";
    }
}
