package com.gmdsoft.jnsia;

import com.gmdsoft.jnsia.utils.FileUtils;
import tech.favware.result.Result;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class FileChannelPool {
    private static final Map<String, FileChannel> fileChannelMap = new HashMap<>();

    private FileChannelPool() {}

    public static Result<FileChannel> getFileChannel(String path) {
        FileChannel fileChannel = fileChannelMap.get(path);
        return fileChannel != null ? Result.ok(fileChannel) : Result.err(new NoSuchElementException());
    }

    public static Result<FileChannel> addFileChannel(String path) {
            return FileUtils.makeFileChannelByPath(path)
                    .onOk(fileChannel -> fileChannelMap.put(path, fileChannel));
    }
}
