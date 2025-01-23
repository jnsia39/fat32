package com.gmdsoft.jnsia.utils;

import tech.favware.result.Result;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static Result<FileChannel> makeFileChannelByPath(String path) {
        try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
            return Result.ok(file.getChannel());
        } catch (IOException ex) {
            return Result.err(ex);
        }
    }
}
