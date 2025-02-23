package com.vay.videos.service;

import com.google.common.net.MediaType;
import com.vay.videos.intagrations.s3.MinioService;
import com.vay.videos.model.FileMetadata;
import com.vay.videos.repository.FileMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultVideoStreamingServiceTest {

    @Mock
    private FileMetadataRepository fileMetadataRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private DefaultVideoStreamingService videoService;

    @Mock
    private MultipartFile videoFile;

    private FileMetadata fileMetadata;

    private UUID videoUUID;

    @BeforeEach
    void setUp() {
        videoUUID = UUID.randomUUID();
        fileMetadata = new FileMetadata(
                null,
                "test-video.mp4",
                videoUUID, MediaType.ANY_VIDEO_TYPE.type(),
                1024L,
                LocalDateTime.now());
    }

    @Test
    void getVideoList_ReturnListOfVideos() {
        // given
        var metadataList = List.of(
                new FileMetadata(1L, "test-video.mp4", videoUUID, MediaType.ANY_VIDEO_TYPE.type(), 1024L, LocalDateTime.now()),
                new FileMetadata(2L, "test-video.mp4", videoUUID, MediaType.ANY_VIDEO_TYPE.type(), 1024L, LocalDateTime.now()),
                new FileMetadata(3L, "test-video.mp4", videoUUID, MediaType.ANY_VIDEO_TYPE.type(), 1024L, LocalDateTime.now()));

        doReturn(metadataList).when(fileMetadataRepository).findAll();

        // when
        var result = videoService.getVideoList();

        // then
        assertEquals(metadataList, result);

        verify(fileMetadataRepository, times(1)).findAll();
    }

//    @Test
//    void saveVideo_WhenSuccess_ShouldReturnUuid() {
//        // given
//        when(videoFile.getContentType()).thenReturn(MediaType.ANY_VIDEO_TYPE.type());
//        when(videoFile.getSize()).thenReturn(1024L);
//        when(fileMetadataRepository.save(any(FileMetadata.class))).thenReturn(Mono.just(fileMetadata));
//        when(minioService.uploadFile(any(MultipartFile.class), any(FileMetadata.class))).thenReturn(Mono.empty());
//
//        // when
//        Mono<String> result = videoService.saveVideo(videoFile, "test-video.mp4");
//
//        // then
//        StepVerifier.create(result).expectNext(fileMetadata.getUuid().toString()).verifyComplete();
//    }
//
//    @Test
//    void deleteVideo() {
//        when(fileMetadataRepository.deleteByUuid(videoUUID)).thenReturn(Mono.empty());
//        when(minioService.deleteFile(videoUUID)).then Return(Mono.empty());
//        doAnswer(invocation -> invocation.getArgument(0)).when(transactionalOperator).transactional(any(Mono.class));
//
//        StepVerifier.create(videoService.deleteVideo(videoUUID)).verifyComplete();
//    }

    @Test
    void fetchChunk() {
    }
}