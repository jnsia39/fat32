package com.gmdsoft.jnsia;

import com.gmdsoft.jnsia.fat32.Fat32;
import com.gmdsoft.jnsia.utils.FileUtils;

public class Main {

    public static void main(String[] args) {
        String[] paths = new String[] {"./FAT32_simple.mdf"};

        // 여러 파일의 경로를 받아서
        for (String path: paths) {
            Fat32 fat32 = Fat32.create();

            // 각각의 파일시스템을 분석한다.
            FileUtils.makeFileChannelByPath(path)
                    .flatMap(fileChannel -> fat32.buildFileSystem(fileChannel))
                    .flatMap(fs -> fs.get("DIR1/LEAF.JPG"))
                    .flatMap(node -> node.exportTo("./output/LEAF.JPG"))
                    .onOk(message -> System.out.println(message))
                    .onErr(ex -> System.err.println(ex.getMessage()));
        }
    }

}