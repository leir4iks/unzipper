package org.example;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class Unarchiver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("unzipper (type 'exit' to quit)");
            System.out.println("Available commands:");
            System.out.println("  unzip <file.zip>    - Extract ZIP archive");
            System.out.println("  unzip <file.tar.gz> - Extract TAR.GZ archive");
            System.out.println("  exit                - Quit the program");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    break;
                }

                if (!input.startsWith("unzip ")) {
                    System.out.println("❌ Unknown command. Use 'unzip <filename>'");
                    continue;
                }

                String filename = input.substring(6).trim();
                if (filename.isEmpty()) {
                    System.out.println("❌ Please specify a filename");
                    continue;
                }

                try {
                    if (filename.endsWith(".tar.gz")) {
                        extractTarGz(filename);
                    } else if (filename.endsWith(".zip")) {
                        extractZip(filename);
                    } else {
                        System.out.println("❌ Unsupported file format. Only .zip and .tar.gz are supported");
                    }
                } catch (IOException e) {
                    System.out.println("❌ Error: " + e.getMessage());
                }
            }
        } finally {
            scanner.close();
        }
    }

    private static void extractTarGz(String filePath) throws IOException {
        Path source = Path.of(filePath).toAbsolutePath();
        if (!Files.exists(source)) {
            throw new IOException("File not found: " + source);
        }

        Path outputDir = Path.of("").toAbsolutePath();
        
        try (InputStream fi = Files.newInputStream(source);
             BufferedInputStream bi = new BufferedInputStream(fi);
             GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
             TarArchiveInputStream ti = new TarArchiveInputStream(gzi)) {

            TarArchiveEntry entry;
            while ((entry = ti.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    Path targetPath = outputDir.resolve(entry.getName()).normalize();
                    
                    // Защита от Zip Slip атак
                    if (!targetPath.startsWith(outputDir)) {
                        throw new IOException("Blocked attempt to extract outside working directory: " + entry.getName());
                    }

                    Path parent = targetPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }

                    try (OutputStream out = Files.newOutputStream(targetPath)) {
                        IOUtils.copy(ti, out);
                    }
                    
                    System.out.println("📁 Extracted: " + entry.getName());
                }
            }
        }

        System.out.println("✅ Successfully extracted TAR.GZ archive to current directory");
    }

    private static void extractZip(String filePath) throws IOException {
        Path source = Path.of(filePath).toAbsolutePath();
        if (!Files.exists(source)) {
            throw new IOException("File not found: " + source);
        }

        Path outputDir = Path.of("").toAbsolutePath();
        byte[] buffer = new byte[1024];

        try (FileInputStream fis = new FileInputStream(source.toFile());
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry zipEntry = zis.getNextEntry();
            
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    Path targetPath = outputDir.resolve(zipEntry.getName()).normalize();
                    
                    // Защита от Zip Slip атак
                    if (!targetPath.startsWith(outputDir)) {
                        throw new IOException("Blocked attempt to extract outside working directory: " + zipEntry.getName());
                    }

                    Path parent = targetPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }

                    try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    
                    System.out.println("📁 Extracted: " + zipEntry.getName());
                }
                
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }

        System.out.println("✅ Successfully extracted ZIP archive to current directory");
    }
}
