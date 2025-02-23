package com.vay.videos.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Sql("/sql/metadata.sql")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FileMetadataRepositoryIT {
    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Test
    void findFileMetadataByUuid_ReturnsFileMetadata() {
        // given
        UUID uuid = UUID.fromString("9f86af4d-714d-44f5-9a56-3d69e8a4073f");

        // when
        var fileMetadata = fileMetadataRepository.findFileMetadataByUuid(uuid);

        // then
        assertThat(fileMetadata).isNotNull();
        assertThat(fileMetadata.getId()).isEqualTo(1L);
        assertThat(fileMetadata.getUuid()).isEqualTo("video#1");
        assertThat(fileMetadata.getUuid()).isEqualTo(uuid);
        assertThat(fileMetadata.getContentType()).isEqualTo("MP4_VIDEO");
        assertThat(fileMetadata.getContentSize()).isEqualTo(10000L);

//        assertEquals(new FileMetadata(
//                1L,
//                "video#1",
//                UUID.fromString("9f86af4d-714d-44f5-9a56-3d69e8a4073f"),
//                "MP4_VIDEO",
//                10000L,
//                LocalDateTime.now()), fileMetadata);
    }
}