package net.thenextlvl.commander.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.thenextlvl.commander.CommanderCommons;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.zip.CRC32C;

@NullMarked
public abstract class JsonFile<R> {
    private static final int BUFFER_SIZE = 8192;
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private final Path file;

    private R root;
    private boolean loaded;

    private String digest = "";
    private long lastModified = 0;

    protected JsonFile(final Path file, final R root) {
        this.file = file;
        this.root = root;
    }

    public R getRoot() {
        if (loaded) return root;
        loaded = true;
        return root = load();
    }

    private JsonFile<R> setRoot(final R root) {
        this.loaded = true;
        this.root = root;
        return this;
    }

    public JsonFile<R> reload() {
        return setRoot(load());
    }

    public JsonFile<R> saveIfAbsent(final FileAttribute<?>... attributes) {
        return Files.isRegularFile(file) ? this : save(attributes);
    }

    private R load() {
        if (!Files.isRegularFile(file)) return getRoot();
        try (final var reader = Files.newBufferedReader(file)) {
            return read(JsonParser.parseReader(reader));
        } catch (final IOException e) {
            CommanderCommons.ERROR_TRACKER.trackError(e).handled(false);
            throw new UncheckedIOException("Failed to read file: " + file.getFileName(), e);
        } finally {
            this.digest = digest();
            this.lastModified = lastModified();
        }
    }

    protected abstract R read(JsonElement element) throws IOException;

    public JsonFile<R> save(final FileAttribute<?>... attributes) {
        try {
            final var root = getRoot();
            Files.createDirectories(file.toAbsolutePath().getParent(), attributes);
            try (final var writer = Files.newBufferedWriter(file)) {
                GSON.toJson(root, writer);
                return this;
            }
        } catch (final IOException e) {
            CommanderCommons.ERROR_TRACKER.trackError(e).handled(false);
            throw new UncheckedIOException("Failed to save file: " + file.getFileName(), e);
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
            final var crc = new CRC32C();
            try (final var input = Files.newInputStream(file)) {
                final var buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    crc.update(buffer, 0, bytesRead);
                }
            }
            return Long.toHexString(crc.getValue());
        } catch (final IOException e) {
            return "";
        }
    }

    private long lastModified() {
        try {
            if (!Files.exists(file)) return System.currentTimeMillis();
            return Files.getLastModifiedTime(file).toMillis();
        } catch (final IOException e) {
            return System.currentTimeMillis();
        }
    }

    public static final class ArraySet extends JsonFile<CopyOnWriteArraySet<String>> {
        public ArraySet(final Path file) {
            super(file, new CopyOnWriteArraySet<>());
        }

        @Override
        protected CopyOnWriteArraySet<String> read(final JsonElement element) {
            final var root = new CopyOnWriteArraySet<String>();
            element.getAsJsonArray().forEach(value -> {
                if (!value.isJsonPrimitive()) return;
                root.add(value.getAsString());
            });
            return root;
        }
    }

    public static final class HashMap extends JsonFile<ConcurrentHashMap<String, String>> {
        public HashMap(final Path file) {
            super(file, new ConcurrentHashMap<>());
        }

        @Override
        protected ConcurrentHashMap<String, String> read(final JsonElement element) {
            final var root = new ConcurrentHashMap<String, String>();
            element.getAsJsonObject().entrySet().forEach(entry -> {
                if (!entry.getValue().isJsonPrimitive()) return;
                root.put(entry.getKey(), entry.getValue().getAsString());
            });
            return root;
        }
    }
}
