package net.thenextlvl.commander.util;

import core.file.FileIO;
import core.io.PathIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32C;

public final class FileUtil {
    private static final int BUFFER_SIZE = 8192;

    public static String digest(FileIO<?> file) {
        return digest(((PathIO) file.getIO()).getPath());
    }

    public static String digest(Path path) {
        try {
            if (!Files.exists(path)) return "";
            var crc = new CRC32C();
            try (var input = Files.newInputStream(path)) {
                var buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    crc.update(buffer, 0, bytesRead);
                }
            }
            return Long.toHexString(crc.getValue());
        } catch (IOException e) {
            return "";
        }
    }

    public static long lastModified(FileIO<?> file) {
        try {
            var path = ((PathIO) file.getIO()).getPath();
            if (!Files.exists(path)) return System.currentTimeMillis();
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return System.currentTimeMillis();
        }
    }

    public static boolean hasChanged(FileIO<?> file, String digest, long lastModified) {
        return lastModified(file) != lastModified && !digest(file).equals(digest);
    }
}
