package com.vay.videos.service;

import com.vay.videos.exception.VideoNotFoundException;
import com.vay.videos.intagrations.s3.MinioService;
import com.vay.videos.model.FileMetadata;
import com.vay.videos.repository.FileMetadataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultVideoStreamingServiceTest {

    @Mock
    FileMetadataRepository fileMetadataRepository;

    @Mock
    MinioService minioService;

    @InjectMocks
    DefaultVideoStreamingService videoService;

    @Mock
    MultipartFile video;
    
    String videoName = "test-video.mp4"; 

    @Test
    void getVideoList_ShouldReturnAllVideos() {
        // given
        var metadataList = List.of(
                new FileMetadata(1L, videoName, UUID.randomUUID(), "video/mp4", 1024L, LocalDateTime.now()),
                new FileMetadata(2L, videoName, UUID.randomUUID(), "video/mp4", 1024L, LocalDateTime.now()),
                new FileMetadata(3L, videoName, UUID.randomUUID(), "video/mp4", 1024L, LocalDateTime.now()));

        when(fileMetadataRepository.findAll()).thenReturn(metadataList);

        // when
        var result = videoService.getVideoList();

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).isEqualTo(metadataList);

        verify(fileMetadataRepository, times(1)).findAll();
    }

    @Test
    void saveVideo_ShouldSaveMetadataAndUploadFile() {
        // given
        when(video.getContentType()).thenReturn("video/mp4");
        when(video.getSize()).thenReturn(1024L);

        // when
        UUID result = videoService.saveVideo(video, videoName);

        // then
        assertThat(result).isNotNull();

        verify(fileMetadataRepository, times(1)).save(any(FileMetadata.class));
        verify(minioService, times(1)).uploadFile(any(MultipartFile.class), any(FileMetadata.class));
    }

    @Test
    void saveVideo_WhenVideoIsNull_ShouldThrowException() {
        // when
        var result = assertThrows(VideoNotFoundException.class, () ->
                videoService.saveVideo(null, videoName));

        // then
        assertThat(result.getMessage()).isEqualTo("MultipartFile is empty");
    }

    @Test
    void saveVideo_WhenVideoIsEmpty_ShouldThrowException() {
        // given
        when(video.isEmpty()).thenReturn(true);
        
        // when
        var result = assertThrows(VideoNotFoundException.class, () ->
                videoService.saveVideo(video, videoName));
        
        // then
        assertThat(result.getMessage()).isEqualTo("MultipartFile is empty");
    }

    @Test
    void deleteVideo_WhenExists_ShouldDeleteVideo() {
        // given
        var uuid = UUID.randomUUID();

        when(fileMetadataRepository.existsByUuid(uuid)).thenReturn(true);
        doNothing().when(fileMetadataRepository).deleteByUuid(uuid);

        // then
        assertDoesNotThrow(() -> videoService.deleteVideo(uuid));

        verify(fileMetadataRepository, times(1)).existsByUuid(uuid);
        verify(fileMetadataRepository, times(1)).deleteByUuid(uuid);
    }

    @Test
    void deleteVideo_WhenNo

    @Test
    void fetchChunk() {
    }
}