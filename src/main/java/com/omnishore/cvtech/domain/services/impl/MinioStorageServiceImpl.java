package com.omnishore.cvtech.domain.services.impl;

import com.omnishore.cvtech.domain.services.MinioStorageService;
import io.minio.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class MinioStorageServiceImpl implements MinioStorageService {
    @NonNull
    private final MinioClient client;
    @Value("${minio.bucket-name}")
    private String bucket;

    @Override
    public String upload(File file, String objectName) throws Exception {
        boolean exists = client.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        client.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .stream(new FileInputStream(file), file.length(), -1)
                .contentType(
                        Files.probeContentType(file.toPath()))
                .build());
        return objectName;
    }

    @Override
    public File download(String objectName, File destFile) throws Exception {
        try (InputStream is = client.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket).object(objectName).build());
             FileOutputStream os = new FileOutputStream(destFile)) {
            is.transferTo(os);
        }
        return destFile;
    }
}
