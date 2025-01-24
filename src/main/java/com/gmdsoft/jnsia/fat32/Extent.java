package com.gmdsoft.jnsia.fat32;

public class Extent {
    long offset;
    long size;

    Extent(long offset, long size) {
        this.offset = offset;
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("Offset: %d / Size: %d", this.offset, this.size);
    }
}