package ru.ntik.book.library.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ntik.book.library.testutils.TestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional

class FileStorageServiceTest {
    @Autowired
    FileStorageService fileStorageService;

    @DisplayName("Save file")
    @Test
    void saveFileTest() throws IOException {
        byte[] bytes = TestUtils.getImgBytes("main.jpg", getClass().getClassLoader());

        String location = fileStorageService.saveFile(bytes, "main.jpg");
        assertThat(location).isNotNull().isNotBlank();

        FileSystemResource inFileSystem = fileStorageService.findInFileSystem(location);
        assertThat(inFileSystem).isNotNull();
    }

}