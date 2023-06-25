package ru.ntik.book.library.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

@Service
public class FileStorageService {
    private static String RESOURCES_DIR = Objects.requireNonNull(FileStorageService.class.getResource("/")).getPath();

    static {
        if (RESOURCES_DIR.startsWith("/")) {
            RESOURCES_DIR = RESOURCES_DIR.substring(1);
        }
    }

    public String saveFile(byte[] content, String fileName) throws IOException {
        Path newFile = Paths.get(RESOURCES_DIR + "files/" + new Date().getTime() + "-" + fileName);
        Files.createDirectories(newFile.getParent());
        Files.write(newFile, content);

        return newFile.toAbsolutePath().toString();
    }

    FileSystemResource findInFileSystem(String location) {
        return new FileSystemResource(Paths.get(location));
    }
}
