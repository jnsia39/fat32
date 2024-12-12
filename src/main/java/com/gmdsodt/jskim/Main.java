package com.gmdsodt.jskim;

public class Main {
    public static void main(String[] args) {
        try {
            String path = "./FAT32_simple.mdf";

            FAT32 fat32 = new FAT32();
            FileSystem fs = fat32.buildFileSystem(path);
            System.out.println(fs);

            Node node = fs.get("DIR1/LEAF.JPG");
            if (node != null && node.exportTo("./output/LEAF.JPG")) {
                System.out.println("Export Success: " + path);
            } else {
                System.out.println("Export Fail: " + path);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}