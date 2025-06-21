package net.thenextlvl.commander.util;

import core.file.FileIO;
import core.io.PathIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class FileUtil {
    private static final int BUFFER_SIZE = 8192;

    public static String digest(FileIO<?> file) {
        return digest(((PathIO) file.getIO()).getPath());
    }

    public static String digest(Path path) {
        try {
            if (!Files.exists(path)) return "";
            var digest = MessageDigest.getInstance("MD5");
            try (var input = new DigestInputStream(Files.newInputStream(path), digest)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (input.read(buffer) != -1) {
                    // just read to update digest
                }
            }
            var builder = new StringBuilder();
            for (var b : digest.digest()) builder.append(String.format("%02x", b));
            return builder.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
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
