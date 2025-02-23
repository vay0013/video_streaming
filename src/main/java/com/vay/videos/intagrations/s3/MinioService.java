package com.vay.videos.intagrations.s3;

import com.vay.videos.model.FileMetadata;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;
    @Value("${minio.stream.part-size}")
    private long streamPartSize;

    @PostConstruct
    private void createBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании бакета MinIO: " + e.getMessage(), e);
        }
    }


    public void uploadFile(MultipartFile file, FileMetadata fileMetadata) {
        try {


            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(String.valueOf(fileMetadata.getUuid()))
                            .stream(file.getInputStream(), file.getSize(), streamPartSize)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла в MinIO: " + e.getMessage(), e);
        }
    }

    public void deleteFile(UUID fileUUID) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileUUID.toString())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла из MinIO: " + e.getMessage(), e);
        }
    }

    public InputStream getFileAsInputStream(FileMetadata fileMetadata, long offset, long length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Длина файла должна быть больше 0");
        }

        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileMetadata.getUuid().toString())
                    .offset(offset)
                    .length(length)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении объекта из MinIO", e);
        }
    }
}
