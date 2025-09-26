package com.leir4iks.extraction;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

public abstract class AbstractTarExtraction implements Extraction {

    protected abstract InputStream getDecompressorStream(InputStream inputStream) throws IOException;

    @Override
    public void extract(Path sourceFile, Path destinationDirectory) throws IOException {
        try (InputStream fi = Files.newInputStream(sourceFile);
             BufferedInputStream bi = new BufferedInputStream(fi);
             InputStream decompressedStream = getDecompressorStream(bi);
             TarArchiveInputStream ti = new TarArchiveInputStream(decompressedStream)) {

            TarArchiveEntry entry;
            while (true) {
                try {
                    entry = ti.getNextTarEntry();
                    if (entry == null) {
                        break;
                    }
                } catch (IOException e) {
                    if (e.getMessage() != null && e.getMessage().contains("Gzip-compressed data is corrupt")) {
                        System.err.println("Warning: Corrupted Gzip data detected. Attempting to skip to the next entry.");
                        continue;
                    } else {
                        throw e;
                    }
                }

                if (!entry.isDirectory()) {
                    Path targetPath = destinationDirectory.resolve(entry.getName()).normalize();
                    if (!targetPath.startsWith(destinationDirectory)) {
                        throw new IOException("Blocked attempt to extract outside target directory: " + entry.getName());
                    }
                    Path parent = targetPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    try (OutputStream out = Files.newOutputStream(targetPath)) {
                        IOUtils.copy(ti, out);
                    }
                }
            }
        }
    }

    @Override
    public List<String> list(Path sourceFile) throws IOException {
        List<String> fileNames = new ArrayList<>();
        try (InputStream fi = Files.newInputStream(sourceFile);
             BufferedInputStream bi = new BufferedInputStream(fi);
             InputStream decompressedStream = getDecompressorStream(bi);
             TarArchiveInputStream ti = new TarArchiveInputStream(decompressedStream)) {

            TarArchiveEntry entry;
            while (true) {
                try {
                    entry = ti.getNextTarEntry();
                    if (entry == null) {
                        break;
                    }
                } catch (IOException e) {
                    if (e.getMessage() != null && e.getMessage().contains("Gzip-compressed data is corrupt")) {
                        System.err.println("Warning: Corrupted Gzip data detected. Attempting to skip to the next entry.");
                        continue;
                    } else {
                        throw e;
                    }
                }
                fileNames.add(entry.getName());
            }
        }
        return fileNames;
    }
}