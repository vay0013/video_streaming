package com.vay.videos.service;

import com.vay.videos.model.FileMetadata;
import com.vay.videos.model.StreamChunk;
import com.vay.videos.model.StreamRange;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface VideoStreamingService {
    List<FileMetadata> getVideoList();

    UUID saveVideo(MultipartFile video, String videoName);

    void deleteVideo(UUID videoUUID);

    StreamChunk fetchChunk(UUID uuid, StreamRange range);
}