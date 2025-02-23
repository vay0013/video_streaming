package com.vay.videos.service;

import com.vay.videos.intagrations.s3.MinioService;
import com.vay.videos.model.FileMetadata;
import com.vay.videos.model.StreamChunk;
import com.vay.videos.model.StreamRange;
import com.vay.videos.repository.FileMetadataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultVideoStreamingService implements VideoStreamingService {

    private final FileMetadataRepository fileMetadataRepository;
    private final MinioService minioService;

    @Override
    public List<FileMetadata> getVideoList() {
        return fileMetadataRepository.findAll();
    }

    @Override
    @Transactional
    public UUID saveVideo(MultipartFile video, String videoName) {
        UUID uuid = UUID.randomUUID();
        FileMetadata metadata = new FileMetadata(
                null,
                videoName,
                uuid,
                video.getContentType(),
                video.getSize(),
                LocalDateTime.now());

        fileMetadataRepository.save(metadata);
        minioService.uploadFile(video, metadata);
        return uuid;
    }

    @Override
    @Transactional
    public void deleteVideo(UUID videoUUID) {
        fileMetadataRepository.deleteByUuid(videoUUID);
    }

    @Override
    public StreamChunk fetchChunk(UUID uuid, StreamRange range) {
        FileMetadata fileMetadata = fileMetadataRepository.findFileMetadataByUuid(uuid);
        return new StreamChunk(fileMetadata, readChunk(fileMetadata, range));
    }

    private byte[] readChunk(FileMetadata fileMetadata, StreamRange range) {
        long start = range.rangeStart();
        long end = range.getRangeEnd(fileMetadata.getContentSize());
        long chunkSize = end - start + 1;

        try {
            return minioService.getFileAsInputStream(fileMetadata, start, chunkSize).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении чанка из MinIO", e);
        }
    }
}