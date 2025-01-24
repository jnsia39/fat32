package com.gmdsoft.jnsia;

import com.gmdsoft.jnsia.utils.FileUtils;
import tech.favware.result.Result;

import java.nio.channels.FileChannel;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class FileChannelPool {
    private static final ConcurrentHashMap<String, FileChannel> fileChannelMap = new ConcurrentHashMap<>();

    public static Result<FileChannel> getFileChannel(String path) {
        FileChannel fileChannel = fileChannelMap.get(path);
        return fileChannel != null ? Result.ok(fileChannel) : Result.err(new NoSuchElementException());
    }

    public static void addFileChannel(String path) {
        FileUtils.makeFileChannelByPath(path).onOk(
            fileChannel -> fileChannelMap.putIfAbsent(path, fileChannel)
        );
    }
}
