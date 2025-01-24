package com.gmdsoft.jnsia.fat32;

public class Cluster {
    int number;
    int size;
    long address;

    Cluster(int number, int size, long address) {
        this.number = number;
        this.size = size;
        this.address = address;
    }
}
