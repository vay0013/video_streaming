package com.vay.videos.service;

import com.vay.videos.intagrations.s3.MinioService;
import com.vay.videos.model.FileMetadata;
import com.vay.videos.model.StreamChunk;
import com.vay.videos.model.StreamRange;
import com.vay.videos.repository.FileMetadataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoStreamingService {
    private final FileMetadataRepository fileMetadataRepository;
    private final MinioService minioService;

    @Transactional
    public UUID saveVideo(MultipartFile video, String videoName) {
        UUID videoUUID = UUID.randomUUID();
        FileMetadata metadata = new FileMetadata(
                null,
                videoName,
                videoUUID,
                video.getContentType(),
                video.getSize(),
                LocalDateTime.now());
        fileMetadataRepository.save(metadata);
        minioService.uploadFile(video, metadata);
        return videoUUID;

    }

    @Transactional
    public void deleteVideo(UUID videoUUID) {
        fileMetadataRepository.deleteByUuid(videoUUID);
        minioService.deleteFile(videoUUID);
    }

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
            throw new RuntimeException(e);
        }
    }

    public List<FileMetadata> getVideoList() {
        return new ArrayList<>(fileMetadataRepository.findAll());
    }
}
