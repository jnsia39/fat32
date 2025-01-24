package com.gmdsoft.jnsia.fat32;

import com.gmdsoft.jnsia.utils.StreamReader;
import tech.favware.result.Result;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class Fat {
    int address;
    int size;
    int rootClusterNo;
    int clusterSize;
    int dataAreaAddr;
    private final List<Integer> fatChain;

    Fat(int address, int size, int rootClusterNo, int clusterSize) {
        this.address = address;
        this.size = size;
        this.rootClusterNo = rootClusterNo;
        this.clusterSize = clusterSize;
        this.dataAreaAddr = address + size;
        this.fatChain = new ArrayList<>();
    }

    public Result<Fat> makeFatChain(FileChannel fc) {
        return StreamReader.readFileChannel(fc, this.address, this.size)
                .flatMap(bb -> readByteBuffer(bb))
                .flatMap(numbers -> appendAll(this.fatChain, numbers))
                .flatMap(bool -> Result.ok(this));
    }

    public int getNext(int clusterNo) {
        return hasNextCluster(clusterNo) ? this.fatChain.get(clusterNo) : 0xfffffff;
    }

    public List<Cluster> getClusters(int clusterNo) {
        List<Cluster> clusters = new ArrayList<>();
        int currClusterNo = clusterNo;

        while (hasNextCluster(currClusterNo)) {
            long clusterAddr = toPhysicalAddr(currClusterNo);
            clusters.add(new Cluster(clusterNo, this.clusterSize, clusterAddr));
            currClusterNo = this.fatChain.get(currClusterNo);
        }

        return clusters;
    }

    private Boolean hasNextCluster(int clusterNo) {
        return (clusterNo != 0xfffffff && clusterNo < this.fatChain.size());
    }

    private Long toPhysicalAddr(int clusterNo) {
        return this.address + this.size + (long) (clusterNo - this.rootClusterNo) * this.clusterSize;
    }

    private Result<List<Integer>> readByteBuffer(ByteBuffer bb) {
        List<Integer> result = new ArrayList<>();

        try {
            while (bb.hasRemaining()) {
                result.add(bb.getInt());
            }
        } catch (BufferUnderflowException ex) {
            return Result.err(ex);
        }

        return Result.ok(result);
    }

    private Result<Boolean> appendAll(List<Integer> targetList, List<Integer> sourceList) {
        return Result.ok(targetList.addAll(sourceList));
    }

    @Override
    public String toString() {
        String fatInfoString = this.fatChain.toString();
        return "FAT Area: " + fatInfoString.substring(0, Math.min(fatInfoString.length(), 200));
    }
}
