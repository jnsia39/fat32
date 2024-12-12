package com.gmdsodt.jskim;

import com.gmdsodt.jskim.util.ByteUtil;
import com.gmdsodt.jskim.util.StreamReader;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FAT32 {
    FileSystem fs;
    StreamReader reader;
    BootRecord br;
    FATArea fatArea;

    public FileSystem buildFileSystem(String path) throws Exception {
        this.reader = new StreamReader();
        if (!this.reader.loadFileChannel(path))
            throw new IOException();

        this.br = new BootRecord();
        if (!this.br.analyze(reader.getFileChannel()))
            throw new IOException();

        this.fatArea = new FATArea();
        if (!this.fatArea.makeFatChain(reader.readBuffer(this.br.reservedAreaSize, this.br.fatSize)))
            throw new BufferUnderflowException();

        this.fs = new FileSystem();
        if (!expandAll(makeRoot()))
            throw new Exception("Can't open root folder");

        return this.fs;
    }

    private Node makeRoot() {
        List<Integer> clusters = this.fatArea.getClusters(this.br.rootClusterNo);
        ScatteredStream stream = makeStream(clusters);

        return new Node(stream);
    }

    private Node makeNode(DirEntry dirEntry) {
        List<Integer> clusters = this.fatArea.getClusters(dirEntry.clusterNo);
        ScatteredStream stream = makeStream(clusters);

        int allocSize = this.br.clusterSize * clusters.size();

        return new Node(dirEntry.name, dirEntry.type, dirEntry.actualSize, allocSize, stream);
    }

    private ScatteredStream makeStream(List<Integer> clusters) {
        List<Extent> extents = new ArrayList<>();

        for (int clusterNo: clusters) {
            long offset = toPhysicalAddr(clusterNo);
            extents.add(new Extent(offset, this.br.clusterSize));
        }

        return new ScatteredStream(extents, reader.getFileChannel());
    }

    private boolean expandAll(Node node) throws IOException {
        Node rootNode = expand(node);

        return fs.unfold(rootNode, "");
    };

    private Node expand(Node node) throws IOException {
        ByteBuffer buf = node.stream.readAll();
        byte[] bytes = new byte[0x20];

        while (buf.hasRemaining()) {
            buf.get(bytes);

            if (ByteUtil.onlyZeroesIn(bytes))
                break;

            DirEntry dirEntry = new DirEntry();
            if (!dirEntry.analyze(ByteBuffer.wrap(bytes)))
                continue;

            if (dirEntry.isLfn())
                continue;

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
