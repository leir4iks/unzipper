package org.example;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

public class Unarchiver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("unzipper (type 'stop' to quit)");
            System.out.println("Available commands:");
            System.out.println("  unzip <file>                    - Extract to current directory");
            System.out.println("  unzip <file> <destination>      - Extract to specified directory");
            System.out.println("  stop                            - Quit the program");
            System.out.println("Supported formats: .zip, .tar.gz, .rar");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("stop")) {
                    System.out.println("Stopping...");
                    break;
                }

                if (!input.startsWith("unzip ")) {
                    System.out.println("❌ Unknown command. Use 'unzip <filename> [destination]'");
                    continue;
                }

                String[] parts = parseCommand(input.substring(6).trim());
                if (parts == null) {
                    System.out.println("❌ Please specify a filename");
                    continue;
                }

                String filename = parts[0];
                String destination = parts[1]; // может быть null

                try {
                    if (filename.endsWith(".tar.gz")) {
                        extractTarGz(filename, destination);
                    } else if (filename.endsWith(".zip")) {
                        extractZip(filename, destination);
                    } else if (filename.endsWith(".rar")) {
                        extractRar(filename, destination);
                    } else {
                        System.out.println("❌ Unsupported file format. Only .zip, .tar.gz and .rar are supported");
                    }
                } catch (IOException e) {
                    System.out.println("❌ Error: " + e.getMessage());
                }
            }
        } finally {
            scanner.close();
        }
    }

    /**
     * Парсит команду и возвращает массив [filename, destination]
     * destination может быть null если не указан
     */
    private static String[] parseCommand(String command) {
        if (command.isEmpty()) {
            return null;
        }

        // Простой парсинг с учетом пробелов в путях
        String[] parts = command.split("\\s+", 2);
        String filename = parts[0];
        String destination = parts.length > 1 ? parts[1] : null;

        return new String[]{filename, destination};
    }

    /**
     * Определяет директорию для извлечения
     */
    private static Path getOutputDirectory(String archivePath, String destination) {
        if (destination != null && !destination.trim().isEmpty()) {
            // Если указан путь назначения, используем его
            return Path.of(destination.trim()).toAbsolutePath();
        } else {
            // Если путь не указан, извлекаем в директорию где лежит архив
            Path archiveFile = Path.of(archivePath).toAbsolutePath();
            return archiveFile.getParent();
        }
    }

    private static void extractTarGz(String filePath, String destination) throws IOException {
        Path source = Path.of(filePath).toAbsolutePath();
        if (!Files.exists(source)) {
            throw new IOException("File not found: " + source);
        }

        Path outputDir = getOutputDirectory(filePath, destination);
        
        // Создаем директорию назначения если она не существует
        Files.createDirectories(outputDir);
        
        try (InputStream fi = Files.newInputStream(source);
             BufferedInputStream bi = new BufferedInputStream(fi);
             GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
             TarArchiveInputStream ti = new TarArchiveInputStream(gzi)) {

            TarArchiveEntry entry;
            while ((entry = ti.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    Path targetPath = outputDir.resolve(entry.getName()).normalize();
                    
                    if (!targetPath.startsWith(outputDir)) {
                        throw new IOException("Blocked attempt to extract outside target directory: " + entry.getName());
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

        System.out.println("✅ Successfully extracted TAR.GZ archive to: " + outputDir);
    }

    private static void extractZip(String filePath, String destination) throws IOException {
        Path source = Path.of(filePath).toAbsolutePath();
        if (!Files.exists(source)) {
            throw new IOException("File not found: " + source);
        }

        Path outputDir = getOutputDirectory(filePath, destination);
        
        // Создаем директорию назначения если она не существует
        Files.createDirectories(outputDir);
        
        byte[] buffer = new byte[1024];

        try (FileInputStream fis = new FileInputStream(source.toFile());
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry zipEntry = zis.getNextEntry();
            
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    Path targetPath = outputDir.resolve(zipEntry.getName()).normalize();
                    
                    if (!targetPath.startsWith(outputDir)) {
                        throw new IOException("Blocked attempt to extract outside target directory: " + zipEntry.getName());
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

        System.out.println("✅ Successfully extracted ZIP archive to: " + outputDir);
    }

    private static void extractRar(String filePath, String destination) throws IOException {
        Path source = Path.of(filePath).toAbsolutePath();
        if (!Files.exists(source)) {
            throw new IOException("File not found: " + source);
        }

        Path outputDir = getOutputDirectory(filePath, destination);
        
        // Создаем директорию назначения если она не существует
        Files.createDirectories(outputDir);

        try (Archive archive = new Archive(source.toFile())) {
            FileHeader fileHeader;
            while ((fileHeader = archive.nextFileHeader()) != null) {
                if (!fileHeader.isDirectory()) {
                    Path targetPath = outputDir.resolve(fileHeader.getFileName()).normalize();
                    
                    if (!targetPath.startsWith(outputDir)) {
                        throw new IOException("Blocked attempt to extract outside target directory: " + fileHeader.getFileName());
                    }

                    Path parent = targetPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }

                    try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                        archive.extractFile(fileHeader, fos);
                    }
                    
                    System.out.println("📁 Extracted: " + fileHeader.getFileName());
                }
            }
        } catch (Exception e) {
            throw new IOException("Error extracting RAR archive: " + e.getMessage());
        }

        System.out.println("✅ Successfully extracted RAR archive to: " + outputDir);
    }
}
