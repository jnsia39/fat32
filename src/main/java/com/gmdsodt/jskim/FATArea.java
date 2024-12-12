package com.gmdsodt.jskim;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.*;

public class FATArea {
    private final List<Integer> fatInfo;

    FATArea() {
        this.fatInfo = new ArrayList<>();
    }

    public boolean makeFatChain(ByteBuffer bb) {
        try {
            while (bb.hasRemaining()) {
                int info = bb.getInt();
                this.fatInfo.add(info);
            }
        } catch (BufferUnderflowException ex) {
            return false;
        }

        return true;
    }

    public int getNext(int clusterNo) {
        return hasNextCluster(clusterNo) ? this.fatInfo.get(clusterNo) : 0xfffffff;
    }

    public List<Integer> getClusters(int clusterNo) {
        List<Integer> clusters = new ArrayList<>();
        int currCluster = clusterNo;

        while (hasNextCluster(currCluster)) {
            clusters.add(currCluster);
            currCluster = this.fatInfo.get(currCluster);
        }

        return clusters;
    }

    private boolean hasNextCluster(int clusterNo) {
        return (clusterNo != 0xfffffff && clusterNo < this.fatInfo.size());
    }

    @Override
    public String toString() {
        String fatInfoString = this.fatInfo.toString();
        return "FAT Area: " + fatInfoString.substring(0, Math.min(fatInfoString.length(), 200));
    }
}
