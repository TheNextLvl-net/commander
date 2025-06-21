package net.thenextlvl.commander.util;

import core.file.FileIO;
import core.io.PathIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
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
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (InputStream in = new DigestInputStream(Files.newInputStream(path), md)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (in.read(buffer) != -1) {
                    // just read to update digest
                }
            }
            byte[] result = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : result) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static FileTime lastModified(FileIO<?> file) {
        try {
            Path path = ((PathIO) file.getIO()).getPath();
            if (Files.exists(path)) {
                return Files.getLastModifiedTime(path);
            }
        } catch (IOException ignored) {
        }
        return FileTime.fromMillis(0);
    }

    public static boolean hasChanged(FileIO<?> file, String digest, FileTime lastModified) {
        try {
            Path path = ((PathIO) file.getIO()).getPath();
            if (!Files.exists(path)) return false;
            FileTime current = Files.getLastModifiedTime(path);
            if (current.equals(lastModified)) return false;
            return !digest(path).equals(digest);
        } catch (IOException e) {
            return false;
        }
    }
}
