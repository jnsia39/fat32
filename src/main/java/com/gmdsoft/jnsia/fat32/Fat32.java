package com.gmdsoft.jnsia.fat32;

import com.gmdsoft.jnsia.utils.ByteUtil;
import tech.favware.result.Result;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Stack;

public class Fat32 {
    FileChannel fc;

    private Fat32() {};

    public static Fat32 create() {
        return new Fat32();
    }

    public Result<FileSystem> buildFileSystem(FileChannel fc) {
        this.fc = fc;

        return BootRecord.create()
                .flatMap(br -> br.analyze(fc))
                .flatMap(br -> readFat(br))
                .flatMap(fat -> fat.makeFatChain(fc))
                .flatMap(fat -> expandAll(fat))
                .flatMap(node -> new FileSystem().unfold(node));
    }

    private Result<Fat> readFat(BootRecord br) {
        return Result.ok(new Fat(br.reservedAreaSize, br.fatAreaSize * br.fatAreaCount, br.rootClusterNo, br.clusterSize));
    }

    private Result<Node> expandAll(Fat fat) throws IOException {
        Node rootNode = makeNode(fat, fat.rootClusterNo);

        return Result.ok(expand(fat, rootNode));
    };

    private Node expand(Fat fat, Node node) throws IOException {
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

            Node childNode = makeNode(fat, dirEntry.clusterNo);

            if (dirEntry.isExpandable()) {
                childNode = expand(fat, childNode);
            }

            int allocSize = dirEntry.clusterNo * node.stream.extents.size();

            node.setName(dirEntry.name);
            node.setType(dirEntry.type);
            node.setActualSize(dirEntry.actualSize);
            node.setAllocSize(allocSize);

            node.children.add(childNode);
        }

        return node;
    }

    private Node makeNode(Fat fat, int clusterNumber) {
        ScatteredStream stream = makeStream(fat, clusterNumber);

        return new Node(stream);
    }

    private ScatteredStream makeStream(Fat fat, int clusterNumber) {
        ScatteredStream stream = new ScatteredStream(this.fc);
        List<Cluster> clusters = fat.getClusters(clusterNumber);

        for (Cluster cluster: clusters) {
            stream.pushExtent(new Extent(cluster.address, cluster.size));
        }

        return stream;
    }

    @Override
    public String toString() {
        return "FAT32 FileSystem";
    }
}
