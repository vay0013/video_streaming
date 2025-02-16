package com.vay.videos.controller;

import com.vay.videos.model.FileMetadata;
import com.vay.videos.model.StreamChunk;
import com.vay.videos.model.StreamRange;
import com.vay.videos.service.VideoStreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/videos")
public class VideoStreamingController {
    private final VideoStreamingService videoStreamingService;

    @Value("${streaming.chunk.default-size}")
    private long chunkDefaultSize;

    @GetMapping("/{uuid}")
    public ResponseEntity<byte[]> fetchChunk(
            @RequestHeader(value = RANGE, required = false) String range,
            @PathVariable UUID uuid
    ) {

        StreamRange parsedRange = StreamRange.parseHttpRangeString(range, chunkDefaultSize);
        StreamChunk streamChunk = videoStreamingService.fetchChunk(uuid, parsedRange);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, streamChunk.fileMetadata().getContentType())
                .header(ACCEPT_RANGES, "bytes")
                .header(CONTENT_LENGTH, getContentLengthHeader(parsedRange, streamChunk.fileMetadata().getContentSize()))
                .header(CONTENT_RANGE, getContentRangeHeader(parsedRange, streamChunk.fileMetadata().getContentSize()))
                .body(streamChunk.chunk());
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileMetadata>> getVideoList() {
        return ResponseEntity.ok(videoStreamingService.getVideoList());
    }

    @PostMapping("/upload")
    public ResponseEntity<UUID> saveVideo(
            @RequestParam("video") MultipartFile video,
            @RequestParam("videoName") String videoName
    ) {
        return ResponseEntity.ok(videoStreamingService.saveVideo(video, videoName));
    }

    @PostMapping("/delete/{uuid}")
    public ResponseEntity<Void> deleteVideo(@PathVariable("uuid") UUID uuid) {
        videoStreamingService.deleteVideo(uuid);
        return ResponseEntity.noContent().build();
    }

    private String getContentLengthHeader(StreamRange range, long fileSize) {
        return Long.toString(range.getRangeEnd(fileSize) - range.rangeStart() + 1);
    }

    private String getContentRangeHeader(StreamRange range, long fileSize) {
        return "bytes %d-%d/%d".formatted(range.rangeStart(), range.getRangeEnd(fileSize), fileSize);
    }
}