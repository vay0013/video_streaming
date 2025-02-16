package com.vay.videos.intagrations.s3;

import com.vay.videos.model.FileMetadata;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
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
    private final MinioClient client;

    private final MinioClient minioClient;
    @Value("${minio.bucket.name}")
    private String bucketName;
    @Value("${minio.stream.part-size}")
    private long streamPartSize;


    @PostConstruct
    public void initialize() {
        if (!bucketExists()) createBucket();
    }

    private boolean bucketExists() {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName).build());
        } catch (ErrorResponseException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createBucket() {
        MakeBucketArgs args = MakeBucketArgs.builder().bucket(bucketName).build();
        try {
            minioClient.makeBucket(args);
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void uploadFile(MultipartFile file, FileMetadata fileMetadata) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileMetadata.getUuid().toString())
                    .stream(file.getInputStream(), file.getSize(), streamPartSize)
                    .build());
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteFile(UUID fileUUID) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileUUID.toString())
                    .build());
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
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
        } catch (ErrorResponseException e) {
            throw new RuntimeException("Ошибка MinIO: " + e.errorResponse().message(), e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении объекта из MinIO", e);
        }
    }
}
