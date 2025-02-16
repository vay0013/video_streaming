package com.vay.videos.repository;

import com.vay.videos.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    FileMetadata findFileMetadataByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
