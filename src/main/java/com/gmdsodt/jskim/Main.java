package com.gmdsodt.jskim;

import tech.favware.result.Result;

public class Main {
    public static void main(String[] args) throws Exception {
            try {
                String path = "./FAT32_simple.mdf";

                FAT32 fat32 = new FAT32();
                Result<?> result = fat32.buildFileSystem(path)
                        .flatMap(fs -> fs.get("DIR1/LEAF.JPG"))
                        .flatMap(node -> node.exportTo("./output/LEAF.JPG"));

                if (!result.isOk())
                    result.unwrap();

                System.out.println("Success File Export");
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
    }
}