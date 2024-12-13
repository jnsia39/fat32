package com.gmdsodt.jskim;

public class Main {
    public static void main(String[] args) {
        String path = "./FAT32_simple.mdf";
        FAT32.create()
            .flatMap(fat32 -> fat32.buildFileSystem(path))
            .flatMap(fs -> fs.get("DIR1/LEAF.JPG"))
            .flatMap(node -> node.exportTo("./output/LEAF.JPG"))
            .onOk(v -> System.out.println("Success"))
            .onErr(ex -> System.err.println(ex.getMessage()));
    }
}