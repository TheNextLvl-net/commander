package net.thenextlvl.commander.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.zip.CRC32C;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@NullMarked
public final class GsonFile<R> {
    private static final int BUFFER_SIZE = 8192;
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private final Path file;
    private final R defaultRoot;
    private final Type type;

    private R root;
    private boolean loaded;

    private String digest = "";
    private long lastModified = 0;

    public GsonFile(Path file, R root, TypeToken<R> token) {
        this.defaultRoot = root;
        this.file = file;
        this.root = root;
        this.type = token.getType();
    }

    public R getRoot() {
        if (loaded) return root;
        loaded = true;
        return root = load();
    }

    private GsonFile<R> setRoot(R root) {
        this.loaded = true;
        this.root = root;
        return this;
    }

    public GsonFile<R> reload() {
        return setRoot(load());
    }

    public GsonFile<R> saveIfAbsent(FileAttribute<?>... attributes) {
        return Files.isRegularFile(file) ? this : save(attributes);
    }

    private R load() {
        if (!Files.isRegularFile(file)) return getRoot();
        try (var reader = new JsonReader(new InputStreamReader(
                Files.newInputStream(file, READ),
                StandardCharsets.UTF_8
        ))) {
            R root = GSON.fromJson(reader, type);
            return root != null ? root : defaultRoot;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.digest = digest();
            this.lastModified = lastModified();
        }
    }

    public GsonFile<R> save(FileAttribute<?>... attributes) {
        try {
            var root = getRoot();
            Files.createDirectories(file.toAbsolutePath().getParent(), attributes);
            try (var writer = new BufferedWriter(new OutputStreamWriter(
                    Files.newOutputStream(file, WRITE, CREATE, TRUNCATE_EXISTING),
                    StandardCharsets.UTF_8
            ))) {
                GSON.toJson(root, type, writer);
                return this;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.digest = digest();
            this.lastModified = lastModified();
        }
    }

    public boolean hasChanged() {
        return lastModified() != lastModified && !digest.equals(digest());
    }

    private String digest() {
        try {
            if (!Files.exists(file)) return "";
            var crc = new CRC32C();
            try (var input = Files.newInputStream(file)) {
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

    private long lastModified() {
        try {
            if (!Files.exists(file)) return System.currentTimeMillis();
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException e) {
            return System.currentTimeMillis();
        }
    }
}
